package org.areasy.runtime;

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

import org.areasy.runtime.actions.SystemAction;
import org.areasy.runtime.actions.system.VersionAction;
import org.areasy.runtime.engine.RuntimeClient;
import org.areasy.runtime.engine.RuntimeLogger;
import org.areasy.runtime.engine.RuntimeServer;
import org.areasy.runtime.engine.RuntimeWrapper;
import org.areasy.runtime.engine.base.AREasyException;
import org.areasy.common.data.*;
import org.areasy.common.logger.Logger;
import org.areasy.common.logger.LoggerFactory;
import org.areasy.common.logger.base.LoggerManager;
import org.areasy.common.support.configuration.Configuration;
import org.areasy.common.support.configuration.ConfigurationEntry;
import org.areasy.common.support.configuration.base.BaseConfiguration;
import org.areasy.common.support.configuration.base.BaseConfigurationEntry;
import org.areasy.common.support.configuration.providers.properties.stream.PropertiesConfiguration;
import org.areasy.common.velocity.Velocity;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.math.BigDecimal;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.util.*;
import java.util.jar.Manifest;

/**
 * AREasy Runtime actions' manager.
 * This library could execute runtime actions defined in these package or outside of this library but
 * registered in the application configuration sector. This class could initiate a dedicated environment
 * to execute or to call implemented runtime actions which should execute parsing operations, data updates
 * in CMDB or ARS server (for other applications), changes in definitions, etc.
 *
 */
public class RuntimeManager
{
	private static Logger logger = null;

	/** Home directory */
	private static File homeDir = null;

	/** Working directory */
	private static File workDir = null;

	/** Runtime action's dictionary */
	private Map actions = new HashMap();

	/** Runtime action configuration */
	private Configuration configuration = null;

	private int mode = 0;
	public static int CLIENT = 1;
	public static int SERVER = 2;
	public static int RUNTIME = 0;

	/**
	 * Default static method to launch in execution AREasy Runtime application.
	 * This method will create an instance of runtime manager which should be started using three type of instances: server, client or standalone.
	 * To differentiate these modes you should specify parameter <code>-mode</code> with one of those values: <i>server</i> or <i>client</i>.
	 * To start in standalone mode it's snough to ignore this parameter. If you start the server mode all other parameters will be ignored.
	 * If you want to execute an action in client mode or standalone you need to specify all necessary parameter to define a complete command.
	 * <p>
	 * In case you need help to see how command line looks like you need to execute runtime binary with command <code>-help</code>. Also in this
	 * situation all additional parameters are ignored.
	 *
	 * @param args input parameters.
	 */
	public static void main(String args[])
	{
		RuntimeManager manager = new RuntimeManager();

		//get input parameters.
		Configuration config = manager.getConfiguration(args);

		//setup runtime manager instance for the current request
		int mode = manager.setup(config);

		//runtime workflow implementation
		if(mode == SERVER)
		{
			//load actions & initialize engines
			manager.setRuntimeActions();
			manager.initVelocityEngine();
			manager.setInputConfiguration(config);

			//start client execution
			manager.server();
		}
		else if(mode == CLIENT)
		{
			//start client execution
			manager.client(config);
			manager.print(config);

			RuntimeLogger.destroy();
		}
		else
		{
			//load actions & initialize engines
			manager.setRuntimeActions();
			manager.initVelocityEngine();
			
			//run standalone application
			manager.process(config);
			manager.print(config);

			RuntimeLogger.destroy();
		}

		//close session.
		manager.end(false);
	}

	/**
	 * Write a message to the standard output
	 *
	 * @param message string message to be written
	 */
	public void write(String message)
	{
		if(StringUtility.isNotEmpty(message)) System.out.println(message);
			else System.out.println();
	}

	/**
	 * Write a message to the standard output enclosed by carriage return character
	 *
	 * @param message string message to be written
	 */
	public void writeln(String message)
	{
		if(StringUtility.isNotEmpty(message)) System.out.println(message + "\n");
			else System.out.println("\n");
	}

