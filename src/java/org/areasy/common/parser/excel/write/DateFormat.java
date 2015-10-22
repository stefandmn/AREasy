package org.areasy.common.parser.excel.write;

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

import org.areasy.common.parser.excel.biff.DisplayFormat;
import org.areasy.common.parser.excel.write.biff.DateFormatRecord;

import java.text.SimpleDateFormat;

/**
 * A custom user defined number format which may be instantiated within user
 * applications in order to present date and time values in the  appropriate
 * format.
 * The string format used to create a DateFormat adheres to the standard
 * java specification, and JExcelApi makes the necessary modifications so
 * that it is rendered as its nearest equivalent in Excel.
 * Once created, this may be used within a CellFormat object, which in turn
 * is a parameter passed to the constructor of the DateTime cell
 */
public class DateFormat extends DateFormatRecord implements DisplayFormat
{
	/**
	 * Constructor. The date format that is passed should comply to the standard
	 * Java date formatting conventions
	 *
	 * @param format the date format
	 */
	public DateFormat(String format)
	{
		super(format);

		// Verify that the format is valid
		SimpleDateFormat df = new SimpleDateFormat(format);
	}
}
