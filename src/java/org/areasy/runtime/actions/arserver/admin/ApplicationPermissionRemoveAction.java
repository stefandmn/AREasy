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
import org.areasy.common.data.StringUtility;

import java.util.List;

/**
 * Dedicated action to remove application license from an user or from a list of users
 * This action is part of the administration action tools for AR System server and it has
 * the main goal to remove a specific application permission for one or many users.
 */
public class ApplicationPermissionRemoveAction extends AbstractUserEnrollmentAction
{
	/**
	 * Execute action's workflow.
	 *
	 * @throws org.areasy.runtime.engine.base.AREasyException
	 *          if any global error occurs. All errors comming from action's execution will become output items
	 */
	public void run() throws AREasyException
	{
		CoreItem permission = new CoreItem();

		//get the application role
		String permissionName = getConfiguration().getString("permission", null);
		if (permissionName != null && permissionName.equals(permissionName.toLowerCase())) permissionName = StringUtility.capitalizeAll(permissionName);

		if (permissionName != null)
		{
			//get permission role structure
			permission.setFormName("LIC:SYS-License Permission Map");
			permission.setAttribute(1000001578, permissionName);
			permission.read(getServerConnection());

			if (!permission.exists()) throw new AREasyException("Permission role '" + permissionName + "' not found");
		}

		//check if it was specified to find all existing users and to apply action's workflow to this users
		if (permission.exists() && (getUsers() == null || getUsers().isEmpty())) setDiscoveredUsers(permission);

		//execute the requested action for each user
		for (int i = 0; i < getUsers().size(); i++)
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
				search.setFormName("CTM:People Permission Groups");
				search.setAttribute(4, people.getLoginId());
				search.setAttribute(1000000080, people.getEntryId());
				search.setAttribute(1000003972, new Integer(1));

				if (permission.exists())
				{
					search.setAttribute(1000001578, permission.getStringAttributeValue(1000001578));
					search.setAttribute(1000001579, permission.getAttributeValue(1000001579));
				}

				List list = search.search(getServerConnection());

				for (int x = 0; list != null && x < list.size(); x++)
				{
					CoreItem entry = (CoreItem) list.get(x);
					String objName = entry.getStringAttributeValue(1000001578);

					entry.setAttribute(1000000076, "DELETE");
					entry.update(getServerConnection());

					RuntimeLogger.info("Application permission '" + objName + "' has been removed for user '" + username + "'");
				}
			}
			catch (Throwable th)
			{
				RuntimeLogger.error("Error removing permission '" + permission + "' for user '" + username + "': " + th.getMessage());
				getLogger().debug("Exception", th);
			}

			// check interruption and and exit if the execution was really interrupted
			if (isInterrupted())
			{
				RuntimeLogger.warn("Execution interrupted by user");
				return;
			}
		}
	}

	protected void setDiscoveredUsers(CoreItem permission) throws AREasyException
	{
		if (getConfiguration().getBoolean("findusers", false))
		{
			CoreItem item = new CoreItem();
			item.setFormName("CTM:People Permission Groups");
			item.setAttribute(1000001578, permission.getStringAttributeValue(1000001578));
			item.setAttribute(1000001579, permission.getStringAttributeValue(1000001579));
			List list = item.search(getServerConnection());

			for (int i = 0; i < list.size(); i++)
			{
				CoreItem entry = (CoreItem) list.get(i);
				String loginId = entry.getStringAttributeValue(4);

				addUser(loginId);
			}

			setExcludedUsers();
		}
	}
}
