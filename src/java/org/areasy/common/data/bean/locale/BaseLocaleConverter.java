package org.areasy.common.data.bean.locale;

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

import org.areasy.common.data.bean.ConversionException;
import org.areasy.common.logger.Logger;
import org.areasy.common.logger.LoggerFactory;

import java.text.ParseException;
import java.util.Locale;


/**
 * <p>The base class for all standart type locale-sensitive converters.
 * It has {@link LocaleConverter} and {@link org.areasy.common.data.bean.Converter} implementations,
 * that convert an incoming locale-sensitive Object into an object of correspond type,
 * optionally using a default value or throwing a {@link ConversionException}
 * if a conversion error occurs.</p>
 *
 * @version $Id: BaseLocaleConverter.java,v 1.2 2008/05/14 09:32:41 swd\stefan.damian Exp $
 */

public abstract class BaseLocaleConverter implements LocaleConverter
{
	/**
	 * All logging goes through this logger
	 */
	private static Logger log = LoggerFactory.getLog(BaseLocaleConverter.class);

	/**
	 * The default value specified to our Constructor, if any.
	 */
	private Object defaultValue = null;

	/**
	 * Should we return the default value on conversion errors?
	 */
	protected boolean useDefault = false;

	/**
	 * The locale specified to our Constructor, by default - system locale.
	 */
	protected Locale locale = Locale.getDefault();

	/**
	 * The default pattern specified to our Constructor, if any.
	 */
	protected String pattern = null;

	/**
	 * The flag indicating whether the given pattern string is localized or not.
	 */
	protected boolean locPattern = false;

	/**
	 * Create a {@link LocaleConverter} that will throw a {@link ConversionException}
	 * if a conversion error occurs.
	 * An unlocalized pattern is used for the convertion.
	 *
	 * @param locale  The locale
	 * @param pattern The convertion pattern
	 */
	protected BaseLocaleConverter(Locale locale, String pattern)
	{
		this(null, locale, pattern, false, false);
	}

	/**
	 * Create a {@link LocaleConverter} that will throw a {@link ConversionException}
	 * if a conversion error occurs.
	 *
	 * @param locale     The locale
	 * @param pattern    The convertion pattern
	 * @param locPattern Indicate whether the pattern is localized or not
	 */
	protected BaseLocaleConverter(Locale locale, String pattern, boolean locPattern)
	{
		this(null, locale, pattern, false, locPattern);
	}

	/**
	 * Create a {@link LocaleConverter} that will return the specified default value
	 * if a conversion error occurs.
	 * An unlocalized pattern is used for the convertion.
	 *
	 * @param defaultValue The default value to be returned
	 * @param locale       The locale
	 * @param pattern      The convertion pattern
	 */
	protected BaseLocaleConverter(Object defaultValue, Locale locale, String pattern)
	{
		this(defaultValue, locale, pattern, false);
	}

	/**
	 * Create a {@link LocaleConverter} that will return the specified default value
	 * if a conversion error occurs.
	 *
	 * @param defaultValue The default value to be returned
	 * @param locale       The locale
	 * @param pattern      The convertion pattern
	 * @param locPattern   Indicate whether the pattern is localized or not
	 */
	protected BaseLocaleConverter(Object defaultValue, Locale locale, String pattern, boolean locPattern)
	{
		this(defaultValue, locale, pattern, true, locPattern);
	}

	/**
	 * Create a {@link LocaleConverter} that will return the specified default value
	 * or throw a {@link ConversionException} if a conversion error occurs.
	 *
	 * @param defaultValue The default value to be returned
	 * @param locale       The locale
	 * @param pattern      The convertion pattern
	 * @param useDefault   Indicate whether the default value is used or not
	 * @param locPattern   Indicate whether the pattern is localized or not
	 */
	private BaseLocaleConverter(Object defaultValue, Locale locale, String pattern, boolean useDefault, boolean locPattern)
	{
		if (useDefault)
		{
			this.defaultValue = defaultValue;
			this.useDefault = true;
		}

		if (locale != null) this.locale = locale;

		this.pattern = pattern;
		this.locPattern = locPattern;
	}

	/**
	 * Convert the specified locale-sensitive input object into an output object of the
	 * specified type.
	 *
	 * @param value   The input object to be converted
	 * @param pattern The pattern is used for the convertion
	 * @throws ConversionException if conversion cannot be performed
	 *                             successfully
	 */

	abstract protected Object parse(Object value, String pattern) throws ParseException;


	/**
	 * Convert the specified locale-sensitive input object into an output object.
	 * The default pattern is used for the convertion.
	 *
	 * @param value The input object to be converted
	 * @throws ConversionException if conversion cannot be performed
	 *                             successfully
	 */
	public Object convert(Object value)
	{
		return convert(value, null);
	}

	/**
	 * Convert the specified locale-sensitive input object into an output object.
	 *
	 * @param value   The input object to be converted
	 * @param pattern The pattern is used for the convertion
	 * @throws ConversionException if conversion cannot be performed
	 *                             successfully
	 */
	public Object convert(Object value, String pattern)
	{
		return convert(null, value, pattern);
	}

	/**
	 * Convert the specified locale-sensitive input object into an output object of the
	 * specified type. The default pattern is used for the convertion.
	 *
	 * @param type  Data type to which this value should be converted
	 * @param value The input object to be converted
	 * @throws ConversionException if conversion cannot be performed
	 *                             successfully
	 */
	public Object convert(Class type, Object value)
	{
		return convert(type, value, null);
	}

	/**
	 * Convert the specified locale-sensitive input object into an output object of the
	 * specified type.
	 *
	 * @param type    Data type to which this value should be converted
	 * @param value   The input object to be converted
	 * @param pattern The pattern is used for the convertion
	 * @throws ConversionException if conversion cannot be performed
	 *                             successfully
	 */
	public Object convert(Class type, Object value, String pattern)
	{
		if (value == null)
		{
			if (useDefault) return (defaultValue);
			else
			{
				// symmetric beanutils function allows null
				// so do not: throw new ConversionException("No value specified");
				log.debug("Null value specified for conversion, returing null");
				return null;
			}
		}

		try
		{
			if (pattern != null) return parse(value, pattern);
				else return parse(value, this.pattern);
		}
		catch (Exception e)
		{
			if (useDefault) return (defaultValue);
				else throw new ConversionException(e);
		}
	}
}
