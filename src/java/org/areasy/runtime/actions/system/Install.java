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

import org.areasy.runtime.RuntimeManager;
import org.areasy.runtime.engine.RuntimeLogger;
import org.areasy.runtime.engine.base.AREasyException;
import org.areasy.runtime.utilities.StreamUtility;
import org.areasy.runtime.utilities.ZipUtility;
import org.areasy.boot.Classpath;
import org.areasy.common.data.NumberUtility;
import org.areasy.common.data.StringUtility;
import org.areasy.common.support.configuration.Configuration;
import org.areasy.common.support.configuration.ConfigurationException;
import org.areasy.common.support.configuration.base.BaseConfiguration;
import org.areasy.common.support.configuration.providers.properties.stream.ConfigurationManager;
import org.areasy.common.support.configuration.providers.properties.stream.PropertiesConfiguration;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;
import java.util.jar.JarFile;
import java.util.jar.Manifest;

/**
 * Package manager to install additional AREasy modules.
 */
public class Install extends Config
{
	/**
	 * Execute the current action.
	 *
	 * @throws org.areasy.runtime.engine.base.AREasyException
	 *          if any error will occur
	 */
	public void run() throws AREasyException
	{
		boolean isRemote = isRemoteAction();
		boolean isPermitted = false;

		//check if the method is executed in runtime mode.
		if(getManager().getExecutionMode() != RuntimeManager.RUNTIME)
		{
			//check if is remote action
			if(isRemote) isPermitted = isAdminUserAuthorized();
				else isPermitted = true;
		}
		else isPermitted = true;

		if(!isPermitted)
		{
			//forbidden shutdown command
			RuntimeLogger.warn("Process action could be executed only from the local server or from an user with administrative privileges");
			getLogger().warn("Process action could be executed only from the local server or from an user with administrative privileges");
			return;
		}

		List cfgInstall = new Vector();
		List cfgResources = new Vector();
		List cfgLibraries = new Vector();
		List cfgRepositories = new Vector();

		String file = getConfiguration().getString("file", getConfiguration().getString("inputfile", null));
		if(file == null || !(new File(file)).exists()) throw new AREasyException("Package file doesn't exist: " + file);

		try
		{
			//identify package file
			JarFile jarLibrary = new JarFile(file);
			Manifest jarManifest = jarLibrary.getManifest();

			String packageName = jarManifest.getMainAttributes().getValue("Product-Name");
			String packageVersion = jarManifest.getMainAttributes().getValue("Product-Version");
			logger.info("Installing package '" + packageName + "', version " + packageVersion);

			//define deployment location
			File destPackage = new File(RuntimeManager.getWorkingDirectory(), "package-" + RuntimeLogger.getChannelName());
			if(destPackage.exists())
			{
				StreamUtility.deleteFolder(destPackage);
				StreamUtility.createFolder(destPackage);
			}
			else StreamUtility.createFolder(destPackage);

			//unzip and investigate content
			ZipUtility.doUnjar(new FileInputStream(file), destPackage);

			//deploying binaries
			File binPackage = new File(destPackage, "bin");
			if(binPackage.exists()) StreamUtility.copyFolderContent(binPackage, RuntimeManager.getBinDirectory());

			//deploying documentation
			File docPackage = new File(destPackage, "doc");
			if(docPackage.exists()) StreamUtility.copyFolderContent(docPackage, RuntimeManager.getDocDirectory());

			//deploying configuration resources
			File cfgPackage = new File(destPackage, "cfg");
			if(docPackage.exists())
			{
				StreamUtility.copyFolderContent(cfgPackage, RuntimeManager.getCfgDirectory());

				File[] cfgFiles = cfgPackage.listFiles();
				for(int x = 0; x < cfgFiles.length; x++)
				{
					if(cfgFiles[x].isFile())
					{
						String fileName = cfgFiles[x].getName();
						if(fileName.endsWith(".properties"))
						{
							if(fileName.startsWith("install")) cfgInstall.add(fileName);
								else cfgResources.add(fileName);
						}
					}
				}
			}

			//deploying libraries
			File libsPackage = new File(destPackage, "libs");
			if(libsPackage.exists())
			{
				StreamUtility.copyFolderContent(libsPackage, RuntimeManager.getLibsDirectory());

				File[] libsFiles = libsPackage.listFiles();
				for(int x = 0; x < libsFiles.length; x++)
				{
					if(libsFiles[x].isFile())
					{
						String fileName = libsFiles[x].getName();
						if(fileName.endsWith(".jar")) cfgLibraries.add(fileName);
					}
					else if(libsFiles[x].isDirectory())
					{
						cfgRepositories.add("${areasy.home}/libs/" + libsFiles[x].getName());

						File subLibsPackage = new File(RuntimeManager.getLibsDirectory(), libsFiles[x].getName());
						File[] subLibsFiles = subLibsPackage.listFiles();

						for(int y = 0; y < subLibsFiles.length; y++)
						{
							String fileName = subLibsFiles[y].getName();
							if(fileName.endsWith(".jar")) cfgLibraries.add(libsFiles[x].getName() + "/" + fileName);
						}
					}
				}
			}

			//delete deployment location
			StreamUtility.deleteFolder(destPackage);

			//load new libraries
			setLibraries(cfgLibraries);

			//set configuration
			setInstall(cfgInstall);
			setConfig(cfgResources);

			//set repositories for libraries
			setRepositories(cfgRepositories);

			//reload configuration
			if(getManager().getExecutionMode() == RuntimeManager.RUNTIME) RuntimeLogger.warn("AREasy configuration will not be reloaded because it was started in runtime mode");
				else reloadConfig();
		}
		catch(Throwable th)
		{
			throw new AREasyException("Package installation error: " + th.getMessage());
		}
	}

