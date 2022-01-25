package org.areasy.runtime.engine.workflows;

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

import org.areasy.common.data.NumberUtility;
import org.areasy.common.data.StringUtility;
import org.areasy.common.logger.Logger;
import org.areasy.common.logger.LoggerFactory;
import org.areasy.runtime.engine.RuntimeLogger;
import org.areasy.runtime.engine.base.AREasyException;
import org.areasy.runtime.engine.base.ServerConnection;
import org.areasy.runtime.engine.structures.CoreItem;
import org.areasy.runtime.engine.structures.data.cmdb.ConfigurationItem;
import org.areasy.runtime.engine.structures.data.cmdb.InventoryLocation;
import org.areasy.runtime.engine.structures.data.itsm.foundation.Organisation;
import org.areasy.runtime.engine.structures.data.itsm.foundation.People;
import org.areasy.runtime.engine.structures.data.itsm.foundation.SupportGroup;

import java.util.List;
import java.util.Map;

/**
 * Workflow processors: direct methods to perform different actions in CMDB application.
 */
public class ProcessorLevel2CmdbApp extends ProcessorLevel1Context
{
	private static Logger logger = LoggerFactory.getLog(ProcessorLevel2CmdbApp.class);

	/**
	 * Generate an application GUID
 	 * @param arsession ARS server connection structure
	 * @return a GUID string value
	 * @throws AREasyException @throws AREasyException if any error will occur
	 */
	public static String getStringInstanceId(ServerConnection arsession) throws AREasyException
	{
		return getStringInstanceId(arsession, null);
	}

	/**
	 * Generate an applicatuin GUID
 	 * @param arsession ARS server connection structure
	 * @param prefix guid prefix (it is necessary, otherwise could be null)
	 * @return a GUID string value
	 * @throws AREasyException @throws AREasyException if any error will occur
	 */
	public static String getStringInstanceId(ServerConnection arsession, String prefix) throws AREasyException
	{
		boolean impersonated = false;
		String output = null;

		try
		{
			//prepare user connection
			if(arsession.isImpersonated())
			{
				impersonated = true;
				arsession.getContext().impersonateUser(null);
			}

			output = arsession.getContext().executeProcess("Application-Generate-GUID" + (prefix != null ? " " + prefix : ""), true).getOutput();

			//set back end-user connection
			if(impersonated) arsession.getContext().impersonateUser(arsession.getUserName());
		}
		catch(Throwable th)
		{
			throw new AREasyException(th);
		}

		return output;
	}

	/**
	 * Create a relationship between two CIs.
	 *
	 * @param arsession ARS server connection structure and session
	 * @param primary configuration item which will register the requested relationship
	 * @param type type of relationship. Here you have to specify the "ProperName" value of the relationship class.
	 * @param isparent specify if the specified CI is parent in this relation.
	 * @param secondary configuration item which will be used to define this relationship.
	 * @return true if the relationship is created
	 * @throws AREasyException if any error will occur
	 */
	public static boolean setConfigurationItemRelationship(ServerConnection arsession, ConfigurationItem primary, ConfigurationItem secondary, String type, boolean isparent) throws AREasyException
	{
		return setConfigurationItemRelationship(arsession, primary, secondary, null, type, isparent);
	}

