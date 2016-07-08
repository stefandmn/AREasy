package org.areasy.runtime.actions.data.admin;

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

import com.bmc.arsys.api.Constants;
import org.areasy.runtime.actions.AbstractAction;
import org.areasy.runtime.engine.RuntimeLogger;
import org.areasy.runtime.engine.base.ARDictionary;
import org.areasy.runtime.engine.base.AREasyException;
import org.areasy.runtime.engine.structures.CoreItem;
import org.areasy.runtime.engine.structures.data.itsm.foundation.SupportGroup;
import org.areasy.common.data.StringUtility;

import java.util.List;

/**
 * Action to migrate an existing support group by creating a new one and the old one will become Obsolete.
 * All members, functional roles, aliases etc. (all related details) will be transferred to the new group. Also this action has a dedicated option
 * to replace the old group details with the new group coordinates in incidents tickets, in incidents templates and other ITSM standard tickets,
 * without changing the existing status of incident tickets.
 */
public class SupportGroupMigration extends AbstractAction
{
	private SupportGroup oldSG = null;
	private SupportGroup newSG = null;

	private String oldPermId = null;
	private String newPermId = null;

	private boolean skip = false;

	public void run() throws AREasyException
	{
		boolean allgroupdetails = getConfiguration().getBoolean("allgroupdetails", false);
		boolean members = getConfiguration().getBoolean("members", false);
		boolean functionalroles = getConfiguration().getBoolean("functionalroles", false);
		boolean aliases = getConfiguration().getBoolean("aliases", false);
		boolean favorites = getConfiguration().getBoolean("favorites", false);
		boolean oncalls = getConfiguration().getBoolean("oncalls", false);
		boolean shifts = getConfiguration().getBoolean("shifts", false);
		boolean autoassignments = getConfiguration().getBoolean("autoassignments", false);
		boolean approvalmappings = getConfiguration().getBoolean("approvalmappings", false);
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
		boolean knowledgerecords = getConfiguration().getBoolean("knowledgerecords", false);

		//Detect old support group and create/update the new support group
		setSupportGroups();
		if(isSkipped()) return;

		//update/create all related details
		if(members || allgroupdetails) setGroupMembers(getOldSupportGroup(), getNewSupportGroup());
		if(functionalroles || allgroupdetails) setFunctionalRoles(getOldSupportGroup(), getNewSupportGroup());
		if(aliases || allgroupdetails) setGroupAliases(getOldSupportGroup(), getNewSupportGroup());
		if(favorites || allgroupdetails) setGroupFavorites(getOldSupportGroup(), getNewSupportGroup());
		if(oncalls || allgroupdetails) setGroupOnCallRecords(getOldSupportGroup(), getNewSupportGroup());
		if(shifts || allgroupdetails) setGroupShifts(getOldSupportGroup(), getNewSupportGroup());
		if(autoassignments || allgroupdetails) setGroupAutoAssignments(getOldSupportGroup(), getNewSupportGroup());
		if(approvalmappings || allgroupdetails) setApprovalMappings(getOldSupportGroup(), getNewSupportGroup());

		//make the old support group unavailable
		disableOldSupportGroup();

		// update incidents and related templates
		if(incidentTemplates || allrelatedtickets) updateIncidentTemplates(getOldSupportGroup(), getNewSupportGroup());
		if(incidents || allrelatedtickets) updateIncidents(getOldSupportGroup(), getNewSupportGroup());

		//update problems, known errors and related templates
		if(problemTemplates || allrelatedtickets) updateProblemTemplates(getOldSupportGroup(), getNewSupportGroup());
		if(problems || allrelatedtickets) updateProblems(getOldSupportGroup(), getNewSupportGroup());
		if(knownerrors || allrelatedtickets) updateKnownErrors(getOldSupportGroup(), getNewSupportGroup());

		//update changes and related templates
		if(changeTemplates || allrelatedtickets) updateChangeTemplates(getOldSupportGroup(), getNewSupportGroup());
		if(changes || allrelatedtickets) updateChanges(getOldSupportGroup(), getNewSupportGroup());

		//update tasks and related templates
		if(taskTemplates || allrelatedtickets) updateTaskTemplates(getOldSupportGroup(), getNewSupportGroup());
		if(tasks || allrelatedtickets) updateTasks(getOldSupportGroup(), getNewSupportGroup());

		//update workorders and related templates
		if(workorderTemplates || allrelatedtickets) updateWorkOrderTemplates(getOldSupportGroup(), getNewSupportGroup());
		if(workorders || allrelatedtickets) updateWorkOrders(getOldSupportGroup(), getNewSupportGroup());

		// update CIs and related assets
		if(assetrelationships || allrelatedtickets) updateAssetRelationships(getOldSupportGroup(), getNewSupportGroup());

		//update KRs
		if(knowledgerecords || allrelatedtickets) updateKnowledgeRecords(getOldSupportGroup(), getNewSupportGroup());
	}

