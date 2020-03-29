package org.areasy.common.data.bean.converters;


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

import org.areasy.common.data.bean.ConversionException;
import org.areasy.common.data.bean.Converter;


/**
 * <p>Standard {@link Converter} implementation that converts an incoming
 * String into a <code>java.lang.Boolean</code> object, optionally using a
 * default value or throwing a {@link ConversionException} if a conversion
 * error occurs.</p>
 *
 * @version $Id: BooleanConverter.java,v 1.2 2008/05/14 09:32:38 swd\stefan.damian Exp $
 */

public final class BooleanConverter implements Converter
{
	/**
	 * Create a {@link Converter} that will throw a {@link ConversionException}
	 * if a conversion error occurs.
	 */
	public BooleanConverter()
	{
		this.defaultValue = null;
		this.useDefault = false;
	}


	/**
	 * Create a {@link Converter} that will return the specified default value
	 * if a conversion error occurs.
	 *
	 * @param defaultValue The default value to be returned
	 */
	public BooleanConverter(Object defaultValue)
	{
		this.defaultValue = defaultValue;
		this.useDefault = true;
	}

	/**
	 * The default value specified to our Constructor, if any.
	 */
	private Object defaultValue = null;

	/**
	 * Should we return the default value on conversion errors?
	 */
	private boolean useDefault = true;


	/**
	 * Convert the specified input object into an output object of the
	 * specified type.
	 *
	 * @param type  Data type to which this value should be converted
	 * @param value The input value to be converted
	 * @throws ConversionException if conversion cannot be performed successfully
	 */
	public Object convert(Class type, Object value)
	{
		if (value == null)
		{
			if (useDefault) return (defaultValue);
				else throw new ConversionException("No value specified");
		}

		if (value instanceof Boolean) return (value);

		try
		{
			String stringValue = value.toString();
			if (stringValue.equalsIgnoreCase("yes") || stringValue.equalsIgnoreCase("y") ||
					stringValue.equalsIgnoreCase("true") || stringValue.equalsIgnoreCase("on") ||
					stringValue.equalsIgnoreCase("1")) return (Boolean.TRUE);
			else if (stringValue.equalsIgnoreCase("no") ||
					stringValue.equalsIgnoreCase("n") || stringValue.equalsIgnoreCase("false") || stringValue.equalsIgnoreCase("off") ||
					stringValue.equalsIgnoreCase("0")) return (Boolean.FALSE);
			else if (useDefault) return (defaultValue);
			else throw new ConversionException(stringValue);
		}
		catch (ClassCastException e)
		{
			if (useDefault) return (defaultValue);
				else throw new ConversionException(e);
		}
	}


}
