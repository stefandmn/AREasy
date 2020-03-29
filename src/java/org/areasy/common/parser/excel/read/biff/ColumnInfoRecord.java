package org.areasy.common.parser.excel.read.biff;

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

import org.areasy.common.parser.excel.biff.IntegerHelper;
import org.areasy.common.parser.excel.biff.RecordData;
import org.areasy.common.parser.excel.biff.Type;

/**
 * Contains the display info data which affects the entire columns
 */
public class ColumnInfoRecord extends RecordData
{
	/**
	 * The raw data
	 */
	private byte[] data;

	/**
	 * The start for which to apply the format information
	 */
	private int startColumn;

	/**
	 * The end column for which to apply the format information
	 */
	private int endColumn;

	/**
	 * The index to the XF record, which applies to each cell in this column
	 */
	private int xfIndex;

	/**
	 * The width of the column in 1/256 of a character
	 */
	private int width;

	/**
	 * A hidden flag
	 */
	private boolean hidden;

	/**
	 * The column's outline level
	 */
	private int outlineLevel;

	/**
	 * The column collapsed flag
	 */
	private boolean collapsed;

	/**
	 * Constructor which creates this object from the binary data
	 *
	 * @param t the record
	 */
	ColumnInfoRecord(Record t)
	{
		super(Type.COLINFO);

		data = t.getData();

		startColumn = IntegerHelper.getInt(data[0], data[1]);
		endColumn = IntegerHelper.getInt(data[2], data[3]);
		width = IntegerHelper.getInt(data[4], data[5]);
		xfIndex = IntegerHelper.getInt(data[6], data[7]);

		int options = IntegerHelper.getInt(data[8], data[9]);
		hidden = ((options & 0x1) != 0);
		outlineLevel = ((options & 0x700) >> 8);
		collapsed = ((options & 0x1000) != 0);
	}

	/**
	 * Accessor for the start column of this range
	 *
	 * @return the start column index
	 */
	public int getStartColumn()
	{
		return startColumn;
	}

	/**
	 * Accessor for the end column of this range
	 *
	 * @return the end column index
	 */
	public int getEndColumn()
	{
		return endColumn;
	}

	/**
	 * Accessor for the column format index
	 *
	 * @return the format index
	 */
	public int getXFIndex()
	{
		return xfIndex;
	}

	/**
	 * Accessor for the column's outline level
	 *
	 * @return the column's outline level
	 */
	public int getOutlineLevel()
	{
		return outlineLevel;
	}

	/**
	 * Accessor for whether the column is collapsed
	 *
	 * @return the column's collapsed state
	 */
	public boolean getCollapsed()
	{
		return collapsed;
	}

	/**
	 * Accessor for the width of the column
	 *
	 * @return the width
	 */
	public int getWidth()
	{
		return width;
	}

	/**
	 * Accessor for the hidden flag. Used when copying sheets
	 *
	 * @return TRUE if the columns are hidden, FALSE otherwise
	 */
	public boolean getHidden()
	{
		return hidden;
	}
}


