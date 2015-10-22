package org.areasy.common.data.bean.locale.converters;

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

import org.areasy.common.data.bean.locale.BaseLocaleConverter;
import org.areasy.common.logger.Logger;
import org.areasy.common.logger.LoggerFactory;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Locale;


/**
 * <p>Standard {@link org.areasy.common.data.bean.locale.LocaleConverter}
 * implementation that converts an incoming
 * locale-sensitive String into a <code>java.util.Date</code> object,
 * optionally using a default value or throwing a
 * {@link org.areasy.common.data.bean.ConversionException}
 * if a conversion error occurs.</p>
 *
 * @version $Id: DateLocaleConverter.java,v 1.2 2008/05/14 09:32:40 swd\stefan.damian Exp $
 */

public class DateLocaleConverter extends BaseLocaleConverter
{
	/**
	 * All logging goes through this logger
	 */
	private static Logger log = LoggerFactory.getLog(DateLocaleConverter.class);

	/**
	 * Should the date conversion be lenient?
	 */
	boolean isLenient = false;

	/**
	 * Create a {@link org.areasy.common.data.bean.locale.LocaleConverter}
	 * that will throw a {@link org.areasy.common.data.bean.ConversionException}
	 * if a conversion error occurs. The locale is the default locale for
	 * this instance of the Java Virtual Machine and an unlocalized pattern is used
	 * for the convertion.
	 */
	public DateLocaleConverter()
	{
		this(false);
	}

	/**
	 * Create a {@link org.areasy.common.data.bean.locale.LocaleConverter}
	 * that will throw a {@link org.areasy.common.data.bean.ConversionException}
	 * if a conversion error occurs. The locale is the default locale for
	 * this instance of the Java Virtual Machine.
	 *
	 * @param locPattern Indicate whether the pattern is localized or not
	 */
	public DateLocaleConverter(boolean locPattern)
	{
		this(Locale.getDefault(), locPattern);
	}

	/**
	 * Create a {@link org.areasy.common.data.bean.locale.LocaleConverter}
	 * that will throw a {@link org.areasy.common.data.bean.ConversionException}
	 * if a conversion error occurs. An unlocalized pattern is used for the convertion.
	 *
	 * @param locale The locale
	 */
	public DateLocaleConverter(Locale locale)
	{
		this(locale, false);
	}

	/**
	 * Create a {@link org.areasy.common.data.bean.locale.LocaleConverter}
	 * that will throw a {@link org.areasy.common.data.bean.ConversionException}
	 * if a conversion error occurs.
	 *
	 * @param locale     The locale
	 * @param locPattern Indicate whether the pattern is localized or not
	 */
	public DateLocaleConverter(Locale locale, boolean locPattern)
	{
		this(locale, (String) null, locPattern);
	}

	/**
	 * Create a {@link org.areasy.common.data.bean.locale.LocaleConverter}
	 * that will throw a {@link org.areasy.common.data.bean.ConversionException}
	 * if a conversion error occurs. An unlocalized pattern is used for the convertion.
	 *
	 * @param locale  The locale
	 * @param pattern The convertion pattern
	 */
	public DateLocaleConverter(Locale locale, String pattern)
	{
		this(locale, pattern, false);
	}

	/**
	 * Create a {@link org.areasy.common.data.bean.locale.LocaleConverter}
	 * that will throw a {@link org.areasy.common.data.bean.ConversionException}
	 * if a conversion error occurs.
	 *
	 * @param locale     The locale
	 * @param pattern    The convertion pattern
	 * @param locPattern Indicate whether the pattern is localized or not
	 */
	public DateLocaleConverter(Locale locale, String pattern, boolean locPattern)
	{

		super(locale, pattern, locPattern);
	}

	/**
	 * Create a {@link org.areasy.common.data.bean.locale.LocaleConverter}
	 * that will return the specified default value
	 * if a conversion error occurs. The locale is the default locale for
	 * this instance of the Java Virtual Machine and an unlocalized pattern is used
	 * for the convertion.
	 *
	 * @param defaultValue The default value to be returned
	 */
	public DateLocaleConverter(Object defaultValue)
	{

		this(defaultValue, false);
	}

