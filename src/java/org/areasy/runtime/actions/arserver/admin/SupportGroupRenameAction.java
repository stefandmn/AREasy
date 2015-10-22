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

import com.bmc.arsys.api.Constants;
import org.areasy.runtime.actions.AbstractAction;
import org.areasy.runtime.engine.RuntimeLogger;
import org.areasy.runtime.engine.base.ARDictionary;
import org.areasy.runtime.engine.base.AREasyException;
import org.areasy.runtime.engine.structures.CoreItem;
import org.areasy.runtime.engine.structures.data.itsm.Change;
import org.areasy.runtime.engine.structures.data.itsm.Incident;
import org.areasy.runtime.engine.structures.data.itsm.KnownError;
import org.areasy.runtime.engine.structures.data.itsm.Problem;
import org.areasy.runtime.engine.structures.data.itsm.foundation.SupportGroup;
import org.areasy.common.data.StringUtility;

import java.util.Iterator;
import java.util.List;

/**
 * Action to rename an existing support group by creating a new one and the old one will become Obsolete.
 * All members, functional roles, aliases etc. (all related details) will be transferred to the new group. Also this action has a dedicated option
 * to replace the old group details with the new group coordinates in incidents tickets, in incidents templates and other ITSM standard tickets,
 * without changing the existing status of incident tickets.
 */
public class SupportGroupRenameAction extends AbstractAction
{
	public void run() throws AREasyException
	{
		boolean allgroupdetails = getConfiguration().getBoolean("allgroupdetails", false);
		boolean memberships = getConfiguration().getBoolean("memberships", false);
		boolean aliases = getConfiguration().getBoolean("aliases", false);
		boolean approvalmappings = getConfiguration().getBoolean("approvalmappings", false);
		boolean favorites = getConfiguration().getBoolean("favorites", false);
		boolean oncalls = getConfiguration().getBoolean("oncalls", false);
		boolean shifts = getConfiguration().getBoolean("shifts", false);
		boolean allrelatedtickets = getConfiguration().getBoolean("allrelatedtickets", false);
		boolean incidents = getConfiguration().getBoolean("incidents", false);
		boolean incidentTemplates = getConfiguration().getBoolean("incidenttemplates", false);
		boolean problems = getConfiguration().getBoolean("problems", false);
		boolean knownerrors = getConfiguration().getBoolean("knownerrors", false);
		boolean problemTemplates = getConfiguration().getBoolean("problemtemplates", false);
		boolean changes = getConfiguration().getBoolean("changes", false);
		boolean changeTemplates = getConfiguration().getBoolean("changetemplates", false);
		boolean tasks = getConfiguration().getBoolean("tasks", false);
		boolean taskTemplates = getConfiguration().getBoolean("tasktemplates", false);
		boolean workorders = getConfiguration().getBoolean("workorders", false);
		boolean workorderTemplates = getConfiguration().getBoolean("workordertemplates", false);
		boolean assetrelationships = getConfiguration().getBoolean("assetrelationships", false);
		boolean cmdbrelationships = getConfiguration().getBoolean("cmdbrelationships", false);
		boolean knowledgerecords = getConfiguration().getBoolean("knowledgerecords", false);

		String oldCompany = getConfiguration().getString("sgroupcompany", getConfiguration().getString("supportgroupcompany", null));
		String oldOrganisation = getConfiguration().getString("sgrouporganisation", getConfiguration().getString("supportgrouporganisation", null));
		String oldGroup = getConfiguration().getString("sgroup", getConfiguration().getString("sgroupname", getConfiguration().getString("supportgroup", getConfiguration().getString("supportgroupname", null))));

		String newCompany = getConfiguration().getString("newsgroupcompany", getConfiguration().getString("newsupportgroupcompany", null));
		String newOrganisation = getConfiguration().getString("newsgrouporganisation", getConfiguration().getString("newsupportgrouporganisation", null));
		String newGroup = getConfiguration().getString("newsgroup", getConfiguration().getString("newsgroupname", getConfiguration().getString("newsupportgroup", getConfiguration().getString("newsupportgroupname", null))));

		SupportGroup oldSG = new SupportGroup();
		oldSG.setCompanyName(oldCompany);
		oldSG.setOrganisationName(oldOrganisation);
		oldSG.setSupportGroupName(oldGroup);

		SupportGroup newSG = new SupportGroup();
		newSG.setCompanyName(newCompany);
		newSG.setOrganisationName(newOrganisation);
		newSG.setSupportGroupName(newGroup);

		if(StringUtility.equals(newSG.getCompanyName(), oldSG.getCompanyName()) &&
			StringUtility.equals(newSG.getOrganisationName(), oldSG.getOrganisationName()) &&
			StringUtility.equals(newSG.getSupportGroupName(), oldSG.getSupportGroupName()))
		{
			RuntimeLogger.warn("New support group '" + newSG.getCompanyName() + "/" + newSG.getOrganisationName() + "/" + newSG.getSupportGroupName() +
							   "' is equals with the old support group: '" +
								oldSG.getCompanyName() + "/" + oldSG.getOrganisationName() + "/" + oldSG.getSupportGroupName() +"'");
			return;
		}

		//run new support group creation procedure and to make the old one obsolete
		updateSupportGroup(oldSG, newSG);

		if (memberships || allgroupdetails) transferMemberships(oldSG, newSG);
		if(aliases || allgroupdetails) transferGroupAliases(oldSG, newSG);
		if(approvalmappings || allgroupdetails) transferApprovalMappings(oldSG, newSG);
		if(favorites || allgroupdetails) transferFavorites(oldSG, newSG);
		if(oncalls || allgroupdetails) transferOnCallRecords(oldSG, newSG);
		if(shifts || allgroupdetails) transferShifts(oldSG, newSG);

		if(incidentTemplates || allrelatedtickets) updateIncidentTemplates(oldSG, newSG);
		if(incidents || allrelatedtickets) updateIncidents(oldSG, newSG);

		if(problemTemplates || allrelatedtickets) updateProblemTemplates(oldSG, newSG);
		if(problems || allrelatedtickets) updateProblems(oldSG, newSG);

		if(knownerrors || allrelatedtickets) updateKnownErrors(oldSG, newSG);

		if(changeTemplates || allrelatedtickets) updateChangeTemplates(oldSG, newSG);
		if(changes || allrelatedtickets) updateChanges(oldSG, newSG);

		if(taskTemplates || allrelatedtickets) updateTaskTemplates(oldSG, newSG);
		if(tasks || allrelatedtickets) updateTasks(oldSG, newSG);

		if(workorderTemplates || allrelatedtickets) updateWorkOrderTemplates(oldSG, newSG);
		if(workorders || allrelatedtickets) updateWorkOrders(oldSG, newSG);

		if(assetrelationships || allrelatedtickets) updateAssetRelationships(oldSG, newSG);
		if(cmdbrelationships || allrelatedtickets) updateCMDBRelationships(oldSG, newSG);

		if(knowledgerecords || allrelatedtickets) updateKnowledgeRecords(oldSG, newSG);
	}

