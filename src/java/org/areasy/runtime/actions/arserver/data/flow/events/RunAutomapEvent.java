package org.areasy.runtime.actions.arserver.data.flow.events;

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

import org.areasy.runtime.engine.RuntimeLogger;
import org.areasy.runtime.engine.base.ARDictionary;
import org.areasy.runtime.engine.base.AREasyException;
import org.areasy.runtime.engine.structures.Attribute;
import org.areasy.runtime.engine.structures.CoreItem;
import org.areasy.runtime.engine.structures.data.cmdb.ConfigurationItem;
import org.areasy.runtime.engine.workflows.ProcessorLevel1Context;
import org.areasy.common.data.StringUtility;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;

/**
 * Dedicated event to automatically submit the data mapping based on headers' details and
 * target entity selected by AAR end-user.
 */
public class RunAutomapEvent extends AbstractEvent
{
	/**
	 * Execute event
	 *
	 * @throws org.areasy.runtime.engine.base.AREasyException if any error occurs
	 */
	public void execute() throws AREasyException
	{
		//disable notification
		getAction().getConfiguration().setKey("notification", "false");

		// validate data-source
		if(getSource() == null) throw new AREasyException("Data-source is null. Check if -instanceid option has been specified and defined!");

		String targetArea = getAction().getConfiguration().getString("targetarea", null);
		String targetType = getAction().getConfiguration().getString("targettype", null);

		//get target entity
		CoreItem item = getTarget(targetArea, targetType);

		//create mapping
		setAutoMapping(item);
	}

	protected CoreItem getTarget(String targetArea, String targetType) throws AREasyException
	{
		CoreItem item = null;

		if(targetArea == null || targetType == null) throw new AREasyException("Unknown target definition. Check if -targetarea and -targettype options have been defined");

		if(StringUtility.equalsIgnoreCase(targetType, "Forms") || StringUtility.equalsIgnoreCase(targetType, "0"))
		{
			item = new CoreItem(targetArea);
		}
		else if(StringUtility.equalsIgnoreCase(targetType, "CI Classes") || StringUtility.equalsIgnoreCase(targetType, "1"))
		{
			item = new ConfigurationItem();
			((ConfigurationItem)item).setClassId(targetArea);
			item.setFormName(ProcessorLevel1Context.getSharedFormName(getAction().getServerConnection(), targetArea));
		}

		return item;
	}

	protected void setAutoMapping(CoreItem entry) throws AREasyException
	{
		if(entry == null)
		{
			RuntimeLogger.warn("Target entity was not recognized!");
			return;
		}

		entry.setAttributes(getAction().getServerConnection());
		Collection attributes = entry.getAttributes();

		CoreItem source = new CoreItem();
		source.setFormName(ARDictionary.FORM_DATA_PARSER);
		source.setAttribute(179, getSource().getSourceItem().getStringAttributeValue(179));
		List sourceValues = source.search(getAction().getServerConnection());

		CoreItem keyItem = null;
		int keyRating = 0;

		for(int i = 0; sourceValues != null && i < sourceValues.size(); i++)
		{
			CoreItem item = (CoreItem) sourceValues.get(i);
			if(item == null) continue;

			String sourceFieldId = item.getStringAttributeValue(536870924);
			String sourceName = item.getStringAttributeValue(536870913);
			sourceName = StringUtility.escapeSpecialChars(sourceName).toLowerCase();
			sourceName = StringUtility.replace(sourceName, "_", "");

			Iterator iterator = attributes.iterator();
			while(iterator != null && iterator.hasNext())
			{
				Attribute attr = (Attribute) iterator.next();
				if(attr == null) continue;

				String targetFieldId = attr.getId();
				String targetLabel = attr.getLabel();

				if(targetLabel != null)
				{
					targetLabel = StringUtility.escapeSpecialChars(targetLabel).toLowerCase();
					targetLabel = StringUtility.replace(targetLabel, "_", "");
				}

				if(StringUtility.equals(targetLabel, sourceName) || StringUtility.equals(targetFieldId, sourceFieldId) || StringUtility.equals(targetFieldId, sourceName))
				{
					CoreItem map = new CoreItem();
					map.setFormName(ARDictionary.FORM_DATA_MAPPING);
					map.setAttribute(179, getSource().getSourceItem().getStringAttributeValue(179));
					map.setAttribute(536870950, attr.getId());

					map.read(getAction().getServerConnection());

					map.setAttribute(2, getAction().getServerConnection().getUserName());
					map.setAttribute(536870914, item.getStringAttributeValue(536870913));
					map.setAttribute(536870924, item.getStringAttributeValue(536870915));
					map.setAttribute(536870920, entry instanceof ConfigurationItem ? new Integer(1) : new Integer(0));
					map.setAttribute(536870919, new Integer(0));
					map.setAttribute(536870951, entry instanceof ConfigurationItem ? ((ConfigurationItem) entry).getClassId() : entry.getFormName());
					map.setAttribute(536870990, attr.getLabel());

					if(map.exists()) map.update(getAction().getServerConnection());
						else map.create(getAction().getServerConnection());

					setKeyRating(keyItem, keyRating, map, targetLabel);
				}
			}
		}

		if(keyItem != null)
		{
			keyItem.setAttribute(536870919, new Integer(1));
			keyItem.update(getAction().getServerConnection());
		}
	}

	private void setKeyRating(CoreItem keyItem, int keyRating, CoreItem map, String targetLabel)
	{
		if(targetLabel == null) return;

		if(targetLabel.indexOf("instanceid") >= 0)
		{
			String eval = StringUtility.replace(targetLabel, "instanceid", "*");
			int rating = 1 * eval.length();

			if(keyItem == null)
			{
				keyItem = map;
				keyRating = rating;
			}
			else
			{
				if(rating < keyRating)
				{
					keyItem = map;
					keyRating = rating;
				}
			}
		}
		else if(targetLabel.indexOf("requestid") >= 0)
		{
			String eval = StringUtility.replace(targetLabel, "requestid", "*");
			int rating = 2 * eval.length();

			if(keyItem == null)
			{
				keyItem = map;
				keyRating = rating;
			}
			else
			{
				if(rating < keyRating)
				{
					keyItem = map;
					keyRating = rating;
				}
			}
		}
		else if(targetLabel.indexOf("serialnumber") >= 0)
		{
			String eval = StringUtility.replace(targetLabel, "serialnumber", "*");
			int rating = 3 * eval.length();

			if(keyItem == null)
			{
				keyItem = map;
				keyRating = rating;
			}
			else
			{
				if(rating < keyRating)
				{
					keyItem = map;
					keyRating = rating;
				}
			}
		}
	}
}
