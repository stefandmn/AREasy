package org.areasy.runtime.actions.system;

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

import org.areasy.runtime.RuntimeAction;
import org.areasy.runtime.RuntimeManager;
import org.areasy.runtime.actions.SystemAction;
import org.areasy.runtime.engine.RuntimeLogger;
import org.areasy.runtime.engine.base.AREasyException;
import org.areasy.common.data.StringUtility;
import org.areasy.common.data.type.credential.ObfuscateCredential;
import org.areasy.common.support.configuration.Configuration;
import org.areasy.common.support.configuration.base.BaseConfiguration;
import org.areasy.common.support.configuration.providers.properties.stream.ConfigurationManager;
import org.areasy.common.support.configuration.providers.properties.stream.PropertiesConfiguration;

import java.io.File;
import java.util.List;
import java.util.Vector;

/**
 * Processing config internal action - set server configuration
 */
public class ConfigAction extends SystemAction implements RuntimeAction
{
	/**
	 * Execute 'config' action.
	 * Processing config internal action - set server configuration
	 *
	 * @throws org.areasy.runtime.engine.base.AREasyException if any error will occur.
	 */
	public void run() throws AREasyException
	{
		boolean isRemote = isRemoteAction();
		boolean isPermitted = false;

		//check if is remote action
		if(isRemote) isPermitted = isAdminUserAuthorized();
			else isPermitted = true;

		if(!isPermitted)
		{
			//forbidden shutdown command
			RuntimeLogger.warn("Set configuration action could be executed only from the local server or from an user with administrative privileges");
			getLogger().warn("Set configuration action could be executed only from the local server or from an user with administrative privileges");
		}
		else
		{
			//set sector configuration
			setConfig();

			//reloadConfig configuration
			if(getConfiguration().getBoolean("reload", false))
			{
				//check if the method is executed in runtime mode.
				if(getManager().getExecutionMode() == RuntimeManager.RUNTIME) RuntimeLogger.warn("AREasy has been started in runtime mode");
					else reloadConfig();
			}
		}
	}

	public void setConfig() throws AREasyException
	{
		//read sector configuration
		String sector = getConfiguration().getString("sector", null);

		//validate configuration
		if(sector == null) sector = "default.properties";
			else if(!sector.contains(".properties")) sector += ".properties";

		//read property name that have to be changed
		String property = getConfiguration().getString("property", null);

		//read the new values for the specified properties
		String value = getConfiguration().getString("value", null);
		if(property != null && property.indexOf(".password") > 0 && StringUtility.isNotEmpty(value) && !value.startsWith("OBF:")) value = ObfuscateCredential.obfuscate(value);

		//get list of values
		List values = getConfiguration().getList("values", new Vector());

		//consolidate value
		if(!values.contains(value)) values.add(value);

		//validate property key
		if(property == null)
		{
			RuntimeLogger.warn("No configuration key have to be changed!");
			return;
		}

		try
		{
			String fullSectorPath = RuntimeManager.getCfgDirectory() + File.separator + sector;
			PropertiesConfiguration base = new PropertiesConfiguration(fullSectorPath);

			Configuration data = new BaseConfiguration();
			data.setKey(property, values);

			ConfigurationManager.update(base, data);
		}
		catch(Throwable th)
		{
			RuntimeLogger.error("Error reloading server configuration: " + th.getMessage());
			getLogger().debug("Exception", th);
		}
	}

	public void reloadConfig()
	{
		try
		{
			String sector = RuntimeManager.getCfgDirectory() + File.separator + "default.properties";
			getServer().getManager().setConfiguration(new PropertiesConfiguration(sector));
			getServer().getManager().setRuntimeActions();

			//processing echo action - echo server execution.
			RuntimeLogger.info("AREasy server configuration has been reloaded");
		}
		catch(Throwable th)
		{
			RuntimeLogger.error("Error reloading server configuration: " + th.getMessage());
			getLogger().debug("Exception", th);
		}
	}
}
