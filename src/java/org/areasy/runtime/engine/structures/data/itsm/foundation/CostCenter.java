package org.areasy.runtime.engine.structures.data.itsm.foundation;

/*
 * Copyright (c) 2007-2016 AREasy Runtime
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

/**
 * CostCenter entity from the Foundation layer from ITSM application suite
 */
public class CostCenter extends CoreItem
{
	/**
	 * Default Company structure instance.
	 */
	public CostCenter()
	{
		setFormName("FIN:ConfigCostCentersRepository");
		clear();
	}

	/**
	 * Create a new instance of core item structure.
	 *
	 * @return new instance of <code>CoreItem</code> structure
	 */
	public CoreItem getInstance()
	{
		return new CostCenter();
	}

	/**
	 * Get string representation of the current core item structure.
	 *
	 * @return string data model.
	 */
	public String toString()
	{
		return "Cost Center [Company = " + getAttributeValue(1000000001) + ", Code = " + getAttributeValue(300469300) + ", Name = " + getAttributeValue(300469200) + "]";
	}

	/**
	 * Get company name attribute value.
	 *
	 * @return company name.
	 */
	public String getCompanyName()
	{
		return getStringAttributeValue(1000000001);
	}

	/**
	 * Set company name.
	 *
	 * @param name company name
	 */
	public void setCompanyName(String name)
	{
		setAttribute(1000000001, name);
	}

	/**
	 * Get cost center code attribute value.
	 *
	 * @return cost center code.
	 */
	public String getCostCenterCode()
	{
		return getStringAttributeValue(300469300);
	}

	/**
	 * Set cost center code.
	 *
	 * @param code cost center code
	 */
	public void setCostCenterCode(String code)
	{
		setAttribute(300469300, code);
	}

	/**
	 * Get cost center code attribute value.
	 *
	 * @return cost center code.
	 */
	public String getCostCenterName()
	{
		return getStringAttributeValue(300469200);
	}

	/**
	 * Set cost center name.
	 *
	 * @param name cost center name
	 */
	public void setCostCenterName(String name)
	{
		setAttribute(300469200, name);
	}
}
