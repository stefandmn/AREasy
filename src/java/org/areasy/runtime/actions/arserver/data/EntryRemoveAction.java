package org.areasy.runtime.actions.arserver.data;

/*
 * Copyright (c) 2007-2016 AREasy Runtime
 *
 * This library, AREasy Runtime and API for BMC Remedy AR System, is free software ("Licensed Software");
 * you can redistribute it and/or modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either version 2.1 of the License,
 * or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 * including but not limited to, the implied warranty of MERCHANTABILITY, NONINFRINGEMENT,
 * or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more details.
 */

import org.areasy.runtime.engine.RuntimeLogger;
import org.areasy.runtime.engine.base.AREasyException;
import org.areasy.runtime.engine.structures.CoreItem;
import org.areasy.runtime.engine.structures.MultiPartItem;
import org.areasy.common.data.StringUtility;

import java.util.List;

/**
 * Delete one or more entries from form.
 */
public class EntryRemoveAction extends EntryAddAction
{
	/**
	 * Execute the current action.
	 *
	 * @throws org.areasy.runtime.engine.base.AREasyException
	 * if any error will occur
	 */
	public void run() throws AREasyException
	{
		runner();
	}

	/**
	 * Execute an action for a specific <code>CoreItem</code>. This item must be identified previously and then the method
	 * could be called. This method will used by standard actions which implement an workflow using these type of action
	 * which permit single change or update.
	 *
	 * @param entry <code>CoreItem</code> structure, which should be instantiated.
	 * @throws org.areasy.runtime.engine.base.AREasyException if any error will occur.
	 */
	public void run(CoreItem entry) throws AREasyException
	{
		if(getConfiguration().getBoolean("multipart", false) && entry instanceof MultiPartItem)
		{
			//set multipart form names
			setMultiPartForms(entry);

			setMultiPartQueryFields(entry);
			((MultiPartItem)entry).readParts(getServerConnection());
			((MultiPartItem)entry).removeParts(getServerConnection());
		}

		if(getConfiguration().getBoolean("force", false))
		{
			try
			{
				entry.remove(getServerConnection());

				//execution counter
				setRecordsCounter();
			}
			catch (Throwable th)
			{
				if(StringUtility.contains(th.getMessage(), "ERROR (302)"))
				{
					try
					{
						entry.remove(getServerConnection());

						//execution counter
						setRecordsCounter();
					}
					catch (Throwable th2)
					{
						logger.error("Error removing entry " + entry + ": " + th.getMessage() + ". " + th2.getMessage());

						//error counter
						setErrorsCounter();
					}
				}
				else
				{
					logger.error("Error removing entry " + entry + ": " + th.getMessage());

					//error counter
					setErrorsCounter();
				}
			}
		}
		else
		{
			entry.remove(getServerConnection());

			//execution counter
			setRecordsCounter();
		}
	}

	protected void operation() throws AREasyException
	{
		List entries = search();

		if(entries == null || entries.size() == 0) RuntimeLogger.debug("No entries found!");
		else
		{
			int chunk = getConfiguration().getInt("chunk", 0);
			boolean nextChunk = chunk > 0 && entries.size() > 0;

			for(int i = 0; i < entries.size(); i++)
			{
				CoreItem entry = (CoreItem) entries.get(i);
				run(entry);
			}

			// run for next chunk
			if(nextChunk) operation();
		}

		RuntimeLogger.debug("Removed " + getRecordsCounter() + " entries");
	}

	protected List search() throws AREasyException
	{
		String form = getConfiguration().getString("form", getConfiguration().getString("formname", null));

		CoreItem entry = getEntity();
		if(form != null) entry.setFormName(form);

		String id = getConfiguration().getString("id", null);
		if(StringUtility.isNotEmpty(id)) entry.setAttribute(1, id);

		if(getConfiguration().getString("qualification", null) == null)
		{
			setQueryFields(entry);
			int chunk = getConfiguration().getInt("chunk", 0);

			if(chunk > 0) return entry.search(getServerConnection(), chunk);
				else return entry.search(getServerConnection());
		}
		else
		{
			String qualification = getTranslatedQualification(getConfiguration().getString("qualification", null));
			int limit = getConfiguration().getInt("limit", 0);

			if(limit > 0)
			{
				if(getConfiguration().containsKey("chunk")) getConfiguration().removeKey("chunk");
				return entry.search(getServerConnection(), qualification, limit);
			}
			else
			{
				int chunk = getConfiguration().getInt("chunk", 0);
				return entry.search(getServerConnection(), qualification, chunk);
			}
		}
	}
}