	protected void setInstall(List cfgInstall) throws AREasyException, ConfigurationException
	{
		if(cfgInstall == null || cfgInstall.size() == 0) return;

		//read environment type
		String environment = getConfiguration().getString("env", getConfiguration().getString("environment", null));
		String installFile = "install.properties";

		if(environment != null && !StringUtility.equalsIgnoreCase(environment, "production")) installFile = "install-" + environment.toLowerCase() + ".properties";

		if(cfgInstall.contains(installFile))
		{
			ConfigurationManager.update(RuntimeManager.getCfgDirectory()+ File.separator + installFile, RuntimeManager.getCfgDirectory().getPath());
		}

		//remove installer configuration files
		for(int i = 0; i < cfgInstall.size(); i++)
		{
			String file = (String) cfgInstall.get(i);

			try
			{
				StreamUtility.deleteFile(RuntimeManager.getCfgDirectory()+ File.separator + file);
			}
			catch(IOException ioe)
			{
				logger.warn("Installer file couldn't be deleted: " + ioe.getMessage());
				logger.debug("Exception", ioe);
			}
		}
	}

	protected void setConfig(List cfgResources) throws AREasyException, ConfigurationException
	{
		if(cfgResources == null || cfgResources.size() == 0) return;

		List includes = getManager().getConfiguration().getList("include", null);

		if(includes != null && cfgResources.size() > 0)
		{
			for(int i = 0; i < cfgResources.size(); i++)
			{
				String sector = (String) cfgResources.get(i);
				if(!includes.contains(sector)) includes.add(sector);
			}

			//set data configuration
			Configuration data = new BaseConfiguration();
			data.setKey("include", includes);
			String defaultConfigSector = RuntimeManager.getCfgDirectory()+ File.separator + "default.properties";
			ConfigurationManager.update(new PropertiesConfiguration(defaultConfigSector), data);
			RuntimeLogger.info("Configuration sectors have been updated");
		}
	}

	protected void setRepositories(List cfgRepositories) throws AREasyException, ConfigurationException
	{
		if(cfgRepositories == null || cfgRepositories.size() == 0) return;

		int cursorRepository = 0;
		int indexLastRepository = -1;
		String defaultConfigSector = RuntimeManager.getCfgDirectory()+ File.separator + "default.properties";
		PropertiesConfiguration sector = new PropertiesConfiguration(defaultConfigSector);
		Configuration repositories = sector.subset("boot.classpath.location");

		Iterator iterator  = repositories.getKeys();
		while(iterator != null && iterator.hasNext())
		{
			String key = (String) iterator.next();
			String value = repositories.getString(key, null);

			if(NumberUtility.isNumber(key) && value != null)
			{
				int respositoryId = NumberUtility.toInt(key);

				if(respositoryId > cursorRepository) cursorRepository = respositoryId;
				if(cfgRepositories.contains(value)) cfgRepositories.remove(value);
			}
		}

		indexLastRepository = sector.indexOf("boot.classpath.location." + cursorRepository);

		for(int i = 0; i < cfgRepositories.size(); i++)
		{
			String repositoryName = (String) cfgRepositories.get(i);

			cursorRepository++;
			indexLastRepository++;

			sector.addKey(indexLastRepository, "boot.classpath.location." + cursorRepository, repositoryName);
		}

		sector.save();
		RuntimeLogger.info("Library repositories have been updated");
	}

	protected void setLibraries(List libsResources) throws AREasyException
	{
		//add libraries to the classpath
		if(libsResources.size() > 0)
		{
			Classpath.BootClassLoader boot = (Classpath.BootClassLoader)RuntimeManager.class.getClassLoader();

			for(int i = 0; i < libsResources.size(); i++)
			{
				File file = new File (RuntimeManager.getLibsDirectory(), (String)libsResources.get(i));
				boot.addLibrary(file);
			}

			try
			{
				Thread.currentThread().wait(3000);
			}
			catch(Exception e) { /*nothing here */ }

			RuntimeLogger.info("Server classloader has been updated");
		}
	}
}
