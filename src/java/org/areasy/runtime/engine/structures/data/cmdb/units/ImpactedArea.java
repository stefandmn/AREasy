package org.areasy.runtime.engine.structures.data.cmdb.units;

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

import com.bmc.arsys.api.Entry;
import org.areasy.runtime.engine.base.AREasyException;
import org.areasy.runtime.engine.base.ServerConnection;
import org.areasy.runtime.engine.structures.CoreItem;
import org.areasy.runtime.engine.structures.data.cmdb.ConfigurationItem;
import org.areasy.runtime.engine.structures.data.itsm.foundation.Company;
import org.areasy.runtime.engine.structures.data.itsm.foundation.Organisation;
import org.areasy.runtime.engine.structures.data.itsm.foundation.Region;
import org.areasy.runtime.engine.structures.data.itsm.foundation.Site;

import java.util.Collection;
import java.util.List;

/**
 * This class will handle the CI details about impacted areas.
 */
public class ImpactedArea extends CoreItem
{
	private Company company = null;
	private Organisation organisation = null;
	private Region region = null;
	private Site site = null;

	private ConfigurationItem sourceConfigurationItem = null;

	public ImpactedArea()
	{
		super();
		setFormName("AST:Impacted Areas");
	}

	/**
	 * Create a new instance of core item structure.
	 *
	 * @return new instance of <code>CoreItem</code> structure
	 */
	public CoreItem getInstance()
	{
		return new ImpactedArea();
	}

	public List searchAllRelationships(ServerConnection arsession, String instanceId) throws AREasyException
	{
		String qualification = "'301218800' = \"" + instanceId + "\" AND '7' <= 1";

		return search(arsession, qualification);
	}

	protected void fetch(ServerConnection arsession, Entry entry) throws AREasyException
	{
		super.fetch(arsession, entry);

		String cirelid = getStringAttributeValue(400129200);

		//discover source CI
		sourceConfigurationItem = new ConfigurationItem();
		sourceConfigurationItem.setAttribute(400129200, cirelid);
		sourceConfigurationItem.read(arsession);

		company = getOwnerCompany();
		organisation = getOrganisationDepartment();
		region = getLocationRegion();
		site = getLocationSite();
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
		commit(arsession);

		super.create(arsession, collection);
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
		commit(arsession);

		super.update(arsession, collection);
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
    protected void merge(ServerConnection arsession, Collection collection, int nMergeType) throws AREasyException
	{
		commit(arsession);

		super.merge(arsession, collection, nMergeType);
	}

	/**
	 * This method will validate all related sub-structures related to a CI.
	 *
	 * @param arsession user session
	 * @throws AREasyException if any error will occur
	 */
	protected void commit(ServerConnection arsession) throws AREasyException
	{
		//company name validation
		if(company != null)
		{
			company.read(arsession);

			if(!company.exists())
			{
				company.setCompanyTypes("Customer");
				company.create(arsession);
			}
		}

		//organisation validation
		if(organisation != null)
		{
			organisation.read(arsession);
			if(!organisation.exists())
			{
				if(organisation.getAttributeValue(1000000001) == null) organisation.setAttribute(1000000001, company.getCompanyName());
				if(organisation.getAttributeValue(200000006) == null) organisation.setAttribute(200000006, "n/a");

				organisation.create(arsession);
			}
		}

		//site validation
		if(region != null)
		{
			region.read(arsession);
			if(!region.exists()) region.create(arsession);
		}

		//site validation
		if(site != null)
		{
			site.read(arsession);

			String city = "Unknown";
			String companyName = getStringAttributeValue(1000000001);
			String regionName = getStringAttributeValue(200000012);
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

	public ConfigurationItem getSourceConfigurationItem()
	{
		return sourceConfigurationItem;
	}

	private Company getOwnerCompany()
	{
		if(getStringAttributeValue(1000000001) != null)
		{
			Company company = new Company();
			company.setCompanyName(getStringAttributeValue(1000000001));

			return company;
		}
		else return null;
	}

	private Organisation getOrganisationDepartment()
	{
		if(getStringAttributeValue(1000000010) != null)
		{
			Organisation organisation = new Organisation();

			organisation.setCompanyName(getStringAttributeValue(1000000001));
			organisation.setOrganisationName(getStringAttributeValue(1000000010));
			organisation.setDepartmentName(getStringAttributeValue(200000006));

			return organisation;
		}
		else return null;
	}

	private Region getLocationRegion()
	{
		if(getStringAttributeValue(200000012) != null)
		{
			Region region = new Region();
			region.setRegionName(getStringAttributeValue(200000012));
			region.setCompanyName(getStringAttributeValue(1000000001));

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
		if(getStringAttributeValue(260000001) != null)
		{
			Site site = new Site();
			site.setSiteName(getStringAttributeValue(260000001));

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

	public Company getCompany()
	{
		return company;
	}

	public Organisation getOrganisation()
	{
		return organisation;
	}

	public Region getRegion()
	{
		return region;
	}

	public Site getSite()
	{
		return site;
	}
}

