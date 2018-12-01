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

import org.areasy.runtime.actions.itsm.admin.AbstractUserEnrollment;
import org.areasy.runtime.engine.RuntimeLogger;
import org.areasy.runtime.engine.base.AREasyException;
import org.areasy.runtime.engine.structures.data.itsm.foundation.People;

/**
 * Dedicated action to remove unrestricted acess permission for an user or for a list of users
 *
 */
public class UnrestrictedAccessRemoveAction extends AbstractUserEnrollment
{
	/**
	 * Execute action's workflow.
	 *
	 * @throws org.areasy.runtime.engine.base.AREasyException if any global error occurs. All errors comming from action's execution will become output items
	 */
	public void run() throws AREasyException
	{
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
					//check people unrestricted access flag.
					if(person.getAttributeValue(1000003975) != null)
					{
						person.setNullAttribute(1000003975);
						person.setIgnoreNullValues(false);

						person.update(getServerConnection());
						RuntimeLogger.info("Unrestricted access flag was removed for user '" + username + "'");
					}
				}
				else RuntimeLogger.error("People account wasn't found: " + person);
			}
			catch(Throwable th)
			{
				RuntimeLogger.error("Error removing unrestricted access for user '" + username + "': " + th.getMessage());
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

	/**
	 * Get a help text about syntaxt execution of the current action.
	 *
	 * @return text message specifying the syntaxt of the current action
	 */
	public String getHelp()
	{
		return "-login <val> [-logins <val1> <val2> ... <valn>]";
	}
}