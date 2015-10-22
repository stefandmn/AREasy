package org.areasy.runtime.actions.arserver.data.tools.sources;

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

import org.areasy.runtime.engine.base.AREasyException;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Null data source that allows AAR workflow to execute AAR events only with constant mapping. The source provide only a
 * counter to specify very clear the total number of records that have to be processed based on this DataSource.
 */
public class NoDataSource extends AbstractSource
{
	int numberOfRecords = 1;
	int index = 0;

	/**
	 * Dedicated method that has to be used internally, to set and validate the data-source configuration (<code>CoreItem</code> structure)
	 *
	 * @throws org.areasy.runtime.engine.base.AREasyException in case of any error will occur
	 */
	public void init() throws AREasyException
	{
		Object object = getSourceItem().getAttributeValue(536870917);
		if(object != null) numberOfRecords = ((Integer)object).intValue();
	}

	/**
	 * Dedicated method to release resources that are used by a data-source
	 *
	 * @throws AREasyException in case of any error will occur
	 */
	public void release() throws AREasyException
	{
		//nothing to do here
	}

	/**
	 * Take and deliver through a <code>Map</code> structure the data headers from the selected data-source.
	 *
	 * @return a <code>Map</code> with data-source headers.
	 * @throws AREasyException in case of any error will occur
	 */
	public Map getHeaders() throws AREasyException
	{
		return null;
	}

	/**
	 * Take and deliver through a <code>Map</code> structure the data read it from
	 * the selected data-source. If the output is null means that the data-source goes to the end.
	 *
	 * @param list this is the list of data source keys.
	 * @return a <code>Map</code> having data source indexes as keys and data as values.
	 * @throws AREasyException in case of any error will occur
	 */
	public Map getNextObject(List list) throws AREasyException
	{
		Map map = null;

		if(index < numberOfRecords)
		{
			index++;

			map = new HashMap();

			for(int i = 0; i < list.size(); i++)
			{
				String colName = (String) list.get(i);
				map.put(colName, null);
			}
		}

		return map;
	}

	/**
	 * Read and return the total number of records found in the data-source.
	 *
	 * @return number of records found
	 */
	public int getDataCount()
	{
		return numberOfRecords;
	}
}
