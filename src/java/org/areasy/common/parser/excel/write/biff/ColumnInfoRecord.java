package org.areasy.common.parser.excel.write.biff;


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

import org.areasy.common.parser.excel.biff.*;

/**
 * Describes the column formatting for a particular column
 */
class ColumnInfoRecord extends WritableRecordData
{
	/**
	 * The binary data
	 */
	private byte[] data;
	/**
	 * The column number which this format applies to
	 */
	private int column;
	/**
	 * The style for the column
	 */
	private XFRecord style;
	/**
	 * The index for the style of this column
	 */
	private int xfIndex;

	/**
	 * The width of the column in 1/256 of a character
	 */
	private int width;

	/**
	 * Flag to indicate the hidden status of this column
	 */
	private boolean hidden;

	/**
	 * The column's outline level
	 */
	private int outlineLevel;

	/**
	 * Column collapsed flag
	 */
	private boolean collapsed;


	/**
	 * Constructor used when setting column information from the user
	 * API
	 *
	 * @param w   the width of the column in characters
	 * @param col the column to format
	 * @param xf  the style for the column
	 */
	public ColumnInfoRecord(int col, int w, XFRecord xf)
	{
		super(Type.COLINFO);

		column = col;
		width = w;
		style = xf;
		xfIndex = style.getXFIndex();
		hidden = false;
	}

	/**
	 * Copy constructor used when copying from sheet to sheet within the
	 * same workbook
	 *
	 * @param the record to copy
	 */
	public ColumnInfoRecord(ColumnInfoRecord cir)
	{
		super(Type.COLINFO);

		column = cir.column;
		width = cir.width;
		style = cir.style;
		xfIndex = cir.xfIndex;
		hidden = cir.hidden;
		outlineLevel = cir.outlineLevel;
		collapsed = cir.collapsed;

	}


	/**
	 * Constructor used when copying an existing spreadsheet
	 *
	 * @param col the column number
	 * @param cir the column info record read in
	 * @param fr  the format records
	 */
	public ColumnInfoRecord(org.areasy.common.parser.excel.read.biff.ColumnInfoRecord cir,
							int col,
							FormattingRecords fr)
	{
		super(Type.COLINFO);

		column = col;
		width = cir.getWidth();
		xfIndex = cir.getXFIndex();
		style = fr.getXFRecord(xfIndex);
		outlineLevel = cir.getOutlineLevel();
		collapsed = cir.getCollapsed();
	}

	/**
	 * Constructor used when importing a sheet from another
	 * spreadsheet
	 *
	 * @param col the column number
	 * @param cir the column info record read in
	 */
	public ColumnInfoRecord(org.areasy.common.parser.excel.read.biff.ColumnInfoRecord cir,
							int col)
	{
		super(Type.COLINFO);

		column = col;
		width = cir.getWidth();
		xfIndex = cir.getXFIndex();
		outlineLevel = cir.getOutlineLevel();
		collapsed = cir.getCollapsed();
	}

	/**
	 * Gets the column this format applies to
	 *
	 * @return the column which is formatted
	 */
	public int getColumn()
	{
		return column;
	}

	/**
	 * Increments the column.  Called when inserting a new column into
	 * the sheet
	 */
	public void incrementColumn()
	{
		column++;
	}

	/**
	 * Decrements the column.  Called when removing  a  column from
	 * the sheet
	 */
	public void decrementColumn()
	{
		column--;
	}

	/**
	 * Accessor for the width
	 *
	 * @return the width
	 */
	int getWidth()
	{
		return width;
	}

	/**
	 * Sets the width.  Used when autosizing columns
	 *
	 * @param w the new width
	 */
	void setWidth(int w)
	{
		width = w;
	}

