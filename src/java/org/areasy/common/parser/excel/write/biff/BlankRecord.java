package org.areasy.common.parser.excel.write.biff;

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

import org.areasy.common.logger.Logger;
import org.areasy.common.logger.LoggerFactory;
import org.areasy.common.parser.excel.Cell;
import org.areasy.common.parser.excel.CellType;
import org.areasy.common.parser.excel.biff.Type;
import org.areasy.common.parser.excel.format.CellFormat;

/**
 * A blank record, which is used to contain formatting information
 */
public abstract class BlankRecord extends CellValue
{
	/**
	 * The logger
	 */
	private static Logger logger = LoggerFactory.getLog(BlankRecord.class);

	/**
	 * Consructor used when creating a label from the user API
	 *
	 * @param c	the column
	 * @param cont the contents
	 * @param r	the row
	 */
	protected BlankRecord(int c, int r)
	{
		super(Type.BLANK, c, r);
	}

	/**
	 * Constructor used when creating a label from the API.  This is
	 * overloaded to allow formatting information to be passed to the record
	 *
	 * @param c  the column
	 * @param r  the row
	 * @param st the format applied to the cell
	 */
	protected BlankRecord(int c, int r, CellFormat st)
	{
		super(Type.BLANK, c, r, st);
	}

	/**
	 * Constructor used when copying a formatted blank cell from a read only
	 * spreadsheet
	 *
	 * @param c the blank cell to copy
	 */
	protected BlankRecord(Cell c)
	{
		super(Type.BLANK, c);
	}

	/**
	 * Copy constructor
	 *
	 * @param c the column
	 * @param r the row
	 * @param b the record to  copy
	 */
	protected BlankRecord(int c, int r, BlankRecord br)
	{
		super(Type.BLANK, c, r, br);
	}

	/**
	 * Returns the content type of this cell
	 *
	 * @return the content type for this cell
	 */
	public CellType getType()
	{
		return CellType.EMPTY;
	}

	/**
	 * Quick and dirty function to return the contents of this cell as a string.
	 *
	 * @return the contents of this cell as a string
	 */
	public String getContents()
	{
		return "";
	}
}



