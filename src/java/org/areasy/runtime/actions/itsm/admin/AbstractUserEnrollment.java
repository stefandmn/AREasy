package org.areasy.runtime.actions.itsm.admin;

/*
 *  Copyright (c) 2007-2023 AREasy Runtime
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
import org.areasy.runtime.actions.ars.data.BaseData;
import org.areasy.runtime.engine.RuntimeLogger;
import org.areasy.runtime.engine.base.AREasyException;
import org.areasy.runtime.engine.structures.CoreItem;
import org.areasy.runtime.engine.structures.MultiPartItem;
import org.areasy.runtime.engine.structures.data.itsm.foundation.People;

import java.math.BigDecimal;
import java.util.List;
import java.util.Vector;

/**
 * Abstract class definition for user enrollment.
 */
public abstract class AbstractUserEnrollment extends BaseData implements RuntimeAction
{
	private List<String> usernames = null;

	public void open() throws AREasyException
	{
		super.open();

		//get usernames
		String username = getConfiguration().getString("login", getConfiguration().getString("username", null));
		usernames = getConfiguration().getVector("logins", getConfiguration().getVector("usernames", new Vector<String>()));
		if(username != null && !usernames.contains(username)) usernames.add(username);

		//get usernames from a custom expression
		String fieldid = getConfiguration().getString("userfieldid", null);
		String fieldvalue = "NULL";
		String qualification = null;
		String formname = null;

		if(fieldid != null)
		{
			if(getConfiguration().getKey("userfieldvalue") instanceof Integer)
			{
				fieldvalue = getConfiguration().getInteger("userfieldvalue", null).toString();
				qualification = "'" + fieldid + "' = " + fieldvalue;
			}
			else if(getConfiguration().getKey("userfieldvalue") instanceof Float)
			{
				fieldvalue = getConfiguration().getFloat("userfieldvalue", null).toString();
				qualification = "'" + fieldid + "' = " + fieldvalue;
			}
			else if(getConfiguration().getKey("userfieldvalue") instanceof BigDecimal)
			{
				fieldvalue = getConfiguration().getBigDecimal("userfieldvalue", null).toString();
				qualification = "'" + fieldid + "' = " + fieldvalue;
			}
			else if(getConfiguration().getKey("userfieldvalue") instanceof String)
			{
				fieldvalue = getConfiguration().getString("userfieldvalue", null).toString();
				if(fieldvalue.startsWith("%") || fieldvalue.endsWith("%")) qualification = "'" + fieldid + "' LIKE \"" + fieldvalue + "\"";
					else qualification = "'" + fieldid + "' = \"" + fieldvalue + "\"";
			}

			if(getConfiguration().getString("userqualification", null) != null)
			{
				qualification += " AND (" + getConfiguration().getString("userqualification", null) + ")";
			}
		}
		else
		{
			qualification = getConfiguration().getString("userqualification", null);
		}

		qualification = getTranslatedQualification(qualification);
		formname = getConfiguration().getString("userform", getConfiguration().getString("userformname", null));
		logger.debug("Running the following query for '"+ (formname == null ? "Poeple" : formname) + "' form entity: "+ qualification);

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
			long fielId = 4;
			if(StringUtility.equals(formname, "User")) fielId = 101;

			CoreItem item = new CoreItem();
			item.setFormName(formname);

			List list = item.search(getServerConnection(), qualification);

			for(int i = 0; list != null && i < list.size(); i++)
			{
				CoreItem entry = (CoreItem) list.get(i);
				usernames.add( entry.getStringAttributeValue(fielId) );
			}

			if(list == null || list.isEmpty()) RuntimeLogger.warn("No entries found using: " + item);
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

		for(int i = 0; exceptions!= null && i < exceptions.size(); i++)
		{
			String loginId = (String) exceptions.get(i);

			if(getUsers().contains(loginId)) getUsers().remove(loginId);
		}
	}

	public void run(CoreItem entry) throws AREasyException
	{
		if (getRunCondition(entry))
		{
			//data fill-in and create record
			if (!entry.exists())
			{
				//set data values
				setDataFields(entry, getConfiguration().getBoolean("createifnotexist", false), getConfiguration().getBoolean("updateifexists", false));

				if (getConfiguration().getBoolean("multipart", false) && entry instanceof MultiPartItem)
				{
					//set multipart form names
					setMultiPartForms(entry);

					//execute transactions
					((MultiPartItem) entry).commitParts(getServerConnection(), getMultiPartQueryFields(), getMultiPartDataFields());
				}
			}
			else if (entry.exists())
			{
				//set data values
				setDataFields(entry, getConfiguration().getBoolean("createifnotexist", false), getConfiguration().getBoolean("updateifexists", false));

				if (getConfiguration().getBoolean("multipart", false) && entry instanceof MultiPartItem)
				{
					//set multipart form names
					setMultiPartForms(entry);

					//execute transactions
					((MultiPartItem) entry).commitParts(getServerConnection(), getMultiPartQueryFields(), getMultiPartDataFields());
				}
			}
		}
		else logger.debug("Execution skipped due to run-condition evaluation");
	}
}