	protected boolean setSupportGroups() throws AREasyException
	{
		String oldCompany = getConfiguration().getString("sgroupcompany", getConfiguration().getString("supportgroupcompany", null));
		String oldOrganisation = getConfiguration().getString("sgrouporganisation", getConfiguration().getString("supportgrouporganisation", null));
		String oldGroupName = getConfiguration().getString("sgroup", getConfiguration().getString("sgroupname", getConfiguration().getString("supportgroup", getConfiguration().getString("supportgroupname", null))));
		String oldGroupId = getConfiguration().getString("sgroupid", getConfiguration().getString("supportgroupid", null));

		String newCompany = getConfiguration().getString("newsgroupcompany", getConfiguration().getString("newsupportgroupcompany", null));
		String newOrganisation = getConfiguration().getString("newsgrouporganisation", getConfiguration().getString("newsupportgrouporganisation", null));
		String newGroup = getConfiguration().getString("newsgroup", getConfiguration().getString("newsgroupname", getConfiguration().getString("newsupportgroup", getConfiguration().getString("newsupportgroupname", null))));

		oldSG = new SupportGroup();
		if(oldCompany != null) oldSG.setCompanyName(oldCompany);
		if(oldOrganisation != null) oldSG.setOrganisationName(oldOrganisation);
		if(oldGroupName != null) oldSG.setSupportGroupName(oldGroupName);
		if(oldGroupId != null) oldSG.setAttribute(1, oldGroupId);

		oldSG.setAttribute(7, new Integer(1));
		oldSG.read(getServerConnection());

		if(!oldSG.exists())
		{
			oldSG.deleteAttribute(7);
			oldSG.read(getServerConnection());

			if(!oldSG.exists()) throw new AREasyException("Support group '" + getSupportGroupString(oldSG) + "' doesn't exist");
		}

		newSG = new SupportGroup();
		newSG.setCompanyName(newCompany);
		newSG.setOrganisationName(newOrganisation);
		newSG.setSupportGroupName(newGroup);

		if(StringUtility.equals(newSG.getCompanyName(), oldSG.getCompanyName()) &&
			StringUtility.equals(newSG.getOrganisationName(), oldSG.getOrganisationName()) &&
			StringUtility.equals(newSG.getSupportGroupName(), oldSG.getSupportGroupName()))
		{
			RuntimeLogger.warn("New support group (" + getSupportGroupString(newSG) + ") has the same name and structure with the old support group: " + getSupportGroupString(oldSG));
			if(!getConfiguration().getBoolean("allowcircleupdate", false)) this.skip = true;
		}
		else this.skip = false;
		
		boolean create = true;
		newSG.read(getServerConnection());

		if (newSG.exists())
		{
			RuntimeLogger.info("Support group '" + getSupportGroupString(newSG) + "' already exists; enabling and updating it");
			newSG.setStatus("Enabled");
			create = false;
		}

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

		if (create && getConfiguration().getBoolean("creategroup", true))
		{
			newSG.create(getServerConnection());
			RuntimeLogger.info("Support group '" + getSupportGroupString(newSG) + "' has been created");
		}
		else if(getConfiguration().getBoolean("updategroup", true))
		{
			newSG.setIgnoreNullValues(false);
			newSG.update(getServerConnection());
			RuntimeLogger.info("Support group '" + getSupportGroupString(newSG) + "' has been updated and enabled");
		}

		//read permissions from new group and from old one
		oldPermId = oldSG.getRelatedSystemGroup(getServerConnection()).getStringAttributeValue(1000001579);
		newPermId = newSG.getRelatedSystemGroup(getServerConnection()).getStringAttributeValue(1000001579);
		return false;
	}

	protected void disableOldSupportGroup() throws AREasyException
	{
		boolean keepoldgroupenabled = getConfiguration().getBoolean("keepoldgroupenabled", !getConfiguration().getBoolean("keepoldmembers", true));

		if(!keepoldgroupenabled)
		{
			//make old group obsolete
			oldSG.setStatus("Obsolete");
			oldSG.update(getServerConnection());

			RuntimeLogger.info("The old support group '" + getSupportGroupString(oldSG) + "' became Obsolete");
		}
	}

	protected void setGroupMembers(SupportGroup fromGroup, SupportGroup toGroup) throws AREasyException
	{
		RuntimeLogger.info("Support Group > Members - Start migration procedure..");
		boolean keepoldmembership = getConfiguration().getBoolean("keepoldmembers", true);

		CoreItem searchMask = new CoreItem();
		searchMask.setFormName("CTM:Support Group Association");
		searchMask.setAttribute(ARDictionary.CTM_SGROUPID, fromGroup.getEntryId());         //Support Group ID
		List groupAssociations = searchMask.search(getServerConnection());

		RuntimeLogger.info("Found " + groupAssociations.size() + " records");
		int correct = 0;
		int errors = 0;

		for (Object groupAssociation : groupAssociations)
		{
			CoreItem currentAssociation = (CoreItem) groupAssociation;

			try
			{
				//Add person to support group
				CoreItem newAssociation = new CoreItem();
				newAssociation.setFormName("CTM:Support Group Association");
				newAssociation.setAttribute(ARDictionary.CTM_SGROUPID, toGroup.getEntryId());
				newAssociation.setAttribute(ARDictionary.CTM_LOGINID, currentAssociation.getAttributeValue(ARDictionary.CTM_LOGINID));
				newAssociation.setAttribute(ARDictionary.CTM_PERSONID, currentAssociation.getAttributeValue(ARDictionary.CTM_PERSONID));                    //Person ID
				newAssociation.setAttribute(ARDictionary.CTM_SGROUP_ASSOC_ROLE, currentAssociation.getAttributeValue(ARDictionary.CTM_SGROUP_ASSOC_ROLE));  //Support Group Association Role
				newAssociation.setAttribute(ARDictionary.CTM_FULLNAME, currentAssociation.getAttributeValue(ARDictionary.CTM_FULLNAME));                    //Full Name
				newAssociation.setAttribute(1000000346, currentAssociation.getAttributeValue(1000000346));                                                  //Assignment Availability

				newAssociation.read(getServerConnection());

				if (!newAssociation.exists())
				{
					newAssociation.setAttribute(1000000075, currentAssociation.getAttributeValue(1000000075)); //Default Group (Yes/No)
					newAssociation.create(getServerConnection());
					RuntimeLogger.debug("Group member '" + newAssociation.getEntryId() + "' became member of the new support group");
					correct++;
				}
				else RuntimeLogger.debug("Group member '" + newAssociation.getEntryId() + "' already exists");

				if (!keepoldmembership)
				{
					currentAssociation.setAttribute(ARDictionary.CTM_Z1DACTION, "DELETE"); //z1D Action
					currentAssociation.update(getServerConnection());
				}
				else if (currentAssociation.getAttributeValue(1000000075) != null && ((Integer)currentAssociation.getAttributeValue(1000000075)).intValue() == 0)
				{
					currentAssociation.setAttribute(1000000075, 1);
					currentAssociation.update(getServerConnection());
				}
			}
			catch (AREasyException are)
			{
				RuntimeLogger.error("Error migrating group member '" + currentAssociation.getEntryId() + "' for the new support group: " + are.getMessage());
				logger.debug("Exception", are);
				errors++;
			}
		}

		RuntimeLogger.info("Support Group > Members - End of migration procedure: " + correct + " update(s) and " + errors + " error(s)");
	}