	/**
	 * Create a {@link org.areasy.common.data.bean.locale.LocaleConverter}
	 * that will return the specified default value
	 * if a conversion error occurs. The locale is the default locale for
	 * this instance of the Java Virtual Machine.
	 *
	 * @param defaultValue The default value to be returned
	 * @param locPattern   Indicate whether the pattern is localized or not
	 */
	public DateLocaleConverter(Object defaultValue, boolean locPattern)
	{

		this(defaultValue, Locale.getDefault(), locPattern);
	}

	/**
	 * Create a {@link org.areasy.common.data.bean.locale.LocaleConverter}
	 * that will return the specified default value
	 * if a conversion error occurs. An unlocalized pattern is used for the convertion.
	 *
	 * @param defaultValue The default value to be returned
	 * @param locale       The locale
	 */
	public DateLocaleConverter(Object defaultValue, Locale locale)
	{

		this(defaultValue, locale, false);
	}

	/**
	 * Create a {@link org.areasy.common.data.bean.locale.LocaleConverter}
	 * that will return the specified default value
	 * if a conversion error occurs.
	 *
	 * @param defaultValue The default value to be returned
	 * @param locale       The locale
	 * @param locPattern   Indicate whether the pattern is localized or not
	 */
	public DateLocaleConverter(Object defaultValue, Locale locale, boolean locPattern)
	{

		this(defaultValue, locale, null, locPattern);
	}


	/**
	 * Create a {@link org.areasy.common.data.bean.locale.LocaleConverter}
	 * that will return the specified default value
	 * if a conversion error occurs. An unlocalized pattern is used for the convertion.
	 *
	 * @param defaultValue The default value to be returned
	 * @param locale       The locale
	 * @param pattern      The convertion pattern
	 */
	public DateLocaleConverter(Object defaultValue, Locale locale, String pattern)
	{

		this(defaultValue, locale, pattern, false);
	}

	/**
	 * Create a {@link org.areasy.common.data.bean.locale.LocaleConverter}
	 * that will return the specified default value
	 * if a conversion error occurs.
	 *
	 * @param defaultValue The default value to be returned
	 * @param locale       The locale
	 * @param pattern      The convertion pattern
	 * @param locPattern   Indicate whether the pattern is localized or not
	 */
	public DateLocaleConverter(Object defaultValue, Locale locale, String pattern, boolean locPattern)
	{

		super(defaultValue, locale, pattern, locPattern);
	}

	/**
	 * Returns whether date formatting is lenient.
	 *
	 * @return true if the <code>DateFormat</code> used for formatting is lenient
	 * @see java.text.DateFormat#isLenient
	 */
	public boolean isLenient()
	{
		return isLenient;
	}

	/**
	 * Specify whether or not date-time parsing should be lenient.
	 *
	 * @param lenient true if the <code>DateFormat</code> used for formatting should be lenient
	 * @see java.text.DateFormat#setLenient
	 */
	public void setLenient(boolean lenient)
	{
		isLenient = lenient;
	}

	/**
	 * Convert the specified locale-sensitive input object into an output object of the
	 * specified type.
	 *
	 * @param value   The input object to be converted
	 * @param pattern The pattern is used for the convertion
	 * @throws org.areasy.common.data.bean.ConversionException
	 *          if conversion cannot be performed
	 *          successfully
	 */
	protected Object parse(Object value, String pattern) throws ParseException
	{
		SimpleDateFormat formatter = getFormatter(pattern, locale);

		if (locPattern) formatter.applyLocalizedPattern(pattern);
			else formatter.applyPattern(pattern);

		return formatter.parse((String) value);
	}

	/**
	 * Gets an appropriate <code>SimpleDateFormat</code> for given locale,
	 * default Date format pattern is not provided.
	 */
	private SimpleDateFormat getFormatter(String pattern, Locale locale)
	{
		// This method is a fix for null pattern, which would cause
		// Null pointer exception when applied
		// Note: that many constructors default the pattern to null,
		// so it only makes sense to handle nulls gracefully
		if (pattern == null)
		{
			pattern = locPattern ? new SimpleDateFormat().toLocalizedPattern() : new SimpleDateFormat().toPattern();
			log.warn("Null pattern was provided, defaulting to: " + pattern);
		}

		SimpleDateFormat format = new SimpleDateFormat(pattern, locale);
		format.setLenient(isLenient);

		return format;
	}
}
