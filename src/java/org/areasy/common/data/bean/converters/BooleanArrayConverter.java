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

import java.util.List;


/**
 * <p>Standard {@link Converter} implementation that converts an incoming
 * String into a primitive array of boolean.  On a conversion failure, returns
 * a specified default value or throws a {@link ConversionException} depending
 * on how this instance is constructed.</p>
 *
 * @version $Id: BooleanArrayConverter.java,v 1.2 2008/05/14 09:32:37 swd\stefan.damian Exp $
 */

public final class BooleanArrayConverter extends AbstractArrayConverter
{
	/**
	 * Create a {@link Converter} that will throw a {@link ConversionException}
	 * if a conversion error occurs.
	 */
	public BooleanArrayConverter()
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
	public BooleanArrayConverter(Object defaultValue)
	{
		this.defaultValue = defaultValue;
		this.useDefault = true;
	}

	/**
	 * <p>Model object for type comparisons.</p>
	 */
	private static boolean model[] = new boolean[0];

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
				boolean results[] = new boolean[values.length];
				for (int i = 0; i < values.length; i++)
				{
					String stringValue = values[i];
					if (stringValue.equalsIgnoreCase("yes") || stringValue.equalsIgnoreCase("y") || stringValue.equalsIgnoreCase("true") ||
							stringValue.equalsIgnoreCase("on") || stringValue.equalsIgnoreCase("1")) results[i] = true;
					else if (stringValue.equalsIgnoreCase("no") || stringValue.equalsIgnoreCase("n") ||
							stringValue.equalsIgnoreCase("false") || stringValue.equalsIgnoreCase("off") || stringValue.equalsIgnoreCase("0")) results[i] = false;
					else
					{
						if (useDefault) return (defaultValue);
							else throw new ConversionException(value.toString());
					}
				}
				return (results);
			}
			catch (Exception e)
			{
				if (useDefault) return (defaultValue);
					else throw new ConversionException(value.toString(), e);
			}
		}

		try
		{
			List list = parseElements(value.toString());
			boolean results[] = new boolean[list.size()];

			for (int i = 0; i < results.length; i++)
			{
				String stringValue = (String) list.get(i);
				if (stringValue.equalsIgnoreCase("yes") || stringValue.equalsIgnoreCase("y") || stringValue.equalsIgnoreCase("true") || stringValue.equalsIgnoreCase("on") ||
						stringValue.equalsIgnoreCase("1")) results[i] = true;
				else if (stringValue.equalsIgnoreCase("no") || stringValue.equalsIgnoreCase("n") ||
						stringValue.equalsIgnoreCase("false") || stringValue.equalsIgnoreCase("off") || stringValue.equalsIgnoreCase("0")) results[i] = false;
				else
				{
					if (useDefault) return (defaultValue);
						else throw new ConversionException(value.toString());
				}
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