	/**
	 * Write an answer to the standard output
	 *
	 * @param config end user configuration
	 */
	public void print(Configuration config)
	{
		String data[] = RuntimeLogger.getData();
		String logtext = RuntimeLogger.getMessages();

		//write data & objects.
		if(!config.getBoolean("reportnodata", false) && data != null && data.length > 0)
		{
			System.out.println();
			System.out.println();

			for(int i = 0; i < data.length; i++)
			{
				if(StringUtility.isNotEmpty(data[i]))
				{
					write(data[i]);
				}
				else write(null);
			}
		}

		if(!config.getBoolean("reportnolog", false) && StringUtility.isNotEmpty(logtext))
		{
			//write answer messages.
			System.out.println();
			System.out.println();

			if(!config.getBoolean("reportnodata", false) && data != null && data.length > 0)
			{
				System.out.println("=== AREasy Log Message ===");
				System.out.println();
			}

			write(logtext.trim());
		}

		System.out.println();
	}

	/**
	 * get execution mode for the current instance of runtime manager.
	 *
	 * @param config input configuration.
	 * @return the execution mode which can be: 1 = client, 2 = server or 0 = distinct instance with direct access to the ARS servers (runtime)
	 */
	public int detectExecutionMode(Configuration config)
	{
		String mode = config.getString("mode", null);
		boolean runtime = config.getBoolean("runtime", false);

		if(StringUtility.equalsIgnoreCase(mode, "server") && !runtime) return 2;
			else if(StringUtility.equalsIgnoreCase(mode, "client") && !runtime) return 1;
				else return 0;
	}

	/**
	 * Default constructor
	 */
	public RuntimeManager()
	{
		try
		{
			//define home directory
			homeDir = new File(System.getProperty("areasy.home"));

			//define working directory.
			workDir = new File(homeDir.getAbsolutePath() + File.separator + "work");
			if(!workDir.exists()) workDir.mkdirs();

			//read configuration
			setConfiguration(new PropertiesConfiguration(homeDir.getAbsolutePath() + File.separator + "cfg" + File.separator + "default.properties"));
		}
		catch (Exception e)
		{
			writeln("Fatal error: " + e.getMessage() + "\n");

			e.printStackTrace();
			end(true);
		}
	}

	/**
	 * Initialize runtime manager instance in concordance with received configuration structure.
	 *
	 * @param config input configuration
	 * @return what type of execution is requests: server (2), client (1) or directly the runtime (0)
	 */
	public final int setup(Configuration config)
	{
		//set logger.
		setLogger(config);

		//get execution mode
		mode = detectExecutionMode(config);

		//set server instance signature.
		setSignature(config);

		return mode;
	}

	public int getExecutionMode()
	{
		return this.mode;
	}

	/**
	 * Dedicated implementation of runtime server. To stop a runtime server instance you must use
	 * a special action called <code>shutdown</code> which should be called in client mode from the same
	 * host like server instance. For more details about it please check <code>process</code> method.
	 */
	public final void server()
	{
		RuntimeServer server = new RuntimeServer(this);
		server.run();
	}

	/**
	 * Dedicated implementation for client runtime to execute remotely all requested actions.
	 * When you start runtime manager in client mode you can use two optional parameters to specify the server host
	 * and the TCP port for connectivity to the runtime server.
	 *
	 * <table border="1">
	 * 	<tr>
	 * 		<td><b>-host</b></td>
	 * 		<td>Runtime server name. If is ignored the server name will be the <code>localhost</code></td>
	 * 	</tr>
	 * 	<tr>
	 * 		<td><b>-port</b></td>
	 * 		<td>Port number to connect to the runtime server. If is omited the port number will be taken from configuration file,
	 * 			reading property <code>app.server.port</code></td>
	 * 	</tr>
	 * </table>
	 *
	 * @param config input configuration extracted from arguments.
	 */
	public final void client(Configuration config)
	{
		RuntimeClient client = new RuntimeClient(this);
		client.run(config);
	}

	/**
	 * Dedicated implementation of standalone execution for runtime actions. Also this method is executed by the runtime server when
	 * a client call is asking to execute an action. To start or to call an action you need to specify parameter <code>-action</code>
	 * (which is mandatory).
	 *
	 * <p>
	 * If the runtime is started in server mode and if you want to stop this server instance you need to run another server instance specifing a special
	 * action called <b>shutdown</b> and will be executed only if the caller ison the same machine like server instance.
	 *
	 * @param config input configuration generated based on input parameters
	 */
	public final void process(Configuration config)
	{
		process(getRuntimeAction(config), config);
	}

