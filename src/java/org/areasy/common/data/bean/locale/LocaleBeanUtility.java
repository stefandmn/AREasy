package org.areasy.common.data.bean.locale;


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

import org.areasy.common.data.bean.BeanUtility;
import org.areasy.common.data.bean.PropertyUtility;

import java.lang.reflect.InvocationTargetException;
import java.util.Locale;


/**
 * <p>Utility methods for populating JavaBeans properties
 * via reflection in a locale-dependent manner.</p>
 * <p/>
 * <p>The implementations for these methods are provided by <code>LocaleBeanUtilsBean</code>.
 * For more details see {@link LocaleBeanUtilityBean}.</p>
 *
 * @version $Id: LocaleBeanUtility.java,v 1.2 2008/05/14 09:32:41 swd\stefan.damian Exp $
 */

public class LocaleBeanUtility extends BeanUtility
{
	/**
	 * <p>Gets the locale used when no locale is passed.</p>
	 * <p/>
	 * <p>For more details see <code>LocaleBeanUtilsBean</code></p>
	 *
	 * @see LocaleBeanUtilityBean#getDefaultLocale()
	 */
	public static Locale getDefaultLocale()
	{
		return LocaleBeanUtilityBean.getLocaleBeanUtilsInstance().getDefaultLocale();
	}


	/**
	 * <p>Sets the locale used when no locale is passed.</p>
	 * <p/>
	 * <p>For more details see <code>LocaleBeanUtilsBean</code></p>
	 *
	 * @see LocaleBeanUtilityBean#setDefaultLocale(Locale)
	 */
	public static void setDefaultLocale(Locale locale)
	{
		LocaleBeanUtilityBean.getLocaleBeanUtilsInstance().setDefaultLocale(locale);
	}

	/**
	 * <p>Gets whether the pattern is localized or not.</p>
	 * <p/>
	 * <p>For more details see <code>LocaleBeanUtilsBean</code></p>
	 *
	 * @see LocaleBeanUtilityBean#getApplyLocalized()
	 */
	public static boolean getApplyLocalized()
	{
		return LocaleBeanUtilityBean.getLocaleBeanUtilsInstance().getApplyLocalized();
	}

	/**
	 * <p>Sets whether the pattern is localized or not.</p>
	 * <p/>
	 * <p>For more details see <code>LocaleBeanUtilsBean</code></p>
	 *
	 * @see LocaleBeanUtilityBean#setApplyLocalized(boolean)
	 */
	public static void setApplyLocalized(boolean newApplyLocalized)
	{
		LocaleBeanUtilityBean.getLocaleBeanUtilsInstance().setApplyLocalized(newApplyLocalized);
	}

	/**
	 * <p>Return the value of the specified locale-sensitive indexed property
	 * of the specified bean, as a String.</p>
	 * <p/>
	 * <p>For more details see <code>LocaleBeanUtilsBean</code></p>
	 *
	 * @see LocaleBeanUtilityBean#getIndexedProperty(Object, String, String)
	 */
	public static String getIndexedProperty(Object bean, String name, String pattern) throws IllegalAccessException, InvocationTargetException, NoSuchMethodException
	{
		return LocaleBeanUtilityBean.getLocaleBeanUtilsInstance().getIndexedProperty(bean, name, pattern);
	}

	/**
	 * Return the value of the specified locale-sensitive indexed property
	 * of the specified bean, as a String using the default convertion pattern of
	 * the corresponding {@link LocaleConverter}.
	 * <p/>
	 * <p>For more details see <code>LocaleBeanUtilsBean</code></p>
	 *
	 * @see LocaleBeanUtilityBean#getIndexedProperty(Object, String)
	 */
	public static String getIndexedProperty(Object bean, String name) throws IllegalAccessException, InvocationTargetException, NoSuchMethodException
	{
		return LocaleBeanUtilityBean.getLocaleBeanUtilsInstance().getIndexedProperty(bean, name);
	}

