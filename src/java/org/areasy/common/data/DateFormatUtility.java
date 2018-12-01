package org.areasy.common.data;

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

import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

/**
 * <p>Date and time formatting utilities and constants.</p>
 * <p/>
 * <p>Formatting is performed using the
 * {@link org.areasy.common.data.FastDateFormatUtility} class.</p>
 *
 * @version $Id: DateFormatUtility.java,v 1.3 2008/05/14 09:32:29 swd\stefan.damian Exp $
 */
public class DateFormatUtility
{
	/**
	 * The format used is <tt>yyyyMMdd</tt>.
	 */
	public static final FastDateFormatUtility DB_TRIM_DATE_FORMAT = FastDateFormatUtility.getInstance("yyyyMMdd");

	/**
	 * The format used is <tt>yyyyMMddHHmmss</tt>.
	 */
	public static final FastDateFormatUtility DB_TRIM_DATETIME_FORMAT = FastDateFormatUtility.getInstance("yyyyMMddHHmmss");

	/**
	 * The format used is <tt>MM/dd/yyyy</tt>.
	 */
	public static final FastDateFormatUtility DB_WIN_DATE_FORMAT = FastDateFormatUtility.getInstance("MM/dd/yyyy");

	/**
	 * The format used is <tt>MM/dd/yyyy HH:mm:ss</tt>.
	 */
	public static final FastDateFormatUtility DB_WIN_DATETIME_FORMAT = FastDateFormatUtility.getInstance("MM/dd/yyyy HH:mm:ss");

	/**
	 * The format used is <tt>MM/dd/yyyy HH:mm:ss</tt>.
	 */
	public static final FastDateFormatUtility DB_WINCLASSIC_DATETIME_FORMAT = FastDateFormatUtility.getInstance("MM/dd/yyyy h:mm");

	/**
	 * ISO8601 formatter for date-time without time zone.
	 * The format used is <tt>yyyy-MM-dd'T'HH:mm:ss</tt>.
	 */
	public static final FastDateFormatUtility ISO_DATETIME_FORMAT = FastDateFormatUtility.getInstance("yyyy-MM-dd'T'HH:mm:ss");

	/**
	 * ISO8601 formatter for date-time without time zone.
	 * The format used is <tt>yyyy-MM-dd HH:mm:ss</tt>.
	 */
	public static final FastDateFormatUtility ISOMT_DATETIME_FORMAT = FastDateFormatUtility.getInstance("yyyy-MM-dd HH:mm:ss");

	/**
	 * ISO8601 formatter for date-time with time zone.
	 * The format used is <tt>yyyy-MM-dd'T'HH:mm:ssZZ</tt>.
	 */
	public static final FastDateFormatUtility ISO_DATETIME_TIME_ZONE_FORMAT = FastDateFormatUtility.getInstance("yyyy-MM-dd'T'HH:mm:ssZZ");

	/**
	 * ISO8601 formatter for date-time with time zone.
	 * The format used is <tt>yyyy-MM-dd HH:mm:ssZZ</tt>.
	 */
	public static final FastDateFormatUtility ISOMT_DATETIME_TIME_ZONE_FORMAT = FastDateFormatUtility.getInstance("yyyy-MM-dd HH:mm:ssZZ");

	/**
	 * ISO8601 formatter for date without time zone.
	 * The format used is <tt>yyyy-MM-dd</tt>.
	 */
	public static final FastDateFormatUtility ISO_DATE_FORMAT = FastDateFormatUtility.getInstance("yyyy-MM-dd");

	/**
	 * ISO8601-like formatter for date with time zone.
	 * The format used is <tt>yyyy-MM-ddZZ</tt>.
	 * This pattern does not comply with the formal ISO8601 specification
	 * as the standard does not allow a time zone  without a time.
	 */
	public static final FastDateFormatUtility ISO_DATE_TIME_ZONE_FORMAT = FastDateFormatUtility.getInstance("yyyy-MM-ddZZ");

	/**
	 * ISO8601 formatter for time without time zone.
	 * The format used is <tt>'T'HH:mm:ss</tt>.
	 */
	public static final FastDateFormatUtility ISO_TIME_FORMAT = FastDateFormatUtility.getInstance("'T'HH:mm:ss");

