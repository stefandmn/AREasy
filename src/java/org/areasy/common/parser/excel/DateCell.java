package org.areasy.common.parser.excel;

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

import java.text.DateFormat;
import java.util.Date;

/**
 * A date cell
 */
public interface DateCell extends Cell
{
	/**
	 * Gets the date contained in this cell
	 *
	 * @return the cell contents
	 */
	public Date getDate();

	/**
	 * Indicates whether the date value contained in this cell refers to a date,
	 * or merely a time
	 *
	 * @return TRUE if the value refers to a time
	 */
	public boolean isTime();

	/**
	 * Gets the DateFormat used to format the cell.  This will normally be
	 * the format specified in the excel spreadsheet, but in the event of any
	 * difficulty parsing this, it will revert to the default date/time format.
	 *
	 * @return the DateFormat object used to format the date in the original
	 *         excel cell
	 */
	public DateFormat getDateFormat();
}
