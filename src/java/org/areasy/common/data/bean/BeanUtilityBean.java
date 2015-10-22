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

import org.areasy.common.logger.Logger;
import org.areasy.common.logger.LoggerFactory;

import java.beans.IndexedPropertyDescriptor;
import java.beans.PropertyDescriptor;
import java.lang.reflect.Array;
import java.lang.reflect.InvocationTargetException;
import java.util.*;


/**
 * <p>JavaBean property population methods.</p>
 * <p/>
 * <p>This class provides implementations for the utility methods in
 * {@link BeanUtility}.
 * Different instances can be used to isolate caches between classloaders
 * and to vary the value converters registered.</p>
 *
 * @version $Id: BeanUtilityBean.java,v 1.2 2008/05/14 09:32:37 swd\stefan.damian Exp $
 */

public class BeanUtilityBean
{
	/**
	 * Contains <code>BeanUtilsBean</code> instances indexed by context classloader.
	 */
	private static final ContextClassLoaderLocal beansByClassLoader = new ContextClassLoaderLocal()
	{
		// Creates the default instance used when the context classloader is unavailable
		protected Object initialValue()
		{
			return new BeanUtilityBean();
		}
	};

	/**
	 * Gets the instance which provides the functionality for {@link BeanUtility}.
	 * This is a pseudo-singleton - an single instance is provided per (thread) context classloader.
	 * This mechanism provides isolation for web apps deployed in the same container.
	 */
	public synchronized static BeanUtilityBean getInstance()
	{
		return (BeanUtilityBean) beansByClassLoader.get();
	}

	/**
	 * Sets the instance which provides the functionality for {@link BeanUtility}.
	 * This is a pseudo-singleton - an single instance is provided per (thread) context classloader.
	 * This mechanism provides isolation for web apps deployed in the same container.
	 */
	public synchronized static void setInstance(BeanUtilityBean newInstance)
	{
		beansByClassLoader.set(newInstance);
	}

	/**
	 * Logging for this instance
	 */
	private Logger log = LoggerFactory.getLog(BeanUtility.class);

	/**
	 * Used to perform conversions between object types when setting properties
	 */
	private ConvertBean convertBean;

	/**
	 * Used to access properties
	 */
	private PropertyBean propertyBean;


	/**
	 * <p>Constructs an instance using new property
	 * and conversion instances.</p>
	 */
	public BeanUtilityBean()
	{
		this(new ConvertBean(), new PropertyBean());
	}

	/**
	 * <p>Constructs an instance using given property and conversion instances.</p>
	 *
	 * @param convertBean  use this <code>ConvertUtilsBean</code>
	 *                          to perform conversions from one object to another
	 * @param propertyBean use this <code>PropertyUtilsBean</code>
	 *                          to access properties
	 */
	public BeanUtilityBean(ConvertBean convertBean, PropertyBean propertyBean)
	{

		this.convertBean = convertBean;
		this.propertyBean = propertyBean;
	}

	/**
	 * <p>Clone a bean based on the available property getters and setters,
	 * even if the bean class itself does not implement Cloneable.</p>
	 * <p/>
	 * <p/>
	 * <strong>Note:</strong> this method creates a <strong>shallow</strong> clone.
	 * In other words, any objects referred to by the bean are shared with the clone
	 * rather than being cloned in turn.
	 * </p>
	 *
	 * @param bean Bean to be cloned
	 * @throws IllegalAccessException    if the caller does not have
	 *                                   access to the property accessor method
	 * @throws InstantiationException    if a new instance of the bean's
	 *                                   class cannot be instantiated
	 * @throws InvocationTargetException if the property accessor method
	 *                                   throws an exception
	 * @throws NoSuchMethodException     if an accessor method for this
	 *                                   property cannot be found
	 */
	public Object cloneBean(Object bean) throws IllegalAccessException, InstantiationException, InvocationTargetException, NoSuchMethodException
	{
		log.debug("Cloning bean: " + bean.getClass().getName());

		Class clazz = bean.getClass();
		Object newBean = null;
		if (bean instanceof DynamicBean) newBean = ((DynamicBean) bean).getDynamicClass().newInstance();
			else newBean = bean.getClass().newInstance();

		getPropertyUtility().copyProperties(newBean, bean);

		return (newBean);
	}


