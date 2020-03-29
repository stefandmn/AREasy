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

import org.areasy.common.data.StringUtility;
import org.areasy.runtime.RuntimeAction;
import org.areasy.runtime.engine.base.AREasyException;
import org.areasy.runtime.engine.structures.CoreItem;
import org.areasy.runtime.engine.structures.data.itsm.foundation.People;

import java.util.List;

/**
 * Dedicated action to remove system license for an user or for a list of users
 *
 */
public class SystemLicenseRemoveAction extends SystemLicenseSetAction implements RuntimeAction
{
	/**
	 * Execute action's workflow.
	 *
	 * @throws org.areasy.runtime.engine.base.AREasyException if any global error occurs. All errors comming from action's execution will become output items
	 */
	public void run() throws AREasyException
	{
		//check if it was specified to find all existing users and to apply action's workflow to this users
		getDiscoveredUsers();

		//run set action
		getConfiguration().setKey("license", "read");

		//run action by inheritance
		super.run();
	}

	protected void getDiscoveredUsers() throws AREasyException
	{
		if(getConfiguration().getBoolean("findusers", false))
		{
			List list = null;

			//get the application role
			String license = getConfiguration().getString("license", "read");

			int licenseType = -1;
			if(StringUtility.equalsIgnoreCase(license, "read")) licenseType = 0;
				else if(StringUtility.equalsIgnoreCase(license, "fixed")) licenseType = 1;
					else if(StringUtility.equalsIgnoreCase(license, "floating")) licenseType = 2;

			if(licenseType < 1) return;

			if(StringUtility.equals(getMethod(), "people"))
			{
				People person = new People();
				person.setAttribute(109, new Integer(licenseType));
				list = person.search(getServerConnection());
			}
			else if(StringUtility.equals(getMethod(), "user"))
			{
				CoreItem user = new CoreItem();
				user.setFormName("User");
				user.setAttribute(109, new Integer(licenseType));
				list = user.search(getServerConnection());
			}

			for(int i = 0; list != null && i < list.size(); i++)
			{
				CoreItem entry = (CoreItem) list.get(i);
				String loginId = entry.getStringAttributeValue(4);

				addUser(loginId);
			}

			setExcludedUsers();
		}
	}
}
