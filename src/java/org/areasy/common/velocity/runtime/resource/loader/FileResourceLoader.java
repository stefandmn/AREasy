package org.areasy.common.velocity.runtime.resource.loader;

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

import org.areasy.common.data.StringUtility;
import org.areasy.common.logger.Logger;
import org.areasy.common.logger.LoggerFactory;
import org.areasy.common.support.configuration.Configuration;
import org.areasy.common.velocity.base.ResourceNotFoundException;
import org.areasy.common.velocity.runtime.resource.Resource;

import java.io.*;
import java.util.Hashtable;
import java.util.Vector;

/**
 * A loader for templates stored on the file system.
 *
 * @version $Id: FileResourceLoader.java,v 1.1 2008/05/25 22:33:15 swd\stefan.damian Exp $
 */
public class FileResourceLoader extends ResourceLoader
{
	/** the logger */
	private static Logger logger = LoggerFactory.getLog(ResourceLoaderFactory.class.getName());

	/**
	 * The paths to search for templates.
	 */
	private Vector paths = null;

	/**
	 * Used to map the path that a template was found on
	 * so that we can properly check the modification
	 * times of the files.
	 */
	private Hashtable templatePaths = new Hashtable();

	public void init(Configuration configuration)
	{
		paths = configuration.getVector("path", new Vector());
	}

	/**
	 * Get an InputStream so that the Runtime can build a
	 * template with it.
	 *
	 * @return InputStream containing the template
	 * @throws ResourceNotFoundException if template not found
	 *                                   in the file template path.
	 */
	public synchronized InputStream getResourceStream(String templateName) throws ResourceNotFoundException
	{
		if (templateName == null || templateName.length() == 0) throw new ResourceNotFoundException("Need to specify a file name or file path!");

		String template = StringUtility.normalizePath(templateName);
		if (template == null || template.length() == 0)
		{
			String msg = "File resource error: argument '" + template + "' contains and may be trying to access content outside of template root.  Rejected.";
			logger.error(msg);

			throw new ResourceNotFoundException(msg);
		}

		if (template.startsWith("/")) template = template.substring(1);

		int size = paths.size();
		for (int i = 0; i < size; i++)
		{
			String path = (String) paths.get(i);
			InputStream inputStream = findTemplate(path, template);

			if (inputStream != null)
			{
				templatePaths.put(templateName, path);
				return inputStream;
			}
		}

		throw new ResourceNotFoundException("Cannot find resource");
	}

	/**
	 * Try to find a template given a normalized path.
	 *
	 * @return InputStream input stream that will be parsed
	 */
	private InputStream findTemplate(String path, String template)
	{
		try
		{
			File file = new File(path, template);

			if (file.canRead()) return new BufferedInputStream(new FileInputStream(file.getAbsolutePath()));
				else return null;
		}
		catch (FileNotFoundException fnfe)
		{
			return null;
		}
	}

	/**
	 * How to keep track of all the modified times
	 * across the paths.  Note that a file might have
	 * appeared in a directory which is earlier in the
	 * path; so we should search the path and see if
	 * the file we find that way is the same as the one
	 * that we have cached.
	 */
	public boolean isSourceModified(Resource resource)
	{
		boolean modified = true;

		String fileName = resource.getName();
		String path = (String) templatePaths.get(fileName);
		File currentFile = null;

		for (int i = 0; currentFile == null && i < paths.size(); i++)
		{
			String testPath = (String) paths.get(i);
			File testFile = new File(testPath, fileName);

			if (testFile.canRead()) currentFile = testFile;
		}

		File file = new File(path, fileName);
		if (currentFile == null || !file.exists())
		{
			/*
			 * noop: if the file is missing now (either the cached
			 * file is gone, or the file can no longer be found)
			 * then we leave modified alone (it's set to true); a
			 * reload attempt will be done, which will either use
			 * a new template or fail with an appropriate message
			 * about how the file couldn't be found.
			 */
		}
		else if (currentFile.equals(file) && file.canRead())
		{
			/*
			 * if only if currentFile is the same as file and
			 * file.lastModified() is the same as
			 * resource.getLastModified(), then we should use the
			 * cached version.
			 */
			modified = (file.lastModified() != resource.getLastModified());
		}

		return modified;
	}

	public long getLastModified(Resource resource)
	{
		String path = (String) templatePaths.get(resource.getName());
		File file = new File(path, resource.getName());

		if (file.canRead()) return file.lastModified();
			else return 0;
	}
}
