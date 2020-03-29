package org.areasy.common.support.configuration.providers.properties.rstream;

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

import org.areasy.common.support.configuration.ConfigurationEntry;
import org.areasy.common.support.configuration.ConfigurationException;
import org.areasy.common.support.configuration.base.BaseConfigurationEntry;
import org.areasy.common.support.configuration.providers.properties.stream.PropertiesConfiguration;

/**
 * Loads the configuration from the classpath utilizing a specified class to get
 * the classloader from. The properties file will be attempted to be loaded
 * first from the classes package directory and then from the class path in
 * general.
 * <p/>
 * This class does not support an empty constructor and saving of a
 * synthesized properties file. Use PropertiesConfiguration for this.
 *
 * @version $Id: ClassPropertiesConfiguration.java,v 1.2 2008/05/14 09:32:42 swd\stefan.damian Exp $
 * @see org.areasy.common.support.configuration.providers.properties.BasePropertiesConfiguration
 */
public class ClassPropertiesConfiguration extends PropertiesConfiguration
{
	/**
	 * Creates and loads an extended properties file from the Class
	 * Resources. Uses the class loader.
	 *
	 * @param resource  The name of the Resource.
	 */
	public ClassPropertiesConfiguration(ClassLoader loader, String resource) throws ConfigurationException
	{
		ClassPropertiesLocator locator = new ClassPropertiesLocator(loader, resource);

		//set locator in configuration store.
		setLocator(locator);

		//read store.
		locator.read(true);
	}

	/**
	 * Creates a configuration structure using a specified and fully functional locator.
	 */
	public ClassPropertiesConfiguration(ClassPropertiesLocator locator)
	{
		setLocator(locator);
	}

	/**
	 * Load the properties from the given fileName. This method will not read recursively all
	 * included locators.
	 *
	 * @param fileName A properties file to load
	 */
	public void load(ClassLoader loader, String fileName) throws ConfigurationException
	{
		if(getLocator() == null)
		{
			ClassPropertiesLocator locator = new ClassPropertiesLocator(loader, fileName);
			setLocator(locator);

			locator.read(false);
		}
		else
		{
			ClassPropertiesLocator locator = new ClassPropertiesLocator(getLocator(), loader, fileName);
			getLocator().addChild(locator);

			getLocator().setNode(new BaseConfigurationEntry(ConfigurationEntry.INCLUDE, fileName));

			locator.read(false);
		}
	}

	/**
	 * Save properties to a file. <b>This method is UNSUPPORTED and NOT IMPLEMENTED</b>e
	 */
	public void save() throws ConfigurationException
	{
		throw new ConfigurationException("Unsupported method.");
	}
}

     

    
