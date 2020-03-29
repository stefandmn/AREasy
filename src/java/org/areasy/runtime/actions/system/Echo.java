package org.areasy.runtime.actions.system;

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
import org.areasy.runtime.RuntimeAction;
import org.areasy.runtime.actions.SystemAction;
import org.areasy.runtime.engine.RuntimeLogger;
import org.areasy.runtime.engine.base.AREasyException;
import org.areasy.runtime.engine.base.ServerConnection;

import java.net.InetAddress;
import java.util.Iterator;
import java.util.List;

/**
 * Processing echo internal command - echo server execution.
 */
public class Echo extends SystemAction implements RuntimeAction
{
	/**
	 * Execute 'echo' action.
	 * Processing echo internal command - echo server execution.
	 *
	 * @throws org.areasy.runtime.engine.base.AREasyException if any error will occur.
	 */
	public void run() throws AREasyException
	{
		String localHost = null;
		String localPort = null;

		if(getConfiguration().getBoolean("arsystem", false))
		{
			try
			{
				this.setServerConnection();
				localHost = getServerConnection().getUserName() + "@" + getServerConnection().getServerName();
				localPort = String.valueOf(getServerConnection().getServerPort());

				//processing echo command - echo server execution.
				RuntimeLogger.info("Echo from AR System instance " + localHost + ":" + localPort);
			}
			catch (Exception e)
			{
				//processing echo command - echo server execution.
				RuntimeLogger.error("No echo from AR System instance: " + e.getMessage());
			}
		}
		else
		{
			try
			{
				localHost = InetAddress.getLocalHost().getHostAddress();
			}
			catch (Exception e)
			{
				if (getServer() != null) localHost = getServer().getManager().getConfiguration().getString("app.server.host", null);
					else localHost = getManager().getConfiguration().getString("app.server.host", null);
			}

			//get server port
			if(getServer() != null) localPort = getServer().getManager().getConfiguration().getString("app.server.port", "0");
				else localPort = getManager().getConfiguration().getString("app.server.port", "0");

			//processing echo command - echo server execution.
			RuntimeLogger.info("Echo from AREasy instance: " + localHost + ":" + localPort);
		}

		//inout parameter implementation
		if(getConfiguration().getBoolean("inout", false))
		{
			Iterator iterator = getConfiguration().getKeys();

			if(iterator != null)
			{
				StringBuilder buffer = new StringBuilder("Input-output parameters list: \n");

				while(iterator.hasNext())
				{
					String param = (String) iterator.next();
					String value = null;

					List list = getConfiguration().getList(param, null);
					if(list != null) value = StringUtility.join( list.toArray(new String[list.size()]), ", ");
					buffer.append("\t\t\t\t").append(param).append(" = ").append(StringUtility.trim(value)).append("\n");
				}


				RuntimeLogger.info(buffer.toString());
			}
		}

		//signal parameter implementation
		if(getConfiguration().getBoolean("signal", false))
		{
			RuntimeLogger.clearData();
			RuntimeLogger.add(String.valueOf(0));
		}
	}
}
