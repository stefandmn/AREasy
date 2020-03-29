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

import java.util.Iterator;
import java.util.Map;

/**
 * <p>Provides a <i>light weight</i> <code>DynamicBean</code> facade to a <code>Map</code> with <i>lazy</i> map/list processing.</p>
 * <p/>
 * <p>Its a <i>light weight</i> <code>DynamicBean</code> implementation because there is no
 * actual <code>DynamicClass</code> associated with this <code>DynamicBean</code> - in fact
 * it implements the <code>DynamicClass</code> interface itself providing <i>pseudo</i> DynamicClass
 * behaviour from the actual values stored in the <code>Map</code>.</p>
 * <p/>
 * <p>As well providing rhe standard <code>DynamicBean</code> access to the <code>Map</code>'s properties
 * this class also provides the usual <i>Slow</i> behaviour:</p>
 * <ul>
 * <li>Properties don't need to be pre-defined in a <code>DynamicClass</code></li>
 * <li>Indexed properties (<code>Lists</code> or <code>Arrays</code>) are automatically instantiated
 * and <i>grown</i> so that they are large enough to cater for the index being set.</li>
 * <li>Mapped properties are automatically instantiated.</li>
 * </ul>
 * <p/>
 * <p><b><u><i>Restricted</i> DynamicClass</u></b></p>
 * <p>This class implements the <code>MutableDynamicClass</code> interface.
 * <code>MutableDynamicClass</code> have a facility to <i>restrict</i> the <code>DynamicClass</code>
 * so that its properties cannot be modified. If the <code>MutableDynamicClass</code> is
 * restricted then calling any of the <code>set()</code> methods for a property which
 * doesn't exist will result in a <code>IllegalArgumentException</code> being thrown.</p>
 *
 * @version $Id: SlowDynamicMap.java,v 1.2 2008/05/14 09:32:37 swd\stefan.damian Exp $
 */
public class SlowDynamicMap extends SlowDynamicBean implements MutableDynamicClass
{

	/**
	 * The name of this DynamicClass (analogous to the
	 * <code>getName()</code> method of <code>java.lang.Class</code>).
	 */
	protected String name;

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
	 * Default Constructor.
	 */
	public SlowDynamicMap()
	{
		this(null, (Map) null);
	}

	/**
	 * Construct a new <code>SlowDynaMap</code> with the specified name.
	 *
	 * @param name Name of this DynamicBean class
	 */
	public SlowDynamicMap(String name)
	{
		this(name, (Map) null);
	}

	/**
	 * Construct a new <code>SlowDynaMap</code> with the specified <code>Map</code>.
	 *
	 * @param values The Map backing this <code>SlowDynaMap</code>
	 */
	public SlowDynamicMap(Map values)
	{
		this(null, values);
	}

	/**
	 * Construct a new <code>SlowDynaMap</code> with the specified name and  <code>Map</code>.
	 *
	 * @param name   Name of this DynamicBean class
	 * @param values The Map backing this <code>SlowDynaMap</code>
	 */
	public SlowDynamicMap(String name, Map values)
	{
		this.name = name == null ? "SlowDynaMap" : name;
		this.values = values == null ? newMap() : values;
		this.dynamicClass = this;
	}

	/**
	 * Construct a new <code>SlowDynaMap</code> with the specified properties.
	 *
	 * @param properties Property descriptors for the supported properties
	 */
	public SlowDynamicMap(DynamicProperty[] properties)
	{
		this(null, properties);
	}

	/**
	 * Construct a new <code>SlowDynaMap</code> with the specified name and properties.
	 *
	 * @param name       Name of this DynamicBean class
	 * @param properties Property descriptors for the supported properties
	 */
	public SlowDynamicMap(String name, DynamicProperty[] properties)
	{
		this(name, (Map) null);
		if (properties != null)
		{
			for (int i = 0; i < properties.length; i++)
			{
				add(properties[i]);
			}
		}
	}