	/**
	 * <p>Return the value of the specified locale-sensetive indexed property
	 * of the specified bean, as a String using the specified convertion pattern.</p>
	 * <p/>
	 * <p>For more details see <code>LocaleBeanUtilsBean</code></p>
	 *
	 * @see LocaleBeanUtilityBean#getIndexedProperty(Object, String, int, String)
	 */
	public static String getIndexedProperty(Object bean, String name, int index, String pattern) throws IllegalAccessException, InvocationTargetException, NoSuchMethodException
	{
		return LocaleBeanUtilityBean.getLocaleBeanUtilsInstance().getIndexedProperty(bean, name, index, pattern);
	}

	/**
	 * <p>Return the value of the specified locale-sensetive indexed property
	 * of the specified bean, as a String using the default convertion pattern of
	 * the corresponding {@link LocaleConverter}.</p>
	 * <p/>
	 * <p>For more details see <code>LocaleBeanUtilsBean</code></p>
	 *
	 * @see LocaleBeanUtilityBean#getIndexedProperty(Object, String, int)
	 */
	public static String getIndexedProperty(Object bean, String name, int index) throws IllegalAccessException, InvocationTargetException, NoSuchMethodException
	{
		return LocaleBeanUtilityBean.getLocaleBeanUtilsInstance().getIndexedProperty(bean, name, index);
	}

	/**
	 * <p>Return the value of the specified simple locale-sensitive property
	 * of the specified bean, converted to a String using the specified
	 * convertion pattern.</p>
	 * <p/>
	 * <p>For more details see <code>LocaleBeanUtilsBean</code></p>
	 *
	 * @see LocaleBeanUtilityBean#getSimpleProperty(Object, String, String)
	 */
	public static String getSimpleProperty(Object bean, String name, String pattern) throws IllegalAccessException, InvocationTargetException, NoSuchMethodException
	{
		return LocaleBeanUtilityBean.getLocaleBeanUtilsInstance().getSimpleProperty(bean, name, pattern);
	}

	/**
	 * <p>Return the value of the specified simple locale-sensitive property
	 * of the specified bean, converted to a String using the default
	 * convertion pattern of the corresponding {@link LocaleConverter}.</p>
	 * <p/>
	 * <p>For more details see <code>LocaleBeanUtilsBean</code></p>
	 *
	 * @see LocaleBeanUtilityBean#getSimpleProperty(Object, String)
	 */
	public static String getSimpleProperty(Object bean, String name) throws IllegalAccessException, InvocationTargetException, NoSuchMethodException
	{
		return LocaleBeanUtilityBean.getLocaleBeanUtilsInstance().getSimpleProperty(bean, name);
	}

	/**
	 * <p>Return the value of the specified mapped locale-sensitive property
	 * of the specified bean, as a String using the specified convertion pattern.</p>
	 * <p/>
	 * <p>For more details see <code>LocaleBeanUtilsBean</code></p>
	 *
	 * @see LocaleBeanUtilityBean#getMappedProperty(Object, String, String, String)
	 */
	public static String getMappedProperty(Object bean, String name, String key, String pattern) throws IllegalAccessException, InvocationTargetException, NoSuchMethodException
	{
		return LocaleBeanUtilityBean.getLocaleBeanUtilsInstance().getMappedProperty(bean, name, key, pattern);
	}

	/**
	 * <p>Return the value of the specified mapped locale-sensitive property
	 * of the specified bean, as a String
	 * The key is specified as a method parameter and must *not* be included
	 * in the property name expression.</p>
	 * <p/>
	 * <p>For more details see <code>LocaleBeanUtilsBean</code></p>
	 *
	 * @see LocaleBeanUtilityBean#getMappedProperty(Object, String, String)
	 */
	public static String getMappedProperty(Object bean, String name, String key) throws IllegalAccessException, InvocationTargetException, NoSuchMethodException
	{
		return LocaleBeanUtilityBean.getLocaleBeanUtilsInstance().getMappedProperty(bean, name, key);
	}


