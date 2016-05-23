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

import org.areasy.common.data.bean.locale.converters.*;
import org.areasy.common.data.type.FastHashMap;
import org.areasy.common.logger.Logger;
import org.areasy.common.logger.LoggerFactory;

import java.lang.reflect.Array;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Locale;

/**
 * <p>Utility methods for converting locale-sensitive String scalar values to objects of the
 * specified Class, String arrays to arrays of the specified Class and
 * object to locale-sensitive String scalar value.</p>
 * <p/>
 * <p>This class provides the implementations used by the static utility methods in
 * {@link LocaleConvertUtility}.</p>
 * <p/>
 * <p>The actual {@link LocaleConverter} instance to be used
 * can be registered for each possible destination Class. Unless you override them, standard
 * {@link LocaleConverter} instances are provided for all of the following
 * destination Classes:</p>
 * <ul>
 * <li>java.lang.BigDecimal</li>
 * <li>java.lang.BigInteger</li>
 * <li>byte and java.lang.Byte</li>
 * <li>double and java.lang.Double</li>
 * <li>float and java.lang.Float</li>
 * <li>int and java.lang.Integer</li>
 * <li>long and java.lang.Long</li>
 * <li>short and java.lang.Short</li>
 * <li>java.lang.String</li>
 * <li>java.sql.Date</li>
 * <li>java.sql.Time</li>
 * <li>java.sql.Timestamp</li>
 * </ul>
 * <p/>
 * <p>For backwards compatibility, the standard locale converters
 * for primitive types (and the corresponding wrapper classes).
 * <p/>
 * If you prefer to have another {@link LocaleConverter}
 * thrown instead, replace the standard {@link LocaleConverter} instances
 * with ones created with the one of the appropriate constructors.
 * <p/>
 * It's important that {@link LocaleConverter} should be registered for
 * the specified locale and Class (or primitive type).
 *
 * @version $Id: LocaleConvertUtilityBean.java,v 1.2 2008/05/14 09:32:41 swd\stefan.damian Exp $
 */
public class LocaleConvertUtilityBean
{

	/**
	 * Gets singleton instance.
	 * This is the same as the instance used by the default {@link LocaleBeanUtilityBean} singleton.
	 */
	public static LocaleConvertUtilityBean getInstance()
	{
		return LocaleBeanUtilityBean.getLocaleBeanUtilsInstance().getLocaleConvertUtils();
	}

	/**
	 * The locale - default for convertion.
	 */
	private Locale defaultLocale = Locale.getDefault();

	/**
	 * Indicate whether the pattern is localized or not
	 */
	private boolean applyLocalized = false;

	/**
	 * The <code>Log</code> instance for this class.
	 */
	private Logger log = LoggerFactory.getLog(LocaleConvertUtility.class);

	/**
	 * Every entry of the mapConverters is:
	 * key = locale
	 * value = FastHashMap of converters for the certain locale.
	 */
	private FastHashMap mapConverters = new FastHashMap();

	/**
	 * Makes the state by default (deregisters all converters for all locales)
	 * and then registers default locale converters.
	 */
	public LocaleConvertUtilityBean()
	{
		deregister();
	}

	/**
	 * getter for defaultLocale
	 */
	public Locale getDefaultLocale()
	{

		return defaultLocale;
	}

	/**
	 * setter for defaultLocale
	 */
	public void setDefaultLocale(Locale locale)
	{
		if (locale == null) defaultLocale = Locale.getDefault();
			else defaultLocale = locale;
	}

	/**
	 * getter for applyLocalized
	 */
	public boolean getApplyLocalized()
	{
		return applyLocalized;
	}

	/**
	 * setter for applyLocalized
	 */
	public void setApplyLocalized(boolean newApplyLocalized)
	{
		applyLocalized = newApplyLocalized;
	}

	/**
	 * Convert the specified locale-sensitive value into a String.
	 *
	 * @param value The Value to be converted
	 * @throws org.areasy.common.data.bean.ConversionException
	 *          if thrown by an underlying Converter
	 */
	public String convert(Object value)
	{
		return convert(value, defaultLocale, null);
	}

	/**
	 * Convert the specified locale-sensitive value into a String
	 * using the convertion pattern.
	 *
	 * @param value   The Value to be converted
	 * @param pattern The convertion pattern
	 * @throws org.areasy.common.data.bean.ConversionException if thrown by an underlying Converter
	 */
	public String convert(Object value, String pattern)
	{
		return convert(value, defaultLocale, pattern);
	}