	/**
	 * Construct a new <code>SlowDynaMap</code> based on an exisiting DynamicClass
	 *
	 * @param dynamicClass DynamicClass to copy the name and properties from
	 */
	public SlowDynamicMap(DynamicClass dynamicClass)
	{
		this(dynamicClass.getName(), dynamicClass.getDynamicProperties());
	}

	/**
	 * Set the Map backing this <code>DynamicBean</code>
	 */
	public void setMap(Map values)
	{
		this.values = values;
	}

	/**
	 * Set the value of a simple property with the specified name.
	 *
	 * @param name  Name of the property whose value is to be set
	 * @param value Value to which this property is to be set
	 */
	public void set(String name, Object value)
	{
		if (isRestricted() && !values.containsKey(name)) throw new IllegalArgumentException("Invalid property name '" + name + "' (DynamicClass is restricted)");

		values.put(name, value);
	}

	/**
	 * Return the name of this DynamicClass (analogous to the
	 * <code>getName()</code> method of <code>java.lang.Class</code)
	 */
	public String getName()
	{
		return this.name;
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
	 * before trying to set it - since these <i>Map</i> implementations automatically
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

		// If it doesn't exist and returnNull is false
		// create a new DynaProperty
		if (!values.containsKey(name) && isReturnNull()) return null;

		Object value = values.get(name);

		if (value == null) return new DynamicProperty(name);
			else return new DynamicProperty(name, value.getClass());
	}

	/**
	 * <p>Return an array of <code>ProperyDescriptors</code> for the properties
	 * currently defined in this DynamicClass.  If no properties are defined, a
	 * zero-length array will be returned.</p>
	 * <p/>
	 * <p><strong>FIXME</strong> - Should we really be implementing
	 * <code>getBeanInfo()</code> instead, which returns property descriptors
	 * and a bunch of other stuff?</p>
	 */
	public DynamicProperty[] getDynamicProperties()
	{
		int i = 0;
		DynamicProperty[] properties = new DynamicProperty[values.size()];
		Iterator iterator = values.keySet().iterator();

		while (iterator.hasNext())
		{
			String name = (String) iterator.next();
			Object value = values.get(name);
			properties[i++] = new DynamicProperty(name, value == null ? null : value.getClass());
		}

		return properties;
	}

	/**
	 * Instantiate and return a new DynamicBean instance, associated
	 * with this DynamicClass.
	 */
	public DynamicBean newInstance()
	{
		return new SlowDynamicMap(this);
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
	 * Add a new dynamic property with no restrictions on data type,
	 * readability, or writeability.
	 *
	 * @param name Name of the new dynamic property
	 * @throws IllegalArgumentException if name is null
	 */
	public void add(String name)
	{
		add(name, null);
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
		if (name == null) throw new IllegalArgumentException("Property name is missing.");

		if (isRestricted()) throw new IllegalStateException("DynamicClass is currently restricted. No new properties can be added.");

		Object value = values.get(name);

		// Check if the property already exists
		if (value == null) values.put(name, type == null ? null : createProperty(name, type));
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
	 */
	protected void add(DynamicProperty property)
	{
		add(property.getName(), property.getType());
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

		// Remove, if property doesn't exist
		if (values.containsKey(name)) values.remove(name);
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
	 * <p>Indicate whether a property actually exists.</p>
	 * <p/>
	 * <p><strong>N.B.</strong> Using <code>getDynaProperty(name) == null</code>
	 * doesn't work in this implementation because that method might
	 * return a DynaProperty if it doesn't exist (depending on the
	 * <code>returnNull</code> indicator).</p>
	 *
	 * @throws IllegalArgumentException if no property name is specified
	 */
	protected boolean isDynamicProperty(String name)
	{
		if (name == null) throw new IllegalArgumentException("Property name is missing.");

		return values.containsKey(name);
	}
}