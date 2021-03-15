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
import org.areasy.runtime.engine.RuntimeLogger;
import org.areasy.runtime.engine.base.ARDictionary;
import org.areasy.runtime.engine.base.AREasyException;
import org.areasy.runtime.engine.structures.CoreItem;
import org.areasy.runtime.engine.structures.data.itsm.foundation.People;
import org.areasy.runtime.engine.structures.data.itsm.foundation.SupportGroup;

import java.util.ArrayList;
import java.util.List;

/**
 * Action for management of ITSM Support Groups.
 */
public class SupportGroupAdministration extends AbstractUserEnrollment
{
	public void run() throws AREasyException
	{
		String operation = getConfiguration().getString("operation", "setentity");

		if(StringUtility.equalsIgnoreCase(operation, "setentity") || StringUtility.equalsIgnoreCase(operation, "set") || StringUtility.equalsIgnoreCase(operation, "create") || StringUtility.equalsIgnoreCase(operation, "update")) setEntity();
		else if(StringUtility.equalsIgnoreCase(operation, "delentity") || StringUtility.equalsIgnoreCase(operation, "delete")) delEntity();
		else if(StringUtility.equalsIgnoreCase(operation, "setmembers")) setMembers();
		else if(StringUtility.equalsIgnoreCase(operation, "delmembers")) delMembers();
		else if(StringUtility.equalsIgnoreCase(operation, "setroles")) setFunctionalRole();
		else if(StringUtility.equalsIgnoreCase(operation, "delroles")) delFunctionalRole();
		else throw new AREasyException("Invalid execution operation: " + operation);
	}

	/**
	 * Create or update support group entities
	 *
	 * @throws AREasyException in case any error occur
	 */
	protected void setEntity() throws AREasyException
	{
		String groupCompany = getConfiguration().getString("sgroupcompany", getConfiguration().getString("supportgroupcompany", null));
		String groupOrganisation = getConfiguration().getString("sgrouporganisation", getConfiguration().getString("supportgrouporganisation", null));
		String groupName = getConfiguration().getString("sgroup", getConfiguration().getString("sgroupname", getConfiguration().getString("supportgroup", getConfiguration().getString("supportgroupname", null))));
		String groupId = getConfiguration().getString("sgroupid", getConfiguration().getString("supportgroupid", null));

		String groupRole = getConfiguration().getString("role", null);
		String groupStatus = getConfiguration().getString("status", null);
		List groupAliases = getConfiguration().getList("alias", new ArrayList());

		SupportGroup group = new SupportGroup();
		if(groupCompany != null) group.setCompanyName(groupCompany);
		if(groupOrganisation != null) group.setOrganisationName(groupOrganisation);
		if(groupName != null) group.setSupportGroupName(groupName);
		if(groupId != null) group.setAttribute(1, groupId);
		group.read(getServerConnection());
		this.run(group);

		if(group.exists())
		{
			if (getConfiguration().getBoolean("updateifexists", true))
			{
				group.setCompanyName(groupCompany);
				group.setOrganisationName(groupOrganisation);
				if (groupRole != null) group.setRole(groupRole);
				if (groupStatus != null) group.setAttribute(7, groupStatus);

				group.update(getServerConnection());
				RuntimeLogger.add("Support group '" + group.getEntryId() + "' has been updated");
			}
		}
		else
		{
			group.setCompanyName(groupCompany);
			group.setOrganisationName(groupOrganisation);
			group.setSupportGroupName(groupName);
			if(groupRole != null) group.setRole(groupRole);
			if(groupStatus != null) group.setAttribute(7, groupStatus);

			group.create(getServerConnection());
			RuntimeLogger.add("Support group '" + group.getEntryId() + "' has been created");
		}

		if(groupAliases != null && !groupAliases.isEmpty())
		{
			for (Object aliasObj : groupAliases)
			{
				String alias = (String) aliasObj;
				createAlias(group.getEntryId(), alias);
			}
		}
	}

