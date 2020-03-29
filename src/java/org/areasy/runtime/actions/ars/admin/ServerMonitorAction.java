package org.areasy.runtime.actions.ars.admin;

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

import org.areasy.common.support.configuration.Configuration;
import org.areasy.common.support.configuration.base.BaseConfiguration;
import org.areasy.runtime.RuntimeAction;
import org.areasy.runtime.engine.RuntimeLogger;
import org.areasy.runtime.engine.base.ServerConnection;

/**
 * Dedicated action to monitor if server instances are working.
 */
public class ServerMonitorAction extends AbstractSystemMonitor implements RuntimeAction
{
	/**
	 * Execute action's for monitoring.
	 *
	 * @return true of false in case of monitoring procedure observed an error or not
	 */
	protected boolean monitor()
	{
		boolean error = false;
		String arserver = getConfiguration().getString("arserver", getManager().getConfiguration().getString("app.server.default.arsystem.server.name", "localhost"));

		//check primary server
		try
		{
			setServerConnection();
			RuntimeLogger.info("Active connection for '" + arserver + "' primary AR server");
		}
		catch (Throwable th)
		{
			error = true;
			RuntimeLogger.error("Invalid connection for '" + arserver + "' primary AR server: " + th.getMessage());

			//mitigate error
			execThwartAction(0);
		}

		boolean found = true;
		int index = 1;

		do
		{
			ServerConnection connection = null;
			String server = getConfiguration().getString("arremoteserver" + index, null);

			if (server != null)
			{
				String aruser = getConfiguration().getString("arremoteuser" + index, getConfiguration().getString("aruser", getManager().getConfiguration().getString("app.server.default.arsystem.user.name", null)));
				String arpassword = getConfiguration().getString("arremotepassword" + index, getConfiguration().getString("arpassword", getManager().getConfiguration().getString("app.server.default.arsystem.user.password", null)));
				int arport = getConfiguration().getInt("arremoteport" + index, getConfiguration().getInt("arport", getManager().getConfiguration().getInt("app.server.default.arsystem.port.number", 0)));

				Configuration configuration = new BaseConfiguration();
				configuration.setKey("server.name", server);
				configuration.setKey("user.name", aruser);
				configuration.setKey("user.password", arpassword);
				configuration.setKey("port.number", String.valueOf(arport));

				try
				{
					connection = new ServerConnection();
					connection.connect(configuration);

					RuntimeLogger.info("Active connection for '" + server + "' AR server node");
				}
				catch (Throwable th)
				{
					error = true;
					RuntimeLogger.error("Invalid connection for '" + server + "' AR server node: " + th.getMessage());

					//mitigate error
					execThwartAction(index);
				}
			}
			else found = false;

			index++;
		}
		while (found);

		return error;
	}

	/**
	 * Get notification subject
	 *
	 * @return notification subject
	 */
	protected String getDefaultMessageSubject()
	{
		return "AR Server(s) Monitoring Alert";
	}


	/**
	 * This method should be called when an error is detected!
	 * 
	 * @param event the event id which occurs and could be handled somehow.
	 */
	protected void execThwartAction(int event)
	{
		//nothing to do here
	}

	/**
	 * Get a help text about syntaxt execution of the current action.
	 *
	 * @return text message specifying the syntaxt of the current action
	 */
	public String getHelp()
	{
		return "[-arremoteserver<index> <server_name>] [-arremoteuser<index> <user_name>] [-arremotepassword<index> <user_password>]";
	}
}
