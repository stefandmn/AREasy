package org.areasy.common.support.configuration.providers.properties;


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

import org.areasy.common.support.configuration.Configuration;
import org.areasy.common.support.configuration.ConfigurationException;
import org.areasy.common.support.configuration.providers.properties.stream.PropertiesConfiguration;
import org.areasy.common.support.configuration.providers.properties.rstream.ClassPropertiesConfiguration;
import org.areasy.common.support.configuration.base.BaseConfiguration;
import org.areasy.common.data.StringUtility;

import java.util.Enumeration;
import java.util.Iterator;
import java.util.Properties;
import java.io.File;
import java.io.InputStream;

/**
 * loads the configuration from a properties file. <p>
 * <p/>
 * <p>The properties file syntax is explained here:
 * <p/>
 * <ul>
 * <li>
 * Each property has the syntax <code>key = value</code>
 * </li>
 * <li>
 * The <i>key</i> may use any character but the equal sign '='.
 * </li>
 * <li>
 * <i>value</i> may be separated on different lines if a backslash
 * is placed at the end of the line that continues below.
 * </li>
 * <li>
 * If <i>value</i> is a list of strings, each token is separated
 * by a comma ','.
 * </li>
 * <li>
 * Commas in each token are escaped placing a backslash right before
 * the comma.
 * </li>
 * <li>
 * If a <i>key</i> is used more than once, the values are appended
 * like if they were on the same line separated with commas.
 * </li>
 * <li>
 * Blank lines and lines starting with character '#' are skipped.
 * </li>
 * <li>
 * If a property is named "include" (or whatever is defined by
 * setInclude() and getInclude() and the value of that property is
 * the full path to a file on disk, that file will be included into
 * the ConfigurationsRepository. You can also pull in files relative
 * to the parent configuration file. So if you have something
 * like the following:
 * <p/>
 * include = additional.properties
 * <p/>
 * Then "additional.properties" is expected to be in the same
 * directory as the parent configuration file.
 * <p/>
 * Duplicate name values will be replaced, so be careful.
 * <p/>
 * </li>
 * </ul>
 * <p/>
 * <p>Here is an example of a valid extended properties file:
 * <p/>
 * <p><pre>
 *      # lines starting with # are comments
 * <p/>
 *      # This is the simplest property
 *      key = value
 * <p/>
 *      # A long property may be separated on multiple lines
 *      longvalue = aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa \
 *                  aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa
 * <p/>
 *      # This is a property with many tokens
 *      tokens_on_a_line = first token, second token
 * <p/>
 *      # This sequence generates exactly the same result
 *      tokens_on_multiple_lines = first token
 *      tokens_on_multiple_lines = second token
 * <p/>
 *      # commas may be escaped in tokens
 *      commas.excaped = Hi\, what'up?
 * <p/>
 *      # properties can reference other properties
 *      base.prop = /base
 *      first.prop = ${base.prop}/first
 *      second.prop = ${first.prop}/second
 * </pre>
 *
 * @version $Id: BasePropertiesConfiguration.java,v 1.3 2008/05/14 09:32:34 swd\stefan.damian Exp $
 */
public abstract class BasePropertiesConfiguration extends BaseConfiguration
{
	/**
	 * Load configuration from the given locator object.
	 *
	 * @param object identifier.
	 * @throws ConfigurationException if any error will occur
	 */
	public abstract void load(Object object) throws ConfigurationException;

	/**
	 * Save properties to a file.
	 *
	 * @throws ConfigurationException if any error will occur
	 */
	public abstract void save() throws ConfigurationException;

	/**
	 * Save properties to a file.
	 *
	 * @param filename name of the properties file
	 * @throws ConfigurationException if any error will occur
	 */
	public abstract void save(String filename) throws ConfigurationException;

	/**
	 * Convert a standard properties class into a configuration class.
	 *
	 * @param p properties object to convert
	 * @return Configuration configuration created from the Properties
	 */
	public static Configuration getConfiguration(Properties p)
	{
		Configuration config = new BaseConfiguration();
		for (Enumeration e = p.keys(); e.hasMoreElements();)
		{
			String key = (String) e.nextElement();
			config.setKey(key, p.getProperty(key));
		}

		return config;
	}

	/**
	 * Convert a Configuration class into a Properties class. Multvalue keys
	 * will be collapsed by {@link Configuration#getString}.
	 *
	 * @param c Configuration object to convert
	 * @return Properties created from the Configuration
	 */
	public static Properties getProperties(Configuration c)
	{
		Properties props = new Properties();

		Iterator iter = c.getKeys();

		while (iter.hasNext())
		{
			String key = (String) iter.next();
			props.setProperty(key, c.getString(key));
		}

		return props;
	}


	/**
	 * Get configuration structure using an input url name. This method will discoover what kind of source
	 * is specified and will return specialized configuration structure according with input url.
	 *
	 * @param url input url name.
	 * @return configuration structure instance.
	 * @throws ConfigurationException configuration exceptions and errors
	 */
	public static Configuration getConfiguration(String url) throws ConfigurationException
	{
		if(StringUtility.isEmpty(url)) throw new ConfigurationException("Null configuration url");

		File file = new File(url);

		if(file.exists()) return new PropertiesConfiguration(url);
		else
		{
			InputStream resource = BasePropertiesConfiguration.class.getResourceAsStream(url);
			if (resource != null) return new ClassPropertiesConfiguration(BasePropertiesConfiguration.class.getClassLoader(), url);
				else throw new ConfigurationException("Configuration source '" + url +"' is invalid");
		}
	}
}
