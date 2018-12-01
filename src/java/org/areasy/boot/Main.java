package org.areasy.boot;

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

import java.io.*;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URLDecoder;
import java.security.Policy;
import java.util.*;

/**
 * Main start and stop class. This class is intended to be the main class listed in the MANIFEST.MF of the
 * boot.jar archive. It allows an application to be started with the command "java -jar
 * boot.jar". The behaviour of Main is controlled by the configuratiobn (boot.properties)
 * obtained as a resource or file.
 * <p/>
 * The syntax for our booter is:
 * <ul>
 * <li><code>-status [start/stop]</code> - specify application status. The next parameter must be a status value:
 * "start" (to run start method) or "stop" (to execute stop method)</li>
 * <li><code>-debug</code> - will display to the standard output/error all info/debug and error messages in each step
 * and the complete stacktrace.</li>
 * <li><code>-config [property file]</code> - specify property file to start or stop application or server.</li>
 * </ul>
 *
 * @version $Id: Main.java,v 1.6 2008/02/13 06:16:38 swd\stefan.damian Exp $
 */
public class Main
{
	/**
	 * Specify if this library will debug execution process
	 */
	static boolean verbose = false;

	/**
	 * Keep boot configuration file
	 */
	private static String bootFileName = null;

	/**
	 * Keep classpath structure
	 */
	private Classpath classpath = new Classpath();

	/**
	 * Environment and custom variables.
	 */
	private static Map variables = null;

	/**
	 * Initialize bootstrap class: get & parse parameters and run start or stop method.
	 */
	public static void main(String[] args)
	{
		if (args == null || args.length <= 0)
		{
			System.err.println("Usage: boot.jar [-status start/stop] [-verbose] [-wait] [-config boot.properties] [parameters]");
			System.exit(1);
		}

		List list = new ArrayList();
		Boolean start = new Boolean(true);

		for (int i = 0; i < args.length; i++)
		{
			if (args[i] == null) continue;

			if (args[i].equalsIgnoreCase("-status") && args.length > i + 1)
			{
				if (args[i + 1].equalsIgnoreCase("start"))
				{
					start = new Boolean(true);
					i++;
				}
				if (args[i + 1].equalsIgnoreCase("stop"))
				{
					start = new Boolean(false);
					i++;
				}
			}
			else if (args[i].equalsIgnoreCase("-verbose")) verbose = true;
			else if (args[i].equalsIgnoreCase("-config") && args.length > i + 1)
			{
				bootFileName = args[i + 1];
				i++;
			}
			else if (args[i].equalsIgnoreCase("-wait") && args.length > i + 1)
			{
				try
				{
					Thread.sleep(Long.parseLong(args[i + 1]));
				}
				catch(Exception e) {}
				i++;
			}
			else list.add(args[i]);
		}

		//get configuration
		Properties configuration = getConfiguration();

		//get system variables.
		getSystemVariables(configuration);

		//set custom variable environment.
		setCustomEnvironment(configuration);

		//run methods
		try
		{
			if (start.booleanValue()) new Main().start(configuration, list);
				else new Main().stop(configuration);
		}
		catch (Throwable e)
		{
			System.err.println("Throwable: " + e.getMessage());
			if (verbose) e.printStackTrace();

			System.exit(2);
		}
	}

	/**
	 * Get debugging status.
	 */
	public static boolean getDebug()
	{
		return verbose;
	}

	/**
	 * Get boot parent directory.
	 */
	public static String getBootPath()
	{
		String directory = Main.class.getProtectionDomain().getCodeSource().getLocation().getFile();
		File file = new File(directory);

		String home = URLDecoder.decode(file.getParentFile().getParentFile().getAbsolutePath());

		return home;
	}