	/**
	 * Convert the specified locale-sensitive value into a String
	 * using the paticular convertion pattern.
	 *
	 * @param value   The Value to be converted
	 * @param locale  The locale
	 * @param pattern The convertion pattern
	 * @throws org.areasy.common.data.bean.ConversionException if thrown by an underlying Converter
	 */
	public String convert(Object value, Locale locale, String pattern)
	{
		LocaleConverter converter = lookup(String.class, locale);

		return (String) converter.convert(String.class, value, pattern);
	}

	/**
	 * Convert the specified value to an object of the specified class (if
	 * possible).  Otherwise, return a String representation of the value.
	 *
	 * @param value The String scalar value to be converted
	 * @param clazz The Data type to which this value should be converted.
	 * @throws org.areasy.common.data.bean.ConversionException if thrown by an underlying Converter
	 */
	public Object convert(String value, Class clazz)
	{
		return convert(value, clazz, defaultLocale, null);
	}

	/**
	 * Convert the specified value to an object of the specified class (if
	 * possible) using the convertion pattern. Otherwise, return a String
	 * representation of the value.
	 *
	 * @param value   The String scalar value to be converted
	 * @param clazz   The Data type to which this value should be converted.
	 * @param pattern The convertion pattern
	 * @throws org.areasy.common.data.bean.ConversionException if thrown by an underlying Converter
	 */
	public Object convert(String value, Class clazz, String pattern)
	{
		return convert(value, clazz, defaultLocale, pattern);
	}

	/**
	 * Convert the specified value to an object of the specified class (if
	 * possible) using the convertion pattern. Otherwise, return a String
	 * representation of the value.
	 *
	 * @param value   The String scalar value to be converted
	 * @param clazz   The Data type to which this value should be converted.
	 * @param locale  The locale
	 * @param pattern The convertion pattern
	 * @throws org.areasy.common.data.bean.ConversionException if thrown by an underlying Converter
	 */
	public Object convert(String value, Class clazz, Locale locale, String pattern)
	{
		LocaleConverter converter = lookup(clazz, locale);

		if (converter == null) converter = (LocaleConverter) lookup(String.class, locale);

		return (converter.convert(clazz, value, pattern));
	}

	/**
	 * Convert an array of specified values to an array of objects of the
	 * specified class (if possible) using the convertion pattern.
	 *
	 * @param values  Value to be converted (may be null)
	 * @param clazz   Java array or element class to be converted to
	 * @param pattern The convertion pattern
	 * @throws org.areasy.common.data.bean.ConversionException if thrown by an underlying Converter
	 */
	public Object convert(String values[], Class clazz, String pattern)
	{
		return convert(values, clazz, getDefaultLocale(), pattern);
	}

	/**
	 * Convert an array of specified values to an array of objects of the
	 * specified class (if possible) .
	 *
	 * @param values Value to be converted (may be null)
	 * @param clazz  Java array or element class to be converted to
	 * @throws org.areasy.common.data.bean.ConversionException if thrown by an underlying Converter
	 */
	public Object convert(String values[], Class clazz)
	{
		return convert(values, clazz, getDefaultLocale(), null);
	}

	/**
	 * Convert an array of specified values to an array of objects of the
	 * specified class (if possible) using the convertion pattern.
	 *
	 * @param values  Value to be converted (may be null)
	 * @param clazz   Java array or element class to be converted to
	 * @param locale  The locale
	 * @param pattern The convertion pattern
	 * @throws org.areasy.common.data.bean.ConversionException if thrown by an underlying Converter
	 */
	public Object convert(String values[], Class clazz, Locale locale, String pattern)
	{
		Class type = clazz;
		if (clazz.isArray()) type = clazz.getComponentType();

		Object array = Array.newInstance(type, values.length);
		for (int i = 0; i < values.length; i++)
		{
			Array.set(array, i, convert(values[i], type, locale, pattern));
		}

		return (array);
	}

	/**
	 * Register a custom {@link LocaleConverter} for the specified destination
	 * <code>Class</code>, replacing any previously registered converter.
	 *
	 * @param converter The LocaleConverter to be registered
	 * @param clazz     The Destination class for conversions performed by this
	 *                  Converter
	 * @param locale    The locale
	 */
	public void register(LocaleConverter converter, Class clazz, Locale locale)
	{
		lookup(locale).put(clazz, converter);
	}