	protected void setFunctionalRoles(SupportGroup fromGroup, SupportGroup toGroup) throws AREasyException
	{
		RuntimeLogger.info("Support Group > Functional Roles - Start migration procedure..");
		boolean keepoldfunctionalroles = getConfiguration().getBoolean("keepoldfunctionalroles", true);

		CoreItem searchMask = new CoreItem();
		searchMask.setFormName("CTM:SupportGroupFunctionalRole");
		searchMask.setAttribute(ARDictionary.CTM_SGROUPID, fromGroup.getEntryId());		 //Support Group ID
		List groupFRoles = searchMask.search(getServerConnection());

		RuntimeLogger.info("Found " + groupFRoles.size() + " records");
		int correct = 0;
		int errors = 0;

		for (Object groupFRole : groupFRoles)
		{
			CoreItem currentFRole = (CoreItem) groupFRole;

			try
			{
				CoreItem newFRole = new CoreItem();
				newFRole.setFormName("CTM:SupportGroupFunctionalRole");
				newFRole.setAttribute(ARDictionary.CTM_LOGINID, currentFRole.getAttributeValue(ARDictionary.CTM_LOGINID));
				newFRole.setAttribute(ARDictionary.CTM_FULLNAME, currentFRole.getAttributeValue(ARDictionary.CTM_FULLNAME));     //Full Name
				newFRole.setAttribute(ARDictionary.CTM_PERSONID, currentFRole.getAttributeValue(ARDictionary.CTM_PERSONID));     //Person ID
				newFRole.setAttribute(1000000346, currentFRole.getAttributeValue(1000000346));                                   //Assignment Availability
				newFRole.setAttribute(1000001859, currentFRole.getAttributeValue(1000001859));                                   //Functional Role Alias
				newFRole.setAttribute(1000000347, currentFRole.getAttributeValue(1000000347));                                   //Availability Hold
				newFRole.setAttribute(1000000171, currentFRole.getAttributeValue(1000000171));                                   //Functional Role
				newFRole.setAttribute(ARDictionary.CTM_SGROUPID, toGroup.getEntryId());                                          //Support Group ID

				newFRole.read(getServerConnection());
				if(!newFRole.exists())
				{
					newFRole.setAttribute(1000000075, currentFRole.getAttributeValue(1000000075) != null ? currentFRole.getAttributeValue(1000000075) : 1); //Default (Yes/No)
					newFRole.create(getServerConnection());
					RuntimeLogger.debug("Functional role '" +  newFRole.getEntryId() + "' has been created for the new support group");
					correct++;
				}
				else RuntimeLogger.debug("Functional role '" + newFRole.getEntryId() + "' already exists");

				if (!keepoldfunctionalroles)
				{
					currentFRole.setAttribute(ARDictionary.CTM_Z1DACTION, "DELETE"); //z1D Action
					currentFRole.update(getServerConnection());
				}
				else if (currentFRole.getAttributeValue(1000000075) != null && ((Integer)currentFRole.getAttributeValue(1000000075)).intValue() == 0)
				{
					currentFRole.setAttribute(1000000075, 1);
					currentFRole.update(getServerConnection());
				}
			}
			catch (AREasyException are)
			{
				RuntimeLogger.error("Error migrating functional role '" + currentFRole.getEntryId() + "' for the new support group: " + are.getMessage());
				logger.debug("Exception", are);
				errors++;
			}
		}

		RuntimeLogger.info("Support Group > Functional Roles - End of migration procedure: " + correct + " update(s) and " + errors + " error(s)");
	}

	protected void setGroupAliases(SupportGroup fromGroup, SupportGroup toGroup) throws AREasyException
	{
		RuntimeLogger.info("Support Group > Aliases - Start migration procedure..");
		boolean keepoldalias = getConfiguration().getBoolean("keepoldalias", true);

		CoreItem searchMask = new CoreItem();
		searchMask.setFormName("CTM:Support Group Alias");
		searchMask.setAttribute(ARDictionary.CTM_SGROUPID, fromGroup.getEntryId()); //Support Group ID
		searchMask.setAttribute(1000000073, 1); //Primary Alias=No
		List groupAliases = searchMask.search(getServerConnection());

		RuntimeLogger.info("Found " + groupAliases.size() + " records");
		int correct = 0;
		int errors = 0;

		for (Object groupAliase : groupAliases)
		{
			CoreItem alias = (CoreItem) groupAliase;

			try
			{
				CoreItem newAlias = new CoreItem();
				newAlias.setFormName("CTM:Support Group Alias");
				newAlias.setAttribute(1000000073, 1); //Primary Alias=No
				newAlias.setAttribute(ARDictionary.CTM_SGROUPID, toGroup.getEntryId()); //Support Group ID
				newAlias.setAttribute(1000000293, alias.getAttributeValue(1000000293));
				newAlias.read(getServerConnection());

				if(!newAlias.exists())
				{
					newAlias.create(getServerConnection());
					RuntimeLogger.debug("Group alias '" + newAlias.getEntryId() + "' has been created for the new support group");
					correct++;
				}
				else RuntimeLogger.debug("Group alias '" + newAlias.getEntryId() + "' already exists");

				//Remove Alias
				if (!keepoldalias)
				{
					alias.setAttribute(ARDictionary.CTM_Z1DACTION, "DELETE"); //z1D Action
					alias.update(getServerConnection());
				}
			}
			catch (AREasyException are)
			{
				RuntimeLogger.error("Error migrating group alias '" + alias.getEntryId() + "' for the new support group: " + are.getMessage());
				logger.debug("Exception", are);
				errors++;
			}
		}

		RuntimeLogger.info("Support Group > Aliases - End of migration procedure: " + correct + " update(s) and " + errors + " error(s)");
	}

