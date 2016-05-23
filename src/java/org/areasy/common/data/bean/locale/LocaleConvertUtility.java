package org.areasy.common.data.bean.locale;

/*
 * Copyright (c) 2007-2016 AREasy Runtime
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

import java.util.Locale;

/**
 * <p>Utility methods for converting locale-sensitive String scalar values to objects of the
 * specified Class, String arrays to arrays of the specified Class and
 * object to locale-sensitive String scalar value.</p>
 * <p/>
 * <p>The implementations for these method are provided by {@link LocaleConvertUtilityBean}.
 * These static utility method use the default instance. More sophisticated can be provided
 * by using a <code>LocaleConvertUtilsBean</code> instance.</p>
 *
 * @version $Id: LocaleConvertUtility.java,v 1.2 2008/05/14 09:32:41 swd\stefan.damian Exp $
 */
public class LocaleConvertUtility
{
	/**
	 * <p>Gets the <code>Locale</code> which will be used when
	 * no <code>Locale</code> is passed to a method.</p>
	 * <p/>
	 * <p>For more details see <code>LocaleConvertUtilsBean</code></p>
	 *
	 * @see LocaleConvertUtilityBean#getDefaultLocale()
	 */
	public static Locale getDefaultLocale()
	{
		return LocaleConvertUtilityBean.getInstance().getDefaultLocale();
	}

	/**
	 * <p>Sets the <code>Locale</code> which will be used when
	 * no <code>Locale</code> is passed to a method.</p>
	 * <p/>
	 * <p>For more details see <code>LocaleConvertUtilsBean</code></p>
	 *
	 * @see LocaleConvertUtilityBean#setDefaultLocale(Locale)
	 */
	public static void setDefaultLocale(Locale locale)
	{
		LocaleConvertUtilityBean.getInstance().setDefaultLocale(locale);
	}

	/**
	 * <p>Gets applyLocalized.</p>
	 * <p/>
	 * <p>For more details see <code>LocaleConvertUtilsBean</code></p>
	 *
	 * @see LocaleConvertUtilityBean#getApplyLocalized()
	 */
	public static boolean getApplyLocalized()
	{
		return LocaleConvertUtilityBean.getInstance().getApplyLocalized();
	}

	/**
	 * <p>Sets applyLocalized.</p>
	 * <p/>
	 * <p>For more details see <code>LocaleConvertUtilsBean</code></p>
	 *
	 * @see LocaleConvertUtilityBean#setApplyLocalized(boolean)
	 */
	public static void setApplyLocalized(boolean newApplyLocalized)
	{
		LocaleConvertUtilityBean.getInstance().setApplyLocalized(newApplyLocalized);
	}

	/**
	 * <p>Convert the specified locale-sensitive value into a String.</p>
	 * <p/>
	 * <p>For more details see <code>LocaleConvertUtilsBean</code></p>
	 *
	 * @see LocaleConvertUtilityBean#convert(Object)
	 */
	public static String convert(Object value)
	{
		return LocaleConvertUtilityBean.getInstance().convert(value);
	}

	/**
	 * <p>Convert the specified locale-sensitive value into a String
	 * using the convertion pattern.</p>
	 * <p/>
	 * <p>For more details see <code>LocaleConvertUtilsBean</code></p>
	 *
	 * @see LocaleConvertUtilityBean#convert(Object, String)
	 */
	public static String convert(Object value, String pattern)
	{
		return LocaleConvertUtilityBean.getInstance().convert(value, pattern);
	}

	/**
	 * <p>Convert the specified locale-sensitive value into a String
	 * using the paticular convertion pattern.</p>
	 * <p/>
	 * <p>For more details see <code>LocaleConvertUtilsBean</code></p>
	 *
	 * @see LocaleConvertUtilityBean#convert(Object, Locale, String)
	 */
	public static String convert(Object value, Locale locale, String pattern)
	{
		return LocaleConvertUtilityBean.getInstance().convert(value, locale, pattern);
	}

	/**
	 * <p>Convert the specified value to an object of the specified class (if
	 * possible).  Otherwise, return a String representation of the value.</p>
	 * <p/>
	 * <p>For more details see <code>LocaleConvertUtilsBean</code></p>
	 *
	 * @see LocaleConvertUtilityBean#convert(String, Class)
	 */
	public static Object convert(String value, Class clazz)
	{
		return LocaleConvertUtilityBean.getInstance().convert(value, clazz);
	}

	/**
	 * <p>Convert the specified value to an object of the specified class (if
	 * possible) using the convertion pattern. Otherwise, return a String
	 * representation of the value.</p>
	 * <p/>
	 * <p>For more details see <code>LocaleConvertUtilsBean</code></p>
	 *
	 * @see LocaleConvertUtilityBean#convert(String, Class, String)
	 */
	public static Object convert(String value, Class clazz, String pattern)
	{
		return LocaleConvertUtilityBean.getInstance().convert(value, clazz, pattern);
	}