	/**
	 * Delete support group entities
	 *
	 * @throws AREasyException in case any error occur
	 */
	protected void delEntity() throws AREasyException
	{
		String groupCompany = getConfiguration().getString("sgroupcompany", getConfiguration().getString("supportgroupcompany", null));
		String groupOrganisation = getConfiguration().getString("sgrouporganisation", getConfiguration().getString("supportgrouporganisation", null));
		String groupName = getConfiguration().getString("sgroup", getConfiguration().getString("sgroupname", getConfiguration().getString("supportgroup", getConfiguration().getString("supportgroupname", null))));
		String groupId = getConfiguration().getString("sgroupid", getConfiguration().getString("supportgroupid", null));

		SupportGroup group = new SupportGroup();
		if(groupCompany != null) group.setCompanyName(groupCompany);
		if(groupOrganisation != null) group.setOrganisationName(groupOrganisation);
		if(groupName != null) group.setSupportGroupName(groupName);
		if(groupId != null) group.setAttribute(1, groupId);
		group.read(getServerConnection());

		if(group.exists())
		{
			//remove aliases
			CoreItem searchAlias = new CoreItem();
			searchAlias.setFormName("CTM:Support Group Alias");
			searchAlias.setAttribute(ARDictionary.CTM_SGROUPID, group.getEntryId());
			List aliases = searchAlias.search(getServerConnection());
			for(int i = 0; i < aliases.size(); i++)
			{
				CoreItem item = (CoreItem) aliases.get(i);
				item.remove(getServerConnection());
			}

			CoreItem searchRole = new CoreItem();
			searchRole.setFormName("CTM:SupportGroupFunctionalRole");
			searchRole.setAttribute(ARDictionary.CTM_SGROUPID, group.getEntryId());
			List roles = searchAlias.search(getServerConnection());
			for(int i = 0; i < roles.size(); i++)
			{
				CoreItem item = (CoreItem) roles.get(i);
				item.remove(getServerConnection());
			}

			CoreItem searchMember = new CoreItem();
			searchMember.setFormName("CTM:Support Group Association");
			searchMember.setAttribute(ARDictionary.CTM_SGROUPID, group.getEntryId());
			List members = searchAlias.search(getServerConnection());
			for(int i = 0; i < members.size(); i++)
			{
				CoreItem item = (CoreItem) members.get(i);
				item.remove(getServerConnection());
			}

			group.remove(getServerConnection());
			RuntimeLogger.add("Support group '" + group.getEntryId() + "' has been deleted");
		}
		else RuntimeLogger.warn("Support group not found: " + group);
	}