	/**
	 * Invoke main class from specified library in boot configuration file.
	 *
	 * @param classloader class loader extracted from the main thread and with filled classpath structure.
	 * @param classname   the name of the library that will be executed
	 * @param args		 arguments for the actual library
	 */
	public static void invokeMain(ClassLoader classloader, String classname, String[] args) throws IllegalAccessException, InvocationTargetException, NoSuchMethodException, ClassNotFoundException
	{
		Class invoked_class = null;
		invoked_class = classloader.loadClass(classname);

		Class[] method_param_types = new Class[1];
		method_param_types[0] = args.getClass();

		Method main = null;
		main = invoked_class.getDeclaredMethod("main", method_param_types);

		Object[] method_params = new Object[1];
		method_params[0] = args;
		main.invoke(null, method_params);
	}

	/**
	 * Set runtime environment with static variable.
	 * @param configuration
	 */
	static void setCustomEnvironment(Properties configuration)
	{
		Enumeration enumeration = configuration.keys();
		while(enumeration != null && enumeration.hasMoreElements())
		{
			String key = (String) enumeration.nextElement();
			if(key != null && key.startsWith("boot.set.environment") && !key.equalsIgnoreCase("boot.set.environment"))
			{
				String variable = key.substring("boot.set.environment".length() + 1);
				String value = replaceVariables(configuration.getProperty(key));

				if (verbose) System.out.println("Environment variable: " + variable + " = " + value);
				System.setProperty(variable, value);
				variables.put(variable.toLowerCase(), value);
			}
		}
	}

	private static String replaceVariables(String argument)
	{
		if (argument != null && argument.length() > 0)
		{
			int index1 = 0, index2;

			while (index1 >= 0)
			{
				index1 = argument.indexOf("${");
				index2 = argument.indexOf("}", index1);

				if(index1 >= 0 && index2 > index1)
				{
					String key = argument.substring(index1 + 2, index2).toLowerCase();
					String value = (String) variables.get(key);
					if (value == null) value = "";

					argument = argument.substring(0, index1) + value + argument.substring(index2 + 1);
				}
				else index1 = -1;
			}
		}

		return argument;
	}

	/**
	 * Get classpath structure using specified coordinates in the boot configuration file.
	 *
	 * @param configuration
	 * @throws Exception
	 */
	void getClasspath(Properties configuration) throws Exception
	{
		int index = 0;
		boolean next = true;

		while (next)
		{
			String location = configuration.getProperty("boot.classpath.location." + index);
			final boolean includeall = new Boolean(configuration.getProperty("boot.classpath.location." + index + ".includeall", "false")).booleanValue();

			if (location != null && location.length() > 0)
			{
				location = replaceVariables(location);

				File file = new File(location);
				if (file.exists() && file.isDirectory())
				{
					//add directory in the class path;
					classpath.addComponent(file);

					// directory of JAR files
					File[] jars = file.listFiles(new FilenameFilter()
					{
						public boolean accept(File dir, String name)
						{
							String namelc = name.toLowerCase();
							return includeall ? true : namelc.endsWith(".jar") || name.endsWith(".zip");
						}
					});

					for (int i = 0; jars != null && i < jars.length; i++)
					{
						classpath.addComponent(jars[i]);
					}
				}
				else if (file.exists() && file.isFile()) classpath.addComponent(file);
			}
			else next = false;

			index = index + 1;
		}
	}

	/**
	 * Get booter configuration into a <code>Properties</code> structure.
	 *
	 * @return <code>Properties</code> structure
	 */
	private static Properties getConfiguration()
	{
		if (bootFileName == null || bootFileName.length() == 0)
		{
			System.err.println("Invalid configuration file");
			System.exit(3);
		}

		Properties configuration = new Properties();
		File bootFile = new File(bootFileName);

		try
		{
			if(bootFile.exists())
			{
				configuration.load(new FileInputStream(bootFile.getAbsolutePath()));
			}
			else
			{
				InputStream stream = Main.class.getClassLoader().getResourceAsStream(bootFileName);

				if(stream != null) configuration.load(stream);
					else throw new Exception("No file identified using path '" + bootFile.getAbsolutePath() + "' and no input stream identified using resource '" + bootFileName + "'");
			}
		}
		catch (Exception e)
		{
			System.err.println("Error reading configuration: " + e.getMessage());
			if (verbose) e.printStackTrace();

			System.exit(4);
		}

		return configuration;
	}

