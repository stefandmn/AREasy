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

import org.areasy.runtime.engine.structures.CoreItem;
import org.areasy.common.data.StringUtility;

/**
 * Organisation entity from the Foundation layer from ITSM application suite.
 */
public class Organisation extends CoreItem
{
	/**
	 * Default Organisation structure instance.
	 */
	public Organisation()
	{
		setFormName("CTM:People Organization");
		clear();
	}

	/**
	 * Create a new instance of core item structure.
	 *
	 * @return new instance of <code>CoreItem</code> structure
	 */
	public CoreItem getInstance()
	{
		return new Organisation();
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
		setDefaultCompanyName(null);
		setDefaultDepartmentName(null);
		setDefaultOrganisationName(null);
		setDefaultDescription(null);

		if(containsAttributeField(POR_STATUS)) getAttribute(POR_STATUS).setLabel("Status");
		if(containsAttributeField(POR_COMPANYNAME)) getAttribute(POR_COMPANYNAME).setLabel("Company");
		if(containsAttributeField(POR_ORGANISATIONNAME)) getAttribute(POR_ORGANISATIONNAME).setLabel("Organisation");
		if(containsAttributeField(POR_DEPARTMENTNAME)) getAttribute(POR_DEPARTMENTNAME).setLabel("Department");
		if(containsAttributeField(POR_DESCRIPTION)) getAttribute(POR_DESCRIPTION).setLabel("Description");
	}

	/**
	 * Get company name attribute value.
	 *
	 * @return company name.
	 */
	public String getCompanyName()
	{
		return getStringAttributeValue(POR_COMPANYNAME);
	}

	/**
	 * Set company name.
	 *
	 * @param name company name
	 */
	public void setDefaultCompanyName(String name)
	{
		setDefaultAttribute(POR_COMPANYNAME, name);
	}

	/**
	 * Set company name.
	 *
	 * @param name company name
	 */
	public void setCompanyName(String name)
	{
		setAttribute(POR_COMPANYNAME, name);
	}

	/**
	 * Get organisation name attribute value.
	 *
	 * @return organisation name.
	 */
	public String getOrganisationName()
	{
		return getStringAttributeValue(POR_ORGANISATIONNAME);
	}

	/**
	 * Set organisation types.
	 *
	 * @param name organisation name
	 */
	public void setDefaultOrganisationName(String name)
	{
		setDefaultAttribute(POR_ORGANISATIONNAME, name);
	}

	/**
	 * Set organisation types.
	 *
	 * @param name organisation name
	 */
	public void setOrganisationName(String name)
	{
		setAttribute(POR_ORGANISATIONNAME, name);
	}

	/**
	 * Get department name attribute value.
	 *
	 * @return department name.
	 */
	public String getDepartmentName()
	{
		return getStringAttributeValue(POR_DEPARTMENTNAME);
	}

	/**
	 * Set department name.
	 *
	 * @param name department name
	 */
	public void setDefaultDepartmentName(String name)
	{
		setDefaultAttribute(POR_DEPARTMENTNAME, name);
	}

	/**
	 * Set department name.
	 *
	 * @param name department name
	 */
	public void setDepartmentName(String name)
	{
		setAttribute(POR_DEPARTMENTNAME, name);
	}

	/**
	 * Set organisation item status.
	 *
	 * @param value organisation item status
	 */
	public void setDefaultStatus(String value)
	{
		setDefaultAttribute(POR_STATUS, value);
	}

	/**
	 * Set organisation item status.
	 *
	 * @param value organisation item status
	 */
	public void setStatus(String value)
	{
		setAttribute(POR_STATUS, value);
	}

	/**
	 * Get organisation item status.
	 *
	 * @return organisation item status
	 */
	public String getStatus()
	{
		return getStringAttributeValue(POR_STATUS);
	}

	/**
	 * Get organisation descriptionattribute value.
	 *
	 * @return organisation description.
	 */
	public String getDescription()
	{
		return getStringAttributeValue(POR_DESCRIPTION);
	}

	/**
	 * Set organisation description.
	 *
	 * @param description organisation description
	 */
	public void setDefaultDescription(String description)
	{
		setDefaultAttribute(POR_DESCRIPTION, description);
	}

	/**
	 * Set organisation description.
	 *
	 * @param description organisation description
	 */
	public void setDescription(String description)
	{
		setAttribute(POR_DESCRIPTION, description);
	}

	public String toString()
	{
		return "People Organisation [Company = " + getCompanyName() + ", Organisation = " + getOrganisationName() + ", Department = " +  getDepartmentName() + "]";
	}

	public String toFullString()
	{
		String data = toString();

		if(StringUtility.isNotEmpty(getEntryId())) data += " - " + getEntryId();
			else data += "]";

		return data;
	}
}
