package org.areasy.runtime.plugins.area;

/*
 * Copyright (c) 2007-2018 AREasy Runtime
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
import com.bmc.arsys.api.Constants;
import com.bmc.arsys.pluginsvr.plugins.AREAPlugin;
import com.bmc.arsys.pluginsvr.plugins.AREAResponse;
import com.bmc.arsys.pluginsvr.plugins.ARPluginContext;
import org.areasy.runtime.RuntimeManager;
import org.areasy.common.data.StringUtility;
import org.areasy.common.logger.LoggerFactory;

import java.lang.reflect.Constructor;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.Vector;

/**
 * This is the implementation of AREASY AREA plugin. This implementation extends
 * <code>areaVerifyLogin</code> and <code>areaNeedSync</code> methods
 *
 */
public class RuntimeArea extends AREAPlugin
{
	private static String HOME = null;
	private static String CONF = null;

	/** Runtime manager */
	private static RuntimeManager manager = null;

	/** List of area actions */
	private Map plugins = new Hashtable();

	/**
     * Implementation of the static init method, to do any static initialization for this class.
     * This method is called once for each class that implements one of the <code>ARPluggable</code> interface
     * on plugin server startup
	 *
     * @param context AR plugin context structure
     */
    public static void init(ARPluginContext context)
	{
		HOME = context.getConfigItem("home");
		if(StringUtility.isNotEmpty(HOME)) System.out.println("User defined value for HOME parameter: " + HOME);

		CONF = context.getConfigItem("conf");
		if(StringUtility.isNotEmpty(CONF)) System.out.println("User defined value for CONF parameter: " + CONF);
    }

	/**
	 * Implementation of the initialization method, to do any static initialization for this class.
	 * This method is called once for each class that implements one of the <code>ARPluggable</code> interface
	 * on plugin server startup
	 *
	 * @param context AR plugin context structure
	 */
	public void initialize(ARPluginContext context) throws ARException
	{
		super.initialize(context);
		info(context, "Reading AREasy AREA plugin configuration");

		try
		{
			if(getManager() == null)
			{
				if(StringUtility.isNotEmpty(HOME)) manager = RuntimeManager.getManager(HOME);
					else if(StringUtility.isNotEmpty(CONF)) manager = RuntimeManager.getManager(CONF);
						else manager = RuntimeManager.getManager();

				info(context, "AREasy Runtime Manager is initialized");
			}
			else info(context, "AREasy Runtime Manager is already initialized");
		}
		catch(Throwable th)
		{
			throw new ARException(Constants.AR_RETURN_ERROR, 98001, th.getMessage() );
		}

		//load registered plugins
		getPlugins(context);
	}

	protected void getPlugins(ARPluginContext context)
	{
		if(getManager() == null)
		{
			error(context, "AREasy Runtime Manager is not yet initialized");
			return;
		}

		//load configured runtime action
		List list = getManager().getConfiguration().getVector("app.plugin.area.class", new Vector());

		if(list == null || list.isEmpty())
		{
			warn(context, "No AREA plugin registered in AREasy Runtime");
			return;
		}

		for(int i = 0; i < list.size(); i++)
		{
			String pClassName = (String) list.get(i);

			if(StringUtility.isNotEmpty(pClassName))
			{
				try
				{
					Class pClass = Class.forName(pClassName);

					if(pClass != null)
					{
						Constructor contructor = pClass.getConstructor(null);
						AbstractArea area = (AbstractArea) contructor.newInstance(null);

						area.setRuntimeArea(this);
						area.open(context);

						this.plugins.put(area.getSignature(), area);
						info(context, "Loaded AREA plugin '" + area.getSignature() + "' with the implementation class: " + pClassName);
					}
				}
				catch(Throwable th)
				{
					error(context, "Error loading '" + pClassName + "' AREA plugin: " + th.getMessage(), th);
				}
			}
		}
	}

