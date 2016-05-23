package org.areasy.runtime.plugins.area;

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

import com.bmc.arsys.api.ARException;
import com.bmc.arsys.pluginsvr.plugins.AREAResponse;
import com.bmc.arsys.pluginsvr.plugins.ARPluginContext;
import org.areasy.common.data.StringUtility;
import org.areasy.common.logger.Logger;
import org.areasy.common.logger.LoggerFactory;

/**
 * Abstract definition of AREA plugin actions
 *
 */
public abstract class AbstractArea
{
	/** Library logger */
	protected static Logger logger = LoggerFactory.getLog(AbstractArea.class);

	private RuntimeArea runtime = null;

	public final void setRuntimeArea(RuntimeArea runtime)
	{
		this.runtime = runtime;
	}

	/**
	 * Initialize AREA plugin
	 *
	 * @param context AREA context
	 * @throws ARException if any error will occur
	 */
	public void open(ARPluginContext context) throws ARException
	{
		//nothing to do now
	}

	/**
	 * This method must be implemented in order to define authentication process for this AREA plugin module.
	 *
	 * @param context plugin context
	 * @param response AREA response
	 * @param user user name
	 * @param password user password
	 * @param networkAddress network address
	 * @param authString authentication string
	 * @throws ARException if any error occur
	 */
	protected abstract int call(ARPluginContext context, AREAResponse response, String user, String password, String networkAddress, String authString) throws ARException;

	/**
	 * Close operation for each specialized plugin when <code>terminate</code> call is executed
	 *
	 * @param context AREA context
	 * @throws ARException when any error will occur
	 */
	public void close(ARPluginContext context) throws ARException
	{
		//nothing to do now
	}

	/**
	 * This method must return a unique name for this plugin.
	 * @return AREA plugin name
	 */
	public abstract String getSignature();

	/**
	 * Get AREA plugin dispatcher
	 *
	 * @return <code>RuntimeArea</code> plugin instance
	 */
	protected RuntimeArea getRuntimeArea()
	{
		return this.runtime;
	}

	protected void authFailedAnswer(AREAResponse response, String message, String user) throws ARException
	{
		response.setLoginStatus(AREAResponse.AREA_LOGIN_FAILED);
		response.setMessageText(message);

		String logMsg = "Authentication failed for '" + user + "'";
		String logText = (message != null ? message  : "");

		if(StringUtility.isEmpty(logText)) logText = logMsg;
			else if(logText.endsWith(".")) logText += " " + logMsg;
				else logText += ". " + logMsg;

		response.setLogText(logText);
		warn(logText);
	}

	protected void authSucceededAnswer(AREAResponse response, String message, String user) throws ARException
	{
		response.setLoginStatus(AREAResponse.AREA_LOGIN_SUCCESS);
		response.setMessageText(null);

		String logMsg = "Authentication succeeded for '" + user + "'";
		String logText = (message != null ? message  : "");

		if(StringUtility.isEmpty(logText)) logText = logMsg;
			else if(logText.endsWith(".")) logText += " " + logMsg;
				else logText += ". " + logMsg;

		response.setLogText(logText);
		info(logText);
	}

	public void debug(String message)
	{
		getRuntimeArea().debug(message);
	}

	public void debug(String message, Throwable th)
	{
		getRuntimeArea().debug(message, th);
	}

	public void debug(Throwable th)
	{
		getRuntimeArea().debug(th);
	}

	public void info( String message)
	{
		getRuntimeArea().info(message);
	}

	public void warn(String message)
	{
		getRuntimeArea().warn(message);
	}

	public void error(String message)
	{
		getRuntimeArea().error(message);
	}

	public void error(String message, Throwable th)
	{
		getRuntimeArea().error(message, th);
	}

	public void info(ARPluginContext context, String message)
	{
		getRuntimeArea().info(context, message);
	}

	public void warn(ARPluginContext context, String message)
	{
		getRuntimeArea().warn(context, message);
	}

	public void error(ARPluginContext context, String message)
	{
		getRuntimeArea().error(context, message);
	}

	public void error(ARPluginContext context, String message, Throwable th)
	{
		getRuntimeArea().error(context, message, th);
	}
}
