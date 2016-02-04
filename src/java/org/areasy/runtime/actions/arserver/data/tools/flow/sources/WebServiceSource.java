package org.areasy.runtime.actions.arserver.data.tools.flow.sources;

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

import org.areasy.runtime.engine.base.AREasyException;

import java.util.List;
import java.util.Map;

/**
 * todo - implementation.
 * This is a data-source that should read data structures and related data directly from a web service provided by an
 * URL.
 */
public class WebServiceSource extends AbstractSource
{
	/**
	 * Dedicated method that has to be used internally, to set and validate the data-source configuration (<code>CoreItem</code> structure)
	 *
	 * @throws org.areasy.runtime.engine.base.AREasyException in case of any error will occur
	 */
	public void init() throws AREasyException
	{

	}

	/**
	 * Dedicated method to release resources that are used by a data-source
	 *
	 * @throws AREasyException in case of any error will occur
	 */
	public void release() throws AREasyException
	{

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
		return null;
	}

	/**
	 * Read and return the total number of records found in the data-source.
	 * This input have to be optimized for long data-sources
	 *
	 * @return number of records found
	 */
	public int getDataCount()
	{
		return 0;
	}
}