	protected void setGroupFavorites(SupportGroup fromGroup, SupportGroup toGroup) throws AREasyException
	{
		RuntimeLogger.info("Support Group > Assignments - Start migration procedure..");
		boolean keepoldfavorites = getConfiguration().getBoolean("keepoldfavorites", true);

		CoreItem searchMask = new CoreItem();
		searchMask.setFormName("CTM:Support Group Assignments");
		searchMask.setAttribute(1000000789, fromGroup.getEntryId()); //Default Support Group ID
		List groupAssignments = searchMask.search(getServerConnection());

		RuntimeLogger.info("Found " + groupAssignments.size() + " referred  records");
		int correct = 0;
		int errors = 0;

		for (Object groupAssigObj : groupAssignments)
		{
			CoreItem groupAssignment = (CoreItem) groupAssigObj;

			try
			{
				groupAssignment.setAttribute(1000000789, toGroup.getEntryId());
				groupAssignment.update(getServerConnection());

				RuntimeLogger.debug("Group assignment '" + groupAssignment.getEntryId() + "' (favorite group) has been updated to refer the new support group");
				correct++;
			}
			catch (AREasyException are)
			{
				RuntimeLogger.error("Error updating existing group assignment '" + groupAssignment.getEntryId() + "' (favorite group) for the new support group: " + are.getMessage());
				logger.debug("Exception", are);
				errors++;
			}
		}

		searchMask = new CoreItem();
		searchMask.setFormName("CTM:Support Group Assignments");
		searchMask.setAttribute(ARDictionary.CTM_SGROUPID, fromGroup.getEntryId()); //Support Group ID
		groupAssignments = searchMask.search(getServerConnection());
		RuntimeLogger.info("Found " + groupAssignments.size() + " reference records");

		for (Object groupAssignment : groupAssignments)
		{
			CoreItem groupAssig = (CoreItem) groupAssignment;

			try
			{
				CoreItem newGroupAssig = new CoreItem();
				newGroupAssig.setFormName("CTM:Support Group Assignments");
				newGroupAssig.setAttribute(ARDictionary.CTM_SGROUPID, toGroup.getEntryId());
				newGroupAssig.setAttribute(1000000789, groupAssig.getAttributeValue(1000000789));
				newGroupAssig.read(getServerConnection());

				if(!newGroupAssig.exists())
				{
					newGroupAssig.create(getServerConnection());
					RuntimeLogger.debug("Group assignment '" + newGroupAssig.getEntryId() + "' (favorite group) has been created for the new support group");
					correct++;
				}
				else RuntimeLogger.debug("Group assignment '" + newGroupAssig.getEntryId() + "' already exists");

				//Remove Assignments
				if (!keepoldfavorites)
				{
					groupAssig.setAttribute(ARDictionary.CTM_Z1DACTION, "DELETE"); //z1D Action
					groupAssig.update(getServerConnection());
				}
			}
			catch (AREasyException are)
			{
				RuntimeLogger.error("Error migrating group assignment '" + groupAssig.getEntryId() + "' (favorite group) for the new support group: " + are.getMessage());
				logger.debug("Exception", are);
				errors++;
			}
		}

		RuntimeLogger.info("Support Group > Assignments - End of migration procedure: " + correct + " update(s) and " + errors + " error(s)");
	}

	protected void setGroupOnCallRecords(SupportGroup fromGroup, SupportGroup toGroup) throws AREasyException
	{
		RuntimeLogger.info("Support Group > OnCall Records - Start migration procedure..");
		boolean keepoldoncallrecords = getConfiguration().getBoolean("keepoldoncallrecords", true);

		CoreItem searchMask = new CoreItem();
		searchMask.setFormName("CTM:Support Group On-Call");
		searchMask.setAttribute(ARDictionary.CTM_SGROUPID, fromGroup.getEntryId()); //Support Group ID
		List onCallRecords = searchMask.search(getServerConnection());

		RuntimeLogger.info("Found " + onCallRecords.size() + " records");
		int correct = 0;
		int errors = 0;

		for (Object onCallRecordObj : onCallRecords)
		{
			CoreItem onCallRecord = (CoreItem) onCallRecordObj;

			try
			{
				CoreItem newOnCallRecord = onCallRecord.copyToNew();
				newOnCallRecord.setAttribute(ARDictionary.CTM_SGROUPID, toGroup.getEntryId());
				newOnCallRecord.read(getServerConnection());

				if(!newOnCallRecord.exists())
				{
					newOnCallRecord.create(getServerConnection());
					RuntimeLogger.debug("Group on-call record '" + newOnCallRecord.getEntryId() + "' has been created for the new support group");
					correct++;
				}
				else RuntimeLogger.debug("Group on-call record '" + newOnCallRecord.getEntryId() + "' already exists");

				if (!keepoldoncallrecords)
				{
					onCallRecord.setAttribute(ARDictionary.CTM_Z1DACTION, "DELETE"); //z1D Action
					onCallRecord.update(getServerConnection());
				}
			}
			catch (AREasyException are)
			{
				RuntimeLogger.error("Error migrating group on-call record '" + onCallRecord.getEntryId() + "' to the new support group: " + are.getMessage());
				logger.debug("Exception", are);
				errors++;
			}
		}

		RuntimeLogger.info("Support Group > OnCall Records - End of migration procedure: " + correct + " update(s) and " + errors + " error(s)");

		try
		{
			if(fromGroup.getOnCallGroup() != null && fromGroup.getOnCallGroup() == 0)
			{
				toGroup.setOnCallGroup(new Integer(0));
				toGroup.update(getServerConnection());
			}
		}
		catch(AREasyException are)
		{
			RuntimeLogger.error("Error setting group on-call flag: " + are.getMessage());
			logger.debug("Exception", are);
		}
	}

