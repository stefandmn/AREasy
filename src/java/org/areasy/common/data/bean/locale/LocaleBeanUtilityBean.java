package org.areasy.common.data.bean.locale;


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

import org.areasy.common.data.bean.*;

import java.beans.IndexedPropertyDescriptor;
import java.beans.PropertyDescriptor;
import java.lang.reflect.InvocationTargetException;
import java.util.Locale;


/**
 * <p>Utility methods for populating JavaBeans properties
 * via reflection in a locale-dependent manner.</p>
 *
 * @version $Id: LocaleBeanUtilityBean.java,v 1.2 2008/05/14 09:32:41 swd\stefan.damian Exp $
 */

public class LocaleBeanUtilityBean extends BeanUtilityBean
{

	/**
	 * Contains <code>LocaleBeanUtilsBean</code> instances indexed by context classloader.
	 */
	private static final ContextClassLoaderLocal localeBeansByClassLoader = new ContextClassLoaderLocal()
	{
		// Creates the default instance used when the context classloader is unavailable
		protected Object initialValue()
		{
			return new LocaleBeanUtilityBean();
		}
	};

	/**
	 * Gets singleton instance
	 */
	public synchronized static LocaleBeanUtilityBean getLocaleBeanUtilsInstance()
	{
		return (LocaleBeanUtilityBean) localeBeansByClassLoader.get();
	}

	/**
	 * Sets the instance which provides the functionality for {@link LocaleBeanUtility}.
	 * This is a pseudo-singleton - an single instance is provided per (thread) context classloader.
	 * This mechanism provides isolation for web apps deployed in the same container.
	 */
	public synchronized static void setInstance(LocaleBeanUtilityBean newInstance)
	{
		localeBeansByClassLoader.set(newInstance);
	}

	/**
	 * Convertor used by this class
	 */
	private LocaleConvertUtilityBean localeConvertUtility;

	/**
	 * Construct instance with standard conversion bean
	 */
	public LocaleBeanUtilityBean()
	{
		this.localeConvertUtility = new LocaleConvertUtilityBean();
	}

	/**
	 * Construct instance that uses given locale conversion
	 *
	 * @param localeConvertUtility use this <code>localeConvertUtils</code> to perform
	 *                           conversions
	 * @param convertBean   use this for standard conversions
	 * @param propertyBean  use this for property conversions
	 */
	public LocaleBeanUtilityBean(LocaleConvertUtilityBean localeConvertUtility, ConvertBean convertBean, PropertyBean propertyBean)
	{
		super(convertBean, propertyBean);

		this.localeConvertUtility = localeConvertUtility;
	}

	/**
	 * Construct instance that uses given locale conversion
	 *
	 * @param localeConvertUtility use this <code>localeConvertUtils</code> to perform
	 *                           conversions
	 */
	public LocaleBeanUtilityBean(LocaleConvertUtilityBean localeConvertUtility)
	{
		this.localeConvertUtility = localeConvertUtility;
	}

	/**
	 * Gets the bean instance used for conversions
	 */
	public LocaleConvertUtilityBean getLocaleConvertUtils()
	{
		return localeConvertUtility;
	}

	/**
	 * Gets the default Locale
	 */
	public Locale getDefaultLocale()
	{
		return getLocaleConvertUtils().getDefaultLocale();
	}


	/**
	 * Sets the default Locale
	 */
	public void setDefaultLocale(Locale locale)
	{
		getLocaleConvertUtils().setDefaultLocale(locale);
	}

	/**
	 * Is the pattern to be applied localized
	 * (Indicate whether the pattern is localized or not)
	 */
	public boolean getApplyLocalized()
	{
		return getLocaleConvertUtils().getApplyLocalized();
	}

	/**
	 * Sets whether the pattern is applied localized
	 * (Indicate whether the pattern is localized or not)
	 */
	public void setApplyLocalized(boolean newApplyLocalized)
	{
		getLocaleConvertUtils().setApplyLocalized(newApplyLocalized);
	}

