package org.areasy.runtime.actions.arserver.admin;

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

import org.areasy.runtime.actions.data.admin.AbstractUserEnrollment;
import org.areasy.runtime.engine.RuntimeLogger;
import org.areasy.runtime.engine.base.AREasyException;
import org.areasy.runtime.engine.structures.CoreItem;
import org.areasy.runtime.engine.structures.data.itsm.foundation.People;
import org.areasy.runtime.engine.structures.data.itsm.foundation.SupportGroup;
import org.areasy.common.data.StringUtility;

import java.util.List;

/**
 * Dedicated action to remove application license from an user or from a list of users
 * This action is part of the administration action tools for AR System server and it has
 * the main goal to remove a specific application role for one or many users.
 */
public class ApplicationRoleRemoveAction extends AbstractUserEnrollment
{
	/**
	 * Execute action's workflow.
	 *
	 * @throws org.areasy.runtime.engine.base.AREasyException if any global error occurs.
	 * All errors coming from action's execution will become output items
	 */
	public void run() throws AREasyException
	{
		CoreItem role = new CoreItem();
		SupportGroup group = new SupportGroup();

		String sgroupCompany = getConfiguration().getString("sgroupcompany", getConfiguration().getString("supportgroupcompany", null));
		String sgroupOrganisation = getConfiguration().getString("sgrouporganisation", getConfiguration().getString("supportgrouporganisation", null));
		String sgroupName = getConfiguration().getString("sgroup", getConfiguration().getString("sgroupname", getConfiguration().getString("supportgroup", getConfiguration().getString("supportgroupname", null))));
		String sgroupId = getConfiguration().getString("sgroupid", getConfiguration().getString("supportgroupid", null));

		if(sgroupCompany != null || sgroupOrganisation != null || sgroupName != null || sgroupId != null)
		{
			if(sgroupCompany != null) group.setCompanyName(sgroupCompany);
			if(sgroupOrganisation != null) group.setOrganisationName(sgroupOrganisation);
			if(sgroupName != null) group.setSupportGroupName(sgroupName);
			if(sgroupId != null) group.setAttribute(1, sgroupId);

			group.read(getServerConnection());
			if(!group.exists()) throw new AREasyException("Support group '" + group + "' does not exist");
		}

		//get the application role
		String roleName = getConfiguration().getString("role", null);
		if(roleName != null && roleName.equals(roleName.toLowerCase())) roleName = StringUtility.capitalizeAll(roleName);

		String appCode = getConfiguration().getString("appcode", getConfiguration().getString("applicationcode", null));
		if(appCode != null && appCode.equals(appCode.toLowerCase())) appCode = StringUtility.capitalizeAll(appCode);

		if(roleName != null || appCode != null)
		{
			role.setFormName("SYS:Menu Items");
			role.setAttribute(1000000007, "Functional Role");

			if(roleName != null)
			{
				role.setAttribute(1000000008, roleName);
				role.setAttribute(1000000009, roleName);
			}

			if(appCode != null) role.setAttribute(1000003698, appCode);

			role.read(getServerConnection());
			if(!role.exists()) throw new AREasyException("No '" + role + "' functional role registered in 'Menu Items' form");
		}

		//check if it was specified to find all existing users and to apply action's workflow to this users
		if(role.exists()) setDiscoveredUsers(role.getStringAttributeValue(1000000008), group.getEntryId());

		//execute the requested action for each user
		for(int i = 0; i < getUsers().size(); i++)
		{
			String username = getUsers().get(i);

			try
			{
				People people = new People();
				people.setLoginId(username);
				people.read(getServerConnection());

				if (!people.exists())
				{
					RuntimeLogger.error("People structure wasn't found: " + people);
					continue;
				}

				CoreItem search = new CoreItem();
				search.setFormName("CTM:SupportGroupFunctionalRole");
				search.setAttribute(4, people.getLoginId());
				search.setAttribute(1000000080, people.getEntryId());

				if(role.exists())
				{
					search.setAttribute(1000001859, role.getAttributeValue(1000004336));
					search.setAttribute(1000000171, role.getStringAttributeValue(1000001809));
				}

				if(group.exists())
				{
					search.setAttribute(1000000079, group.getEntryId());
				}

				List list = search.search(getServerConnection());

				for(int x = 0; list != null && x < list.size(); x++)
				{
					CoreItem entry = (CoreItem) list.get(x);
					String objName1 = entry.getStringAttributeValue(1000000171);
					String objName2 = entry.getStringAttributeValue(1000000079);

					entry.setAttribute(1000000076, "DELETE");
					entry.update(getServerConnection());

					RuntimeLogger.info("Functional role '" + objName1 + "' has been removed for user '" + username + "' and support group '" + objName2 + "'");
				}
			}
			catch(Throwable th)
			{
				RuntimeLogger.error("Error removing functional role '" + role + "' for user '" + username + "': " + th.getMessage());
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

	protected void setDiscoveredUsers(String role, String groupId) throws AREasyException
	{
		if(getConfiguration().getBoolean("findusers", false))
		{
			CoreItem item = new CoreItem();
			item.setFormName("CTM:SupportGroupFunctionalRole");
			item.setAttribute(1000000171, role);

			if(groupId != null) item.setAttribute(1000000079, groupId);

			List list = item.search(getServerConnection());

			for(int i = 0; i < list.size(); i++)
			{
				CoreItem entry = (CoreItem) list.get(i);
				String loginId = entry.getStringAttributeValue(4);

				addUser(loginId);
			}

			setExcludedUsers();
		}
	}
}
