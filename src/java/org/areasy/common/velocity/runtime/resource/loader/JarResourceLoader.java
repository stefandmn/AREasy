package org.areasy.common.velocity.runtime.resource.loader;

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
import org.areasy.common.logger.Logger;
import org.areasy.common.logger.LoggerFactory;
import org.areasy.common.support.configuration.Configuration;
import org.areasy.common.velocity.base.ResourceNotFoundException;
import org.areasy.common.velocity.runtime.resource.Resource;

import java.io.InputStream;
import java.util.Hashtable;
import java.util.Vector;

/**
 * <p/>
 * ResourceLoader to load templates from multiple Jar files.
 * </p>
 * <p/>
 * The configuration of the JarResourceLoader is straightforward -
 * You simply add the JarResourceLoader to the configuration via
 * </p>
 * <p><pre>
 *    resource.loader = jar
 *    jar.resource.loader.class = org.areasy.common.parser.engines.velocity.runtime.resource.loader.JarResourceLoader
 *    jar.resource.loader.path = list of JAR &lt;URL&gt;s
 * </pre></p>
 * <p/>
 * <p> So for example, if you had a jar file on your local filesystem, you could simply do
 * <pre>
 *    jar.resource.loader.path = jar:file:/opt/myfiles/jar1.jar
 *    </pre>
 * </p>
 * <p> Note that jar specification for the <code>.path</code> configuration property
 * conforms to the same rules for the java.net.JarUrlConnection class.
 * </p>
 * <p/>
 *
 * @version $Id: JarResourceLoader.java,v 1.1 2008/05/25 22:33:15 swd\stefan.damian Exp $
 */
public class JarResourceLoader extends ResourceLoader
{
	/** the logger */
	private static Logger logger = LoggerFactory.getLog(ResourceLoaderFactory.class.getName());

	/**
	 * Maps entries to the parent JAR File
	 * Key = the entry *excluding* plain directories
	 * Value = the JAR URL
	 */
	private Hashtable entryDirectory = new Hashtable(559);

	/**
	 * Maps JAR URLs to the actual JAR
	 * Key = the JAR URL
	 * Value = the JAR
	 */
	private Hashtable jarfiles = new Hashtable(89);

	/**
	 * Called by Velocity to initialize the loader
	 */
	public void init(Configuration configuration)
	{
		Vector paths = configuration.getVector("path");

		if (paths == null || paths.size() == 0)
		{
			paths = configuration.getVector("resource.path");

			if (paths != null && paths.size() > 0)
			{
				logger.warn("You are using a deprecated configuration"
						+ " property for the JarResourceLoader -> 'resource.loader.<name>.resource.path'."
						+ " Please change to the conventional 'resource.loader.<name>.path'.");
			}
		}

		for (int i = 0; i < paths.size(); i++)
		{
			loadJar((String) paths.get(i));
		}
	}

	private void loadJar(String path)
	{
		logger.debug("Trying to load: " + path);

		// Check path information
		if (path == null) logger.error("Can not load JAR - JAR path is null");

		if (!path.startsWith("jar:")) logger.error("JAR path must start with jar: -> see java.net.JarURLConnection for information");

		if (!path.endsWith("!/")) path += "!/";

		// Close the jar if it's already open this is useful for a reload
		closeJar(path);

		// Create a new JarHolder
		JarHolder temp = new JarHolder(rsvc, path);

		// Add it's entries to the entryCollection
		addEntries(temp.getEntries());

		// Add it to the Jar table
		jarfiles.put(temp.getUrlPath(), temp);
	}

	/**
	 * Closes a Jar file and set its URLConnection
	 * to null.
	 */
	private void closeJar(String path)
	{
		if (jarfiles.containsKey(path))
		{
			JarHolder theJar = (JarHolder) jarfiles.get(path);
			theJar.close();
		}
	}

	/**
	 * Copy all the entries into the entryDirectory
	 * It will overwrite any duplicate keys.
	 */
	private synchronized void addEntries(Hashtable entries)
	{
		entryDirectory.putAll(entries);
	}

	/**
	 * Get an InputStream so that the Runtime can build a
	 * template with it.
	 *
	 * @return InputStream containing the template
	 * @throws ResourceNotFoundException if template not found
	 *                                   in the file template path.
	 */
	public synchronized InputStream getResourceStream(String source)
			throws ResourceNotFoundException
	{
		InputStream results = null;

		if (source == null || source.length() == 0)
		{
			throw new ResourceNotFoundException("Need to have a resource!");
		}

		String normalizedPath = StringUtility.normalizePath(source);

		if (normalizedPath == null || normalizedPath.length() == 0)
		{
			String msg = "Argument " + normalizedPath + " contains and may be trying to access content outside of template root. Request is rejected.";
			logger.error(msg);

			throw new ResourceNotFoundException(msg);
		}

		/*
		 *  if a / leads off, then just nip that :)
		 */
		if (normalizedPath.startsWith("/"))
		{
			normalizedPath = normalizedPath.substring(1);
		}

		if (entryDirectory.containsKey(normalizedPath))
		{
			String jarurl = (String) entryDirectory.get(normalizedPath);

			if (jarfiles.containsKey(jarurl))
			{
				JarHolder holder = (JarHolder) jarfiles.get(jarurl);
				results = holder.getResource(normalizedPath);
				return results;
			}
		}

		throw new ResourceNotFoundException("Cannot find resource '" + source + "' source");

	}

	public boolean isSourceModified(Resource resource)
	{
		return true;
	}

	public long getLastModified(Resource resource)
	{
		return 0;
	}
}