	/**
	 * Create a relationship between two CIs.
	 *
	 * @param arsession ARS server connection structure and session
	 * @param primary configuration item which will register the requested relationship
	 * @param relationClass relationClass of relationship. Here you have to specify the "ProperName" value of the relationship class.
	 * @param impact specify relationship impact. Usually could take values like "Source-Destination" or "Destination-Source" Also,
	 * you can use the following abbreviations: SD, DS.
	 * @param isparent specify if the specified CI is parent in this relation.
	 * @param secondary configuration item which will be used to define this relationship.
	 * @return true if the relationship is created or updated
	 * @throws AREasyException if any error will occur
	 */
	public static boolean setConfigurationItemRelationship(ServerConnection arsession, ConfigurationItem primary, ConfigurationItem secondary, String impact, String relationClass, boolean isparent) throws AREasyException
	{
		String relationClassName = null;
		String relationClassForm = null;
		String relationClassId = null;

		CoreItem cmdbclass = null;

		try
		{
			cmdbclass = new CoreItem(FORM_SHRSCHEMANAMES);
			cmdbclass.read(arsession, "'260000000' = \"" + relationClass + "\" OR '490001100' = \"" + relationClass + "\"");

			if(cmdbclass.exists())
			{
				relationClassId = cmdbclass.getStringAttributeValue(230000009);
				relationClassForm = cmdbclass.getStringAttributeValue(301170700);
				relationClassName = cmdbclass.getStringAttributeValue(260000000);
			}
		}
		catch(Throwable th)
		{
			logger.debug("Error reading class definition from Asset Management dictionary: " + th.getMessage());
		}

		if(cmdbclass != null && !cmdbclass.exists())
		{
			cmdbclass = new CoreItem(FORM_OBJSTRCLASS);
			cmdbclass.read(arsession, "'490001100' = \"" + relationClass + "\"");

			if(cmdbclass.exists())
			{
				relationClassId = cmdbclass.getStringAttributeValue(179);
				relationClassForm = cmdbclass.getStringAttributeValue(400130800);
				relationClassName = cmdbclass.getStringAttributeValue(490021100);

				if(StringUtility.isEmpty(relationClassName)) relationClassName = relationClassId;
			}
		}

		if(relationClassId == null) throw new AREasyException("Invalid relationship class: " + relationClass);

		//1.validate source CI.
		if(primary == null || !primary.exists()) throw new AREasyException("Primary relationship CI doesn't exist: " + primary);

		//2.validate target CI.
		if(secondary == null || !secondary.exists()) throw new AREasyException("Secondary relationship CI doesn't exist: " + secondary);

		String relationSourceClassId;
		String relationSourceInstanceId;
		String relationSourceReconciliationId;

		String relationDestinationClassId;
		String relationDestinationInstanceId;
		String relationDestinationReconciliationId;

		if(isparent)
		{
			relationSourceClassId = secondary.getClassId();
			relationSourceInstanceId = secondary.getInstanceId();
			relationSourceReconciliationId = secondary.getReconciliationId();

			relationDestinationClassId = primary.getClassId();
			relationDestinationInstanceId = primary.getInstanceId();
			relationDestinationReconciliationId = primary.getReconciliationId();
		}
		else
		{
			relationSourceClassId = primary.getClassId();
			relationSourceInstanceId = primary.getInstanceId();
			relationSourceReconciliationId = primary.getReconciliationId();

			relationDestinationClassId = secondary.getClassId();
			relationDestinationInstanceId = secondary.getInstanceId();
			relationDestinationReconciliationId = secondary.getReconciliationId();
		}

		CoreItem item = new CoreItem();
		item.setFormName(relationClassForm);
		item.setAttribute(CRL_SRC_CLSID, relationSourceClassId);
		item.setAttribute(CRL_SRC_DATASETID, primary.getDatasetId());
		item.setAttribute(CRL_SRC_INSTANCEID, relationSourceInstanceId);
		item.setAttribute(CRL_SRC_RECONCILIATIONID, relationSourceReconciliationId);
		item.setAttribute(CRL_DEST_CLSID, relationDestinationClassId);
		item.setAttribute(CRL_DEST_DATASETID, secondary.getDatasetId());
		item.setAttribute(CRL_DEST_INSTANCEID, relationDestinationInstanceId);
		item.setAttribute(CRL_DEST_RECONCILIATIONID, relationDestinationReconciliationId);

		List items = item.search(arsession);
		if(items != null && !items.isEmpty())
		{
			if(items.size() == 1)
			{
				item = (CoreItem) items.get(0);

				if(impact != null)
				{
					item.setAttribute(CRL_HASIMPACT, "Yes");
					item.setAttribute(CRL_IMPACTDIR, impact);

					RuntimeLogger.debug("CI relationship already exists but it will be updated to set the impact details: " + impact);
					item.setIgnoreUnchangedValues(true);
					item.update(arsession);
					return true;
				}
				else if(item.getAttributeValue(CRL_HASIMPACT) != null && 10 == (Integer)item.getAttributeValue(CRL_HASIMPACT))
				{
					item.setAttribute(CRL_HASIMPACT, "No");
					item.setNullAttribute(CRL_IMPACTDIR);

					RuntimeLogger.debug("CI relationship already exists but it will be updated to make null the impact details");
					item.setIgnoreUnchangedValues(true);
					item.update(arsession);
					return true;
				}
				else
				{
					RuntimeLogger.warn("CI relationship already exists: " + item);
					return false;
				}
			}
			else
			{
				RuntimeLogger.warn("Multiple CI relationships found the specified qualification criteria");
				return false;
			}
		}
		else
		{
			item.clear();
			item.setAttribute(CRL_DATASETID, primary.getDatasetId());
			item.setAttribute(CRL_TYPE, relationClassId);
			item.setAttribute(CRL_SRC_CLSID, relationSourceClassId);
			item.setAttribute(CRL_SRC_DATASETID, primary.getDatasetId());
			item.setAttribute(CRL_SRC_INSTANCEID, relationSourceInstanceId);
			item.setAttribute(CRL_SRC_RECONCILIATIONID, relationSourceReconciliationId);
			item.setAttribute(CRL_DEST_CLSID, relationDestinationClassId);
			item.setAttribute(CRL_DEST_DATASETID, secondary.getDatasetId());
			item.setAttribute(CRL_DEST_INSTANCEID, relationDestinationInstanceId);
			item.setAttribute(CRL_DEST_RECONCILIATIONID, relationDestinationReconciliationId);
			item.setAttribute(CRL_NAME, relationClassName);

			if(impact != null)
			{
				item.setAttribute(CRL_HASIMPACT, "Yes");
				item.setAttribute(CRL_IMPACTDIR, impact);
			}

			item.setIgnoreUnchangedValues(true);
			item.setIgnoreNullValues(true);

			item.create(arsession);

			return true;
		}
	}

