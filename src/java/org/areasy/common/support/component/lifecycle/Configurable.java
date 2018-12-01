package org.areasy.common.support.component.lifecycle;

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

import org.areasy.common.support.configuration.Configuration;

/**
 * The Configurable interface is used by components that need to be initialized.
 *
 * @version $Id: Configurable.java,v 1.2 2008/05/14 09:32:39 swd\stefan.damian Exp $
 */
public interface Configurable
{
	/**
	 * Configure and integrate the component
	 *
	 * @param configuration
	 */
	public abstract void configure(Configuration configuration) throws Exception;
}
