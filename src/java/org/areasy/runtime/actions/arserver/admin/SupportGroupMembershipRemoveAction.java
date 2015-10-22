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
import org.areasy.runtime.engine.base.ARDictionary;
import org.areasy.runtime.engine.base.AREasyException;
import org.areasy.runtime.engine.structures.CoreItem;
import org.areasy.runtime.engine.structures.data.itsm.foundation.People;
import org.areasy.runtime.engine.structures.data.itsm.foundation.SupportGroup;

import java.util.List;

/**
 * Dedicated action to remove support group(s) for a login id
 */
public class SupportGroupMembershipRemoveAction extends AbstractUserEnrollmentAction
{
	/**
	 * Execute the current action.
	 *
	 * @throws org.areasy.runtime.engine.base.AREasyException
	 *          if any error will occur
	 */
	public void run() throws AREasyException
	{
		SupportGroup group = new SupportGroup();

		String sgroupCompany = getConfiguration().getString("sgroupcompany", getConfiguration().getString("supportgroupcompany", null));
		String sgroupOrganisation = getConfiguration().getString("sgrouporganisation", getConfiguration().getString("supportgrouporganisation", null));
		String sgroupName = getConfiguration().getString("sgroup", getConfiguration().getString("sgroupname", getConfiguration().getString("supportgroup", getConfiguration().getString("supportgroupname", null))));
		String sgroupId = getConfiguration().getString("sgroupid", getConfiguration().getString("supportgroupid", null));

		if(sgroupCompany != null || sgroupOrganisation != null || sgroupName != null || sgroupId != null)
		{
			if(sgroupCompany != null) group.setCompanyName(sgroupCompany);
			if(sgroupOrganisation != null) group.setOrganisationName(sgroupOrganisation);
			if(sgroupName != null) group.setSupportGroupName(sgroupName);
			if(sgroupId != null) group.setAttribute(1, sgroupId);

			group.read(getServerConnection());
			if(!group.exists()) throw new AREasyException("Support group '" + group + "' does not exist");
		}

		//check if it was specified to find all existing users and to apply action's workflow to this users
		if (group.exists() && (getUsers() == null || getUsers().isEmpty())) setDiscoveredUsers(group.getEntryId());

		for(int i = 0; i < getUsers().size(); i++)
		{
			String loginId = getUsers().get(i);

			People people = new People();
			people.setLoginId(loginId);
			people.read(getServerConnection());

			if (!people.exists())
			{
				RuntimeLogger.warn("Person with login id '" + loginId + "' not found");
				continue;
			}

			//Delete from CTM:Support Group Association
			CoreItem groupAssocSearch = new CoreItem();
			groupAssocSearch.setFormName("CTM:Support Group Association");
			groupAssocSearch.setAttribute(ARDictionary.CTM_PERSONID, people.getEntryId());		//Person ID
			if(group.exists()) groupAssocSearch.setAttribute(ARDictionary.CTM_SGROUPID, group.getEntryId()); 		//Support Group ID
			List groupAssocList = groupAssocSearch.search(getServerConnection());

			for(int x = 0; groupAssocList != null && x < groupAssocList.size(); x++)
			{
				CoreItem entry = (CoreItem) groupAssocList.get(x);
				entry.setAttribute(1000000076, "DELETE"); //z1D Action
				entry.update(getServerConnection());
			}

			//Delete from CTM:Support Group Shift Assoc
			CoreItem groupShiftAssocSearch = new CoreItem();
			groupShiftAssocSearch.setFormName("CTM:Support Group Shift Assoc");
			groupShiftAssocSearch.setAttribute(ARDictionary.CTM_PERSONID, people.getEntryId());		//Person ID
			if(group.exists()) groupShiftAssocSearch.setAttribute(ARDictionary.CTM_SGROUPID, group.getEntryId()); 		//Support Group ID
			List groupShiftAssocList = groupShiftAssocSearch.search(getServerConnection());

			for(int x = 0; groupShiftAssocList != null && x < groupShiftAssocList.size(); x++)
			{
				CoreItem entry = (CoreItem) groupShiftAssocList.get(x);
				entry.setAttribute(1000000076, "DELETE"); //z1D Action
				entry.update(getServerConnection());
			}

			//Delete from CTM:Support Group Shift Assoc
			CoreItem groupFuncRoleSearch = new CoreItem();
			groupFuncRoleSearch.setFormName("CTM:SupportGroupFunctionalRole");
			groupFuncRoleSearch.setAttribute(ARDictionary.CTM_PERSONID, people.getEntryId());		//Person ID
			if(group.exists()) groupFuncRoleSearch.setAttribute(ARDictionary.CTM_SGROUPID, group.getEntryId()); 		//Support Group ID
			List groupFuncRoleList = groupFuncRoleSearch.search(getServerConnection());

			for(int x = 0; groupFuncRoleList != null && x < groupFuncRoleList.size(); x++)
			{
				CoreItem entry = (CoreItem) groupFuncRoleList.get(x);
				entry.setAttribute(1000000076, "DELETE"); //z1D Action
				entry.update(getServerConnection());
			}

			//Delete any any that remains in "CTM:People Permission Groups"
			CoreItem groupPeoplePermSearch = new CoreItem();
			groupPeoplePermSearch.setFormName("CTM:People Permission Groups");
			groupPeoplePermSearch.setAttribute(4, people.getLoginId());
			groupPeoplePermSearch.setAttribute(1000000080, people.getEntryId());
			groupPeoplePermSearch.setAttribute(1000003972, new Integer(2));
			if(group.exists()) groupPeoplePermSearch.setAttribute(301242000, group.getEntryId());
			List groupPeoplePermList = groupPeoplePermSearch.search(getServerConnection());

			for(int x = 0; groupPeoplePermList != null && x < groupPeoplePermList.size(); x++)
			{
				CoreItem entry = (CoreItem) groupPeoplePermList.get(x);
				entry.setAttribute(1000000076, "DELETE");
				entry.update(getServerConnection());
			}

			RuntimeLogger.info("'" + loginId + "' successfully removed from " + (group.exists() ? "support group '" + group.getSupportGroupName() + "'" : "all support groups"));

			// check interruption and and exit if the execution was really interrupted
			if(isInterrupted())
			{
				RuntimeLogger.warn("Execution interrupted by user");
				return;
			}
		}
	}

	protected void setDiscoveredUsers(String groupId) throws AREasyException
	{
		if(getConfiguration().getBoolean("findusers", false))
		{
			CoreItem item = new CoreItem();
			item.setFormName("CTM:Support Group Association");
			item.setAttribute(ARDictionary.CTM_SGROUPID, groupId);
			List list = item.search(getServerConnection());

			for(int i = 0; i < list.size(); i++)
			{
				CoreItem entry = (CoreItem) list.get(i);
				String loginId = entry.getStringAttributeValue(4);

				addUser(loginId);
			}

			setExcludedUsers();
		}
	}
}
