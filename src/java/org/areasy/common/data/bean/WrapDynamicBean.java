package org.areasy.common.data.bean;

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
 * <p>Implementation of <code>DynamicBean</code> that wraps a standard JavaBean
 * instance, so that DynamicBean APIs can be used to access its properties.</p>
 * <p/>
 * <p/>
 * The most common use cases for this class involve wrapping an existing java bean.
 * (This makes it different from the typical use cases for other <code>DynamicBean</code>'s.)
 * For example:
 * </p>
 * <code><pre>
 *  Object aJavaBean = ...;
 *  ...
 *  DynamicBean db = new WrapDynamicBean(aJavaBean);
 *  ...
 * </pre></code>
 * <p/>
 * <p><strong>IMPLEMENTATION NOTE</strong> - This implementation does not
 * support the <code>contains()</code> and <code>remove()</code> methods.</p>
 *
 * @version $Id: WrapDynamicBean.java,v 1.2 2008/05/14 09:32:37 swd\stefan.damian Exp $
 */

public class WrapDynamicBean implements DynamicBean
{
	/**
	 * Construct a new <code>DynamicBean</code> associated with the specified
	 * JavaBean instance.
	 *
	 * @param instance JavaBean instance to be wrapped
	 */
	public WrapDynamicBean(Object instance)
	{
		super();

		this.instance = instance;
		this.dynaClass = WrapDynamicClass.createDynamicClass(instance.getClass());
	}

	/**
	 * The <code>DynamicClass</code> "base class" that this DynamicBean
	 * is associated with.
	 */
	protected WrapDynamicClass dynaClass = null;

	/**
	 * The JavaBean instance wrapped by this WrapDynamicBean.
	 */
	protected Object instance = null;

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
		throw new UnsupportedOperationException("WrapDynamicBean does not support contains()");
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
		Object value = null;
		try
		{
			value = PropertyUtility.getSimpleProperty(instance, name);
		}
		catch (Throwable t)
		{
			throw new IllegalArgumentException("Property '" + name + "' has no read method");
		}

		return (value);
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

		Object value = null;
		try
		{
			value = PropertyUtility.getIndexedProperty(instance, name, index);
		}
		catch (IndexOutOfBoundsException e)
		{
			throw e;
		}
		catch (Throwable t)
		{
			throw new IllegalArgumentException ("Property '" + name + "' has no indexed read method");
		}

		return (value);
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
		Object value = null;
		try
		{
			value = PropertyUtility.getMappedProperty(instance, name, key);
		}
		catch (Throwable t)
		{
			throw new IllegalArgumentException("Property '" + name + "' has no mapped read method");
		}

		return (value);
	}


	/**
	 * Return the <code>DynamicClass</code> instance that describes the set of
	 * properties available for this DynamicBean.
	 */
	public DynamicClass getDynamicClass()
	{
		return (this.dynaClass);
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
		throw new UnsupportedOperationException("WrapDynamicBean does not support remove()");
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
		try
		{
			PropertyUtility.setSimpleProperty(instance, name, value);
		}
		catch (Throwable t)
		{
			throw new IllegalArgumentException("Property '" + name + "' has no write method");
		}

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

		try
		{
			PropertyUtility.setIndexedProperty(instance, name, index, value);
		}
		catch (IndexOutOfBoundsException e)
		{
			throw e;
		}
		catch (Throwable t)
		{
			throw new IllegalArgumentException("Property '" + name + "' has no indexed write method");
		}

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
		try
		{
			PropertyUtility.setMappedProperty(instance, name, key, value);
		}
		catch (Throwable t)
		{
			throw new IllegalArgumentException("Property '" + name + "' has no mapped write method");
		}

	}

	/**
	 * Gets the bean instance wrapped by this DynamicBean.
	 * For most common use cases,
	 * this object should already be known
	 * and this method safely be ignored.
	 * But some creators of frameworks using <code>DynamicBean</code>'s may
	 * find this useful.
	 *
	 * @return the java bean Object wrapped by this <code>DynamicBean</code>
	 */
	public Object getInstance()
	{
		return instance;
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
		if (descriptor == null) throw new IllegalArgumentException("Invalid property name '" + name + "'");

		return (descriptor);
	}
}
