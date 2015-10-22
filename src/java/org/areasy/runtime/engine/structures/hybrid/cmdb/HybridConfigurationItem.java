package org.areasy.runtime.engine.structures.hybrid.cmdb;

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

import org.areasy.common.data.NumberUtility;
import org.areasy.common.data.StringUtility;
import org.areasy.common.support.configuration.Configuration;
import org.areasy.runtime.actions.PatternAction;
import org.areasy.runtime.engine.RuntimeLogger;
import org.areasy.runtime.engine.base.ARDictionary;
import org.areasy.runtime.engine.base.AREasyException;
import org.areasy.runtime.engine.base.ServerConnection;
import org.areasy.runtime.engine.structures.CoreItem;
import org.areasy.runtime.engine.structures.data.cmdb.ConfigurationItem;
import org.areasy.runtime.engine.structures.data.cmdb.units.AssetPeople;
import org.areasy.runtime.engine.structures.data.cmdb.units.ImpactedArea;
import org.areasy.runtime.engine.structures.data.cmdb.units.Relationship;
import org.areasy.runtime.engine.structures.data.cmdb.units.WorkLog;
import org.areasy.runtime.engine.structures.data.itsm.foundation.*;
import org.areasy.runtime.engine.structures.hybrid.AbstractHybrid;
import org.areasy.runtime.engine.structures.hybrid.HybridCoreItem;

import java.util.*;


public class HybridConfigurationItem extends ConfigurationItem implements AbstractHybrid
{
	private ServerConnection sourceConnection = null;
	private Configuration sourceConfig = null;

	public HybridConfigurationItem()
	{
		super();
	}

	/**
	 * Create a new HybridSupportGroup structure.
	 *
	 * @return new People instance
	 */
	public HybridConfigurationItem getInstance()
	{
		return new HybridConfigurationItem();
	}