	/**
	 * Run authentication process.
	 */
	public AREAResponse areaVerifyLogin(ARPluginContext context, String user, String password, String networkAddress, String authString) throws ARException
	{
		boolean stopped = false;
		AREAResponse response = new AREAResponse();

		response.setLoginStatus(AREAResponse.AREA_LOGIN_UNKNOWN_USER);
		response.setLogText("Waiting '" + user + "' to login");

		info(context, "AREasy AREA plugin call - areaVerifyLogin()");
		List list = getManager().getConfiguration().getList("app.plugin.area.workflow", null);

		if(list != null)
		{
			debug("Found plugins in the authentication workflow: " +
					StringUtility.join(getManager().getConfiguration().getStringArray("app.plugin.area.workflow", null), ", "));
		}
		else
		{
			response.setLogText("No AREA plugin configured");
			error(context, "No AREA plugin configured");

			return response;
		}

		for(int i = 0; !stopped && list!= null && i < list.size(); i++)
		{
			String code = (String) list.get(i);
			AbstractArea plugin = null;

			try
			{
				//get plugin action
				plugin = (AbstractArea) this.plugins.get(code);

				debug("Call '" + plugin.getSignature() + "' plugin");
				int signal = plugin.call(context, response, user, password, networkAddress, authString);

				if(signal <= 0) stopped = true;
				info(context, "Login execution for user '" + user + "' [Status = " + response.getLoginStatus() + ", Text = " + response.getLogText() + ", Message = " + response.getMessageText() + "]");
			}
			catch (Throwable th)
			{
				error(context, "Error executing " + (code != null ? ("'" + code + "'") : "") + " call AREA plugin action: " + th.getMessage(), th);
				stopped = false;
			}
		}

		return response;
	}

	/**
	 * Plugin synchronization with the plugin server.
	 *
	 * @param context plugin context
	 * @return true is the plugin was synch
	 * @throws ARException in case of any error will occur
	 */
	public boolean areaNeedSync(ARPluginContext context) throws ARException
	{
		return false;
	}

	public void terminate(ARPluginContext context) throws com.bmc.arsys.api.ARException
	{
		info(context, "AREasy AREA plugin call - terminate()");
		List list = getManager().getConfiguration().getVector("app.plugin.area.workflow", new Vector());

		for(int i = 0; list != null && i < list.size(); i++)
		{
			String code = (String) list.get(i);
			AbstractArea plugin = null;

			try
			{
				//get plugin action
				plugin = (AbstractArea) this.plugins.get(code);
				info(context, "Close call for '" + plugin.getSignature() + "' plugin");

				//execute calls
				plugin.close(context);
			}
			catch (Throwable th)
			{
				error(context, "Error executing " + (code != null ? ("'" + code + "'") : "") + " close AREA plugin action: " + th.getMessage(), th);
			}
		}
	}

	/**
	 * Get runtime manager configuration structure.
	 *
	 * @return configuration structure
	 */
	public RuntimeManager getManager()
	{
		return manager;
	}

	public boolean isTraceEnabled()
	{
		return LoggerFactory.getLog(getClass()).isTraceEnabled();
	}

	public void debug(String message)
	{
		LoggerFactory.getLog(getClass()).debug(message);
	}

	public void debug(String message, Throwable th)
	{
		LoggerFactory.getLog(getClass()).debug(message);
		LoggerFactory.getLog(getClass()).debug("Exception", th);
	}

	public void debug(Throwable th)
	{
		LoggerFactory.getLog(getClass()).debug("Exception", th);
	}

	public void info(String message)
	{
		LoggerFactory.getLog(getClass()).info(message);
	}

	public void warn(String message)
	{
		LoggerFactory.getLog(getClass()).warn(message);
	}

	public void error(String message)
	{
		LoggerFactory.getLog(getClass()).error(message);
	}

	public void error(String message, Throwable th)
	{
		LoggerFactory.getLog(getClass()).error(message);
		LoggerFactory.getLog(getClass()).debug("Exception", th);
	}

	public void info(ARPluginContext context, String message)
	{
		context.logMessage(context.getPluginInfo(), 0, message);
	}

	public void warn(ARPluginContext context, String message)
	{
		context.logMessage(context.getPluginInfo(), 1, message);
	}

	public void error(ARPluginContext context, String message)
	{
		context.logMessage(context.getPluginInfo(), 2, message);
	}

	public void error(ARPluginContext context, String message, Throwable th)
	{
		context.logMessage(context.getPluginInfo(), 2, message);
		LoggerFactory.getLog(getClass()).debug("Exception", th);
	}
}
