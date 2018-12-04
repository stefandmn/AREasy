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
import org.areasy.runtime.engine.base.ARDictionary;
import org.areasy.runtime.engine.base.AREasyException;
import org.areasy.runtime.engine.base.ServerConnection;
import org.areasy.runtime.engine.structures.CoreItem;
import org.areasy.runtime.engine.structures.MultiPartItem;

import java.util.List;

/**
 * Support Group entity from the Foundation layer from ITSM application suite.
 *
 */
public class SupportGroup extends MultiPartItem
{
	/**
	 * Default People structure instance.
	 */
	public SupportGroup()
	{
		setFormName("CTM:Support Group");
	}

	/**
	 * Create a new People structure.
	 *
	 * @return new People instance
	 */
	public MultiPartItem getInstance()
	{
		return new SupportGroup();
	}

	/**
	 * Get group instance id value.
	 *
	 * @return group instance id.
	 */
	public String getInstanceId()
	{
		return getStringAttributeValue(CTM_INSTANCEID);
	}

	/**
	 * Set group instance id.
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
	public String getCompanyName()
	{
		return getStringAttributeValue(CTM_COMPANYNAME);
	}

	/**
	 * Set person first name.
	 *
	 * @param value person first name
	 */
	public void setCompanyName(String value)
	{
		setAttribute(CTM_COMPANYNAME, value);
	}

	/**
	 * Get group organisation attribute value.
	 *
	 * @return group organisation.
	 */
	public String getOrganisationName()
	{
		return getStringAttributeValue(CTM_SGRORGANISATION);
	}

	/**
	 * Set group organisation.
	 *
	 * @param value group organisation
	 */
	public void setOrganisationName(String value)
	{
		setAttribute(CTM_SGRORGANISATION, value);
	}

	/**
	 * Get group name attribute value.
	 *
	 * @return group name.
	 */
	public String getSupportGroupName()
	{
		return getStringAttributeValue(CTM_GROUPNAME);
	}

	/**
	 * Set group name.
	 *
	 * @param value group name
	 */
	public void setSupportGroupName(String value)
	{
		setAttribute(CTM_GROUPNAME, value);
	}

	public String toString()
	{
		return "Support Group [Request ID = " + getEntryId() + ", Company = " + getCompanyName() + ", Organisation = " + getOrganisationName() + ", Group Name = " + getSupportGroupName() + "]";
	}

	/**
	 * Get group role.
	 *
	 * @return group role
	 */
	public String getRole()
	{
		return getStringAttributeValue(CTM_SGROUPROLE);
	}

	/**
	 * Set group role.
	 *
	 * @param role group role
	 */
	public void setRole(String role)
	{
		setAttribute(CTM_SGROUPROLE, role);
	}

    public String getDescription()
    {
        return getStringAttributeValue(CTM_SGROUPDESCRIPTION);
    }

    public void setDescription(String description)
    {
        setAttribute(CTM_SGROUPDESCRIPTION,description);
    }


    public Integer getVendorGroup()
    {
        return (Integer) getAttributeValue(CTM_SGROUPVENDOR);
    }

    public void setVendorGroup(Integer vendorGroup)
    {
        setAttribute(CTM_SGROUPVENDOR,vendorGroup);
    }

    public Integer getOnCallGroup()
    {
        return (Integer) getAttributeValue(CTM_SGROUPONCALL);
    }

    public void setOnCallGroup(Integer onCallGroup)
    {
        setAttribute(CTM_SGROUPONCALL,onCallGroup);
    }

    public void setStatus(String status)
	{
        setAttribute(7, status);
    }

    public String getStatus()
	{
        return getStringAttributeValue(7);
    }

    public Integer getStatusId()
	{
        return (Integer)getAttributeValue(7);
    }

	public String toFullString()
	{
		String data = toString();

		if(StringUtility.isNotEmpty(getEntryId())) data += " - " + getEntryId();
			else data += "]";

		return data;
	}

	public void readBySystemGroupId(ServerConnection arsession, Integer groupId) throws AREasyException
	{
		CoreItem permission = new CoreItem();
		permission.setFormName("CTM:SYS-Access Permission Grps");
		permission.setAttribute(1000001579, groupId);

		permission.read(arsession);

		if(permission.exists())
		{
			String supportId = permission.getStringAttributeValue(301242000);
			clear();

			setAttribute(1, supportId);
			read(arsession);
		}
	}

	public CoreItem getRelatedSystemGroup(ServerConnection arsession) throws AREasyException
	{
		if(exists())
		{
			CoreItem permission = new CoreItem();

			permission.setFormName("CTM:SYS-Access Permission Grps");
			permission.setAttribute(301242000, getEntryId());

			permission.read(arsession);
			return permission;
		}
		else return null;
	}

	public List getAliases(ServerConnection arsession) throws AREasyException
	{
		if(exists())
		{
			CoreItem data = new CoreItem();

			data.setFormName("CTM:Support Group Alias");
			data.setAttribute(ARDictionary.CTM_SGROUPID, getEntryId());

			return data.search(arsession);
		}
		else return null;
	}
}