	protected void setGroupShifts(SupportGroup fromGroup, SupportGroup toGroup) throws AREasyException
	{
		RuntimeLogger.info("Support Group > Shifts - Start migration procedure..");
		boolean keepoldshifts = getConfiguration().getBoolean("keepoldshifts", true);

		CoreItem searchMask = new CoreItem();
		searchMask.setFormName("CTM:Support Group Shifts");
		searchMask.setAttribute(ARDictionary.CTM_SGROUPID, fromGroup.getEntryId()); //Support Group ID
		List groupShifts = searchMask.search(getServerConnection());

		RuntimeLogger.info("Found " + groupShifts.size() + " records");
		int correct = 0;
		int errors = 0;

		for (Object groupShiftObj : groupShifts)
		{
			CoreItem groupShift = (CoreItem) groupShiftObj;

			try
			{
				CoreItem newGroupShift = groupShift.copyToNew();
				newGroupShift.setAttribute(ARDictionary.CTM_SGROUPID, toGroup.getEntryId());
				newGroupShift.read(getServerConnection());

				if (!newGroupShift.exists())
				{
					newGroupShift.create(getServerConnection());
					RuntimeLogger.debug("Group shift '" + newGroupShift.getEntryId() + "' has been created for the new support group");
					correct++;
				}
				else RuntimeLogger.debug("Group shift '" + newGroupShift.getEntryId() + "' already exists");

				if (!keepoldshifts)
				{
					groupShift.setAttribute(ARDictionary.CTM_Z1DACTION, "DELETE"); //z1D Action
					groupShift.update(getServerConnection());
				}
			}
			catch (AREasyException are)
			{
				RuntimeLogger.error("Error migrating group shift '" + groupShift.getEntryId() + "' for the new support group: " + are.getMessage());
				logger.debug("Exception", are);
				errors++;
			}
		}

		searchMask = new CoreItem();
		searchMask.setFormName("CTM:Support Group Shift Assoc");
		searchMask.setAttribute(ARDictionary.CTM_SGROUPID, fromGroup.getEntryId()); //Support Group ID
		groupShifts = searchMask.search(getServerConnection());
		RuntimeLogger.info("Found additional " + groupShifts.size() + " records (shift associations)");

		for (Object groupShiftAssocObj : groupShifts)
		{
			CoreItem groupShiftAssoc = (CoreItem) groupShiftAssocObj;

			try
			{
				CoreItem newGroupShiftAssoc = groupShiftAssoc.copyToNew();
				newGroupShiftAssoc.setAttribute(ARDictionary.CTM_SGROUPID, toGroup.getEntryId());
				newGroupShiftAssoc.read(getServerConnection());

				if (!newGroupShiftAssoc.exists())
				{
					newGroupShiftAssoc.create(getServerConnection());
					RuntimeLogger.debug("Group shift association '" + newGroupShiftAssoc.getEntryId() + "' has been created for the new support group");
					correct++;
				}
				else RuntimeLogger.debug("Group shift association '" + newGroupShiftAssoc.getEntryId() + "' already exists");

				if (!keepoldshifts)
				{
					groupShiftAssoc.setAttribute(ARDictionary.CTM_Z1DACTION, "DELETE"); //z1D Action
					groupShiftAssoc.update(getServerConnection());
				}
			}
			catch (AREasyException are)
			{
				RuntimeLogger.error("Error transferring group shift association '" + groupShiftAssoc.getEntryId() + "' to the new support group: " + are.getMessage());
				logger.debug("Exception", are);
				errors++;
			}
		}

		RuntimeLogger.info("Support Group > Shifts - End of migration procedure: " + correct + " update(s) and " + errors + " error(s)");
	}

	protected void setGroupAutoAssignments(SupportGroup fromGroup, SupportGroup toGroup) throws AREasyException
	{
		RuntimeLogger.info("Support Group > AutoAssignment - Start migration procedure..");

		CoreItem searchMask = new CoreItem();
		searchMask.setFormName("CFG:Assignment");
		searchMask.setAttribute(ARDictionary.CTM_SGROUPID, fromGroup.getEntryId()); //Support Group ID
		List assignments = searchMask.search(getServerConnection());

		RuntimeLogger.info("Found " + assignments.size() + " records");
		int correct = 0;
		int errors = 0;

		for (Object assignObj : assignments)
		{
			CoreItem assign = (CoreItem) assignObj;

			try
			{
				assign.setAttribute(1000000251, toGroup.getCompanyName());
				assign.setAttribute(1000000014, toGroup.getOrganisationName());
				assign.setAttribute(1000000217, toGroup.getSupportGroupName());
				assign.setAttribute(ARDictionary.CTM_SGROUPID, toGroup.getEntryId());
				assign.merge(getServerConnection(), Constants.AR_MERGE_ENTRY_DUP_MERGE | Constants.AR_MERGE_NO_WORKFLOW_FIRED | Constants.AR_MERGE_NO_PATTERNS_INCREMENT);
				RuntimeLogger.debug("Group auto-assignment '" + assign.getEntryId() + "' has been migrated to the new support group");
				correct++;
			}
			catch (AREasyException are)
			{
				RuntimeLogger.error("Error migrating the auto-assignment '" + assign.getEntryId() + "' to the new support group: " + are.getMessage());
				logger.debug("Exception", are);
				errors++;
			}
		}

		RuntimeLogger.info("Support Group > AutoAssignment - End of migration procedure: " + correct + " update(s) and " + errors + " error(s)");
	}

	protected void setApprovalMappings(SupportGroup fromGroup, SupportGroup toGroup) throws AREasyException
	{
		RuntimeLogger.info("Support Group > ApprovalMapping - Start migration procedure..");

		CoreItem searchMask = new CoreItem();
		searchMask.setFormName("APR:Approver Lookup");
		searchMask.setAttribute(ARDictionary.CTM_SGROUPID, fromGroup.getEntryId()); //Support Group ID
		List approvals = searchMask.search(getServerConnection());

		RuntimeLogger.info("Found " + approvals.size() + " records");
		int correct = 0;
		int errors = 0;

		for (Object approvalObj : approvals)
		{
			CoreItem approval = (CoreItem) approvalObj;

			try
			{
				approval.setAttribute(ARDictionary.CTM_SGROUPID, toGroup.getEntryId());
				approval.merge(getServerConnection(), Constants.AR_MERGE_ENTRY_DUP_MERGE | Constants.AR_MERGE_NO_WORKFLOW_FIRED | Constants.AR_MERGE_NO_PATTERNS_INCREMENT);
				RuntimeLogger.debug("Group approval mapping '" + approval.getEntryId() + "' has been migrated to the new support group");
				correct++;
			}
			catch (AREasyException are)
			{
				RuntimeLogger.error("Error migrating the approval mapping '" + approval.getEntryId() + "' to the new support group: " + are.getMessage());
				logger.debug("Exception", are);
				errors++;
			}
		}

		RuntimeLogger.info("Support Group > ApprovalMapping - End of migration procedure: " + correct + " update(s) and " + errors + " error(s)");
	}