	/**
	 * Adds person(s) to a support group.
	 *
	 * @throws AREasyException
	 */
	public void setMembers() throws AREasyException
	{
		String groupCompany = getConfiguration().getString("sgroupcompany", getConfiguration().getString("supportgroupcompany", null));
		String groupOrganisation = getConfiguration().getString("sgrouporganisation", getConfiguration().getString("supportgrouporganisation", null));
		String groupName = getConfiguration().getString("sgroup", getConfiguration().getString("sgroupname", getConfiguration().getString("supportgroup", getConfiguration().getString("supportgroupname", null))));
		String groupId = getConfiguration().getString("sgroupid", getConfiguration().getString("supportgroupid", null));

		String groupRole = getConfiguration().getString("role", "Member");
		boolean defaultGroup = getConfiguration().getBoolean("default", false);
		boolean supportStaff = getConfiguration().getBoolean("supportstaff", true);

		SupportGroup sgroup = new SupportGroup();
		if(groupCompany != null) sgroup.setCompanyName(groupCompany);
		if(groupOrganisation != null) sgroup.setOrganisationName(groupOrganisation);
		if(groupName != null) sgroup.setSupportGroupName(groupName);
		if(groupId != null) sgroup.setAttribute(1, groupId);
		sgroup.read(getServerConnection());

		//get real groupRole name
		if(groupRole.equals(groupRole.toLowerCase())) groupRole = StringUtility.capitalize(groupRole);

		if (!sgroup.exists()) throw new AREasyException("Support Group '" + groupName + "' does not exist");

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
				RuntimeLogger.warn("Person with login '" + loginId + "' is already member of group '" + groupName + "'");
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
			ci.setAttribute(ARDictionary.CTM_SGROUP_ASSOC_ROLE, groupRole);			//Support Group Association Role
			ci.setAttribute(ARDictionary.CTM_FULLNAME, people.getFullName());		//Full Name
			ci.setAttribute(1000000075, defaultGroup ? 0 : (hasMemberships(people.getEntryId()) ? 1 : 0)); //Default Group (Yes/No)

			this.run(ci);
			ci.create(getServerConnection());
			RuntimeLogger.info("'" + loginId + "' successfully added to group '" + groupName + "'");

			if(supportStaff)
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

	public void delMembers() throws AREasyException
	{
		String groupCompany = getConfiguration().getString("sgroupcompany", getConfiguration().getString("supportgroupcompany", null));
		String groupOrganisation = getConfiguration().getString("sgrouporganisation", getConfiguration().getString("supportgrouporganisation", null));
		String groupName = getConfiguration().getString("sgroup", getConfiguration().getString("sgroupname", getConfiguration().getString("supportgroup", getConfiguration().getString("supportgroupname", null))));
		String groupId = getConfiguration().getString("sgroupid", getConfiguration().getString("supportgroupid", null));

		SupportGroup group = new SupportGroup();
		if(groupCompany != null) group.setCompanyName(groupCompany);
		if(groupOrganisation != null) group.setOrganisationName(groupOrganisation);
		if(groupName != null) group.setSupportGroupName(groupName);
		if(groupId != null) group.setAttribute(1, groupId);
		group.read(getServerConnection());

		if(!group.exists() && !getConfiguration().getBoolean("skipvalidation", false)) throw new AREasyException("Support group '" + group + "' does not exist");

		//check if it was specified to find all existing users and to apply action's workflow to this users
		if (group.exists() && (getUsers() == null || getUsers().isEmpty())) setDiscoveredUsers(group.getEntryId());

		for(int i = 0; i < getUsers().size(); i++)
		{
			String loginId = getUsers().get(i);

			People people = new People();
			people.setLoginId(loginId);
			people.read(getServerConnection());

			if (!people.exists() && !getConfiguration().getBoolean("skipvalidation", false))
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

	public void setFunctionalRole() throws AREasyException
	{
		//get the application permission and role
		String role = getConfiguration().getString("role", null);
		String sgroupCompany = getConfiguration().getString("sgroupcompany", getConfiguration().getString("supportgroupcompany", null));
		String sgroupOrganisation = getConfiguration().getString("sgrouporganisation", getConfiguration().getString("supportgrouporganisation", null));
		String sgroupName = getConfiguration().getString("sgroup", getConfiguration().getString("sgroupname", getConfiguration().getString("supportgroup", getConfiguration().getString("supportgroupname", null))));
		String sgroupId = getConfiguration().getString("sgroupid", getConfiguration().getString("supportgroupid", null));
		String appCode = getConfiguration().getString("appcode", getConfiguration().getString("applicationcode", null));

		SupportGroup sgroup = new SupportGroup();
		if(sgroupCompany != null) sgroup.setCompanyName(sgroupCompany);
		if(sgroupOrganisation != null) sgroup.setOrganisationName(sgroupOrganisation);
		if(sgroupName != null) sgroup.setSupportGroupName(sgroupName);
		if(sgroupId != null) sgroup.setAttribute(1, sgroupId);
		sgroup.read(getServerConnection());
		if(!sgroup.exists()) throw new AREasyException("No support group found: " + sgroup);

		//get real role name
		if(role.equals(role.toLowerCase())) role = StringUtility.capitalizeAll(role);

		//get real application name
		if(appCode != null && appCode.equals(appCode.toLowerCase())) appCode = StringUtility.capitalizeAll(appCode);

		CoreItem item = new CoreItem();
		item.setFormName("SYS:Menu Items");
		item.setAttribute(1000000007, "Functional Role");
		item.setAttribute(1000000008, role);
		item.setAttribute(1000000009, role);
		if(appCode != null) item.setAttribute(1000003698, appCode);
		item.read(getServerConnection());
		if(!item.exists()) throw new AREasyException("No '" + role + "' functional role registered in 'Menu Items' form");

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
					entry.setFormName("CTM:SupportGroupFunctionalRole");
					entry.setAttribute(1000001859, item.getAttributeValue(1000004336));
					entry.setAttribute(1000000171, item.getStringAttributeValue(1000001809));
					entry.setAttribute(1000000079, sgroup.getEntryId());
					entry.setAttribute(4, person.getLoginId());
					entry.setAttribute(1000000080, person.getEntryId());

					entry.read(getServerConnection());
					this.run(entry);

					if(!entry.exists())
					{
						entry.setAttribute(1000000346, new Integer(0)); //assignment availability
						entry.setAttribute(1000000347, new Integer(0)); //assignment hold
						entry.setAttribute(1000000017, person.getFullName());
						entry.setAttribute(7, "Enabled");

						entry.create(getServerConnection());

						RuntimeLogger.info("Functional role '" + role + "' was configured for user '" + username + "'");
					}
					else RuntimeLogger.warn("Functional role '" + role + "' is already configured for user '" + username + "'");
				}
				else RuntimeLogger.error("People structure wasn't found: " + person);
			}
			catch(Throwable th)
			{
				RuntimeLogger.error("Error configuring functional role '" + role + "' for user '" + username + "': " + th.getMessage());
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

	public void delFunctionalRole() throws AREasyException
	{
		CoreItem role = new CoreItem();
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
			if(!group.exists() && !getConfiguration().getBoolean("skipvalidation", false)) throw new AREasyException("Support group '" + group + "' does not exist");
		}

		//get the application role
		String roleName = getConfiguration().getString("role", null);
		if(roleName != null && roleName.equals(roleName.toLowerCase())) roleName = StringUtility.capitalizeAll(roleName);

		String appCode = getConfiguration().getString("appcode", getConfiguration().getString("applicationcode", null));
		if(appCode != null && appCode.equals(appCode.toLowerCase())) appCode = StringUtility.capitalizeAll(appCode);

		if(roleName != null || appCode != null)
		{
			role.setFormName("SYS:Menu Items");
			role.setAttribute(1000000007, "Functional Role");

			if(roleName != null)
			{
				role.setAttribute(1000000008, roleName);
				role.setAttribute(1000000009, roleName);
			}

			if(appCode != null) role.setAttribute(1000003698, appCode);

			role.read(getServerConnection());
			if(!role.exists() && !getConfiguration().getBoolean("skipvalidation", false)) throw new AREasyException("No '" + role + "' functional role registered in 'Menu Items' form");
		}

		//check if it was specified to find all existing users and to apply action's workflow to this users
		if(role.exists()) setDiscoveredUsers(role.getStringAttributeValue(1000000008), group.getEntryId());

		//execute the requested action for each user
		for(int i = 0; i < getUsers().size(); i++)
		{
			String username = getUsers().get(i);

			try
			{
				People people = new People();
				people.setLoginId(username);
				people.read(getServerConnection());

				if (!people.exists() && !getConfiguration().getBoolean("skipvalidation", false))
				{
					RuntimeLogger.error("People structure wasn't found: " + people);
					continue;
				}

				CoreItem search = new CoreItem();
				search.setFormName("CTM:SupportGroupFunctionalRole");
				search.setAttribute(4, people.getLoginId());
				search.setAttribute(1000000080, people.getEntryId());

				if(role.exists())
				{
					search.setAttribute(1000001859, role.getAttributeValue(1000004336));
					search.setAttribute(1000000171, role.getStringAttributeValue(1000001809));
				}

				if(group.exists())
				{
					search.setAttribute(1000000079, group.getEntryId());
				}

				List list = search.search(getServerConnection());

				for(int x = 0; list != null && x < list.size(); x++)
				{
					CoreItem entry = (CoreItem) list.get(x);
					String objName1 = entry.getStringAttributeValue(1000000171);
					String objName2 = entry.getStringAttributeValue(1000000079);

					entry.setAttribute(1000000076, "DELETE");
					entry.update(getServerConnection());

					RuntimeLogger.info("Functional role '" + objName1 + "' has been removed for user '" + username + "' and support group '" + objName2 + "'");
				}
			}
			catch(Throwable th)
			{
				RuntimeLogger.error("Error removing functional role '" + role + "' for user '" + username + "': " + th.getMessage());
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
	 * Create alias for support group. If alias exists and enableIfExists is true it is enabled.
	 * If alias exists and enableIfExists is false nothing is done.
	 *
	 * @param supportGroupId Id of support group
	 * @param alias alias
	 * @throws AREasyException when error occurs
	 */
	private void createAlias(String supportGroupId, String alias) throws AREasyException
	{
		CoreItem entry = new CoreItem();
		entry.setFormName("CTM:Support Group Alias");
		entry.setAttribute(ARDictionary.CTM_SGROUPID, supportGroupId);
		entry.setAttribute(ARDictionary.CTM_SGROUPALIAS, alias);

		entry.read(getServerConnection());
		this.run(entry);

		if (entry.exists())
		{
			entry.setAttribute(7, "Enabled");
			entry.update(getServerConnection());
			RuntimeLogger.add("Support Group Alias " + alias + " enabled");
		}
		else
		{
			entry.create(getServerConnection());
			RuntimeLogger.add("Support Group Alias " + alias + " created");
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

	protected void setDiscoveredUsers(String role, String groupId) throws AREasyException
	{
		if(getConfiguration().getBoolean("findusers", false))
		{
			CoreItem item = new CoreItem();
			item.setFormName("CTM:SupportGroupFunctionalRole");
			item.setAttribute(1000000171, role);

			if(groupId != null) item.setAttribute(1000000079, groupId);

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
