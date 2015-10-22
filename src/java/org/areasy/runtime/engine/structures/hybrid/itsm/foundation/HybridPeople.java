package org.areasy.runtime.engine.structures.hybrid.itsm.foundation;

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

import org.areasy.common.data.StringUtility;
import org.areasy.common.support.configuration.Configuration;
import org.areasy.runtime.actions.PatternAction;
import org.areasy.runtime.engine.RuntimeLogger;
import org.areasy.runtime.engine.base.AREasyException;
import org.areasy.runtime.engine.base.ServerConnection;
import org.areasy.runtime.engine.structures.CoreItem;
import org.areasy.runtime.engine.structures.data.itsm.foundation.*;
import org.areasy.runtime.engine.structures.hybrid.AbstractHybrid;
import org.areasy.runtime.engine.structures.hybrid.HybridCoreItem;

import java.util.*;

/**
 * This is a data structure who manages <Code>People</code> entities that includes all related sub-structures,
 * and each transaction of the main entity will handle also the parts.
 */
public class HybridPeople extends People implements AbstractHybrid
{
	private Configuration config = null;

	public HybridPeople()
	{
		super();
	}

	/**
	 * Create a new HybridPeople structure.
	 *
	 * @return new People instance
	 */
	public HybridPeople getInstance()
	{
		return new HybridPeople();
	}

	/**
	 * Method to generate related data structures.
	 *
	 * @param arsession source connection instance
	 * @param config runtime configuration or more specifically, multipart configuration
	 * @throws AREasyException if any error will occur
	 */
	public void relate(ServerConnection arsession, Configuration config) throws AREasyException
	{
		//read secondary structures.
		if(exists())
		{
			if(config.getBoolean("user", true))
			{
				CoreItem user = new CoreItem();
				user.setFormName("User");
				user.setAttribute(101, getStringAttributeValue(4));
				user.read(arsession);

				if(user.exists())
				{
					addPart("user", user);
					addPartKeyPair("user", 101, 4);
					addPartKeyPair("user", 109, 109);
				}
			}

			if(config.getBoolean("workinfo", true))
			{
				//read worklog
				CoreItem searchWorklog = new CoreItem();
				searchWorklog.setFormName("CTM:People WorkLog");
				searchWorklog.setAttribute(1000000080, getEntryId());
				List<CoreItem> worklogs = searchWorklog.search(arsession);

				for(int i = 0; worklogs != null && i < worklogs.size(); i++)
				{
					CoreItem item = worklogs.get(i);

					addPartWithPrefix("workinfo", item);
					addPartKeyPairWithPrefix("workinfo", item, 1000000080, 1);
				}
			}

			if(config.getBoolean("permission", true))
			{
				//read person permissions
				CoreItem searchPerm = new CoreItem();
				searchPerm.setFormName("CTM:People Permission Groups");
				searchPerm.setAttribute(1000000080, getEntryId());
				List<CoreItem> permissions = searchPerm.search(arsession);

				for(int i = 0; permissions != null && i < permissions.size(); i++)
				{
					CoreItem item = permissions.get(i);

					addPartWithPrefix("permission", item);
					addPartKeyPairWithPrefix("permission", item, 1000000080, 1);
					addPartKeyPairWithPrefix("permission", item, 4, 4);
				}
			}

			if(config.getBoolean("assignedgroup", true))
			{
				//read assigned groups
				CoreItem searchAssGrp = new CoreItem();
				searchAssGrp.setFormName("CTM:Support Group Association");
				searchAssGrp.setAttribute(1000000080, getEntryId());
				List<CoreItem> assignedGroups = searchAssGrp.search(arsession);

				for(int i = 0; assignedGroups != null && i < assignedGroups.size(); i++)
				{
					CoreItem item = assignedGroups.get(i);

					SupportGroup sgroup = new SupportGroup();
					sgroup.setAttribute(1, item.getAttributeValue(1000000079));
					sgroup.read(arsession);

					if(sgroup.exists() && sgroup.getStatusId() <= 1)
					{
						addPartWithPrefix("assignedgroup", item);
						addPartKeyPairWithPrefix("assignedgroup", item, 1000000080, 1);
						addPartKeyPairWithPrefix("assignedgroup", item, 4, 4);
						addPartKeyPairWithPrefix("assignedgroup", item, 1000000017, 1000000017);
					}
					else
					{
						RuntimeLogger.warn("Source group could not be found for assignment: " +item.getAttributeValue(1000000079));
					}
				}
			}

			if(config.getBoolean("functionalrole", true))
			{
				//read functional roles
				CoreItem searchFuncRoles = new CoreItem();
				searchFuncRoles.setFormName("CTM:SupportGroupFunctionalRole");
				searchFuncRoles.setAttribute(1000000080, getEntryId());
				List<CoreItem> functionalRoles = searchFuncRoles.search(arsession);

				for(int i = 0; functionalRoles != null && i < functionalRoles.size(); i++)
				{
					CoreItem item = functionalRoles.get(i);

					SupportGroup sgroup = new SupportGroup();
					sgroup.setAttribute(1, item.getAttributeValue(1000000079));
					sgroup.read(arsession);

					if(sgroup.exists() && sgroup.getStatusId() <= 1)
					{
						addPartWithPrefix("functionalrole", item);
						addPartKeyPairWithPrefix("functionalrole", item, 1000000080, 1);
						addPartKeyPairWithPrefix("functionalrole", item, 4, 4);
						addPartKeyPairWithPrefix("functionalrole", item, 1000000017, 1000000017);
						addPartKeyPairWithPrefix("functionalrole", item, 1000000346, 1000000346);
					}
					else
					{
						RuntimeLogger.warn("Source group could not be found for functional roles: " +item.getAttributeValue(1000000079));
					}
				}
			}
		}
	}