	/**
	 * Return the value of the specified locale-sensitive indexed property
	 * of the specified bean, as a String. The zero-relative index of the
	 * required value must be included (in square brackets) as a suffix to
	 * the property name, or <code>IllegalArgumentException</code> will be
	 * thrown.
	 *
	 * @param bean    Bean whose property is to be extracted
	 * @param name    <code>propertyname[index]</code> of the property value
	 *                to be extracted
	 * @param pattern The convertion pattern
	 * @throws IllegalAccessException    if the caller does not have
	 *                                   access to the property accessor method
	 * @throws InvocationTargetException if the property accessor method
	 *                                   throws an exception
	 * @throws NoSuchMethodException     if an accessor method for this
	 *                                   propety cannot be found
	 */
	public String getIndexedProperty(Object bean, String name, String pattern) throws IllegalAccessException, InvocationTargetException, NoSuchMethodException
	{
		Object value = getPropertyUtility().getIndexedProperty(bean, name);
		return getLocaleConvertUtils().convert(value, pattern);
	}

	/**
	 * Return the value of the specified locale-sensitive indexed property
	 * of the specified bean, as a String using the default convertion pattern of
	 * the corresponding {@link LocaleConverter}. The zero-relative index
	 * of the required value must be included (in square brackets) as a suffix
	 * to the property name, or <code>IllegalArgumentException</code> will be thrown.
	 *
	 * @param bean Bean whose property is to be extracted
	 * @param name <code>propertyname[index]</code> of the property value
	 *             to be extracted
	 * @throws IllegalAccessException    if the caller does not have
	 *                                   access to the property accessor method
	 * @throws InvocationTargetException if the property accessor method
	 *                                   throws an exception
	 * @throws NoSuchMethodException     if an accessor method for this
	 *                                   propety cannot be found
	 */
	public String getIndexedProperty(Object bean, String name) throws IllegalAccessException, InvocationTargetException, NoSuchMethodException
	{
		return getIndexedProperty(bean, name, null);
	}

	/**
	 * Return the value of the specified locale-sensetive indexed property
	 * of the specified bean, as a String using the specified convertion pattern.
	 * The index is specified as a method parameter and
	 * must *not* be included in the property name expression
	 *
	 * @param bean    Bean whose property is to be extracted
	 * @param name    Simple property name of the property value to be extracted
	 * @param index   Index of the property value to be extracted
	 * @param pattern The convertion pattern
	 * @throws IllegalAccessException    if the caller does not have
	 *                                   access to the property accessor method
	 * @throws InvocationTargetException if the property accessor method
	 *                                   throws an exception
	 * @throws NoSuchMethodException     if an accessor method for this
	 *                                   propety cannot be found
	 */
	public String getIndexedProperty(Object bean, String name, int index, String pattern) throws IllegalAccessException, InvocationTargetException, NoSuchMethodException
	{
		Object value = getPropertyUtility().getIndexedProperty(bean, name, index);
		return getLocaleConvertUtils().convert(value, pattern);
	}

	/**
	 * Return the value of the specified locale-sensetive indexed property
	 * of the specified bean, as a String using the default convertion pattern of
	 * the corresponding {@link LocaleConverter}.
	 * The index is specified as a method parameter and
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
	 *                                   propety cannot be found
	 */
	public String getIndexedProperty(Object bean, String name, int index) throws IllegalAccessException, InvocationTargetException, NoSuchMethodException
	{
		return getIndexedProperty(bean, name, index, null);
	}

	/**
	 * Return the value of the specified simple locale-sensitive property
	 * of the specified bean, converted to a String using the specified
	 * convertion pattern.
	 *
	 * @param bean    Bean whose property is to be extracted
	 * @param name    Name of the property to be extracted
	 * @param pattern The convertion pattern
	 * @throws IllegalAccessException    if the caller does not have
	 *                                   access to the property accessor method
	 * @throws InvocationTargetException if the property accessor method
	 *                                   throws an exception
	 * @throws NoSuchMethodException     if an accessor method for this
	 *                                   propety cannot be found
	 */
	public String getSimpleProperty(Object bean, String name, String pattern) throws IllegalAccessException, InvocationTargetException, NoSuchMethodException
	{
		Object value = getPropertyUtility().getSimpleProperty(bean, name);
		return getLocaleConvertUtils().convert(value, pattern);
	}

	/**
	 * Return the value of the specified simple locale-sensitive property
	 * of the specified bean, converted to a String using the default
	 * convertion pattern of the corresponding {@link LocaleConverter}.
	 *
	 * @param bean Bean whose property is to be extracted
	 * @param name Name of the property to be extracted
	 * @throws IllegalAccessException    if the caller does not have
	 *                                   access to the property accessor method
	 * @throws InvocationTargetException if the property accessor method
	 *                                   throws an exception
	 * @throws NoSuchMethodException     if an accessor method for this
	 *                                   propety cannot be found
	 */
	public String getSimpleProperty(Object bean, String name) throws IllegalAccessException, InvocationTargetException, NoSuchMethodException
	{
		return getSimpleProperty(bean, name, null);
	}