	/**
	 * Dedicated implementation of standalone execution for runtime actions. Also this method is executed by the runtime server when
	 * a client call is asking to execute an action. To start or to call an action you need to specify parameter <code>-action</code>
	 * (which is mandatory).
	 *
	 * <p>
	 * If the runtime is started in server mode and if you want to stop this server instance you need to run another server instance specifing a special
	 * action called <b>shutdown</b> and will be executed only if the caller ison the same machine like server instance.
	 *
	 * @param action runtime action to be processed
	 * @param config input configuration generated based on input parameters
	 */
	public final void process(RuntimeAction action, Configuration config)
	{
		if(action != null)
		{
			try
			{
				//register the channel name in the runtime logger
				RuntimeLogger.debug("Runtime Logger Channel: " + RuntimeLogger.getChannelName());

				if(!config.getBoolean("help", false))
				{
					//action initialization
					action.init(config, this);

					//action execution
					action.run();
				}
				else
				{
					//fake initialization
					action.init(config, this, null);

					//get help text
					String help = action.help();

					//put it in output data buffer
					if(StringUtility.isEmpty(help))
					{
						RuntimeLogger.warn("No available help text for action '" + action + "'");
					}
					else
					{
						RuntimeLogger.add(help);
					}
				}
			}
			catch(Throwable th)
			{
				//handle exceptions
				action.throwable(th);
				logger.debug("Exception", th);
			}

			//notify results
			action.report();

			//dispose action objects.
			action.dispose();
		}
	}

	/**
	 * This method will load all runtime actions registered in the configuration sector.This method
	 * should be executed when the runtime manager is running in server or standalone mode.
	 */
	public final void setRuntimeActions()
	{
		//load configured runtime action
		this.actions.clear();
		List actions = getConfiguration().getVector("app.runtime.actions", new Vector());

		for(int i = 0; i < actions.size(); i++)
		{
			String aName = (String) actions.get(i);
			String aClass = getConfiguration().getString("app.runtime.action." + aName + ".class", null);

			if(StringUtility.isNotEmpty(aName) && StringUtility.isNotEmpty(aClass))
			{
				try
				{
					Class actionClass = Class.forName(aClass);

					if(actionClass != null)
					{
						this.actions.put(aName, actionClass);
						logger.trace("Action '" + aName + "' has been loaded using class: " + actionClass);

						String aAlias = getConfiguration().getString("app.runtime.action." + aName + ".alias", null);

						if(aAlias != null)
						{
							this.actions.put(aAlias, actionClass);
							logger.trace("Action '" + aAlias + "' has been loaded using class: " + actionClass);
						}
					}
				}
				catch(Throwable th)
				{
					logger.error("Error reading action '" + aName + "': " + th.getMessage());
					logger.debug("Exception", th);
				}
			}
		}
	}

	/**
	 * Get an empty runtime action instance and structure based on a specific configuration.
	 *
	 * @param config action configuration
	 * @return <code>RuntimeAction</code>
	 */
	public final RuntimeAction getRuntimeAction(Configuration config)
	{
		String action = null;

		try
		{
			//validate configuration.
			if(config != null)
			{
				//get action code.
				action = config.getString("action", null);
			}

			//validate action code
			if(StringUtility.isEmpty(action))
			{
				RuntimeLogger.warn("Action is null. Run with parameter '-action'");
				logger.warn("Action is null. Run with parameter '-action'");
			}
			else
			{
				if(SystemAction.isSystemAction(action)) return SystemAction.getRuntimeAction(null, action);
					else return getRuntimeAction(action);
			}
		}
		catch(Throwable th)
		{
			RuntimeLogger.error("Error discovering action '" + action + "': " + th.getMessage());

			logger.error("Error discovering action '" + action + "': " + th.getMessage());
			logger.debug("Exception", th);
		}

		return null;
	}

	/**
	 * Get all registered action in the current runtime instance.
	 *
	 * @return an iterator structure with all runtime action names
	 */
	public final Iterator getRuntimeActions()
	{
		if(this.actions.keySet() != null) return this.actions.keySet().iterator();
			else return null;
	}

