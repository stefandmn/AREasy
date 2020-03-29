package org.areasy.runtime.engine.structures.data.cmdb;

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

import org.areasy.common.data.StringUtility;

import java.util.Map;

/**
 * Inventory Location CI structure.
 *
 */
public class InventoryLocation extends ConfigurationItem
{
	/**
	 * Default constructor for a general CI.
	 */
	public InventoryLocation()
	{
		super();
	}

	/**
	 * CI constructor specifying some field to defined attributes for this CI instance.
	 *
	 * @param map mapping with field ids and values.
	 */
	public InventoryLocation(Map map)
	{
		this();

		setData(map);
	}

	/**
	 * Create a new instance of configuration item structure.
	 *
	 * @return new instance of <code>ConfigurationItem</code> structure
	 */
	public InventoryLocation getInstance()
	{
		return new InventoryLocation();
	}

	/**
	 * Set default attributes for a general CI.
	 */
	protected void setDefault()
	{
		super.setDefault();

		setLocation(null);
		setClassId("BMC_INVENTORYSTORAGE");
		setClassForm("AST:InventoryStorage");

		if(containsAttributeField(CI_LOCATION)) getAttribute(CI_LOCATION).setLabel("Location");
	}

	/**
	 * Get inventory location name.
	 *
	 * @return class dataset id
	 */
	public String getLocation()
	{
		return getStringAttributeValue(CI_LOCATION);
	}

	/**
	 * Set inventory location name
	 *
	 * @param value dataset id
	 */
	public void setLocation(String value)
	{
		setAttribute(CI_LOCATION, value);
	}

	public String toString()
	{
		String data = "Inventory Storage CI [ Location = " + getLocation() + ", Dataset Id = " + getDatasetId();

		if(StringUtility.isNotEmpty(getClassId())) data += ", Class Id = " + getClassId();
		if(StringUtility.isNotEmpty(getAssetId())) data += ", Asset Id = " + getAssetId();

		data += "]";

		return data;
	}

	public String toLongString()
	{
		String data = "Inventory Storage CI [ Location = " + getLocation() + ", Dataset Id = " + getDatasetId();

		if(StringUtility.isNotEmpty(getClassId())) data += ", Class Id = " + getClassId();
		if(StringUtility.isNotEmpty(getAssetId())) data += ", Asset Id = " + getAssetId();
		if(StringUtility.isNotEmpty(getTagNumber())) data += ", Alias = " + getTagNumber();
		if(StringUtility.isNotEmpty(getName())) data += ", Name = " + getName();
		if(StringUtility.isNotEmpty(getInstanceId())) data += ", Instance Id = " + getInstanceId();
		if(StringUtility.isNotEmpty(getReconciliationId())) data += ", Reconciliation Id = " + getReconciliationId();

		if(StringUtility.isNotEmpty(getEntryId())) data += "] - " + getEntryId();
			else data += "]";

		return data;
	}
}
