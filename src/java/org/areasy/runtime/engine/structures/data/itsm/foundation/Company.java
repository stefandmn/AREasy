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
import org.areasy.runtime.engine.structures.MultiPartItem;

/**
 * Company entity from the Foundation layer from ITSM application suite
 */
public class Company extends MultiPartItem
{
	/**
	 * Default Company structure instance.
	 */
	public Company()
	{
		setFormName("COM:Company");
		clear();
	}

	/**
	 * Create a new instance of core item structure.
	 *
	 * @return new instance of <code>CoreItem</code> structure
	 */
	public Company getInstance()
	{
		return new Company();
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
		setDefaultCompanyTypes(null);
		setDefaultCompanyName(null);

		if(containsAttributeField(COM_STATUS)) getAttribute(COM_STATUS).setLabel("Status");
		if(containsAttributeField(COM_COMPANYNAME)) getAttribute(COM_COMPANYNAME).setLabel("Name");
		if(containsAttributeField(COM_COMPANYTYPES)) getAttribute(COM_COMPANYTYPES).setLabel("Types");
	}

	/**
	 * Get company types attribute value.
	 *
	 * @return company types.
	 */
	public String getCompanyTypes()
	{
		return getStringAttributeValue(COM_COMPANYTYPES);
	}

	/**
	 * Set company types.
	 *
	 * @param types company types
	 */
	public void setDefaultCompanyTypes(String types)
	{
		setDefaultAttribute(COM_COMPANYTYPES, types);
	}

	/**
	 * Set company types.
	 *
	 * @param types company types
	 */
	public void setCompanyTypes(String types)
	{
		setAttribute(COM_COMPANYTYPES, types);
	}

	/**
	 * Get company name attribute value.
	 *
	 * @return company name.
	 */
	public String getCompanyName()
	{
		return getStringAttributeValue(COM_COMPANYNAME);
	}

	/**
	 * Set company name.
	 *
	 * @param name company name
	 */
	public void setDefaultCompanyName(String name)
	{
		setDefaultAttribute(COM_COMPANYNAME, name);
	}

	/**
	 * Set company name.
	 *
	 * @param name company name
	 */
	public void setCompanyName(String name)
	{
		setAttribute(COM_COMPANYNAME, name);
	}

	/**
	 * Set company item status.
	 *
	 * @param value company item status
	 */
	public void setDefaultStatus(String value)
	{
		setDefaultAttribute(COM_STATUS, value);
	}

	/**
	 * Set company item status.
	 *
	 * @param value company item status
	 */
	public void setStatus(String value)
	{
		setAttribute(COM_STATUS, value);
	}

	/**
	 * Get company item status.
	 *
	 * @return company item status
	 */
	public String getStatus()
	{
		return getStringAttributeValue(COM_STATUS);
	}

	public String toString()
	{
		return "Company [Name = " + getCompanyName() + ", Types = " + getCompanyTypes() + "]";
	}

	public String toFullString()
	{
		String data = toString();

		if(StringUtility.isNotEmpty(getEntryId())) data += " - " + getEntryId();
			else data += "]";

		return data;
	}
}
