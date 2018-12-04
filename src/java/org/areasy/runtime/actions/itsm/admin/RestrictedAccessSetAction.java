package org.areasy.runtime.actions.itsm.admin;

/*
 * Copyright (c) 2007-2018 AREasy Runtime
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
 * Dedicated action to set an access restriction for an user or for a list of users
 *
 */
public class RestrictedAccessSetAction extends AbstractUserEnrollment
{
	/**
	 * Execute action's workflow.
	 *
	 * @throws org.areasy.runtime.engine.base.AREasyException if any global error occurs. All errors comming from action's execution will become output items
	 */
	public void run() throws AREasyException
	{
		boolean exclusive = getConfiguration().getBoolean("exclusive", false);
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
					//cleanup unrestricted access if exists
					if(exclusive)
					{
						//check people unrestricted access flag.
						if(person.getAttributeValue(1000003975) != null)
						{
							person.setNullAttribute(1000003975);
							person.setIgnoreNullValues(false);

							person.update(getServerConnection());
							RuntimeLogger.info("Unrestricted access flag was removed for user '" + username + "'");
						}
					}

					//check if Unrestricted Acess Permission still exist
					CoreItem entry = new CoreItem();
					entry.setFormName("CTM:People Permission Groups");
					entry.setAttribute(4, person.getLoginId());
					entry.setAttribute(1000000080, person.getEntryId());
					entry.setAttribute(1000001578, company.getStringAttributeValue(1000001578)); //system group name
					entry.setAttribute(1000001579, new Long(NumberUtility.toInt(company.getStringAttributeValue(1000001579)))); //system group id
					entry.setAttribute(1000000001, company.getStringAttributeValue(1000000001));

					entry.read(getServerConnection());

					if(!entry.exists())
					{
						entry.setAttribute(7, "Enabled");
						entry.setAttribute(1000000076, "START"); //action
						entry.setAttribute(1000003972, new Integer(2)); //permission group type

						entry.create(getServerConnection());
						RuntimeLogger.info("Restricted access to '" + companyName + "' company was configured for user '" + username + "'");
					}
					else RuntimeLogger.warn("Restricted access to '" + companyName + "' company is already configured for user '" + username + "'");
				}
				else RuntimeLogger.error("People account wasn't found: " + person);
			}
			catch(Throwable th)
			{
				RuntimeLogger.error("Error configuring restricted access to '" + companyName + "' company for user '" + username + "': " + th.getMessage());
				getLogger().debug("Exception", th);
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

	/**
	 * Get a help text about syntaxt execution of the current action.
	 *
	 * @return text message specifying the syntaxt of the current action
	 */
	public String getHelp()
	{
		return "-company <company name> -login <val> [-logins <val1> <val2> ... <valn>] [-exclusive]";
	}
}