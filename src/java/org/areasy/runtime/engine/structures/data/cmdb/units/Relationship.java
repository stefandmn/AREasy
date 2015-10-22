package org.areasy.runtime.engine.structures.data.cmdb.units;

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

import com.bmc.arsys.api.Entry;
import org.areasy.common.data.StringUtility;
import org.areasy.runtime.engine.base.AREasyException;
import org.areasy.runtime.engine.base.ServerConnection;
import org.areasy.runtime.engine.structures.CoreItem;
import org.areasy.runtime.engine.structures.data.cmdb.ConfigurationItem;
import org.areasy.runtime.engine.workflows.ProcessorLevel1Context;

import java.util.List;

/**
 * This class manages the CI relationships.
 */
public class Relationship extends CoreItem
{
	private boolean inventory = false;
	private boolean isRealFormName = true;

	private ConfigurationItem source = null;
	private ConfigurationItem destination = null;

	/**
	 * Default CI relationship instance.
	 */
	public Relationship()
	{
		//nothing to do here
		super();

		setFormName("BMC.CORE:BMC_BaseRelationship");
	}

	public void setFormName(String formName)
	{
		isRealFormName = !StringUtility.equals("BMC.CORE:BMC_BaseRelationship", formName);
		super.setFormName(formName);
	}

	/**
	 * Find all relationships that are not deleted, where the specified CI (instance id) is destination or source
	 *
	 * @param arsession  user session
	 * @param instanceId CI instance id
	 * @return a list for relationship records
	 * @throws AREasyException if any error will occur
	 */
	public List searchAllRelationships(ServerConnection arsession, String instanceId) throws AREasyException
	{
		String qualification = "('490008000' = \"" + instanceId + "\" OR '490009000' = \"" + instanceId + "\") AND ('400129100' = $NULL$ OR '400129100' = 0)";

		return search(arsession, qualification);
	}

	/**
	 * Create a new instance of core item structure.
	 *
	 * @return new instance of <code>CoreItem</code> structure
	 */
	public CoreItem getInstance()
	{
		return new Relationship();
	}

	protected void fetch(ServerConnection arsession, Entry entry) throws AREasyException
	{
		super.fetch(arsession, entry);

		String relationKeyword = getStringAttributeValue(400079600);
		String relationInstaceId = getStringAttributeValue(179);

		if(!isRealFormName)
		{
			String formName = null;
			CoreItem item = new CoreItem();

			try
			{
				item.setFormName(ProcessorLevel1Context.FORM_SHRSCHEMANAMES);
				item.setAttribute(230000009, relationKeyword);

				item.read(arsession);

				if(item.exists()) formName = item.getStringAttributeValue(301170700);
			}
			catch(Throwable th)
			{
				logger.debug("Error reading form definition using Asset Management dictionary: " + th.getMessage());
			}

			if(formName == null)
			{
				item = new CoreItem();
				item.setFormName(ProcessorLevel1Context.FORM_OBJSTRCLASS);
				item.setAttribute(179, relationKeyword);

				item.read(arsession);

				if(item.exists()) formName = item.getStringAttributeValue(400130800);
			}


			if(formName == null) throw new AREasyException("Relationship type '" + relationKeyword + "' does not have dedicated AR form");
			else
			{
				setFormName(formName);
				clear();

				setAttribute(179, relationInstaceId);
				read(arsession);

				//fetch data
				if(!exists()) throw new AREasyException("Relationship type '" + relationKeyword + "' could not be found in '" + formName + "' using '" + relationInstaceId + "' instance id");
			}

			//discover source and destination
			source = new ConfigurationItem();
			source.setClassId( getStringAttributeValue(490008100) );
			source.setInstanceId( getStringAttributeValue(490008000) );
			source.read(arsession);

			destination = new ConfigurationItem();
			destination.setClassId( getStringAttributeValue(490009100) );
			destination.setInstanceId( getStringAttributeValue(490009000) );
			destination.read(arsession);

			if(StringUtility.equals(source.getClassId(), "BMC_INVENTORYSTORAGE")) this.inventory = true;
		}
		else throw new AREasyException("Invalid relationship type: " + relationKeyword + ". This relationship is not registered");
	}

	public String getInstanceId()
	{
		return getStringAttributeValue(179);
	}

	public String getReconciliationInstanceId()
	{
		return getStringAttributeValue(400129200);
	}

	public String getDataset()
	{
		return getStringAttributeValue(400129200);
	}

	public String getClassId()
	{
		return getStringAttributeValue(400079600);
	}

	public String getName()
	{
		return getStringAttributeValue(200000020);
	}

	public ConfigurationItem getSourceConfigurationItem()
	{
		return this.source;
	}

	public ConfigurationItem getDestinationConfigurationItem()
	{
		return this.destination;
	}

	/**
	 * This method can say if the current relationship is an inventory relationship (with a <code>BMC_INVENTORYSTORAGE</code> class) or not
	 *
	 * @return true if it is a relationship class
	 */
	public boolean isInventory()
	{
		return this.inventory;
	}
}