	/**
	 * Get an empty runtime action instance and structure.
	 *
	 * @param action action name (registered in the configuration sectors)
	 * @return <code>RuntimeAction</code>
	 * @throws AREasyException runtime action instance and structure
	 */
	public final RuntimeAction getRuntimeAction(String action) throws AREasyException
	{
		RuntimeAction runtime;

		if(StringUtility.isNotEmpty(action))
		{
			Class classAction = (Class) actions.get(action);
			if(classAction == null) throw new AREasyException("Runtime action '" + action  + "' is not registered!");

			try
			{
				Constructor contructor = classAction.getConstructor(null);

				runtime = (RuntimeAction) contructor.newInstance(null);
				runtime.setCode(action);
			}
			catch(Throwable th)
			{
				throw new AREasyException("Runtime initialization error for action '" + action + "'", th);
			}
		}
		else throw new AREasyException("Unknown runtime action. Check your configuration file!");

		return runtime;
	}

	/**
	 * Close runtime instance.
	 * @param error specify if the closing procedure is finished in normal condition or because was happen an error.
	 */
	private void end(boolean error)
	{
		if(error)
		{
			if(logger != null) logger.info("Drop execution of AREasy Runtime because was recorded errors..");
				else writeln("Drop execution of AREasy Runtime because was recorded errors..");

			System.exit(1);
		}
		else
		{
			if(logger != null) logger.info("End execution of AREasy Runtime Session.");
				else writeln("End execution of AREasy Runtime Session.");

			System.exit(0);
		}
	}

	/**
	 * Set runtime logger.
	 *
	 * @param config command line parametrization
	 */
	public void setLogger(Configuration config)
	{
		try
		{
			String formatter = config.getString("logformatter", null);
			String level = config.getString("loglevel", null);

			String[] loggers = getConfiguration().getStringArray("app.runtime.loggers", null);

			if(loggers == null)
			{
				loggers = new String[1];
				loggers[0] = "root";
			}

			if(loggers.length > 1 && !loggers[0].equals("root"))
			{
				for(int i = 1; i < loggers.length; i++)
				{
					if(loggers[i].equals("root"))
					{
						String temp = loggers[0];
						loggers[0] = loggers[i];
						loggers[i] = temp;
					}
				}
			}

			for(int i = 0; i < loggers.length; i++)
			{
				setLogger(loggers[i], formatter, level, true);
			}

			if(logger == null) logger = LoggerFactory.getLog(RuntimeManager.class);
			logger.info("Starting AREasy Runtime at " + new Date().toString());
		}
		catch(Exception e)
		{
			writeln("Error creating logger: " + e.getMessage() + "\n");

			e.printStackTrace();
			end(true);
		}
	}

	/**
	 * Set logger.
	 */
	public final void setLogger()
	{
		try
		{
			String formatter = getConfiguration().getString("app.runtime.logger.formatter", null);
			String level = getConfiguration().getString("app.runtime.logger.level", null);
			boolean append = getConfiguration().getBoolean("app.runtime.logger.append", false);

			String[] loggers = getConfiguration().getStringArray("app.runtime.loggers", null);

			for(int i = 0; loggers != null && i < loggers.length; i++)
			{
				setLogger(loggers[i], formatter, level, append);
			}

			if(logger == null) logger = LoggerFactory.getLog(RuntimeManager.class);
			logger.info("Runtime logger has been initialized");
		}
		catch(Throwable th)
		{
			writeln("Error creating logger: " + th.getMessage() + "\n");
			th.printStackTrace();
		}
	}

	/**
	 * Append/active a specific logger.
	 *
	 * @param name logger name defined in the configuration file. If is null "root" logger will be considered
	 * @throws Exception in case of any error will occur
	 */
	public final void setLogger(String name) throws Exception
	{
		setLogger(name, null, null, false);
	}

