package org.areasy.runtime.engine.structures.data.itsm.foundation;

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

import org.areasy.runtime.engine.structures.CoreItem;
import org.areasy.common.data.StringUtility;

/**
 * Region entity from the Foundation layer from ITSM application suite.
 */
public class Region extends CoreItem
{
	/**
	 * Default Organisation structure instance.
	 */
	public Region()
	{
		setFormName("CTM:Region");
		clear();
	}

	/**
	 * Create a new instance of core item structure.
	 *
	 * @return new instance of <code>CoreItem</code> structure
	 */
	public CoreItem getInstance()
	{
		return new Region();
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
		setDefaultRegionName(null);
		setDefaultCompany(null);

		if(containsAttributeField(CTM_STATUS)) getAttribute(CTM_STATUS).setLabel("Status");
		if(containsAttributeField(CTM_REGIONNAME)) getAttribute(CTM_REGIONNAME).setLabel("Name");
		if(containsAttributeField(CTM_COMPANYNAME)) getAttribute(CTM_COMPANYNAME).setLabel("Company");
	}

	/**
	 * Get region name attribute value.
	 *
	 * @return region name.
	 */
	public String getRegionName()
	{
		return getStringAttributeValue(CTM_REGIONNAME);
	}

	/**
	 * Set region name.
	 *
	 * @param name region name
	 */
	public void setDefaultRegionName(String name)
	{
		setDefaultAttribute(CTM_REGIONNAME, name);
	}

	/**
	 * Set region name.
	 *
	 * @param name region name
	 */
	public void setRegionName(String name)
	{
		setAttribute(CTM_REGIONNAME, name);
	}

	/**
	 * Get company name attribute value.
	 *
	 * @return company name.
	 */
	public String getCompany()
	{
		return getStringAttributeValue(CTM_COMPANYNAME);
	}

	/**
	 * Set company name.
	 *
	 * @param name country name
	 */
	public void setDefaultCompany(String name)
	{
		setDefaultAttribute(CTM_COMPANYNAME, name);
	}

	/**
	 * Set company name.
	 *
	 * @param name country name
	 */
	public void setCompanyName(String name)
	{
		setAttribute(CTM_COMPANYNAME, name);
	}

	/**
	 * Set organisation item status.
	 *
	 * @param value organisation item status
	 */
	public void setDefaultStatus(String value)
	{
		setDefaultAttribute(CTM_STATUS, value);
	}

	/**
	 * Set organisation item status.
	 *
	 * @param value organisation item status
	 */
	public void setStatus(String value)
	{
		setAttribute(CTM_STATUS, value);
	}

	/**
	 * Get organisation item status.
	 *
	 * @return organisation item status
	 */
	public String getStatus()
	{
		return getStringAttributeValue(CTM_STATUS);
	}

	public String toString()
	{
		return "Region [Name = " + getRegionName() + ", Company = " + getCompany() + "]";
	}

	public String toFullString()
	{
		String data = toString();

		if(StringUtility.isNotEmpty(getEntryId())) data += " - " + getEntryId();
			else data += "]";

		return data;
	}
}