	/**
	 * <p>Copy property values from the origin bean to the destination bean
	 * for all cases where the property names are the same.  For each
	 * property, a conversion is attempted as necessary.  All combinations of
	 * standard JavaBeans and DynamicBeans as origin and destination are
	 * supported.  Properties that exist in the origin bean, but do not exist
	 * in the destination bean (or are read-only in the destination bean) are
	 * silently ignored.</p>
	 * <p/>
	 * <p>If the origin "bean" is actually a <code>Map</code>, it is assumed
	 * to contain String-valued <strong>simple</strong> property names as the keys, pointing at
	 * the corresponding property values that will be converted (if necessary)
	 * and set in the destination bean. <strong>Note</strong> that this method
	 * is intended to perform a "shallow copy" of the properties and so complex
	 * properties (for example, nested ones) will not be copied.</p>
	 * <p/>
	 * <p>This method differs from <code>populate()</code>, which
	 * was primarily designed for populating JavaBeans from the map of request
	 * parameters retrieved on an HTTP request, is that no scalar->indexed
	 * or indexed->scalar manipulations are performed.  If the origin property
	 * is indexed, the destination property must be also.</p>
	 * <p/>
	 * <p>If you know that no type conversions are required, the
	 * <code>copyProperties()</code> method in {@link PropertyUtility} will
	 * execute faster than this method.</p>
	 * <p/>
	 *
	 * @param dest Destination bean whose properties are modified
	 * @param orig Origin bean whose properties are retrieved
	 * @throws IllegalAccessException    if the caller does not have
	 *                                   access to the property accessor method
	 * @throws IllegalArgumentException  if the <code>dest</code> or
	 *                                   <code>orig</code> argument is null
	 * @throws InvocationTargetException if the property accessor method
	 *                                   throws an exception
	 */
	public void copyProperties(Object dest, Object orig) throws IllegalAccessException, InvocationTargetException
	{

		// Validate existence of the specified beans
		if (dest == null) throw new IllegalArgumentException("No destination bean specified");

		if (orig == null) throw new IllegalArgumentException("No origin bean specified");

		log.debug("BeanUtility.copyProperties(" + dest + ", " + orig + ")");

		// Copy the properties, converting as necessary
		if (orig instanceof DynamicBean)
		{
			DynamicProperty origDescriptors[] = ((DynamicBean) orig).getDynamicClass().getDynamicProperties();
			for (int i = 0; i < origDescriptors.length; i++)
			{
				String name = origDescriptors[i].getName();
				if (getPropertyUtility().isWriteable(dest, name))
				{
					Object value = ((DynamicBean) orig).get(name);
					copyProperty(dest, name, value);
				}
			}
		}
		else if (orig instanceof Map)
		{
			Iterator names = ((Map) orig).keySet().iterator();
			while (names.hasNext())
			{
				String name = (String) names.next();
				if (getPropertyUtility().isWriteable(dest, name))
				{
					Object value = ((Map) orig).get(name);
					copyProperty(dest, name, value);
				}
			}
		}
		else
		{
			PropertyDescriptor origDescriptors[] =
					getPropertyUtility().getPropertyDescriptors(orig);
			for (int i = 0; i < origDescriptors.length; i++)
			{
				String name = origDescriptors[i].getName();
				if ("class".equals(name)) continue; // No point in trying to set an object's class

				if (getPropertyUtility().isReadable(orig, name) && getPropertyUtility().isWriteable(dest, name))
				{
					try
					{
						Object value = getPropertyUtility().getSimpleProperty(orig, name);
						copyProperty(dest, name, value);
					}
					catch (NoSuchMethodException e)
					{
						; // Should not happen
					}
				}
			}
		}

	}


