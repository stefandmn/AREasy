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

import java.util.List;

/**
 * Update one or more entries from form.
 */
public class EntryUpdateAction extends EntryRemoveAction
{
	/**
	 * Execute the current action.
	 *
	 * @throws org.areasy.runtime.engine.base.AREasyException
	 *          if any error will occur
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
		if(!entry.exists())
		{
			if(getConfiguration().getBoolean("force", false))
			{
				//set data values
				setDataFields(entry);

				if(getConfiguration().getBoolean("merge", false))
				{
					List qualList = getConfiguration().getList("mergematchingfieldids", null);

					if(qualList != null) entry.merge(getServerConnection(), getMergeTypeAndOptions(getConfiguration()), qualList);
						else entry.merge(getServerConnection(), getMergeTypeAndOptions(getConfiguration()));

					RuntimeLogger.debug("Merged forced entry: " + entry);
				}
				else if(!getConfiguration().getBoolean("onlyupdate", false))
				{
					entry.create(getServerConnection());
					RuntimeLogger.debug("Created data entry: " + entry);

					if(getConfiguration().getBoolean("multipart", false) && entry instanceof MultiPartItem)
					{
						//set multipart form names
						setMultiPartForms(entry);

						//execute transactions
						((MultiPartItem)entry).commitParts(getServerConnection(), getMultiPartQueryFields(), getMultiPartDataFields());
					}
				}
				else if(getConfiguration().getBoolean("onlyupdate", false)) RuntimeLogger.warn("No data found and only update operation is allowed");
			}
			else RuntimeLogger.warn("No data found!");
		}
		else
		{
			//set data values
			setDataFields(entry);

			if(getConfiguration().getBoolean("merge", false))
			{
				List qualList = getConfiguration().getList("mergematchingfieldids", null);

				if(qualList != null) entry.merge(getServerConnection(), getMergeTypeAndOptions(getConfiguration()), qualList);
					else entry.merge(getServerConnection(), getMergeTypeAndOptions(getConfiguration()));

				RuntimeLogger.debug("Merged data entry: " + entry);
			}
			else
			{
				entry.update(getServerConnection());
				RuntimeLogger.debug("Updated data entry: " + entry);

				if(getConfiguration().getBoolean("multipart", false) && entry instanceof MultiPartItem)
				{
					//set multipart form names
					setMultiPartForms(entry);

					//execute transactions
					((MultiPartItem)entry).commitParts(getServerConnection(), getMultiPartQueryFields(), getMultiPartDataFields());
				}
			}
		}
	}

	protected void operation() throws AREasyException
	{
		String form = getConfiguration().getString("form", getConfiguration().getString("formname", null));

		List entries = search();

		if(entries == null || entries.size() == 0)
		{
			if(getConfiguration().getBoolean("force", false))
			{
				CoreItem entry = getEntity();

				if(form != null) entry.setFormName(form);
					else throw new AREasyException("Form name is null");

				run(entry);

				//execution counter incrementation
				setRecordsCounter();
			}
		}
		else
		{
			for (Object entry1 : entries)
			{
				CoreItem entry = (CoreItem) entry1;

				if (form != null) entry.setFormName(form);
					else throw new AREasyException("Form name is null");

				try
				{
					run(entry);
				}
				catch (Throwable th)
				{
					if (getConfiguration().getBoolean("force", false))
					{
						RuntimeLogger.error("Error updating data: " + th.getMessage());
					}
					else
					{
						if (th instanceof AREasyException) throw (AREasyException) th;
							else throw new AREasyException(th);
					}
				}

				//execution counter incrementation
				setRecordsCounter();
			}
		}
	}
}