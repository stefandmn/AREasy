package org.areasy.runtime.actions.itsm.admin;

/*
 * Copyright (c) 2007-2020 AREasy Runtime
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

import org.areasy.common.data.NumberUtility;
import org.areasy.runtime.engine.RuntimeLogger;
import org.areasy.runtime.engine.base.AREasyException;
import org.areasy.runtime.engine.structures.CoreItem;
import org.areasy.runtime.engine.structures.data.itsm.foundation.People;

import java.util.List;

/**
 * Dedicated action to remove access restriction for an user or for a list of users
 *
 */
public class RestrictedAccessRemoveAction extends AbstractUserEnrollment
{
	/**
	 * Execute action's workflow.
	 *
	 * @throws org.areasy.runtime.engine.base.AREasyException if any global error occurs. All errors comming from action's execution will become output items
	 */
	public void run() throws AREasyException
	{
		boolean validation = getConfiguration().getBoolean("validation", false);
		String companyName = getConfiguration().getString("company", null);

		//read and validate company structure in order to take corresponding system group id
		CoreItem company = getCompanyPermission(companyName);
		if(company == null || !company.exists()) throw new AREasyException("Invalid company: " + companyName);

		//execute the requested action for each user
		for(int i = 0; i < getUsers().size(); i++)
		{
			String username = (String) getUsers().get(i);

			try
			{
				People person = new People();
				person.setLoginId(username);
				person.read(getServerConnection());

				if(person.exists())
				{
					//check if Unrestricted Access Permission still exist
					CoreItem entry = new CoreItem();
					entry.setFormName("CTM:People Permission Groups");
					entry.setAttribute(4, person.getLoginId());
					entry.setAttribute(1000000080, person.getEntryId());

					if(!validation)
					{
						entry.setAttribute(1000001578, company.getStringAttributeValue(1000001578)); //system group name
						entry.setAttribute(1000001579, new Long(NumberUtility.toInt(company.getStringAttributeValue(1000001579)))); //system group id
					}
					else entry.setAttribute(1000000001, company.getStringAttributeValue(1000000001));

					List list = entry.search(getServerConnection());

					for(int x = 0; list != null && x < list.size(); x++)
					{
						CoreItem item = (CoreItem) list.get(x);
						RuntimeLogger.info("Remove restricted access permission configured for user '" + username + "'");

						item.setAttribute(1000000076, "DELETE"); //action
						item.update(getServerConnection());
					}
				}
				else RuntimeLogger.error("People account wasn't found: " + person);
			}
			catch(Throwable th)
			{
				getLogger().error("Error configuring restricted access to '" + companyName + "' company for user '" + username + "': " + th.getMessage());
				getLogger().debug("Exception", th);

				RuntimeLogger.error("Error configuring restricted access to '" + companyName + "' company for user '" + username + "': " + th.getMessage());
			}

			// check interruption and and exit if the execution was really interrupted
			if(isInterrupted())
			{
				RuntimeLogger.warn("Execution interrupted by user");
				return;
			}
		}
	}

	protected CoreItem getCompanyPermission(String companyName) throws AREasyException
	{
		CoreItem item = null;
		CoreItem search = new CoreItem();
		search.setFormName("CTM:SYS-Access Permission Grps");

		search.setAttribute(1000000001, companyName);
		search.setNullAttribute(301363000);
		search.setNullAttribute(301363100);
		search.setNullAttribute(301242000);

		List list = search.search(getServerConnection());

		for(int i = 0; list != null && i < list.size(); i++)
		{
			CoreItem entry = (CoreItem)list.get(i);
			if(entry.getAttributeValue(301363000) == null && entry.getAttributeValue(301363100) == null && entry.getAttributeValue(301242000) == null) item = entry;
		}

		return item;
	}
}