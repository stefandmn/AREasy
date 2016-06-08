package org.areasy.runtime.actions.data;

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

import org.areasy.common.data.StringUtility;
import org.areasy.runtime.engine.RuntimeLogger;
import org.areasy.runtime.engine.base.AREasyException;
import org.areasy.runtime.engine.structures.CoreItem;
import org.areasy.runtime.engine.structures.MultiPartItem;

import java.util.List;

/**
 * Dedicated library to perform all standard operations (data transactions) for a Remedy form.
 * This action is usually combined with file.wrapper runtime action
 */
public class FormData extends BaseData
{
	/**
	 * Execute the current action.
	 *
	 * @throws AREasyException
	 * if any error will occur
	 */
	public void run() throws AREasyException
	{
		String operation = getConfiguration().getString("operation", null);
		if(operation == null) throw new AREasyException("Data operation is not specified");

		String formName = getConfiguration().getString("form", getConfiguration().getString("formname", null));
		if(formName == null) throw new AREasyException("Form name is null");

		//run operation procedure
		operation();
	}

	protected void operation() throws AREasyException
	{
		CoreItem searchEntry = getEntity();
		searchEntry.setFormName(getConfiguration().getString("form", getConfiguration().getString("formname", null)));

		List entries = search(searchEntry);

		if(entries == null || entries.size() == 0)
		{
			run(searchEntry);
		}
		else
		{
			int chunk = getConfiguration().getInt("chunk", 0);
			boolean nextChunk = chunk > 0 && chunk < entries.size();

			for(int i = 0; i < entries.size(); i++)
			{
				CoreItem entry = (CoreItem) entries.get(i);
				if(nextChunk) getConfiguration().setKey("nextchunkid", entry.getEntryId());

				run(entry);
			}

			// run for next chunk
			operation();
		}
	}

	protected List search(CoreItem entry) throws AREasyException
	{
		String id = getConfiguration().getString("id", null);
		if(StringUtility.isNotEmpty(id)) entry.setAttribute(1, id);

		if(getConfiguration().getString("qualification", null) == null)
		{
			setQueryFields(entry);
			int chunk = getConfiguration().getInt("chunk", 0);

			if(chunk > 0)
			{
				if(getConfiguration().containsKey("nextchunkid")) entry.setAttribute(1, ">" + getConfiguration().getString("nextchunkid"));
				return entry.search(getServerConnection(), chunk);
			}
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
				if(getConfiguration().containsKey("nextchunkid")) qualification = "('1'>\"" + getConfiguration().getString("nextchunkid") + "\") AND (" + qualification + ")";
				return entry.search(getServerConnection(), qualification, chunk);
			}
		}
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
		String operation = getConfiguration().getString("operation", null);

		if(StringUtility.equalsIgnoreCase(operation, "create") || StringUtility.equalsIgnoreCase(operation, "insert")) runCreate(entry);
		else if(StringUtility.equalsIgnoreCase(operation, "update") || StringUtility.equalsIgnoreCase(operation, "modify")) runUpdate(entry);
		else if(StringUtility.equalsIgnoreCase(operation, "delete") || StringUtility.equalsIgnoreCase(operation, "remove")) runRemove(entry);
		else if(StringUtility.equalsIgnoreCase(operation, "merge")) runMerge(entry);
	}

	/**
	 * Insert/Create data through a Reemdy regular form.
	 *
	 * @param entry <code>CoreItem</code> structure, which should be instantiated.
	 * @throws AREasyException if any error will occur.
	 */
	public void runCreate(CoreItem entry) throws AREasyException
	{
		//data fill-in and create record
		if(!entry.exists())
		{
			//set data values
			setDataFields(entry);

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
		else if(getConfiguration().getBoolean("updateifexists", false) && !entry.exists()) runUpdate(entry);
	}

	public void runUpdate(CoreItem entry) throws AREasyException
	{
		if(entry.exists())
		{
			//set data values
			setDataFields(entry);

			entry.update(getServerConnection());
			RuntimeLogger.debug("Updated data entry: " + entry);

			if (getConfiguration().getBoolean("multipart", false) && entry instanceof MultiPartItem)
			{
				//set multipart form names
				setMultiPartForms(entry);

				//execute transactions
				((MultiPartItem) entry).commitParts(getServerConnection(), getMultiPartQueryFields(), getMultiPartDataFields());
			}
		}
		else if(getConfiguration().getBoolean("createifnotexist", false) && !entry.exists()) runCreate(entry);
	}

	/**
	 * Remove/delete data froma Remedy regular form.
	 *
	 * @param entry <code>CoreItem</code> structure, which should be instantiated.
	 * @throws org.areasy.runtime.engine.base.AREasyException if any error will occur.
	 */
	public void runRemove(CoreItem entry) throws AREasyException
	{
		if(entry.exists())
		{
			if (getConfiguration().getBoolean("multipart", false) && entry instanceof MultiPartItem)
			{
				//set multipart form names
				setMultiPartForms(entry);

				setMultiPartQueryFields(entry);
				((MultiPartItem) entry).readParts(getServerConnection());
				((MultiPartItem) entry).removeParts(getServerConnection());
			}

			entry.remove(getServerConnection());
		}
	}

	public void runMerge(CoreItem entry) throws AREasyException
	{
		//set data values
		setDataFields(entry);

		List qualList = getConfiguration().getList("mergematchingfieldids", null);

		if(qualList != null) entry.merge(getServerConnection(), getMergeTypeAndOptions(getConfiguration()), qualList);
			else entry.merge(getServerConnection(), getMergeTypeAndOptions(getConfiguration()));

		RuntimeLogger.debug("Merged data entry: " + entry);
	}
}