	/**
	 * ISO8601 formatter for time without time zone.
	 * The format used is <tt>HH:mm:ss</tt>.
	 */
	public static final FastDateFormatUtility ISOMT_TIME_FORMAT = FastDateFormatUtility.getInstance("HH:mm:ss");

	/**
	 * ISO8601 formatter for time with time zone.
	 * The format used is <tt>'T'HH:mm:ssZZ</tt>.
	 */
	public static final FastDateFormatUtility ISO_TIME_TIME_ZONE_FORMAT = FastDateFormatUtility.getInstance("'T'HH:mm:ssZZ");

	/**
	 * ISO8601 formatter for time with time zone.
	 * The format used is <tt>'T'HH:mm:ssZZ</tt>.
	 */
	public static final FastDateFormatUtility ISOMT_TIME_TIME_ZONE_FORMAT = FastDateFormatUtility.getInstance("HH:mm:ssZZ");

	/**
	 * ISO8601-like formatter for time with time zone.
	 * The format used is <tt>HH:mm:ssZZ</tt>.
	 * This pattern does not comply with the formal ISO8601 specification
	 * as the standard requires the 'T' prefix for times.
	 */
	public static final FastDateFormatUtility ISO_TIME_NO_T_TIME_ZONE_FORMAT = FastDateFormatUtility.getInstance("HH:mm:ssZZ");

	/**
	 * User formatter for date without time zone.
	 * The format used is <tt>dd-MM-yyyy HH:mm</tt>.
	 */
	public static final FastDateFormatUtility USER_DATE_FORMAT = FastDateFormatUtility.getInstance("dd-MM-yyyy HH:mm");

	/**
	 * SMTP (and probably other) date headers.
	 * The format used is <tt>EEE, dd MMM yyyy HH:mm:ss Z</tt> in US locale.
	 */
	public static final FastDateFormatUtility SMTP_DATETIME_FORMAT = FastDateFormatUtility.getInstance("EEE, dd MMM yyyy HH:mm:ss Z", Locale.US);

	public static final String DATE_MSB_PATTERNS[] = { ISOMT_DATETIME_TIME_ZONE_FORMAT.getPattern(), ISOMT_DATETIME_FORMAT.getPattern(),
													ISO_DATE_FORMAT.getPattern(), ISOMT_TIME_FORMAT.getPattern(),
													ISO_DATETIME_TIME_ZONE_FORMAT.getPattern(), ISO_DATETIME_FORMAT.getPattern(),
												  	ISO_DATE_TIME_ZONE_FORMAT.getPattern(), ISO_TIME_FORMAT.getPattern(),
												  	ISO_TIME_TIME_ZONE_FORMAT.getPattern(), SMTP_DATETIME_FORMAT.getPattern(),
													DB_TRIM_DATE_FORMAT.getPattern(), DB_TRIM_DATETIME_FORMAT.getPattern(),
												  	DB_WIN_DATE_FORMAT.getPattern(), DB_WIN_DATETIME_FORMAT.getPattern(),
													DB_WINCLASSIC_DATETIME_FORMAT.getPattern() };

	/**
	 * <p>DateFormatUtility instances should NOT be constructed in standard programming.</p>
	 * <p/>
	 * <p>This constructor is public to permit tools that require a JavaBean instance
	 * to operate.</p>
	 */
	public DateFormatUtility()
	{
		//nothing to do
	}

	/**
	 * <p>Format a date/time into a specific pattern using the UTC time zone.</p>
	 *
	 * @param millis  the date to format expressed in milliseconds
	 * @param pattern the pattern to use to format the date
	 * @return the formatted date
	 */
	public static String formatUTC(long millis, String pattern)
	{
		return format(new Date(millis), pattern, DateUtility.UTC_TIME_ZONE, null);
	}

	/**
	 * <p>Format a date/time into a specific pattern using the UTC time zone.</p>
	 *
	 * @param date    the date to format
	 * @param pattern the pattern to use to format the date
	 * @return the formatted date
	 */
	public static String formatUTC(Date date, String pattern)
	{
		return format(date, pattern, DateUtility.UTC_TIME_ZONE, null);
	}