	/**
	 * Return the value of the specified mapped locale-sensitive property
	 * of the specified bean, as a String using the specified convertion pattern.
	 * The key is specified as a method parameter and must *not* be included in
	 * the property name expression.
	 *
	 * @param bean    Bean whose property is to be extracted
	 * @param name    Simple property name of the property value to be extracted
	 * @param key     Lookup key of the property value to be extracted
	 * @param pattern The convertion pattern
	 * @throws IllegalAccessException    if the caller does not have
	 *                                   access to the property accessor method
	 * @throws InvocationTargetException if the property accessor method
	 *                                   throws an exception
	 * @throws NoSuchMethodException     if an accessor method for this
	 *                                   propety cannot be found
	 */
	public String getMappedProperty(Object bean, String name, String key, String pattern) throws IllegalAccessException, InvocationTargetException, NoSuchMethodException
	{
		Object value = getPropertyUtility().getMappedProperty(bean, name, key);
		return getLocaleConvertUtils().convert(value, pattern);
	}

	/**
	 * Return the value of the specified mapped locale-sensitive property
	 * of the specified bean, as a String
	 * The key is specified as a method parameter and must *not* be included
	 * in the property name expression
	 *
	 * @param bean Bean whose property is to be extracted
	 * @param name Simple property name of the property value to be extracted
	 * @param key  Lookup key of the property value to be extracted
	 * @throws IllegalAccessException    if the caller does not have
	 *                                   access to the property accessor method
	 * @throws InvocationTargetException if the property accessor method
	 *                                   throws an exception
	 * @throws NoSuchMethodException     if an accessor method for this
	 *                                   propety cannot be found
	 */
	public String getMappedProperty(Object bean, String name, String key) throws IllegalAccessException, InvocationTargetException, NoSuchMethodException
	{
		return getMappedProperty(bean, name, key, null);
	}


	/**
	 * Return the value of the specified locale-sensitive mapped property
	 * of the specified bean, as a String using the specified pattern.
	 * The String-valued key of the required value
	 * must be included (in parentheses) as a suffix to
	 * the property name, or <code>IllegalArgumentException</code> will be
	 * thrown.
	 *
	 * @param bean    Bean whose property is to be extracted
	 * @param name    <code>propertyname(index)</code> of the property value
	 *                to be extracted
	 * @param pattern The convertion pattern
	 * @throws IllegalAccessException    if the caller does not have
	 *                                   access to the property accessor method
	 * @throws InvocationTargetException if the property accessor method
	 *                                   throws an exception
	 * @throws NoSuchMethodException     if an accessor method for this
	 *                                   propety cannot be found
	 */
	public String getMappedPropertyLocale(Object bean, String name, String pattern) throws IllegalAccessException, InvocationTargetException, NoSuchMethodException
	{
		Object value = getPropertyUtility().getMappedProperty(bean, name);
		return getLocaleConvertUtils().convert(value, pattern);
	}


	/**
	 * Return the value of the specified locale-sensitive mapped property
	 * of the specified bean, as a String using the default
	 * convertion pattern of the corresponding {@link LocaleConverter}.
	 * The String-valued key of the required value
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
	 *                                   propety cannot be found
	 */
	public String getMappedProperty(Object bean, String name) throws IllegalAccessException, InvocationTargetException, NoSuchMethodException
	{
		return getMappedPropertyLocale(bean, name, null);
	}

	/**
	 * Return the value of the (possibly nested) locale-sensitive property
	 * of the specified name, for the specified bean,
	 * as a String using the specified pattern.
	 *
	 * @param bean    Bean whose property is to be extracted
	 * @param name    Possibly nested name of the property to be extracted
	 * @param pattern The convertion pattern
	 * @throws IllegalAccessException    if the caller does not have
	 *                                   access to the property accessor method
	 * @throws IllegalArgumentException  if a nested reference to a
	 *                                   property returns null
	 * @throws InvocationTargetException if the property accessor method
	 *                                   throws an exception
	 * @throws NoSuchMethodException     if an accessor method for this
	 *                                   propety cannot be found
	 */
	public String getNestedProperty(Object bean, String name, String pattern) throws IllegalAccessException, InvocationTargetException, NoSuchMethodException
	{
		Object value = getPropertyUtility().getNestedProperty(bean, name);
		return getLocaleConvertUtils().convert(value, pattern);
	}

