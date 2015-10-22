package org.areasy.common.data.bean;

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

/**
 * <p>A <strong>DynamicClass</strong> is a simulation of the functionality of
 * <code>java.lang.Class</code> for classes implementing the
 * <code>DynamicBean</code> interface.  DynamicBean instances that share the same
 * DynamicClass all have the same set of available properties, along with any
 * associated data types, read-only states, and write-only states.</p>
 *
 * @version $Id: DynamicClass.java,v 1.2 2008/05/14 09:32:37 swd\stefan.damian Exp $
 */

public interface DynamicClass
{


	/**
	 * Return the name of this DynamicClass (analogous to the
	 * <code>getName()</code> method of <code>java.lang.Class</code), which
	 * allows the same <code>DynamicClass</code> implementation class to support
	 * different dynamic classes, with different sets of properties.
	 */
	public String getName();


	/**
	 * Return a property descriptor for the specified property, if it exists;
	 * otherwise, return <code>null</code>.
	 *
	 * @param name Name of the dynamic property for which a descriptor
	 *             is requested
	 * @throws IllegalArgumentException if no property name is specified
	 */
	public DynamicProperty getDynamicProperty(String name);


	/**
	 * <p>Return an array of <code>ProperyDescriptors</code> for the properties
	 * currently defined in this DynamicClass.  If no properties are defined, a
	 * zero-length array will be returned.</p>
	 * <p/>
	 * <p><strong>FIXME</strong> - Should we really be implementing
	 * <code>getBeanInfo()</code> instead, which returns property descriptors
	 * and a bunch of other stuff?</p>
	 */
	public DynamicProperty[] getDynamicProperties();


	/**
	 * Instantiate and return a new DynamicBean instance, associated
	 * with this DynamicClass.
	 *
	 * @throws IllegalAccessException if the Class or the appropriate
	 *                                constructor is not accessible
	 * @throws InstantiationException if this Class represents an abstract
	 *                                class, an array class, a primitive type, or void; or if instantiation
	 *                                fails for some other reason
	 */
	public DynamicBean newInstance() throws IllegalAccessException, InstantiationException;
}
