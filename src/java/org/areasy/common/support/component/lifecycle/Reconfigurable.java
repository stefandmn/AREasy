package org.areasy.common.support.component.lifecycle;

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

import org.areasy.common.errors.NestableException;
import org.areasy.common.support.configuration.Configuration;

/**
 * The Reconfigurable interface is used by components that need to be reinitialized.
 *
 * @version $Id: Reconfigurable.java,v 1.2 2008/05/14 09:32:38 swd\stefan.damian Exp $
 */
public interface Reconfigurable
{
	/**
	 * Reconfigure and (re)integrate the component.
	 * This method implementation should involve reinitialization process.
	 *
	 * @param configuration
	 */
	public abstract void reconfigure(Configuration configuration) throws NestableException;
}
