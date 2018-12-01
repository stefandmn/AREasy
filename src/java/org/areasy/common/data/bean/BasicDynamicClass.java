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
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;


/**
 * <p>Minimal implementation of the <code>DynamicClass</code> interface.  Can be
 * used as a convenience base class for more sophisticated implementations.</p> *
 * <p><strong>IMPLEMENTATION NOTE</strong> - The <code>DynamicBean</code>
 * implementation class supplied to our constructor MUST have a one-argument
 * constructor of its own that accepts a <code>DynamicClass</code>.  This is
 * used to associate the DynamicBean instance with this DynamicClass.</p>
 *
 * @version $Id: BasicDynamicClass.java,v 1.2 2008/05/14 09:32:37 swd\stefan.damian Exp $
 */

public class BasicDynamicClass implements DynamicClass, Serializable
{
	/**
	 * Construct a new BasicDynamicClass with default parameters.
	 */
	public BasicDynamicClass()
	{
		this(null, null, null);
	}


	/**
	 * Construct a new BasicDynamicClass with the specified parameters.
	 *
	 * @param name          Name of this DynamicBean class
	 * @param dynaBeanClass The implementation class for new instances
	 */
	public BasicDynamicClass(String name, Class dynaBeanClass)
	{
		this(name, dynaBeanClass, null);
	}


	/**
	 * Construct a new BasicDynamicClass with the specified parameters.
	 *
	 * @param name          Name of this DynamicBean class
	 * @param dynaBeanClass The implementation class for new intances
	 * @param properties    Property descriptors for the supported properties
	 */
	public BasicDynamicClass(String name, Class dynaBeanClass, DynamicProperty properties[])
	{
		super();

		if (name != null) this.name = name;

		if (dynaBeanClass == null) dynaBeanClass = BasicDynamicBean.class;

		setDynamicBeanClass(dynaBeanClass);
		if (properties != null) setProperties(properties);
	}

	/**
	 * The constructor of the <code>dynaBeanClass</code> that we will use
	 * for creating new instances.
	 */
	protected transient Constructor constructor = null;


	/**
	 * The method signature of the constructor we will use to create
	 * new DynamicBean instances.
	 */
	protected static Class constructorTypes[] = {DynamicClass.class};


	/**
	 * The argument values to be passed to the constructore we will use
	 * to create new DynamicBean instances.
	 */
	protected Object constructorValues[] = {this};


	/**
	 * The <code>DynamicBean</code> implementation class we will use for
	 * creating new instances.
	 */
	protected Class dynaBeanClass = BasicDynamicBean.class;


	/**
	 * The "name" of this DynamicBean class.
	 */
	protected String name = this.getClass().getName();


	/**
	 * The set of dynamic properties that are part of this DynamicClass.
	 */
	protected DynamicProperty properties[] = new DynamicProperty[0];


	/**
	 * The set of dynamic properties that are part of this DynamicClass,
	 * keyed by the property name.  Individual descriptor instances will
	 * be the same instances as those in the <code>properties</code> list.
	 */
	protected HashMap propertiesMap = new HashMap();

	/**
	 * Return the name of this DynamicClass (analogous to the
	 * <code>getName()</code> method of <code>java.lang.Class</code), which
	 * allows the same <code>DynamicClass</code> implementation class to support
	 * different dynamic classes, with different sets of properties.
	 */
	public String getName()
	{
		return (this.name);
	}


	/**
	 * Return a property descriptor for the specified property, if it exists;
	 * otherwise, return <code>null</code>.
	 *
	 * @param name Name of the dynamic property for which a descriptor
	 *             is requested
	 * @throws IllegalArgumentException if no property name is specified
	 */
	public DynamicProperty getDynamicProperty(String name)
	{
		if (name == null) throw new IllegalArgumentException("No property name specified");

		return ((DynamicProperty) propertiesMap.get(name));
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
		return (properties);
	}


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
	public DynamicBean newInstance() throws IllegalAccessException, InstantiationException
	{
		try
		{
			// Refind the constructor after a deserialization (if needed)
			if (constructor == null) setDynamicBeanClass(this.dynaBeanClass);

			// Invoke the constructor to create a new bean instance
			return ((DynamicBean) constructor.newInstance(constructorValues));
		}
		catch (InvocationTargetException e)
		{
			throw new InstantiationException(e.getTargetException().getMessage());
		}
	}

	/**
	 * Return the Class object we will use to create new instances in the
	 * <code>newInstance()</code> method.  This Class <strong>MUST</strong>
	 * implement the <code>DynamicBean</code> interface.
	 */
	public Class getDynamicBeanClass()
	{
		return (this.dynaBeanClass);
	}

	/**
	 * Set the Class object we will use to create new instances in the
	 * <code>newInstance()</code> method.  This Class <strong>MUST</strong>
	 * implement the <code>DynamicBean</code> interface.
	 *
	 * @param dynaBeanClass The new Class object
	 * @throws IllegalArgumentException if the specified Class does not
	 *                                  implement the <code>DynamicBean</code> interface
	 */
	protected void setDynamicBeanClass(Class dynaBeanClass)
	{

		// Validate the argument type specified
		if (dynaBeanClass.isInterface()) throw new IllegalArgumentException("Class " + dynaBeanClass.getName() + " is an interface, not a class");
		if (!DynamicBean.class.isAssignableFrom(dynaBeanClass))throw new IllegalArgumentException("Class " + dynaBeanClass.getName() + " does not implement DynamicBean");

		// Identify the Constructor we will use in newInstance()
		try
		{
			this.constructor = dynaBeanClass.getConstructor(constructorTypes);
		}
		catch (NoSuchMethodException e)
		{
			throw new IllegalArgumentException("Class " + dynaBeanClass.getName() + " does not have an appropriate constructor");
		}

		this.dynaBeanClass = dynaBeanClass;

	}


	/**
	 * Set the list of dynamic properties supported by this DynamicClass.
	 *
	 * @param properties List of dynamic properties to be supported
	 */
	protected void setProperties(DynamicProperty properties[])
	{
		this.properties = properties;
		propertiesMap.clear();

		for (int i = 0; i < properties.length; i++)
		{
			propertiesMap.put(properties[i].getName(), properties[i]);
		}
	}
}