	protected void updateSupportGroup(SupportGroup oldSG, SupportGroup newSG) throws AREasyException
	{
		boolean create = true;
		newSG.read(getServerConnection());

		if (newSG.exists())
		{
			RuntimeLogger.warn("Support group '" + newSG.getCompanyName() + "/" + newSG.getOrganisationName() + "/" + newSG.getSupportGroupName() + "' already exists; enabling and updating it");
			newSG.setStatus("Enabled");
			create = false;
		}

		oldSG.read(getServerConnection());
		if(!oldSG.exists()) throw new AREasyException("Support group '" + oldSG.getCompanyName() + "/" + oldSG.getOrganisationName() + "/" + oldSG.getSupportGroupName() + "' doesn't exist");

		newSG.setRole(oldSG.getRole());
		newSG.setDescription(oldSG.getDescription());
		newSG.setVendorGroup(oldSG.getVendorGroup());
		if(oldSG.getOnCallGroup() != null && oldSG.getOnCallGroup() == 0) newSG.setOnCallGroup(new Integer(1));
		newSG.setAttribute(1000000546, oldSG.getStringAttributeValue(1000000546)); //Business Holidays Tag
		newSG.setAttribute(1000000545, oldSG.getStringAttributeValue(1000000545));  //Business Workdays Tag
		newSG.setAttribute(303471800, oldSG.getStringAttributeValue(303471800)); //disable notifications
		newSG.setAttribute(303500800, oldSG.getStringAttributeValue(303500800)); //group email
		newSG.setAttribute(1000000571, oldSG.getStringAttributeValue(1000000571));	//uses SLA
		newSG.setAttribute(1000000572, oldSG.getStringAttributeValue(1000000572));	//uses OLA

		if (create)
		{
			newSG.create(getServerConnection());
			RuntimeLogger.info("Support group '" + newSG.getCompanyName() + "/" + newSG.getOrganisationName() + "/" + newSG.getSupportGroupName() + "' has been created");
		}
		else
		{
			newSG.setIgnoreNullValues(false);
			newSG.update(getServerConnection());
			RuntimeLogger.info("Support group '" + newSG.getCompanyName() + "/" + newSG.getOrganisationName() + "/" + newSG.getSupportGroupName() + "' has been updated and enabled");
		}

		oldSG.setStatus("Obsolete");
		oldSG.update(getServerConnection());
		RuntimeLogger.info("Support group '" + oldSG.getCompanyName() + "/" + oldSG.getOrganisationName() + "/" + oldSG.getSupportGroupName() + "' has been set as Obsolete");
	}

	protected void transferMemberships(SupportGroup fromGroup, SupportGroup toGroup) throws AREasyException
	{
		CoreItem searchMask = new CoreItem();
		searchMask.setFormName("CTM:Support Group Association");
		searchMask.setAttribute(ARDictionary.CTM_SGROUPID, fromGroup.getEntryId());		 //Support Group ID
		List groupAssociations = searchMask.search(getServerConnection());

		int correct = 0;
		int errors = 0;

		for (Iterator i = groupAssociations.iterator(); i.hasNext();)
		{
			CoreItem currentAssociation = (CoreItem) i.next();

			try
			{
				//Add person to support group
				CoreItem newAssociation = new CoreItem();
				newAssociation.setFormName("CTM:Support Group Association");
				newAssociation.setAttribute(ARDictionary.CTM_SGROUPID, toGroup.getEntryId());
				newAssociation.setAttribute(ARDictionary.CTM_LOGINID, currentAssociation.getAttributeValue(ARDictionary.CTM_LOGINID));
				newAssociation.setAttribute(ARDictionary.CTM_PERSONID, currentAssociation.getAttributeValue(ARDictionary.CTM_PERSONID));					//Person ID
				newAssociation.setAttribute(ARDictionary.CTM_SGROUP_ASSOC_ROLE, currentAssociation.getAttributeValue(ARDictionary.CTM_SGROUP_ASSOC_ROLE));  //Support Group Association Role
				newAssociation.setAttribute(ARDictionary.CTM_FULLNAME, currentAssociation.getAttributeValue(ARDictionary.CTM_FULLNAME));					//Full Name
				newAssociation.setAttribute(1000000075, currentAssociation.getAttributeValue(1000000075));												  //Default Group (Yes/No)
				newAssociation.setAttribute(1000000346, currentAssociation.getAttributeValue(1000000346));												  //Assignment Availability
				newAssociation.create(getServerConnection());

				//Remove person from old Group
				currentAssociation.setAttribute(ARDictionary.CTM_Z1DACTION, "DELETE"); //z1D Action
				currentAssociation.update(getServerConnection());
				RuntimeLogger.debug("Group member '" + currentAssociation.getAttributeValue(ARDictionary.CTM_LOGINID) + "' have been transferred to the new support group");
				correct++;
			}
			catch(AREasyException are)
			{
				RuntimeLogger.error("Error transferring group member '" + currentAssociation.getAttributeValue(ARDictionary.CTM_LOGINID) + "' to the new support group: " + are.getMessage());
				logger.debug("Exception", are);
				errors++;
			}
		}

		setInfoLoggerMessage(correct, errors, "support group member", "support group members", "transferred", "transfer");
		correct = 0;
		errors = 0;

		searchMask = new CoreItem();
		searchMask.setFormName("CTM:SupportGroupFunctionalRole");
		searchMask.setAttribute(ARDictionary.CTM_SGROUPID, fromGroup.getEntryId());		 //Support Group ID
		List groupFRoles = searchMask.search(getServerConnection());

		for (Iterator i = groupFRoles.iterator(); i.hasNext();)
		{
			CoreItem currentFRole = (CoreItem) i.next();

			try
			{
				//Add new FRole
				CoreItem newFRole = new CoreItem();
				newFRole.setFormName("CTM:SupportGroupFunctionalRole");
				newFRole.setAttribute(4, currentFRole.getAttributeValue(4));
				newFRole.setAttribute(ARDictionary.CTM_FULLNAME, currentFRole.getAttributeValue(ARDictionary.CTM_FULLNAME));	 //Full Name
				newFRole.setAttribute(ARDictionary.CTM_PERSONID, currentFRole.getAttributeValue(ARDictionary.CTM_PERSONID));	 //Person ID
				newFRole.setAttribute(1000000346, currentFRole.getAttributeValue(1000000346));								   //Assignment Availability
				newFRole.setAttribute(1000001859, currentFRole.getAttributeValue(1000001859));								   //Functional Role Alias
				newFRole.setAttribute(1000000347, currentFRole.getAttributeValue(1000000347));								   //Availability Hold
				newFRole.setAttribute(1000000171, currentFRole.getAttributeValue(1000000171));								   //Functional Role
				newFRole.setAttribute(ARDictionary.CTM_SGROUPID, toGroup.getEntryId());										  //Support Group ID

				newFRole.create(getServerConnection());

				//Remove FRole
				currentFRole.setAttribute(ARDictionary.CTM_Z1DACTION, "DELETE"); //z1D Action
				currentFRole.update(getServerConnection());
				RuntimeLogger.debug("Functional role '" + currentFRole.getAttributeValue(1000000171) + "' has been transferred for '" + currentFRole.getAttributeValue(ARDictionary.CTM_FULLNAME) + "' to the new support group");
				correct++;
			}
			catch(AREasyException are)
			{
				RuntimeLogger.error("Error transferring functional role '" + currentFRole.getAttributeValue(1000000171) + "' for '" + currentFRole.getAttributeValue(ARDictionary.CTM_FULLNAME) + "' to the new support group: " + are.getMessage());
				logger.debug("Exception", are);
				errors++;
			}
		}

		setInfoLoggerMessage(correct, errors, "functional role", "functional roles", "transferred", "transfer");
	}