	/**
	 * Append/active a specific logger.
	 *
	 * @param defaultFormatter default formatter: <code>%d [%t] %-5p %c{1} - %m%n</code>
	 * @param defaultLevel default log level: <code>info</code>
	 * @param name logger name defined in the configuration file. If is null "root" logger will be considered
	 * @param append if the new logger will append data to existing logging file
	 * @throws Exception in case of any error will occur
	 */
	protected final void setLogger(String name, String defaultFormatter, String defaultLevel, boolean append) throws Exception
	{
		if(name == null) name= "root";

		String loggerLevel = defaultLevel != null ? defaultLevel : getConfiguration().getString("app.runtime.logger." + name + ".level", "info");
		String loggerFormatter = defaultFormatter != null ? defaultFormatter : getConfiguration().getString("app.runtime.logger." + name + ".formatter", "%d [%t] %-5p %c{1} - %m%n");
		boolean logAppend = append ? append : getConfiguration().getBoolean("app.runtime.logger." + name + ".append", false);

		if(name.equals("root"))
		{
			String loggerFileName = getConfiguration().getString("app.runtime.logger.outer.file", "external.log");
			File loggerFile = new File(getLogsDirectory(), loggerFileName);

			LoggerFactory.getLogManager().addFileLogger("root", LoggerManager.getLoggerLevel(loggerLevel),
				"AREasy Outer", loggerFile.getAbsolutePath(), logAppend,
				LoggerManager.FORMATTER_SIMPLE, loggerFormatter);

			loggerFileName = getConfiguration().getString("app.runtime.logger." + name + ".file", name + ".log");
			loggerFile = new File(getLogsDirectory(), loggerFileName);

			LoggerFactory.getLogManager().addFileLogger("org.areasy", LoggerManager.getLoggerLevel(loggerLevel),
				"AREasy Root", loggerFile.getAbsolutePath(), logAppend,
				LoggerManager.FORMATTER_SIMPLE, loggerFormatter);
		}
		else
		{
			String loggerFileName = getConfiguration().getString("app.runtime.logger." + name + ".file", name + ".log");
			File loggerFile = new File(getLogsDirectory(), loggerFileName);

			String loggerTitle = "AREasy " + StringUtility.capitalize(name);
			String loggerName = "org.areasy.runtime." + name;

			LoggerFactory.getLogManager().addFileLogger(loggerName, LoggerManager.getLoggerLevel(loggerLevel),
				loggerTitle, loggerFile.getAbsolutePath(), logAppend,
				LoggerManager.FORMATTER_SIMPLE, loggerFormatter);
		}
	}

	public static Logger getLogger()
	{
		return logger;
	}

	private void setInputConfiguration(Configuration config)
	{
		if(config != null)
		{
			String host = config.getString("host", null);
			if(StringUtility.isNotEmpty(host)) getConfiguration().setKey("app.server.host", host);

			String port = config.getString("port", null);
			if(StringUtility.isNotEmpty(port)) getConfiguration().setKey("app.server.port", port);

			String threads = config.getString("threads", null);
			if(StringUtility.isNotEmpty(threads)) getConfiguration().setKey("app.server.threads", threads);

			String arserver = config.getString("arserver", null);
			if(StringUtility.isNotEmpty(arserver)) getConfiguration().setKey("app.server.default.arsystem.server.name", arserver);

			String arport = config.getString("arport", null);
			if(StringUtility.isNotEmpty(arport)) getConfiguration().setKey("app.server.default.arsystem.port.number", arport);

			String aruser = config.getString("aruser", null);
			if(StringUtility.isNotEmpty(aruser)) getConfiguration().setKey("app.server.default.arsystem.user.name", aruser);

			String arpassword = config.getString("arpassword", null);
			if(StringUtility.isNotEmpty(arpassword)) getConfiguration().setKey("app.server.default.arsystem.user.password", arpassword);			
		}
	}

	/**
	 * Get runtime manager configuration structure.
	 *
	 * @return configuration structure
	 */
	public Configuration getConfiguration()
	{
		return configuration;
	}

	public String getServerName()
	{
		String hostName = getConfiguration().getString("app.server.host");

		if(StringUtility.equalsIgnoreCase("localhost", hostName) || StringUtility.equalsIgnoreCase("127.0.0.1", hostName))
		{
			String name = null;

			try
			{
				Enumeration<NetworkInterface> enet = NetworkInterface.getNetworkInterfaces();

				while ( enet.hasMoreElements() && (name == null) )
				{
					NetworkInterface net = enet.nextElement();

					if ( net.isLoopback() ) continue;

					Enumeration<InetAddress> eaddr = net.getInetAddresses();

					while ( eaddr.hasMoreElements() )
					{
						InetAddress inet = eaddr.nextElement();

						if ( inet.getCanonicalHostName().equalsIgnoreCase( inet.getHostAddress() ) == false )
						{
							name = inet.getCanonicalHostName();
							break;
						}
					}
				}
			}
			catch(Throwable th)
			{
				logger.error("Error reading hostname: " + th.getMessage());
			}

			if(name == null)
			{
				try
				{
					java.net.InetAddress localMachine = java.net.InetAddress.getLocalHost();
					name = localMachine.getHostName();
				}
				catch(Throwable th)
				{
					logger.error("Error reading hostname: " + th.getMessage());
				}
			}

			if(name != null) hostName = name;
		}

		return hostName;
	}

