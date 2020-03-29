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

import java.beans.PropertyDescriptor;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;


/**
 * <p>Implementation of <code>DynamicClass</code> for DynamicBeans that wrap
 * standard JavaBean instances.</p>
 * <p/>
 * <p/>
 * It is suggested that this class should not usually need to be used directly
 * to create new <code>WrapDynamicBean</code> instances.
 * It's usually better to call the <code>WrapDynamicBean</code> constructor directly.
 * For example:</p>
 * <code><pre>
 *   Object javaBean = ...;
 *   DynamicBean wrapper = new WrapDynamicBean(javaBean);
 * </pre></code>
 * <p/>
 *
 * @version $Id: WrapDynamicClass.java,v 1.2 2008/05/14 09:32:37 swd\stefan.damian Exp $
 */

public class WrapDynamicClass implements DynamicClass
{
	/**
	 * Construct a new WrapDynamicClass for the specified JavaBean class.  This
	 * constructor is private; WrapDynamicClass instances will be created as
	 * needed via calls to the <code>createDynamicClass(Class)</code> method.
	 *
	 * @param beanClass JavaBean class to be introspected around
	 */
	private WrapDynamicClass(Class beanClass)
	{
		this.beanClass = beanClass;
		introspect();
	}

	/**
	 * The JavaBean <code>Class</code> which is represented by this
	 * <code>WrapDynamicClass</code>.
	 */
	protected Class beanClass = null;

	/**
	 * The set of PropertyDescriptors for this bean class.
	 */
	protected PropertyDescriptor descriptors[] = null;

	/**
	 * The set of PropertyDescriptors for this bean class, keyed by the
	 * property name.  Individual descriptor instances will be the same
	 * instances as those in the <code>descriptors</code> list.
	 */
	protected HashMap descriptorsMap = new HashMap();

	/**
	 * The set of dynamic properties that are part of this DynamicClass.
	 */
	protected DynamicProperty properties[] = null;

	/**
	 * The set of dynamic properties that are part of this DynamicClass,
	 * keyed by the property name.  Individual descriptor instances will
	 * be the same instances as those in the <code>properties</code> list.
	 */
	protected HashMap propertiesMap = new HashMap();

	/**
	 * The set of <code>WrapDynamicClass</code> instances that have ever been
	 * created, keyed by the underlying bean Class.
	 */
	protected static HashMap dynaClasses = new HashMap();


	/**
	 * Return the name of this DynamicClass (analogous to the
	 * <code>getName()</code> method of <code>java.lang.Class</code), which
	 * allows the same <code>DynamicClass</code> implementation class to support
	 * different dynamic classes, with different sets of properties.
	 */
	public String getName()
	{
		return (this.beanClass.getName());
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
	 * <p>Instantiates a new standard JavaBean instance associated with
	 * this DynamicClass and return it wrapped in a new WrapDynamicBean
	 * instance. <strong>NOTE</strong> the JavaBean should have a
	 * no argument constructor.</p>
	 * <p/>
	 * <strong>NOTE</strong> - Most common use cases should not need to use
	 * this method. It is usually better to create new
	 * <code>WrapDynamicBean</code> instances by calling its constructor.
	 * For example:</p>
	 * <code><pre>
	 *   Object javaBean = ...;
	 *   DynamicBean wrapper = new WrapDynamicBean(javaBean);
	 * </pre></code>
	 * <p/>
	 * (This method is needed for some kinds of <code>DynamicBean</code> framework.)
	 * </p>
	 *
	 * @throws IllegalAccessException if the Class or the appropriate
	 *                                constructor is not accessible
	 * @throws InstantiationException if this Class represents an abstract
	 *                                class, an array class, a primitive type, or void; or if instantiation
	 *                                fails for some other reason
	 */
	public DynamicBean newInstance() throws IllegalAccessException, InstantiationException
	{
		return new WrapDynamicBean(beanClass.newInstance());
	}

	/**
	 * Return the PropertyDescriptor for the specified property name, if any;
	 * otherwise return <code>null</code>.
	 *
	 * @param name Name of the property to be retrieved
	 */
	public PropertyDescriptor getPropertyDescriptor(String name)
	{
		return ((PropertyDescriptor) descriptorsMap.get(name));
	}

	/**
	 * Clear our cache of WrapDynamicClass instances.
	 */
	public static void clear()
	{
		synchronized (dynaClasses)
		{
			dynaClasses.clear();
		}
	}


	/**
	 * Create (if necessary) and return a new <code>WrapDynamicClass</code>
	 * instance for the specified bean class.
	 *
	 * @param beanClass Bean class for which a WrapDynamicClass is requested
	 */
	public static WrapDynamicClass createDynamicClass(Class beanClass)
	{
		synchronized (dynaClasses)
		{
			WrapDynamicClass dynaClass = (WrapDynamicClass) dynaClasses.get(beanClass);
			if (dynaClass == null)
			{
				dynaClass = new WrapDynamicClass(beanClass);
				dynaClasses.put(beanClass, dynaClass);
			}

			return (dynaClass);
		}
	}

	/**
	 * Introspect our bean class to identify the supported properties.
	 */
	protected void introspect()
	{

		// Look up the property descriptors for this bean class
		PropertyDescriptor regulars[] = PropertyUtility.getPropertyDescriptors(beanClass);
		if (regulars == null) regulars = new PropertyDescriptor[0];

		HashMap mappeds = PropertyUtility.getMappedPropertyDescriptors(beanClass);
		if (mappeds == null) mappeds = new HashMap();

		// Construct corresponding DynaProperty information
		properties = new DynamicProperty[regulars.length + mappeds.size()];
		for (int i = 0; i < regulars.length; i++)
		{
			descriptorsMap.put(regulars[i].getName(), regulars[i]);
			properties[i] = new DynamicProperty(regulars[i].getName(), regulars[i].getPropertyType());
			propertiesMap.put(properties[i].getName(), properties[i]);
		}

		int j = regulars.length;
		Iterator names = mappeds.keySet().iterator();
		while (names.hasNext())
		{
			String name = (String) names.next();
			PropertyDescriptor descriptor = (PropertyDescriptor) mappeds.get(name);
			properties[j] = new DynamicProperty(descriptor.getName(), Map.class);
			propertiesMap.put(properties[j].getName(), properties[j]);

			j++;
		}

	}
}