	/**
	 * Method to generate target data structure.
	 *
	 * @param target structure to be populated
	 * @param map data mapping between source and target
	 * @param arsession source connection instance
	 */
	public void transfer(CoreItem target, Map<Integer,Object> map, ServerConnection arsession, Configuration config) throws AREasyException
	{
		HybridCoreItem.setTarget(target, this, map, arsession);

		//set-up all related sub-structures
		if(target instanceof HybridPeople)
		{
			HybridPeople people = (HybridPeople)target;
			people.setMultiPart(this);

			//store configuration
			people.config = config;
		}
	}

	/**
	 * Commit part item instances based on specified attributes.
	 *
	 * @param arsession user session
	 * @throws AREasyException if any error will occur
	 */
	protected void commit(ServerConnection arsession) throws AREasyException
	{
		if(exists())
		{
			//make sync between parts and base structure
			synch();

			Iterator iterator = getPartInstances();

			while(iterator != null && iterator.hasNext())
			{
				CoreItem item = (CoreItem) iterator.next();

				try
				{
					item.readByNonCoreFloating(arsession);

					if(item.exists())
					{
						item.update(arsession);
					}
					else
					{
						item.resetEntryId();
						if(item.getAttribute(1) != null) item.deleteAttribute(1);

						item.setChanged();
						item.create(arsession);
					}
				}
				catch(Throwable th)
				{
					String message = "Error during execution of part commit action: " + th.getMessage();
					message += ". Part " + item;
					message += ". " + this;

					RuntimeLogger.warn(message);
					logger.debug("Exception", th);
				}
			}
		}
	}

	/**
	 * Create an entry record in the ARS server using an attribute's collection and then is read it
	 * to transform it into a valid core item instance.
	 *
	 * @param arsession user session
	 * @param collection attribute's collection
	 * @throws AREasyException if any error will occur
	 */
	protected void create(ServerConnection arsession, Collection collection) throws AREasyException
	{
		//validate components parts
		validate(arsession);

		//get fields collection (again)
		if(ignoreUnchangedValues()) collection = getChangedAttributes();
		else collection = getAttributes();

		super.create(arsession, collection);

		//commit part items
		commit(arsession);
	}

	/**
	 * Update the current core item instance based on specified attributes.
	 *
	 * @param arsession user session
	 * @param collection attribute's collection
	 * @throws AREasyException if any error will occur
	 */
	protected void update(ServerConnection arsession, Collection collection) throws AREasyException
	{
		//validate components parts
		validate(arsession);

		//get fields collection (again)
		if(ignoreUnchangedValues()) collection = getChangedAttributes();
		else collection = getAttributes();

		super.update(arsession, collection);

		//commit part items
		commit(arsession);
	}

