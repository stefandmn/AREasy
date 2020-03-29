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

import org.areasy.common.data.type.FastHashMap;

import java.beans.PropertyDescriptor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Map;


/**
 * <p>Utility methods for using Java Reflection APIs to facilitate generic
 * property getter and setter operations on Java objects.</p>
 * <p/>
 * <p>The implementations for these methods are provided by <code>PropertyUtilsBean</code>.
 * For more details see {@link org.areasy.common.data.bean.PropertyBean}.</p>
 *
 * @version $Id: PropertyUtility.java,v 1.2 2008/05/14 09:32:37 swd\stefan.damian Exp $
 * @see org.areasy.common.data.bean.PropertyBean
 */

public class PropertyUtility
{
	/**
	 * The delimiter that preceeds the zero-relative subscript for an
	 * indexed reference.
	 */
	public static final char INDEXED_DELIM = '[';


	/**
	 * The delimiter that follows the zero-relative subscript for an
	 * indexed reference.
	 */
	public static final char INDEXED_DELIM2 = ']';


	/**
	 * The delimiter that preceeds the key of a mapped property.
	 */
	public static final char MAPPED_DELIM = '(';


	/**
	 * The delimiter that follows the key of a mapped property.
	 */
	public static final char MAPPED_DELIM2 = ')';


	/**
	 * The delimiter that separates the components of a nested reference.
	 */
	public static final char NESTED_DELIM = '.';

	/**
	 * Clear any cached property descriptors information for all classes
	 * loaded by any class loaders.  This is useful in cases where class
	 * loaders are thrown away to implement class reloading.
	 * <p/>
	 * <p>For more details see <code>PropertyUtilsBean</code>.</p>
	 *
	 * @see org.areasy.common.data.bean.PropertyBean#clearDescriptors
	 */
	public static void clearDescriptors()
	{
		PropertyBean.getInstance().clearDescriptors();
	}


	/**
	 * <p>Copy property values from the "origin" bean to the "destination" bean
	 * for all cases where the property names are the same (even though the
	 * actual getter and setter methods might have been customized via
	 * <code>BeanInfo</code> classes).</p>
	 * <p/>
	 * <p>For more details see <code>PropertyUtilsBean</code>.</p>
	 *
	 * @see PropertyBean#copyProperties
	 */
	public static void copyProperties(Object dest, Object orig) throws IllegalAccessException, InvocationTargetException, NoSuchMethodException
	{
		PropertyBean.getInstance().copyProperties(dest, orig);
	}


	/**
	 * <p>Return the entire set of properties for which the specified bean
	 * provides a read method.</p>
	 * <p/>
	 * <p>For more details see <code>PropertyUtilsBean</code>.</p>
	 *
	 * @see PropertyBean#describe
	 */
	public static Map describe(Object bean) throws IllegalAccessException, InvocationTargetException, NoSuchMethodException
	{

		return (PropertyBean.getInstance().describe(bean));

	}


	/**
	 * <p>Return the value of the specified indexed property of the specified
	 * bean, with no type conversions.</p>
	 * <p/>
	 * <p>For more details see <code>PropertyUtilsBean</code>.</p>
	 *
	 * @see PropertyBean#getIndexedProperty(Object,String)
	 */
	public static Object getIndexedProperty(Object bean, String name)
			throws IllegalAccessException, InvocationTargetException,
			NoSuchMethodException
	{

		return (PropertyBean.getInstance().getIndexedProperty(bean, name));

	}


	/**
	 * <p>Return the value of the specified indexed property of the specified
	 * bean, with no type conversions.</p>
	 * <p/>
	 * <p>For more details see <code>PropertyUtilsBean</code>.</p>
	 *
	 * @see org.areasy.common.data.bean.PropertyBean#getIndexedProperty(Object,String, int)
	 */
	public static Object getIndexedProperty(Object bean, String name, int index) throws IllegalAccessException, InvocationTargetException, NoSuchMethodException
	{
		return (PropertyBean.getInstance().getIndexedProperty(bean, name, index));
	}


