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

import org.areasy.common.logger.Logger;
import org.areasy.common.logger.LoggerFactory;
import org.areasy.common.errors.ExceptionUtility;
import org.areasy.common.velocity.runtime.RuntimeService;

/**
 * Factory to grab a template loader.
 *
 * @version $Id: ResourceLoaderFactory.java,v 1.1 2008/05/25 22:33:15 swd\stefan.damian Exp $
 */
public class ResourceLoaderFactory
{
	/** the logger */
	private static Logger logger = LoggerFactory.getLog(ResourceLoaderFactory.class.getName());

	/**
	 * Gets the loader specified in the configuration file.
	 *
	 * @return TemplateLoader
	 */
	public static ResourceLoader getLoader(RuntimeService rs, String loaderClassName) throws Exception
	{
		ResourceLoader loader = null;

		try
		{
			loader = ((ResourceLoader) Class.forName(loaderClassName).newInstance());

			logger.debug("Resource loader instantiated: " + loader.getClass().getName());

			return loader;
		}
		catch (Exception e)
		{
			logger.error("Problem instantiating the template loader.Look at your properties file and make sure the " +
					"name of the template loader is correct. Here is the error: " + e.getMessage());

			logger.debug("Exception", e);

			throw new Exception("Problem initializing template loader: " + loaderClassName + " Error is: " + ExceptionUtility.getStackTrace(e));
		}
	}
}