	/**
	 * Start to boot
	 */
	public void start(Properties configuration, List list)
	{
		String classname = null;

		Listener.startup(configuration);

		try
		{
			getClasspath(configuration);
		}
		catch (Exception e)
		{
			if (verbose) e.printStackTrace();
				else System.err.println("Error getting classpath: " + e.getMessage());

			stop(configuration);
		}

		// okay, classpath complete.
		System.setProperty("java.class.path", classpath.toString());
		ClassLoader cl = classpath.getClassLoader();

		// Invoke main(args) using new classloader.
		Thread.currentThread().setContextClassLoader(cl);

		// re-eval the policy now that env is set
		try
		{
			Policy policy = Policy.getPolicy();
			if (policy != null) policy.refresh();
		}
		catch (Exception e)
		{
			if (verbose) e.printStackTrace();
		}

		//get boot classname
		classname = configuration.getProperty("boot.classname");

		//check if you want to boot without environment parameters.
		String without = configuration.getProperty("boot.without.environment", "false");
		if (new Boolean(without.toLowerCase()).booleanValue()) list.clear();

		//get boot parameters.
		try
		{
			int index = 0;
			boolean next = true;

			List blist = new ArrayList();
			while (next)
			{
				String argument = configuration.getProperty("boot.argument." + index);
				if (argument != null)
				{
					argument = replaceVariables(argument);
					if (verbose) System.err.println("Set input parameter: " + argument);

					//add libraries container in list.
					blist.add(argument);
				}
				else next = false;

				index = index + 1;
			}

			list.addAll(0, blist);
			String args[] = new String[list.size()];
			args = (String[]) list.toArray(args);
			if (verbose) System.err.println("Invoking main method from class: " + classname);

			invokeMain(cl, classname, args);
		}
		catch (Exception e)
		{
			System.err.println("Error invoking method: " + e.getMessage());
			if (verbose) e.printStackTrace();
		}
	}

	/**
	 * Stop booted application (via monitor server)
	 */
	void stop(Properties configuration)
	{
		Listener.shutdown(configuration);
	}

	/**
	 * Launch the appropriate call to the operating system and capture the output.
	 * The following snippet select from all environment variables <code>JAVA_HOME</code> variable and
	 * together with other runtime parameters will generate variables array.
	 */
	public static Map getSystemVariables(Properties configuration)
	{
		if(variables == null) variables = new Hashtable();

		Map env = getEnvironmentVariables();
		if(env.containsKey("java_home")) variables.put("java_home", env.get("java_home"));

		//runtime variables.
		String key = System.getProperty("java.home");
		if (key != null) variables.put("java.home", key);

		if(configuration != null)
		{
			//get application home path.
			String varName = configuration.getProperty("boot.set.environment");

			//set environment variables.
			if (varName != null && varName.length() > 0)
			{
				String path = getBootPath();

				if (verbose) System.out.println("Environment variable: " + varName + " = " + path);
				configuration.setProperty(varName, path);

				System.setProperty(varName, path);
				variables.put(varName.toLowerCase(), path);
			}
		}

		return variables;
	}

	/**
	 * Launch the appropriate call to the operating system and capture the output.
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

			// System.out.println(OS);
			if (OS.indexOf("windows 9") > -1) command = "command.com /c set";
				else if (OS.indexOf("windows") > -1) command = "cmd.exe /c set";
					else command = "env";

			if (verbose) System.out.println("Running environment command for '"+ OS + "' OS: " + command);
			p = r.exec(command);
			BufferedReader br = new BufferedReader(new InputStreamReader(p.getInputStream()));

			String line;

			while ((line = br.readLine()) != null)
			{
				int idx = line.indexOf('=');

				if(idx > 0)
				{
					String key = line.substring(0, idx);
					String value = line.substring(idx + 1);

					variables.put(key.toLowerCase(), value);
				}
			}
		}
		catch(Throwable th)
		{
			System.err.println("Error reading environment variables: " + th.getMessage());
			if (verbose) th.printStackTrace();
		}

		return variables;
	}
}