	protected void transferGroupAliases(SupportGroup fromGroup, SupportGroup toGroup) throws AREasyException
	{
		CoreItem searchMask = new CoreItem();
		searchMask.setFormName("CTM:Support Group Alias");
		searchMask.setAttribute(ARDictionary.CTM_SGROUPID, fromGroup.getEntryId()); //Support Group ID
		searchMask.setAttribute(1000000073, 1); //Primary Alias=No
		List groupAliases = searchMask.search(getServerConnection());

		int correct = 0;
		int errors = 0;

		for (Iterator i = groupAliases.iterator(); i.hasNext();)
		{
			CoreItem alias = (CoreItem) i.next();

			try
			{
				alias.setAttribute(ARDictionary.CTM_SGROUPID, toGroup.getEntryId());
				alias.update(getServerConnection());

				RuntimeLogger.debug("Group alias '" + alias.getAttributeValue(1000000293) + "' has been transferred to the new support group");
				correct++;
			}
			catch(AREasyException are)
			{
				RuntimeLogger.error("Error transferring the group alias '" + alias.getAttributeValue(1000000293) + "' to the new support group: " + are.getMessage());
				logger.debug("Exception", are);
				errors++;
			}
		}

		setInfoLoggerMessage(correct, errors, "group alias", "group aliases", "transferred", "transfer");
	}

	protected void transferApprovalMappings(SupportGroup fromGroup, SupportGroup toGroup) throws AREasyException
	{
		CoreItem searchMask = new CoreItem();
		searchMask.setFormName("APR:Approver Lookup");
		searchMask.setAttribute(ARDictionary.CTM_SGROUPID, fromGroup.getEntryId()); //Support Group ID
		//searchMask.setAttribute(7, 1);
		List list = searchMask.search(getServerConnection());

		int correct = 0;
		int errors = 0;

		for (Iterator i = list.iterator(); i.hasNext();)
		{
			CoreItem data = (CoreItem) i.next();

			try
			{
				data.setAttribute(ARDictionary.CTM_SGROUPID, toGroup.getEntryId());
				data.update(getServerConnection());

				RuntimeLogger.debug("Group approval mapping '" + data.getEntryId() + "' has been transferred to the new support group");
				correct++;
			}
			catch(AREasyException are)
			{
				RuntimeLogger.error("Error transferring the group alias '" + data.getEntryId() + "' to the new support group: " + are.getMessage());
				logger.debug("Exception", are);
				errors++;
			}
		}

		setInfoLoggerMessage(correct, errors, "group approval mapping", "group approval mappings", "transferred", "transfer");
	}

	protected void transferFavorites(SupportGroup fromGroup, SupportGroup toGroup) throws AREasyException
	{
		CoreItem searchMask = new CoreItem();
		searchMask.setFormName("CTM:Support Group Assignments");
		searchMask.setAttribute(ARDictionary.CTM_SGROUPID, fromGroup.getEntryId()); //Support Group ID
		//searchMask.setAttribute(7, 1);
		List list = searchMask.search(getServerConnection());

		int correct = 0;
		int errors = 0;

		for (Iterator i = list.iterator(); i.hasNext();)
		{
			CoreItem data = (CoreItem) i.next();

			try
			{
				data.setAttribute(ARDictionary.CTM_SGROUPID, toGroup.getEntryId());
				data.update(getServerConnection());

				RuntimeLogger.debug("Group assignment '" + data.getEntryId() + "' (favorite group) has been transferred to the new support group");
				correct++;
			}
			catch(AREasyException are)
			{
				RuntimeLogger.error("Error transferring the group assignment '" + data.getEntryId() + "' (favorite group) to the new support group: " + are.getMessage());
				logger.debug("Exception", are);
				errors++;
			}
		}

		setInfoLoggerMessage(correct, errors, "group assignment", "group assignments", "transferred", "transfer");
	}

	protected void transferOnCallRecords(SupportGroup fromGroup, SupportGroup toGroup) throws AREasyException
	{
		CoreItem searchMask = new CoreItem();
		searchMask.setFormName("CTM:Support Group On-Call");
		searchMask.setAttribute(ARDictionary.CTM_SGROUPID, fromGroup.getEntryId()); //Support Group ID
		//searchMask.setAttribute(7, 1);
		List list = searchMask.search(getServerConnection());

		int correct = 0;
		int errors = 0;

		for (Iterator i = list.iterator(); i.hasNext();)
		{
			CoreItem data = (CoreItem) i.next();

			try
			{
				data.setAttribute(ARDictionary.CTM_SGROUPID, toGroup.getEntryId());
				data.update(getServerConnection());

				RuntimeLogger.debug("Group on-call record '" + data.getEntryId() + "' has been transferred to the new support group");
				correct++;
			}
			catch(AREasyException are)
			{
				RuntimeLogger.error("Error transferring group on-call record '" + data.getEntryId() + "' to the new support group: " + are.getMessage());
				logger.debug("Exception", are);
				errors++;
			}
		}

		setInfoLoggerMessage(correct, errors, "group on-call record", "group on-call records", "transferred", "transfer");

		try
		{
			if(fromGroup.getOnCallGroup() != null && fromGroup.getOnCallGroup() == 0) toGroup.setOnCallGroup(new Integer(0));
			toGroup.update(getServerConnection());
		}
		catch(AREasyException are)
		{
			RuntimeLogger.error("Error setting group on-call flag: " + are.getMessage());
			logger.debug("Exception", are);
		}
	}

	protected void transferShifts(SupportGroup fromGroup, SupportGroup toGroup) throws AREasyException
	{
		CoreItem searchMask = new CoreItem();
		searchMask.setFormName("CTM:Support Group Shifts");
		searchMask.setAttribute(ARDictionary.CTM_SGROUPID, fromGroup.getEntryId()); //Support Group ID
		//searchMask.setAttribute(7, 1);
		List list = searchMask.search(getServerConnection());

		int correct = 0;
		int errors = 0;

		for (Iterator i = list.iterator(); i.hasNext();)
		{
			CoreItem data = (CoreItem) i.next();

			try
			{
				data.setAttribute(ARDictionary.CTM_SGROUPID, toGroup.getEntryId());
				data.update(getServerConnection());

				RuntimeLogger.debug("Group shift '" + data.getEntryId() + "' has been transferred to the new support group");
				correct++;
			}
			catch(AREasyException are)
			{
				RuntimeLogger.error("Error transferring group shift '" + data.getEntryId() + "' to the new support group: " + are.getMessage());
				logger.debug("Exception", are);
				errors++;
			}
		}

		setInfoLoggerMessage(correct, errors, "group shift", "group shifts", "transferred", "transfer");
		correct = 0;
		errors = 0;

		searchMask = new CoreItem();
		searchMask.setFormName("CTM:Support Group Shift Assoc");
		searchMask.setAttribute(ARDictionary.CTM_SGROUPID, fromGroup.getEntryId()); //Support Group ID
		//searchMask.setAttribute(7, 1);
		list = searchMask.search(getServerConnection());

		for (Iterator i = list.iterator(); i.hasNext();)
		{
			CoreItem data = (CoreItem) i.next();

			try
			{
				data.setAttribute(ARDictionary.CTM_SGROUPID, toGroup.getEntryId());
				data.update(getServerConnection());

				RuntimeLogger.debug("Group shift association'" + data.getEntryId() + "' has been transferred to the new support group");
				correct++;
			}
			catch(AREasyException are)
			{
				RuntimeLogger.error("Error transferring group shift association '" + data.getEntryId() + "' to the new support group: " + are.getMessage());
				logger.debug("Exception", are);
				errors++;
			}
		}

		setInfoLoggerMessage(correct, errors, "group shift association", "group shift associations", "transferred", "transfer");
	}

