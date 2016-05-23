package org.areasy.common.data.bean;

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

/**
 * <p>A specialized extension to <code>DynamicClass</code> that allows properties
 * to be added or removed dynamically.</p>
 * <p/>
 * <p><strong>WARNING</strong> - No guarantees that this will be in the final
 * APIs ... it's here primarily to preserve some concepts that were in the
 * original proposal for further discussion.</p>
 *
 * @version $Id: MutableDynamicClass.java,v 1.2 2008/05/14 09:32:37 swd\stefan.damian Exp $
 */

public interface MutableDynamicClass extends DynamicClass
{


	/**
	 * Add a new dynamic property with no restrictions on data type,
	 * readability, or writeability.
	 *
	 * @param name Name of the new dynamic property
	 * @throws IllegalArgumentException if name is null
	 * @throws IllegalStateException    if this DynamicClass is currently
	 *                                  restricted, so no new properties can be added
	 */
	public void add(String name);


	/**
	 * Add a new dynamic property with the specified data type, but with
	 * no restrictions on readability or writeability.
	 *
	 * @param name Name of the new dynamic property
	 * @param type Data type of the new dynamic property (null for no
	 *             restrictions)
	 * @throws IllegalArgumentException if name is null
	 * @throws IllegalStateException    if this DynamicClass is currently
	 *                                  restricted, so no new properties can be added
	 */
	public void add(String name, Class type);


	/**
	 * Add a new dynamic property with the specified data type, readability,
	 * and writeability.
	 *
	 * @param name      Name of the new dynamic property
	 * @param type      Data type of the new dynamic property (null for no
	 *                  restrictions)
	 * @param readable  Set to <code>true</code> if this property value
	 *                  should be readable
	 * @param writeable Set to <code>true</code> if this property value
	 *                  should be writeable
	 * @throws IllegalArgumentException if name is null
	 * @throws IllegalStateException    if this DynamicClass is currently
	 *                                  restricted, so no new properties can be added
	 */
	public void add(String name, Class type, boolean readable, boolean writeable);


	/**
	 * Is this DynamicClass currently restricted, if so, no changes to the
	 * existing registration of property names, data types, readability, or
	 * writeability are allowed.
	 */
	public boolean isRestricted();


	/**
	 * Remove the specified dynamic property, and any associated data type,
	 * readability, and writeability, from this dynamic class.
	 * <strong>NOTE</strong> - This does <strong>NOT</strong> cause any
	 * corresponding property values to be removed from DynamicBean instances
	 * associated with this DynamicClass.
	 *
	 * @param name Name of the dynamic property to remove
	 * @throws IllegalArgumentException if name is null
	 * @throws IllegalStateException    if this DynamicClass is currently
	 *                                  restricted, so no properties can be removed
	 */
	public void remove(String name);


	/**
	 * Set the restricted state of this DynamicClass to the specified value.
	 *
	 * @param restricted The new restricted state
	 */
	public void setRestricted(boolean restricted);


}