	/**
	 * <p>Copy the specified property value to the specified destination bean,
	 * performing any type conversion that is required.  If the specified
	 * bean does not have a property of the specified name, or the property
	 * is read only on the destination bean, return without
	 * doing anything.  If you have custom destination property types, register
	 * {@link Converter}s for them by calling the <code>register()</code>
	 * method of {@link org.areasy.common.data.bean.ConvertUtility}.</p>
	 * <p/>
	 * <p><strong>IMPLEMENTATION RESTRICTIONS</strong>:</p>
	 * <ul>
	 * <li>Does not support destination properties that are indexed,
	 * but only an indexed setter (as opposed to an array setter)
	 * is available.</li>
	 * <li>Does not support destination properties that are mapped,
	 * but only a keyed setter (as opposed to a Map setter)
	 * is available.</li>
	 * <li>The desired property type of a mapped setter cannot be
	 * determined (since Maps support any data type), so no conversion
	 * will be performed.</li>
	 * </ul>
	 *
	 * @param bean  Bean on which setting is to be performed
	 * @param name  Property name (can be nested/indexed/mapped/combo)
	 * @param value Value to be set
	 * @throws IllegalAccessException    if the caller does not have
	 *                                   access to the property accessor method
	 * @throws InvocationTargetException if the property accessor method
	 *                                   throws an exception
	 */
	public void copyProperty(Object bean, String name, Object value) throws IllegalAccessException, InvocationTargetException
	{
		// Resolve any nested expression to get the actual target bean
		Object target = bean;
		int delim = name.lastIndexOf(PropertyUtility.NESTED_DELIM);
		if (delim >= 0)
		{
			try
			{
				target = getPropertyUtility().getProperty(bean, name.substring(0, delim));
			}
			catch (NoSuchMethodException e)
			{
				return; // Skip this property setter
			}

			name = name.substring(delim + 1);
		}

		// Declare local variables we will require
		String propName = null;          // Simple name of target property
		Class type = null;               // Java type of target property
		int index = -1;                  // Indexed subscript value (if any)
		String key = null;               // Mapped key value (if any)

		// Calculate the target property name, index, and key values
		propName = name;
		int i = propName.indexOf(PropertyUtility.INDEXED_DELIM);
		if (i >= 0)
		{
			int k = propName.indexOf(PropertyUtility.INDEXED_DELIM2);
			try
			{
				index = Integer.parseInt(propName.substring(i + 1, k));
			}
			catch (NumberFormatException e)
			{
				;
			}

			propName = propName.substring(0, i);
		}

		int j = propName.indexOf(PropertyUtility.MAPPED_DELIM);
		if (j >= 0)
		{
			int k = propName.indexOf(PropertyUtility.MAPPED_DELIM2);
			try
			{
				key = propName.substring(j + 1, k);
			}
			catch (IndexOutOfBoundsException e)
			{
				;
			}
			propName = propName.substring(0, j);
		}

		// Calculate the target property type
		if (target instanceof DynamicBean)
		{
			DynamicClass dynamicClass = ((DynamicBean) target).getDynamicClass();
			DynamicProperty dynamicProperty = dynamicClass.getDynamicProperty(propName);

			if (dynamicProperty == null) return; // Skip this property setter

			type = dynamicProperty.getType();
		}
		else
		{
			PropertyDescriptor descriptor = null;
			try
			{
				descriptor = getPropertyUtility().getPropertyDescriptor(target, name);
				if (descriptor == null) return; // Skip this property setter
			}
			catch (NoSuchMethodException e)
			{
				return; // Skip this property setter
			}

			type = descriptor.getPropertyType();
			if (type == null) return;
		}

		// Convert the specified value to the required type and store it
		if (index >= 0)
		{                    // Destination must be indexed
			Converter converter = getConvertUtility().lookup(type.getComponentType());
			if (converter != null) value = converter.convert(type, value);

			try
			{
				getPropertyUtility().setIndexedProperty(target, propName, index, value);
			}
			catch (NoSuchMethodException e)
			{
				throw new InvocationTargetException(e, "Cannot set " + propName);
			}
		}
		else if (key != null)
		{
			try
			{
				getPropertyUtility().setMappedProperty(target, propName, key, value);
			}
			catch (NoSuchMethodException e)
			{
				throw new InvocationTargetException(e, "Cannot set " + propName);
			}
		}
		else
		{                             // Destination must be simple
			Converter converter = getConvertUtility().lookup(type);
			if (converter != null) value = converter.convert(type, value);

			try
			{
				getPropertyUtility().setSimpleProperty(target, propName, value);
			}
			catch (NoSuchMethodException e)
			{
				throw new InvocationTargetException(e, "Cannot set " + propName);
			}
		}

	}


