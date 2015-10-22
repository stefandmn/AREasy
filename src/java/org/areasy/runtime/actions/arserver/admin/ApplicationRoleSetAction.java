package org.areasy.runtime.actions.arserver.admin;

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
import org.areasy.runtime.engine.structures.data.itsm.foundation.People;
import org.areasy.runtime.engine.structures.data.itsm.foundation.SupportGroup;
import org.areasy.common.data.StringUtility;

/**
 * Dedicated action to set application role for an user or for a list of users
 */
public class ApplicationRoleSetAction extends AbstractUserEnrollmentAction
{
	/**
	 * Execute action's workflow.
	 *
	 * @throws org.areasy.runtime.engine.base.AREasyException if any global error occurs. All errors coming from action's execution will become output items
	 */
	public void run() throws AREasyException
	{
		//get the application permission and role
		String role = getConfiguration().getString("role", null);
		String sgroupCompany = getConfiguration().getString("sgroupcompany", getConfiguration().getString("supportgroupcompany", null));
		String sgroupOrganisation = getConfiguration().getString("sgrouporganisation", getConfiguration().getString("supportgrouporganisation", null));
		String sgroupName = getConfiguration().getString("sgroup", getConfiguration().getString("sgroupname", getConfiguration().getString("supportgroup", getConfiguration().getString("supportgroupname", null))));
		String sgroupId = getConfiguration().getString("sgroupid", getConfiguration().getString("supportgroupid", null));
		String appCode = getConfiguration().getString("appcode", getConfiguration().getString("applicationcode", null));

		SupportGroup sgroup = new SupportGroup();
		if(sgroupCompany != null) sgroup.setCompanyName(sgroupCompany);
		if(sgroupOrganisation != null) sgroup.setOrganisationName(sgroupOrganisation);
		if(sgroupName != null) sgroup.setSupportGroupName(sgroupName);
		if(sgroupId != null) sgroup.setAttribute(1, sgroupId);
		sgroup.read(getServerConnection());
		if(!sgroup.exists()) throw new AREasyException("No support group found: " + sgroup);

		//get real role name
		if(role.equals(role.toLowerCase())) role = StringUtility.capitalizeAll(role);

		//get real application name
		if(appCode != null && appCode.equals(appCode.toLowerCase())) appCode = StringUtility.capitalizeAll(appCode);

		CoreItem item = new CoreItem();
		item.setFormName("SYS:Menu Items");
		item.setAttribute(1000000007, "Functional Role");
		item.setAttribute(1000000008, role);
		item.setAttribute(1000000009, role);
		if(appCode != null) item.setAttribute(1000003698, appCode);
		item.read(getServerConnection());
		if(!item.exists()) throw new AREasyException("No '" + role + "' functional role registered in 'Menu Items' form");

		//execute the requested action for each user
		for(int i = 0; i < getUsers().size(); i++)
		{
			String username = getUsers().get(i);

			try
			{
				People person = new People();
				person.setLoginId(username);
				person.read(getServerConnection());

				if(person.exists())
				{
					CoreItem entry = new CoreItem();
					entry.setFormName("CTM:SupportGroupFunctionalRole");
					entry.setAttribute(1000001859, item.getAttributeValue(1000004336));
					entry.setAttribute(1000000171, item.getStringAttributeValue(1000001809));
					entry.setAttribute(1000000079, sgroup.getEntryId());
					entry.setAttribute(4, person.getLoginId());
					entry.setAttribute(1000000080, person.getEntryId());

					entry.read(getServerConnection());

					if(!entry.exists())
					{
						entry.setAttribute(1000000346, new Integer(0)); //assignment availability
						entry.setAttribute(1000000017, person.getFullName());
						entry.setAttribute(7, "Enabled");

						entry.create(getServerConnection());

						RuntimeLogger.info("Functional role '" + role + "' was configured for user '" + username + "'");
					}
					else RuntimeLogger.warn("Functional role '" + role + "' is already configured for user '" + username + "'");
				}
				else RuntimeLogger.error("People structure wasn't found: " + person);
			}
			catch(Throwable th)
			{
				RuntimeLogger.error("Error configuring functional role '" + role + "' for user '" + username + "': " + th.getMessage());
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
}