	/**
	 * Set the runtime manager configuration structure based on a configuration sector (usually read from a property/configuration file).
	 * 
	 * @param configuration configuration structure
	 */
	public void setConfiguration(Configuration configuration)
	{
		this.configuration = configuration;
		this.configuration.setKey("areasy.home", getHomeDirectory().getAbsolutePath());
	}

	/**
	 * Get home directory structure for runtime application.
	 *
	 * @return directory file structure
	 */
	public static File getHomeDirectory()
	{
		return homeDir;
	}

	/**
	 * Get working directory structure for runtime application. Here should be stored all temporary or processed resources.
	 *
	 * @return directory file structure
	 */
	public static File getWorkingDirectory()
	{
		return workDir;
	}

	/**
	 * Get logs directory structure for runtime application. Here should be stored all log files.
	 *
	 * @return directory file structure
	 */
	public static File getLogsDirectory()
	{
		String logFileString = homeDir.getAbsolutePath() + File.separator + "logs";
		File logDir = new File(logFileString);
		if(!logDir.exists()) logDir.mkdirs();

		return logDir;
	}

	/**
	 * Get libs directory structure for runtime application. Here should be stored all libraries files.
	 *
	 * @return directory file structure
	 */
	public static File getLibsDirectory()
	{
		String libsFileString = homeDir.getAbsolutePath() + File.separator + "libs";
		File libsDir = new File(libsFileString);

		if(!libsDir.exists()) libsDir.mkdirs();

		return libsDir;
	}

	/**
	 * Get config directory structure for runtime application. Here should be stored all configuration resources.
	 *
	 * @return directory file structure
	 */
	public static File getCfgDirectory()
	{
		String libsFileString = homeDir.getAbsolutePath() + File.separator + "cfg";
		File libsDir = new File(libsFileString);

		if(!libsDir.exists()) libsDir.mkdirs();

		return libsDir;
	}

	/**
	 * Get documentation directory structure for runtime application. Here should be stored all related documents.
	 *
	 * @return directory file structure
	 */
	public static File getBinDirectory()
	{
		String libsFileString = homeDir.getAbsolutePath() + File.separator + "bin";
		File libsDir = new File(libsFileString);

		if(!libsDir.exists()) libsDir.mkdirs();

		return libsDir;
	}

	/**
	 * Get documentation directory structure for runtime application. Here should be stored all related documents.
	 *
	 * @return directory file structure
	 */
	public static File getDocDirectory()
	{
		String libsFileString = homeDir.getAbsolutePath() + File.separator + "doc";
		File libsDir = new File(libsFileString);

		if(!libsDir.exists()) libsDir.mkdirs();

		return libsDir;
	}

	/**
	 * Transform arguments into a configuration structure.
	 *
	 * @param params action parameters
	 * @return translated configuration structure from input arguments.
	 */
	public Configuration getConfiguration(String[] params)
	{
		List list = new Vector();
		ConfigurationEntry current = null;

		Configuration configuration = new BaseConfiguration();

		for(int i = 0; params != null && i < params.length; i++)
		{
			if(params[i] != null && params[i].startsWith("-"))
			{
				String key = params[i].substring(1);

				if(i + 1 < params.length)
				{
					if(params[i + 1] != null && (!params[i + 1].startsWith("-") || (params[i + 1].startsWith("-") && params[i + 1].contains(" "))))
					{
						String value = params[i + 1];
						Object objvalue = getInstanceFromString(value);

						current = new BaseConfigurationEntry(key, objvalue);
						list.add(current);

						i++;
					}
					else
					{
						current = new BaseConfigurationEntry(key, "true");
						list.add(current);
					}
				}
				else
				{
					current = new BaseConfigurationEntry(key, "true");
					list.add(current);
				}
			}
			else if(params[i] != null && current != null)
			{
				String value = params[i];
				Object objvalue = getInstanceFromString(value);

				current.addValue(objvalue);
			}
		}

		//generates configuration structure.
		for(int i = 0; i < list.size(); i++) configuration.addConfigurationEntry( (ConfigurationEntry)list.get(i) );

		return configuration;
	}