	public void relate(ServerConnection arsession, Configuration config) throws AREasyException
	{
		if(exists())
		{
			if(config.getBoolean("workinfo", true))
			{
				//read worklog and load it
				CoreItem searchWorklog = new WorkLog();
				searchWorklog.setAttribute(301218800, getReconciliationId());
				List<CoreItem> worklogs = searchWorklog.search(arsession);

				for(int i = 0; worklogs != null && i < worklogs.size(); i++)
				{
					CoreItem item = worklogs.get(i);

					addPartWithPrefix("workinfo", item);
					addPartKeyPairWithPrefix("workinfo", item, 301218800, 400129200);
					addPartKeyPairWithPrefix("workinfo", item, 200000020, 200000020);
					addPartKeyPairWithPrefix("workinfo", item, 1000001563, 210000000);
				}
			}

			if(config.getBoolean("cirelation", true))
			{
				//read ci relationships
				Relationship searchRelationships = new Relationship();
				List cirelations = searchRelationships.searchAllRelationships(arsession, getInstanceId());

				for(int i = 0; cirelations != null && i < cirelations.size(); i++)
				{
					Relationship item = (Relationship) cirelations.get(i);

					ConfigurationItem relSource = item.getSourceConfigurationItem();
					ConfigurationItem relDestination = item.getDestinationConfigurationItem();

					if(relSource.exists() && relDestination.exists())
					{
						if(StringUtility.equals(relSource.getInstanceId(), getInstanceId()) )
						{
							addPartWithPrefix("cirelation", item);

							addPartKeyPairWithPrefix("cirelation", item, 490008100, 400079600);
							addPartKeyPairWithPrefix("cirelation", item, 400128800, 400127400);
							addPartKeyPairWithPrefix("cirelation", item, 490008000, 179);
							addPartKeyPairWithPrefix("cirelation", item, 400130900, 400129200);
						}
						else if(StringUtility.equals(relDestination.getInstanceId(), getInstanceId()) )
						{
							addPartWithPrefix("cirelation", item);

							addPartKeyPairWithPrefix("cirelation", item, 490009100, 400079600);
							addPartKeyPairWithPrefix("cirelation", item, 400128900, 400127400);
							addPartKeyPairWithPrefix("cirelation", item, 490009000, 179);
							addPartKeyPairWithPrefix("cirelation", item, 400131000, 400129200);
						}
						else RuntimeLogger.debug("Relationship skipped because the members could not be found: " + item);
					}
					else RuntimeLogger.debug("Relationship skipped because source or destination could not be identified: " + item);
				}
			}

			if(config.getBoolean("peoplerelation", true))
			{
				//read people relationships
				AssetPeople searchPeoplerelations = new AssetPeople();
				List peoplerelations = searchPeoplerelations.searchAllRelationships(arsession, getReconciliationId());

				for(int i = 0; peoplerelations != null && i < peoplerelations.size(); i++)
				{
					AssetPeople item = (AssetPeople)peoplerelations.get(i);

					if((item.getPeopleEntity() != null || item.getSupportGroupEntity() != null || item.getSystemGroup() != null) && item.getSourceConfigurationItem() != null && item.getSourceConfigurationItem().exists())
					{
						addPartWithPrefix("peoplerelation", item);

						addPartKeyPairWithPrefix("peoplerelation", item, 301104100, 400129200);
						addPartKeyPairWithPrefix("peoplerelation", item, 260100008, 210000000);
						addPartKeyPairWithPrefix("peoplerelation", item, 400079600, 400079600);
					}
				}
			}

			if(config.getBoolean("impactedarea", true))
			{
				//read people relationships
				ImpactedArea seaechImpactedareas = new ImpactedArea();
				List impactedareas = seaechImpactedareas.searchAllRelationships(arsession, getInstanceId());

				for(int i = 0; impactedareas != null && i < impactedareas.size(); i++)
				{
					ImpactedArea item = (ImpactedArea)impactedareas.get(i);

					if(item.getSourceConfigurationItem() != null && item.getSourceConfigurationItem().exists())
					{
						addPartWithPrefix("impactedarea", item);
						addPartKeyPairWithPrefix("impactedarea", item, 400129200, 400129200);
						addPartKeyPairWithPrefix("impactedarea", item, 301218800, 179);
						addPartKeyPairWithPrefix("impactedarea", item, 1000000148, 210000000);
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
	 * @param config runtime configuration or more specifically, multipart configuration
	 */
	public void transfer(CoreItem target, Map<Integer,Object> map, ServerConnection arsession, Configuration config) throws AREasyException
	{
		HybridCoreItem.setTarget(target, this, map, arsession);

		//set-up all related sub-structures
		if(target instanceof HybridConfigurationItem)
		{
			HybridConfigurationItem entry = (HybridConfigurationItem)target;
			entry.setMultiPart(this);

			//store configuration
			entry.sourceConfig = config;
			entry.sourceConnection = arsession;
		}
	}

	/**
	 * Create an entry record in the ARS server using an attribute's collection and then is read it
	 * to trasform it into a valid core item instance.
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
			//prepare environment
			prepare(arsession, getSourceConfig());

			//make sync between parts and base structure
			synch();

			Iterator iterator = getPartCodes();

			while(iterator != null && iterator.hasNext())
			{
				String code = (String) iterator.next();
				CoreItem output = getPartInstance(code);

				//get the part prefix
				if(code.indexOf("-") > 0) code = code.substring(0, code.indexOf("-", 0));
				int partMergeType = PatternAction.getMergeTypeAndOptions(this.getSourceConfig().subset(code));
				if(partMergeType == 0) partMergeType = nMergeType;

				CoreItem item = null;

				try
				{
					//take necessary conversion
					item = convert(arsession, output, code);

					//merge converted record
					if(item != null)
					{
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
	 * Commit part item instances based on specified attributes.
	 *
	 * @param arsession user session
	 * @throws AREasyException if any error will occur
	 */
	protected void commit(ServerConnection arsession) throws AREasyException
	{
		//prepare environment
		prepare(arsession, getSourceConfig());

		if(exists())
		{
			//make sync between parts and base structure
			synch();

			Iterator iterator = getPartCodes();

			while(iterator != null && iterator.hasNext())
			{
				String code = (String) iterator.next();
				CoreItem output = getPartInstance(code);

				//get code prefix
				if(code.indexOf("-") > 0) code = code.substring(0, code.indexOf("-", 0));

				CoreItem item = null;

				try
				{
					//take necessary conversion
					item = convert(arsession, output, code);

					if(item != null)
					{
						//delete unknown fields
						item.attrFixedToData(arsession);

						//read output
						item.readByNonCoreFloating(arsession);

						if(item.exists())
						{
							item.update(arsession);
						}
						else
						{
							item.setChanged();
							item.create(arsession);
						}
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
	 * Prepare part transaction
	 *
	 * @param arsession user session
	 * @param sourceConfig session configuration
	 * @throws AREasyException if any error will occur
	 */
	protected void prepare(ServerConnection arsession, Configuration sourceConfig) throws AREasyException
	{
		//nothing to do here
	}

	/**
	 * perform data conversion if is necessary.
	 *
	 * @param arsession user session
	 * @param output <code>CoreItem</code> structure
	 * @param code part type
	 * @return converted <code>CoreItem</code> structure.
	 * @throws AREasyException if any error will occur
	 */
	protected CoreItem convert(ServerConnection arsession, CoreItem output, String code) throws AREasyException
	{
		if(StringUtility.equals(code, "peoplerelation")) output = convertPeopleRelationship(arsession, output);

		return output;
	}

	protected CoreItem convertPeopleRelationship(ServerConnection arsession, CoreItem item) throws AREasyException
	{
		AssetPeople relation = (AssetPeople)item;

		String loginId = relation.getStringAttributeValue(260100002);
		String originalRole = relation.getStringAttributeValue(260100005);

		if(loginId != null)
		{
			loginId = loginId.toLowerCase();
			People people = new People();
			people.setLoginId(loginId);
			people.read(arsession);

			if(people.exists())
			{
				//set attributes
				relation.setAttribute(260100003, people.getFullName());
				relation.setAttribute(260100006, people.getEntryId());
				relation.setAttribute(301104200, people.getInstanceId());

				//validate if the relationship is "used by" to exist also the Person CI. Be sure that Sandbox is configured in "inline" mode.
				if(NumberUtility.toInt(originalRole) == 6000) setPersonReconciledCI(arsession, people);

				return relation;
			}
			else
			{
				RuntimeLogger.warn("Login Id '" + loginId + "' could not be found for People Relationship: " + relation + ". " + this);
				return null;
			}
		}
		else
		{
			if(getSourceConnection() != null)
			{
				People remotePeople = new People();
				remotePeople.setAttribute(1, relation.getAttributeValue(260100006));
				remotePeople.read(getSourceConnection());

				if(remotePeople.exists())
				{
					String remoteLoginId = remotePeople.getLoginId();
					String remoteCorporateId = remotePeople.getCorporateId();

					People people = new People();

					//find by login
					if(remoteLoginId != null)
					{
						remoteLoginId = remoteLoginId.toLowerCase();
						people.setLoginId(remoteLoginId);
						people.read(arsession);
					}

					//find by corporate id
					if(!people.exists() && remoteCorporateId != null)
					{
						people.clear();
						remoteCorporateId = remoteCorporateId.toUpperCase();
						people.setCorporateId(remoteCorporateId);
						people.read(arsession);
					}

					if(people.exists())
					{
						//set attributes
						relation.setAttribute(260100003, people.getFullName());
						relation.setAttribute(260100006, people.getEntryId());
						relation.setAttribute(301104200, people.getInstanceId());

						//validate if the relationship is "used by" to exist also the Person CI. Be sure that Sandbox is configured in "inline" mode.
						if(NumberUtility.toInt(originalRole) == 6000) setPersonReconciledCI(arsession, people);

						return relation;
					}
					else
					{
						RuntimeLogger.debug("Remote person could not be identified on the target server: " + people + ". It will be created for: " + this);
						people.setData(remotePeople.getData());
						people.deleteAttribute(1);
						people.deleteAttribute(3);
						people.deleteAttribute(5);
						people.deleteAttribute(6);
						people.setIgnoreUnchangedValues(false);

						Site site = new Site();
						site.setSiteName(people.getSiteName());
						site.read(arsession);

						if(site.exists())
						{
							people.setAttribute(1000000074, site.getEntryId());
						}
						else
						{
							site.setAttribute(1000000002, people.getStringAttributeValue(1000000002));
							site.setAttribute(1000000004, people.getStringAttributeValue(1000000004));

							site.create(arsession);
							site.setCompanyAssociation(arsession, people.getStringAttributeValue(1000000001), people.getStringAttributeValue(200000012), people.getStringAttributeValue(200000007), 0);
							RuntimeLogger.debug("Create new Site record: " + site);

							people.setAttribute(1000000074, site.getEntryId());
						}

						if(StringUtility.isNotEmpty(people.getStringAttributeValue(300469300)))
						{
							CostCenter cc = new CostCenter();
							cc.setCostCenterCode(people.getStringAttributeValue(300469300));
							cc.setCostCenterName(people.getStringAttributeValue(300469200));
							cc.setCompanyName(people.getStringAttributeValue(1000000001));

							cc.read(arsession);

							if(!cc.exists())
							{
								cc.setAttribute(7, new Integer(1));
								cc.create(arsession);

								RuntimeLogger.debug("Create new CostCenter record: " + cc);
							}
							else if(cc.exists() && (Integer)cc.getAttributeValue(7) != 1)
							{
								cc.setAttribute(7, new Integer(1));
								cc.update(arsession);
							}
						}

						people.create(arsession);
						RuntimeLogger.debug("Create new People record: " + people);

						if(people.exists())
						{
							//set attributes
							relation.setAttribute(260100003, people.getFullName());
							relation.setAttribute(260100006, people.getEntryId());
							relation.setAttribute(301104200, people.getInstanceId());

							//validate if the relationship is "used by" to exist also the Person CI. Be sure that Sandbox is configured in "inline" mode.
							if(NumberUtility.toInt(originalRole) == 6000) setPersonReconciledCI(arsession, people);

							return relation;
						}
						else
						{
							RuntimeLogger.debug("Remote person could not be identified on the target server: " + people + ". Can not be created for: " + this);
							return null;
						}
					}
				}
				else
				{
					RuntimeLogger.warn("Remote person could not be found: " + remotePeople + ". " + this);
					return null;
				}
			}
			else
			{
				RuntimeLogger.warn("Remote connection is null and can not be initiated." + this);
				return null;
			}
		}
	}

	protected void setPersonReconciledCI(ServerConnection arsession, People people) throws AREasyException
	{
		ConfigurationItem person = new ConfigurationItem();
		person.setClassId("BMC_PERSON");
		person.setAttribute(400129200, people.getStringAttributeValue(400129200));
		person.read(arsession);

		if(!person.exists())
		{
			arsession.getContext().setClientType(9);
			people.setAttribute(1000000076, "PEOPLESYNC_UPDATE");

			people.update(arsession);
			arsession.getContext().setClientType(0);

			boolean fixed = false;
			int x = 0;

			while(!fixed)
			{
				try
				{
					Thread.sleep(500);
				}
				catch(Exception e) { /** nothing to do */ }

				person.clear();
				person.setClassId("BMC_PERSON");
				person.setAttribute(400129200, people.getStringAttributeValue(400129200));
				person.read(arsession);

				x++;
				fixed = person.exists() || x > 5;
			}
		}
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
			}
		}

		//supplier name validation
		Company supplier = getSupplierCompany();
		if(supplier != null)
		{
			supplier.read(arsession);

			if(!supplier.exists())
			{
				supplier.setCompanyTypes("Supplier");
				supplier.create(arsession);
			}
		}

		//manufacturer name validation
		Company manufacturer = getManufacturerCompany();
		if(manufacturer != null)
		{
			manufacturer.read(arsession);

			if(!manufacturer.exists())
			{
				manufacturer.setCompanyTypes("Manufacturer");
				manufacturer.create(arsession);
			}
		}

		//product catalog validation
		ProductCategory cti = getProductCategory();
		if(cti != null)
		{
			List list = cti.search(arsession);

			if(list == null || list.size() == 0)
			{
				cti.setAssetFlag();
				cti.setClassAssociation(getClassId());
				cti.setAttribute(ARDictionary.PCT_STATUS, new Integer(1));
				cti.setAttribute(ARDictionary.PCT_ORIGIN, new Integer(1));
				cti.setAttribute(ARDictionary.PCT_SUITEDEF, new Integer(0));
				cti.setAttribute(ARDictionary.PCT_COMPANYNAME, getStringAttributeValue(ARDictionary.CI_COMPANYNAME));

				cti.create(arsession);
			}
		}

		//site validation
		Region region = getLocationRegion();
		if(region != null)
		{
			region.read(arsession);

			if(!region.exists()) region.create(arsession);
		}

		//site validation
		Site site = getLocationSite();
		if(site != null)
		{
			site.read(arsession);

			String city = "Unknown";
			String companyName = getStringAttributeValue(CI_COMPANYNAME);
			String regionName = getStringAttributeValue(CI_REGIONNAME);
			String siteGroup = getStringAttributeValue(200000007);

			if(!site.exists())
			{
				int index1 = site.getSiteName().indexOf("-", 0);
				int index2 = site.getSiteName().lastIndexOf(" ");

				if(site.getSiteName().startsWith(region +"-") && index1 > 0 && index2 > index1) city = site.getSiteName().substring(index1 + 1, index2);

				site.setCity(city);
				site.setCountry( getCountryByCode(arsession, regionName) );

				site.create(arsession);
			}

			//site-company association
			site.setCompanyAssociation(arsession, companyName, regionName, siteGroup, null);
		}
	}

	private Company getOwnerCompany()
	{
		if(getStringAttributeValue(CI_COMPANYNAME) != null)
		{
			Company company = new Company();
			company.setCompanyName(getStringAttributeValue(CI_COMPANYNAME));

			return company;
		}
		else return null;
	}

	private Company getSupplierCompany()
	{
		if(getStringAttributeValue(CI_SUPPLIER) != null)
		{
			Company company = new Company();
			company.setCompanyName(getStringAttributeValue(CI_SUPPLIER));

			return company;
		}
		else return null;
	}

	private Company getManufacturerCompany()
	{
		if(getStringAttributeValue(CI_MANUFACTURER) != null)
		{
			Company company = new Company();
			company.setCompanyName(getStringAttributeValue(CI_MANUFACTURER));

			return company;
		}
		else return null;
	}

	private Region getLocationRegion()
	{
		if(getStringAttributeValue(CI_REGIONNAME) != null)
		{
			Region region = new Region();
			region.setRegionName(getStringAttributeValue(CI_REGIONNAME));
			region.setCompanyName(getStringAttributeValue(CI_COMPANYNAME));

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
		if(getStringAttributeValue(CI_SITENAME) != null)
		{
			Site site = new Site();
			site.setSiteName(getStringAttributeValue(CI_SITENAME));

			return site;
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

	public ServerConnection getSourceConnection()
	{
		return sourceConnection;
	}

	public Configuration getSourceConfig()
	{
		return sourceConfig;
	}

	protected static boolean isDataEmpty(String[] data)
	{
		if(data == null || data.length == 0) return true;
		else
		{
			boolean empty = true;

			for (int i = 0; empty && i < data.length; i++)
			{
				empty = StringUtility.isEmpty(data[i]);
			}

			return empty;
		}
	}

	public String toString()
	{
		return "Hybrid " + super.toString();
	}
}