	/**
	 * <p>Return the value of the specified mapped property of the
	 * specified bean, with no type conversions.</p>
	 * <p/>
	 * <p>For more details see <code>PropertyUtilsBean</code>.</p>
	 *
	 * @see PropertyBean#getMappedProperty(Object,String)
	 */
	public static Object getMappedProperty(Object bean, String name) throws IllegalAccessException, InvocationTargetException, NoSuchMethodException
	{
		return (PropertyBean.getInstance().getMappedProperty(bean, name));
	}


	/**
	 * <p>Return the value of the specified mapped property of the specified
	 * bean, with no type conversions.</p>
	 * <p/>
	 * <p>For more details see <code>PropertyUtilsBean</code>.</p>
	 *
	 * @see PropertyBean#getMappedProperty(Object,String, String)
	 */
	public static Object getMappedProperty(Object bean, String name, String key) throws IllegalAccessException, InvocationTargetException, NoSuchMethodException
	{
		return PropertyBean.getInstance().getMappedProperty(bean, name, key);
	}


	/**
	 * <p>Return the mapped property descriptors for this bean class.</p>
	 * <p/>
	 * <p>For more details see <code>PropertyUtilsBean</code>.</p>
	 *
	 * @see PropertyBean#getMappedPropertyDescriptors(Class)
	 */
	public static FastHashMap getMappedPropertyDescriptors(Class beanClass)
	{
		return PropertyBean.getInstance().getMappedPropertyDescriptors(beanClass);
	}


	/**
	 * <p>Return the mapped property descriptors for this bean.</p>
	 * <p/>
	 * <p>For more details see <code>PropertyUtilsBean</code>.</p>
	 *
	 * @see PropertyBean#getMappedPropertyDescriptors(Object)
	 * @deprecated This method should not be exposed
	 */
	public static FastHashMap getMappedPropertyDescriptors(Object bean)
	{
		return PropertyBean.getInstance().getMappedPropertyDescriptors(bean);
	}


	/**
	 * <p>Return the value of the (possibly nested) property of the specified
	 * name, for the specified bean, with no type conversions.</p>
	 * <p/>
	 * <p>For more details see <code>PropertyUtilsBean</code>.</p>
	 *
	 * @see PropertyBean#getNestedProperty
	 */
	public static Object getNestedProperty(Object bean, String name) throws IllegalAccessException, InvocationTargetException, NoSuchMethodException
	{
		return PropertyBean.getInstance().getNestedProperty(bean, name);
	}


	/**
	 * <p>Return the value of the specified property of the specified bean,
	 * no matter which property reference format is used, with no
	 * type conversions.</p>
	 * <p/>
	 * <p>For more details see <code>PropertyUtilsBean</code>.</p>
	 *
	 * @see PropertyBean#getProperty
	 */
	public static Object getProperty(Object bean, String name) throws IllegalAccessException, InvocationTargetException, NoSuchMethodException
	{
		return (PropertyBean.getInstance().getProperty(bean, name));
	}


	/**
	 * <p>Retrieve the property descriptor for the specified property of the
	 * specified bean, or return <code>null</code> if there is no such
	 * descriptor.</p>
	 * <p/>
	 * <p>For more details see <code>PropertyUtilsBean</code>.</p>
	 *
	 * @see PropertyBean#getPropertyDescriptor
	 */
	public static PropertyDescriptor getPropertyDescriptor(Object bean, String name) throws IllegalAccessException, InvocationTargetException, NoSuchMethodException
	{
		return PropertyBean.getInstance().getPropertyDescriptor(bean, name);
	}


	/**
	 * <p>Retrieve the property descriptors for the specified class,
	 * introspecting and caching them the first time a particular bean class
	 * is encountered.</p>
	 * <p/>
	 * <p>For more details see <code>PropertyUtilsBean</code>.</p>
	 *
	 * @see PropertyBean#getPropertyDescriptors(Class)
	 */
	public static PropertyDescriptor[] getPropertyDescriptors(Class beanClass)
	{
		return PropertyBean.getInstance().getPropertyDescriptors(beanClass);
	}


