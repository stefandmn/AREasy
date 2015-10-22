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
 * <p>DynamicClass which implements the <code>MutableDynamicClass</code> interface.</p>
 * <p/>
 * <p>A <code>MutableDynamicClass</code> is a specialized extension to <code>DynamicClass</code>
 * that allows properties to be added or removed dynamically.</p>
 * <p/>
 * <p>This implementation has one slightly unusual default behaviour - calling
 * the <code>getDynaProperty(name)</code> method for a property which doesn't
 * exist returns a <code>DynaProperty</code> rather than <code>null</code>. The
 * reason for this is that <code>BeanUtils</code> calls this method to check if
 * a property exists before trying to set the value. This would defeat the object
 * of the <code>SlowDynamicBean</code> which automatically adds missing properties
 * when any of its <code>set()</code> methods are called. For this reason the
 * <code>isDynaProperty(name)</code> method has been added to this implementation
 * in order to determine if a property actually exists. If the more <i>normal</i>
 * behaviour of returning <code>null</code> is required, then this can be achieved
 * by calling the <code>setReturnNull(true)</code>.</p>
 * <p/>
 * <p>The <code>add(name, type, readable, writable)</code> method is not implemented
 * and always throws an <code>UnsupportedOperationException</code>. I believe
 * this attributes need to be added to the <code>DynaProperty</code> class
 * in order to control read/write facilities.</p>
 *
 * @version $Id: SlowDynamicClass.java,v 1.2 2008/05/14 09:32:37 swd\stefan.damian Exp $
 */
public class SlowDynamicClass extends BasicDynamicClass implements MutableDynamicClass
{

	/**
	 * Controls whether changes to this DynamicClass's properties are allowed.
	 */
	protected boolean restricted;

	/**
	 * <p>Controls whether the <code>getDynaProperty()</code> method returns
	 * null if a property doesn't exist - or creates a new one.</p>
	 * <p/>
	 * <p>Default is <code>false</code>.
	 */
	protected boolean returnNull = false;

	/**
	 * Construct a new SlowDynamicClass with default parameters.
	 */
	public SlowDynamicClass()
	{
		this(null, (DynamicProperty[]) null);
	}

	/**
	 * Construct a new SlowDynamicClass with the specified name.
	 *
	 * @param name Name of this DynamicBean class
	 */
	public SlowDynamicClass(String name)
	{
		this(name, (DynamicProperty[]) null);
	}

	/**
	 * Construct a new SlowDynamicClass with the specified name and DynamicBean class.
	 *
	 * @param name          Name of this DynamicBean class
	 * @param dynaBeanClass The implementation class for new instances
	 */
	public SlowDynamicClass(String name, Class dynaBeanClass)
	{
		this(name, dynaBeanClass, null);
	}

	/**
	 * Construct a new SlowDynamicClass with the specified name and properties.
	 *
	 * @param name       Name of this DynamicBean class
	 * @param properties Property descriptors for the supported properties
	 */
	public SlowDynamicClass(String name, DynamicProperty[] properties)
	{
		this(name, SlowDynamicBean.class, properties);
	}

	/**
	 * Construct a new SlowDynamicClass with the specified name, DynamicBean class and properties.
	 *
	 * @param name          Name of this DynamicBean class
	 * @param dynaBeanClass The implementation class for new intances
	 * @param properties    Property descriptors for the supported properties
	 */
	public SlowDynamicClass(String name, Class dynaBeanClass, DynamicProperty properties[])
	{
		super(name, dynaBeanClass, properties);
	}

	/**
	 * <p>Is this DynamicClass currently restricted.</p>
	 * <p>If restricted, no changes to the existing registration of
	 * property names, data types, readability, or writeability are allowed.</p>
	 */
	public boolean isRestricted()
	{
		return restricted;
	}

	/**
	 * <p>Set whether this DynamicClass is currently restricted.</p>
	 * <p>If restricted, no changes to the existing registration of
	 * property names, data types, readability, or writeability are allowed.</p>
	 */
	public void setRestricted(boolean restricted)
	{
		this.restricted = restricted;
	}

	/**
	 * Should this DynamicClass return a <code>null</code> from
	 * the <code>getDynaProperty(name)</code> method if the property
	 * doesn't exist.
	 */
	public boolean isReturnNull()
	{
		return returnNull;
	}

	/**
	 * Set whether this DynamicClass should return a <code>null</code> from
	 * the <code>getDynaProperty(name)</code> method if the property
	 * doesn't exist.
	 */
	public void setReturnNull(boolean returnNull)
	{
		this.returnNull = returnNull;
	}

	/**
	 * Add a new dynamic property with no restrictions on data type,
	 * readability, or writeability.
	 *
	 * @param name Name of the new dynamic property
	 * @throws IllegalArgumentException if name is null
	 * @throws IllegalStateException    if this DynamicClass is currently
	 *                                  restricted, so no new properties can be added
	 */
	public void add(String name)
	{
		add(new DynamicProperty(name));
	}

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
	public void add(String name, Class type)
	{
		add(new DynamicProperty(name, type));
	}

