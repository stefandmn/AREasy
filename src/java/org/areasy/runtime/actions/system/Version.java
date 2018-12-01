package org.areasy.runtime.actions.system;

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

import org.areasy.runtime.RuntimeAction;
import org.areasy.runtime.RuntimeManager;
import org.areasy.runtime.actions.SystemAction;
import org.areasy.runtime.engine.RuntimeLogger;
import org.areasy.runtime.engine.base.AREasyException;
import org.areasy.common.data.StringUtility;

import java.io.File;
import java.util.List;
import java.util.Vector;
import java.util.jar.JarFile;
import java.util.jar.Manifest;


/**
 * Get runtime and add-ons version(s) action implementation.
 *
 */
public class Version extends SystemAction implements RuntimeAction
{
	/**
	 * Execute 'version' action.
	 * Processing version internal command - status server execution
	 *
	 * @throws org.areasy.runtime.engine.base.AREasyException if any error will occur.
	 */
	public void run() throws AREasyException
	{
		Manifest manifests[] = Version.getManifests("areasy");

		for(int i = 0; i < manifests.length; i++)
		{
			String name = getProductName(manifests[i]);

			if(StringUtility.isNotEmpty(name))
			{
				String text = name.toUpperCase() + " " + getProductVersion(manifests[i]);
				text += "\n" + getProductDescription(manifests[i]);
				if(i + 1 < manifests.length) text += "\n";

				RuntimeLogger.add( text );
			}
		}
	}

	/**
	 * Get <code>Manifest</code> structure from a specified JAR name name.
	 *
	 * @param name library name (must be located in [AREASY_HOME]/libs.
	 * @return ar array of manifest structure.
	 */
	public static Manifest[] getManifests(String name)
	{
		List libraries = new Vector();
		List manifests = new  Vector();

		File home = RuntimeManager.getLibsDirectory();

		if (home.isDirectory() && home.exists())
		{
			for (int i = 0; i < home.listFiles().length; i++)
			{
				File file = home.listFiles()[i];
				if (file.getName().indexOf(name) >= 0) libraries.add(file.getAbsolutePath());
			}
		}

		for(int i = 0; i < libraries.size(); i++)
		{
			String libraryName = (String) libraries.get(i);

			try
			{
				JarFile libraryjar = new JarFile(libraryName);
				manifests.add(libraryjar.getManifest());
			}
			catch (Exception e)
			{
				logger.debug("Error extracting manifest structure from library '" + libraryName + "': " + e.getMessage());
			}
		}

		if(manifests.size() > 0) return (Manifest[]) manifests.toArray(new Manifest[manifests.size()]);
			else return null;
	}

	/**
	 * Get product name.
	 *
	 * @param manifest data manifest structure
	 * @return product name (product code)
	 */
	public static String getProductName(Manifest manifest)
	{
		return manifest.getMainAttributes().getValue("Product-Name");
	}

	/**
	 * Get product description.
	 *
	 * @param manifest data manifest structure
	 * @return product details
	 */
	public static String getProductDescription(Manifest manifest)
	{
		return manifest.getMainAttributes().getValue("Product-Description");
	}

	/**
	 * Get product version.
	 *
	 * @param manifest data manifest structure
	 * @return product version
	 */
	public static String getProductVersion(Manifest manifest)
	{
		return manifest.getMainAttributes().getValue("Product-Version");
	}

	/**
	 * Get product version.
	 *
	 * @param manifest data manifest structure
	 * @return vendor name
	 */
	public static String getProductVendor(Manifest manifest)
	{
		return manifest.getMainAttributes().getValue("Product-Vendor");
	}

	/**
	 * Get product years.
	 *
	 * @param manifest data manifest structure
	 * @return product development period
	 */
	public static String getProductYears(Manifest manifest)
	{
		return manifest.getMainAttributes().getValue("Product-Years");
	}

	/**
	 * Get <code>Manifest</code> structure from <code>eframe-api.jar</code> library.
	 *
	 * @return manifest structure
	 */
	private static Manifest getManifest()
	{
		return getManifests("areasy")[0];
	}

	/**
	 * Get product name.
	 * @return product name (product code)
	 */
	public static String getProductName()
	{
		return getManifest().getMainAttributes().getValue("Product-Name");
	}

	/**
	 * Get product description.
	 * @return product details
	 */
	public static String getProductDescription()
	{
		return getManifest().getMainAttributes().getValue("Product-Description");
	}	

	/**
	 * Get product version.
	 * @return product version
	 */
	public static String getProductVersion()
	{
		return getManifest().getMainAttributes().getValue("Product-Version");
	}

	/**
	 * Get product version.
	 * @return vendor name
	 */
	public static String getProductVendor()
	{
		return getManifest().getMainAttributes().getValue("Product-Vendor");
	}

	/**
	 * Get product years.
	 * @return product development period
	 */
	public static String getProductYears()
	{
		return getManifest().getMainAttributes().getValue("Product-Years");
	}
}