	/**
	 * Gets the binary data to be written to the output file
	 *
	 * @return the data to write to file
	 */
	public byte[] getData()
	{
		data = new byte[0x0c];

		IntegerHelper.getTwoBytes(column, data, 0);
		IntegerHelper.getTwoBytes(column, data, 2);
		IntegerHelper.getTwoBytes(width, data, 4);
		IntegerHelper.getTwoBytes(xfIndex, data, 6);

		//    int options = 0x2;
		int options = 0x6 | (outlineLevel << 8);
		if (hidden)
		{
			options |= 0x1;
		}

		outlineLevel = ((options & 0x700) / 0x100);

		if (collapsed)
		{
			options |= 0x1000;
		}

		IntegerHelper.getTwoBytes(options, data, 8);
		//    IntegerHelper.getTwoBytes(2, data, 10);

		return data;
	}

	/**
	 * Gets the cell format associated with this column info record
	 *
	 * @return the cell format for this column
	 */
	public XFRecord getCellFormat()
	{
		return style;
	}

	/**
	 * Sets the cell format.  Used when importing spreadsheets
	 *
	 * @param xfr the xf record
	 */
	public void setCellFormat(XFRecord xfr)
	{
		style = xfr;
	}

	/**
	 * Accessor for the xf index, used when importing from another spreadsheet
	 *
	 * @return the xf index
	 */
	public int getXfIndex()
	{
		return xfIndex;
	}

	/**
	 * Rationalizes the sheets xf index mapping
	 *
	 * @param xfmapping the index mapping
	 */
	void rationalize(IndexMapping xfmapping)
	{
		xfIndex = xfmapping.getNewIndex(xfIndex);
	}

	/**
	 * Sets this column to be hidden (or otherwise)
	 *
	 * @param h TRUE if the column is to be hidden, FALSE otherwise
	 */
	void setHidden(boolean h)
	{
		hidden = h;
	}

	/**
	 * Accessor for the hidden flag
	 *
	 * @return TRUE if this column is hidden, FALSE otherwise
	 */
	boolean getHidden()
	{
		return hidden;
	}

	/**
	 * Standard equals method
	 *
	 * @return TRUE if these objects are equal, FALSE otherwise
	 */
	public boolean equals(Object o)
	{
		if (o == this)
		{
			return true;
		}

		if (!(o instanceof ColumnInfoRecord))
		{
			return false;
		}

		ColumnInfoRecord cir = (ColumnInfoRecord) o;

		if (column != cir.column ||
				xfIndex != cir.xfIndex ||
				width != cir.width ||
				hidden != cir.hidden ||
				outlineLevel != cir.outlineLevel ||
				collapsed != cir.collapsed)
		{
			return false;
		}

		if ((style == null && cir.style != null) ||
				(style != null && cir.style == null))
		{
			return false;
		}

		return style.equals(cir.style);
	}

	/**
	 * Standard hashCode method
	 *
	 * @return the hashCode
	 */
	public int hashCode()
	{
		int hashValue = 137;
		int oddPrimeNumber = 79;

		hashValue = hashValue * oddPrimeNumber + column;
		hashValue = hashValue * oddPrimeNumber + xfIndex;
		hashValue = hashValue * oddPrimeNumber + width;
		hashValue = hashValue * oddPrimeNumber + (hidden ? 1 : 0);

		if (style != null)
		{
			hashValue ^= style.hashCode();
		}

		return hashValue;
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
	 * Increments the column's outline level.  This is how groups are made
	 * as well
	 */
	public void incrementOutlineLevel()
	{
		outlineLevel++;
	}

	/**
	 * Decrements the column's outline level.  This removes it from a
	 * grouping level.  If
	 * all outline levels are gone the uncollapse the column.
	 */
	public void decrementOutlineLevel()
	{
		if (0 < outlineLevel)
		{
			outlineLevel--;
		}

		if (0 == outlineLevel)
		{
			collapsed = false;
		}
	}

	/**
	 * Sets the column's outline level
	 *
	 * @param level the column's outline level
	 */
	public void setOutlineLevel(int level)
	{
		outlineLevel = level;
	}

	/**
	 * Sets the column's collapsed state
	 *
	 * @param value the column's collapsed state
	 */
	public void setCollapsed(boolean value)
	{
		collapsed = value;
	}

}
