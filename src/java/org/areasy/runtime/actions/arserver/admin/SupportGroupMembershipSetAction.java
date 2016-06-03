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
import org.areasy.runtime.engine.base.ARDictionary;
import org.areasy.runtime.engine.base.AREasyException;
import org.areasy.runtime.engine.structures.CoreItem;
import org.areasy.runtime.engine.structures.data.itsm.foundation.People;
import org.areasy.runtime.engine.structures.data.itsm.foundation.SupportGroup;
import org.areasy.common.data.StringUtility;

import java.util.List;

/**
 * Adds a person to a support group.
 */
public class SupportGroupMembershipSetAction extends AbstractUserEnrollment
{
	/**
	 * Execute the current action.
	 *
	 * @throws org.areasy.runtime.engine.base.AREasyException
	 *          if any error will occur
	 */
	public void run() throws AREasyException
	{
		String sgroupCompany = getConfiguration().getString("sgroupcompany", getConfiguration().getString("supportgroupcompany", null));
		String sgroupOrganisation = getConfiguration().getString("sgrouporganisation", getConfiguration().getString("supportgrouporganisation", null));
		String sgroupName = getConfiguration().getString("sgroup", getConfiguration().getString("sgroupname", getConfiguration().getString("supportgroup", getConfiguration().getString("supportgroupname", null))));
		String sgroupId = getConfiguration().getString("sgroupid", getConfiguration().getString("supportgroupid", null));
		String role = getConfiguration().getString("role", "Member");
		
		boolean defaultGroup = getConfiguration().getBoolean("default",false);
		boolean supportstaff = getConfiguration().getBoolean("supportstaff", true);

		SupportGroup sgroup = new SupportGroup();
		if(sgroupCompany != null) sgroup.setCompanyName(sgroupCompany);
		if(sgroupOrganisation != null) sgroup.setOrganisationName(sgroupOrganisation);
		if(sgroupName != null) sgroup.setSupportGroupName(sgroupName);
		if(sgroupId != null) sgroup.setAttribute(1, sgroupId);
		sgroup.read(getServerConnection());

		//get real role name
		if(role.equals(role.toLowerCase())) role = StringUtility.capitalize(role);

		if (!sgroup.exists()) throw new AREasyException("Support Group '" + sgroupName + "' does not exist");

		for(int i = 0; i < getUsers().size(); i++)
		{
			String loginId = getUsers().get(i);

			People people = new People();
			people.setLoginId(loginId);
			people.read(getServerConnection());

			if (!people.exists())
			{
				RuntimeLogger.warn("Person with login '" + loginId + "' not found");
				continue;
			}

			//Check if already in group
			CoreItem prevAssoc = new CoreItem();
			prevAssoc.setFormName("CTM:Support Group Association");
			prevAssoc.setAttribute(ARDictionary.CTM_SGROUPID, sgroup.getEntryId()); 		//Support Group ID
			prevAssoc.setAttribute(ARDictionary.CTM_LOGINID, people.getLoginId());			//Login ID
			prevAssoc.read(getServerConnection());

			if (prevAssoc.exists())
			{
				RuntimeLogger.warn("Person with login '" + loginId + "' is already member of group '" + sgroupName + "'");
				return;
			}

			//Check default group
			if (defaultGroup)
			{
				CoreItem prevDefaultAssoc = new CoreItem();
				prevDefaultAssoc.setFormName("CTM:Support Group Association");
				prevDefaultAssoc.setAttribute(ARDictionary.CTM_LOGINID, people.getLoginId());			//Login ID
				prevDefaultAssoc.setAttribute(1000000075,0);
				prevDefaultAssoc.read(getServerConnection());

				if (prevDefaultAssoc.exists())
				{
					prevDefaultAssoc.setAttribute(1000000075, 1);
					prevDefaultAssoc.update(getServerConnection());
				}
			}

			//Add person to support group
			CoreItem ci = new CoreItem();
			ci.setFormName("CTM:Support Group Association");
			ci.setAttribute(ARDictionary.CTM_SGROUPID, sgroup.getEntryId()); 		//Support Group ID
			ci.setAttribute(ARDictionary.CTM_LOGINID, people.getLoginId());			//Login ID
			ci.setAttribute(ARDictionary.CTM_PERSONID, people.getEntryId());		//Person ID
			ci.setAttribute(ARDictionary.CTM_SGROUP_ASSOC_ROLE, role);				//Support Group Association Role
			ci.setAttribute(ARDictionary.CTM_FULLNAME, people.getFullName());		//Full Name
			ci.setAttribute(1000000075, defaultGroup ? 0 : (hasMemberships(people.getEntryId()) ? 1 : 0)); //Default Group (Yes/No)

			ci.create(getServerConnection());

			RuntimeLogger.info("'" + loginId + "' successfully added to group '" + sgroupName + "'");

			if(supportstaff)
			{
				people.setAttribute(1000000025, new Integer(0));
				people.setAttribute(1000000346, new Integer(0));

				people.update(getServerConnection());
				RuntimeLogger.debug("'" + loginId + "' profile updated setting 'Availability' and 'Support Staff' flags.");
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
	 * Checks whether a person is member of at least one Support Group.
	 * @param personID the person to check
	 * @return true if the person is member of at least one Support Group; false otherwise
	 * @throws AREasyException when error occurs
	 */
	private boolean hasMemberships(String personID) throws AREasyException
	{
		CoreItem searchMask = new CoreItem();
		searchMask.setFormName("CTM:Support Group Association");
		searchMask.setAttribute(ARDictionary.CTM_PERSONID, personID);
		List otherMemberships = searchMask.search(getServerConnection());

		return otherMemberships.size() > 0;
	}
}
