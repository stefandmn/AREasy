package org.areasy.runtime.engine;

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

import org.areasy.common.data.StringUtility;
import org.areasy.common.logger.Logger;
import org.areasy.common.logger.LoggerFactory;
import org.areasy.common.support.configuration.Configuration;
import org.areasy.runtime.RuntimeManager;
import org.areasy.runtime.engine.base.AREasyException;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.Vector;

/**
 * This is the implementation of AREASY wrapper for FILTERAPI plugin.
 */
public class RuntimeWrapper
{
	private Logger logger = null;

	protected RuntimeManager getManager(String file) throws AREasyException
	{
		//get home location from configuration
		if(StringUtility.isEmpty(file))
		{
			//get home location from java environment
			String home = System.getProperty("areasy.home");
			if(StringUtility.isEmpty(home))
			{
				//get the map with all system variables
				Map map = getEnvironmentVariables();

				home = (String)map.get("AREASY_HOME");
				if(StringUtility.isEmpty(home))
				{
					home = (String)map.get("areasy_home");
					if(StringUtility.isEmpty(home)) throw new AREasyException("There is no application or system variable to refer the AREasy Runtime home location");
				}

	            //set the local variable
				System.setProperty("areasy.home", home);
			}
		}
		else System.setProperty("areasy.home", file);

		return new RuntimeManager();
	}

	/**
	 * This is the main wrapper call that is passed a list of values that the filter action has setup
	 * as input values. Also, a list of values is expected as an output.
	 *
	 * @param file configuration for the runtime manager (for each frapper call will be imitialized a new runtime manager)
	 * @param args list of values that the filter action has setup
	 * @return a list of values is expected as an output; first element is the output messages bundle and the rest is the output
	 */
	public String[] call(String file, String args[])
	{
		String command = "";
		List output = new Vector();

		//generate input command
		for(int i = 0; args != null && i < args.length; i++)
		{
			if(StringUtility.isEmpty(args[i])) continue;

			if(StringUtility.isNotEmpty(command)) command += "\n" + args[i];
				else command += args[i];
		}

		//execute generated command
		try
		{
			RuntimeManager manager = getManager(file);
			Configuration config = manager.getConfiguration(command);

			//setup runtime manager instance for the current request and define a proper logger channel
			manager.setup(config);
			setLogger();

			//start client execution
			debug("Received command: " + StringUtility.replace(command, "\n", " "));
			manager.client(config);

			String data[] = RuntimeLogger.getData();
			String text = RuntimeLogger.getMessages();
			debug("Generated output: " + (data != null ? data.length : 0) + " data part(s), and a log message having " + (text != null ? text.length() : 0) + " char(s)");

			//put in the output all runtime log messages
			if(text != null) output.add(StringUtility.trim(text));
				else output.add("");

			// put in the output structure the entire runtime output array
			for(int i = 0; data != null && i < data.length; i++)
			{
				if(data[i] == null) output.add("");
					else output.add(data[i]);
			}

			//deallocate and destroy logger channel(s)
			RuntimeLogger.destroy();
			setNullLogger();
		}
		catch (Throwable th)
		{
			output.add("Error running AREasy Runtime session: " + th.getMessage());

			error("Error running AREasy Runtime session: " + th.getMessage());
			debug("Exception", th);
		}

		return (String[]) output.toArray(new String[output.size()]);
	}

	/**
	 * Launch the appropriate call to the operating system and capture the output.
	 * @return a map with all environment variables
	 */
	public static Map getEnvironmentVariables()
	{
		Process p;
		Map variables = new Hashtable();

		try
		{
			String command = null;
			Runtime r = Runtime.getRuntime();
			String OS = System.getProperty("os.name").toLowerCase();

			//System.out.println("**** AREasy environment variable: os.name = " + OS);
			if (OS.indexOf("windows 9") > -1) command = "command.com /c set";
				else if (OS.indexOf("windows") > -1) command = "cmd.exe /c set";
					else command = "env";

			p = r.exec(command);
			BufferedReader br = new BufferedReader(new InputStreamReader(p.getInputStream()));
			String line;

			while ((line = br.readLine()) != null)
			{
				int idx = line.indexOf('=');
				String key = line.substring(0, idx);
				String value = line.substring(idx + 1);

				variables.put(key.toLowerCase(), value);
			}
		}
		catch(Throwable th)
		{
			System.err.println("Error reading environment variables: " + th.getMessage());
		}

		return variables;
	}

	public Logger getLogger()
	{
		return logger;
	}

	public void setLogger()
	{
		if(logger == null) logger = LoggerFactory.getLog(RuntimeWrapper.class);
	}

	public void setLogger(Logger newLogger)
	{
		logger = newLogger;
	}

	public void setNullLogger()
	{
		logger = null;
	}

	public void debug(String message)
	{
		if(logger != null) logger.debug(message);
			else System.out.println(message);
	}

	public void debug(String message, Throwable th)
	{
		if(logger != null) logger.debug(message, th);
		else
		{
			System.out.println(message);
			th.printStackTrace(System.out);
		}
	}

	public void error(String message)
	{
		if(logger != null) logger.error(message);
			else System.err.println(message);
	}
}
