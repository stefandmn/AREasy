package org.areasy.runtime.actions.arserver.data.tools.flow.sources.databases;

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

import org.areasy.runtime.actions.arserver.data.tools.flow.sources.DatabaseSource;
import org.areasy.runtime.engine.base.AREasyException;
import org.areasy.common.data.StringUtility;

/**
 * Dedicated data source to process data from a Derby database (9 and 10).
 * The data-source uses <code>derby</code> driver and libraries. Derby is an open source 100% pure Java (type 4) JDBC 3.0 library.
 */
public class DerbySource extends DatabaseSource
{
	/**
	 * Dedicated method that has to be used internally, to set and validate the data-source configuration (<code>CoreItem</code> structure)
	 *
	 * @throws org.areasy.runtime.engine.base.AREasyException in case of any error will occur
	 */
	public void init() throws AREasyException
	{
		String serverName = getSourceItem().getStringAttributeValue(536870942);
		String serverPort = getSourceItem().getStringAttributeValue(536870944);
		String userName = getSourceItem().getStringAttributeValue(536870948);
		String userPassword = getSourceItem().getStringAttributeValue(536870950);
		String serverDatabase = getSourceItem().getStringAttributeValue(536870952);

		if(serverName == null && serverPort == null)
		{
			String url = "jdbc:derby:" + serverDatabase;
			setConnection(url, "org.apache.derby.jdbc.EmbeddedDriver");
		}
		else
		{
			String url = "jdbc:derby://" + serverName + (StringUtility.isNotEmpty(serverPort) ? ":" + serverPort : "") +
				(StringUtility.isNotEmpty(serverDatabase) ?  "/" + serverDatabase : "");

			setConnection(url, "org.apache.derby.jdbc.ClientDriver", userName, userPassword);
		}

		setQualification( getSourceItem().getStringAttributeValue(536870953) );
	}
}