	/**
	 * Remove a relationship between two CIs.
	 *
	 * @param arsession ARS server connection structure and session
	 * @param primary configuration item which will register the requested relationship
	 * @param secondary configuration item which will be used to define this relationship.
	 * @return true if the relationship(s) is/are removed
	 * @throws AREasyException if any error will occur
	 */
	public static boolean removeConfigurationItemRelationship(ServerConnection arsession, ConfigurationItem primary, ConfigurationItem secondary) throws AREasyException
	{
		//1.validate source CI.
		if(primary== null || !primary.exists()) throw new AREasyException("Primary CI entry doesn't exist: " + primary);

		//2.validate target CI.
		if(secondary == null || !secondary.exists()) throw new AREasyException("Secondary CI entry doesn't exist: " + secondary);

		CoreItem entry = new CoreItem();
		entry.setFormName(FORM_BASERELATIONSHIP);

		entry.setAttribute(490008100, primary.getClassId());
		entry.setAttribute(400128800, primary.getDatasetId());
		entry.setAttribute(490008000, primary.getInstanceId());
		entry.setAttribute(490009100, secondary.getClassId());
		entry.setAttribute(400128900, secondary.getDatasetId());
		entry.setAttribute(490009000, secondary.getInstanceId());

		//set transactional flags.
		entry.read(arsession);
		entry.remove(arsession);

		return true;
	}

