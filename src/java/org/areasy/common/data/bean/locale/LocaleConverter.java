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

import org.areasy.common.data.bean.Converter;


/**
 * <p>General purpose locale-sensitive data type converter that can be registered and used
 * within the BeanUtils package to manage the conversion of objects from
 * one type to another.
 *
 * @version $Id: LocaleConverter.java,v 1.2 2008/05/14 09:32:41 swd\stefan.damian Exp $
 */

public interface LocaleConverter extends Converter
{


	/**
	 * Convert the specified locale-sensitive input object into an output object of the
	 * specified type.
	 *
	 * @param type    Data type to which this value should be converted
	 * @param value   The input value to be converted
	 * @param pattern The user-defined pattern is used for the input object formatting.
	 * @throws org.areasy.common.data.bean.ConversionException if conversion cannot be performed
	 *                             successfully
	 */
	public Object convert(Class type, Object value, String pattern);
}
