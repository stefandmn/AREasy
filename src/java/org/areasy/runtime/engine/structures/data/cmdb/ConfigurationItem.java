package org.areasy.runtime.engine.structures.data.cmdb;

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

import org.areasy.runtime.engine.base.AREasyException;
import org.areasy.runtime.engine.base.ServerConnection;
import org.areasy.runtime.engine.structures.MultiPartItem;
import org.areasy.runtime.engine.structures.data.itsm.foundation.ProductCategory;
import org.areasy.runtime.engine.workflows.ProcessorLevel1Context;
import org.areasy.runtime.engine.workflows.ProcessorLevel2CmdbApp;
import org.areasy.common.data.StringUtility;
import org.areasy.common.logger.Logger;
import org.areasy.common.logger.LoggerFactory;

import java.util.Hashtable;
import java.util.List;
import java.util.Map;

/**
 * Configuration item structure.
 *
 */
public class ConfigurationItem extends MultiPartItem
{
	protected static Logger logger = LoggerFactory.getLog(ConfigurationItem.class);

	/** This a flag to specify if the CI class will use CMDB or AssetManagement forms */
	private boolean usewrappers = true;

	/**
	 * Default constructor for a general CI.
	 */
	public ConfigurationItem()
	{
		super();
	}

	/**
	 * CI constructor specifying some field to defined attributes for this CI instance.
	 *
	 * @param map mapping with field ids and values.
	 */
	public ConfigurationItem(Map map)
	{
		this();

		setData(map);
	}