	/**
	 * Set people relationships for an existent CI.
	 *
	 * @param arsession ARS server connection structure
	 * @param item configuration item structure
	 * @param map additional fields mapping to complete the relationship.
	 * @param role relation role name
	 * @param entry core item entity which is related with the specified configuration item
	 * @return true if the relationship is created
	 * @throws AREasyException if any error will occur
	 */
	public static boolean setPeopleRelationship(ServerConnection arsession, ConfigurationItem item, CoreItem entry, Map map, String role) throws AREasyException
	{
		String entryId;
		String entryName;
		String entryInstanceId;
		String entryEntityName;
		int entryEntityCode = 0;

		//asset validation
		if(item == null || !item.exists()) throw new AREasyException("Source CI entry doesn't exist: " + item);

		//target validation
		if(entry == null|| !entry.exists())
		{
			logger.warn("Target entry (people, support group or people organisation) was not found: " + entry);
			return false;
		}

		if(entry instanceof People)
		{
			People people = (People)entry;

			entryId = people.getEntryId();
			entryName = people.getFirstName() + " " + people.getLastName();
			entryInstanceId = people.getInstanceId();

			entryEntityName = CONST_PEOPLE_RELATIONENTITIES[0];
			entryEntityCode = 1800;
		}
		else if(entry instanceof SupportGroup)
		{
		    SupportGroup group = (SupportGroup)entry;

			entryId = group.getEntryId();
			entryName = group.getCompanyName() + "->" + group.getOrganisationName() + "->" + group.getSupportGroupName();
			entryInstanceId = group.getInstanceId();

			entryEntityName = CONST_PEOPLE_RELATIONENTITIES[1];
			entryEntityCode = 2500;
		}
		else if(entry instanceof Organisation)
		{
			Organisation organisation = (Organisation)entry;
			entryId = organisation.getEntryId();
			if (StringUtility.equalsIgnoreCase((String)map.get("relationshiplevel"), "department")) entryName = organisation.getCompanyName() + "->" + organisation.getOrganisationName() + "->" + organisation.getDepartmentName();
				else if (StringUtility.equalsIgnoreCase((String)map.get("relationshiplevel"), "organisation")) entryName = organisation.getCompanyName() + "->" + organisation.getOrganisationName();
					else if (!map.containsKey("relationshiplevel") || StringUtility.equalsIgnoreCase((String)map.get("relationshiplevel"), "company")) entryName = organisation.getCompanyName();
						else throw new AREasyException("Unknown the relationship level");
			map.remove("relationshiplevel");

			entryInstanceId = organisation.getInstanceId();
			entryEntityName = CONST_PEOPLE_RELATIONENTITIES[2];
			entryEntityCode = 1900;
		}
		else throw new AREasyException("Target entity is not recognized: " + entry);

		//get role id.
		int roleId = 5;

		if(!NumberUtility.isNumber(role))
		{
			for(int i = 0; i < CONST_PEOPLE_ROLENAMES.length; i++)
			{
				if(StringUtility.equalsIgnoreCase(role, CONST_PEOPLE_ROLENAMES[i]))
				{
					if(i < CONST_PEOPLE_ROLEIDS.length) roleId = CONST_PEOPLE_ROLEIDS[i];
				}
			}
		}
		else roleId = NumberUtility.toInt(role);

		//define relation structure
		CoreItem astpeople = new CoreItem();
		astpeople.setFormName(FORM_ASSETPEOPLE);
		astpeople.setAttribute(ASP_ROLENAME, new Integer(roleId));
		astpeople.setAttribute(ASP_ENTITYNAME, entryEntityName);
		astpeople.setAttribute(ASP_PEOPLEID, entryId);
		astpeople.setAttribute(ASP_PEOPLE_INSTANCEID, entryInstanceId);
		astpeople.setAttribute(ASP_AINSTANCE, item.getReconciliationId());

		//set transactional flags.
		astpeople.setIgnoreUnchangedValues(true);
		astpeople.setIgnoreNullValues(true);

		//check if already exist
		astpeople.read(arsession);

		if(astpeople.exists())
		{
			RuntimeLogger.warn("People relationship already exist: " + entry + " -> " + item);
			return false;
		}
		else
		{
			//fill additional attributes
			astpeople.setAttribute(ASP_ASSETID, item.getName());
			astpeople.setAttribute(ASP_STATUS, new Integer(0));
			astpeople.setAttribute(ASP_ROLENAME, new Integer(roleId));
			astpeople.setAttribute(ASP_PEOPLE_FULLNAME, entryName);
			astpeople.setAttribute(ASP_DATASET, item.getDatasetId());
			astpeople.setAttribute(ASP_AENTRYID, item.getEntryId());
			astpeople.setAttribute(ASP_CLASSID, item.getClassId());
			astpeople.setAttribute(ASP_REQUESTTYPE, new Integer(entryEntityCode));
			astpeople.setAttribute(ASP_INDIVIDUALORGROUP, entryEntityName);

			//add additional attributes.
			astpeople.setData(map);

			//create relationship
			astpeople.create(arsession);

			return true;
		}
	}