	/**
	 * <p>Return the entire set of properties for which the specified bean
	 * provides a read method. This map contains the to <code>String</code>
	 * converted property values for all properties for which a read method
	 * is provided (i.e. where the getReadMethod() returns non-null).</p>
	 * <p/>
	 * <p>This map can be fed back to a call to
	 * <code>BeanUtils.populate()</code> to reconsitute the same set of
	 * properties, modulo differences for read-only and write-only
	 * properties, but only if there are no indexed properties.</p>
	 *
	 * @param bean Bean whose properties are to be extracted
	 * @throws IllegalAccessException    if the caller does not have
	 *                                   access to the property accessor method
	 * @throws InvocationTargetException if the property accessor method
	 *                                   throws an exception
	 * @throws NoSuchMethodException     if an accessor method for this
	 *                                   property cannot be found
	 */
	public Map describe(Object bean) throws IllegalAccessException, InvocationTargetException, NoSuchMethodException
	{
		if (bean == null) return (new java.util.HashMap());

		log.debug("Describing bean: " + bean.getClass().getName());

		Map description = new HashMap();
		if (bean instanceof DynamicBean)
		{
			DynamicProperty descriptors[] = ((DynamicBean) bean).getDynamicClass().getDynamicProperties();
			for (int i = 0; i < descriptors.length; i++)
			{
				String name = descriptors[i].getName();
				description.put(name, getProperty(bean, name));
			}
		}
		else
		{
			PropertyDescriptor descriptors[] = getPropertyUtility().getPropertyDescriptors(bean);
			for (int i = 0; i < descriptors.length; i++)
			{
				String name = descriptors[i].getName();
				if (descriptors[i].getReadMethod() != null)
				{
					description.put(name, getProperty(bean, name));
				}
			}
		}

		return (description);
	}


	/**
	 * Return the value of the specified array property of the specified
	 * bean, as a String array.
	 *
	 * @param bean Bean whose property is to be extracted
	 * @param name Name of the property to be extracted
	 * @throws IllegalAccessException    if the caller does not have
	 *                                   access to the property accessor method
	 * @throws InvocationTargetException if the property accessor method
	 *                                   throws an exception
	 * @throws NoSuchMethodException     if an accessor method for this
	 *                                   property cannot be found
	 */
	public String[] getArrayProperty(Object bean, String name) throws IllegalAccessException, InvocationTargetException, NoSuchMethodException
	{

		Object value = getPropertyUtility().getProperty(bean, name);

		if (value == null) return (null);
		else if (value instanceof Collection)
		{
			ArrayList values = new ArrayList();
			Iterator items = ((Collection) value).iterator();
			while (items.hasNext())
			{
				Object item = items.next();
				if (item == null) values.add((String) null);
					else values.add(getConvertUtility().convert(item));
			}
			return ((String[]) values.toArray(new String[values.size()]));
		}
		else if (value.getClass().isArray())
		{
			int n = Array.getLength(value);
			String results[] = new String[n];
			for (int i = 0; i < n; i++)
			{
				Object item = Array.get(value, i);
				if (item == null) results[i] = null;
					else results[i] = getConvertUtility().convert(item);
			}

			return (results);
		}
		else
		{
			String results[] = new String[1];
			results[0] = value.toString();
			return (results);
		}
	}


