package org.areasy.runtime.actions.arserver.data;

/*
 * Copyright (c) 2007-2015 AREasy Runtime
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

import java.util.List;

/**
 * Merge one or more entries from form.
 */
public class EntryMergeAction extends EntryUpdateAction
{
	/**
	 * Run secondary initialization (from the final implementation class)
	 *
	 * @throws AREasyException if any error will occur.
	 */
	public void open() throws AREasyException
	{
		if(!getConfiguration().containsKey("merge")) getConfiguration().setKey("merge", true);

		super.open();
	}

	protected void operation() throws AREasyException
	{
		String form = getConfiguration().getString("form", getConfiguration().getString("formname", null));
		CoreItem entry = getEntity();

		if(form != null) entry.setFormName(form);
			else throw new AREasyException("Form name is null");

		run(entry);
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
		//set data values
		setDataFields(entry);

		List qualList = getConfiguration().getList("mergematchingfieldids", null);

		boolean onlyupdate = getConfiguration().getBoolean("onlyupdate", false);
		boolean onlycreate = getConfiguration().getBoolean("onlycreate", false);

		if(onlycreate && entry.exists())
		{
			RuntimeLogger.warn("Only create operation is accepted and the structure is already defined: " + entry);
			return;
		}

		if(onlyupdate && !entry.exists())
		{
			RuntimeLogger.warn("Only update/overwrite operation is accepted and the structure is doesn't exist: " + entry);
			return;
		}

		if(qualList != null) entry.merge(getServerConnection(), getMergeTypeAndOptions(getConfiguration()), qualList);
			else entry.merge(getServerConnection(), getMergeTypeAndOptions(getConfiguration()));

		RuntimeLogger.debug("Merged data entry: " + entry);
	}
}
