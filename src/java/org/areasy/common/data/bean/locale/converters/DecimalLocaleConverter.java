package org.areasy.common.data.bean.locale.converters;

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

import org.areasy.common.data.bean.locale.BaseLocaleConverter;
import org.areasy.common.logger.Logger;
import org.areasy.common.logger.LoggerFactory;

import java.text.DecimalFormat;
import java.text.ParseException;
import java.util.Locale;


/**
 * <p>Standard {@link org.areasy.common.data.bean.locale.LocaleConverter}
 * implementation that converts an incoming
 * locale-sensitive String into a <code>java.lang.Decimal</code> object,
 * optionally using a default value or throwing a
 * {@link org.areasy.common.data.bean.ConversionException}
 * if a conversion error occurs.</p>
 *
 * @version $Id: DecimalLocaleConverter.java,v 1.2 2008/05/14 09:32:40 swd\stefan.damian Exp $
 */

public class DecimalLocaleConverter extends BaseLocaleConverter
{
	/**
	 * All logging goes through this logger
	 */
	private static Logger log = LoggerFactory.getLog(DecimalLocaleConverter.class);

	/**
	 * Create a {@link org.areasy.common.data.bean.locale.LocaleConverter}
	 * that will throw a {@link org.areasy.common.data.bean.ConversionException}
	 * if a conversion error occurs. The locale is the default locale for
	 * this instance of the Java Virtual Machine and an unlocalized pattern is used
	 * for the convertion.
	 */
	public DecimalLocaleConverter()
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
	public DecimalLocaleConverter(boolean locPattern)
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
	public DecimalLocaleConverter(Locale locale)
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
	public DecimalLocaleConverter(Locale locale, boolean locPattern)
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
	public DecimalLocaleConverter(Locale locale, String pattern)
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
	public DecimalLocaleConverter(Locale locale, String pattern, boolean locPattern)
	{
		this(null, locale, pattern, locPattern);
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
	public DecimalLocaleConverter(Object defaultValue)
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
	public DecimalLocaleConverter(Object defaultValue, boolean locPattern)
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
	public DecimalLocaleConverter(Object defaultValue, Locale locale)
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
	public DecimalLocaleConverter(Object defaultValue, Locale locale, boolean locPattern)
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
	public DecimalLocaleConverter(Object defaultValue, Locale locale, String pattern)
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
	public DecimalLocaleConverter(Object defaultValue, Locale locale, String pattern, boolean locPattern)
	{
		super(defaultValue, locale, pattern, locPattern);
	}

	/**
	 * Convert the specified locale-sensitive input object into an output object of the
	 * specified type.
	 *
	 * @param value   The input object to be converted
	 * @param pattern The pattern is used for the convertion
	 * @throws org.areasy.common.data.bean.ConversionException if conversion cannot be performed
	 *                             successfully
	 */
	protected Object parse(Object value, String pattern) throws ParseException
	{
		// DecimalFormat is not thread safe so best to construct one each time
		DecimalFormat formatter = (DecimalFormat) DecimalFormat.getInstance(locale);
		// if some constructors default pattern to null, it makes only sense to handle null pattern gracefully
		if (pattern != null)
		{
			if (locPattern) formatter.applyLocalizedPattern(pattern);
				else formatter.applyPattern(pattern);
		}
		else log.warn("No pattern provided, using default.");

		return formatter.parse((String) value);
	}
}