	/**
	 * <p>Retrieve the property descriptors for the specified bean,
	 * introspecting and caching them the first time a particular bean class
	 * is encountered.</p>
	 * <p/>
	 * <p>For more details see <code>PropertyUtilsBean</code>.</p>
	 *
	 * @see PropertyBean#getPropertyDescriptors(Object)
	 */
	public static PropertyDescriptor[] getPropertyDescriptors(Object bean)
	{
		return PropertyBean.getInstance().getPropertyDescriptors(bean);
	}


	/**
	 * <p>Return the Java Class repesenting the property editor class that has
	 * been registered for this property (if any).</p>
	 * <p/>
	 * <p>For more details see <code>PropertyUtilsBean</code>.</p>
	 *
	 * @see PropertyBean#getPropertyEditorClass(Object,String)
	 */
	public static Class getPropertyEditorClass(Object bean, String name) throws IllegalAccessException, InvocationTargetException, NoSuchMethodException
	{
		return PropertyBean.getInstance().getPropertyEditorClass(bean, name);
	}


	/**
	 * <p>Return the Java Class representing the property type of the specified
	 * property, or <code>null</code> if there is no such property for the
	 * specified bean.</p>
	 * <p/>
	 * <p>For more details see <code>PropertyUtilsBean</code>.</p>
	 *
	 * @see PropertyBean#getPropertyType
	 */
	public static Class getPropertyType(Object bean, String name) throws IllegalAccessException, InvocationTargetException, NoSuchMethodException
	{
		return PropertyBean.getInstance().getPropertyType(bean, name);
	}


	/**
	 * <p>Return an accessible property getter method for this property,
	 * if there is one; otherwise return <code>null</code>.</p>
	 * <p/>
	 * <p>For more details see <code>PropertyUtilsBean</code>.</p>
	 *
	 * @see org.areasy.common.data.bean.PropertyBean#getReadMethod
	 */
	public static Method getReadMethod(PropertyDescriptor descriptor)
	{
		return (PropertyBean.getInstance().getReadMethod(descriptor));
	}


	/**
	 * <p>Return the value of the specified simple property of the specified
	 * bean, with no type conversions.</p>
	 * <p/>
	 * <p>For more details see <code>PropertyUtilsBean</code>.</p>
	 *
	 * @see PropertyBean#getSimpleProperty
	 */
	public static Object getSimpleProperty(Object bean, String name) throws IllegalAccessException, InvocationTargetException, NoSuchMethodException
	{
		return PropertyBean.getInstance().getSimpleProperty(bean, name);
	}


	/**
	 * <p>Return an accessible property setter method for this property,
	 * if there is one; otherwise return <code>null</code>.</p>
	 * <p/>
	 * <p>For more details see <code>PropertyUtilsBean</code>.</p>
	 *
	 * @see PropertyBean#getWriteMethod
	 */
	public static Method getWriteMethod(PropertyDescriptor descriptor)
	{
		return PropertyBean.getInstance().getWriteMethod(descriptor);
	}


	/**
	 * <p>Return <code>true</code> if the specified property name identifies
	 * a readable property on the specified bean; otherwise, return
	 * <code>false</code>.</p>
	 * <p/>
	 * <p>For more details see <code>PropertyUtilsBean</code>.</p>
	 *
	 * @see PropertyBean#isReadable
	 * @since BeanUtils 1.6
	 */
	public static boolean isReadable(Object bean, String name)
	{
		return PropertyBean.getInstance().isReadable(bean, name);
	}