	protected void updateIncidentTemplates(SupportGroup fromGroup, SupportGroup toGroup) throws AREasyException
	{
		//Update Assigned Group
		CoreItem searchMask = new CoreItem();
		searchMask.setFormName("HPD:Template");
		searchMask.setAttribute(1000000251, fromGroup.getCompanyName()); //Assigned Company
		searchMask.setAttribute(302126600, fromGroup.getOrganisationName()); //Assigned Organisation
		searchMask.setAttribute(1000000217, fromGroup.getSupportGroupName()); //Assigned Group
		//searchMask.setAttribute(1000000580, 1); //Template Status = Enabled
		List templates = searchMask.search(getServerConnection());

		int correct = 0;
		int errors = 0;

		for (Iterator i = templates.iterator(); i.hasNext();)
		{
			CoreItem template = (CoreItem) i.next();

			try
			{
				template.setAttribute(ARDictionary.CTM_SGROUPID, toGroup.getEntryId());
				template.setAttribute(1000000251, toGroup.getCompanyName());
				template.setAttribute(302126600, toGroup.getOrganisationName());
				template.setAttribute(1000000217, toGroup.getSupportGroupName());

				template.update(getServerConnection());
				RuntimeLogger.debug("Assignment of Incident template '" + template.getEntryId() + "' has been updated to take the new support group");
				correct++;
			}
			catch(AREasyException are)
			{
				RuntimeLogger.error("Error changing assignment group of Incident template '" + template.getEntryId() + "': " + are.getMessage());
				logger.debug("Exception", are);
				errors++;
			}
		}

		setInfoLoggerMessage(correct, errors, "incident template assignment", "incident template assignments", "updated", "update");
		correct = 0;
		errors = 0;

		//Update Authoring Group
		searchMask = new CoreItem();
		searchMask.setFormName("HPD:Template");
		searchMask.setAttribute(1000001341, fromGroup.getCompanyName()); //Authoring Company
		searchMask.setAttribute(1000001340, fromGroup.getOrganisationName()); //Authoring Organisation
		searchMask.setAttribute(1000001339, fromGroup.getSupportGroupName()); //Authoring Group
		templates = searchMask.search(getServerConnection());

		for (Iterator i = templates.iterator(); i.hasNext();)
		{
			CoreItem template = (CoreItem) i.next();

			try
			{
				template.setAttribute(1000000828, toGroup.getEntryId()); //Authoring Group ID
				template.setAttribute(1000001341, toGroup.getCompanyName()); //Authoring Company
				template.setAttribute(1000001340, toGroup.getOrganisationName()); //Authoring Organisation
				template.setAttribute(1000001339, toGroup.getSupportGroupName()); //Authoring Group

				template.update(getServerConnection());
				RuntimeLogger.debug("Authoring primary group of Incident template '" + template.getEntryId() + "' has been updated to take the new support group");
				correct++;
			}
			catch(AREasyException are)
			{
				RuntimeLogger.error("Error changing authoring primary group of Incident template '" + template.getEntryId() + "': " + are.getMessage());
				logger.debug("Exception", are);
				errors++;
			}
		}

		setInfoLoggerMessage(correct, errors, "incident template authoring primary group", "incident template authoring primary groups", "updated", "update");
		correct = 0;
		errors = 0;

		//Update Authoring Group
		searchMask = new CoreItem();
		searchMask.setFormName("HPD:TemplateSPGAssoc");
		searchMask.setAttribute(1000000001, fromGroup.getCompanyName()); //Authoring Company
		searchMask.setAttribute(1000000014, fromGroup.getOrganisationName()); //Authoring Organisation
		searchMask.setAttribute(1000000015, fromGroup.getSupportGroupName()); //Authoring Group
		templates = searchMask.search(getServerConnection());

		for (Iterator i = templates.iterator(); i.hasNext();)
		{
			CoreItem template = (CoreItem) i.next();

			try
			{
				template.setAttribute(1000000079, toGroup.getEntryId()); //Authoring Group ID
				template.setAttribute(1000000001, toGroup.getCompanyName()); //Authoring Company
				template.setAttribute(1000000014, toGroup.getOrganisationName()); //Authoring Organisation
				template.setAttribute(1000000015, toGroup.getSupportGroupName()); //Authoring Group

				template.update(getServerConnection());
				RuntimeLogger.debug("Authoring association group of Incident template '" + template.getEntryId() + "' has been updated to take the new support group");
				correct++;
			}
			catch(AREasyException are)
			{
				RuntimeLogger.error("Error changing authoring association group of Incident template '" + template.getEntryId() + "': " + are.getMessage());
				logger.debug("Exception", are);
				errors++;
			}
		}

		setInfoLoggerMessage(correct, errors, "incident template authoring association group", "incident template authoring association groups", "updated", "update");

	}

	protected void updateIncidents(SupportGroup fromGroup, SupportGroup toGroup) throws AREasyException
	{
		Long before = getConfiguration().getLong("incidentsbefore", null);
		Long after = getConfiguration().getLong("incidentsafter", null);
		boolean openIncidents = getConfiguration().getBoolean("openincidents", false);

		//Update Assigned Group
		Incident searchMask = new Incident();
		searchMask.setAssignmentCompany(fromGroup.getCompanyName());
		searchMask.setAssignmentOrganisation(fromGroup.getOrganisationName());
		searchMask.setAssignmentGroup(fromGroup.getSupportGroupName());

		if(openIncidents ) searchMask.setAttribute(7, "< 5");
		if (after != null) searchMask.setAttribute(3, "> " + after.longValue());
			else if (before != null) searchMask.setAttribute(3, "< " + before.longValue());

		List incidents = searchMask.search(getServerConnection());

		int correct = 0;
		int errors = 0;

		for (Iterator i = incidents.iterator(); i.hasNext();)
		{
			Incident incident = (Incident) i.next();

			try
			{
				incident.setAssignmentCompany(toGroup.getCompanyName());
				incident.setAssignmentOrganisation(toGroup.getOrganisationName());
				incident.setAssignmentGroup(toGroup.getSupportGroupName());
				incident.setAssignmentGroupId(toGroup.getEntryId());

				incident.merge(getServerConnection(), Constants.AR_MERGE_ENTRY_DUP_MERGE);
				RuntimeLogger.debug("Assignment of Incident '" + incident.getIncidentNumber() + "' has been updated to take the new support group");
				correct++;
			}
			catch(AREasyException are)
			{
				RuntimeLogger.error("Error changing assignment of Incident '" + incident.getIncidentNumber() + "': " + are.getMessage());
				logger.debug("Exception", are);
				errors++;
			}
		}

		setInfoLoggerMessage(correct, errors, "incident assignment", "incident assignments", "updated", "update");
		correct = 0;
		errors = 0;

		//Update Owner Group
		searchMask = new Incident();
		searchMask.setOwnerCompany(fromGroup.getCompanyName());
		searchMask.setOwnerOrganisation(fromGroup.getOrganisationName());
		searchMask.setOwnerGroup(fromGroup.getSupportGroupName());

		if(openIncidents ) searchMask.setAttribute(7, "< 5");
		if (after != null) searchMask.setAttribute(3, "> " + after.longValue());
			else if (before != null) searchMask.setAttribute(3, "< " + before.longValue());

		incidents = searchMask.search(getServerConnection());

		for (Iterator i = incidents.iterator(); i.hasNext();)
		{
			Incident incident = (Incident) i.next();

			try
			{
				incident.setOwnerCompany(toGroup.getCompanyName());
				incident.setOwnerOrganisation(toGroup.getOrganisationName());
				incident.setOwnerGroup(toGroup.getSupportGroupName());
				incident.setOwnerGroupId(toGroup.getEntryId());

				incident.merge(getServerConnection(), Constants.AR_MERGE_ENTRY_DUP_MERGE);
				RuntimeLogger.debug("Owner group of Incident '" + incident.getIncidentNumber() + "' has been updated to take the new support group");
				correct++;
			}
			catch(AREasyException are)
			{
				RuntimeLogger.error("Error changing owner group of Incident '" + incident.getIncidentNumber() + "': " + are.getMessage());
				logger.debug("Exception", are);
				errors++;
			}
		}

		setInfoLoggerMessage(correct, errors, "incident owner", "incident owners", "updated", "update");
	}

