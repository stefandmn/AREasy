package org.areasy.runtime.actions.arserver.data.tools.events;

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
import org.areasy.runtime.engine.structures.CoreItem;

import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * Dedicated event for data import action to extract data-source headers, to use them in the
 * data mapping process.
 */
public class ReadHeadersEvent extends AbstractEvent
{
	/**
	 * Execute event
	 *
	 * @throws AREasyException if any error occurs
	 */
	public void execute() throws AREasyException
	{
		//disable notification
		getAction().getConfiguration().setKey("notification", "false");

		// validate data-source
		if(getSource() == null) throw new AREasyException("Data-source is null. Check if -instanceid option has been specified and defined!");

		//remove existing map records
		resetSourceSelection();

		//get data-source headers map
		Map map = getSource().getHeaders();

		//record found headers map
		setSourceSelection(map);

		//get number of records and display this info in AAR interface
		int count = getSource().getDataCount();
		if(count > 1) RuntimeLogger.info("Found " + count + " records!");
			else if(count == 1) RuntimeLogger.info("Found " + count + " record!");
				else if(count <= 0) RuntimeLogger.warn("Unknown number of records in the selected data-source!");
	}

	/**
	 * Remove all records that corresponds to the current user and to the current data-source from "SNT:AAR:DMI:Source Selection" form.
	 *
	 * @throws org.areasy.runtime.engine.base.AREasyException is any error occur
	 */
	protected void resetSourceSelection() throws AREasyException
	{
		CoreItem sourcemap = new CoreItem();
		sourcemap.setFormName(ARDictionary.FORM_DATA_PARSER);
		sourcemap.setAttribute(179, getSource().getSourceItem().getStringAttributeValue(179));

		List values = sourcemap.search(getAction().getServerConnection());

		for(int i = 0; values != null && i < values.size(); i++)
		{
			CoreItem item = (CoreItem) values.get(i);
			item.setAttribute(8, "DELETE");
			item.update(getAction().getServerConnection());
		}
	}

	/**
	 * Record source headers (source map structure) in "SNT:AAR:DMI:Source Selection" form.
	 *
	 * @param map source map structure
	 * @throws AREasyException is any error occur
	 */
	protected void setSourceSelection(Map map) throws AREasyException
	{
		if(map != null)
		{
			Iterator iterator = map.keySet().iterator();

			while(iterator != null && iterator.hasNext())
			{
				CoreItem sourcemap = new CoreItem();
				sourcemap.setFormName(ARDictionary.FORM_DATA_PARSER);
				sourcemap.setAttribute(2, getAction().getServerConnection().getUserName());
				sourcemap.setAttribute(179, getSource().getSourceItem().getStringAttributeValue(179));

				String fieldName = (String) iterator.next();
				String fieldId = (String) map.get(fieldName);

				sourcemap.setAttribute(536870913, fieldName);
				sourcemap.setAttribute(536870915, fieldId);

				sourcemap.create(getAction().getServerConnection());
			}
		}
	}
}
