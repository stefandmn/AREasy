package org.areasy.runtime.engine.structures.data.itsm.foundation;

/*
 * Copyright (c) 2007-2018 AREasy Runtime
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
import org.areasy.runtime.engine.base.AREasyException;
import org.areasy.runtime.engine.base.ServerConnection;
import org.areasy.runtime.engine.structures.CoreItem;
import org.areasy.runtime.engine.structures.MultiPartItem;

/**
 * Organisation entity from the Foundation layer from ITSM application suite.
 *
 */
public class Site extends MultiPartItem
{
	/**
	 * Default Organisation structure instance.
	 */
	public Site()
	{
		setFormName("SIT:Site");
		clear();
	}

	/**
	 * Create a new instance of core item structure.
	 *
	 * @return new instance of <code>CoreItem</code> structure
	 */
	public MultiPartItem getInstance()
	{
		return new Site();
	}

	/**
	 * Delete all attributes and reset the enty id.
	 */
	public void clear()
	{
		super.clear();
		setDefault();
	}

	/**
	 * Set default attributes for a general company structure.
	 */
	protected void setDefault()
	{
		setDefaultStatus(null);
		setDefaultSiteName(null);
		setDefaultCity(null);
		setDefaultCountry(null);

		if(containsAttributeField(SIT_STATUS)) getAttribute(SIT_STATUS).setLabel("Status");
		if(containsAttributeField(SIT_SITENAME)) getAttribute(SIT_SITENAME).setLabel("Name");
		if(containsAttributeField(SIT_COUNTRY)) getAttribute(SIT_COUNTRY).setLabel("Country");
		if(containsAttributeField(SIT_CITY)) getAttribute(SIT_CITY).setLabel("City");
	}

	/**
	 * Get site name attribute value.
	 *
	 * @return site name.
	 */
	public String getSiteName()
	{
		return getStringAttributeValue(SIT_SITENAME);
	}

	/**
	 * Set site name.
	 *
	 * @param name site name
	 */
	public void setDefaultSiteName(String name)
	{
		setDefaultAttribute(SIT_SITENAME, name);
	}

	/**
	 * Set site name.
	 *
	 * @param name site name
	 */
	public void setSiteName(String name)
	{
		setAttribute(SIT_SITENAME, name);
	}

	/**
	 * Get country name attribute value.
	 *
	 * @return country name.
	 */
	public String getCountry()
	{
		return getStringAttributeValue(SIT_COUNTRY);
	}

	/**
	 * Set country name.
	 *
	 * @param name country name
	 */
	public void setDefaultCountry(String name)
	{
		setDefaultAttribute(SIT_COUNTRY, name);
	}

	/**
	 * Set country name.
	 *
	 * @param name country name
	 */
	public void setCountry(String name)
	{
		setAttribute(SIT_COUNTRY, name);
	}

	/**
	 * Get city name attribute value.
	 *
	 * @return city name.
	 */
	public String getCity()
	{
		return getStringAttributeValue(SIT_CITY);
	}

	/**
	 * Set city name.
	 *
	 * @param name city name
	 */
	public void setDefaultCity(String name)
	{
		setDefaultAttribute(SIT_CITY, name);
	}

	/**
	 * Set city name.
	 *
	 * @param name city name
	 */
	public void setCity(String name)
	{
		setAttribute(SIT_CITY, name);
	}

	/**
	 * Set organisation item status.
	 *
	 * @param value organisation item status
	 */
	public void setDefaultStatus(String value)
	{
		setDefaultAttribute(SIT_STATUS, value);
	}

	/**
	 * Set organisation item status.
	 *
	 * @param value organisation item status
	 */
	public void setStatus(String value)
	{
		setAttribute(SIT_STATUS, value);
	}

	/**
	 * Get organisation item status.
	 *
	 * @return organisation item status
	 */
	public String getStatus()
	{
		return getStringAttributeValue(SIT_STATUS);
	}

	public String toString()
	{
		return "Site [Name = " + getSiteName() + ", Country = " + getCountry() + ", City = " +  getCity() + "]";
	}

	public String toFullString()
	{
		String data = toString();

		if(StringUtility.isNotEmpty(getEntryId())) data += " - " + getEntryId();
			else data += "]";

		return data;
	}

	public void setCompanyAssociation(ServerConnection arsession, String company, String region) throws AREasyException
	{
		setCompanyAssociation(arsession, company, region, null, null);
	}

	public void setCompanyAssociation(ServerConnection arsession, String company, String region, String siteGroup, Integer type) throws AREasyException
	{
		if(!this.exists()) throw new AREasyException("Site structure is not registered: " + this);

		if(company == null) throw new AREasyException("Company entity is null: " + company);

		Company entity = new Company();
		entity.setCompanyName(company);

		entity.read(arsession);

		if(entity.exists())
		{
			CoreItem assoc = new CoreItem();
			assoc.setFormName("SIT:Site Company Association");
			assoc.setAttribute(1000000074, getEntryId());
			assoc.setAttribute(1000000001, company);

			assoc.read(arsession);

			assoc.setAttribute(7, new Integer(1));

			if(region != null) assoc.setAttribute(200000012, region);
			if(siteGroup != null) assoc.setAttribute(200000007, siteGroup);
			if(type != null) assoc.setAttribute(1000000704, type);

			if(assoc.exists())
			{
				assoc.update(arsession);
			}
			else
			{
				assoc.create(arsession);

				RuntimeLogger.debug("Site/Company association has been created: " + assoc);
			}
		}
		else  throw new AREasyException("Company entity couldn't be found: " + entity);
	}
}
