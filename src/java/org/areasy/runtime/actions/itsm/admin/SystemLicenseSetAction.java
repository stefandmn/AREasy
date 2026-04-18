package org.areasy.runtime.actions.itsm.admin;

/*
 *  Copyright (c) 2007-2025 AREasy Runtime
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
import org.areasy.common.data.StringUtility;
import org.areasy.runtime.engine.RuntimeLogger;
import org.areasy.runtime.engine.base.AREasyException;
import org.areasy.runtime.engine.structures.CoreItem;
import org.areasy.runtime.engine.structures.data.itsm.foundation.People;

import java.util.List;

/**
 * Dedicated action to set system license for an user or for a list of users
 *
 */
public class SystemLicenseSetAction extends AbstractUserEnrollment
{
	public static String BUNDLED_SMNUL = "Remedy Service Management Suite - User License";
	public static String BUNDLED_SMCUL = "Remedy Service Management Suite - Concurrent User License";
	public static String BUNDLED_SDNUL = "Remedy Service Desk - User License";
	public static String BUNDLED_SDCUL = "Remedy Service Desk - Concurrent User License";
	public static String BUNDLED_SONUL = "Remedy Service Optimization - User License";
	public static String BUNDLED_SOCUL = "Remedy Service Optimization - Concurrent User License";

	private String method = null;
	private String convert = null;

	public void open() throws AREasyException
	{
		method = getConfiguration().getString("method", "people");
		convert = getConfiguration().getString("convert", null);

		if(convert != null) method = "people";

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

		if(StringUtility.equals(convert, "fixed2bundled"))
		{
			getConfiguration().setKey("userfieldid", "109");
			getConfiguration().setKey("userfieldvalue", new Integer(1));
			getConfiguration().setKey("license", "bundled");
		}
		else if(StringUtility.equals(convert, "floating2bundled"))
		{
			getConfiguration().setKey("userfieldid", "109");
			getConfiguration().setKey("userfieldvalue", new Integer(2));
			getConfiguration().setKey("license", "bundled");
		}

		super.open();
	}

	/**
	 * Execute action's workflow.
	 *
	 * @throws org.areasy.runtime.engine.base.AREasyException if any global error occurs.
	 * All errors coming from action's execution will become output items
	 */
	public void run() throws AREasyException
	{
		//get the application role
		String license = getConfiguration().getString("license", "read");
		String bundled = getConfiguration().getString("bundled", null);

		//validation of username(s) and specified role
		if(getUsers().isEmpty() || (!StringUtility.equalsIgnoreCase(license, "read") && !StringUtility.equalsIgnoreCase(license, "fixed") && !StringUtility.equalsIgnoreCase(license, "floating") && !StringUtility.equalsIgnoreCase(license, "bundled"))) throw new AREasyException("Invalid input command. Please refer help manual!");

		int licenseType = -1;
		if(StringUtility.equalsIgnoreCase(license, "read")) licenseType = 0;
			else if(StringUtility.equalsIgnoreCase(license, "fixed")) licenseType = 1;
				else if(StringUtility.equalsIgnoreCase(license, "floating")) licenseType = 2;
				else if(StringUtility.equalsIgnoreCase(license, "bundled")) licenseType = 4;

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
							//apply bundled license
							boolean convert2Read = false;
							if(licenseType == 4) convert2Read = setBundledLicense(person, bundled);
							if(convert2Read)
							{
								RuntimeLogger.info("License converted to Read for user '" + person.getLoginId() + "'");
								licenseType = 0;
							}

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
							//applied bundled license
							if(licenseType == 4 && bundled != null) user.setAttribute(122, bundled);

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

	protected String getConvert()
	{
		return convert;
	}

	/**
	 * Registered bundled licenses for the specified People account. IN case the bundle name is not specified if is going to be detected
	 * based on the existing permissions. In case the bundle is registered, all licenses associated to permissions are reset.
	 * @param person People account to be updated
	 * @param bundled bundled license name, could be empty/null
	 * @return False if the conversion is applied or overwritten, and it is not need to convert the existing license into a READ one.
	 * @throws AREasyException in case of any error occurs
	 */
	public boolean setBundledLicense(People person, String bundled) throws AREasyException
	{
		// detect bundled named if its not specified - that means it is desired to optimize the type of licenses
		if(StringUtility.isEmpty(bundled))
		{
			int SD = 0;
			int SO = 0;

			CoreItem ppg = new CoreItem("CTM:People Permission Groups");
			ppg.setAttribute(4, person.getLoginId());
			ppg.setAttribute(1000003972, 1);
			List perms = ppg.search(getServerConnection());

			for(int i = 0; i < perms.size(); i++)
			{
				CoreItem perm = (CoreItem) perms.get(i);
				String product = perm.getStringAttributeValue(240001002);

				if(StringUtility.equals(product, "Incident Mgmt/Service Desk") || StringUtility.equals(product, "Problem Management")) SD = 1;
					else if(StringUtility.equals(product, "Change Management") || StringUtility.equals(product, "Configuration/Asset Management") || StringUtility.equals(product, "Service Level Management")) SO = 2;
			}

			int bundledType = SO + SD;
			int licenseType = NumberUtility.toInt(person.getStringAttributeValue(109), -1);

			if(bundledType == 1 && licenseType == 1) bundled = BUNDLED_SDNUL;
			else if(bundledType == 1 && licenseType == 2) bundled = BUNDLED_SDCUL;
			else if(bundledType == 2 && licenseType == 1) bundled = BUNDLED_SONUL;
			else if(bundledType == 2 && licenseType == 2) bundled = BUNDLED_SOCUL;
		}

		if(StringUtility.isNotEmpty(bundled))
		{
			CoreItem pbg = new CoreItem("CTM:PeopleBundledGroup");
			pbg.setAttribute(4, person.getLoginId());
			pbg.read(getServerConnection());

			if (pbg.exists())
			{
				pbg.setAttribute(8, bundled);
				pbg.update(getServerConnection());
			}
			else
			{
				pbg.setAttribute(2, getServerConnection().getUserName());
				pbg.setAttribute(7, "Enabled");
				pbg.setAttribute(8, bundled);
				pbg.setAttribute(1000000080, person.getEntryId());
				pbg.setAttribute(304414751, 2); //GA Blue
				pbg.create(getServerConnection());
			}

			//for people bundled licenses, reset licenses associated with permissions
			resetBundledPermissions(person);

			//update user profile from User form
			setBundledUser(person.getLoginId(), bundled);

			return false;
		}

		return true;
	}

	public void setBundledUser(String login, String bundled) throws AREasyException
	{
		CoreItem user = new CoreItem("User");
		user.setAttribute(101, login);
		user.read(getServerConnection());

		user.setAttribute(109, new Integer(4));
		user.setAttribute(122, bundled);
		user.update(getServerConnection());
	}

	public void resetBundledPermissions(People person) throws AREasyException
	{
		CoreItem ppg = new CoreItem("CTM:People Permission Groups");
		String query = "'4'=\"" + person.getLoginId() + "\" AND ('1000002294'=\"Fixed\" OR '1000002294'=\"Floating\")";
		List perms = ppg.search(getServerConnection(), query);

		for(int i = 0; i < perms.size(); i++)
		{
			CoreItem perm = (CoreItem) perms.get(i);

			perm.setAttribute(1000002294, "Not Applicable");
			perm.update(getServerConnection());
		}
	}
}
