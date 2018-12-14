package org.areasy.runtime.actions.itsm.data;

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

import org.areasy.runtime.engine.RuntimeLogger;
import org.areasy.runtime.engine.base.ARDictionary;
import org.areasy.runtime.engine.base.AREasyException;
import org.areasy.runtime.engine.structures.data.cmdb.ConfigurationItem;
import org.areasy.runtime.engine.structures.data.itsm.foundation.ProductCategory;

import java.util.Map;

/**
 * Update an existent configuration item data instance into CMDB application.
 *
 */
public class ConfigurationItemUpdateAction extends ConfigurationItemCreateAction implements ConfigurationItemAction
{
	/**
	 * Update an existent configuration item data instance into CMDB application.
	 *
	 * @throws AREasyException if any error will occur
	 */
	public void run(ConfigurationItem item) throws AREasyException
	{
		//define transaction.
		if(item != null && item.exists())
		{
			boolean commitrelated = getConfiguration().getBoolean("commitrelated", false);
			boolean ignorenullvalue = getConfiguration().getBoolean("ignorenullvalues", true);
			boolean ignoreunchangedvalues = getConfiguration().getBoolean("ignoreunchangedvalues", true);

			//define keys mapping
			Map fmap = getDataFields();
			Map lmap = getDataMap();

			//include additional attributes to be saved.
			if(fmap != null && fmap.size() > 0) item.setData(fmap);
			else if(lmap != null && lmap.size() > 0) item.setData(lmap);

			//validate and manage CTI enforcement.
			if(commitrelated)
			{
				ProductCategory cti = item.getProductCategory();

				if(cti != null)
				{
					//read cti
					cti.read(getServerConnection());

					if(!cti.exists())
					{
						cti.setClassAssociation(item.getClassId());
						cti.setAttribute(ARDictionary.PCT_STATUS, new Integer(1));
						cti.setAttribute(ARDictionary.PCT_ORIGIN, new Integer(1));
						cti.setAttribute(ARDictionary.PCT_SUITEDEF, new Integer(0));
						cti.setAttribute(ARDictionary.PCT_COMPANYNAME, item.getStringAttributeValue(ARDictionary.CI_COMPANYNAME));
						
						cti.create(getServerConnection());
					}
				}
			}

			//set validation flags
			item.setIgnoreNullValues(ignorenullvalue);
			item.setIgnoreUnchangedValues(ignoreunchangedvalues);

			//update current item
			item.update(getServerConnection());
			RuntimeLogger.debug("CI has been updated: " + item);
		}
		else if(item != null && !item.exists())
		{
			super.run(item);
		}
	}
}