	protected void updateProblemTemplates(SupportGroup fromGroup, SupportGroup toGroup) throws AREasyException
	{
		//Update Coordinator Assigned Group
		CoreItem searchMask = new CoreItem();
		searchMask.setFormName("PBM:Template");
		searchMask.setAttribute(1000000834, fromGroup.getCompanyName()); //Coordinator Assigned Company
		searchMask.setAttribute(1000000835, fromGroup.getOrganisationName()); //Coordinator Assigned Organisation
		searchMask.setAttribute(1000000837, fromGroup.getSupportGroupName()); //Coordinator Assigned Group
		//searchMask.setAttribute(1000000580, 1); //Template Status = Enabled
		List templates = searchMask.search(getServerConnection());

		int correct = 0;
		int errors = 0;

		for (Iterator i = templates.iterator(); i.hasNext();)
		{
			CoreItem template = (CoreItem) i.next();

			try
			{
				template.setAttribute(ARDictionary.CTM_SGROUPID, toGroup.getEntryId());
				template.setAttribute(1000000834, toGroup.getCompanyName());
				template.setAttribute(1000000835, toGroup.getOrganisationName());
				template.setAttribute(1000000837, toGroup.getSupportGroupName());

				template.update(getServerConnection());
				RuntimeLogger.debug("Coordinator Assignment of Problem template '" + template.getEntryId() + "' has been updated to take the new support group");
				correct++;
			}
			catch(AREasyException are)
			{
				RuntimeLogger.error("Error changing coordinator assignment group of Problem template '" + template.getEntryId() + "': " + are.getMessage());
				logger.debug("Exception", are);
				errors++;
			}
		}

		//Update Problem Assigned Group
		searchMask.setFormName("PBM:Template");
		searchMask.setAttribute(1000000251, fromGroup.getCompanyName()); //Assigned Company
		searchMask.setAttribute(1000000014, fromGroup.getOrganisationName()); //Assigned Organisation
		searchMask.setAttribute(1000000217, fromGroup.getSupportGroupName()); //Assigned Group
		//searchMask.setAttribute(1000000580, 1); //Template Status = Enabled
		templates = searchMask.search(getServerConnection());

		for (Iterator i = templates.iterator(); i.hasNext();)
		{
			CoreItem template = (CoreItem) i.next();

			try
			{
				template.setAttribute(ARDictionary.CTM_SGROUPID, toGroup.getEntryId());
				template.setAttribute(1000000251, toGroup.getCompanyName());
				template.setAttribute(1000000014, toGroup.getOrganisationName());
				template.setAttribute(1000000217, toGroup.getSupportGroupName());

				template.update(getServerConnection());
				RuntimeLogger.debug("Assignment of Problem template '" + template.getEntryId() + "' has been updated to take the new support group");
				correct++;
			}
			catch(AREasyException are)
			{
				RuntimeLogger.error("Error changing assignment group of Problem template '" + template.getEntryId() + "': " + are.getMessage());
				logger.debug("Exception", are);
				errors++;
			}
		}

		setInfoLoggerMessage(correct, errors, "problem template assignment", "problem template assignments", "updated", "update");
		correct = 0;
		errors = 0;

		//Update Authoring Group
		searchMask = new CoreItem();
		searchMask.setFormName("PBM:Template");
		searchMask.setAttribute(1000001341, fromGroup.getCompanyName()); //Authoring Company
		searchMask.setAttribute(1000001340, fromGroup.getOrganisationName()); //Authoring Organisation
		searchMask.setAttribute(1000001339, fromGroup.getSupportGroupName()); //Authoring Group
		templates = searchMask.search(getServerConnection());

		for (Iterator i = templates.iterator(); i.hasNext();)
		{
			CoreItem template = (CoreItem) i.next();

			try
			{
				template.setAttribute(1000000828, toGroup.getEntryId()); //Authoring Group ID
				template.setAttribute(1000001341, toGroup.getCompanyName()); //Authoring Company
				template.setAttribute(1000001340, toGroup.getOrganisationName()); //Authoring Organisation
				template.setAttribute(1000001339, toGroup.getSupportGroupName()); //Authoring Group

				template.update(getServerConnection());
				RuntimeLogger.debug("Authoring primary group of Problem template '" + template.getEntryId() + "' has been updated to take the new support group");
				correct++;
			}
			catch(AREasyException are)
			{
				RuntimeLogger.error("Error changing authoring primary group of Problem template '" + template.getEntryId() + "': " + are.getMessage());
				logger.debug("Exception", are);
				errors++;
			}
		}

		setInfoLoggerMessage(correct, errors, "problem template authoring primary group", "problem template authoring primary groups", "updated", "update");
		correct = 0;
		errors = 0;

		//Update Authoring Group
		searchMask = new CoreItem();
		searchMask.setFormName("PBM:TemplateSPGAssoc");
		searchMask.setAttribute(1000000001, fromGroup.getCompanyName()); //Authoring Company
		searchMask.setAttribute(1000000014, fromGroup.getOrganisationName()); //Authoring Organisation
		searchMask.setAttribute(1000000015, fromGroup.getSupportGroupName()); //Authoring Group
		templates = searchMask.search(getServerConnection());

		for (Iterator i = templates.iterator(); i.hasNext();)
		{
			CoreItem template = (CoreItem) i.next();

			try
			{
				template.setAttribute(1000000079, toGroup.getEntryId()); //Authoring Group ID
				template.setAttribute(1000000001, toGroup.getCompanyName()); //Authoring Company
				template.setAttribute(1000000014, toGroup.getOrganisationName()); //Authoring Organisation
				template.setAttribute(1000000015, toGroup.getSupportGroupName()); //Authoring Group

				template.update(getServerConnection());
				RuntimeLogger.debug("Authoring association group of Problem template '" + template.getEntryId() + "' has been updated to take the new support group");
				correct++;
			}
			catch(AREasyException are)
			{
				RuntimeLogger.error("Error changing authoring association group of Problem template '" + template.getEntryId() + "': " + are.getMessage());
				logger.debug("Exception", are);
				errors++;
			}
		}

		setInfoLoggerMessage(correct, errors, "problem template authoring association group", "problem template authoring association groups", "updated", "update");
	}

