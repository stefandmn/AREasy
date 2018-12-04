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
import org.areasy.common.logger.Logger;
import org.areasy.common.logger.LoggerFactory;
import org.areasy.runtime.engine.base.AREasyException;
import org.areasy.runtime.engine.base.ServerConnection;
import org.areasy.runtime.engine.structures.CoreItem;
import org.areasy.runtime.engine.structures.MultiPartItem;

import java.util.Iterator;
import java.util.Map;

/**
 * Product categorization structure.
 */
public class ProductCategory extends MultiPartItem
{
	protected static Logger logger = LoggerFactory.getLog(ProductCategory.class);

	private boolean incidentFlag = false;
	private boolean problemFlag = false;
	private boolean workorderFlag = false;
	private boolean assetFlag = false;
	private boolean requestFlag = false;
	private boolean changeFlag = false;
	private boolean purchaseFlag = false;
	private boolean releaseFlag = false;

	/**
	 * Default Product Category structure instance.
	 */
	public ProductCategory()
	{
		super();
		setFormName("PCT:Product Catalog");
	}

	/**
	 * Create a new Product Category structure.
	 *
	 * @return new Product Category instance
	 */
	public ProductCategory getInstance()
	{
		return new ProductCategory();
	}

	/**
	 * Product category instance with all principal attributes.
	 *
	 * @param category product category
	 * @param type product type
	 * @param item product item
	 * @param model product name
	 * @param manufacturer product manufacturer
	 */
	public ProductCategory(String category, String type, String item, String model, String manufacturer)
	{
		this();

		setCategory(category);
		setType(type);
		setItem(item);

		setModel(model);
		setManufacturer(manufacturer);
	}

	/**
	 * Get product category attribute value.
	 *
	 * @return product category.
	 */
	public String getCategory()
	{
		return getStringAttributeValue(PCT_CATEGORY);
	}

	/**
	 * Set product category.
	 *
	 * @param category product category
	 */
	public void setCategory(String category)
	{
		setAttribute(PCT_CATEGORY, category);
	}

	/**
	 * Get product type.
	 *
	 * @return product type
	 */
	public String getType()
	{
		return getStringAttributeValue(PCT_TYPE);
	}

	/**
	 * Set ptoduct type.
	 *
	 * @param type product type
	 */
	public void setType(String type)
	{
		setAttribute(PCT_TYPE, type);
	}

	/**
	 * Get product item.
	 *
	 * @return product item
	 */
	public String getItem()
	{
		return getStringAttributeValue(PCT_ITEM);
	}

	/**
	 * Set product item.
	 *
	 * @param item product item
	 */
	public void setItem(String item)
	{
		setAttribute(PCT_ITEM, item);
	}

	/**
	 * Get product model.
	 *
	 * @return product model (product name)
	 */
	public String getModel()
	{
		return getStringAttributeValue(PCT_MODEL);
	}

	/**
	 * Set product model.
	 *
	 * @param model product model
	 */
	public void setModel(String model)
	{
		if(StringUtility.isEmpty(model) && StringUtility.isNotEmpty( getManufacturer() )) model = "- None -";
		setAttribute(PCT_MODEL, model);
	}

	/**
	 * Get product manufacturer.
	 *
	 * @return product manufacturer
	 */
	public String getManufacturer()
	{
		return getStringAttributeValue(PCT_MANUFACTURER);
	}

	/**
	 * Set product manufacturer.
	 *
	 * @param manufacturer product manufacturer
	 */
	public void setManufacturer(String manufacturer)
	{
		setAttribute(PCT_MANUFACTURER, manufacturer);
		setModel(getModel());
	}

	/**
	 * Get associated company for this product category instance.
	 *
	 * @return the company name
	 */
	public String getCompanyName()
	{
		return getStringAttributeValue(PCT_COMPANYNAME);
	}

	/**
	 * Set an associated company name for this product category instance.
	 * @param company company name
	 */
	public void setCompanyName(String company)
	{
		setAttribute(PCT_COMPANYNAME, company);
	}

	/**
	 * Get CMDB class association with this product category instance.
	 *
	 * @return the CMDB class id.
	 */
	public String getClassAssociation()
	{
		return getStringAttributeValue(PCT_CLASSKEYWORD);
	}

	/**
	 * Set the CMDB class association with this product category instance.
	 *
	 * @param classAssociation the CMDB class id (class keyword)
	 */
	public void setClassAssociation(String classAssociation)
	{
		setAttribute(PCT_CLASSKEYWORD, classAssociation);
	}

