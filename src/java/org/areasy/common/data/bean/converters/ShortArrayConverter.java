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

import java.util.List;


/**
 * <p>Standard {@link Converter} implementation that converts an incoming
 * String into a primitive array of short.  On a conversion failure, returns
 * a specified default value or throws a {@link ConversionException} depending
 * on how this instance is constructed.</p>
 *
 * @version $Id: ShortArrayConverter.java,v 1.2 2008/05/14 09:32:38 swd\stefan.damian Exp $
 */

public final class ShortArrayConverter extends AbstractArrayConverter
{
	/**
	 * Create a {@link Converter} that will throw a {@link ConversionException}
	 * if a conversion error occurs.
	 */
	public ShortArrayConverter()
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
	public ShortArrayConverter(Object defaultValue)
	{
		this.defaultValue = defaultValue;
		this.useDefault = true;
	}

	/**
	 * <p>Model object for type comparisons.</p>
	 */
	private static short model[] = new short[0];

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
		// Deal with a null value
		if (value == null)
		{
			if (useDefault) return (defaultValue);
				else throw new ConversionException("No value specified");
		}

		// Deal with the no-conversion-needed case
		if (model.getClass() == value.getClass()) return (value);

		// Deal with input value as a String array
		if (strings.getClass() == value.getClass())
		{
			try
			{
				String values[] = (String[]) value;
				short results[] = new short[values.length];

				for (int i = 0; i < values.length; i++)
				{
					results[i] = Short.parseShort(values[i]);
				}

				return (results);
			}
			catch (Exception e)
			{
				if (useDefault) return (defaultValue);
					else throw new ConversionException(value.toString(), e);
			}
		}

		// Parse the input value as a String into elements
		// and convert to the appropriate type
		try
		{
			List list = parseElements(value.toString());
			short results[] = new short[list.size()];

			for (int i = 0; i < results.length; i++)
			{
				results[i] = Short.parseShort((String) list.get(i));
			}

			return (results);
		}
		catch (Exception e)
		{
			if (useDefault) return (defaultValue);
				else throw new ConversionException(value.toString(), e);
		}
	}
}