	protected void updateIncidentTemplates(SupportGroup fromGroup, SupportGroup toGroup) throws AREasyException
	{
		updateTemplates(fromGroup, toGroup, "HPD:Template", 1000000251, 302126600, 1000000217, 1000000079, "Incident", "Assignee Assignment");
		updateTemplates(fromGroup, toGroup, "HPD:Template", 1000000396, 1000003662, 1000003663, 0, "Incident", "Vendor Assignment");
		updateTemplates(fromGroup, toGroup, "HPD:Template", 1000001341, 1000001340, 1000001339, 1000000828, "Incident", "Authoring Assignment");
		updateTemplates(fromGroup, toGroup, "HPD:TemplateSPGAssoc", 1000000001, 1000000014, 1000000015, 1000000079, "IncidentAuthoring", "Support Group");

		RuntimeLogger.info("Incident Templates > Correlate Support Organisation Assignment - Start migration procedure..");

		CoreItem searchMask = new CoreItem();
		searchMask.setFormName("HPD:Template");

		List templates = searchMask.search(getServerConnection(), "'302126600' != '1000000014'");
		RuntimeLogger.info("Found " + templates.size() + " incident templates");

		int correct = 0;
		int errors = 0;

		for (Object tmplObject : templates)
		{
			CoreItem template = (CoreItem) tmplObject;

			try
			{
				template.setAttribute(1000000014, template.getAttributeValue(302126600));

				template.merge(getServerConnection(), Constants.AR_MERGE_ENTRY_DUP_MERGE | Constants.AR_MERGE_NO_WORKFLOW_FIRED | Constants.AR_MERGE_NO_PATTERNS_INCREMENT);
				RuntimeLogger.debug("Assignment of incident template '" + template.getEntryId() + "' has been correlated for the new support organisation");
				correct++;
			}
			catch (AREasyException are)
			{
				RuntimeLogger.error("Error correlating assignment of incident template '" + template.getEntryId() + "': " + are.getMessage());
				logger.debug("Exception", are);
				errors++;
			}
		}

		RuntimeLogger.info("Incident Templates > Correlate Support Organisation Assignment - End of migration procedure: " + correct + " update(s) and " + errors + " error(s)");
	}

	protected void updateIncidents(SupportGroup fromGroup, SupportGroup toGroup) throws AREasyException
	{
		updateTickets(fromGroup, toGroup, "HPD:Help Desk", "incidents", 5, 1000000251, 1000000014, 1000000217, 1000000079, "Incident", "Assignee Assignment");
		updateTickets(fromGroup, toGroup, "HPD:Help Desk", "incidents", 5, 1000000426, 1000000342, 1000000422, 1000000427, "Incident", "Owner Assignment");
		updateTickets(fromGroup, toGroup, "HPD:Help Desk", "incidents", 5, 1000000396, 1000003662, 1000003663, 1000003664, "Incident", "Vendor Assignment");
	}

	protected void updateProblemTemplates(SupportGroup fromGroup, SupportGroup toGroup) throws AREasyException
	{
		updateTemplates(fromGroup, toGroup, "PBM:Template", 1000000834, 1000000835, 1000000837, 0, "Problem", "Coordinator Assignment");
		updateTemplates(fromGroup, toGroup, "PBM:Template", 1000000251, 1000000014, 1000000217, 1000000079, "Problem", "Assignee Assignment");
		updateTemplates(fromGroup, toGroup, "PBM:Template", 1000001341, 1000001340, 1000001339, 1000000828, "Problem", "Authoring Assignment");
		updateTemplates(fromGroup, toGroup, "PBM:TemplateSPGAssoc", 1000000001, 1000000014, 1000000015, 1000000079, "ProblemAuthoring", "Support Group");
	}

	protected void updateProblems(SupportGroup fromGroup, SupportGroup toGroup) throws AREasyException
	{
		updateTickets(fromGroup, toGroup, "PBM:Problem Investigation", "problems", 8, 1000000251, 1000000014, 1000000217, 1000000079, "Problem", "Assignee Assignment");
		updateTickets(fromGroup, toGroup, "PBM:Problem Investigation", "problems", 8, 1000000834, 1000000835, 1000000837, 1000000427, "Problem", "Coordinator Assignment");
		updateTickets(fromGroup, toGroup, "PBM:Problem Investigation", "problems", 8, 1000000082, 1000001640, 1000001639, 1000001641, "Problem", "Requester Support Group");
	}

	protected void updateKnownErrors(SupportGroup fromGroup, SupportGroup toGroup) throws AREasyException
	{
		updateTickets(fromGroup, toGroup, "PBM:Known Error", "knownerrors", 5, 1000000251, 1000000014, 1000000217, 1000000079, "KnownError", "Assignee Assignment");
		updateTickets(fromGroup, toGroup, "PBM:Known Error", "knownerrors", 5, 1000000834, 1000000835, 1000000837, 1000000427, "KnownError", "Assignee Assignment");
	}

	protected void updateChangeTemplates(SupportGroup fromGroup, SupportGroup toGroup) throws AREasyException
	{
		updateTemplates(fromGroup, toGroup, "CHG:Template", 1000003228, 1000003227, 1000003229, 1000003234, "Change", "Coordinator Assignment");
		updateTemplates(fromGroup, toGroup, "CHG:Template", 1000000251, 1000000014, 1000000015, 1000000079, "Change", "Manager Assignment");
		updateTemplates(fromGroup, toGroup, "CHG:Template", 1000003254, 1000003255, 1000003256, 1000003259, "Change", "Implementer Assignment");
		updateTemplates(fromGroup, toGroup, "CHG:Template", 1000000396, 1000003662, 1000003663, 1000003664, "Change", "Vendor Assignment");
		updateTemplates(fromGroup, toGroup, "CHG:Template", 1000001341, 1000001340, 1000001339, 1000002500, "Change", "Authoring Assignment");
		updateTemplates(fromGroup, toGroup, "CHG:TemplateSPGAssoc", 1000000001, 1000000014, 1000000015, 1000000079, "ChangeAuthoring", "Support Group");
	}

	protected void updateChanges(SupportGroup fromGroup, SupportGroup toGroup) throws AREasyException
	{
		updateTickets(fromGroup, toGroup, "CHG:Infrastructure Change", "changes", 11, 1000003228, 1000003227, 1000003229, 1000003234, "Change", "Coordinator Assignment");
		updateTickets(fromGroup, toGroup, "CHG:Infrastructure Change", "changes", 11, 1000000251, 1000000014, 1000000015, 1000000079, "Change", "Manager Assignment");
		updateTickets(fromGroup, toGroup, "CHG:Infrastructure Change", "changes", 11, 1000000082, 1000000342, 1000000341, 1000000427, "Change", "Requester Support Group");
	}

	protected void updateTaskTemplates(SupportGroup fromGroup, SupportGroup toGroup) throws AREasyException
	{
		updateTemplates(fromGroup, toGroup, "TMS:TaskTemplate", 1000000251, 1000000014, 10002506, 10002505, "Task", "Assignee Assignment");
	}