	/**
	 * Create a new instance of configuration item structure.
	 *
	 * @return new instance of <code>ConfigurationItem</code> structure
	 */
	public ConfigurationItem getInstance()
	{
		return new ConfigurationItem();
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
	 * Set default attributes for a general CI.
	 */
	protected void setDefault()
	{
		setDefaultClassId(null);
		setDefaultAssetId(null);
		setDefaultInstanceId(null);
		setDefaultReconciliationId(null);
		setDefaultStatus(null);
		setDefaultName(null);
		setDefaultAlias(null);

		if(containsAttributeField(CI_CLASSID)) getAttribute(CI_CLASSID).setLabel("Class ID");
		if(containsAttributeField(CI_ASSETID)) getAttribute(CI_ASSETID).setLabel("CI ID");
		if(containsAttributeField(CI_DATASETID)) getAttribute(CI_DATASETID).setLabel("Dataset ID");
		if(containsAttributeField(CI_INSTANCEID)) getAttribute(CI_INSTANCEID).setLabel("Instance ID");
		if(containsAttributeField(CI_RECONCILIATIONID)) getAttribute(CI_RECONCILIATIONID).setLabel("Reconciliation ID");
		if(containsAttributeField(CI_STATUS)) getAttribute(CI_STATUS).setLabel("Status");
		if(containsAttributeField(CI_NAME)) getAttribute(CI_NAME).setLabel("CI Name");
		if(containsAttributeField(CI_TAGNUMBER)) getAttribute(CI_TAGNUMBER).setLabel("CI Alias");
	}

	/**
	 * Get product category structure from the current CI instance.
	 *
	 * @return <code>ProductCategory</code> structure (not validated)
	 */
	public ProductCategory getProductCategory()
	{
		String category = getCategory();
		String type = getType();
		String item = getItem();
		String model = getModel();
		String manufacturer = getManufacturer();

		ProductCategory product = null;

		if(category != null && type != null && item != null)
		{
			product = new ProductCategory(category, type, item, model, manufacturer);
			product.setClassAssociation(getClassId());
		}

		return product;
	}

	/**
	 * Include all attributes from a ProductCategory structure in this CI instance.
	 *
	 * @param cti code>ProductCategory</code> structure
	 */
	public void setProductCategory(ProductCategory cti)
	{
		setAttribute(CI_CATEGORY, cti.getCategory());
		setAttribute(CI_TYPE, cti.getType());
		setAttribute(CI_ITEM, cti.getItem());
		setAttribute(CI_MODEL, cti.getModel());
		setAttribute(CI_MANUFACTURER, cti.getManufacturer());
	}

	/**
	 * Get product category attribute value.
	 *
	 * @return product category.
	 */
	public String getCategory()
	{
		return getStringAttributeValue(CI_CATEGORY);
	}

	/**
	 * Set product category.
	 *
	 * @param value product category
	 */
	public void setCategory(String value)
	{
		setAttribute(CI_CATEGORY, value);
	}

	/**
	 * Get product type.
	 *
	 * @return product type
	 */
	public String getType()
	{
		return getStringAttributeValue(CI_TYPE);
	}

	/**
	 * Set ptoduct type.
	 *
	 * @param value product type
	 */
	public void setType(String value)
	{
		setAttribute(CI_TYPE, value);
	}

	/**
	 * Get product item.
	 *
	 * @return product item
	 */
	public String getItem()
	{
		return getStringAttributeValue(CI_ITEM);
	}

	/**
	 * Set product item.
	 *
	 * @param value product item
	 */
	public void setItem(String value)
	{
		setAttribute(CI_ITEM, value);
	}

	/**
	 * Get product model.
	 *
	 * @return product model (product name)
	 */
	public String getModel()
	{
		return getStringAttributeValue(CI_MODEL);
	}

	/**
	 * Set product model.
	 *
	 * @param value product model
	 */
	public void setModel(String value)
	{
		setAttribute(CI_MODEL, value);
	}

	/**
	 * Get product manufacturer.
	 *
	 * @return product manufacturer
	 */
	public String getManufacturer()
	{
		return getStringAttributeValue(CI_MANUFACTURER);
	}

	/**
	 * Set product manufacturer.
	 *
	 * @param value product manufacturer
	 */
	public void setManufacturer(String value)
	{
		setAttribute(CI_MANUFACTURER, value);
	}

	/**
	 * Set serial number.
	 *
	 * @param value product manufacturer
	 */
	public void setSerialNumber(String value)
	{
		setAttribute(CI_SERIALNUMBER, value);
	}

	/**
	 * Get serial number.
	 *
	 * @return CI serial number
	 */
	public String getSerialNumber()
	{
		return getStringAttributeValue(CI_SERIALNUMBER);
	}

	/**
	 * Get class dataset id.
	 *
	 * @return class dataset id
	 */
	public String getDatasetId()
	{
		return getStringAttributeValue(CI_DATASETID);
	}

	/**
	 * Set class dataset
	 *
	 * @param value dataset id
	 */
	public void setDefaultDatasetId(String value)
	{
		setDefaultAttribute(CI_DATASETID, value);
	}

	/**
	 * Set class dataset
	 *
	 * @param value dataset id
	 */
	public void setDatasetId(String value)
	{
		setAttribute(CI_DATASETID, value);
	}

	/**
	 * Get class id (class keyword).
	 *
	 * @return class dataset id
	 */
	public String getClassId()
	{
		return getStringAttributeValue(CI_CLASSID);
	}

	/**
	 * Set class keyword
	 *
	 * @param value dataset id
	 */
	public void setDefaultClassId(String value)
	{
		setDefaultAttribute(CI_CLASSID, value);
	}

	/**
	 * Set class keyword
	 *
	 * @param value dataset id
	 */
	public void setClassId(String value)
	{
		setAttribute(CI_CLASSID, value);
	}

	/**
	 * Get configuration item instance id.
	 *
	 * @return instance id
	 */
	public String getInstanceId()
	{
		return getStringAttributeValue(CI_INSTANCEID);
	}

	/**
	 * Set instance id
	 *
	 * @param value instance id
	 */
	public void setDefaultInstanceId(String value)
	{
		setDefaultAttribute(CI_INSTANCEID, value);
	}

	/**
	 * Set instance id
	 *
	 * @param value instance id
	 */
	public void setInstanceId(String value)
	{
		setAttribute(CI_INSTANCEID, value);
	}

	/**
	 * Get configuration item reconciliation id.
	 *
	 * @return reconciliation id
	 */
	public String getReconciliationId()
	{
		return getStringAttributeValue(CI_RECONCILIATIONID);
	}

	/**
	 * Set reconciliation id
	 *
	 * @param value reconciliation id
	 */
	public void setDefaultReconciliationId(String value)
	{
		setDefaultAttribute(CI_RECONCILIATIONID, value);
	}

	/**
	 * Set reconciliation id
	 *
	 * @param value reconciliation id
	 */
	public void setReconciliationId(String value)
	{
		setAttribute(CI_RECONCILIATIONID, value);
	}

	/**
	 * Get configuration item id.
	 *
	 * @return configuration item id
	 */
	public String getAssetId()
	{
		return getStringAttributeValue(CI_ASSETID);
	}

	/**
	 * Set configuration item id.
	 *
	 * @param value configuration item id
	 */
	public void setDefaultAssetId(String value)
	{
		setDefaultAttribute(CI_ASSETID, value);
	}

	/**
	 * Set configuration item id.
	 *
	 * @param value configuration item id
	 */
	public void setAssetId(String value)
	{
		setAttribute(CI_ASSETID, value);
	}

	/**
	 * Set configuration item status.
	 *
	 * @param value configuration item status
	 */
	public void setDefaultStatus(String value)
	{
		setDefaultAttribute(CI_STATUS, value);
	}

	/**
	 * Set configuration item status.
	 *
	 * @param value configuration item status
	 */
	public void setStatus(String value)
	{
		setAttribute(CI_STATUS, value);
	}

	/**
	 * Get configuration item status.
	 *
	 * @return configuration item status
	 */
	public String getStatus()
	{
		return getStringAttributeValue(CI_STATUS);
	}

	/**
	 * Set configuration item name.
	 *
	 * @param value configuration item name
	 */
	public void setDefaultName(String value)
	{
		setDefaultAttribute(CI_NAME, value);
	}

	/**
	 * Set configuration item name.
	 *
	 * @param value configuration item name
	 */
	public void setName(String value)
	{
		setAttribute(CI_NAME, value);
	}

	/**
	 * Get configuration item name.
	 *
	 * @return configuration item name
	 */
	public String getName()
	{
		return getStringAttributeValue(CI_NAME);
	}

	/**
	 * Set configuration item alias (tag number).
	 *
	 * @param value configuration item alias (tag number)
	 */
	public void setDefaultAlias(String value)
	{
		setDefaultAttribute(CI_TAGNUMBER, value);
	}

	/**
	 * Set configuration item alias (tag number).
	 *
	 * @param value configuration item alias (tag number)
	 */
	public void setAlias(String value)
	{
		setAttribute(CI_TAGNUMBER, value);
	}

	/**
	 * Get configuration item alias (tag number).
	 *
	 * @return configuration item alias (tag number)
	 */
	public String getTagNumber()
	{
		return getStringAttributeValue(CI_TAGNUMBER);
	}

	/**
	 * Get associated class form name.
	 *
	 * @return ARS form name.
	 */
	public String getClassForm()
	{
		return getFormName();
	}

	/**
	 * Set class form name
	 *
	 * @param classForm associated form name
	 */
	public void setClassForm(String classForm)
	{
		setFormName(classForm);
	}

	/**
	 * Set class form name, finding it using class id attribute.
	 *
	 * @param arsession user session
	 * @throws AREasyException if the class form was not discovered
	 */
	public void setClassForm(ServerConnection arsession) throws AREasyException
	{
		getClassForm(arsession);
	}

	/**
	 * Set and provide the class form name, finding it using class id attribute.
	 *
	 * @param arsession user session
	 * @return the associated form for this class (configuration instance)
	 * @throws AREasyException if the class form was not discovered
	 */
	protected String getClassForm(ServerConnection arsession) throws AREasyException
	{
		//class type and class form validations.
		if(getClassForm() == null && getClassId() == null)
		{
			logger.debug("Try to discover the form name and class id using '" + ProcessorLevel1Context.FORM_BASEELEMENT + "' class");

			ConfigurationItem searchitem = new ConfigurationItem();
			searchitem.setClassForm(ProcessorLevel1Context.FORM_BASEELEMENT);
			searchitem.setData(getAttributes());

			//read based on the abstract class
			searchitem.read(arsession);

			if(searchitem.exists())
			{
				setClassId(searchitem.getClassId());
				logger.debug("Read class id: " + getClassId());

				if(this.usewrappers) setClassForm( ProcessorLevel2CmdbApp.getSharedFormName(arsession, getClassId()));
					else setClassForm( ProcessorLevel2CmdbApp.getCmdbFormName(arsession, getClassId()));
				logger.debug("Read form name: " + getClassForm());
			}
			else
			{
				ProductCategory cti = searchitem.getProductCategory();
				logger.debug("Reading CTI from base CI structure: " + cti);

				List list = cti.search(arsession);

				if(list.size() > 0)
				{
					setClassId( ((ProductCategory)list.get(0)).getClassAssociation() );
					logger.debug("Read class id by CTI: " + getClassId());

					if(this.usewrappers) setClassForm( ProcessorLevel2CmdbApp.getSharedFormName(arsession, getClassId()));
						else setClassForm( ProcessorLevel2CmdbApp.getCmdbFormName(arsession, getClassId()));
					logger.debug("Read form name by CTI: " + getClassForm());
				}
				else logger.error("Error finding associated form: CI class couldn't be identified");
			}
		}
		else if(getClassForm() == null && getClassId() != null)
		{
			logger.debug("Try to discover the form name using '" + getClassId() + "' class id");

			if(this.usewrappers) setClassForm( ProcessorLevel2CmdbApp.getSharedFormName(arsession, getClassId()));
				else setClassForm( ProcessorLevel2CmdbApp.getCmdbFormName(arsession, getClassId()));

			logger.debug("Discovered form name: " + getClassForm());
		}

		return getClassForm();
	}

	/**
	 * Read actual structure of the current CI using asset id (CI ID, and should be also inventory id).
	 *
	 * @param arsession user session
	 * @throws AREasyException if any error will occur
	 */
	public void readByAssetId(ServerConnection arsession) throws AREasyException
	{
		readByAssetId(arsession, getAssetId());
	}

	/**
	 * Read actual structure of the current CI using asset id (CI ID, and should be also inventory id).
	 *
	 * @param arsession user session
	 * @param assetId asset identifier
	 * @throws AREasyException if any error will occur
	 */
	public void readByAssetId(ServerConnection arsession, String assetId) throws AREasyException
	{
		setAssetId(assetId);

		Map map = new Hashtable();
		map.put(CI_ASSETID,  getAssetId());

		read(arsession, map);
	}

	/**
	 * Read actual structure of the current CI using instance id.
	 *
	 * @param arsession user session
	 * @throws AREasyException if any error will occur
	 */
	public void readByInstanceId(ServerConnection arsession) throws AREasyException
	{
		Map map = new Hashtable();
		map.put(CI_INSTANCEID,  getInstanceId());

		read(arsession, map);
	}

	/**
	 * Read actual structure of the current CI using serial number attribute.
	 *
	 * @param arsession user session
	 * @throws AREasyException if any error will occur
	 */
	public void readBySerialNumber(ServerConnection arsession) throws AREasyException
	{
		readBySerialNumber(arsession, getStringAttributeValue(CI_SERIALNUMBER));
	}

	/**
	 * Read actual structure of the current CI using serial number attribute.
	 *
	 * @param arsession user session
	 * @param serialNumber serial number value to be found
	 * @throws AREasyException if any error will occur
	 */
	public void readBySerialNumber(ServerConnection arsession, String serialNumber) throws AREasyException
	{
		Map map = new Hashtable();
		map.put(CI_SERIALNUMBER, serialNumber);

		read(arsession, map);
	}

	/**
	 * Read actual structure of the current CI using CI alias value (which is comming from tag number or like a custom attribute).
	 *
	 * @param arsession user session
	 * @throws AREasyException if any error will occur
	 */
	public void readByAlias(ServerConnection arsession) throws AREasyException
	{
		readByAlias(arsession, getStringAttributeValue(CI_TAGNUMBER));
	}

	/**
	 * Read actual structure of the current CI using CI alias value (which is comming from tag number or like a custom attribute).
	 *
	 * @param arsession user session
	 * @param cialias CI alias value to be found
	 * @throws AREasyException if any error will occur
	 */
	public void readByAlias(ServerConnection arsession, String cialias) throws AREasyException
	{
		Map map = new Hashtable();
		map.put(CI_TAGNUMBER, cialias);

		read(arsession, map);
	}

	/**
	 * Read actual structure of the current CI using pair with a key and and a value. This key pair should be unique, otherwise will
	 * return an invalid answer.
	 *
	 * @param arsession user session
	 * @param fieldid CI field id
	 * @param fieldvalue field value
	 * @throws AREasyException if any error will occur
	 */
	public void read(ServerConnection arsession, String fieldid, String fieldvalue) throws AREasyException
	{
		Map map = new Hashtable();
		map.put(fieldid,  fieldvalue);

		read(arsession, map);
	}

	/**
	 * Read actual structure of the current CI using lists with fileds and values.
	 *
	 * @param arsession user session
	 * @param fieldids list of field ids
	 * @param fieldvalues list of field values
	 * @throws org.areasy.runtime.engine.base.AREasyException if any error will occur
	 */
	public void read(ServerConnection arsession, List fieldids, List fieldvalues) throws AREasyException
	{
		Map map = new Hashtable();

		for(int i = 0; fieldids != null && fieldvalues != null && i < Math.min(fieldids.size(), fieldvalues.size()); i++)
		{
			String fieldid = (String) fieldids.get(i);
			String fieldvalue = (String) fieldvalues.get(i);

			map.put(fieldid,  fieldvalue);
		}

		read(arsession, map);
	}

	/**
	 * Read data structure and fill all found field attributes. This method will consider only fields defined in this map.
	 *
	 * @param arsession user session
	 * @param map user map with field ids and field values
	 * @throws AREasyException if any error will occur.
	 */
	public void read(ServerConnection arsession, Map map) throws AREasyException
	{
		//put CTI
		if(getCategory() != null) map.put(String.valueOf(CI_CATEGORY), getCategory());
		if(getType() != null) map.put(String.valueOf(CI_TYPE), getType());
		if(getItem() != null) map.put(String.valueOf(CI_ITEM), getItem());
		if(getModel() != null) map.put(String.valueOf(CI_MODEL), getModel());
		if(getManufacturer() != null) map.put(String.valueOf(CI_MANUFACTURER), getManufacturer());

		//put class
		if(getClassId() != null) map.put(String.valueOf(CI_CLASSID), getClassId());

		//validate map
		if(map == null || map.isEmpty()) throw new AREasyException("Search key(s) are null");

		//put data set
		if(getDatasetId() == null) map.put(String.valueOf(CI_DATASETID), ProcessorLevel1Context.DEFAULTDATASET);
			else map.put(String.valueOf(CI_DATASETID), getDatasetId());

		//set and validate the class form
		if(getClassForm(arsession) == null) throw new AREasyException("Associated class form wasn't discovered and the CI couldn't be detected using specified criteria");

		super.read(arsession, map);
	}

	/**
	 * Read data structure and fill all found field attributes. This method will consider only changed field to perform a searching
	 * operation.
	 *
	 * @param arsession user session
	 * @throws AREasyException if any error will occur.
	 */
	public void read(ServerConnection arsession) throws AREasyException
	{
		//set default dataset value
		if(getDatasetId() == null) setDatasetId(ProcessorLevel1Context.DEFAULTDATASET);

		//set and validate the class form
		if(getClassForm(arsession) == null) throw new AREasyException("Associated class form wasn't discovered and the CI couldn't be detected using specified criteria");

		super.read(arsession);
	}

	/**
	 * Update the current configuration item instance based on changed attributes.
	 *
	 * @param arsession user session
	 * @throws AREasyException if any error will occur
	 */
	public void update(ServerConnection arsession) throws AREasyException
	{
		//set and validate the class form
		if(getClassForm(arsession) == null) throw new AREasyException("Associated class form wasn't discovered and the CI couldn't be detected using specified criteria");

		super.update(arsession);
	}

	/**
	 * Create an entry record in the ARS server using core item attributes and then is read it
	 * to transform it into a valid core item instance.
	 *
	 * @param arsession user session
	 * @throws org.areasy.runtime.engine.base.AREasyException if any error will occur
	 */
	public void create(ServerConnection arsession) throws AREasyException
	{
		//set and validate the class form
		if(getClassForm(arsession) == null) throw new AREasyException("Associated class form wasn't discovered and the CI couldn't be detected using specified criteria");

		//set anf force the 'Instance Id' value (if is not already configured)
		if(getInstanceId() == null) setInstanceId(ProcessorLevel2CmdbApp.getStringInstanceId(arsession, "AG"));

		//set default dataset value
		if(getDatasetId() == null) setDatasetId(ProcessorLevel1Context.DEFAULTDATASET);

		//create CI.
		super.create(arsession);

		//check CI for identification
		if(!exists())
		{
			String instanceId = getInstanceId();   

			if(StringUtility.isNotEmpty(instanceId))
			{
				clear();
				ignoreUnchangedValues();
				setInstanceId(instanceId);
				
				super.read(arsession);
			}
		}		
	}

	/**
	 * Search all configuration item structures in the ARS server using this core item template and specified attribute's collection for searching
	 *
	 * @param arsession user session
	 * @param map mapping with field ids and field values.
	 * @return a list with all found <code>CoreItem</code> instances.
	 * @throws AREasyException if any error will occur
	 */
	public List search(ServerConnection arsession, Map map) throws AREasyException
	{
		//set and validate the class form
		getClassForm(arsession);

		return super.search(arsession, map);
	}

	/**
	 * Search all configuration item structures in the ARS server using this core item template and only changed attributes (for searching)
	 *
	 * @param arsession user session
	 * @return a list with all found <code>ConfigurationItem</code> instances.
	 * @throws AREasyException if any error will occur
	 */
	public List search(ServerConnection arsession) throws AREasyException
	{
		//set and validate the class form
		getClassForm(arsession);

		return super.search(arsession);
	}

	/**
	 * Search all core item structures in the ARS server using a string format of the qualification string
	 *
	 * @param arsession user session
	 * @param qualification qualification string
	 * @return a list with all found <code>CoreItem</code> instances.
	 * @throws AREasyException if any error will occur
	 */
	public List search(ServerConnection arsession, String qualification) throws AREasyException
	{
		//set and validate the class form
		getClassForm(arsession);

		return super.search(arsession, qualification);
	}

	/**
	 * Search all core item structures in the ARS server using a string format of the qualification string
	 *
	 * @param arsession user session
	 * @param qualification qualification string
	 * @return a list with all found <code>CoreItem</code> instances.
	 * @param maxlimit maximum limit for search
	 * @throws AREasyException if any error will occur
	 */
	public List search(ServerConnection arsession, String qualification, int maxlimit) throws AREasyException
	{
		//set and validate the class form
		getClassForm(arsession);

		return super.search(arsession, qualification, maxlimit);
	}

	public String toString()
	{
		String data = "Configuration Item (" + (getClassId() != null ? getClassId() : getFormName()) + ") [ Name = " + getName();

		if(StringUtility.isNotEmpty(getAssetId())) data += ", Asset Id = " + getAssetId();
		if(StringUtility.isNotEmpty(getTagNumber())) data += ", Tag Number = " + getTagNumber();
		if(StringUtility.isNotEmpty(getDatasetId())) data += ", Dataset Id = " + getDatasetId();
		if(StringUtility.isNotEmpty(getCategory())) data += ", Category = " + getCategory();
		if(StringUtility.isNotEmpty(getType())) data += ", Type = " + getType();
		if(StringUtility.isNotEmpty(getItem())) data += ", Item = " + getItem();
		if(StringUtility.isNotEmpty(getModel())) data += ", Product = " + getModel();
		if(StringUtility.isNotEmpty(getManufacturer())) data += ", Manufacturer = " + getManufacturer();
		if(StringUtility.isNotEmpty(getSerialNumber())) data += ", Serial Number = " + getSerialNumber();
		if(StringUtility.isNotEmpty(getInstanceId())) data += ", Instance Id = " + getInstanceId();
		if(StringUtility.isNotEmpty(getReconciliationId())) data += ", Reconciliation Id = " + getReconciliationId();

		data += "]";

		return data;
	}

	public String toLongString()
	{
		String data = toString();

		if(StringUtility.isNotEmpty(getEntryId())) data += " - " + getEntryId();

		return data;
	}

	public String toFullString()
	{
		String data = toLongString();

		List ids = getAttributeIds();
		for(int i = 0; i < ids.size(); i++)
		{
			String id = (String) ids.get(i);
			String value = getStringAttributeValue(id);

			data +="\n- \t" + id + " = " + value;
		}

		return data;
	}

	public void useAssetManagement()
	{
		this.usewrappers = true;
	}

	public void useConfigurationManagement()
	{
		this.usewrappers = false;
	}
}

