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
 * Dedicated data source to process data from a Microsoft SQL Server database (6.5, 7, 2000, 2005 and 2008).
 * The data-source uses <code>jtds</code> driver and libraries. jTDS is an open source 100% pure Java (type 4) JDBC 3.0 library.
 */
public class MSSQLSource extends DatabaseSource
{
	/**
	 * Dedicated method that has to be used internally, to set and validate the data-source configuration (<code>CoreItem</code> structure)
	 *
	 * @throws org.areasy.runtime.engine.base.AREasyException in case of any error will occur
	 */
	public void init() throws AREasyException
	{
		String serverName = getSourceItem().getStringAttributeValue(536870947);
		String serverPort = getSourceItem().getStringAttributeValue(536870951);
		String userName = getSourceItem().getStringAttributeValue(536870945);
		String userPassword = getSourceItem().getStringAttributeValue(536870946);
		String serverDatabase = getSourceItem().getStringAttributeValue(536870968);
		String serverInstance = getSourceItem().getStringAttributeValue(536870974);

		String url = "jdbc:jtds:sqlserver://" + serverName + (StringUtility.isNotEmpty(serverPort) ? ":" + serverPort : "") +
			(StringUtility.isNotEmpty(serverDatabase) ?  "/" + serverDatabase : "") +
			(StringUtility.isNotEmpty(serverInstance) ? ";" + serverInstance : "");

		setConnection(url, "net.sourceforge.jtds.jdbc.Driver", userName, userPassword);
		setQualification( getSourceItem().getStringAttributeValue(536870989) );
	}
}