	/**
	 * Return the value of the specified indexed property of the specified
	 * bean, as a String.  The zero-relative index of the
	 * required value must be included (in square brackets) as a suffix to
	 * the property name, or <code>IllegalArgumentException</code> will be
	 * thrown.
	 *
	 * @param bean Bean whose property is to be extracted
	 * @param name <code>propertyname[index]</code> of the property value
	 *             to be extracted
	 * @throws IllegalAccessException    if the caller does not have
	 *                                   access to the property accessor method
	 * @throws InvocationTargetException if the property accessor method
	 *                                   throws an exception
	 * @throws NoSuchMethodException     if an accessor method for this
	 *                                   property cannot be found
	 */
	public String getIndexedProperty(Object bean, String name) throws IllegalAccessException, InvocationTargetException, NoSuchMethodException
	{
		Object value = getPropertyUtility().getIndexedProperty(bean, name);
		return (getConvertUtility().convert(value));
	}


	/**
	 * Return the value of the specified indexed property of the specified
	 * bean, as a String.  The index is specified as a method parameter and
	 * must *not* be included in the property name expression
	 *
	 * @param bean  Bean whose property is to be extracted
	 * @param name  Simple property name of the property value to be extracted
	 * @param index Index of the property value to be extracted
	 * @throws IllegalAccessException    if the caller does not have
	 *                                   access to the property accessor method
	 * @throws InvocationTargetException if the property accessor method
	 *                                   throws an exception
	 * @throws NoSuchMethodException     if an accessor method for this
	 *                                   property cannot be found
	 */
	public String getIndexedProperty(Object bean, String name, int index) throws IllegalAccessException, InvocationTargetException, NoSuchMethodException
	{
		Object value = getPropertyUtility().getIndexedProperty(bean, name, index);
		return (getConvertUtility().convert(value));
	}


	/**
	 * Return the value of the specified indexed property of the specified
	 * bean, as a String.  The String-valued key of the required value
	 * must be included (in parentheses) as a suffix to
	 * the property name, or <code>IllegalArgumentException</code> will be
	 * thrown.
	 *
	 * @param bean Bean whose property is to be extracted
	 * @param name <code>propertyname(index)</code> of the property value
	 *             to be extracted
	 * @throws IllegalAccessException    if the caller does not have
	 *                                   access to the property accessor method
	 * @throws InvocationTargetException if the property accessor method
	 *                                   throws an exception
	 * @throws NoSuchMethodException     if an accessor method for this
	 *                                   property cannot be found
	 */
	public String getMappedProperty(Object bean, String name) throws IllegalAccessException, InvocationTargetException, NoSuchMethodException
	{
		Object value = getPropertyUtility().getMappedProperty(bean, name);
		return (getConvertUtility().convert(value));
	}


	/**
	 * Return the value of the specified mapped property of the specified
	 * bean, as a String.  The key is specified as a method parameter and
	 * must *not* be included in the property name expression
	 *
	 * @param bean Bean whose property is to be extracted
	 * @param name Simple property name of the property value to be extracted
	 * @param key  Lookup key of the property value to be extracted
	 * @throws IllegalAccessException    if the caller does not have
	 *                                   access to the property accessor method
	 * @throws InvocationTargetException if the property accessor method
	 *                                   throws an exception
	 * @throws NoSuchMethodException     if an accessor method for this
	 *                                   property cannot be found
	 */
	public String getMappedProperty(Object bean, String name, String key) throws IllegalAccessException, InvocationTargetException, NoSuchMethodException
	{
		Object value = getPropertyUtility().getMappedProperty(bean, name, key);
		return (getConvertUtility().convert(value));
	}