	/**
	 * Translate input value into an object. In case of the input value will have functions like
	 * (int, long, float, double, date, bool, string and file) it will be transformed accordingly.
	 *
	 * @param input text input value
	 * @return an object or the same text value in case of no function is used.
	 */
	public Object getInstanceFromString(String input)
	{
		if(input == null) return null;

		Object value = input;
		String text = input.trim();

		int index1 = text.indexOf("(", 0);
		int index2 = text.indexOf(")", index1);

		if(index1 > 0 && index1 < index2)
		{
			String fragment = text.substring(index1 + 1, index2).trim();
			if(fragment != null) fragment = fragment.trim();

			try
			{
				if(text.startsWith("int")) value = new Integer( NumberUtility.toInt(fragment) );
				if(text.startsWith("long")) value = new Long( NumberUtility.toLong(fragment) );
				if(text.startsWith("float")) value = new Float( NumberUtility.toFloat(fragment) );
				if(text.startsWith("double")) value = new Double( NumberUtility.toDouble(fragment) );
				if(text.startsWith("decimal")) value = new BigDecimal( NumberUtility.toDouble(fragment) );
				if(text.startsWith("bool")) value = new Boolean(BooleanUtility.toBoolean(fragment));
				if(text.startsWith("date")) value = DateUtility.parseDate(fragment);
				if(text.startsWith("file")) value = new File(fragment);
			}
			catch (Throwable th)
			{
				logger.debug("Error transforming data input '" + input + "': " + th.getMessage());
			}
		}

		return value;
	}

	/**
	 * Translate an input value into a string. In case of the input is a non-string value the output will include
	 * functions for data transformation.
	 *
	 * @param object input object instance
	 * @return a string pattern of the specified object
	 */
	public String getStringFromInstance(Object object)
	{
		if(object == null) return null;

		String text = null;

		if(object instanceof String)
		{
			text = object.toString();

			if(text.indexOf("\r\n") > 0) text = StringUtility.replace(text, "\r\n", "\\\n");
				else if(text.indexOf('\n') > 0) text = StringUtility.replace(text, "\n", "\\\n");
		}
		else if(object instanceof Integer) text = "int(" + object + ")";
		else if(object instanceof Long) text = "long(" + object + ")";
		else if(object instanceof Float) text = "float(" + object + ")";
		else if(object instanceof Double) text = "double(" + object + ")";
		else if(object instanceof BigDecimal) text = "decimal(" + object + ")";
		else if(object instanceof Boolean) text = "bool(" + object + ")";
		else if(object instanceof Date) text = "date(" + object + ")";
		else if(object instanceof File) text = "file(" + ((File)object).getPath() + ")";
		else text = object.toString();

		return text;
	}

	/**
	 * Set runtime manager instance signature for the current session.
	 * This method will set an environment variable called <code>areasy.signature</code>
	 *
	 * @param config input configuration.
	 */
	protected void setSignature(Configuration config)
	{
		String signature = "";
		String mode = config.getString("mode", null);
		String action = config.getString("action", null);

		if(StringUtility.isNotEmpty(mode)) signature += mode + "-";
		if(StringUtility.isNotEmpty(action)) signature += action + "-";

		signature += DateFormatUtility.DB_TRIM_DATETIME_FORMAT.format(new Date());

		System.setProperty("areasy.signature", signature);
	}

	/**
	 * Initialize Velocity engine
	 */
	private void initVelocityEngine()
	{
		try
		{
			Configuration properties = new BaseConfiguration();
			properties.setKey("parser.pool.size", "5");

			Velocity.init(properties);
		}
		catch(Exception e)
		{
			logger.error("Error initializing velocity engine: " + e.getMessage());
			logger.debug("Exception", e);
		}
	}

	/**
	 * Get configuration structure from an input command line.
	 *
	 * @param command input command line
	 * @return <code>Configuration</code> structure generated from input parameters
	 */
	public Configuration getConfiguration(String command)
	{
		//get arguments from the input parameters
		String[] arguments = tokenize(command);

		return getConfiguration(arguments);
	}

	/**
	 * Get command line from an input configuration.
	 *
	 * @param config input configuration structure
	 * @return command line string value
	 */
	public String getCommandLine(Configuration config)
	{
		String command = null;

		Iterator iterator = config.getKeys();

		while(iterator != null && iterator.hasNext())
		{
			String key = (String) iterator.next();
			String value = config.getString(key, null);

			if(value != null && value.indexOf(' ') >= 0) value ="\"" + value + "\"";

			if(command == null) command = "-" + key + " " + value;
				else command += " -" + key + " " + value;
		}

		return command;
	}

