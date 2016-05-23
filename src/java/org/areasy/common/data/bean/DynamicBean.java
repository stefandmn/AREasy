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
 * <p>A <strong>DynamicBean</strong> is a Java object that supports properties
 * whose names and data types, as well as values, may be dynamically modified.
 * To the maximum degree feasible, other components of the BeanUtils package
 * will recognize such beans and treat them as standard JavaBeans for the
 * purpose of retrieving and setting property values.</p>
 *
 * @version $Id: DynamicBean.java,v 1.2 2008/05/14 09:32:37 swd\stefan.damian Exp $
 */

public interface DynamicBean
{


	/**
	 * Does the specified mapped property contain a value for the specified
	 * key value?
	 *
	 * @param name Name of the property to check
	 * @param key  Name of the key to check
	 * @throws IllegalArgumentException if there is no property
	 *                                  of the specified name
	 */
	public boolean contains(String name, String key);


	/**
	 * Return the value of a simple property with the specified name.
	 *
	 * @param name Name of the property whose value is to be retrieved
	 * @throws IllegalArgumentException if there is no property
	 *                                  of the specified name
	 */
	public Object get(String name);


	/**
	 * Return the value of an indexed property with the specified name.
	 *
	 * @param name  Name of the property whose value is to be retrieved
	 * @param index Index of the value to be retrieved
	 * @throws IllegalArgumentException  if there is no property
	 *                                   of the specified name
	 * @throws IllegalArgumentException  if the specified property
	 *                                   exists, but is not indexed
	 * @throws IndexOutOfBoundsException if the specified index
	 *                                   is outside the range of the underlying property
	 * @throws NullPointerException      if no array or List has been
	 *                                   initialized for this property
	 */
	public Object get(String name, int index);


	/**
	 * Return the value of a mapped property with the specified name,
	 * or <code>null</code> if there is no value for the specified key.
	 *
	 * @param name Name of the property whose value is to be retrieved
	 * @param key  Key of the value to be retrieved
	 * @throws IllegalArgumentException if there is no property
	 *                                  of the specified name
	 * @throws IllegalArgumentException if the specified property
	 *                                  exists, but is not mapped
	 */
	public Object get(String name, String key);


	/**
	 * Return the <code>DynamicClass</code> instance that describes the set of
	 * properties available for this DynamicBean.
	 */
	public DynamicClass getDynamicClass();


	/**
	 * Remove any existing value for the specified key on the
	 * specified mapped property.
	 *
	 * @param name Name of the property for which a value is to
	 *             be removed
	 * @param key  Key of the value to be removed
	 * @throws IllegalArgumentException if there is no property
	 *                                  of the specified name
	 */
	public void remove(String name, String key);


	/**
	 * Set the value of a simple property with the specified name.
	 *
	 * @param name  Name of the property whose value is to be set
	 * @param value Value to which this property is to be set
	 * @throws ConversionException      if the specified value cannot be
	 *                                  converted to the type required for this property
	 * @throws IllegalArgumentException if there is no property
	 *                                  of the specified name
	 * @throws NullPointerException     if an attempt is made to set a
	 *                                  primitive property to null
	 */
	public void set(String name, Object value);


	/**
	 * Set the value of an indexed property with the specified name.
	 *
	 * @param name  Name of the property whose value is to be set
	 * @param index Index of the property to be set
	 * @param value Value to which this property is to be set
	 * @throws ConversionException       if the specified value cannot be
	 *                                   converted to the type required for this property
	 * @throws IllegalArgumentException  if there is no property
	 *                                   of the specified name
	 * @throws IllegalArgumentException  if the specified property
	 *                                   exists, but is not indexed
	 * @throws IndexOutOfBoundsException if the specified index
	 *                                   is outside the range of the underlying property
	 */
	public void set(String name, int index, Object value);


	/**
	 * Set the value of a mapped property with the specified name.
	 *
	 * @param name  Name of the property whose value is to be set
	 * @param key   Key of the property to be set
	 * @param value Value to which this property is to be set
	 * @throws ConversionException      if the specified value cannot be
	 *                                  converted to the type required for this property
	 * @throws IllegalArgumentException if there is no property
	 *                                  of the specified name
	 * @throws IllegalArgumentException if the specified property
	 *                                  exists, but is not mapped
	 */
	public void set(String name, String key, Object value);


}