	public boolean isValid()
	{
		boolean valid = true;

		if(StringUtility.isEmpty(getCategory()) || StringUtility.isEmpty(getType()) || StringUtility.isEmpty(getItem())) valid = false;

		if(StringUtility.isEmpty(getModel()) && StringUtility.isEmpty(getManufacturer())) valid = true;
			else if(StringUtility.isNotEmpty(getModel()) && StringUtility.isNotEmpty(getManufacturer())) valid = true;
				else if(StringUtility.isNotEmpty(getModel()) && StringUtility.isEmpty(getManufacturer())) valid = false;
					else if(StringUtility.isEmpty(getModel()) && StringUtility.isNotEmpty(getManufacturer())) valid = false;

		return valid;
	}

	/**
	 * Read Product category structure and fill all existent field attributes.
	 *
	 * @param arsession user session
	 */
	public void read(ServerConnection arsession) throws AREasyException
	{
		if(!isValid()) throw new AREasyException("Invalid product category structure for model and manufacturer: " + getModel() + "->" + getManufacturer());

		//validate product category entry.
		if(ignoreNullValues())
		{
			if(StringUtility.isEmpty(getModel())) setDefaultNullAttribute(PCT_MODEL);
			if(StringUtility.isEmpty(getManufacturer())) setDefaultNullAttribute(PCT_MANUFACTURER);
		}
		else
		{
			if(StringUtility.isEmpty(getModel())) setNullAttribute(PCT_MODEL);
			if(StringUtility.isEmpty(getManufacturer())) setNullAttribute(PCT_MANUFACTURER);
		}

		try
		{
			String company = getCompanyName();
			setDefaultNullAttribute(PCT_COMPANYNAME);

			super.read(arsession);
			if(company != null) setCompanyName(company);

			//add parts
			if(exists())
			{
				CoreItem item1 = new CoreItem();
				item1.setFormName("PCT:ProductCompanyAssociation");
				item1.setAttribute(1000000097, getEntryId());
				item1.read(arsession);

				//register product-company association
				if(item1.exists()) addPart("assoc", item1, 1000000097, 1);

				CoreItem item2 = new CoreItem();
				item2.setFormName("PCT:Product Alias");
				item2.setAttribute(1000000097, getEntryId());
				item2.read(arsession);

				//register product alias
				if(item2.exists()) addPart("alias", item2, 1000000097, 1);
			}
		}
		catch(Throwable th)
		{
			logger.error("Error reading product category structure '" + this + "': " + th.getMessage());
			logger.debug("Exception", th);

			if(th instanceof AREasyException) throw (AREasyException)th;
				else throw new AREasyException("Error reading product category structure '" + this + "': " + th.getMessage());
		}
	}

	/**
	 * Create Product category structure and fill all existent field attributes.
	 *
	 * @param arsession user session
	 * @throws AREasyException if any error will occur
	 */
	public void create(ServerConnection arsession) throws AREasyException
	{
		String company = getCompanyName();

		super.create(arsession);

		if(hasMultiParts())
		{
			//process all parts
			processParts(arsession);
		}
		else if(!StringUtility.isEmpty(company)) //perform company relationship
		{
			setCompanyName(company);
			setCompanyAssoc(arsession);
		}
	}

	public void update(ServerConnection arsession) throws AREasyException
	{
		String company = getCompanyName();
		
		super.update(arsession);

		if(hasMultiParts())
		{
			//process all parts
			processParts(arsession);
		}
		else if(!StringUtility.isEmpty(company))
		{
			//perform company relationship
			setCompanyName(company);
			setCompanyAssoc(arsession);
		}
	}

	protected void processParts(ServerConnection arsession) throws AREasyException
	{
		if(hasMultiParts())
		{
			Iterator iterator = getPartInstances();

			while(iterator != null && iterator.hasNext())
			{
				CoreItem item = (CoreItem) iterator.next();
				CoreItem entry = new CoreItem();
				entry.setFormName(item.getFormName());
				entry.setAttribute(1000000097, item.getEntryId());
				entry.read(arsession);

				if(entry.exists())
				{
					Map map = item.getData();
					if(map != null && map.containsKey(new Integer(1))) map.remove(new Integer(1));

					entry.setData(map);
					entry.update(arsession);
				}
				else item.create(arsession);
			}
		}
	}

