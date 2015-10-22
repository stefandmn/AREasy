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

import org.areasy.common.support.configuration.Configuration;
import org.areasy.common.velocity.base.ResourceNotFoundException;
import org.areasy.common.velocity.runtime.resource.Resource;

import java.io.InputStream;

/**
 * ClasspathResourceLoader is a simple loader that will load
 * templates from the classpath.
 * <br>
 * <br>
 * Will load templates from  from multiple instances of
 * and arbitrary combinations of :
 * <ul>
 * <li> jar files
 * <li> zip files
 * <li> template directories (any directory containing templates)
 * </ul>
 * This is a configuration-free loader, in that there are no
 * parameters to be specified in the configuration properties,
 * other than specifying this as the loader to use.  For example
 * the following is all that the loader needs to be functional :
 * <br>
 * <br>
 * resource.loader = class
 * class.resource.loader.class =
 * org.areasy.common.parser.engines.velocity.runtime.resource.loader.ClasspathResourceLoader
 * <br>
 * <br>
 * To use, put your template directories, jars
 * and zip files into the classpath or other mechanisms that make
 * resources accessable to the classloader.
 * <br>
 * <br>
 * I have also tried it with a WAR deployment, and that seemed to
 * work just fine.
 *
 * @version $Id: ClasspathResourceLoader.java,v 1.1 2008/05/25 22:33:15 swd\stefan.damian Exp $
 */
public class ClasspathResourceLoader extends ResourceLoader
{
	/**
	 * This is abstract in the base class, so we need it
	 */
	public void init(Configuration configuration)
	{
		//nothing to do.
	}

	/**
	 * Get an InputStream so that the Runtime can build a
	 * template with it.
	 *
	 * @param name name of template to get
	 * @return InputStream containing the template
	 * @throws ResourceNotFoundException if template not found
	 *                                   in  classpath.
	 */
	public synchronized InputStream getResourceStream(String name) throws ResourceNotFoundException
	{
		InputStream result = null;

		if (name == null || name.length() == 0) throw new ResourceNotFoundException("No template name provided");

		try
		{
			ClassLoader classLoader = this.getClass().getClassLoader();
			result = classLoader.getResourceAsStream(name);
		}
		catch (Exception fnfe)
		{
			throw new ResourceNotFoundException(fnfe.getMessage());
		}

		return result;
	}

	/**
	 * Defaults to return false.
	 */
	public boolean isSourceModified(Resource resource)
	{
		return false;
	}

	/**
	 * Defaults to return 0
	 */
	public long getLastModified(Resource resource)
	{
		return 0;
	}
}

