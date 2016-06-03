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
import org.areasy.common.data.NumberUtility;
import org.areasy.common.data.StringUtility;

/**
 * Dedicated action to set application permission and license from an user or for a list of users
 *
 */
public class ApplicationPermissionSetAction extends AbstractUserEnrollment
{
	/**
	 * Execute action's workflow.
	 *
	 * @throws org.areasy.runtime.engine.base.AREasyException if any global error occurs. All errors coming from action's execution will become output items
	 */
	public void run() throws AREasyException
	{
		//get the application permission and role
		String permission = getConfiguration().getString("permission", null);
		String license = getConfiguration().getString("license", "none");

		//validation of username(s) and specified role
		if(getUsers().isEmpty() || permission == null || license == null ||
			(!StringUtility.equalsIgnoreCase(license, "Fixed") && !StringUtility.equalsIgnoreCase(license, "Floating") &&
			StringUtility.equalsIgnoreCase(license, "Read") &&!StringUtility.equalsIgnoreCase(license, "None") &&
			StringUtility.equalsIgnoreCase(license, "Not Applicable"))) throw new AREasyException("Invalid input command. Please check help manual!");

		//get real license type and role
		if(permission.equals(permission.toLowerCase())) permission = StringUtility.capitalizeAll(permission);
		if(license.equals(license.toLowerCase())) license = StringUtility.capitalizeAll(license);

		//get permission role structure
		CoreItem item = new CoreItem();
		item.setFormName("LIC:SYS-License Permission Map");
		item.setAttribute(1000001578, permission);
		item.read(getServerConnection());

		if(!item.exists()) throw new AREasyException("Permission role '" + permission + "' not found");

		//get required license type
		int requiredLicenseType = NumberUtility.toInt(item.getStringAttributeValue(1000002296), -1);
		if(requiredLicenseType == 0) license = "Not Applicable";
			else if(requiredLicenseType == 1) license = "Read";
				else if(requiredLicenseType == 2) license = "Fixed";
					else if(requiredLicenseType == 3) license = "Floating";
						else if(requiredLicenseType >=4 && !StringUtility.equalsIgnoreCase(license, "Floating") && !StringUtility.equalsIgnoreCase(license, "Fixed")) throw new AREasyException("For application role '" + permission + "' is mandatory to choose between 'Floating' or 'Fixed' license types");

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
					entry.setFormName("CTM:People Permission Groups");

					entry.setAttribute(4, person.getLoginId());
					entry.setAttribute(1000000080, person.getEntryId());
					entry.setAttribute(1000001578, item.getStringAttributeValue(1000001578)); //application role name
					entry.setAttribute(1000001579, new Integer(NumberUtility.toInt(item.getStringAttributeValue(1000001579), 0))); //application role id

					entry.read(getServerConnection());

					if(!entry.exists())
					{
						entry.setAttribute(7, "Enabled");
						entry.setAttribute(1000000076, "START"); //action
						entry.setAttribute(240001002, item.getStringAttributeValue(240001002)); //product name
						entry.setAttribute(1000002340, item.getStringAttributeValue(1000002340)); //permission tag name
						entry.setAttribute(1000003972, new Integer(1)); //permission group type
						entry.setAttribute(1000002294, license); //license type

						entry.create(getServerConnection());

						RuntimeLogger.info("Application role '" + permission + "' was configured for user '" + username + "'");
					}
					else RuntimeLogger.warn("Application role '" + permission + "' is already configured for user '" + username + "'");
				}
				else RuntimeLogger.error("People structure wasn't found: " + person);
			}
			catch(Throwable th)
			{
				RuntimeLogger.error("Error configuring role '" + permission + "' for user '" + username + "': " + th.getMessage());
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
