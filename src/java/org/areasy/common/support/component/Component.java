package org.areasy.common.support.component;

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

/**
 * Component interface.
 * The purpose is to define an interface to describe configurable, loadable, startable, etc. modules.
 * (java application).
 *
 * @version $Id: Component.java,v 1.2 2008/05/14 09:32:42 swd\stefan.damian Exp $
 */
public interface Component
{
	/**
	 * Check if component was configured.
	 */
	boolean isConfigured();

	/**
	 * Check if component was initialized.
	 * @return
	 */
	boolean isInitialized();

	/**
	 * Get component configuration.
	 * @return
	 */	
	Configuration getConfiguration();
}