	/**
	 * Remove any registered {@link LocaleConverter}.
	 */
	public void deregister()
	{
		FastHashMap defaultConverter = lookup(defaultLocale);

		mapConverters.setFast(false);

		mapConverters.clear();
		mapConverters.put(defaultLocale, defaultConverter);

		mapConverters.setFast(true);
	}

	/**
	 * Remove any registered {@link LocaleConverter} for the specified locale
	 *
	 * @param locale The locale
	 */
	public void deregister(Locale locale)
	{
		mapConverters.remove(locale);
	}

	/**
	 * Remove any registered {@link LocaleConverter} for the specified locale and Class.
	 *
	 * @param clazz  Class for which to remove a registered Converter
	 * @param locale The locale
	 */
	public void deregister(Class clazz, Locale locale)
	{
		lookup(locale).remove(clazz);
	}

	/**
	 * Look up and return any registered {@link LocaleConverter} for the specified
	 * destination class and locale; if there is no registered Converter, return
	 * <code>null</code>.
	 *
	 * @param clazz  Class for which to return a registered Converter
	 * @param locale The Locale
	 */
	public LocaleConverter lookup(Class clazz, Locale locale)
	{
		LocaleConverter converter = (LocaleConverter) lookup(locale).get(clazz);

		return converter;
	}

	/**
	 * Look up and return any registered FastHashMap instance for the specified locale;
	 * if there is no registered one, return <code>null</code>.
	 *
	 * @param locale The Locale
	 * @return The FastHashMap instance contains the all {@link LocaleConverter} types for
	 *         the specified locale.
	 */
	protected FastHashMap lookup(Locale locale)
	{
		FastHashMap localeConverters;

		if (locale == null) localeConverters = (FastHashMap) mapConverters.get(defaultLocale);
		else
		{
			localeConverters = (FastHashMap) mapConverters.get(locale);

			if (localeConverters == null)
			{
				localeConverters = create(locale);
				mapConverters.put(locale, localeConverters);
			}
		}

		return localeConverters;
	}

	/**
	 * Create all {@link LocaleConverter} types for specified locale.
	 *
	 * @param locale The Locale
	 * @return The FastHashMap instance contains the all {@link LocaleConverter} types
	 *         for the specified locale.
	 */
	protected FastHashMap create(Locale locale)
	{
		FastHashMap converter = new FastHashMap();
		converter.setFast(false);

		converter.put(BigDecimal.class, new BigDecimalLocaleConverter(locale, applyLocalized));
		converter.put(BigInteger.class, new BigIntegerLocaleConverter(locale, applyLocalized));

		converter.put(Byte.class, new ByteLocaleConverter(locale, applyLocalized));
		converter.put(Byte.TYPE, new ByteLocaleConverter(locale, applyLocalized));

		converter.put(Double.class, new DoubleLocaleConverter(locale, applyLocalized));
		converter.put(Double.TYPE, new DoubleLocaleConverter(locale, applyLocalized));

		converter.put(Float.class, new FloatLocaleConverter(locale, applyLocalized));
		converter.put(Float.TYPE, new FloatLocaleConverter(locale, applyLocalized));

		converter.put(Integer.class, new IntegerLocaleConverter(locale, applyLocalized));
		converter.put(Integer.TYPE, new IntegerLocaleConverter(locale, applyLocalized));

		converter.put(Long.class, new LongLocaleConverter(locale, applyLocalized));
		converter.put(Long.TYPE, new LongLocaleConverter(locale, applyLocalized));

		converter.put(Short.class, new ShortLocaleConverter(locale, applyLocalized));
		converter.put(Short.TYPE, new ShortLocaleConverter(locale, applyLocalized));

		converter.put(String.class, new StringLocaleConverter(locale, applyLocalized));

		// conversion format patterns of java.sql.* types should correspond to default
		// behaviour of toString and valueOf methods of these classes
		converter.put(java.sql.Date.class, new SqlDateLocaleConverter(locale, "yyyy-MM-dd"));
		converter.put(java.sql.Time.class, new SqlTimeLocaleConverter(locale, "HH:mm:ss"));
		converter.put(java.sql.Timestamp.class, new SqlTimestampLocaleConverter(locale, "yyyy-MM-dd HH:mm:ss.S"));

		converter.setFast(true);

		return converter;
	}
}