	protected void updateProblems(SupportGroup fromGroup, SupportGroup toGroup) throws AREasyException
	{
		Long before = getConfiguration().getLong("problemsbefore", null);
		Long after = getConfiguration().getLong("problemsafter", null);
		boolean openProblems = getConfiguration().getBoolean("openproblems", false);

		//Update Assigned Group
		Problem searchMask = new Problem();
		searchMask.setAssignmentCompany(fromGroup.getCompanyName());
		searchMask.setAssignmentOrganisation(fromGroup.getOrganisationName());
		searchMask.setAssignmentGroup(fromGroup.getSupportGroupName());

		if(openProblems ) searchMask.setAttribute(7, "< 8");
		if (after != null) searchMask.setAttribute(3, "> " + after.longValue());
			else if (before != null) searchMask.setAttribute(3, "< " + before.longValue());

		List problems = searchMask.search(getServerConnection());

		int correct = 0;
		int errors = 0;

		for (Iterator i = problems.iterator(); i.hasNext();)
		{
			Problem problem = (Problem) i.next();

			try
			{
				problem.setAssignmentCompany(toGroup.getCompanyName());
				problem.setAssignmentOrganisation(toGroup.getOrganisationName());
				problem.setAssignmentGroup(toGroup.getSupportGroupName());
				problem.setAssignmentGroupId(toGroup.getEntryId());

				problem.merge(getServerConnection(), Constants.AR_MERGE_ENTRY_DUP_MERGE);
				RuntimeLogger.debug("Assignment of Problem '" + problem.getProblemNumber() + "' has been updated to take the new support group");
				correct++;
			}
			catch(AREasyException are)
			{
				RuntimeLogger.error("Error changing assignment of Problem '" + problem.getProblemNumber() + "': " + are.getMessage());
				logger.debug("Exception", are);
				errors++;
			}
		}

		setInfoLoggerMessage(correct, errors, "problem assignment", "problem assignments", "updated", "update");
		correct = 0;
		errors = 0;

		//Update Coordinator Group
		searchMask = new Problem();
		searchMask.setAssignedProblemCoordinatorCompany(fromGroup.getCompanyName());
		searchMask.setAssignedProblemCoordinatorOrganisation(fromGroup.getOrganisationName());
		searchMask.setAssignedProblemCoordinatorGroup(fromGroup.getSupportGroupName());

		if(openProblems ) searchMask.setAttribute(7, "< 8");
		if (after != null) searchMask.setAttribute(3, "> " + after.longValue());
			else if (before != null) searchMask.setAttribute(3, "< " + before.longValue());

		problems = searchMask.search(getServerConnection());

		for (Iterator i = problems.iterator(); i.hasNext();)
		{
			Problem problem = (Problem) i.next();

			try
			{
				problem.setAssignedProblemCoordinatorCompany(toGroup.getCompanyName());
				problem.setAssignedProblemCoordinatorOrganisation(toGroup.getOrganisationName());
				problem.setAssignedProblemCoordinatorGroup(toGroup.getSupportGroupName());
				problem.setAssignedProblemCoordinatorGroupId(toGroup.getEntryId());

				problem.merge(getServerConnection(), Constants.AR_MERGE_ENTRY_DUP_MERGE);
				RuntimeLogger.debug("Coordinator group of Problem '" + problem.getProblemNumber() + "' has been updated to take the new support group");
				correct++;
			}
			catch(AREasyException are)
			{
				RuntimeLogger.error("Error changing coordinator group of Problem '" + problem.getProblemNumber() + ": " + are.getMessage());
				logger.debug("Exception", are);
				errors++;
			}
		}

		setInfoLoggerMessage(correct, errors, "problem coordinator", "problem coordinators", "updated", "update");
	}

	protected void updateKnownErrors(SupportGroup fromGroup, SupportGroup toGroup) throws AREasyException
	{
		Long before = getConfiguration().getLong("knownerrorsbefore", null);
		Long after = getConfiguration().getLong("knownerrorssafter", null);
		boolean openKnownErrors = getConfiguration().getBoolean("openknownerrors", false);

		//Update Assigned Group
		KnownError searchMask = new KnownError();
		searchMask.setAssignmentCompany(fromGroup.getCompanyName());
		searchMask.setAssignmentOrganisation(fromGroup.getOrganisationName());
		searchMask.setAssignmentGroup(fromGroup.getSupportGroupName());

		if(openKnownErrors ) searchMask.setAttribute(7, "< 5");
		if (after != null) searchMask.setAttribute(3, "> " + after.longValue());
			else if (before != null) searchMask.setAttribute(3, "< " + before.longValue());

		List knownerrors = searchMask.search(getServerConnection());

		int correct = 0;
		int errors = 0;

		for (Iterator i = knownerrors.iterator(); i.hasNext();)
		{
			KnownError knownerror = (KnownError) i.next();

			try
			{
				knownerror.setAssignmentCompany(toGroup.getCompanyName());
				knownerror.setAssignmentOrganisation(toGroup.getOrganisationName());
				knownerror.setAssignmentGroup(toGroup.getSupportGroupName());
				knownerror.setAssignmentGroupId(toGroup.getEntryId());

				knownerror.merge(getServerConnection(), Constants.AR_MERGE_ENTRY_DUP_MERGE);
				RuntimeLogger.debug("Assignment of Known Error '" + knownerror.getKnownErrorId() + "' has been updated to take the new support group");
				correct++;
			}
			catch(AREasyException are)
			{
				RuntimeLogger.error("Error changing assignment of Known Error '" + knownerror.getKnownErrorId() + "': " + are.getMessage());
				logger.debug("Exception", are);
				errors++;
			}
		}

		setInfoLoggerMessage(correct, errors, "known error assignment", "known error assignments", "updated", "update");
		correct = 0;
		errors = 0;

		//Update Coordinator Group
		searchMask = new KnownError();
		searchMask.setAssignedKnownErrorCoordinatorCompany(fromGroup.getCompanyName());
		searchMask.setAssignedKnownErrorCoordinatorOrganisation(fromGroup.getOrganisationName());
		searchMask.setAssignedKnownErrorCoordinatorGroup(fromGroup.getSupportGroupName());

		if(openKnownErrors ) searchMask.setAttribute(7, "< 5");
		if (after != null) searchMask.setAttribute(3, "> " + after.longValue());
			else if (before != null) searchMask.setAttribute(3, "< " + before.longValue());

		knownerrors = searchMask.search(getServerConnection());

		for (Iterator i = knownerrors.iterator(); i.hasNext();)
		{
			KnownError knownerror = (KnownError) i.next();

			try
			{
				knownerror.setAssignedKnownErrorCoordinatorCompany(toGroup.getCompanyName());
				knownerror.setAssignedKnownErrorCoordinatorOrganisation(toGroup.getOrganisationName());
				knownerror.setAssignedKnownErrorCoordinatorGroup(toGroup.getSupportGroupName());
				knownerror.setAssignedKnownErrorCoordinatorGroupId(toGroup.getEntryId());

				knownerror.merge(getServerConnection(), Constants.AR_MERGE_ENTRY_DUP_MERGE);
				RuntimeLogger.debug("Coordinator group of Known Error '" + knownerror.getKnownErrorId() + "' has been updated to take the new support group");
				correct++;
			}
			catch(AREasyException are)
			{
				RuntimeLogger.error("Error changing coordinator group of Known Error '" + knownerror.getKnownErrorId() + "': " + are.getMessage());
				logger.debug("Exception", are);
				errors++;
			}
		}

		setInfoLoggerMessage(correct, errors, "known error coordinator", "known error coordinators", "updated", "update");
	}