	protected void updateTasks(SupportGroup fromGroup, SupportGroup toGroup) throws AREasyException
	{
		updateTickets(fromGroup, toGroup, "TMS:Task", "tasks", 6000, 1000000251, 1000000014, 10002506, 10002505, "Task", "Assignee Assignment");
		updateTickets(fromGroup, toGroup, "TMS:Task", "tasks", 6000, 1000000082, 1000000342, 1000000341, 1000000427, "Task", "Requester Support Group");
	}

	protected void updateWorkOrderTemplates(SupportGroup fromGroup, SupportGroup toGroup) throws AREasyException
	{
		updateTemplates(fromGroup, toGroup, "WOI:Template", 1000000251, 1000000014, 1000000015, 0, "WorkOrder", "Manager Assignment");
		updateTemplates(fromGroup, toGroup, "WOI:Template", 1000003228, 1000003227, 1000003229, 0, "WorkOrder", "Assignee Assignment");
	}

	protected void updateWorkOrders(SupportGroup fromGroup, SupportGroup toGroup) throws AREasyException
	{
		updateTickets(fromGroup, toGroup, "WOI:WorkOrder", "workorders", 5, 1000000251, 1000000014, 1000000015, 1000000079, "WorkOrder", "Manager Assignment");
		updateTickets(fromGroup, toGroup, "WOI:WorkOrder", "workorders", 5, 1000003228, 1000003227, 1000003229, 1000003234, "WorkOrder", "Assignee Assignment");
		updateTickets(fromGroup, toGroup, "WOI:WorkOrder", "workorders", 5, 1000000082, 1000000342, 1000000341, 1000000427, "WorkOrder", "Requester Support Group");
	}

	protected void updateKnowledgeRecords(SupportGroup fromGroup, SupportGroup toGroup) throws AREasyException
	{
		updateTickets(fromGroup, toGroup, "RKM:KnowledgeArticleManager", "knowledgerecords", 8, 302300585, 302300586, 302300512, 302300544, "KnowledgeRecord", "Assignee Assignment");
		updateTickets(fromGroup, toGroup, "RKM:KnowledgeArticleManager", "knowledgerecords", 8, 302300587, 302300584, 302300542, 302300543, "KnowledgeRecord", "Owner Assignment");
	}

	protected void updateAssetRelationships(SupportGroup fromGroup, SupportGroup toGroup) throws AREasyException
	{
		RuntimeLogger.info("Asset > Support Group Relationships - Start migration procedure..");

		CoreItem searchMask = new CoreItem();
		searchMask.setFormName("AST:AssetPeople");
		searchMask.setAttribute(260100006, fromGroup.getEntryId());
		searchMask.setAttribute(260100013, "Support Group");

		List relations = searchMask.search(getServerConnection());
		RuntimeLogger.info("Found " + relations.size() + " relationships");

		int correct = 0;
		int errors = 0;

		for (Object relationObj : relations)
		{
			CoreItem relation = (CoreItem) relationObj;

			try
			{
				//set permissions through field 112
				setDataAccess(relation);

				//support group update data
				relation.setAttribute(260100006, toGroup.getEntryId());
				relation.setAttribute(301104200, toGroup.getInstanceId());
				relation.setAttribute(260100003, toGroup.getCompanyName() + "->" + toGroup.getOrganisationName() + "->" + toGroup.getSupportGroupName());

				relation.merge(getServerConnection(), Constants.AR_MERGE_ENTRY_DUP_MERGE | Constants.AR_MERGE_NO_WORKFLOW_FIRED | Constants.AR_MERGE_NO_PATTERNS_INCREMENT);
				RuntimeLogger.debug("Asset relationship record '" + relation.getEntryId() + "' has been updated to take the new support group");
				correct++;

				try
				{
					CoreItem ci = new CoreItem("BMC.CORE:BMC_BaseElement");
					ci.setAttribute(400129200, relation.getStringAttributeValue(301104100));
					ci.read((getServerConnection()));

					if(ci.exists())
					{
						//set permissions through field 112 and 60513
						setDataAccess(ci);
						setDataAccess(ci, "60513");

						ci.merge(getServerConnection(), Constants.AR_MERGE_ENTRY_DUP_MERGE | Constants.AR_MERGE_NO_WORKFLOW_FIRED | Constants.AR_MERGE_NO_PATTERNS_INCREMENT);
						RuntimeLogger.debug("Related configuration item '" + ci.getEntryId() + "' has been updated to take the new support group");
					}
				}
				catch (AREasyException are)
				{
					RuntimeLogger.error("Error changing related configuration item '" + relation.getStringAttributeValue(301104100) + "': " + are.getMessage());
					logger.debug("Exception", are);
					errors++;
				}
			}
			catch (AREasyException are)
			{
				RuntimeLogger.error("Error changing asset relationship record '" + relation.getEntryId() + "': " + are.getMessage());
				logger.debug("Exception", are);
				errors++;
			}
		}

		RuntimeLogger.info("Asset > Support Group Relationships - End of migration procedure: " + correct + " update(s) and " + errors + " error(s)");
	}

	protected void updateTemplates(SupportGroup fromGroup, SupportGroup toGroup, String formName, long cName, long oName, long gName, long gId, String ticketName, String assignmentName) throws AREasyException
	{
		RuntimeLogger.info(ticketName + " Templates > " + assignmentName + " - Start migration procedure..");

		CoreItem searchMask = new CoreItem();
		searchMask.setFormName(formName);

		if(cName > 0 && oName > 0 && gName > 0)
		{
			searchMask.setAttribute(cName, fromGroup.getCompanyName());         //Assigned Company
			searchMask.setAttribute(oName, fromGroup.getOrganisationName());    //Assigned Organisation
			searchMask.setAttribute(gName, fromGroup.getSupportGroupName());    //Assigned Group
		}
		else if (gId > 0)
		{
			searchMask.setAttribute(gId, fromGroup.getEntryId());   		 	//Assigned Group ID
		}

		List templates = searchMask.search(getServerConnection());
		RuntimeLogger.info("Found " + templates.size() + " " + ticketName + " templates");

		int correct = 0;
		int errors = 0;

		for (Object tmplObject : templates)
		{
			CoreItem template = (CoreItem) tmplObject;

			try
			{
				//set permissions through field 112
				setDataAccess(template);

				//update support group data
				if(cName > 0) template.setAttribute(cName, toGroup.getCompanyName());            //Assigned Company
				if(oName > 0) template.setAttribute(oName, toGroup.getOrganisationName());       //Assigned Organisation
				if(gName > 0) template.setAttribute(gName, toGroup.getSupportGroupName());       //Assigned Group Name
				if(gId > 0) template.setAttribute(gId, toGroup.getEntryId());	 				 //Assigned Group Id

				template.merge(getServerConnection(), Constants.AR_MERGE_ENTRY_DUP_MERGE | Constants.AR_MERGE_NO_WORKFLOW_FIRED | Constants.AR_MERGE_NO_PATTERNS_INCREMENT);
				RuntimeLogger.debug(assignmentName + " of " + ticketName + " template '" + template.getEntryId() + "' has been migrated to the new support group");
				correct++;
			}
			catch (AREasyException are)
			{
				RuntimeLogger.error("Error migrating " + assignmentName + " of " + ticketName + " template '" + template.getEntryId() + "': " + are.getMessage());
				logger.debug("Exception", are);
				errors++;
			}
		}

		RuntimeLogger.info(ticketName + " Templates > " + assignmentName + " - End of migration procedure: " + correct + " update(s) and " + errors + " error(s)");
	}

