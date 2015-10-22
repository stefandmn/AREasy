package org.areasy.common.velocity.runtime.resource.loader;

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

import org.areasy.common.logger.Logger;
import org.areasy.common.logger.LoggerFactory;
import org.areasy.common.velocity.base.ResourceNotFoundException;
import org.areasy.common.velocity.runtime.RuntimeService;

import java.io.InputStream;
import java.net.JarURLConnection;
import java.net.URL;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

/**
 * A small wrapper around a Jar
 *
 * @version $Id: JarHolder.java,v 1.1 2008/05/25 22:33:15 swd\stefan.damian Exp $
 */
public class JarHolder
{
	/** the logger */
	private static Logger logger = LoggerFactory.getLog(ResourceLoaderFactory.class.getName());

	private String urlpath = null;
	private JarFile theJar = null;
	private JarURLConnection conn = null;

	private RuntimeService rsvc = null;

	public JarHolder(RuntimeService rs, String urlpath)
	{
		rsvc = rs;

		this.urlpath = urlpath;
		init();
	}

	public void init()
	{
		try
		{
			URL url = new URL(urlpath);

			conn = (JarURLConnection) url.openConnection();
			conn.setAllowUserInteraction(false);
			conn.setDoInput(true);
			conn.setDoOutput(false);
			conn.connect();
			
			theJar = conn.getJarFile();
		}
		catch (Exception e)
		{
			logger.error("Error establishing connection to JAR: " + e.getMessage());
			logger.debug("Exception", e);
		}
	}

	public void close()
	{
		try
		{
			theJar.close();
		}
		catch (Exception e)
		{
			logger.error("  JarHolder : error Closing JAR the file " + e);
		}

		theJar = null;
		conn = null;
	}

	public InputStream getResource(String theentry) throws ResourceNotFoundException
	{
		InputStream data = null;

		try
		{
			JarEntry entry = theJar.getJarEntry(theentry);

			if (entry != null) data = theJar.getInputStream(entry);
		}
		catch (Exception fnfe)
		{
			logger.error("Error getting resource: " + fnfe.getMessage());
			logger.debug("Exception", fnfe);

			throw new ResourceNotFoundException(fnfe.getMessage());
		}

		return data;
	}

	public Hashtable getEntries()
	{
		Hashtable allEntries = new Hashtable(559);

		Enumeration all = theJar.entries();
		while (all.hasMoreElements())
		{
			JarEntry je = (JarEntry) all.nextElement();

			// We don't map plain directory entries
			if (!je.isDirectory()) allEntries.put(je.getName(), this.urlpath);
		}

		return allEntries;
	}

	public String getUrlPath()
	{
		return urlpath;
	}
}







