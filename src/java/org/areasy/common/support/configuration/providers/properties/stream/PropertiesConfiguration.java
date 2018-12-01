package org.areasy.common.support.configuration.providers.properties.stream;


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

import org.areasy.common.logger.Logger;
import org.areasy.common.logger.LoggerFactory;
import org.areasy.common.support.configuration.ConfigurationEntry;
import org.areasy.common.support.configuration.ConfigurationException;
import org.areasy.common.support.configuration.ConfigurationLocator;
import org.areasy.common.support.configuration.Configuration;
import org.areasy.common.support.configuration.base.BaseConfigurationLocator;
import org.areasy.common.support.configuration.providers.properties.BasePropertiesConfiguration;
import org.areasy.common.data.StringUtility;

import java.util.Iterator;
import java.io.*;

/**
 * This is the "classic" Properties loader which loads the values from
 * a single or multiple files (which can be chained with "include =".
 * All given path references are either absolute or relative to the
 * file name supplied in the Constructor.
 * <p/>
 * In this class, empty PropertyConfigurations can be built, properties
 * added and later saved. include statements are (obviously) not supported
 * if you don't construct a PropertyConfiguration from a file.
 * <p/>
 * If you want to use the getResourceAsStream() trick to load your
 * resources without an absolute path, please take a look at the
 * ClassPropertiesConfiguration which is intended to be used for this.
 *
 * @version $Id: PropertiesConfiguration.java,v 1.2 2008/05/14 09:32:35 swd\stefan.damian Exp $
 */
public class PropertiesConfiguration extends BasePropertiesConfiguration
{
	/** Static logger */
	Logger logger = LoggerFactory.getLog(PropertiesConfiguration.class);

	/**
	 * Creates an empty PropertyConfiguration object which can be
	 * used to synthesize a new Properties file by adding values and
	 * then saving(). An object constructed by this C'tor can not be
	 * tickled into loading included files because it cannot supply a
	 * base for relative includes.
	 */
	public PropertiesConfiguration()
	{
		setLocator(new BaseConfigurationLocator());
	}

	/**
	 * Creates a configuration structure using a specified and fully functional locator.
	 *
	 * @param locator configuration locator (sector file)
	 */
	public PropertiesConfiguration(PropertiesLocator locator)
	{
		setLocator(locator);
	}

	/**
	 * Creates and loads the extended properties from the specified file.
	 * The specified file can contain "include = " properties which then
	 * are loaded and merged into the properties.
	 *
	 * @param filename file which will define the configuration locator
	 * @throws ConfigurationException is any error will occur
	 */
	public PropertiesConfiguration(String filename) throws ConfigurationException
	{
		load(filename, true);
	}

	/**
	 * Load the properties from the given fileName. This method will not read recursively all
	 * included locators.
	 */
	public void load(Object object) throws ConfigurationException
	{
		load(object, true);
	}

	/**
	 * Load the properties from the given fileName. This method will not read recursively all
	 * included locators.
	 *
	 * @param object data object to be loaded
	 * @param all check if load procedure will load current sector and all related sub-sectors
	 * @throws org.areasy.common.support.configuration.ConfigurationException if any configuration error will occur
	 */
	public void load(Object object, boolean all) throws ConfigurationException
	{
		PropertiesLocator locator = new PropertiesLocator( (String) object);
		setLocator(locator);

		locator.read(all);
	}

	/**
	 * Save properties to file(s). This method will save all included locators in root locator.
	 */
	public void save() throws ConfigurationException
	{
		if(getLocator() != null) ((PropertiesLocator)getLocator()).write(true);
			else throw new ConfigurationException("Null locator structure");
	}

	/**
	 * Save properties to a file. This method will not write recursively all included locators.
	 *
	 * @param filename name of the properties file
	 */
	public void save(String filename) throws ConfigurationException
	{
		PropertiesLocator base = new PropertiesLocator(filename);

		getAllEntriesInOneLocator(base, getLocator());

		//save built locator.
		base.write(false);
	}

