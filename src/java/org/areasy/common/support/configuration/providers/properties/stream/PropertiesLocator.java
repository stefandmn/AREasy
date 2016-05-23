package org.areasy.common.support.configuration.providers.properties.stream;

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

import org.areasy.common.logger.Logger;
import org.areasy.common.logger.LoggerFactory;
import org.areasy.common.support.configuration.*;
import org.areasy.common.support.configuration.base.BaseConfigurationLocator;

import java.io.*;
import java.util.Iterator;
import java.util.List;

/**
 * Implementation of locator structure for properties files.
 *
 * @version $Id: PropertiesLocator.java,v 1.4 2008/05/19 20:05:13 swd\stefan.damian Exp $
 */
public class PropertiesLocator extends BaseConfigurationLocator implements ConfigurationStream
{
	/** Static logger */
	private Logger logger = LoggerFactory.getLog(PropertiesLocator.class);

	/** Source for this configuration locator is a file location */
	protected String file = null;

	/**
	 * Default constructor, specifying source location
	 *
	 * @param source current locator location
	 */
	public PropertiesLocator(String source)
	{
		super();
		this.file = source;
	}

	/**
	 * Constructor to define a new locator with a specific parent.
	 *
	 * @param parent parent locator for the current instance
	 * @param source current locator location
	 */
	public PropertiesLocator(ConfigurationLocator parent, String source)
	{
		super(parent);
		this.file = source;
	}

	/**
	 * Get real location from the current locator, considering all parents.
	 */
	public Object getSource()
	{
		File file = new File(this.file);

		if(file.exists()) return file.getAbsolutePath();
			else if(hasParent()) return ((PropertiesLocator)getParent()).getSource(this.file);
				else return this.file;
	}

	/**
	 * Recursive methods to get real location for the current locator.
	 *
	 * @param location specific and granular location for the current locator instance.
	 * @return current locator location
	 */
	protected String getSource(String location)
	{
		File file = new File(this.file);

		if(file.exists() && file.isFile()) return file.getParentFile().getAbsolutePath() + File.separator + location;
			else if(file.exists() && file.isDirectory()) return file.getAbsolutePath() + File.separator + location;
				else if(hasParent()) return ((PropertiesLocator)getParent()).getSource(this.file + File.separator + location);
					else return file.getAbsolutePath() + File.separator + location;
	}

	/**
	 * Get configuration locator identify. This implementation will return input file values.
	 */
	public Object getIdentity()
	{
		return this.file;
	}

	public ConfigurationEntry getConfigurationEntry(String data)
	{
		return new PropertiesEntry(data);
	}

	/**
	 * Get source reader instance.
	 *
	 * @return <code>BufferedReader</code> structure
	 * @throws java.io.IOException if any I/O error will occur
	 */
	protected LineNumberReader getReader() throws IOException
	{
		FileInputStream stream = new FileInputStream( (String)getSource() );

		return new LineNumberReader(new InputStreamReader(stream));
	}

	/**
	 * Read a configuration entries. Concatenates lines ending with "\".
	 *
	 * @param reader <code>BufferedReader</code> structure
	 * @throws ConfigurationException if any general error will occur
	 */
	public void read(BufferedReader reader) throws ConfigurationException
	{
		StringBuffer buffer = new StringBuffer();

		try
		{
			String line = reader.readLine();
			while(line != null)
			{
				//compose line to be parsed.
				if (line.trim().endsWith("\\"))
				{
					line = line.trim().substring(0, line.trim().length() - 1);
					buffer.append(line);
				}
				else
				{
					//create data entry
					buffer.append(line);

					String data = buffer.toString();
					ConfigurationEntry entry = new PropertiesEntry( data );

					//register configuration entry.
					addNode(entry);

					//cleaning line buffer.
					buffer.delete(0, buffer.length());
				}

				//read next line.
				line = reader.readLine();
			}
		}
		catch(Exception e)
		{
			throw new ConfigurationException("Error reading '" + getSource() + "' properties configuration locator", e);
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
	 * Read a configuration entries. Concatenates lines ending with "\".
	 *
	 * @param all specify if all sub sectors will be read or not
	 * @throws ConfigurationException if any general error will occur
	 */
	public void read(boolean all) throws ConfigurationException
	{
		LineNumberReader reader;

		try
		{
			reader = getReader();
		}
		catch(Exception e)
		{
			throw new ConfigurationException("Error reading '" + getSource() + "' properties configuration file", e);
		}

		//read data
		read(reader);

		ConfigurationEntry include = getEntry(ConfigurationEntry.INCLUDE);
		if(all && include != null && include.getLocator().equals(this))
		{
			List values = include.getValues();

			for(int i = 0; i < values.size(); i++)
			{
				PropertiesLocator included = new PropertiesLocator(this, (String)values.get(i));
				addChild(included);

				//read all entries.
				included.read(all);
			}
		}
	}

	/**
	 * Get source writer instance.
	 *
	 * @return <code>PrintWriter</code> structure
	 * @throws java.io.IOException if any I/O error will occur
	 */
	protected PrintWriter getWriter() throws IOException
	{
		return new PrintWriter(new FileOutputStream( (String)getSource()) );
	}

	/**
	 * Write configuration entries from a specific locator.
	 *
	 * @param writer <code>PrintWriter</code> structure
	 * @throws org.areasy.common.support.configuration.ConfigurationException if any general error will occur
	 */
	public void write(PrintWriter writer) throws ConfigurationException
	{
		Iterator iterator = getLocatorEntries();
		if(iterator == null) return;

		try
		{
			while(iterator.hasNext())
			{
				ConfigurationEntry entry = (ConfigurationEntry) iterator.next();

				if(entry.isComment())
				{
					writer.println( entry.getComment() );
				}
				else if(entry.isData())
				{
					for(int j = 0; j < entry.getValues().size(); j++)
					{
						writer.println( entry.getKey() + " = " + entry.getValues().get(j) );
					}
				}
			}
		}
		catch(Exception e)
		{
			throw new ConfigurationException("Error writing '" + getSource() + "' properties configuration locator", e);
		}
		finally
		{
			if(writer != null) writer.close();
		}
	}

	/**
	 * Write configuration entries from a specific locator.
	 *
	 * @param all specify if all sub sectors will be read or not
	 * @throws ConfigurationException if any general error will occur
	 */
	public void write(boolean all) throws ConfigurationException
	{
		PrintWriter writer;

		try
		{
			writer = getWriter();
		}
		catch(IOException e)
		{
			throw new ConfigurationException("Error writing '" + getSource() + "' properties configuration file", e);
		}

		//write data
		write(writer);

		//get inclusions.
		if(all && getChildren() != null && getChildren().size() > 0)
		{
			Iterator childs = getChildren().iterator();

			while(childs.hasNext())
			{
				PropertiesLocator included = (PropertiesLocator)childs.next();

				//write all entries from this configuration locator.
				included.write(all);
			}
		}
	}

	/**
	 * Create configuration structure the current locator.
	 *
	 * @return configuration structure which encapsulate this locator.
	 */
	public Configuration getConfiguration()
	{
		return new PropertiesConfiguration(this);
	}
}
