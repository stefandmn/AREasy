package org.areasy.common.parser.excel.write;

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

import org.areasy.common.parser.excel.BooleanCell;
import org.areasy.common.parser.excel.format.CellFormat;
import org.areasy.common.parser.excel.write.biff.BooleanRecord;

/**
 * A cell, created by user applications, which contains a boolean (or
 * in some cases an error) value
 */
public class Boolean extends BooleanRecord implements WritableCell, BooleanCell
{
	/**
	 * Constructs a boolean value, which, when added to a spreadsheet, will
	 * display the specified value at the column/row position indicated.
	 *
	 * @param c   the column
	 * @param r   the row
	 * @param val the value
	 */
	public Boolean(int c, int r, boolean val)
	{
		super(c, r, val);
	}

	/**
	 * Constructs a boolean, which, when added to a spreadsheet, will display the
	 * specified value at the column/row position with the specified CellFormat.
	 * The CellFormat may specify font information
	 *
	 * @param c   the column
	 * @param r   the row
	 * @param val the value
	 * @param st  the cell format
	 */
	public Boolean(int c, int r, boolean val, CellFormat st)
	{
		super(c, r, val, st);
	}

	/**
	 * Constructor used internally by the application when making a writable
	 * copy of a spreadsheet that has been read in
	 *
	 * @param nc the cell to copy
	 */
	public Boolean(BooleanCell nc)
	{
		super(nc);
	}

	/**
	 * Copy constructor used for deep copying
	 *
	 * @param col the column
	 * @param row the row
	 * @param b   the cell to copy
	 */
	protected Boolean(int col, int row, Boolean b)
	{
		super(col, row, b);
	}

	/**
	 * Sets the boolean value for this cell
	 *
	 * @param val the value
	 */
	public void setValue(boolean val)
	{
		super.setValue(val);
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
		return new Boolean(col, row, this);
	}
}
