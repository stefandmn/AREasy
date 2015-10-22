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
import org.areasy.common.data.NumberUtility;
import org.areasy.common.data.StringUtility;

/**
 * Dedicated action to set system license for an user or for a list of users
 *
 */
public class SystemLicenseSetAction extends AbstractUserEnrollmentAction
{
	private String method = null;

	public void open() throws AREasyException
	{
		method = getConfiguration().getString("method", "people");

		if(StringUtility.equals(method, "people"))
		{
			if(getConfiguration().containsKey("userformname")) getConfiguration().removeKey("userformname");
		}
		else if(StringUtility.equals(method, "user"))
		{
			if(getConfiguration().containsKey("userqualifiation"))
			{
				getConfiguration().setKey("userfieldid", "101");
				getConfiguration().setKey("userformname", "User");
			}
		}
		else throw new AREasyException("Invalid searching method: " + method);

		super.open();
	}

	/**
	 * Execute action's workflow.
	 *
	 * @throws org.areasy.runtime.engine.base.AREasyException if any global error occurs.
	 * All errors comming from action's execution will become output items
	 */
	public void run() throws AREasyException
	{
		//get the application role
		String license = getConfiguration().getString("license", "read");

		//validation of username(s) and specified role
		if(getUsers().isEmpty() || (!StringUtility.equalsIgnoreCase(license, "read") && !StringUtility.equalsIgnoreCase(license, "fixed") && !StringUtility.equalsIgnoreCase(license, "floating"))) throw new AREasyException("Invalid input command. Please refer help manual!");

		int licenseType = -1;
		if(StringUtility.equalsIgnoreCase(license, "read")) licenseType = 0;
			else if(StringUtility.equalsIgnoreCase(license, "fixed")) licenseType = 1;
				else if(StringUtility.equalsIgnoreCase(license, "floating")) licenseType = 2;

		//execute the requested action for each user
		for(int i = 0; i < getUsers().size(); i++)
		{
			String username = getUsers().get(i);

			try
			{
				if(StringUtility.equals(method, "people"))
				{
					People person = new People();
					person.setLoginId(username);
					person.read(getServerConnection());

					if(person.exists())
					{
						if(NumberUtility.toInt(person.getStringAttributeValue(109), -1) != licenseType)
						{
							person.setAttribute(109, new Integer(licenseType));
							person.update(getServerConnection());

							RuntimeLogger.info("'"+ license + "' system license has been configured for user '" + username + "' using 'people' method");
						}
						else RuntimeLogger.warn("'" + license + "' system license is already configured for '" + username + "' user account");
					}
					else if(!StringUtility.equals(method, "any")) RuntimeLogger.error("Person with login '" + username + "' not found");
				}
				else if(StringUtility.equals(method, "user"))
				{
					CoreItem user = new CoreItem();
					user.setFormName("User");

					user.setAttribute(101, username);
					user.read(getServerConnection());

					if(user.exists())
					{
						if(NumberUtility.toInt(user.getStringAttributeValue(109), -1) != licenseType)
						{
							user.setAttribute(109, new Integer(licenseType));
							user.update(getServerConnection());

							RuntimeLogger.info("'" + license + "' system license has been configured for user '" + username + "' using 'user' method");
						}
						else RuntimeLogger.warn("'" + license + "' system license is already configured for '" + username + "' user account");
					}
					else RuntimeLogger.error("User with login '" + user + "' not found");
				}
				else throw new AREasyException("Invalid searching method: " + method);
			}
			catch(Throwable th)
			{
				RuntimeLogger.error("Error configuring '" + license  + "' system license using '" + method + "' method, for user '" + username + "': " + th.getMessage());
				getLogger().debug("Exception", th);
			}
		}
	}

	protected String getMethod()
	{
		return method;
	}
}