	/**
	 * <p>Return <code>true</code> if the specified property name identifies
	 * a writeable property on the specified bean; otherwise, return
	 * <code>false</code>.</p>
	 * <p/>
	 * <p>For more details see <code>PropertyUtilsBean</code>.</p>
	 */
	public static boolean isWriteable(Object bean, String name)
	{
		return PropertyBean.getInstance().isWriteable(bean, name);
	}


	/**
	 * <p>Sets the value of the specified indexed property of the specified
	 * bean, with no type conversions.</p>
	 * <p/>
	 * <p>For more details see <code>PropertyUtilsBean</code>.</p>
	 *
	 * @see PropertyBean#setIndexedProperty(Object, String, Object)
	 */
	public static void setIndexedProperty(Object bean, String name, Object value) throws IllegalAccessException, InvocationTargetException, NoSuchMethodException
	{
		PropertyBean.getInstance().setIndexedProperty(bean, name, value);
	}


	/**
	 * <p>Sets the value of the specified indexed property of the specified
	 * bean, with no type conversions.</p>
	 * <p/>
	 * <p>For more details see <code>PropertyUtilsBean</code>.</p>
	 *
	 * @see PropertyBean#setIndexedProperty(Object, String, Object)
	 */
	public static void setIndexedProperty(Object bean, String name, int index, Object value) throws IllegalAccessException, InvocationTargetException, NoSuchMethodException
	{
		PropertyBean.getInstance().setIndexedProperty(bean, name, index, value);
	}


	/**
	 * <p>Sets the value of the specified mapped property of the
	 * specified bean, with no type conversions.</p>
	 * <p/>
	 * <p>For more details see <code>PropertyUtilsBean</code>.</p>
	 *
	 * @see PropertyBean#setMappedProperty(Object, String, Object)
	 */
	public static void setMappedProperty(Object bean, String name, Object value) throws IllegalAccessException, InvocationTargetException, NoSuchMethodException
	{
		PropertyBean.getInstance().setMappedProperty(bean, name, value);
	}


	/**
	 * <p>Sets the value of the specified mapped property of the specified
	 * bean, with no type conversions.</p>
	 * <p/>
	 * <p>For more details see <code>PropertyUtilsBean</code>.</p>
	 *
	 * @see PropertyBean#setMappedProperty(Object, String, String, Object)
	 */
	public static void setMappedProperty(Object bean, String name, String key, Object value) throws IllegalAccessException, InvocationTargetException, NoSuchMethodException
	{
		PropertyBean.getInstance().setMappedProperty(bean, name, key, value);
	}


	/**
	 * <p>Sets the value of the (possibly nested) property of the specified
	 * name, for the specified bean, with no type conversions.</p>
	 * <p/>
	 * <p>For more details see <code>PropertyUtilsBean</code>.</p>
	 *
	 * @see PropertyBean#setNestedProperty
	 */
	public static void setNestedProperty(Object bean, String name, Object value) throws IllegalAccessException, InvocationTargetException, NoSuchMethodException
	{
		PropertyBean.getInstance().setNestedProperty(bean, name, value);
	}


	/**
	 * <p>Set the value of the specified property of the specified bean,
	 * no matter which property reference format is used, with no
	 * type conversions.</p>
	 * <p/>
	 * <p>For more details see <code>PropertyUtilsBean</code>.</p>
	 *
	 * @see PropertyBean#setProperty
	 */
	public static void setProperty(Object bean, String name, Object value) throws IllegalAccessException, InvocationTargetException, NoSuchMethodException
	{
		PropertyBean.getInstance().setProperty(bean, name, value);
	}


	/**
	 * <p>Set the value of the specified simple property of the specified bean,
	 * with no type conversions.</p>
	 * <p/>
	 * <p>For more details see <code>PropertyUtilsBean</code>.</p>
	 *
	 * @see PropertyBean#setSimpleProperty
	 */
	public static void setSimpleProperty(Object bean, String name, Object value) throws IllegalAccessException, InvocationTargetException, NoSuchMethodException
	{
		PropertyBean.getInstance().setSimpleProperty(bean, name, value);
	}
}
