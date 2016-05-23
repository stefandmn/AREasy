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

import org.areasy.common.data.StringUtility;
import org.areasy.common.support.configuration.ConfigurationException;
import org.areasy.common.support.configuration.Configuration;
import org.areasy.common.support.configuration.ConfigurationEntry;
import org.areasy.common.support.configuration.base.BaseConfigurationEntry;
import org.areasy.common.support.configuration.base.BaseConfiguration;
import org.areasy.common.logger.Logger;
import org.areasy.common.logger.LoggerFactory;

import java.util.List;
import java.util.Iterator;
import java.util.Vector;
import java.io.File;

public class ConfigurationManager
{
	/** Static logger */
	private static Logger logger = LoggerFactory.getLog(ConfigurationManager.class);

	/**
	 * Update and save on the file system a configuration sector
	 *
	 * @param data data structure loaded from configuration sector
	 * @param subconfig configuration structure that have to be applied on the configuration sector
	 *
	 * @throws ConfigurationException if any error will occur
	 */
	public static void update(PropertiesConfiguration data, Configuration subconfig) throws ConfigurationException
	{
		Iterator iterator = subconfig.getKeys();

		while(iterator != null && iterator.hasNext())
		{
			String key = (String) iterator.next();

			String tmpDataString = null;
			List tmpDataList = subconfig.getList(key, null);

			//process commands
			if(key != null && key.startsWith("comment."))
			{
				//execute comment operation
				key = key.substring("comment".length());
				ConfigurationEntry entry = data.getConfigurationEntry(key);

				if(entry != null)
				{
					entry.convertInComment();
					logger.debug("Property is converted in comment: " + key);
				}
				else
				{
					entry = new PropertiesEntry( key, tmpDataString);
					entry.convertInComment();
					logger.debug("Property is added as a comment: " + key);
				}
			}
			else if(key != null && key.startsWith("uncomment."))
			{
				//execute uncomment operation
				List list = data.getComments(subconfig.getString(key, null));

				for(int j = 0; list != null && j < list.size(); j++)
				{
					ConfigurationEntry entry = (ConfigurationEntry) list.get(j);
					if(entry.isComment())
					{
						boolean converted = entry.convertInData();

						if(converted) logger.debug("Comment is converted in data: " + key.substring("uncomment".length()));
							else logger.debug("Comment is already converted or cannot be converted in data: " + key.substring("uncomment".length()));
					}
				}
			}
			else if(key != null && key.startsWith("add."))
			{
				key = key.substring("add.".length());

				if(tmpDataList != null && tmpDataList.size() > 0)
				{
					ConfigurationEntry entry = data.getConfigurationEntry(key);

					if(entry != null)
					{
						for(int i = 0; i < tmpDataList.size(); i++)
						{
							tmpDataString = (String) tmpDataList.get(i);
							List entryValues = entry.getValues();

							if(entryValues == null || entryValues.size() == 0 || (entryValues.size() == 1 && StringUtility.isEmpty((String)entryValues.get(0)))) entry.setValue(tmpDataString);
								else if(!entryValues.contains(tmpDataString)) entry.addValue(tmpDataString);
						}
					}
					else
					{
						entry = new PropertiesEntry(key, tmpDataList);
						data.addConfigurationEntry(entry);
					}
				}
			}
			else if(key != null && key.startsWith("delete."))
			{
				key = key.substring("delete.".length());

				if(tmpDataList != null && tmpDataList.size() > 0)
				{
					ConfigurationEntry entry = data.getConfigurationEntry(key);

					for(int i = 0; entry != null && i < tmpDataList.size(); i++)
					{
						tmpDataString = (String) tmpDataList.get(i);
						List entryValues = entry.getValues();

						if(entryValues != null && !entryValues.contains(tmpDataString)) entry.getValues().remove(tmpDataString);
					}

					if(entry != null && (entry.getValues() == null || entry.getValues().size() == 0)) data.removeKey(key);
				}
			}
			else if(key != null)
			{
				tmpDataString = subconfig.getString(key, null);
				if(key.startsWith("set.")) key = key.substring("set.".length());

				//set normal property
				if(tmpDataList == null || tmpDataList.size() <= 1)
				{
					data.setConfigurationEntry(new PropertiesEntry( key, tmpDataString) );
					logger.debug("Set string property: " + key + " = " + tmpDataString);
				}
				else
				{
					data.setConfigurationEntry(new PropertiesEntry( key, tmpDataList) );
					logger.debug("Set list property: " + key);
				}
			}
		}

		data.save();
		logger.info("Source configuration file updated: " + data.getLocator().getSource());
	}

	/**
	 * Update one or many configuration sectors using a configuration mapping
	 *
	 * @param configFile configuration mapping file
	 * @param remotePath location where could be found the configuration sectors pointed out in the configuration mapping
	 * @throws ConfigurationException if any error will occur.
	 */
	public static void update(String configFile, String remotePath) throws ConfigurationException
	{
		PropertiesConfiguration config = new PropertiesConfiguration(configFile);

		List sources = config.getList("deploy.config.sources", null);
		if(sources == null || sources.isEmpty())
		{
			logger.warn("Deployment configuration didn't include sources");
			return;
		}

		for(int i = 0; i < sources.size(); i++)
		{
			String source = (String) sources.get(i);
			File file = new File(remotePath, config.getString("deploy.config.source." + source + ".file", null));

			if(!file.exists())
			{
				logger.warn("Configuration source file '" + source + "' doesn't exist: " + file.getPath());
				continue;
			}
			else logger.info("Updating configuration source file '" + source + "': " + file.getPath());

			try
			{
				PropertiesConfiguration data = new PropertiesConfiguration(file.getPath());
				Configuration subconfig = config.subset("deploy.config.source." + source + ".property");

				update(data, subconfig);
			}
			catch(Throwable th)
			{
				logger.error("Error updating source configuration file: " + th.getMessage());
				logger.debug("Exception", th);
			}
		}
	}

	public static void main(String args[])
	{
		//get input parameters.
		Configuration config = getConfiguration(args);

		String configFile = config.getString("configfile", null);
		logger.info("Configuration file: " + configFile);

		String remotePath = config.getString("remotepath", null);
		logger.info("Remote location path: " + remotePath);

		try
		{
			update(configFile, remotePath);
		}
		catch(Throwable th)
		{
			logger.error("Error running update action: " + th.getMessage());
			logger.debug("Exception", th);
		}
	}

	/**
	 * Transform arguments into a configuration structure.
	 *
	 * @param params action parameters
	 * @return translated configuration structure from input arguments.
	 */
	public static Configuration getConfiguration(String[] params)
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

						current = new BaseConfigurationEntry(key, value);
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
			else if(params[i] != null && current != null) current.addValue(params[i]);
		}

		//generates configuration structure.
		for(int i = 0; i < list.size(); i++) configuration.addConfigurationEntry( (ConfigurationEntry)list.get(i) );

		return configuration;
	}
}