	protected void updateChangeTemplates(SupportGroup fromGroup, SupportGroup toGroup) throws AREasyException
	{
		//Update Manager Assigned Group
		CoreItem searchMask = new CoreItem();
		searchMask.setFormName("CHG:Template");
		searchMask.setAttribute(1000000251, fromGroup.getCompanyName()); //Coordinator Assigned Company
		searchMask.setAttribute(1000000014, fromGroup.getOrganisationName()); //Coordinator Assigned Organisation
		searchMask.setAttribute(1000000015, fromGroup.getSupportGroupName()); //Coordinator Assigned Group
		//searchMask.setAttribute(7, 1); //Template Status = Enabled
		List templates = searchMask.search(getServerConnection());

		int correct = 0;
		int errors = 0;

		for (Iterator i = templates.iterator(); i.hasNext();)
		{
			CoreItem template = (CoreItem) i.next();

			try
			{
				template.setAttribute(ARDictionary.CTM_SGROUPID, toGroup.getEntryId());
				template.setAttribute(1000000251, toGroup.getCompanyName());
				template.setAttribute(1000000014, toGroup.getOrganisationName());
				template.setAttribute(1000000015, toGroup.getSupportGroupName());

				template.update(getServerConnection());
				RuntimeLogger.debug("Manager Assignment of Change template '" + template.getEntryId() + "' has been updated to take the new support group");
				correct++;
			}
			catch(AREasyException are)
			{
				RuntimeLogger.error("Error changing manager assignment group of Change template '" + template.getEntryId() + "': " + are.getMessage());
				logger.debug("Exception", are);
				errors++;
			}
		}

		//Update Coordinator Assigned Group
		searchMask = new CoreItem();
		searchMask.setFormName("CHG:Template");
		searchMask.setAttribute(1000003228, fromGroup.getCompanyName()); //Coordinator Assigned Company
		searchMask.setAttribute(1000003227, fromGroup.getOrganisationName()); //Coordinator Assigned Organisation
		searchMask.setAttribute(1000003229, fromGroup.getSupportGroupName()); //Coordinator Assigned Group
		//searchMask.setAttribute(7, 1); //Template Status = Enabled
		templates = searchMask.search(getServerConnection());

		for (Iterator i = templates.iterator(); i.hasNext();)
		{
			CoreItem template = (CoreItem) i.next();

			try
			{
				template.setAttribute(ARDictionary.CTM_SGROUPID, toGroup.getEntryId());
				template.setAttribute(1000003228, toGroup.getCompanyName());
				template.setAttribute(1000003227, toGroup.getOrganisationName());
				template.setAttribute(1000003229, toGroup.getSupportGroupName());

				template.update(getServerConnection());
				RuntimeLogger.debug("Coordinator Assignment of Change template '" + template.getEntryId() + "' has been updated to take the new support group");
				correct++;
			}
			catch(AREasyException are)
			{
				RuntimeLogger.error("Error changing coordinator assignment group of Change template '" + template.getEntryId() + "': " + are.getMessage());
				logger.debug("Exception", are);
				errors++;
			}
		}

		//Update Assigned Implementer Group
		searchMask.setFormName("CHG:Template");
		searchMask.setAttribute(1000003254, fromGroup.getCompanyName()); //Assigned Company
		searchMask.setAttribute(1000003255, fromGroup.getOrganisationName()); //Assigned Organisation
		searchMask.setAttribute(1000003256, fromGroup.getSupportGroupName()); //Assigned Group
		//searchMask.setAttribute(7, 1); //Template Status = Enabled
		templates = searchMask.search(getServerConnection());

		for (Iterator i = templates.iterator(); i.hasNext();)
		{
			CoreItem template = (CoreItem) i.next();

			try
			{
				template.setAttribute(ARDictionary.CTM_SGROUPID, toGroup.getEntryId());
				template.setAttribute(1000003254, toGroup.getCompanyName());
				template.setAttribute(1000003255, toGroup.getOrganisationName());
				template.setAttribute(1000003256, toGroup.getSupportGroupName());

				template.update(getServerConnection());
				RuntimeLogger.debug("Assignment of Change template '" + template.getEntryId() + "' has been updated to take the new support group");
				correct++;
			}
			catch(AREasyException are)
			{
				RuntimeLogger.error("Error changing assignment group of Change template '" + template.getEntryId() + "': " + are.getMessage());
				logger.debug("Exception", are);
				errors++;
			}
		}

		setInfoLoggerMessage(correct, errors, "change template assignment", "change template assignments", "updated", "update");
		correct = 0;
		errors = 0;

		//Update Authoring Group
		searchMask = new CoreItem();
		searchMask.setFormName("CHG:Template");
		searchMask.setAttribute(1000001341, fromGroup.getCompanyName()); //Authoring Company
		searchMask.setAttribute(1000001340, fromGroup.getOrganisationName()); //Authoring Organisation
		searchMask.setAttribute(1000001339, fromGroup.getSupportGroupName()); //Authoring Group
		templates = searchMask.search(getServerConnection());

		for (Iterator i = templates.iterator(); i.hasNext();)
		{
			CoreItem template = (CoreItem) i.next();

			try
			{
				template.setAttribute(1000000828, toGroup.getEntryId()); //Authoring Group ID
				template.setAttribute(1000001341, toGroup.getCompanyName()); //Authoring Company
				template.setAttribute(1000001340, toGroup.getOrganisationName()); //Authoring Organisation
				template.setAttribute(1000001339, toGroup.getSupportGroupName()); //Authoring Group

				template.update(getServerConnection());
				RuntimeLogger.debug("Authoring primary group of Change template '" + template.getEntryId() + "' has been updated to take the new support group");
				correct++;
			}
			catch(AREasyException are)
			{
				RuntimeLogger.error("Error changing authoring primary group of Change template '" + template.getEntryId() + "': " + are.getMessage());
				logger.debug("Exception", are);
				errors++;
			}
		}

		setInfoLoggerMessage(correct, errors, "change template authoring primary group", "change template authoring primary groups", "updated", "update");
		correct = 0;
		errors = 0;

		//Update Authoring Group
		searchMask = new CoreItem();
		searchMask.setFormName("CHG:TemplateSPGAssoc");
		searchMask.setAttribute(1000000001, fromGroup.getCompanyName()); //Authoring Company
		searchMask.setAttribute(1000000014, fromGroup.getOrganisationName()); //Authoring Organisation
		searchMask.setAttribute(1000000015, fromGroup.getSupportGroupName()); //Authoring Group
		templates = searchMask.search(getServerConnection());

		for (Iterator i = templates.iterator(); i.hasNext();)
		{
			CoreItem template = (CoreItem) i.next();

			try
			{
				template.setAttribute(1000000079, toGroup.getEntryId()); //Authoring Group ID
				template.setAttribute(1000000001, toGroup.getCompanyName()); //Authoring Company
				template.setAttribute(1000000014, toGroup.getOrganisationName()); //Authoring Organisation
				template.setAttribute(1000000015, toGroup.getSupportGroupName()); //Authoring Group

				template.update(getServerConnection());
				RuntimeLogger.debug("Authoring association group of Change template '" + template.getEntryId() + "' has been updated to take the new support group");
				correct++;
			}
			catch(AREasyException are)
			{
				RuntimeLogger.error("Error changing authoring association group of Problem template '" + template.getEntryId() + "': " + are.getMessage());
				logger.debug("Exception", are);
				errors++;
			}
		}

		setInfoLoggerMessage(correct, errors, "problem template authoring association group", "problem template authoring association groups", "updated", "update");
	}