	/**
	 * Remove people relationships for a specific CI.
	 *
	 * @param arsession user session and server connection.
	 * @param item configuration item structure
	 * @param map additional fields mapping to complete the relationship.
	 * @param role remote entity role in this relation
	 * @return true if the relationship(s) is/are removed
	 * @throws AREasyException if any error will occur
	 */
	public static boolean removePeopleRelationships(ServerConnection arsession, ConfigurationItem item, Map map, String role) throws AREasyException
	{
		//asset validation
		if(item == null || !item.exists()) throw new AREasyException("CI doesn't exist: " + item);

		//get role id.
		int roleId = -1;

		if(!NumberUtility.isNumber(role))
		{
			for(int i = 0; i < CONST_PEOPLE_ROLENAMES.length; i++)
			{
				if(StringUtility.equalsIgnoreCase(role, CONST_PEOPLE_ROLENAMES[i]))
				{
					if(i < CONST_PEOPLE_ROLEIDS.length) roleId = CONST_PEOPLE_ROLEIDS[i];
				}
			}
		}
		else roleId = NumberUtility.toInt(role);

		//define relation structure
		CoreItem astpeople = new CoreItem();
		astpeople.setFormName(FORM_ASSETPEOPLE);
		astpeople.setAttribute(ASP_ASSETID, item.getAssetId());
		astpeople.setAttribute(ASP_AINSTANCE, item.getReconciliationId());
		if(roleId >= 0) astpeople.setAttribute(ASP_ROLENAME, new Integer(roleId));

		//set transactional flags.
		astpeople.setIgnoreUnchangedValues(true);
		astpeople.setIgnoreNullValues(true);

		//check if already exist
		List list = astpeople.search(arsession);

		if(list == null || list.isEmpty())
		{
			RuntimeLogger.warn("There is no people relationship: " + item);
			return false;
		}
		else
		{
			for(int i = 0; i < list.size(); i++)
			{
				CoreItem entry = (CoreItem) list.get(i);
				entry.setFormName(FORM_ASSETPEOPLE);
				entry.setAttribute(ASP_ACTION, "DELETE");
				astpeople.setAttribute(ASP_STATUS, new Integer(1));

				//set transactional flags.
				entry.setIgnoreUnchangedValues(true);
				entry.setIgnoreNullValues(true);

				//add additional attributes.
				entry.setData(map);

				entry.update(arsession);
			}

			return true;
		}
	}

	/**
	 * Register specified asset (CI) into a dedicated location. After this action (and if it is executed with success) the asset will have
	 * "In Inventory" status and the status could be changed until the asset will become reserved. This operation isn't use simulation flag so, it is
	 * a real action and the result could be an error or an asset in reserve state. The inventory location considered the actual
	 * <code>Site</code> attribute value.
	 *
	 * @param arsession ARS server connection structure
	 * @param item configuration item structure (asset in CMDB)
	 * @return true if the relationship is created or updated
	 * @throws AREasyException if any error will occur
	 */
	public static boolean setInventoryLocation(ServerConnection arsession, ConfigurationItem item) throws AREasyException
	{
		return setInventoryLocation(arsession, item, (String)null);
	}

	/**
	 * Register specified asset (CI) into a dedicated location. After this action (and if it is executed with success) the asset will have
	 * "In Inventory" status and the status could be changed until the asset will become reserved.
	 *
	 * @param arsession ARS server connection structure
	 * @param item configuration item structure (asset in CMDB)
	 * @param location the name of inventory location. If is null the name will be considered the actual <code>Site</code> attribute value.
	 * @return true if the relationship is created or updated
	 * @throws AREasyException if any error will occur
	 */
	public static boolean setInventoryLocation(ServerConnection arsession, ConfigurationItem item, String location) throws AREasyException
	{
		InventoryLocation inventory = new InventoryLocation();

		//check location.
		if(StringUtility.isEmpty(location)) location = item.getStringAttributeValue(CI_SITENAME);
		if(StringUtility.isEmpty(location)) throw new AREasyException("Location attribute is null and can not be discovered");

		inventory.setLocation(location);
		inventory.read(arsession);

		return setInventoryLocation(arsession, item, inventory);
	}

