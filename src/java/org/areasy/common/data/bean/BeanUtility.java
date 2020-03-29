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

import java.lang.reflect.InvocationTargetException;
import java.util.Map;


/**
 * <p>Utility methods for populating JavaBeans properties via reflection.</p>
 * <p/>
 * <p>The implementations are provided by {@link BeanUtilityBean}.
 * These static utility methods use the default instance.
 * More sophisticated behaviour can be provided by using a <code>BeanUtilsBean</code> instance.</p>
 *
 * @version $Id: BeanUtility.java,v 1.2 2008/05/14 09:32:37 swd\stefan.damian Exp $
 */

public class BeanUtility
{
	/**
	 * <p>Clone a bean based on the available property getters and setters,
	 * even if the bean class itself does not implement Cloneable.</p>
	 * <p/>
	 * <p>For more details see <code>BeanUtilsBean</code>.</p>
	 *
	 * @see BeanUtilityBean#cloneBean
	 */
	public static Object cloneBean(Object bean) throws IllegalAccessException, InstantiationException, InvocationTargetException, NoSuchMethodException
	{
		return BeanUtilityBean.getInstance().cloneBean(bean);
	}


	/**
	 * <p>Copy property values from the origin bean to the destination bean
	 * for all cases where the property names are the same.</p>
	 * <p/>
	 * <p>For more details see <code>BeanUtilsBean</code>.</p>
	 *
	 * @see BeanUtilityBean#copyProperties
	 */
	public static void copyProperties(Object dest, Object orig) throws IllegalAccessException, InvocationTargetException
	{
		BeanUtilityBean.getInstance().copyProperties(dest, orig);
	}


	/**
	 * <p>Copy the specified property value to the specified destination bean,
	 * performing any type conversion that is required.</p>
	 * <p/>
	 * <p>For more details see <code>BeanUtilsBean</code>.</p>
	 *
	 * @see BeanUtilityBean#copyProperty
	 */
	public static void copyProperty(Object bean, String name, Object value) throws IllegalAccessException, InvocationTargetException
	{
		BeanUtilityBean.getInstance().copyProperty(bean, name, value);
	}


	/**
	 * <p>Return the entire set of properties for which the specified bean
	 * provides a read method.</p>
	 * <p/>
	 * <p>For more details see <code>BeanUtilsBean</code>.</p>
	 *
	 * @see BeanUtilityBean#describe
	 */
	public static Map describe(Object bean) throws IllegalAccessException, InvocationTargetException, NoSuchMethodException
	{
		return BeanUtilityBean.getInstance().describe(bean);
	}


	/**
	 * <p>Return the value of the specified array property of the specified
	 * bean, as a String array.</p>
	 * <p/>
	 * <p>For more details see <code>BeanUtilsBean</code>.</p>
	 *
	 * @see BeanUtilityBean#getArrayProperty
	 */
	public static String[] getArrayProperty(Object bean, String name) throws IllegalAccessException, InvocationTargetException, NoSuchMethodException
	{
		return BeanUtilityBean.getInstance().getArrayProperty(bean, name);
	}


	/**
	 * <p>Return the value of the specified indexed property of the specified
	 * bean, as a String.</p>
	 * <p/>
	 * <p>For more details see <code>BeanUtilsBean</code>.</p>
	 *
	 * @see BeanUtilityBean#getIndexedProperty(Object, String)
	 */
	public static String getIndexedProperty(Object bean, String name) throws IllegalAccessException, InvocationTargetException, NoSuchMethodException
	{
		return BeanUtilityBean.getInstance().getIndexedProperty(bean, name);
	}


	/**
	 * Return the value of the specified indexed property of the specified
	 * bean, as a String.  The index is specified as a method parameter and
	 * must *not* be included in the property name expression
	 * <p/>
	 * <p>For more details see <code>BeanUtilsBean</code>.</p>
	 *
	 * @see BeanUtilityBean#getIndexedProperty(Object, String, int)
	 */
	public static String getIndexedProperty(Object bean, String name, int index) throws IllegalAccessException, InvocationTargetException, NoSuchMethodException
	{
		return BeanUtilityBean.getInstance().getIndexedProperty(bean, name, index);
	}


	/**
	 * </p>Return the value of the specified indexed property of the specified
	 * bean, as a String.</p>
	 * <p/>
	 * <p>For more details see <code>BeanUtilsBean</code>.</p>
	 *
	 * @see BeanUtilityBean#getMappedProperty(Object, String)
	 */
	public static String getMappedProperty(Object bean, String name) throws IllegalAccessException, InvocationTargetException, NoSuchMethodException
	{
		return BeanUtilityBean.getInstance().getMappedProperty(bean, name);
	}


	/**
	 * </p>Return the value of the specified mapped property of the specified
	 * bean, as a String.</p>
	 * <p/>
	 * <p>For more details see <code>BeanUtilsBean</code>.</p>
	 *
	 * @see BeanUtilityBean#getMappedProperty(Object, String, String)
	 */
	public static String getMappedProperty(Object bean, String name, String key) throws IllegalAccessException, InvocationTargetException, NoSuchMethodException
	{
		return BeanUtilityBean.getInstance().getMappedProperty(bean, name, key);
	}


	/**
	 * <p>Return the value of the (possibly nested) property of the specified
	 * name, for the specified bean, as a String.</p>
	 * <p/>
	 * <p>For more details see <code>BeanUtilsBean</code>.</p>
	 *
	 * @see BeanUtilityBean#getNestedProperty
	 */
	public static String getNestedProperty(Object bean, String name) throws IllegalAccessException, InvocationTargetException, NoSuchMethodException
	{
		return BeanUtilityBean.getInstance().getNestedProperty(bean, name);
	}


	/**
	 * <p>Return the value of the specified property of the specified bean,
	 * no matter which property reference format is used, as a String.</p>
	 * <p/>
	 * <p>For more details see <code>BeanUtilsBean</code>.</p>
	 *
	 * @see BeanUtilityBean#getProperty
	 */
	public static String getProperty(Object bean, String name) throws IllegalAccessException, InvocationTargetException, NoSuchMethodException
	{
		return BeanUtilityBean.getInstance().getProperty(bean, name);
	}


	/**
	 * <p>Return the value of the specified simple property of the specified
	 * bean, converted to a String.</p>
	 * <p/>
	 * <p>For more details see <code>BeanUtilsBean</code>.</p>
	 *
	 * @see BeanUtilityBean#getSimpleProperty
	 */
	public static String getSimpleProperty(Object bean, String name) throws IllegalAccessException, InvocationTargetException, NoSuchMethodException
	{
		return BeanUtilityBean.getInstance().getSimpleProperty(bean, name);
	}


	/**
	 * <p>Populate the JavaBeans properties of the specified bean, based on
	 * the specified name/value pairs.</p>
	 * <p/>
	 * <p>For more details see <code>BeanUtilsBean</code>.</p>
	 *
	 * @see BeanUtilityBean#populate
	 */
	public static void populate(Object bean, Map properties) throws IllegalAccessException, InvocationTargetException
	{
		BeanUtilityBean.getInstance().populate(bean, properties);
	}


	/**
	 * <p>Set the specified property value, performing type conversions as
	 * required to conform to the type of the destination property.</p>
	 * <p/>
	 * <p>For more details see <code>BeanUtilsBean</code>.</p>
	 *
	 * @see BeanUtilityBean#setProperty
	 */
	public static void setProperty(Object bean, String name, Object value) throws IllegalAccessException, InvocationTargetException
	{
		BeanUtilityBean.getInstance().setProperty(bean, name, value);
	}
}