	/**
	 * <p>Return the value of the specified locale-sensitive mapped property
	 * of the specified bean, as a String using the specified pattern.</p>
	 * <p/>
	 * <p>For more details see <code>LocaleBeanUtilsBean</code></p>
	 *
	 * @see LocaleBeanUtilityBean#getMappedPropertyLocale(Object, String, String)
	 */
	public static String getMappedPropertyLocale(Object bean, String name, String pattern) throws IllegalAccessException, InvocationTargetException, NoSuchMethodException
	{
		return LocaleBeanUtilityBean.getLocaleBeanUtilsInstance().getMappedPropertyLocale(bean, name, pattern);
	}


	/**
	 * <p>Return the value of the specified locale-sensitive mapped property
	 * of the specified bean, as a String using the default
	 * convertion pattern of the corresponding {@link LocaleConverter}.</p>
	 * <p/>
	 * <p>For more details see <code>LocaleBeanUtilsBean</code></p>
	 *
	 * @see LocaleBeanUtilityBean#getMappedProperty(Object, String)
	 */
	public static String getMappedProperty(Object bean, String name) throws IllegalAccessException, InvocationTargetException, NoSuchMethodException
	{
		return LocaleBeanUtilityBean.getLocaleBeanUtilsInstance().getMappedProperty(bean, name);
	}

	/**
	 * <p>Return the value of the (possibly nested) locale-sensitive property
	 * of the specified name, for the specified bean,
	 * as a String using the specified pattern.</p>
	 * <p/>
	 * <p>For more details see <code>LocaleBeanUtilsBean</code></p>
	 *
	 * @see LocaleBeanUtilityBean#getNestedProperty(Object, String, String)
	 */
	public static String getNestedProperty(Object bean, String name, String pattern) throws IllegalAccessException, InvocationTargetException, NoSuchMethodException
	{
		return LocaleBeanUtilityBean.getLocaleBeanUtilsInstance().getNestedProperty(bean, name, pattern);
	}

	/**
	 * <p>Return the value of the (possibly nested) locale-sensitive property
	 * of the specified name.</p>
	 * <p/>
	 * <p>For more details see <code>LocaleBeanUtilsBean</code></p>
	 *
	 * @see LocaleBeanUtilityBean#getNestedProperty(Object, String)
	 */
	public static String getNestedProperty(Object bean, String name) throws IllegalAccessException, InvocationTargetException, NoSuchMethodException
	{
		return LocaleBeanUtilityBean.getLocaleBeanUtilsInstance().getNestedProperty(bean, name);
	}

	/**
	 * <p>Return the value of the specified locale-sensitive property
	 * of the specified bean.</p>
	 * <p/>
	 * <p>For more details see <code>LocaleBeanUtilsBean</code></p>
	 *
	 * @see LocaleBeanUtilityBean#getProperty(Object, String, String)
	 */
	public static String getProperty(Object bean, String name, String pattern) throws IllegalAccessException, InvocationTargetException, NoSuchMethodException
	{
		return LocaleBeanUtilityBean.getLocaleBeanUtilsInstance().getProperty(bean, name, pattern);
	}

	/**
	 * <p>Return the value of the specified locale-sensitive property
	 * of the specified bean.</p>
	 * <p/>
	 * <p>For more details see <code>LocaleBeanUtilsBean</code></p>
	 *
	 * @see LocaleBeanUtilityBean#getProperty(Object, String)
	 */
	public static String getProperty(Object bean, String name) throws IllegalAccessException, InvocationTargetException, NoSuchMethodException
	{
		return LocaleBeanUtilityBean.getLocaleBeanUtilsInstance().getProperty(bean, name);
	}

	/**
	 * <p>Set the specified locale-sensitive property value, performing type
	 * conversions as required to conform to the type of the destination property
	 * using the default convertion pattern of the corresponding {@link LocaleConverter}.</p>
	 * <p/>
	 * <p>For more details see <code>LocaleBeanUtilsBean</code></p>
	 *
	 * @see LocaleBeanUtilityBean#setProperty(Object, String, Object)
	 */
	public static void setProperty(Object bean, String name, Object value) throws IllegalAccessException, InvocationTargetException
	{
		LocaleBeanUtilityBean.getLocaleBeanUtilsInstance().setProperty(bean, name, value);
	}