	/**
	 * Return the value of the (possibly nested) property of the specified
	 * name, for the specified bean, as a String.
	 *
	 * @param bean Bean whose property is to be extracted
	 * @param name Possibly nested name of the property to be extracted
	 * @throws IllegalAccessException    if the caller does not have
	 *                                   access to the property accessor method
	 * @throws IllegalArgumentException  if a nested reference to a
	 *                                   property returns null
	 * @throws InvocationTargetException if the property accessor method
	 *                                   throws an exception
	 * @throws NoSuchMethodException     if an accessor method for this
	 *                                   property cannot be found
	 */
	public String getNestedProperty(Object bean, String name) throws IllegalAccessException, InvocationTargetException, NoSuchMethodException
	{
		Object value = getPropertyUtility().getNestedProperty(bean, name);
		return (getConvertUtility().convert(value));
	}


	/**
	 * Return the value of the specified property of the specified bean,
	 * no matter which property reference format is used, as a String.
	 *
	 * @param bean Bean whose property is to be extracted
	 * @param name Possibly indexed and/or nested name of the property
	 *             to be extracted
	 * @throws IllegalAccessException    if the caller does not have
	 *                                   access to the property accessor method
	 * @throws InvocationTargetException if the property accessor method
	 *                                   throws an exception
	 * @throws NoSuchMethodException     if an accessor method for this
	 *                                   property cannot be found
	 */
	public String getProperty(Object bean, String name) throws IllegalAccessException, InvocationTargetException, NoSuchMethodException
	{
		return (getNestedProperty(bean, name));
	}


	/**
	 * Return the value of the specified simple property of the specified
	 * bean, converted to a String.
	 *
	 * @param bean Bean whose property is to be extracted
	 * @param name Name of the property to be extracted
	 * @throws IllegalAccessException    if the caller does not have
	 *                                   access to the property accessor method
	 * @throws InvocationTargetException if the property accessor method
	 *                                   throws an exception
	 * @throws NoSuchMethodException     if an accessor method for this
	 *                                   property cannot be found
	 */
	public String getSimpleProperty(Object bean, String name) throws IllegalAccessException, InvocationTargetException, NoSuchMethodException
	{
		Object value = getPropertyUtility().getSimpleProperty(bean, name);

		return (getConvertUtility().convert(value));
	}


	/**
	 * <p>Populate the JavaBeans properties of the specified bean, based on
	 * the specified name/value pairs.  This method uses Java reflection APIs
	 * to identify corresponding "property setter" method names, and deals
	 * with setter arguments of type <code>String</code>, <code>boolean</code>,
	 * <code>int</code>, <code>long</code>, <code>float</code>, and
	 * <code>double</code>.  In addition, array setters for these types (or the
	 * corresponding primitive types) can also be identified.</p>
	 * <p/>
	 * <p>The particular setter method to be called for each property is
	 * determined using the usual JavaBeans introspection mechanisms.  Thus,
	 * you may identify custom setter methods using a BeanInfo class that is
	 * associated with the class of the bean itself.  If no such BeanInfo
	 * class is available, the standard method name conversion ("set" plus
	 * the capitalized name of the property in question) is used.</p>
	 * <p/>
	 * <p><strong>NOTE</strong>:  It is contrary to the JavaBeans Specification
	 * to have more than one setter method (with different argument
	 * signatures) for the same property.</p>
	 * <p/>
	 * <p><strong>WARNING</strong> - The logic of this method is customized
	 * for extracting String-based request parameters from an HTTP request.
	 * It is probably not what you want for general property copying with
	 * type conversion.  For that purpose, check out the
	 * <code>copyProperties()</code> method instead.</p>
	 *
	 * @param bean       JavaBean whose properties are being populated
	 * @param properties Map keyed by property name, with the
	 *                   corresponding (String or String[]) value(s) to be set
	 * @throws IllegalAccessException    if the caller does not have
	 *                                   access to the property accessor method
	 * @throws InvocationTargetException if the property accessor method
	 *                                   throws an exception
	 */
	public void populate(Object bean, Map properties) throws IllegalAccessException, InvocationTargetException
	{
		// Do nothing unless both arguments have been specified
		if ((bean == null) || (properties == null)) return;

		// Loop through the property name/value pairs to be set
		Iterator names = properties.keySet().iterator();
		while (names.hasNext())
		{
			// Identify the property name and value(s) to be assigned
			String name = (String) names.next();
			if (name == null) continue;

			Object value = properties.get(name);

			// Perform the assignment for this property
			setProperty(bean, name, value);
		}
	}


