package org.areasy.common.parser.excel.write;

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

import org.areasy.common.parser.excel.DateCell;
import org.areasy.common.parser.excel.format.CellFormat;
import org.areasy.common.parser.excel.write.biff.DateRecord;

import java.util.Date;

/**
 * A Date which may be created on the fly by a user application and added to a
 * spreadsheet
 * <p/>
 * NOTE:  By default, all dates will have local timezone information added to
 * their UTC value.  If this is not desired (eg. if the date entered
 * represents an interval eg. 9.83s for the 100m world record, then use
 * the overloaded constructor which indicate that the date passed in was
 * created under the GMT timezone.  It is important that when the date
 * was created, an instruction like
 * Calendar.setTimeZone(TimeZone.getTimeZone("GMT"))
 * was made prior to that
 */
public class DateTime extends DateRecord implements WritableCell, DateCell
{
	/**
	 * Instance variable for dummy variable overload
	 */
	public static final GMTDate GMT = new GMTDate();

	/**
	 * Constructor.  The date will be displayed with date and time components
	 * using the default date format
	 *
	 * @param c the column
	 * @param r the row
	 * @param d the date
	 */
	public DateTime(int c, int r, Date d)
	{
		super(c, r, d);
	}

	/**
	 * Constructor, which adjusts the specified date to take timezone
	 * considerations into account.  The date passed in will be displayed with
	 * date and time components using the default date format
	 *
	 * @param c the column
	 * @param r the row
	 * @param d the date
	 * @param a dummy overload
	 */
	public DateTime(int c, int r, Date d, GMTDate a)
	{
		super(c, r, d, a);
	}

	/**
	 * Constructor which takes the format for this cell
	 *
	 * @param c  the column
	 * @param r  the row
	 * @param st the format
	 * @param d  the date
	 */
	public DateTime(int c, int r, Date d, CellFormat st)
	{
		super(c, r, d, st);
	}

	/**
	 * Constructor, which adjusts the specified date to take timezone
	 * considerations into account
	 *
	 * @param c  the column
	 * @param r  the row
	 * @param d  the date
	 * @param st the cell format
	 * @param a  the cummy overload
	 */
	public DateTime(int c, int r, Date d, CellFormat st, GMTDate a)
	{
		super(c, r, d, st, a);
	}

	/**
	 * Constructor which takes the format for the cell and an indicator
	 * as to whether this cell is a full date time or purely just a time
	 * eg. if the spreadsheet is to contain the world record for 100m, then the
	 * value would be 9.83s, which would be indicated as just a time
	 *
	 * @param c   the column
	 * @param r   the row
	 * @param st  the style
	 * @param tim flag indicating that this represents a time
	 * @param d   the date
	 */
	public DateTime(int c, int r, Date d, CellFormat st, boolean tim)
	{
		super(c, r, d, st, tim);
	}

	/**
	 * A constructor called by the process when creating a writable version
	 * of a spreadsheet that has been read in
	 *
	 * @param dc the date to copy
	 */
	public DateTime(DateCell dc)
	{
		super(dc);
	}

	/**
	 * Copy constructor used for deep copying
	 *
	 * @param col the column
	 * @param row the row
	 * @param dt  the date to copy
	 */
	protected DateTime(int col, int row, DateTime dt)
	{
		super(col, row, dt);
	}


	/**
	 * Sets the date for this cell
	 *
	 * @param d the date
	 */
	public void setDate(Date d)
	{
		super.setDate(d);
	}

	/**
	 * Sets the date for this cell, performing the necessary timezone adjustments
	 *
	 * @param d the date
	 * @param a the dummy overload
	 */
	public void setDate(Date d, GMTDate a)
	{
		super.setDate(d, a);
	}

	/**
	 * Implementation of the deep copy function
	 *
	 * @param col the column which the new cell will occupy
	 * @param row the row which the new cell will occupy
	 * @return a copy of this cell, which can then be added to the sheet
	 */
	public WritableCell copyTo(int col, int row)
	{
		return new DateTime(col, row, this);
	}
}


