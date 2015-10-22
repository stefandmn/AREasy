package org.areasy.common.doclet;

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

import org.areasy.common.support.configuration.Configuration;
import org.areasy.common.support.configuration.providers.properties.BasePropertiesConfiguration;
import org.areasy.common.doclet.document.Fonts;
import org.areasy.common.doclet.document.State;
import org.areasy.common.doclet.utilities.DocletUtility;
import com.sun.javadoc.RootDoc;
import sun.tools.java.ClassPath;

import java.io.File;
import java.io.FileInputStream;
import java.util.*;

/**
 * Handles the PDFDoclet configuration properties.
 * 
 * @version $Id: DefaultConfiguration.java,v 1.3 2008/05/14 09:36:48 swd\stefan.damian Exp $
 */
public class DefaultConfiguration implements AbstractConfiguration
{
	/**
	 * Flag which determines if author should be printed
	 */
	private static boolean isShowAuthorActive = false;

	/**
	 * Flag which determines if method filtering is active.
	 */
	private static boolean isFilterActive = false;

	/**
	 * Flag which determines if version should be printed
	 */
	private static boolean isShowVersionActive = false;

	/**
	 * Flag which determines if since should be printed
	 */
	private static boolean isShowSinceActive = false;

	/**
	 * Flag which determines if summaries should be printed
	 */
	private static boolean isShowSummaryActive = false;

	/**
	 * Flag which determines if inherited summaries should be printed
	 */
	private static boolean isShowInheritedSummaryActive = false;

	/**
	 * Flag which determines if inherited summaries of external classes should be printed
	 */
	private static boolean isShowExternalInheritedSummaryActive = false;

	/**
	 * Flag which determines if a bookmark frame should be created
	 */
	private static boolean isCreateFrame = false;

	/**
	 * Stores package groups defined by the -group parameter.
	 */
	private static Properties groups = new Properties();

	/**
	 * Flag which determines if any internal links should
	 * be created in the document
	 */
	private static boolean isCreateLinksActive = true;

	/**
	 * Configuration file.
	 */
	private static File configFile = null;

	/**
	 * The working directory
	 */
	private static String workDir = null;

	/**
	 * Description content
	 */
	private static String desriptionContent = null;

	/**
	 * The path for the source files
	 */
	private static ClassPath sourcePath;

	/**
	 * Holds the configuration properties
	 */
	private static Configuration configuration;

	/**
	 * Returns all configuration properties with a certain
	 * name prefix. This method is useful to collect a list
	 * of a set of properties grouped by numbering, for examle:
	 * <pre>
	 *   overview.file.1=...
	 *   overview.file.2=...
	 * </pre>
	 *
	 * @param prefix The property name prefix.
	 * @return A Properties object with all properties found, or null,
	 *         if none were found.
	 */
	public static String[] findNumberedProperties(String prefix)
	{
		Map pkgMap = new TreeMap();
		Iterator keys = getConfiguration().getKeys();

		// Find all properties with the prefix and store them
		// in the map to sort them alphabetically
		while (keys.hasNext())
		{
			String name = (String) keys.next();
			if (name.startsWith(prefix)) pkgMap.put(name, name);
		}

		// Now iterate alphabetically
		ArrayList result = new ArrayList();
		for (Iterator i = pkgMap.keySet().iterator(); i.hasNext();)
		{
			String name = (String) i.next();
			if (hasProperty(name))
			{
				String value = getString(name);
				result.add(value);
			}
		}

		if (result.size() == 0) return null;

		return (String[]) result.toArray(new String[result.size()]);
	}

	/**
	 * Returns a Properties object with all groups defined by
	 * the -group parameter.
	 * <p/>
	 * If no groups have been defined, the properties object is empty.
	 *
	 * @return A Properties object where each key is a group name
	 *         (aka bookmark label) and the value a list of colon-separated
	 *         package names (see the documentation of javadoc's -group
	 *         parameter for details).
	 */
	public static Properties getGroups()
	{
		return groups;
	}

	/**
	 * Determines if links should be created.
	 *
	 * @return True if links should be created.
	 */
	public static boolean isLinksCreationActive()
	{
		return isCreateLinksActive;
	}

	/**
	 * Determines if a bookmark frame should be created.
	 *
	 * @return True if the bookmark frame should be created.
	 */
	public static boolean isCreateFrameActive()
	{
		return isCreateFrame;
	}

	/**
	 * Determines if summary tables should be printed.
	 *
	 * @return True if method/field summary tables should be printed.
	 */
	public static boolean isShowSummaryActive()
	{
		return isShowSummaryActive;
	}

	/**
	 * Determines if inherited summary tables should be printed.
	 *
	 * @return True if summaries of inherited methods/fields should be printed.
	 */
	public static boolean isShowInheritedSummaryActive()
	{
		return isShowInheritedSummaryActive;
	}

	/**
	 * Determines if inherited summary tables should be printed
	 * for external classes.
	 *
	 * @return True if summaries of inherited external methods/fields should be printed.
	 */
	public static boolean isShowExternalInheritedSummaryActive()
	{
		return isShowExternalInheritedSummaryActive;
	}