	/**
	 * Return the value of the (possibly nested) locale-sensitive property
	 * of the specified name, for the specified bean, as a String using the default
	 * convertion pattern of the corresponding {@link LocaleConverter}.
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
	 *                                   propety cannot be found
	 */
	public String getNestedProperty(Object bean, String name) throws IllegalAccessException, InvocationTargetException, NoSuchMethodException
	{
		return getNestedProperty(bean, name, null);
	}

	/**
	 * Return the value of the specified locale-sensitive property
	 * of the specified bean, no matter which property reference
	 * format is used, as a String using the specified convertion pattern.
	 *
	 * @param bean    Bean whose property is to be extracted
	 * @param name    Possibly indexed and/or nested name of the property
	 *                to be extracted
	 * @param pattern The convertion pattern
	 * @throws IllegalAccessException    if the caller does not have
	 *                                   access to the property accessor method
	 * @throws InvocationTargetException if the property accessor method
	 *                                   throws an exception
	 * @throws NoSuchMethodException     if an accessor method for this
	 *                                   propety cannot be found
	 */
	public String getProperty(Object bean, String name, String pattern) throws IllegalAccessException, InvocationTargetException, NoSuchMethodException
	{
		return getNestedProperty(bean, name, pattern);
	}

	/**
	 * Return the value of the specified locale-sensitive property
	 * of the specified bean, no matter which property reference
	 * format is used, as a String using the default
	 * convertion pattern of the corresponding {@link LocaleConverter}.
	 *
	 * @param bean Bean whose property is to be extracted
	 * @param name Possibly indexed and/or nested name of the property
	 *             to be extracted
	 * @throws IllegalAccessException    if the caller does not have
	 *                                   access to the property accessor method
	 * @throws InvocationTargetException if the property accessor method
	 *                                   throws an exception
	 * @throws NoSuchMethodException     if an accessor method for this
	 *                                   propety cannot be found
	 */
	public String getProperty(Object bean, String name) throws IllegalAccessException, InvocationTargetException, NoSuchMethodException
	{
		return getNestedProperty(bean, name);
	}

	/**
	 * Set the specified locale-sensitive property value, performing type
	 * conversions as required to conform to the type of the destination property
	 * using the default convertion pattern of the corresponding {@link LocaleConverter}.
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
		setProperty(bean, name, value, null);
	}

	/**
	 * Set the specified locale-sensitive property value, performing type
	 * conversions as required to conform to the type of the destination
	 * property using the specified convertion pattern.
	 *
	 * @param bean    Bean on which setting is to be performed
	 * @param name    Property name (can be nested/indexed/mapped/combo)
	 * @param value   Value to be set
	 * @param pattern The convertion pattern
	 * @throws IllegalAccessException    if the caller does not have
	 *                                   access to the property accessor method
	 * @throws InvocationTargetException if the property accessor method
	 *                                   throws an exception
	 */
	public void setProperty(Object bean, String name, Object value, String pattern) throws IllegalAccessException, InvocationTargetException
	{
		AbstractDescriptor propInfo = calculate(bean, name);

		if (propInfo != null)
		{
			Class type = definePropertyType(propInfo.getTarget(), name, propInfo.getPropName());

			if (type != null)
			{
				Object newValue = convert(type, propInfo.getIndex(), value, pattern);
				invokeSetter(propInfo.getTarget(), propInfo.getPropName(), propInfo.getKey(), propInfo.getIndex(), newValue);
			}
		}
	}

	/**
	 * Calculate the property type.
	 *
	 * @param target   The bean
	 * @param name     The property name
	 * @param propName The Simple name of target property
	 * @throws IllegalAccessException    if the caller does not have
	 *                                   access to the property accessor method
	 * @throws InvocationTargetException if the property accessor method
	 *                                   throws an exception
	 */
	protected Class definePropertyType(Object target, String name, String propName) throws IllegalAccessException, InvocationTargetException
	{

		Class type = null;               // Java type of target property

		if (target instanceof DynamicBean)
		{
			DynamicClass dynamicClass = ((DynamicBean) target).getDynamicClass();
			DynamicProperty dynamicProperty = dynamicClass.getDynamicProperty(propName);

			if (dynamicProperty == null) return null; // Skip this property setter

			type = dynamicProperty.getType();
		}
		else
		{
			PropertyDescriptor descriptor = null;
			try
			{
				descriptor = getPropertyUtility().getPropertyDescriptor(target, name);
				if (descriptor == null) return null; // Skip this property setter
			}
			catch (NoSuchMethodException e)
			{
				return null; // Skip this property setter
			}

			if (descriptor instanceof MappedPropertyDescriptor) type = ((MappedPropertyDescriptor) descriptor).getMappedPropertyType();
				else if (descriptor instanceof IndexedPropertyDescriptor) type = ((IndexedPropertyDescriptor) descriptor).getIndexedPropertyType();
					else type = descriptor.getPropertyType();
		}

		return type;
	}