	/**
	 * <p>Add a new dynamic property with the specified data type, readability,
	 * and writeability.</p>
	 * <p/>
	 * <p><strong>N.B.</strong>Support for readable/writeable properties has not been implemented
	 * and this method always throws a <code>UnsupportedOperationException</code>.</p>
	 * <p/>
	 * <p>I'm not sure the intention of the original authors for this method, but it seems to
	 * me that readable/writable should be attributes of the <code>DynaProperty</code> class
	 * (which they are not) and is the reason this method has not been implemented.</p>
	 *
	 * @param name      Name of the new dynamic property
	 * @param type      Data type of the new dynamic property (null for no
	 *                  restrictions)
	 * @param readable  Set to <code>true</code> if this property value
	 *                  should be readable
	 * @param writeable Set to <code>true</code> if this property value
	 *                  should be writeable
	 * @throws UnsupportedOperationException anytime this method is called
	 */
	public void add(String name, Class type, boolean readable, boolean writeable)
	{
		throw new java.lang.UnsupportedOperationException("readable/writable properties not supported");
	}

	/**
	 * Add a new dynamic property.
	 *
	 * @param property Property the new dynamic property to add.
	 * @throws IllegalArgumentException if name is null
	 * @throws IllegalStateException    if this DynamicClass is currently
	 *                                  restricted, so no new properties can be added
	 */
	protected void add(DynamicProperty property)
	{
		if (property.getName() == null) throw new IllegalArgumentException("Property name is missing.");

		if (isRestricted()) throw new IllegalStateException("DynamicClass is currently restricted. No new properties can be added.");

		// Check if property already exists
		if (propertiesMap.get(property.getName()) != null) return;

		// Create a new property array with the specified property
		DynamicProperty[] oldProperties = getDynamicProperties();
		DynamicProperty[] newProperties = new DynamicProperty[oldProperties.length + 1];
		System.arraycopy(oldProperties, 0, newProperties, 0, oldProperties.length);
		newProperties[oldProperties.length] = property;

		// Update the properties
		setProperties(newProperties);
	}

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
	public void remove(String name)
	{
		if (name == null) throw new IllegalArgumentException("Property name is missing.");

		if (isRestricted()) throw new IllegalStateException("DynamicClass is currently restricted. No properties can be removed.");

		// Ignore if property doesn't exist
		if (propertiesMap.get(name) == null) return;

		// Create a new property array of without the specified property
		DynamicProperty[] oldProperties = getDynamicProperties();
		DynamicProperty[] newProperties = new DynamicProperty[oldProperties.length - 1];
		int j = 0;
		for (int i = 0; i < oldProperties.length; i++)
		{
			if (!(name.equals(oldProperties[i].getName())))
			{
				newProperties[j] = oldProperties[i];
				j++;
			}
		}

		// Update the properties
		setProperties(newProperties);

	}

	/**
	 * <p>Return a property descriptor for the specified property.</p>
	 * <p/>
	 * <p>If the property is not found and the <code>returnNull</code> indicator is
	 * <code>true</code>, this method always returns <code>null</code>.</p>
	 * <p/>
	 * <p>If the property is not found and the <code>returnNull</code> indicator is
	 * <code>false</code> a new property descriptor is created and returned (although
	 * its not actually added to the DynamicClass's properties). This is the default
	 * beahviour.</p>
	 * <p/>
	 * <p>The reason for not returning a <code>null</code> property descriptor is that
	 * <code>BeanUtils</code> uses this method to check if a property exists
	 * before trying to set it - since these <i>Slow</i> implementations automatically
	 * add any new properties when they are set, returning <code>null</code> from
	 * this method would defeat their purpose.</p>
	 *
	 * @param name Name of the dynamic property for which a descriptor
	 *             is requested
	 * @throws IllegalArgumentException if no property name is specified
	 */
	public DynamicProperty getDynamicProperty(String name)
	{
		if (name == null) throw new IllegalArgumentException("Property name is missing.");

		DynamicProperty dynamicProperty = (DynamicProperty) propertiesMap.get(name);

		// If it doesn't exist and returnNull is false
		// create a new DynaProperty
		if (dynamicProperty == null && !isReturnNull() && !isRestricted()) dynamicProperty = new DynamicProperty(name);

		return dynamicProperty;
	}

	/**
	 * <p>Indicate whether a property actually exists.</p>
	 * <p/>
	 * <p><strong>N.B.</strong> Using <code>getDynaProperty(name) == null</code>
	 * doesn't work in this implementation because that method might
	 * return a DynaProperty if it doesn't exist (depending on the
	 * <code>returnNull</code> indicator).</p>
	 *
	 * @throws IllegalArgumentException if no property name is specified
	 */
	public boolean isDynamicProperty(String name)
	{
		if (name == null) throw new IllegalArgumentException("Property name is missing.");

		return propertiesMap.get(name) == null ? false : true;
	}

}