	/**
	 * Parses the specified command line into an array of individual arguments.
	 * Arguments containing spaces should be enclosed in quotes.
	 * Quotes that should be in the argument string should be escaped with a
	 * preceding backslash ('\') character.  Backslash characters that should
	 * be in the argument string should also be escaped with a preceding
	 * backslash character.
	 *
	 * @param command the command line to parse
	 * @return an argument array representing the specified command line.
	 */
	public String[] tokenize(String command)
	{
		List list = new java.util.ArrayList();

		if (command != null)
		{
			int length = command.length();
			boolean insideQuotes = false;

			StringBuffer buffer = new StringBuffer();

			for (int i = 0; i < length; ++i)
			{
				char c = command.charAt(i);

				if (c == '"')
				{
					appendToBufferForTokenizer(list, buffer);
					insideQuotes = !insideQuotes;
				}
				else if (c == '\\')
				{
					if ((length > i + 1) && ((command.charAt(i + 1) == '"') || (command.charAt(i + 1) == '\\')))
					{
						buffer.append(command.charAt(i + 1));
						++i;
					}
					else buffer.append("\\");
				}
				else
				{
					if (insideQuotes) buffer.append(c);
					else
					{
						if (Character.isWhitespace(c)) appendToBufferForTokenizer(list, buffer);
							else buffer.append(c);
					}
				}
			}

			appendToBufferForTokenizer(list, buffer);
		}

		String[] result = new String[list.size()];
		return ((String[]) list.toArray(result));
	}

	/**
	 * Goofy internal utility to avoid duplicated code.  If the specified
	 * StringBuffer is not empty, its contents are appended to the resulting
	 * array (temporarily stored in the specified ArrayList).  The StringBuffer
	 * is then emptied in order to begin storing the next argument.
	 *
	 * @param list the List temporarily storing the resulting argument array.
	 * @param buffer the StringBuffer storing the current argument.
	 */
	private void appendToBufferForTokenizer(List list, StringBuffer buffer)
	{
		if (buffer.length() > 0)
		{
			list.add(buffer.toString());
			buffer.setLength(0);
		}
	}

	public static RuntimeManager getManager() throws AREasyException
	{
		return getManager(null);
	}

	public static RuntimeManager getManager(String defaultHome) throws AREasyException
	{
		//get home location from configuration
		if(homeDir == null)
		{
			//get home location from java environment
			String home = System.getProperty("areasy.home");

			if(StringUtility.isEmpty(home))
			{
				//get the map with all system variables
				Map map = RuntimeWrapper.getEnvironmentVariables();

				home = (String)map.get("AREASY_HOME");
				if(StringUtility.isEmpty(home))
				{
					home = (String)map.get("areasy_home");
					if(StringUtility.isEmpty(home))
					{
						if(StringUtility.isEmpty(defaultHome)) throw new AREasyException("There is no application or system variable to refer the AREasy plugin home location" );
							else home = defaultHome;
					}
				}

				//set the local variable
				System.setProperty("areasy.home", home);
			}
		}

		return new RuntimeManager();
	}

	/**
	 * This method is used to identify the current host name(s) and IP addresses of the local host
	 *
	 * @return a list with all names and IP addresses.
	 */
	public List getHostsIds()
	{
		List list = new Vector();

		try
		{
			java.net.InetAddress addr = java.net.InetAddress.getLocalHost();

			// Get hostname
			String hostname = addr.getHostName();
			if(hostname != null) list.add(hostname);

			// Get hostname
			hostname = addr.getCanonicalHostName();
			if(hostname != null) list.add(hostname);

			// Get IP Address
			hostname = addr.getHostAddress();
			if(hostname != null && !list.contains(hostname)) list.add(hostname);
		}
		catch (IOException e)
		{
			logger.error("Error running NetHost method: " + e.getMessage());
			logger.debug("Exception", e);
		}

		return list;
	}

	/**
	 * This method is used to read and deliver all components versions
	 *
	 * @return a list with all application modules and their version.
	 */
	public List getRuntimeModules()
	{
		List list = new Vector();
		Manifest manifests[] = VersionAction.getManifests("areasy");

		for(int i = 0; i < manifests.length; i++)
		{
			String name = VersionAction.getProductName(manifests[i]);
			String version = VersionAction.getProductVersion(manifests[i]);

			list.add(name + "/" + version);
		}

		return list;
	}
}