	/**
	 * Read a configuration entries. Concatenates lines ending with "\".
	 *
	 * @param reader <code>BufferedReader</code> structure
	 * @throws ConfigurationException if any general error will occur
	 */
	public void read(LineNumberReader reader) throws ConfigurationException
	{
		StringBuffer buffer = new StringBuffer();

		try
		{
			String line;

			do
			{
				//read next line.
				line = reader.readLine();

				if(line != null)
				{
					//compose line to be parsed.
					if (line.endsWith("\\"))
					{
						line = line.substring(0, line.length() - 1);

						buffer.append(line);
						buffer.append('\n');
					}
					else
					{
						//create data entry
						buffer.append(line);
						ConfigurationEntry entry = new PropertiesEntry( buffer.toString() );

						if( entry.isData() && containsKey(entry.getKey()) ) getConfigurationEntry(entry.getKey()).addValue(entry.getValue());
							else setConfigurationEntry(entry);

						//cleaning line buffer.
						buffer = new StringBuffer();
					}
				}
			}
			while(line != null);
		}
		catch(Exception e)
		{
			throw new ConfigurationException("Error reading stream configuration structure", e);
		}
		finally
		{
			try
			{
				//close reader.
				if(reader != null) reader.close();
			}
			catch(IOException ioe)
			{
				logger.debug("Error closing reader: " + ioe.getMessage());
			}
		}
	}

	/**
	 * Write configuration entries from a specific locator.
	 *
	 * @param writer <code>PrintWriter</code> structure
	 * @throws org.areasy.common.support.configuration.ConfigurationException if any general error will occur
	 */
	public void write(StringWriter writer) throws ConfigurationException
	{
		Iterator iterator = getKeys();

		if(iterator == null) return;

		try
		{
			while(iterator.hasNext())
			{
				String key = (String) iterator.next();
				ConfigurationEntry entry = getConfigurationEntry( key );

				if(entry.isComment())
				{
					writer.write( entry.getComment() );
					writer.write( "\n" );
				}
				else if(entry.isData())
				{
					for(int j = 0; j < entry.getValues().size(); j++)
					{
						Object valueObject = entry.getValues().get(j);

						if(valueObject != null && valueObject instanceof String)
						{
							String valueString = (String)valueObject;

							if(valueString.indexOf("\r\n") > 0) valueString = StringUtility.replace(valueString, "\r\n", "\\\n");
								else if(valueString.indexOf('\n') > 0) valueString = StringUtility.replace(valueString, "\n", "\\\n");

							valueObject = valueString;
						}

						writer.write( entry.getKey() + " = " + valueObject);
						writer.write( "\n" );
					}
				}
			}
		}
		catch(Exception e)
		{
			throw new ConfigurationException("Error writing stream configuration structure", e);
		}
		finally
		{
			try
			{
				//close reader.
				if(writer != null) writer.close();
			}
			catch(IOException ioe)
			{
				logger.debug("Error closing writer: " + ioe.getMessage());
			}
		}
	}

	/**
	 * Write configuration entries from a specific locator.
	 *
	 * @param config general <code>Configuration</code> structure
	 */
	public void copy(Configuration config)
	{
		Iterator iterator = config.getKeys();

		if(iterator == null) return;

		while(iterator.hasNext())
		{
			String key = (String) iterator.next();
			ConfigurationEntry entry = config.getConfigurationEntry( key );

			setConfigurationEntry(entry);
		}
	}

	/**
	 * Gather all entries from all locators in one big locator.
	 * @param base target configuration locator
	 * @param locator source configuration locator
	 * @throws org.areasy.common.support.configuration.ConfigurationException if any configuration error will occur
	 */
	private void getAllEntriesInOneLocator(ConfigurationLocator base, ConfigurationLocator locator) throws ConfigurationException
	{
		Iterator entries = locator.getAllEntries();
		
		while(entries != null && entries.hasNext())
		{
			ConfigurationEntry entry = (ConfigurationEntry) entries.next();
			ConfigurationEntry copy = (ConfigurationEntry)entry.clone();

			copy.setLocator(base);
			base.setNode(copy);
		}

		if(locator.getChildren() != null && locator.getChildren().size() > 0)
		{
			Iterator childs = locator.getChildren().iterator();

			while(childs.hasNext())
			{
				ConfigurationLocator included = (PropertiesLocator)childs.next();
	        	getAllEntriesInOneLocator(base, included);
			}
		}
	}
}
