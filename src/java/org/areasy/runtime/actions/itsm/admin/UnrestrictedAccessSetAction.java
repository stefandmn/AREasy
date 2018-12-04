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

import org.areasy.runtime.engine.RuntimeLogger;
import org.areasy.runtime.engine.base.AREasyException;
import org.areasy.runtime.engine.structures.CoreItem;
import org.areasy.runtime.engine.structures.data.itsm.foundation.People;

import java.util.List;

/**
 * Dedicated action to set unrestricted access permission for an user or for a list of users
 *
 */
public class UnrestrictedAccessSetAction extends AbstractUserEnrollment
{
	/**
	 * Execute action's workflow.
	 *
	 * @throws org.areasy.runtime.engine.base.AREasyException if any global error occurs. All errors comming from action's execution will become output items
	 */
	public void run() throws AREasyException
	{
		//exclusiv permission
		boolean exclusive = getConfiguration().getBoolean("exclusive", false);

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
					if(person.getAttributeValue(1000003975) == null)
					{
						person.setAttribute(1000003975, new Integer(0));
						person.update(getServerConnection());
						RuntimeLogger.info("Unrestricted access flag was configured for user '" + username + "'");
					}
					else RuntimeLogger.warn("Unrestricted access is already configured for user '" + username + "'");

					//remove access restriction permission if exists
					if(exclusive)
					{
						CoreItem entry = new CoreItem();
						entry.setFormName("CTM:People Permission Groups");

						entry.setAttribute(4, person.getLoginId());
						entry.setAttribute(1000000080, person.getEntryId());
						entry.setAttribute(1000003972, new Integer(2)); //permission group type

						List list = entry.search(getServerConnection());

						for(int x = 0; list != null && x < list.size(); x++)
						{
							CoreItem item = (CoreItem) list.get(x);
							RuntimeLogger.info("Remove restricted access to '" + item.getStringAttributeValue(1000000001) + "' company was configured for user '" + username + "'");

							item.setAttribute(1000000076, "DELETE"); //action
							item.update(getServerConnection());
						}
					}
				}
				else RuntimeLogger.error("People account wasn't found: " + person);
			}
			catch(Throwable th)
			{
				RuntimeLogger.error("Error configuring restricted access for user '" + username + "': " + th.getMessage());
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