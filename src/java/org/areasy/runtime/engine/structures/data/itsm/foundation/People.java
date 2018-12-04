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

import java.util.Date;

/**
 * People entity from the Foundation layer from ITSM application suite.
 *
 */
public class People extends MultiPartItem
{
	/**
	 * Default People structure instance.
	 */
	public People()
	{
		setFormName("CTM:People");
	}

	/**
	 * Create a new People structure.
	 *
	 * @return new People instance
	 */
	public People getInstance()
	{
		return new People();
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
		setDefaultFirstName(null);
		setDefaultLastName(null);
		setDefaultHRId(null);
		setDefaultCorporateId(null);
		setDefaultLoginId(null);

		if(containsAttributeField(CTM_FIRSTNAME)) getAttribute(CTM_FIRSTNAME).setLabel("First Name");
		if(containsAttributeField(CTM_LASTNAME)) getAttribute(CTM_LASTNAME).setLabel("Last Name");
		if(containsAttributeField(CTM_HRID)) getAttribute(CTM_HRID).setLabel("HR Id");
		if(containsAttributeField(CTM_CORPORATEID)) getAttribute(CTM_CORPORATEID).setLabel("Corporate Id");
		if(containsAttributeField(CTM_LOGINID)) getAttribute(CTM_LOGINID).setLabel("Login Id");
	}

	/**
	 * Get person instance id value.
	 *
	 * @return person instance id.
	 */
	public String getInstanceId()
	{
		return getStringAttributeValue(CTM_INSTANCEID);
	}

	/**
	 * Set person instance id.
	 *
	 * @param value instance id
	 */
	public void setDefaultInstanceId(String value)
	{
		setDefaultAttribute(CTM_INSTANCEID, value);
	}

	/**
	 * Set person instance id.
	 *
	 * @param value instance id
	 */
	public void setInstanceId(String value)
	{
		setAttribute(CTM_INSTANCEID, value);
	}

	/**
	 * Get person first name attribute value.
	 *
	 * @return person first name.
	 */
	public String getFirstName()
	{
		return getStringAttributeValue(CTM_FIRSTNAME);
	}

	/**
	 * Set person first name.
	 *
	 * @param value person first name
	 */
	public void setDefaultFirstName(String value)
	{
		setDefaultAttribute(CTM_FIRSTNAME, value);
	}

	/**
	 * Set person first name.
	 *
	 * @param value person first name
	 */
	public void setFirstName(String value)
	{
		setAttribute(CTM_FIRSTNAME, value);
	}

	/**
	 * Get person last name attribute value.
	 *
	 * @return person last name.
	 */
	public String getLastName()
	{
		return getStringAttributeValue(CTM_LASTNAME);
	}

	/**
	 * Set person last name.
	 *
	 * @param value person first name
	 */
	public void setDefaultLastName(String value)
	{
		setDefaultAttribute(CTM_LASTNAME, value);
	}

	/**
	 * Set person last name.
	 *
	 * @param value person first name
	 */
	public void setLastName(String value)
	{
		setAttribute(CTM_LASTNAME, value);
	}

	/**
	 * Get person corporate id attribute value.
	 *
	 * @return person corporate id.
	 */
	public String getCorporateId()
	{
		return getStringAttributeValue(CTM_CORPORATEID);
	}

	/**
	 * Set person corporate id.
	 *
	 * @param value person corporate id
	 */
	public void setDefaultCorporateId(String value)
	{
		setDefaultAttribute(CTM_CORPORATEID, value);
	}

	/**
	 * Set person corporate id.
	 *
	 * @param value person corporate id
	 */
	public void setCorporateId(String value)
	{
		setAttribute(CTM_CORPORATEID, value);
	}

	/**
	 * Get person HR id attribute value.
	 *
	 * @return person HR id.
	 */
	public String getHRId()
	{
		return getStringAttributeValue(CTM_HRID);
	}

	/**
	 * Set person HR id.
	 *
	 * @param value person HR id
	 */
	public void setDefaultHRId(String value)
	{
		setDefaultAttribute(CTM_HRID, value);
	}

	/**
	 * Set person HR id.
	 *
	 * @param value person HR id
	 */
	public void setHRId(String value)
	{
		setAttribute(CTM_HRID, value);
	}

	/**
	 * Get person login id attribute value.
	 *
	 * @return person login id.
	 */
	public String getLoginId()
	{
		return getStringAttributeValue(CTM_LOGINID);
	}

	/**
	 * Set person login id.
	 *
	 * @param value person login id
	 */
	public void setDefaultLoginId(String value)
	{
		setDefaultAttribute(CTM_LOGINID, value);
	}

	/**
	 * Set person login id.
	 *
	 * @param value person login id
	 */
	public void setLoginId(String value)
	{
		setAttribute(CTM_LOGINID, value);
	}

	/**
	 * Get person company name attribute value.
	 *
	 * @return person company name.
	 */
	public String getCompanyName()
	{
		return getStringAttributeValue(CTM_COMPANYNAME);
	}

	/**
	 * Set person company name.
	 *
	 * @param value person company name
	 */
	public void setDefaultCompanyName(String value)
	{
		setDefaultAttribute(CTM_COMPANYNAME, value);
	}

	/**
	 * Set person company name.
	 *
	 * @param value person company name
	 */
	public void setCompanyName(String value)
	{
		setAttribute(CTM_COMPANYNAME, value);
	}

