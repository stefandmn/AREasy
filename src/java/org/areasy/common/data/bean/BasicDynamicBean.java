package org.areasy.common.data.bean;


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

import java.io.Serializable;
import java.lang.reflect.Array;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * <p>Minimal implementation of the <code>DynamicBean</code> interface.  Can be
 * used as a convenience base class for more sophisticated implementations.</p>
 * <p/>
 * <p><strong>IMPLEMENTATION NOTE</strong> - Instances of this class that are
 * accessed from multiple threads simultaneously need to be synchronized.</p>
 * <p/>
 * <p><strong>IMPLEMENTATION NOTE</strong> - Instances of this class can be
 * successfully serialized and deserialized <strong>ONLY</strong> if all
 * property values are <code>Serializable</code>.</p>
 *
 * @version $Id: BasicDynamicBean.java,v 1.2 2008/05/14 09:32:37 swd\stefan.damian Exp $
 */

public class BasicDynamicBean implements DynamicBean, Serializable
{
	/**
	 * Construct a new <code>DynamicBean</code> associated with the specified
	 * <code>DynamicClass</code> instance.
	 *
	 * @param dynamicClass The DynamicClass we are associated with
	 */
	public BasicDynamicBean(DynamicClass dynamicClass)
	{
		super();
		this.dynamicClass = dynamicClass;
	}

	/**
	 * The <code>DynamicClass</code> "base class" that this DynamicBean
	 * is associated with.
	 */
	protected DynamicClass dynamicClass = null;

	/**
	 * The set of property values for this DynamicBean, keyed by property name.
	 */
	protected HashMap values = new HashMap();

	/**
	 * Does the specified mapped property contain a value for the specified
	 * key value?
	 *
	 * @param name Name of the property to check
	 * @param key  Name of the key to check
	 * @throws IllegalArgumentException if there is no property
	 *                                  of the specified name
	 */
	public boolean contains(String name, String key)
	{
		Object value = values.get(name);

		if (value == null) throw new NullPointerException("No mapped value for '" + name + "(" + key + ")'");
			else if (value instanceof Map) return (((Map) value).containsKey(key));
				else throw new IllegalArgumentException ("Non-mapped property for '" + name + "(" + key + ")'");
	}

	/**
	 * Return the value of a simple property with the specified name.
	 *
	 * @param name Name of the property whose value is to be retrieved
	 * @throws IllegalArgumentException if there is no property
	 *                                  of the specified name
	 */
	public Object get(String name)
	{
		// Return any non-null value for the specified property
		Object value = values.get(name);
		if (value != null) return (value);

		// Return a null value for a non-primitive property
		Class type = getDynamicProperty(name).getType();
		if (!type.isPrimitive()) return (value);

		// Manufacture default values for primitive properties
		if (type == Boolean.TYPE) return (Boolean.FALSE);
		else if (type == Byte.TYPE) return (new Byte((byte) 0));
		else if (type == Character.TYPE) return (new Character((char) 0));
		else if (type == Double.TYPE) return (new Double((double) 0.0));
		else if (type == Float.TYPE) return (new Float((float) 0.0));
		else if (type == Integer.TYPE) return (new Integer((int) 0));
		else if (type == Long.TYPE) return (new Long((int) 0));
		else if (type == Short.TYPE) return (new Short((short) 0));
		else return (null);
	}


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
	public Object get(String name, int index)
	{
		Object value = values.get(name);

		if (value == null) throw new NullPointerException("No indexed value for '" + name + "[" + index + "]'");
			else if (value.getClass().isArray()) return (Array.get(value, index));
				else if (value instanceof List) return ((List) value).get(index);
					else throw new IllegalArgumentException("Non-indexed property for '" + name + "[" + index + "]'");
	}

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
	public Object get(String name, String key)
	{
		Object value = values.get(name);

		if (value == null) throw new NullPointerException("No mapped value for '" + name + "(" + key + ")'");
			else if (value instanceof Map) return (((Map) value).get(key));
				else throw new IllegalArgumentException("Non-mapped property for '" + name + "(" + key + ")'");
	}


	/**
	 * Return the <code>DynamicClass</code> instance that describes the set of
	 * properties available for this DynamicBean.
	 */
	public DynamicClass getDynamicClass()
	{
		return (this.dynamicClass);
	}


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
	public void remove(String name, String key)
	{
		Object value = values.get(name);

		if (value == null) throw new NullPointerException("No mapped value for '" + name + "(" + key + ")'");
			else if (value instanceof Map) ((Map) value).remove(key);
				else throw new IllegalArgumentException("Non-mapped property for '" + name + "(" + key + ")'");
	}


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
	public void set(String name, Object value)
	{
		DynamicProperty descriptor = getDynamicProperty(name);

		if (value == null)
		{
			if (descriptor.getType().isPrimitive()) throw new NullPointerException("Primitive value for '" + name + "'");
		}
		else if (!isAssignable(descriptor.getType(), value.getClass())) throw new ConversionException("Cannot assign value of type '" + value.getClass().getName() + "' to property '" + name + "' of type '" + descriptor.getType().getName() + "'");

		values.put(name, value);
	}


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
	public void set(String name, int index, Object value)
	{
		Object prop = values.get(name);

		if (prop == null) throw new NullPointerException("No indexed value for '" + name + "[" + index + "]'");
		else if (prop.getClass().isArray()) Array.set(prop, index, value);
		else if (prop instanceof List)
		{
			try
			{
				((List) prop).set(index, value);
			}
			catch (ClassCastException e)
			{
				throw new ConversionException(e.getMessage());
			}
		}
		else throw new IllegalArgumentException("Non-indexed property for '" + name + "[" + index + "]'");
	}


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
	public void set(String name, String key, Object value)
	{

		Object prop = values.get(name);
		if (prop == null) throw new NullPointerException("No mapped value for '" + name + "(" + key + ")'");
			else if (prop instanceof Map) ((Map) prop).put(key, value);
				else throw new IllegalArgumentException("Non-mapped property for '" + name + "(" + key + ")'");
	}

	/**
	 * Return the property descriptor for the specified property name.
	 *
	 * @param name Name of the property for which to retrieve the descriptor
	 * @throws IllegalArgumentException if this is not a valid property
	 *                                  name for our DynamicClass
	 */
	protected DynamicProperty getDynamicProperty(String name)
	{
		DynamicProperty descriptor = getDynamicClass().getDynamicProperty(name);
		if (descriptor == null)throw new IllegalArgumentException("Invalid property name '" + name + "'");

		return (descriptor);
	}


	/**
	 * Is an object of the source class assignable to the destination class?
	 *
	 * @param dest   Destination class
	 * @param source Source class
	 */
	protected boolean isAssignable(Class dest, Class source)
	{
		if (dest.isAssignableFrom(source) || ((dest == Boolean.TYPE) && (source == Boolean.class)) ||
				((dest == Byte.TYPE) && (source == Byte.class)) || ((dest == Character.TYPE) && (source == Character.class)) ||
				((dest == Double.TYPE) && (source == Double.class)) || ((dest == Float.TYPE) && (source == Float.class)) ||
				((dest == Integer.TYPE) && (source == Integer.class)) || ((dest == Long.TYPE) && (source == Long.class)) ||
				((dest == Short.TYPE) && (source == Short.class))) return (true);
			else return (false);
	}
}
