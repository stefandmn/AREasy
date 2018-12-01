package org.areasy.common.support.configuration.providers.database;

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

import org.areasy.common.support.configuration.ConfigurationEntry;
import org.areasy.common.support.configuration.ConfigurationException;
import org.areasy.common.support.configuration.providers.properties.BasePropertiesConfiguration;
import org.areasy.common.support.configuration.providers.properties.stream.PropertiesLocator;

import java.sql.Connection;
import java.util.Iterator;

/**
 * Configuration stored in a database.
 *
 * @version $Id: DatabaseConfiguration.java,v 1.3 2008/05/14 09:32:34 swd\stefan.damian Exp $
 */
public class DatabaseConfiguration extends BasePropertiesConfiguration
{
	/**
	 * Build a configuration from a table containing multiple configurations.
	 *
	 * @param connection  the database connection structure
	 * @param table       the name of the table containing the configurations
	 * @param keyColumn   the column containing the keys of the configuration
	 * @param valueColumn the column containing the values of the configuration
	 */
	public DatabaseConfiguration(Connection connection, String table, String keyColumn, String valueColumn) throws ConfigurationException
	{
		DatabaseLocator locator = new DatabaseLocator(connection, table, keyColumn, valueColumn);
		setLocator(locator);

		locator.read(false);
	}

	/**
	 * Build a configuration from an object.
	 */
	public DatabaseConfiguration(DatabaseLocatorObject object) throws ConfigurationException
	{
		load(object);
	}

	/**
	 * Creates a configuration structure using a specified and fully functional locator.
	 */
	public DatabaseConfiguration(DatabaseLocator locator)
	{
		setLocator(locator);
	}

	/**
	 * Load the properties from the given fileName. This method will not read recursively all
	 * included locators.
	 */
	public void load(Object object) throws ConfigurationException
	{
		DatabaseLocator locator = new DatabaseLocator( (DatabaseLocatorObject)object);
		setLocator(locator);

		locator.read(false);
	}

	/**
	 * Save properties to file(s). This method will save all included locators in root locator.
	 */
	public void save() throws ConfigurationException
	{
		if(getLocator() != null) ((DatabaseLocator)getLocator()).write(true);
			else throw new ConfigurationException("Null locator structure");
	}

	/**
	 * Save configuration entries to a file.
	 *
	 * @param filename name of the properties file
	 */
	public void save(String filename) throws ConfigurationException
	{
		//create new locator.
		PropertiesLocator base = new PropertiesLocator(filename);

		//get all entries and transform it.
		Iterator iterator = getLocator().getAllEntries();
		if(iterator == null) return;

		while(iterator.hasNext())
		{
			ConfigurationEntry entry = (ConfigurationEntry) iterator.next();
			ConfigurationEntry copy = (ConfigurationEntry) entry.clone();

			copy.setLocator(base);
			base.setNode(copy);
		}

		//save built locator.
		base.write(false);
	}
}
