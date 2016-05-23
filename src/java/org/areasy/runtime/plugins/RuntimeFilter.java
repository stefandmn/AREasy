package org.areasy.runtime.plugins;

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
import com.bmc.arsys.api.Constants;
import com.bmc.arsys.api.DataType;
import com.bmc.arsys.api.Value;
import com.bmc.arsys.pluginsvr.plugins.ARFilterAPIPlugin;
import com.bmc.arsys.pluginsvr.plugins.ARPluginContext;
import org.areasy.runtime.RuntimeManager;
import org.areasy.runtime.engine.RuntimeLogger;
import org.areasy.runtime.plugins.area.RuntimeArea;
import org.areasy.common.data.StringUtility;
import org.areasy.common.logger.Logger;
import org.areasy.common.logger.LoggerFactory;
import org.areasy.common.support.configuration.Configuration;

import java.util.List;
import java.util.Vector;

/**
 * This is the implementation of AREASY FILTERAPI plugin. In this case, this implementation extends
 * ARFilterAPIPlugin. Alternatively, one could just implement the interface ARFilterAPIPluggable.
 *
 */
public class RuntimeFilter extends ARFilterAPIPlugin
{
	/** Library logger */
	public static Logger logger = LoggerFactory.getLog(RuntimeFilter.class);

	private static String HOME = null;

	/** Runtime manager */
	private RuntimeManager manager = null;

	/**
     * Implementation of the static init method, to do any static initialization for this class.
     * This method is called once for each class that implements one of the ARPluggable interface
     * on plugin server startup
	 *
     * @param context AR plugin context structure
     */
    public static void init(ARPluginContext context)
	{
    	HOME = context.getConfigItem("home");
    	System.out.println("User defined value for HOME parameter: " + HOME);
    }

	/**
	 * Implementation of the initialization method, to do any static initialization for this class.
	 * This method is called once for each class that implements one of the ARPluggable interface
	 * on plugin server startup
	 *
	 * @param context AR plugin context structure
	 */
	public void initialize(ARPluginContext context) throws ARException
	{
		super.initialize(context);
		info(context, "Reading AREasy FilterAPI plugin configuration");

		try
		{
			if(getManager() == null)
			{
				manager = RuntimeManager.getManager(HOME);
				info(context, "AREasy Runtime Manager is initialized.");
			}
		}
		catch(Throwable th)
		{
			throw new ARException(Constants.AR_RETURN_ERROR, 98001, th.getMessage() );
		}
	}

	/**
	 * This is the main filter api call that is passed a list of values that the filter action has setup
	 * as input values.  A list of Value objects is expected as an output.
	 */
	public List filterAPICall(ARPluginContext context, List input) throws ARException
	{
		List output = new Vector();
		info(context, "AREasy FilterAPI call");

		//generate input command
		String command = "";
		for(int i = 0; input != null && i < input.size(); i++)
		{
			Value value = (Value) input.get(i);

			if(value == null) continue;

			String strValue = StringUtility.trim(value.toString());
			if(strValue == null) strValue = "";

			if(i < input.size() - 1) command += strValue + " ";
				else command += strValue;
		}

		//execute generated command
		try
		{
			Configuration config = getManager().getConfiguration(command);

			//setup runtime manager instance for the current request
			getManager().setup(config);

			//start client execution
			getManager().client(config);

			String data[] = RuntimeLogger.getData();
			String text = RuntimeLogger.getMessages();

			//generate the output
			if(text != null) output.add(new Value(text));
				else output.add(new Value("", DataType.NULL));

			for(int i = 0; i < data.length; i++)
			{
				if(data[i] == null) output.add(new Value("", DataType.NULL));
					else output.add(new Value(data[i]));
			}

			RuntimeLogger.destroy();
		}
		catch (Exception e)
		{
			debug("Exception", e);
			throw new ARException( Constants.AR_RETURN_ERROR, 98002, "Error running AREasy Runtime session: " + e.getMessage());
		}
		
		return output;
	}

	/**
	 * Get runtime manager configuration structure.
	 *
	 * @return configuration structure
	 */
	protected RuntimeManager getManager()
	{
		return manager;
	}

	public void debug(String message)
	{
		LoggerFactory.getLog(RuntimeArea.class).debug(message);
	}

	public void debug(String message, Throwable th)
	{
		LoggerFactory.getLog(getClass()).debug(message);
		LoggerFactory.getLog(getClass()).debug("Exception", th);
	}

	public void info(String message)
	{
		LoggerFactory.getLog(getClass()).info( message);
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
		LoggerFactory.getLog(getClass()).error( message);
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

