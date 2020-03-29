package org.areasy.common.support.component;

/*
 * Copyright (c) 2007-2020 AREasy Runtime
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

import org.areasy.common.errors.NestableException;
import org.areasy.common.logger.Logger;
import org.areasy.common.logger.LoggerFactory;
import org.areasy.common.support.component.lifecycle.Configurable;
import org.areasy.common.support.component.lifecycle.Initializable;
import org.areasy.common.support.configuration.providers.properties.stream.PropertiesConfiguration;

/**
 * Factory library that knows to load "component" applications.
 *
 * @version $Id: ComponentLoader.java,v 1.2 2008/05/14 09:32:41 swd\stefan.damian Exp $
 */
public class ComponentLoader
{
	/** Library logger */
	private static Logger logger = LoggerFactory.getLog(ComponentLoader.class);

	/**
	 * Load the given component, configure it with the given config
	 * file, and initialize it. <br>
	 * The component must implement the <code>Initializable</code> and
	 * <code>Configurable</code> interfaces.
	 *
	 * @param className  the String class name of the component to load
	 * @param configFile the String path name of the component's config file
	 */
	public static Object load(String className, String configFile) throws Exception
	{
		return load(null, className, configFile);
	}

	/**
	 * Load the given component, configure it with the given config
	 * file, and initialize it. <br>
	 * The component must implement the <code>Initializable</code> and
	 * <code>Configurable</code> interfaces.
	 *
	 * @param className  the String class name of the component to load
	 * @param configFile the String path name of the component's config file
	 */
	public static Object load(ClassLoader loader, String className, String configFile) throws Exception
	{
		Object component = null;

		logger.debug("Attempting to load '" + className + "' module with the configuration file: " + configFile);

		if(loader == null) component = Class.forName(className).newInstance();
			else component = loader.loadClass(className).newInstance();

		if(!(component instanceof Component)) throw new NestableException("Class '" + className + "' could not be loaded because is not a component module.");
		if(!(component instanceof Configurable)) throw new NestableException("Class '" + className + "' could not be loaded because is not a configurable module.");
		if(!(component instanceof Initializable)) throw new NestableException("Class '" + className + "' could not be loaded because is not an initializable module.");

		// configure component using the given config file
		((Configurable) component).configure(new PropertiesConfiguration(configFile));

		// initialize component
		((Initializable) component).initialize();
		logger.debug("'" + className + "' successfully configured and initialized.");

		return component;
	}
}
