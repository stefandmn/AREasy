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

import org.areasy.common.parser.excel.Cell;
import org.areasy.common.parser.excel.format.CellFormat;
import org.areasy.common.parser.excel.write.biff.BlankRecord;

/**
 * A blank cell.  Despite not having any contents, it may contain
 * formatting information.  Such cells are typically used when creating
 * templates
 */
public class Blank extends BlankRecord implements WritableCell
{
	/**
	 * Creates a cell which, when added to the sheet, will be presented at the
	 * specified column and row co-ordinates
	 *
	 * @param c the column
	 * @param r the row
	 */
	public Blank(int c, int r)
	{
		super(c, r);
	}

	/**
	 * Creates a cell which, when added to the sheet, will be presented at the
	 * specified column and row co-ordinates
	 * in the manner specified by the CellFormat parameter
	 *
	 * @param c  the column
	 * @param r  the row
	 * @param st the cell format
	 */
	public Blank(int c, int r, CellFormat st)
	{
		super(c, r, st);
	}

	/**
	 * Constructor used internally by the application when making a writable
	 * copy of a spreadsheet being read in
	 *
	 * @param lc the cell to copy
	 */
	public Blank(Cell lc)
	{
		super(lc);
	}


	/**
	 * Copy constructor used for deep copying
	 *
	 * @param col the column
	 * @param row the row
	 * @param b   the balnk cell to copy
	 */
	protected Blank(int col, int row, Blank b)
	{
		super(col, row, b);
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
		return new Blank(col, row, this);
	}
}

