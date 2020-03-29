package org.areasy.runtime.engine.structures.data.itsm.foundation;

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
import org.areasy.common.logger.Logger;
import org.areasy.common.logger.LoggerFactory;
import org.areasy.runtime.engine.base.AREasyException;
import org.areasy.runtime.engine.base.ServerConnection;
import org.areasy.runtime.engine.structures.MultiPartItem;

/**
 * Services categorization structure.
 *
 */
public class ServiceCategory extends MultiPartItem
{
	protected static Logger logger = LoggerFactory.getLog(ServiceCategory.class);

	/**
	 * Default Service Category structure instance.
	 */
	public ServiceCategory()
	{
		super();
		setFormName("CFG:Service Catalog");
	}

	/**
	 * Create a new Service Category structure.
	 *
	 * @return new Service Category instance
	 */
	public MultiPartItem getInstance()
	{
		return new ServiceCategory();
	}

	/**
	 * Service category instance with all principal attributes.
	 *
	 * @param category service category
	 * @param type service type
	 * @param item service item
	 */
	public ServiceCategory(String category, String type, String item)
	{
		this();

		setCategory(category);
		setType(type);
		setItem(item);
	}

	/**
	 * Get product category attribute value.
	 *
	 * @return service category.
	 */
	public String getCategory()
	{
		return getStringAttributeValue(CFG_CATEGORY);
	}

	/**
	 * Set service category.
	 *
	 * @param category service category
	 */
	public void setCategory(String category)
	{
		setAttribute(CFG_CATEGORY, category);
	}

	/**
	 * Get service type.
	 *
	 * @return service type
	 */
	public String getType()
	{
		return getStringAttributeValue(CFG_TYPE);
	}

	/**
	 * Set service type.
	 *
	 * @param type service type
	 */
	public void setType(String type)
	{
		setAttribute(CFG_TYPE, type);
	}

	/**
	 * Get service item.
	 *
	 * @return service item
	 */
	public String getItem()
	{
		return getStringAttributeValue(CFG_ITEM);
	}

	/**
	 * Set service item.
	 *
	 * @param item service item
	 */
	public void setItem(String item)
	{
		setAttribute(CFG_ITEM, item);
	}

	/**
	 * Get associated company for this service category instance.
	 *
	 * @return the company name
	 */
	public String getCompany()
	{
		return getStringAttributeValue(CFG_COMPANYNAME);
	}

	/**
	 * Set an associated company name for this service category instance.
	 * @param company company name
	 */
	public void setCompany(String company)
	{
		setAttribute(CFG_COMPANYNAME, company);
	}

	/**
	 * Read service category structure and fill all existent field attributes.
	 *
	 * @param arsession user session
	 */
	public void read(ServerConnection arsession) throws AREasyException
	{
		try
		{
			String company = getCompany();
			setNullAttribute(CFG_COMPANYNAME);

			super.read(arsession);
			if(company != null) setCompany(company);			
		}
		catch(Throwable th)
		{
			logger.error("Error reading service category structure '" + this + "': " + th.getMessage());
			logger.debug("Exception", th);

			if(th instanceof AREasyException) throw (AREasyException)th;
				else throw new AREasyException("Error reading service category structure '" + this + "': " + th.getMessage());
		}
	}

	public String toString()
	{
		return "Service Category [Category = " + getCategory() + ", Type = " + getType() + ", Item = " + getItem() + "]";
	}

	public String toFullString()
	{
		String data = toString();

		if(StringUtility.isNotEmpty(getEntryId())) data += " - " + getEntryId();

		return data;
	}
}