	/**
	 * <p>Set the specified locale-sensitive property value, performing type
	 * conversions as required to conform to the type of the destination
	 * property using the specified convertion pattern.</p>
	 * <p/>
	 * <p>For more details see <code>LocaleBeanUtilsBean</code></p>
	 *
	 * @see LocaleBeanUtilityBean#setProperty(Object, String, Object, String)
	 */
	public static void setProperty(Object bean, String name, Object value, String pattern) throws IllegalAccessException, InvocationTargetException
	{
		LocaleBeanUtilityBean.getLocaleBeanUtilsInstance().setProperty(bean, name, value, pattern);
	}

	/**
	 * <p>Calculate the property type.</p>
	 * <p/>
	 * <p>For more details see <code>LocaleBeanUtilsBean</code></p>
	 *
	 * @see LocaleBeanUtilityBean#definePropertyType(Object, String, String)
	 */
	protected static Class definePropertyType(Object target, String name, String propName) throws IllegalAccessException, InvocationTargetException
	{
		return LocaleBeanUtilityBean.getLocaleBeanUtilsInstance().definePropertyType(target, name, propName);
	}

	/**
	 * <p>Convert the specified value to the required type using the
	 * specified convertion pattern.</p>
	 * <p/>
	 * <p>For more details see <code>LocaleBeanUtilsBean</code></p>
	 *
	 * @see LocaleBeanUtilityBean#convert(Class, int, Object, String)
	 */
	protected static Object convert(Class type, int index, Object value, String pattern)
	{
		return LocaleBeanUtilityBean.getLocaleBeanUtilsInstance().convert(type, index, value, pattern);
	}

	/**
	 * <p>Convert the specified value to the required type.</p>
	 * <p/>
	 * <p>For more details see <code>LocaleBeanUtilsBean</code></p>
	 *
	 * @see LocaleBeanUtilityBean#convert(Class, int, Object)
	 */
	protected static Object convert(Class type, int index, Object value)
	{
		return LocaleBeanUtilityBean.getLocaleBeanUtilsInstance().convert(type, index, value);
	}

	/**
	 * <p>Invoke the setter method.</p>
	 * <p/>
	 * <p>For more details see <code>LocaleBeanUtilsBean</code></p>
	 *
	 * @see LocaleBeanUtilityBean#invokeSetter(Object, String, String, int, Object)
	 */
	protected static void invokeSetter(Object target, String propName, String key, int index, Object newValue) throws IllegalAccessException, InvocationTargetException
	{
		LocaleBeanUtilityBean.getLocaleBeanUtilsInstance().invokeSetter(target, propName, key, index, newValue);
	}

	/**
	 * Resolve any nested expression to get the actual target bean.
	 *
	 * @param bean The bean
	 * @param name The property name
	 * @throws IllegalAccessException    if the caller does not have
	 *                                   access to the property accessor method
	 * @throws InvocationTargetException if the property accessor method
	 *                                   throws an exception
	 * @deprecated moved into <code>LocaleBeanUtilsBean</code>
	 */
	protected static AbstractDescriptor calculate(Object bean, String name) throws IllegalAccessException, InvocationTargetException
	{
		String propName = null;          // Simple name of target property
		int index = -1;                  // Indexed subscript value (if any)
		String key = null;               // Mapped key value (if any)

		Object target = bean;
		int delim = name.lastIndexOf(PropertyUtility.NESTED_DELIM);
		if (delim >= 0)
		{
			try
			{
				target = PropertyUtility.getProperty(bean, name.substring(0, delim));
			}
			catch (NoSuchMethodException e)
			{
				return null; // Skip this property setter
			}

			name = name.substring(delim + 1);
		}

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

		return new AbstractDescriptor(target, name, propName, key, index);
	}
}


