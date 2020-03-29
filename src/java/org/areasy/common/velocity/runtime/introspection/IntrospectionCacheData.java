package org.areasy.common.velocity.runtime.introspection;

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

/**
 * Holds information for node-local context data introspection
 * information.
 *
 * @version $Id: IntrospectionCacheData.java,v 1.1 2008/05/25 22:33:15 swd\stefan.damian Exp $
 */
public class IntrospectionCacheData
{
	/**
	 * Object to pair with class - currently either a Method or
	 * AbstractExecutor. It can be used in any way the using node
	 * wishes.
	 */
	public Object thingy;

	/*
	 *  Class of context data object associated with the introspection
	 *  information
	 */
	public Class contextData;
}