	/**
	 * Determines if version should be printed.
	 *
	 * @return True if version should be printed.
	 */
	public static boolean isShowVersionActive()
	{
		return isShowVersionActive;
	}

	/**
	 * Determines if since should be printed.
	 *
	 * @return True if since should be printed.
	 */
	public static boolean isShowSinceActive()
	{
		return isShowSinceActive;
	}

	/**
	 * Determines if summaries should be printed.
	 *
	 * @return True if summaries should be printed.
	 */
	public static boolean isShowAuthorActive()
	{
		return isShowAuthorActive;
	}

	/**
	 * Determines if filtering (based on tags,
	 * usually custom tags) is active.
	 *
	 * @return True if filtering is active.
	 */
	public static boolean isFilterActive()
	{
		return isFilterActive;
	}

	/**
	 * Returns the String with the comma-separated list
	 * of packages which defines the order of those
	 * packages in the bookmarks frame.
	 *
	 * @return The String with the comma-separated package list.
	 */
	public static String getPackageOrder()
	{
		return configuration.getString(ARG_SORT, null);
	}

	/**
	 * Method getWorkDir returns the current working directory used to resolve
	 * relative paths while looking for files, like target document filename,
	 * config file, title file name.
	 *
	 * @return String The working directory, WITHOUT a directory
	 *         separating character at the end
	 */
	public static String getWorkDir()
	{
		if (workDir == null) workDir = ".";

		if (workDir.endsWith(File.separator)) workDir = workDir.substring(0, workDir.length() - 1);

		return workDir;
	}

	/**
	 * Method getSourcePath returns the classpath for the source path.
	 *
	 * @return ClassPath
	 */
	public static ClassPath getSourcePath()
	{
		return sourcePath;
	}

	/**
	 * Returns the configuration structure of the PDF doclet.
	 *
	 * @return The Properties object with all configuration values.
	 */
	public static Configuration getConfiguration()
	{
		return configuration;
	}

	/**
	 * Set configuration structure for the PDF doclet.
	 */
	public static void setConfiguration(Configuration config)
	{
		configuration = config;
	}

	/**
	 * Returns the value of a certain configuration property.
	 *
	 * @param name The name of the property.
	 * @return The value of the property (may be null).
	 */
	public static String getString(String name)
	{
		return configuration.getString(name, null);
	}

	public static int getInt(String property)
	{
		return configuration.getInt(property);
	}

	public static int getInt(String property, int defaultValue)
	{
		return configuration.getInt(property, defaultValue);
	}

	public static float getFloat(String property)
	{
		return configuration.getFloat(property);
	}

	public static float getFloat(String property, float defaultValue)
	{
		return configuration.getFloat(property, defaultValue);
	}

	/**
	 * Returns the value of a certain configuration property.
	 *
	 * @param name         The name of the property.
	 * @param defaultValue The default value.
	 * @return The value of the property (or the default value).
	 */
	public static String getString(String name, String defaultValue)
	{
		return configuration.getString(name, defaultValue);
	}

	/**
	 * Initializes the configuration by processing the
	 * input (commandline) options and then optionally
	 * reading configuration values from a file.
	 *
	 * @param root The javadoc root object.
	 */
	public static void start(RootDoc root) throws Exception
	{
		String configFilename = null;
		String[][] options = root.options();

		// If a config file was specified, process it first
		for (int i = 0; i < options.length; i++)
		{
			if ((options[i][0].equals("-" + ARG_CONFIG)) && (options[i].length > 1)) configFilename = options[i][1];
		}

		if (configFilename != null)
		{
			configFile = new File(configFilename);

			if (!configFile.exists()) configFile = new File(getWorkDir(), configFilename);

			if (!configFile.exists()) throw new RuntimeException("Configuration file not found: " + configFilename);
			else
			{
				Properties properties = new Properties();
				properties.load(new FileInputStream(configFile));

				configuration = BasePropertiesConfiguration.getConfiguration(properties);
			}
		}

		//set logger (only if is not used an external logger)
		State.setDebug(getBooleanConfigValue(ARG_DEBUG, false));
		DocletUtility.initLogger(getBooleanConfigValue(ARG_DEBUG, false));

		// Then process command line arguments to override file values
		for (int i = 0; i < options.length; i++)
		{
			if (options[i][0].startsWith("-"))
			{
				// Special handling of -group parameters for two reasons:
				// 1. this parameter can appear more than once and
				// 2. it has two arguments
				if (options[i][0].startsWith("-group"))
				{
					String groupName = options[i][1];
					String packages = options[i][2];

					getGroups().setProperty(groupName, packages);
				}
				else
				{
					String propName = options[i][0];
					propName = propName.substring(1, propName.length());
					String propValue = "";

					if (options[i].length > 1) propValue = options[i][1];

					getConfiguration().setKey(propName, propValue);
				}
			}
		}

		processConfiguration();
	}

	/**
	 * Initializes the configuration by processing an input configuration structure
	 *
	 * @param config configuration structure.
	 */
	public static void start(Configuration config)
	{
		configuration = config;
		processConfiguration();
	}