	protected void setCompanyAssoc(ServerConnection arsession) throws AREasyException
	{
		if(isValid() && exists() && StringUtility.isNotEmpty(getStringAttributeValue(179)) && StringUtility.isNotEmpty(getCompanyName()))
		{
			CoreItem item = new CoreItem();
			item.setFormName("PCT:ProductCompanyAssociation");
			item.setAttribute(1000000097, getEntryId());
			item.setAttribute(300724400, getStringAttributeValue(179));

			item.read(arsession);

			//update incident flag
			if(isIncidentFlag())
			{
				item.setAttribute(1000000104, new Integer(0));
				item.setAttribute(1000000111, new Integer(0));
				item.setAttribute(1000000113, new Integer(0));
				item.setAttribute(1000000112, new Integer(0));
				item.setAttribute(1000000679, new Integer(0));
			}
			else
			{
				item.setNullAttribute(1000000104);
				item.setNullAttribute(1000000111);
				item.setNullAttribute(1000000113);
				item.setNullAttribute(1000000112);
				item.setNullAttribute(1000000679);
			}

			//update problem flag
			if(isProblemFlag()) item.setAttribute(1000000105, new Integer(0));
				else item.setNullAttribute(1000000105);

			//update asset flag
			if(isAssetFlag()) item.setAttribute(1000000108, new Integer(0));
				else item.setNullAttribute(1000000108);

			//update request flag
			if(isRequestFlag()) item.setAttribute(1000000107, new Integer(0));
				else item.setNullAttribute(1000000107);

			//update change flag
			if(isChangeFlag())item.setAttribute(1000000106, new Integer(0));
				else item.setNullAttribute(1000000106);

			//update purchase flag
			if(isPurchaseFlag())item.setAttribute(1000000178, new Integer(0));
				else item.setNullAttribute(1000000178);

			//update workorder flag
			if(isWorkorderFlag()) item.setAttribute(377771011, new Integer(0));
				else item.setNullAttribute(377771011);

			//update release flag
			if(isReleaseFlag())item.setAttribute(1000001834, new Integer(0));
				else item.setNullAttribute(1000001834);

			//not ignore null value
			item.setNotIgnoreNullValues();

			//commit transaction
			if(!item.exists())
			{
				item.setAttribute(1000000001, getCompanyName());
				item.setAttribute(2, arsession.getUserName());
				item.setAttribute(8, ".");

				item.create(arsession);
			}
			else
			{
				item.setAttribute(1000000001, getCompanyName());
				item.update(arsession);
			}
		}
	}

	public String toString()
	{
		String data = "Product Category [";

		data += "Category = " + getCategory() + ", Type = " + getType() + ", Item = " + getItem();

		if(StringUtility.isNotEmpty(getModel()) && StringUtility.isNotEmpty(getManufacturer()))
		{
			data += ", Product = " + getModel();
			data += ", Manufacturer = " + getManufacturer();
		}

		data += ", Class Id = " + getClassAssociation();
		
		data += "]";
		
		return data;
	}

	public String toFullString()
	{
		String data = toString();

		if(StringUtility.isNotEmpty(getEntryId())) data += " - " + getEntryId();

		return data;
	}

	public boolean isIncidentFlag()
	{
		return incidentFlag;
	}

	public void setIncidentFlag()
	{
		this.incidentFlag = true;
	}

	public boolean isProblemFlag()
	{
		return problemFlag;
	}

	public void setProblemFlag()
	{
		this.problemFlag = true;
	}

	public boolean isWorkorderFlag()
	{
		return workorderFlag;
	}

	public void setWorkorderFlag()
	{
		this.workorderFlag = true;
	}

	public boolean isAssetFlag()
	{
		return assetFlag;
	}

	public void setAssetFlag()
	{
		this.assetFlag = true;
	}

	public boolean isRequestFlag()
	{
		return requestFlag;
	}

	public void setRequestFlag()
	{
		this.requestFlag = true;
	}

	public boolean isChangeFlag()
	{
		return changeFlag;
	}

	public void setChangeFlag()
	{
		this.changeFlag = true;
	}

	public void setAllFlags()
	{
		this.changeFlag = true;
		this.requestFlag = true;
		this.assetFlag = true;
		this.problemFlag = true;
		this.incidentFlag = true;
		this.purchaseFlag = true;
		this.releaseFlag = true;
	}

	public boolean isPurchaseFlag()
	{
		return purchaseFlag;
	}

	public void setPurchaseFlag()
	{
		this.purchaseFlag = true;
	}

	public boolean isReleaseFlag()
	{
		return releaseFlag;
	}

	public void setReleaseFlag()
	{
		this.releaseFlag = true;
	}
}