	/**
	 * <p>Convert the specified value to an object of the specified class (if
	 * possible) using the convertion pattern. Otherwise, return a String
	 * representation of the value.</p>
	 * <p/>
	 * <p>For more details see <code>LocaleConvertUtilsBean</code></p>
	 *
	 * @see LocaleConvertUtilityBean#convert(String, Class, Locale, String)
	 */
	public static Object convert(String value, Class clazz, Locale locale, String pattern)
	{
		return LocaleConvertUtilityBean.getInstance().convert(value, clazz, locale, pattern);
	}

	/**
	 * <p>Convert an array of specified values to an array of objects of the
	 * specified class (if possible) using the convertion pattern.</p>
	 * <p/>
	 * <p>For more details see <code>LocaleConvertUtilsBean</code></p>
	 *
	 * @see LocaleConvertUtilityBean#convert(String[], Class, String)
	 */
	public static Object convert(String values[], Class clazz, String pattern)
	{
		return LocaleConvertUtilityBean.getInstance().convert(values, clazz, pattern);
	}

	/**
	 * <p>Convert an array of specified values to an array of objects of the
	 * specified class (if possible).</p>
	 * <p/>
	 * <p>For more details see <code>LocaleConvertUtilsBean</code></p>
	 *
	 * @see LocaleConvertUtilityBean#convert(String[], Class)
	 */
	public static Object convert(String values[], Class clazz)
	{
		return LocaleConvertUtilityBean.getInstance().convert(values, clazz);
	}

	/**
	 * <p>Convert an array of specified values to an array of objects of the
	 * specified class (if possible) using the convertion pattern.</p>
	 * <p/>
	 * <p>For more details see <code>LocaleConvertUtilsBean</code></p>
	 *
	 * @see LocaleConvertUtilityBean#convert(String[], Class, Locale, String)
	 */
	public static Object convert(String values[], Class clazz, Locale locale, String pattern)
	{
		return LocaleConvertUtilityBean.getInstance().convert(values, clazz, locale, pattern);
	}

	/**
	 * <p>Register a custom {@link LocaleConverter} for the specified destination
	 * <code>Class</code>, replacing any previously registered converter.</p>
	 * <p/>
	 * <p>For more details see <code>LocaleConvertUtilsBean</code></p>
	 *
	 * @see LocaleConvertUtilityBean#register(LocaleConverter, Class, Locale)
	 */
	public static void register(LocaleConverter converter, Class clazz, Locale locale)
	{
		LocaleConvertUtilityBean.getInstance().register(converter, clazz, locale);
	}

	/**
	 * <p>Remove any registered {@link LocaleConverter}.</p>
	 * <p/>
	 * <p>For more details see <code>LocaleConvertUtilsBean</code></p>
	 *
	 * @see LocaleConvertUtilityBean#deregister()
	 */
	public static void deregister()
	{
		LocaleConvertUtilityBean.getInstance().deregister();
	}


	/**
	 * <p>Remove any registered {@link LocaleConverter} for the specified locale.</p>
	 * <p/>
	 * <p>For more details see <code>LocaleConvertUtilsBean</code></p>
	 *
	 * @see LocaleConvertUtilityBean#deregister(Locale)
	 */
	public static void deregister(Locale locale)
	{
		LocaleConvertUtilityBean.getInstance().deregister(locale);
	}


	/**
	 * <p>Remove any registered {@link LocaleConverter} for the specified locale and Class.</p>
	 * <p/>
	 * <p>For more details see <code>LocaleConvertUtilsBean</code></p>
	 *
	 * @see LocaleConvertUtilityBean#deregister(Class, Locale)
	 */
	public static void deregister(Class clazz, Locale locale)
	{
		LocaleConvertUtilityBean.getInstance().deregister(clazz, locale);
	}

	/**
	 * <p>Look up and return any registered {@link LocaleConverter} for the specified
	 * destination class and locale; if there is no registered Converter, return
	 * <code>null</code>.</p>
	 * <p/>
	 * <p>For more details see <code>LocaleConvertUtilsBean</code></p>
	 *
	 * @see LocaleConvertUtilityBean#lookup(Class, Locale)
	 */
	public static LocaleConverter lookup(Class clazz, Locale locale)
	{
		return LocaleConvertUtilityBean.getInstance().lookup(clazz, locale);
	}

	/**
	 * <p>Look up and return any registered FastHashMap instance for the specified locale.</p>
	 * <p/>
	 * <p>For more details see <code>LocaleConvertUtilsBean</code></p>
	 *
	 * @see LocaleConvertUtilityBean#lookup(Locale)
	 */
	protected static FastHashMap lookup(Locale locale)
	{
		return LocaleConvertUtilityBean.getInstance().lookup(locale);
	}

	/**
	 * <p>Create all {@link LocaleConverter} types for specified locale.</p>
	 * <p/>
	 * <p>For more details see <code>LocaleConvertUtilsBean</code></p>
	 *
	 * @see LocaleConvertUtilityBean#create(Locale)
	 */
	protected static FastHashMap create(Locale locale)
	{
		return LocaleConvertUtilityBean.getInstance().create(locale);
	}
}