	/**
	 * <p>Set the specified property value, performing type conversions as
	 * required to conform to the type of the destination property.</p>
	 * <p/>
	 * <p>If the property is read only then the method returns
	 * without throwing an exception.</p>
	 * <p/>
	 * <p>If <code>null</code> is passed into a property expecting a primitive value,
	 * then this will be converted as if it were a <code>null</code> string.</p>
	 * <p/>
	 * <p><strong>WARNING</strong> - The logic of this method is customized
	 * to meet the needs of <code>populate()</code>, and is probably not what
	 * you want for general property copying with type conversion.  For that
	 * purpose, check out the <code>copyProperty()</code> method instead.</p>
	 * <p/>
	 * <p><strong>WARNING</strong> - PLEASE do not modify the behavior of this
	 * method without consulting with the Struts developer community.  There
	 * are some subtleties to its functionality that are not documented in the
	 * Javadoc description above, yet are vital to the way that Struts utilizes
	 * this method.</p>
	 *
	 * @param bean  Bean on which setting is to be performed
	 * @param name  Property name (can be nested/indexed/mapped/combo)
	 * @param value Value to be set
	 * @throws IllegalAccessException    if the caller does not have
	 *                                   access to the property accessor method
	 * @throws InvocationTargetException if the property accessor method
	 *                                   throws an exception
	 */
	public void setProperty(Object bean, String name, Object value) throws IllegalAccessException, InvocationTargetException
	{
		// Resolve any nested expression to get the actual target bean
		Object target = bean;
		int delim = findLastNestedIndex(name);
		if (delim >= 0)
		{
			try
			{
				target = getPropertyUtility().getProperty(bean, name.substring(0, delim));
			}
			catch (NoSuchMethodException e)
			{
				return; // Skip this property setter
			}

			name = name.substring(delim + 1);
		}

		// Declare local variables we will require
		String propName = null;          // Simple name of target property
		Class type = null;               // Java type of target property
		int index = -1;                  // Indexed subscript value (if any)
		String key = null;               // Mapped key value (if any)

		// Calculate the property name, index, and key values
		propName = name;
		int i = propName.indexOf(PropertyUtility.INDEXED_DELIM);
		if (i >= 0)
		{
			int k = propName.indexOf(PropertyUtility.INDEXED_DELIM2);
			try
			{
				index = Integer.parseInt(propName.substring(i + 1, k));
			}
			catch (NumberFormatException e)
			{
				;
			}
			propName = propName.substring(0, i);
		}

		int j = propName.indexOf(PropertyUtility.MAPPED_DELIM);
		if (j >= 0)
		{
			int k = propName.indexOf(PropertyUtility.MAPPED_DELIM2);
			try
			{
				key = propName.substring(j + 1, k);
			}
			catch (IndexOutOfBoundsException e)
			{
				;
			}
			propName = propName.substring(0, j);
		}

		// Calculate the property type
		if (target instanceof DynamicBean)
		{
			DynamicClass dynamicClass = ((DynamicBean) target).getDynamicClass();
			DynamicProperty dynamicProperty = dynamicClass.getDynamicProperty(propName);

			if (dynamicProperty == null) return; // Skip this property setter

			type = dynamicProperty.getType();
		}
		else
		{
			PropertyDescriptor descriptor = null;
			try
			{
				descriptor = getPropertyUtility().getPropertyDescriptor(target, name);
				if (descriptor == null) return; // Skip this property setter
			}
			catch (NoSuchMethodException e)
			{
				return; // Skip this property setter
			}

			if (descriptor instanceof MappedPropertyDescriptor)
			{
				if (((MappedPropertyDescriptor) descriptor).getMappedWriteMethod() == null)
				{
					if (log.isDebugEnabled())
					{
						log.debug("Skipping read-only property");
					}
					return; // Read-only, skip this property setter
				}

				type = ((MappedPropertyDescriptor) descriptor).getMappedPropertyType();
			}
			else if (descriptor instanceof IndexedPropertyDescriptor)
			{
				if (((IndexedPropertyDescriptor) descriptor).getIndexedWriteMethod() == null)
				{
					if (log.isDebugEnabled())
					{
						log.debug("Skipping read-only property");
					}
					return; // Read-only, skip this property setter
				}

				type = ((IndexedPropertyDescriptor) descriptor).getIndexedPropertyType();
			}
			else
			{
				if (descriptor.getWriteMethod() == null)
				{
					log.debug("Skipping read-only property");
					return; // Read-only, skip this property setter
				}

				type = descriptor.getPropertyType();
			}
		}

		// Convert the specified value to the required type
		Object newValue = null;
		if (type.isArray() && (index < 0))
		{ // Scalar value into array
			if (value == null)
			{
				String values[] = new String[1];
				values[0] = (String) value;

				newValue = getConvertUtility().convert((String[]) values, type);
			}
			else if (value instanceof String)
			{
				String values[] = new String[1];
				values[0] = (String) value;

				newValue = getConvertUtility().convert((String[]) values, type);
			}
			else if (value instanceof String[]) newValue = getConvertUtility().convert((String[]) value, type);
			else newValue = value;
		}
		else if (type.isArray())
		{         // Indexed value into array
			if (value instanceof String) newValue = getConvertUtility().convert((String) value, type.getComponentType());
				else if (value instanceof String[]) newValue = getConvertUtility().convert(((String[]) value)[0], type.getComponentType());
					else newValue = value;
		}
		else
		{                             // Value into scalar
			if ((value instanceof String) || (value == null)) newValue = getConvertUtility().convert((String) value, type);
				else if (value instanceof String[]) newValue = getConvertUtility().convert(((String[]) value)[0], type);
					else if (getConvertUtility().lookup(value.getClass()) != null) newValue = getConvertUtility().convert(value.toString(), type);
						else newValue = value;
		}

		// Invoke the setter method
		try
		{
			if (index >= 0) getPropertyUtility().setIndexedProperty(target, propName, index, newValue);
				else if (key != null) getPropertyUtility().setMappedProperty(target, propName, key, newValue);
					else getPropertyUtility().setProperty(target, propName, newValue);
		}
		catch (NoSuchMethodException e)
		{
			throw new InvocationTargetException (e, "Cannot set " + propName);
		}

	}

	private int findLastNestedIndex(String expression)
	{
		// walk back from the end to the start
		// and find the first index that
		int bracketCount = 0;
		for (int i = expression.length() - 1; i >= 0; i--)
		{
			char at = expression.charAt(i);

			switch (at)
			{
				case PropertyUtility.NESTED_DELIM:
					if (bracketCount < 1) return i;
					break;

				case PropertyUtility.MAPPED_DELIM:
				case PropertyUtility.INDEXED_DELIM:
					// not bothered which
					--bracketCount;
					break;

				case PropertyUtility.MAPPED_DELIM2:
				case PropertyUtility.INDEXED_DELIM2:
					// not bothered which
					++bracketCount;
					break;
			}
		}

		// can't find any
		return -1;
	}

	/**
	 * Gets the <code>ConvertUtilsBean</code> instance used to perform the conversions.
	 */
	public ConvertBean getConvertUtility()
	{
		return convertBean;
	}

	/**
	 * Gets the <code>PropertyUtilsBean</code> instance used to access properties.
	 */
	public PropertyBean getPropertyUtility()
	{
		return propertyBean;
	}
}
