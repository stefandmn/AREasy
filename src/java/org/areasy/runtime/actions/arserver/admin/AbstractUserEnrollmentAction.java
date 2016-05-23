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

import org.areasy.runtime.RuntimeAction;
import org.areasy.runtime.actions.AbstractAction;
import org.areasy.runtime.engine.RuntimeLogger;
import org.areasy.runtime.engine.base.AREasyException;
import org.areasy.runtime.engine.structures.CoreItem;
import org.areasy.runtime.engine.structures.data.itsm.foundation.People;

import java.util.List;
import java.util.Vector;

/**
 * Abstract class definition for user enrollment.
 */
public abstract class AbstractUserEnrollmentAction extends AbstractAction implements RuntimeAction
{
	private List<String> usernames = null;

	public void open() throws AREasyException
	{
		//get usernames
		String username = getConfiguration().getString("login", getConfiguration().getString("username", null));
		usernames = getConfiguration().getVector("logins", getConfiguration().getVector("usernames", new Vector<String>()));
		if(username != null && !usernames.contains(username)) usernames.add(username);

		//get usernames from a custom expression
		String fieldid = getConfiguration().getString("userfieldid", null);
		String fieldvalue = getConfiguration().getString("userfieldvalue", null);

		if(fieldid != null && fieldvalue != null)
		{
			String qualification = "'" +fieldid + "' = \"" + fieldvalue + "\"";
			People people = new People();
			List list = people.search(getServerConnection(), qualification);

			for(int i = 0; list != null && i < list.size(); i++)
			{
				People person = (People) list.get(i);
				usernames.add( person.getLoginId() );
			}

			if(list == null || list.isEmpty()) RuntimeLogger.warn("No entries found using: " + people);
		}
		else
		{
			String qualification = getConfiguration().getString("userqualification", null);
			String formname = getConfiguration().getString("userform", getConfiguration().getString("userformname", null));
			String userfieldid = getConfiguration().getString("userfieldid", null);

			if(qualification != null && formname == null)
			{
				People people = new People();
				List list = people.search(getServerConnection(), qualification);

				for(int i = 0; list != null && i < list.size(); i++)
				{
					People person = (People) list.get(i);
					usernames.add( person.getLoginId() );
				}

				if(list == null || list.isEmpty()) RuntimeLogger.warn("No entries found using: " + people);
			}
			else if(qualification != null && formname != null)
			{
				CoreItem item = new CoreItem();
				item.setFormName(formname);

				if(userfieldid == null) userfieldid = "4";

				List list = item.search(getServerConnection(), qualification);

				for(int i = 0; list != null && i < list.size(); i++)
				{
					CoreItem entry = (CoreItem) list.get(i);
					usernames.add( entry.getStringAttributeValue(userfieldid) );
				}

				if(list == null || list.isEmpty()) RuntimeLogger.warn("No entries found using: " + item);
			}
		}
	}

	public List<String> getUsers()
	{
		return usernames;
	}

	public void addUser(String username)
	{
		usernames.add(username);
	}

	protected void setExcludedUsers()
	{
		List exceptions = getConfiguration().getList("excludeusers", null);

		for(int i = 0; i < exceptions.size(); i++)
		{
			String loginId = (String) exceptions.get(i);

			if(getUsers().contains(loginId)) getUsers().remove(loginId);
		}
	}
}
