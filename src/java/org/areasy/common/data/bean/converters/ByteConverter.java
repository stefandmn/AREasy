package org.areasy.common.data.bean.converters;


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

import org.areasy.common.data.bean.ConversionException;
import org.areasy.common.data.bean.Converter;


/**
 * <p>Standard {@link Converter} implementation that converts an incoming
 * String into a <code>java.lang.Byte</code> object, optionally using a
 * default value or throwing a {@link ConversionException} if a conversion
 * error occurs.</p>
 *
 * @version $Id: ByteConverter.java,v 1.2 2008/05/14 09:32:38 swd\stefan.damian Exp $
 */

public final class ByteConverter implements Converter
{
	/**
	 * Create a {@link Converter} that will throw a {@link ConversionException}
	 * if a conversion error occurs.
	 */
	public ByteConverter()
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
	public ByteConverter(Object defaultValue)
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
	 * @throws ConversionException if conversion cannot be performed
	 *                             successfully
	 */
	public Object convert(Class type, Object value)
	{
		if (value == null)
		{
			if (useDefault) return (defaultValue);
				else throw new ConversionException("No value specified");
		}

		if (value instanceof Byte) return (value);
			else if (value instanceof Number) return new Byte(((Number) value).byteValue());

		try
		{
			return (new Byte(value.toString()));
		}
		catch (Exception e)
		{
			if (useDefault) return (defaultValue);
				else throw new ConversionException(e);
		}
	}
}