	/**
	 * Processes the configuration Properties and
	 * sets internal values accordingly.
	 */
	private static void processConfiguration()
	{
		if (configuration.getString(ARG_SOURCEPATH, null) != null) sourcePath = new ClassPath(configuration.getString(ARG_SOURCEPATH));

		if (configuration.getString(ARG_FONT_TEXT_NAME, null) != null) Fonts.mapFont(configuration.getString(ARG_FONT_TEXT_NAME), configuration.getString(ARG_FONT_TEXT_ENC), TEXT_FONT);

		if (configuration.getString(ARG_WORKDIR, null) != null) workDir = configuration.getString(ARG_WORKDIR);
		if(workDir == null || workDir.length() == 0)
		{
			if(configFile != null) workDir = configFile.getParent();
			else
			{
				File file = new File(configuration.getString(ARG_PDF, ARG_VAL_PDF));
				workDir = file.getParent();
			}
		}

		// Some backward compatibility stuff
		if (hasProperty("tag." + ARG_AUTHOR)) isShowAuthorActive = getBooleanConfigValue("tag." + ARG_AUTHOR, false);
			else isShowAuthorActive = getBooleanConfigValue(ARG_AUTHOR, false);

		if (hasProperty("tag." + ARG_VERSION)) isShowVersionActive = getBooleanConfigValue("tag." + ARG_VERSION, false);
			else isShowVersionActive = getBooleanConfigValue(ARG_VERSION, false);

		if (hasProperty("tag." + ARG_SINCE)) isShowSinceActive = getBooleanConfigValue("tag." + ARG_SINCE, false);
			else isShowSinceActive = getBooleanConfigValue(ARG_SINCE, false);

		isShowSummaryActive = getBooleanConfigValue(ARG_SUMMARY_TABLE, true);

		if (isShowSummaryActive)
		{
			String inheritedValue = getString(ARG_INHERITED_SUMMARY_TABLE, ARG_VAL_YES);
			if (inheritedValue.equalsIgnoreCase(ARG_VAL_INTERNAL))
			{
				isShowInheritedSummaryActive = true;
				isShowExternalInheritedSummaryActive = false;
			}
			else
			{
				isShowInheritedSummaryActive = getBooleanConfigValue(ARG_INHERITED_SUMMARY_TABLE, isShowSummaryActive);
				if (isShowInheritedSummaryActive) isShowExternalInheritedSummaryActive = true;
			}
		}
		else
		{
			isShowInheritedSummaryActive = false;
			isShowExternalInheritedSummaryActive = false;
		}

		isFilterActive = getBooleanConfigValue(ARG_FILTER, false);
		isCreateLinksActive = getBooleanConfigValue(ARG_CREATE_LINKS, true);
		isCreateFrame = getBooleanConfigValue(ARG_CREATE_FRAME, true);
	}

	/**
	 * Checks if the given property exists in the configuration.
	 *
	 * @param property The name of the property.
	 * @return True if such a property is available in the configuration,
	 *         false if not.
	 */
	public static boolean hasProperty(String property)
	{
		if (getString(property) == null) return false;

		return true;
	}

	/**
	 * Returns the boolean value of a configuration property.
	 * If the property has a value of either "false" or "no",
	 * the method returns false. Otherwise it returns true
	 * if the property exists, but has no value. If the
	 * property does not exist at all, it returns the
	 * given default value.
	 *
	 * @param property     The name of the configuration property.
	 * @param defaultValue The default value for the property if it's not set.
	 * @return The property's value.
	 */
	public static boolean getBooleanConfigValue(String property, boolean defaultValue)
	{
		String value = configuration.getString(property, null);
		boolean result = defaultValue;

		if (value != null && (value.equalsIgnoreCase("no") || value.equalsIgnoreCase("false"))) result = false;
		if (value != null && (value.equalsIgnoreCase("yes") || value.equalsIgnoreCase("true"))) result = true;

		return result;
	}

	/**
	 * Returns the boolean value of a configuration property.
	 * If the property has a value of either "false" or "no",
	 * the method returns false. Otherwise it returns true
	 * if the property exists, but has no value. If the
	 * property does not exist at all, it returns the
	 * given default value.
	 *
	 * @param property     The name of the configuration property.
	 * @param defaultValue The default value for the property if it's not set.
	 * @return The property's value.
	 */
	public static boolean getBooleanConfigValue(String property, String defaultValue)
	{
		String value = getString(property, defaultValue);
		boolean result = new Boolean(defaultValue).booleanValue();

		if (value != null && (value.equalsIgnoreCase("no") || value.equalsIgnoreCase("false"))) result = false;
		if (value != null && (value.equalsIgnoreCase("yes") || value.equalsIgnoreCase("true"))) result = true;

		return result;
	}

	/**
	 * Returns configuration file.
	 */
	public static File getConfigurationFile()
	{
		return configFile;
	}

	public static String getDesriptionContent()
	{
		return desriptionContent;
	}

	public static void setDesriptionContent(String content)
	{
		desriptionContent = content;
	}
}