	/**
	 * Get person organisation name attribute value.
	 *
	 * @return person organisation name.
	 */
	public String getOrganisationName()
	{
		return getStringAttributeValue(CTM_PPLORGANISATION);
	}

	/**
	 * Set person organisation name.
	 *
	 * @param value person organisation name
	 */
	public void setDefaultOrganisationName(String value)
	{
		setDefaultAttribute(CTM_PPLORGANISATION, value);
	}

	/**
	 * Set person organisation name.
	 *
	 * @param value person organisation name
	 */
	public void setOrganisationName(String value)
	{
		setAttribute(CTM_PPLORGANISATION, value);
	}

	/**
	 * Get person department name attribute value.
	 *
	 * @return person department name.
	 */
	public String getDepartmentName()
	{
		return getStringAttributeValue(CTM_DEPARTMENTNAME);
	}

	/**
	 * Set person department name.
	 *
	 * @param value person department name
	 */
	public void setDefaultDepartmentName(String value)
	{
		setDefaultAttribute(CTM_DEPARTMENTNAME, value);
	}

	/**
	 * Set person department name.
	 *
	 * @param value person department name
	 */
	public void setDepartmentName(String value)
	{
		setAttribute(CTM_DEPARTMENTNAME, value);
	}

	/**
	 * Get person region code attribute value.
	 *
	 * @return person region code.
	 */
	public String getRegion()
	{
		return getStringAttributeValue(CTM_REGIONNAME);
	}

	/**
	 * Set person region code.
	 *
	 * @param value person region code
	 */
	public void setDefaultRegion(String value)
	{
		setDefaultAttribute(CTM_REGIONNAME, value);
	}

	/**
	 * Set person region code.
	 *
	 * @param value person region code
	 */
	public void setRegion(String value)
	{
		setAttribute(CTM_REGIONNAME, value);
	}

	/**
	 * Get person site name attribute value.
	 *
	 * @return person site name.
	 */
	public String getSiteName()
	{
		return getStringAttributeValue(CTM_SITENAME);
	}

	/**
	 * Set person site name.
	 *
	 * @param value person site name
	 */
	public void setDefaultSiteName(String value)
	{
		setDefaultAttribute(CTM_SITENAME, value);
	}

	/**
	 * Set person site name.
	 *
	 * @param value person site name
	 */
	public void setSiteName(String value)
	{
		setAttribute(CTM_SITENAME, value);
	}

	/**
	 * Get person site group attribute value.
	 *
	 * @return person site group.
	 */
	public String getSiteGroup()
	{
		return getStringAttributeValue(CTM_SITEGROUP);
	}

	/**
	 * Set person site group.
	 *
	 * @param value person site group
	 */
	public void setDefaultSiteGroup(String value)
	{
		setDefaultAttribute(CTM_SITEGROUP, value);
	}

	/**
	 * Set person site group.
	 *
	 * @param value person site group
	 */
	public void setSiteGroup(String value)
	{
		setAttribute(CTM_SITEGROUP, value);
	}

	/**
	 * Get person site id attribute value.
	 *
	 * @return person site id.
	 */
	public String getSiteId()
	{
		return getStringAttributeValue(CTM_SITEID);
	}

	/**
	 * Set person site id.
	 *
	 * @param value person site id
	 */
	public void setDefaultSiteId(String value)
	{
		setDefaultAttribute(CTM_SITEID, value);
	}

	/**
	 * Set person site id.
	 *
	 * @param value person site id
	 */
	public void setSiteId(String value)
	{
		setAttribute(CTM_SITEID, value);
	}

    /**
     * Get person full name attribute value.
     *
     * @return person full name.
     */
    public String getFullName()
	{
        return getStringAttributeValue(CTM_FULLNAME);
    }

    /**
     * Set person full name attribute value.
     *
     * @param value person full name
     */
    public void setDefaultFullName(String value)
	{
        setDefaultAttribute(CTM_FULLNAME, value);
    }

	/**
     * Set person full name attribute value.
     *
     * @param value person full name
     */
    public void setFullName(String value)
	{
        setAttribute(CTM_FULLNAME, value);
    }

    /**
     * Set person last modified date value.
     */
    public void setLastModifiedDate()
	{
        setAttribute(6, new Date());
    }

	public String toString()
	{
		return "People [First Name = " + getFirstName() + ", Last Name = " + getLastName() +
				", Login Id = " + getLoginId() + ", HR Id = " + getHRId() + ", Corporate Id = " + getCorporateId() +
				", Company = " + getCompanyName() + ", Organisation = " + getOrganisationName() + ", Department = " + getDepartmentName() +
				", Region = " + getRegion() + ", Site Group = " + getSiteGroup() + ", Site = " + getSiteName() +
				", Email = " + getStringAttributeValue(1000000048) + ", Cost Center Code = " + getStringAttributeValue(300469300) +
				", Cost Center Name = " + getStringAttributeValue(300469200) + "]";
	}

	public String toFullString()
	{
		String data = toString();

		if(StringUtility.isNotEmpty(getEntryId())) data += " - " + getEntryId();
			else data += "]";

		return data;
	}

	/**
	 * Creates and returns a copy of this object.
	 */
	public Object clone()
	{
		return copy();
	}
}