	/**
	 * Register specified asset (CI) into a dedicated location. After this action (and if it is executed with success) the asset will have
	 * "In Inventory" status and the status could be changed until the asset will become reserved.
	 *
	 * @param arsession ARS server connection structure
	 * @param item configuration item structure (asset in CMDB)
	 * @param inventory inventory location configuration item structure (which must be defined)
	 * @return true if the relationship is created or updated
	 * @throws AREasyException if any error will occur
	 */
	public static boolean setInventoryLocation(ServerConnection arsession, ConfigurationItem item, InventoryLocation inventory) throws AREasyException
	{
		if(inventory == null) throw new AREasyException("Inventory location entity is null");
		if(item == null) throw new AREasyException("CI entity is null");

		//1.get source CI.
		if(!inventory.exists()) throw new AREasyException("Inventory location doesn't exist: " + inventory);

		//1.get source CI.
		if(!item.exists())
		{
			try
			{
				//set reconciliation id and instance id.
				String reid = getStringInstanceId(arsession, "RE");
				item.setAttribute(String.valueOf(CI_RECONCILIATIONID), reid);
			}
			catch(Throwable th)
			{
				throw new AREasyException(th);
			}
		}
		else
		{
			String status = item.getStatus();

			if((StringUtility.equalsIgnoreCase("9", status) || StringUtility.equalsIgnoreCase("in inventory", status)) && item.getAttribute(7).isNotChanged())
			{
				CoreItem location = new CoreItem();

				location.setFormName(FORM_ASSETINVENTORYQTY);
				location.setAttribute(ASP_INVQ_INSTANCEID, item.getInstanceId());
				location.setAttribute(ASP_INVQ_CLASSID, item.getClassId());

				location.read(arsession);

				if(location.exists())
				{
					String existInstanceId = location.getStringAttributeValue(301149000);

					if(StringUtility.equals(existInstanceId, inventory.getInstanceId()))
					{
						RuntimeLogger.warn("CI is already registered in the inventory location: " + inventory + ", " + item);
						return false;
					}
					else throw new AREasyException("CI is already registered in other inventory location (" + existInstanceId + "):" + item);
				}
				else
				{
					RuntimeLogger.warn("The CI status is 'In Inventory' but the inventory relationship couldn't be found: " + item + ". The workflow will try to repair CI structure");
				}

			}

			item.setAttribute(CI_INVFLAG, "No");
		}

		//commit relationship
		item.setAttribute(CI_INVKEYWORD, inventory.getClassId());
		item.setAttribute(CI_INVRECONCILIATIONID, inventory.getReconciliationId());
		item.setAttribute(CI_INVINSTANCEID, inventory.getInstanceId());
		item.setAttribute(CI_INVNAME, "ELEMENTLOCATION");
		item.setAttribute(CI_STATUS, "9");

		item.setIgnoreUnchangedValues(true);

		if(!item.exists()) item.create(arsession);
			else item.update(arsession);

		return true;
	}

	/**
	 * Reserve or get out an asset from the inventory location.
	 *
	 * @param arsession ARS server connection structure
	 * @param item configuration item structure (CI)
	 * @return true if the relationship is removed
	 * @throws AREasyException if any error will occur
	 */
	public static boolean unsetLocation(ServerConnection arsession, ConfigurationItem item) throws AREasyException
	{
		//1.validate target CI.
		if(item == null || !item.exists()) throw new AREasyException("CI entry doesn't exist: " + item);

		CoreItem location = new CoreItem();

		location.setFormName(FORM_ASSETINVENTORYQTY);
		location.setAttribute(ASP_INVQ_INSTANCEID, item.getReconciliationId());
		location.setAttribute(ASP_INVQ_CLASSID, item.getClassId());

		location.setIgnoreNullValues(true);
		location.setIgnoreUnchangedValues(true);
		location.read(arsession);

		if(location.exists())
		{
			location.setAttribute(ASP_INVQ_TRACTION, new Integer(1));
			location.setAttribute(ASP_INVQ_TRQUANTIY, new Integer(1));

			location.update(arsession);
			item.readByInstanceId(arsession);

			return true;
		}
		else throw new AREasyException("Inventory location CI doesn't exist: " + location);
	}
}
