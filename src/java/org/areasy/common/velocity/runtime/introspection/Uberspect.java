package org.areasy.common.velocity.runtime.introspection;

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

import java.util.Iterator;

/**
 * 'Federated' introspection/reflection interface to allow the introspection
 * behavior in Velocity to be customized.
 *
 * @version $Id: Uberspect.java,v 1.1 2008/05/25 22:33:15 swd\stefan.damian Exp $
 */
public interface Uberspect
{
	/**
	 * Initializer - will be called before use
	 */
	public void init() throws Exception;

	/**
	 * To support iteratives - #foreach()
	 */
	public Iterator getIterator(Object obj, Information information) throws Exception;

	/**
	 * Returns a general method, corresponding to $foo.bar( $woogie )
	 */
	public VelocityMethod getMethod(Object obj, String method, Object[] args, Information information) throws Exception;

	/**
	 * Property getter - returns VelPropertyGet appropos for #set($foo = $bar.woogie)
	 */
	public VelocityPropertyGet getPropertyGet(Object obj, String identifier, Information information) throws Exception;

	/**
	 * Property setter - returns VelPropertySet appropos for #set($foo.bar = "geir")
	 */
	public VelocityPropertySet getPropertySet(Object obj, String identifier, Object arg, Information information) throws Exception;
}