	/**
	 * Convert the specified value to the required type using the
	 * specified convertion pattern.
	 *
	 * @param type    The Java type of target property
	 * @param index   The indexed subscript value (if any)
	 * @param value   The value to be converted
	 * @param pattern The convertion pattern
	 */
	protected Object convert(Class type, int index, Object value, String pattern)
	{
		Object newValue = null;

		if (type.isArray() && (index < 0))
		{
			// Scalar value into array
			if (value instanceof String)
			{
				String values[] = new String[1];
				values[0] = (String) value;
				newValue = getLocaleConvertUtils().convert((String[]) values, type, pattern);
			}
			else if (value instanceof String[]) newValue = getLocaleConvertUtils().convert((String[]) value, type, pattern);
				else newValue = value;
		}
		else if (type.isArray())
		{         // Indexed value into array
			if (value instanceof String) newValue = getLocaleConvertUtils().convert((String) value, type.getComponentType(), pattern);
				else if (value instanceof String[]) newValue = getLocaleConvertUtils().convert(((String[]) value)[0], type.getComponentType(), pattern);
					else newValue = value;
		}
		else
		{                             // Value into scalar
			if (value instanceof String)
			{
				newValue = getLocaleConvertUtils().convert((String) value, type, pattern);
			}
			else if (value instanceof String[])
			{
				newValue = getLocaleConvertUtils().convert(((String[]) value)[0],
						type, pattern);
			}
			else
			{
				newValue = value;
			}
		}

		return newValue;
	}

	/**
	 * Convert the specified value to the required type.
	 *
	 * @param type  The Java type of target property
	 * @param index The indexed subscript value (if any)
	 * @param value The value to be converted
	 */
	protected Object convert(Class type, int index, Object value)
	{

		Object newValue = null;

		if (type.isArray() && (index < 0))
		{
			// Scalar value into array
			if (value instanceof String)
			{
				String values[] = new String[1];
				values[0] = (String) value;
				newValue = ConvertUtility.convert((String[]) values, type);
			}
			else if (value instanceof String[]) newValue = ConvertUtility.convert((String[]) value, type);
				else newValue = value;
		}
		else if (type.isArray())
		{         // Indexed value into array
			if (value instanceof String) newValue = ConvertUtility.convert((String) value, type.getComponentType());
				else if (value instanceof String[]) newValue = ConvertUtility.convert(((String[]) value)[0], type.getComponentType());
					else newValue = value;
		}
		else
		{                             // Value into scalar
			if (value instanceof String) newValue = ConvertUtility.convert((String) value, type);
				else if (value instanceof String[]) newValue = ConvertUtility.convert(((String[]) value)[0], type);
					else newValue = value;
		}

		return newValue;
	}

	/**
	 * Invoke the setter method.
	 *
	 * @param target   The bean
	 * @param propName The Simple name of target property
	 * @param key      The Mapped key value (if any)
	 * @param index    The indexed subscript value (if any)
	 * @param newValue The value to be set
	 * @throws IllegalAccessException    if the caller does not have
	 *                                   access to the property accessor method
	 * @throws InvocationTargetException if the property accessor method
	 *                                   throws an exception
	 */
	protected void invokeSetter(Object target, String propName, String key, int index, Object newValue) throws IllegalAccessException, InvocationTargetException
	{
		try
		{
			if (index >= 0) getPropertyUtility().setIndexedProperty(target, propName, index, newValue);
				else if (key != null) getPropertyUtility().setMappedProperty(target, propName, key, newValue);
					else getPropertyUtility().setProperty(target, propName, newValue);
		}
		catch (NoSuchMethodException e)
		{
			throw new InvocationTargetException(e, "Cannot set " + propName);
		}
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
	 */
	protected AbstractDescriptor calculate(Object bean, String name) throws IllegalAccessException, InvocationTargetException
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
				target = getPropertyUtility().getProperty(bean, name.substring(0, delim));
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