	protected void updateChanges(SupportGroup fromGroup, SupportGroup toGroup) throws AREasyException
	{
		Long before = getConfiguration().getLong("changesbefore", null);
		Long after = getConfiguration().getLong("changesafter", null);
		boolean openChanges = getConfiguration().getBoolean("openchanges", false);

		//Update Assigned Group
		Change searchMask = new Change();
		searchMask.setAssignmentCompany(fromGroup.getCompanyName());
		searchMask.setAssignmentOrganisation(fromGroup.getOrganisationName());
		searchMask.setAssignmentGroup(fromGroup.getSupportGroupName());

		if(openChanges ) searchMask.setAttribute(7, "< 11");
		if (after != null) searchMask.setAttribute(3, "> " + after.longValue());
			else if (before != null) searchMask.setAttribute(3, "< " + before.longValue());

		List changes = searchMask.search(getServerConnection());

		int correct = 0;
		int errors = 0;

		for (Iterator i = changes.iterator(); i.hasNext();)
		{
			Change change = (Change) i.next();

			try
			{
				change.setAssignmentCompany(toGroup.getCompanyName());
				change.setAssignmentOrganisation(toGroup.getOrganisationName());
				change.setAssignmentGroup(toGroup.getSupportGroupName());
				change.setAssignmentGroupId(toGroup.getEntryId());

				change.merge(getServerConnection(), Constants.AR_MERGE_ENTRY_DUP_MERGE);
				RuntimeLogger.debug("Assignment of Change '" + change.getChangeNumber() + "' has been updated to take the new support group");
				correct++;
			}
			catch(AREasyException are)
			{
				RuntimeLogger.error("Error changing assignment of Change '" + change.getChangeNumber() + "': " + are.getMessage());
				logger.debug("Exception", are);
				errors++;
			}
		}

		setInfoLoggerMessage(correct, errors, "change assignment", "change assignments", "updated", "update");
		correct = 0;
		errors = 0;

		//Update Coordinator Group
		searchMask = new Change();
		searchMask.setAssignedChangeCoordinatorCompany(fromGroup.getCompanyName());
		searchMask.setAssignedChangeCoordinatorOrganisation(fromGroup.getOrganisationName());
		searchMask.setAssignedChangeCoordinatorGroup(fromGroup.getSupportGroupName());

		if(openChanges ) searchMask.setAttribute(7, "< 11");
		if (after != null) searchMask.setAttribute(3, "> " + after.longValue());
			else if (before != null) searchMask.setAttribute(3, "< " + before.longValue());

		changes = searchMask.search(getServerConnection());

		for (Iterator i = changes.iterator(); i.hasNext();)
		{
			Change change = (Change) i.next();

			try
			{
				change.setAssignedChangeCoordinatorCompany(toGroup.getCompanyName());
				change.setAssignedChangeCoordinatorOrganisation(toGroup.getOrganisationName());
				change.setAssignedChangeCoordinatorGroup(toGroup.getSupportGroupName());
				change.setAssignedChangeCoordinatorGroupId(toGroup.getEntryId());

				change.merge(getServerConnection(), Constants.AR_MERGE_ENTRY_DUP_MERGE);
				RuntimeLogger.debug("Coordinator group of Change '" + change.getChangeNumber() + "' has been updated to take the new support group");
				correct++;
			}
			catch(AREasyException are)
			{
				RuntimeLogger.error("Error changing coordinator group of Change '" + change.getChangeNumber() + ": " + are.getMessage());
				logger.debug("Exception", are);
				errors++;
			}
		}

		setInfoLoggerMessage(correct, errors, "change coordinator", "change coordinators", "updated", "update");
		correct = 0;
		errors = 0;

		//Update Manager Group
		searchMask = new Change();
		searchMask.setAssignedChangeManagerCompany(fromGroup.getCompanyName());
		searchMask.setAssignedChangeManagerOrganisation(fromGroup.getOrganisationName());
		searchMask.setAssignedChangeManagerGroup(fromGroup.getSupportGroupName());

		if(openChanges ) searchMask.setAttribute(7, "< 11");
		if (after != null) searchMask.setAttribute(3, "> " + after.longValue());
			else if (before != null) searchMask.setAttribute(3, "< " + before.longValue());

		changes = searchMask.search(getServerConnection());

		for (Iterator i = changes.iterator(); i.hasNext();)
		{
			Change change = (Change) i.next();

			try
			{
				change.setAssignedChangeManagerCompany(toGroup.getCompanyName());
				change.setAssignedChangeManagerOrganisation(toGroup.getOrganisationName());
				change.setAssignedChangeManagerGroup(toGroup.getSupportGroupName());
				change.setAssignedChangeManagerGroupId(toGroup.getEntryId());

				change.merge(getServerConnection(), Constants.AR_MERGE_ENTRY_DUP_MERGE);
				RuntimeLogger.debug("Manager group of Change '" + change.getChangeNumber() + "' has been updated to take the new support group");
				correct++;
			}
			catch(AREasyException are)
			{
				RuntimeLogger.error("Error changing manager group of Change '" + change.getChangeNumber() + ": " + are.getMessage());
				logger.debug("Exception", are);
				errors++;
			}
		}

		setInfoLoggerMessage(correct, errors, "change manager", "change managers", "updated", "update");
	}

	protected void updateTaskTemplates(SupportGroup fromGroup, SupportGroup toGroup) throws AREasyException
	{

	}

	protected void updateTasks(SupportGroup fromGroup, SupportGroup toGroup) throws AREasyException
	{

	}

	protected void updateWorkOrderTemplates(SupportGroup fromGroup, SupportGroup toGroup) throws AREasyException
	{

	}

	protected void updateWorkOrders(SupportGroup fromGroup, SupportGroup toGroup) throws AREasyException
	{

	}

	protected void updateKnowledgeRecords(SupportGroup fromGroup, SupportGroup toGroup) throws AREasyException
	{

	}

	protected void updateAssetRelationships(SupportGroup fromGroup, SupportGroup toGroup) throws AREasyException
	{
		CoreItem searchMask = new CoreItem();
		searchMask.setFormName("AST:AssetPeople");
		searchMask.setAttribute(260100006, fromGroup.getEntryId());
		searchMask.setAttribute(260100013, "Support Group");

		//searchMask.setAttribute(7, 1); //Template Status = Enabled
		List relations = searchMask.search(getServerConnection());

		int correct = 0;
		int errors = 0;

		for (Iterator i = relations.iterator(); i.hasNext();)
		{
			CoreItem relation = (CoreItem) i.next();

			try
			{
				relation.setAttribute(260100006, toGroup.getEntryId());
				relation.setAttribute(301104200, toGroup.getInstanceId());
				relation.setAttribute(260100003, toGroup.getCompanyName() + "->" + toGroup.getOrganisationName() + "->" + toGroup.getSupportGroupName());

				relation.update(getServerConnection());
				RuntimeLogger.debug("Asset relationship record '" + relation.getEntryId() + "' has been updated to take the new support group");
				correct++;
			}
			catch(AREasyException are)
			{
				RuntimeLogger.error("Error changing asset relationship record '" + relation.getEntryId() + "': " + are.getMessage());
				logger.debug("Exception", are);
				errors++;
			}
		}

		setInfoLoggerMessage(correct, errors, "asset relationship", "asset relationships", "updated", "update");
	}

	protected void updateCMDBRelationships(SupportGroup fromGroup, SupportGroup toGroup) throws AREasyException
	{

	}

	protected void setInfoLoggerMessage(int correct, int errors, String entity, String entities, String actioned, String action)
	{
		String correctString = "";
		String errorString = "";

		if(correct <= 0) correctString = "No " + entity + " has been " + actioned;
			else if(correct == 1) correctString = correct + " " + entity + " has been " + actioned;
				else correctString = correct + " " + entities + " have been " + actioned;

		if(errors <= 0) errorString = " and no error met during " + action;
			else if(errors == 1) errorString = " and " + errorString + " error met during " + action;
				else errorString = " and " + errorString + " errors have been met during " + action;

		RuntimeLogger.info(correctString + errorString);
	}
}