	/**
	 * Merge an entry record in the ARS server using an attribute's collection and then is read it
	 * to transform it into a valid core item instance.
	 *
	 * @param arsession user session
	 * @param collection attribute's collection
	 * @throws AREasyException if any error will occur
	 * @param nMergeType merge type for conflicts
	 */
	protected void merge(ServerConnection arsession, Collection collection, int nMergeType, List mergeQualList) throws AREasyException
	{
		//validate components parts
		validate(arsession);

		if(mergeQualList != null)
		{
			String baseIdentifier = "base@";
			List mergeBaseQualList = new Vector();

			for(int i = 0; i < mergeQualList.size(); i++)
			{
				String fid = (String) mergeQualList.get(i);
				if(StringUtility.isNotEmpty(fid) && (!fid.contains("@") || fid.startsWith(baseIdentifier)))
				{
					if(fid.startsWith(baseIdentifier)) mergeBaseQualList.add( fid.substring(baseIdentifier.length()) );
					else mergeBaseQualList.add(fid);
				}
			}

			super.merge(arsession, collection, nMergeType, mergeBaseQualList);
		}
		else super.merge(arsession, collection, nMergeType);

		if(exists())
		{
			//make sync between parts and base structure
			synch();

			Iterator iterator = getPartCodes();

			while(iterator != null && iterator.hasNext())
			{
				String code = (String) iterator.next();
				CoreItem item = getPartInstance(code);

				try
				{
					if(code.indexOf("-") > 0) code = code.substring(0, code.indexOf("-", 0));
					int partMergeType = PatternAction.getMergeTypeAndOptions(this.config.subset(code));
					if(partMergeType == 0) partMergeType = nMergeType;

					if(mergeQualList != null)
					{
						String partIdentifier = code + "@";
						List mergePartQualList = new Vector();

						for(int i = 0; i < mergeQualList.size(); i++)
						{
							String fid = (String) mergeQualList.get(i);

							if(StringUtility.isNotEmpty(fid) && fid.startsWith(partIdentifier))
							{
								mergePartQualList.add( fid.substring(partIdentifier.length()) );
							}
						}

						item.merge(arsession, nMergeType, mergePartQualList);
					}
					else item.merge(arsession, partMergeType);
				}
				catch(Throwable th)
				{
					String message = "Error during execution of part merge action: " + th.getMessage();
					message += ". Part " + item;
					message += ". " + this;

					RuntimeLogger.warn(message);
					logger.debug("Exception", th);
				}
			}
		}
	}

	/**
	 * Get string representation of the current core item structure.
	 *
	 * @return string data model.
	 */
	public String toString()
	{
		return "Hybrid " + super.toString();
	}

