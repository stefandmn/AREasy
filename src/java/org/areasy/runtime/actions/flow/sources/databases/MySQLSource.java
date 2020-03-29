package org.areasy.runtime.actions.flow.sources.databases;

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
import org.areasy.runtime.actions.flow.sources.DatabaseSource;
import org.areasy.runtime.engine.base.AREasyException;

/**
 * Dedicated data source to process data from a MySQL Server database.
 * The data-source uses <code>MySQL</code> native driver and libraries.
 */
public class MySQLSource extends DatabaseSource
{
	/**
	 * Dedicated method that has to be used internally, to set and validate the data-source configuration (<code>CoreItem</code> structure)
	 *
	 * @throws org.areasy.runtime.engine.base.AREasyException in case of any error will occur
	 */
	public void init() throws AREasyException
	{
		String serverName = getSourceItem().getStringAttributeValue(536871014);
		String serverPort = getSourceItem().getStringAttributeValue(536871015);
		String userName = getSourceItem().getStringAttributeValue(536871016);
		String userPassword = getSourceItem().getStringAttributeValue(536871017);
		String serverDatabase = getSourceItem().getStringAttributeValue(536871018);

		String url = "jdbc:mysql://" + serverName + (StringUtility.isNotEmpty(serverPort) ? ":" + serverPort : "") + "/" + serverDatabase;

		setConnection(url, "com.mysql.jdbc.Driver", userName, userPassword);
		setQualification( getSourceItem().getStringAttributeValue(536871019) );
	}
}
