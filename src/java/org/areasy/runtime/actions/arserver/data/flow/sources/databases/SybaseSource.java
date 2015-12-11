package org.areasy.runtime.actions.arserver.data.flow.sources.databases;

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

import org.areasy.runtime.actions.arserver.data.flow.sources.DatabaseSource;
import org.areasy.runtime.engine.base.AREasyException;
import org.areasy.common.data.StringUtility;

/**
 * Dedicated data source to process data from a Sybase database (10, 11, 12 and 15).
 * The data-source uses <code>jconnect or jtds</code> drivers and libraries.
 */
public class SybaseSource extends DatabaseSource
{
	/**
	 * Dedicated method that has to be used internally, to set and validate the data-source configuration (<code>CoreItem</code> structure)
	 *
	 * @throws org.areasy.runtime.engine.base.AREasyException in case of any error will occur
	 */
	public void init() throws AREasyException
	{
		Class clazz = null;

		try
		{
			clazz = Class.forName("com.sybase.jdbc4.jdbc.SybDriver");
		}
		catch(ClassNotFoundException cnfe)
		{
			clazz = null;
		}

		String serverName = getSourceItem().getStringAttributeValue(536870933);
		String serverPort = getSourceItem().getStringAttributeValue(536870934);
		String userName = getSourceItem().getStringAttributeValue(536870935);
		String userPassword = getSourceItem().getStringAttributeValue(536870936);
		String serverDatabase = getSourceItem().getStringAttributeValue(536870937);

		if(clazz == null)
		{
			String url = "jdbc:jtds:sybase://" + serverName + (StringUtility.isNotEmpty(serverPort) ? ":" + serverPort : "") +
				(StringUtility.isNotEmpty(serverDatabase) ?  "/" + serverDatabase : "");

			setConnection(url, "net.sourceforge.jtds.jdbc.Driver", userName, userPassword);
		}
		else
		{
		    String url = "jdbc:sybase:Tds:" + serverName + (StringUtility.isNotEmpty(serverPort) ? ":" + serverPort : "") +
				(StringUtility.isNotEmpty(serverDatabase) ?  "/" + serverDatabase : "");

			setConnection(url, clazz.getName(), userName, userPassword);
		}

		setQualification( getSourceItem().getStringAttributeValue(536870938) );
	}
}