	/**
	 * This method will validate all related sub-structures related to a CI.
	 *
	 * @param arsession user session
	 * @throws AREasyException if any error will occur
	 */
	protected void validate(ServerConnection arsession) throws AREasyException
	{
		//company name validation
		Company customer = getOwnerCompany();

		if(customer != null)
		{
			customer.read(arsession);

			if(!customer.exists())
			{
				customer.setCompanyTypes("Customer");
				customer.create(arsession);

				RuntimeLogger.debug("Customer Company has been created: " + customer);
			}
		}

		Organisation organisation = getOrganisationDepartment();

		if(organisation != null)
		{
			organisation.read(arsession);

			if(!organisation.exists())
			{
				organisation.create(arsession);

				RuntimeLogger.debug("Company Organisation has been created: " + organisation);
			}
		}

		//site validation
		Region region = getLocationRegion();

		if(region != null)
		{
			region.read(arsession);

			if(!region.exists())
			{
				region.create(arsession);

				RuntimeLogger.debug("Region entity has been created: " + region);
			}
		}

		//site validation
		Site site = getLocationSite();

		if(site != null)
		{
			site.read(arsession);

			String city = null;
			String country = null;
			String state = null;
			String zip = null;
			String street = null;

			String companyName = getStringAttributeValue(CI_COMPANYNAME);
			String regionName = getStringAttributeValue(CI_REGIONNAME);
			String siteGroup = getStringAttributeValue(200000007);

			if(!site.exists())
			{
				city = getStringAttributeValue(1000000004);
				country = getStringAttributeValue(1000000002);
				state = getStringAttributeValue(1000000003);
				zip = getStringAttributeValue(1000000039);
				street = getStringAttributeValue(1000000037);

				site.setCity(city);
				site.setCountry(country);
				if(state != null) site.setAttribute(1000000003, state);
				if(zip != null) site.setAttribute(1000000039, zip);
				if(street != null) site.setAttribute(1000000037, street);

				site.create(arsession);
				RuntimeLogger.debug("Site entity has been created: " + site);
			}

			//apply site id
			if(site.exists())
			{
				//site-company association
				CoreItem assoc = new CoreItem();
				assoc.setFormName("SIT:Site Company Association");
				assoc.setAttribute(1000000074, site.getEntryId());
				assoc.setAttribute(1000000001, companyName);

				assoc.read(arsession);

				if(!assoc.exists())
				{
					if(regionName != null) assoc.setAttribute(200000012, regionName);
					if(siteGroup != null) assoc.setAttribute(200000007, siteGroup);

					assoc.create(arsession);
					RuntimeLogger.debug("Site/Company association has been created: " + assoc);
				}

				setAttribute(CTM_SITEID, site.getEntryId());
				setAttribute(1000000002, site.getStringAttributeValue(1000000002));
				setAttribute(1000000004, site.getStringAttributeValue(1000000004));
				RuntimeLogger.debug("Set Site details in People profile: Site ID = " + site.getEntryId() +
						", Country = " + site.getStringAttributeValue(1000000002) +
						", City = " + site.getStringAttributeValue(1000000004));
			}
		}

		//cost center validation
		CostCenter cost = getCostCenter();

		if(cost != null)
		{
			cost.read(arsession);

			if(!cost.exists())
			{
				cost.setCostCenterName(getStringAttributeValue(300469200));
				cost.create(arsession);

				RuntimeLogger.debug("CostCenter entity has been created: " + cost);
			}

			//apply cost center id
			if(cost.exists())
			{
				setAttribute(300495800, cost.getStringAttributeValue(179));
			}
		}
	}

	private Company getOwnerCompany()
	{
		if(getStringAttributeValue(CTM_COMPANYNAME) != null)
		{
			Company company = new Company();
			company.setCompanyName(getStringAttributeValue(CTM_COMPANYNAME));

			return company;
		}
		else return null;
	}

	private Organisation getOrganisationDepartment()
	{
		if(getStringAttributeValue(CTM_PPLORGANISATION) != null)
		{
			Organisation organisation = new Organisation();

			organisation.setCompanyName(getStringAttributeValue(CTM_COMPANYNAME));
			organisation.setOrganisationName(getStringAttributeValue(CTM_PPLORGANISATION));
			organisation.setDepartmentName(getStringAttributeValue(CTM_DEPARTMENTNAME));

			return organisation;
		}
		else return null;
	}

	private Region getLocationRegion()
	{
		if(getStringAttributeValue(CTM_REGIONNAME) != null)
		{
			Region region = new Region();
			region.setRegionName(getStringAttributeValue(CTM_REGIONNAME));
			region.setCompanyName(getStringAttributeValue(CTM_COMPANYNAME));

			return region;
		}
		else return null;
	}

	/**
	 * This method return a <code>Site</code> instance but it is not verified if is registered or not.
	 * @return <code>Site</code> instance prepared for search or for commit.
	 */
	private Site getLocationSite()
	{
		if(getStringAttributeValue(CTM_SITENAME) != null)
		{
			Site site = new Site();
			site.setSiteName(getStringAttributeValue(CTM_SITENAME));

			return site;
		}
		else return null;
	}

	private CostCenter getCostCenter()
	{
		if(getStringAttributeValue(CTM_COSTCENTER) != null)
		{
			CostCenter cost = new CostCenter();
			cost.setCostCenterCode(getStringAttributeValue(CTM_COSTCENTER));
			cost.setCompanyName(getStringAttributeValue(CTM_COMPANYNAME));

			return cost;
		}
		else return null;
	}

	private String getCountryByCode(ServerConnection arsession, String code) throws AREasyException
	{
		CoreItem item = new CoreItem();
		item.setFormName("CFG:Geography Country");
		item.setAttribute(1000000695, code);

		item.read(arsession);

		if(!item.exists()) return "Unknown";
		else return item.getStringAttributeValue(1000000002);
	}
}