	protected void updateTickets(SupportGroup fromGroup, SupportGroup toGroup, String formName, String optionName, int openStatus, long cName, long oName, long gName, long gId, String ticketName, String assignmentName) throws AREasyException
	{
		RuntimeLogger.info(ticketName + " Tickets > " + assignmentName + " - Start migration procedure..");

		Long TicketsBefore = getConfiguration().getLong(optionName + "before", null);
		Long TicketsAfter = getConfiguration().getLong(optionName + "after", null);
		boolean openTickets = getConfiguration().getBoolean("open" + optionName, false);

		CoreItem searchMask = new CoreItem();
		searchMask.setFormName(formName);

		if(cName > 0 && oName > 0 && gName > 0)
		{
			searchMask.setAttribute(cName, fromGroup.getCompanyName());         //Assigned Company
			searchMask.setAttribute(oName, fromGroup.getOrganisationName());    //Assigned Organisation
			searchMask.setAttribute(gName, fromGroup.getSupportGroupName());    //Assigned Group
		}
		else if (gId > 0)
		{
			searchMask.setAttribute(gId, fromGroup.getEntryId());   		 	//Assigned Group ID
		}

		if (openTickets) searchMask.setAttribute(7, "< "  + String.valueOf(openStatus));
		if (TicketsAfter != null) searchMask.setAttribute(3, "> " + TicketsAfter.longValue());
			else if (TicketsBefore != null) searchMask.setAttribute(3, "< " + TicketsBefore.longValue());

		List tickets = searchMask.search(getServerConnection());
		RuntimeLogger.info("Found " + tickets.size() + " " + ticketName + " tickets");

		int correct = 0;
		int errors = 0;

		for (Object ticketObject : tickets)
		{
			CoreItem ticket = (CoreItem) ticketObject;

			try
			{
				if(cName > 0) ticket.setAttribute(cName, toGroup.getCompanyName());            //Assigned Company
				if(oName > 0) ticket.setAttribute(oName, toGroup.getOrganisationName());       //Assigned Organisation
				if(gName > 0) ticket.setAttribute(gName, toGroup.getSupportGroupName());       //Assigned Group Name
				if(gId > 0) ticket.setAttribute(gId, toGroup.getEntryId());	 				   //Assigned Group Id

				//set permissions through field 112
				setDataAccess(ticket);
				setDataAccess(ticket, "60900");
				setDataAccess(ticket, "60903");

				ticket.merge(getServerConnection(), Constants.AR_MERGE_ENTRY_DUP_MERGE | Constants.AR_MERGE_NO_WORKFLOW_FIRED | Constants.AR_MERGE_NO_PATTERNS_INCREMENT);
				RuntimeLogger.debug(assignmentName + " of " + ticketName + " '" + ticket.getEntryId() + "' has been migrated to the new support group");
				correct++;
			}
			catch (AREasyException are)
			{
				RuntimeLogger.error("Error migrating " + assignmentName + " of " + ticketName + " '" + ticket.getEntryId() + "': " + are.getMessage());
				logger.debug("Exception", are);
				errors++;
			}
		}

		RuntimeLogger.info(ticketName + " Tickets > " + assignmentName + " - End of migration procedure: " + correct + " updates(s) and " + errors + " error(s)");
	}

	protected void setDataAccess(CoreItem entity)
	{
		setDataAccess(entity, null, "112");
	}

	protected void setDataAccess(CoreItem entity, String fieldId)
	{
		setDataAccess(entity, null, fieldId);
	}

	protected void setDataAccess(CoreItem entity, String mode, String fieldId)
	{
		if(mode == null) mode = getConfiguration().getString("dataaccess", "update");
		if(fieldId == null) fieldId = "112";

		if(StringUtility.equalsIgnoreCase(mode, "update"))
		{
			String fieldValue = entity.getStringAttributeValue(fieldId);
			if(fieldValue != null && StringUtility.contains(fieldValue, getOldPermissionId()))
			{
				entity.setAttribute(fieldId, StringUtility.replace(fieldValue, getOldPermissionId(), getNewPermissionId()));
			}
		}
		else if(StringUtility.equalsIgnoreCase(mode, "append"))
		{
			String fieldValue = entity.getStringAttributeValue(fieldId);
			if(fieldValue != null && !StringUtility.contains(fieldValue, getNewPermissionId()))
			{
				if(fieldValue.endsWith(";")) entity.setAttribute(fieldId, fieldValue + getNewPermissionId() + ";");
					else entity.setAttribute(fieldId, fieldValue + ";" + getNewPermissionId());
			}
		}
	}

	protected String getOldPermissionId()
	{
		return this.oldPermId;
	}

	protected String getNewPermissionId()
	{
		return this.newPermId;
	}

	protected SupportGroup getOldSupportGroup()
	{
		return this.oldSG;
	}

	protected SupportGroup getNewSupportGroup()
	{
		return this.newSG;
	}

	protected String getSupportGroupString(SupportGroup group)
	{
		return group.getCompanyName() + ">" + group.getOrganisationName() + ">" + group.getSupportGroupName();
	}

	public boolean isSkipped()
	{
		return skip;
	}
}
