package org.areasy.common.parser.excel.write.biff;

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

import org.areasy.common.parser.excel.biff.FormatRecord;

/**
 * A class which contains a date format
 */
public class DateFormatRecord extends FormatRecord
{
	/**
	 * Constructor
	 *
	 * @param fmt the date format
	 */
	protected DateFormatRecord(String fmt)
	{
		super();

		// Do the replacements in the format string
		String fs = fmt;

		fs = replace(fs, "a", "AM/PM");
		fs = replace(fs, "S", "0");

		setFormatString(fs);
	}
}