	/**
	 * <p>Format a date/time into a specific pattern using the UTC time zone.</p>
	 *
	 * @param millis  the date to format expressed in milliseconds
	 * @param pattern the pattern to use to format the date
	 * @param locale  the locale to use, may be <code>null</code>
	 * @return the formatted date
	 */
	public static String formatUTC(long millis, String pattern, Locale locale)
	{
		return format(new Date(millis), pattern, DateUtility.UTC_TIME_ZONE, locale);
	}

	/**
	 * <p>Format a date/time into a specific pattern using the UTC time zone.</p>
	 *
	 * @param date    the date to format
	 * @param pattern the pattern to use to format the date
	 * @param locale  the locale to use, may be <code>null</code>
	 * @return the formatted date
	 */
	public static String formatUTC(Date date, String pattern, Locale locale)
	{
		return format(date, pattern, DateUtility.UTC_TIME_ZONE, locale);
	}

	/**
	 * <p>Format a date/time into a specific pattern.</p>
	 *
	 * @param millis  the date to format expressed in milliseconds
	 * @param pattern the pattern to use to format the date
	 * @return the formatted date
	 */
	public static String format(long millis, String pattern)
	{
		return format(new Date(millis), pattern, null, null);
	}

	/**
	 * <p>Format a date/time into a specific pattern.</p>
	 *
	 * @param date    the date to format
	 * @param pattern the pattern to use to format the date
	 * @return the formatted date
	 */
	public static String format(Date date, String pattern)
	{
		return format(date, pattern, null, null);
	}

	/**
	 * <p>Format a date/time into a specific pattern in a time zone.</p>
	 *
	 * @param millis   the time expressed in milliseconds
	 * @param pattern  the pattern to use to format the date
	 * @param timeZone the time zone  to use, may be <code>null</code>
	 * @return the formatted date
	 */
	public static String format(long millis, String pattern, TimeZone timeZone)
	{
		return format(new Date(millis), pattern, timeZone, null);
	}

	/**
	 * <p>Format a date/time into a specific pattern in a time zone.</p>
	 *
	 * @param date     the date to format
	 * @param pattern  the pattern to use to format the date
	 * @param timeZone the time zone  to use, may be <code>null</code>
	 * @return the formatted date
	 */
	public static String format(Date date, String pattern, TimeZone timeZone)
	{
		return format(date, pattern, timeZone, null);
	}

	/**
	 * <p>Format a date/time into a specific pattern in a locale.</p>
	 *
	 * @param millis  the date to format expressed in milliseconds
	 * @param pattern the pattern to use to format the date
	 * @param locale  the locale to use, may be <code>null</code>
	 * @return the formatted date
	 */
	public static String format(long millis, String pattern, Locale locale)
	{
		return format(new Date(millis), pattern, null, locale);
	}

	/**
	 * <p>Format a date/time into a specific pattern in a locale.</p>
	 *
	 * @param date    the date to format
	 * @param pattern the pattern to use to format the date
	 * @param locale  the locale to use, may be <code>null</code>
	 * @return the formatted date
	 */
	public static String format(Date date, String pattern, Locale locale)
	{
		return format(date, pattern, null, locale);
	}

	/**
	 * <p>Format a date/time into a specific pattern in a time zone  and locale.</p>
	 *
	 * @param millis   the date to format expressed in milliseconds
	 * @param pattern  the pattern to use to format the date
	 * @param timeZone the time zone  to use, may be <code>null</code>
	 * @param locale   the locale to use, may be <code>null</code>
	 * @return the formatted date
	 */
	public static String format(long millis, String pattern, TimeZone timeZone, Locale locale)
	{
		return format(new Date(millis), pattern, timeZone, locale);
	}

	/**
	 * <p>Format a date/time into a specific pattern in a time zone  and locale.</p>
	 *
	 * @param date     the date to format
	 * @param pattern  the pattern to use to format the date
	 * @param timeZone the time zone  to use, may be <code>null</code>
	 * @param locale   the locale to use, may be <code>null</code>
	 * @return the formatted date
	 */
	public static String format(Date date, String pattern, TimeZone timeZone, Locale locale)
	{
		FastDateFormatUtility df = FastDateFormatUtility.getInstance(pattern, timeZone, locale);
		return df.format(date);
	}

}
