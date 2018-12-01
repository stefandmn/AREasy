package org.areasy.common.parser.excel.read.biff;

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

import org.areasy.common.logger.Logger;
import org.areasy.common.logger.LoggerFactory;
import org.areasy.common.parser.excel.Cell;
import org.areasy.common.parser.excel.CellFeatures;
import org.areasy.common.parser.excel.CellType;
import org.areasy.common.parser.excel.biff.FormattingRecords;
import org.areasy.common.parser.excel.format.CellFormat;

/**
 * A blank cell value, initialized indirectly from a multiple biff record
 * rather than directly from the binary data
 */
class MulBlankCell implements Cell, CellFeaturesAccessor
{
	/**
	 * The logger
	 */
	private static Logger logger = LoggerFactory.getLog(MulBlankCell.class);

	/**
	 * The row containing this blank
	 */
	private int row;
	/**
	 * The column containing this blank
	 */
	private int column;
	/**
	 * The raw cell format
	 */
	private CellFormat cellFormat;

	/**
	 * The index to the XF Record
	 */
	private int xfIndex;

	/**
	 * A handle to the formatting records
	 */
	private FormattingRecords formattingRecords;

	/**
	 * A flag to indicate whether this object's formatting things have
	 * been initialized
	 */
	private boolean initialized;

	/**
	 * A handle to the sheet
	 */
	private DefaultSheet sheet;

	/**
	 * The cell features
	 */
	private CellFeatures features;


	/**
	 * Constructs this cell
	 *
	 * @param r   the zero based row
	 * @param c   the zero base column
	 * @param xfi the xf index
	 * @param fr  the formatting records
	 * @param si  the sheet
	 */
	public MulBlankCell(int r, int c,
						int xfi,
						FormattingRecords fr,
						DefaultSheet si)
	{
		row = r;
		column = c;
		xfIndex = xfi;
		formattingRecords = fr;
		sheet = si;
		initialized = false;
	}

	/**
	 * Accessor for the row
	 *
	 * @return the zero based row
	 */
	public final int getRow()
	{
		return row;
	}

	/**
	 * Accessor for the column
	 *
	 * @return the zero based column
	 */
	public final int getColumn()
	{
		return column;
	}

	/**
	 * Accessor for the contents as a string
	 *
	 * @return the value as a string
	 */
	public String getContents()
	{
		return "";
	}

	/**
	 * Accessor for the cell type
	 *
	 * @return the cell type
	 */
	public CellType getType()
	{
		return CellType.EMPTY;
	}

	/**
	 * Gets the cell format for this cell
	 *
	 * @return the cell format for these cells
	 */
	public CellFormat getCellFormat()
	{
		if (!initialized)
		{
			cellFormat = formattingRecords.getXFRecord(xfIndex);
			initialized = true;
		}

		return cellFormat;
	}

	/**
	 * Determines whether or not this cell has been hidden
	 *
	 * @return TRUE if this cell has been hidden, FALSE otherwise
	 */
	public boolean isHidden()
	{
		ColumnInfoRecord cir = sheet.getColumnInfo(column);

		if (cir != null && cir.getWidth() == 0)
		{
			return true;
		}

		RowRecord rr = sheet.getRowInfo(row);

		if (rr != null && (rr.getRowHeight() == 0 || rr.isCollapsed()))
		{
			return true;
		}

		return false;
	}

	/**
	 * Accessor for the cell features
	 *
	 * @return the cell features or NULL if this cell doesn't have any
	 */
	public CellFeatures getCellFeatures()
	{
		return features;
	}

	/**
	 * Sets the cell features during the reading process
	 *
	 * @param cf the cell features
	 */
	public void setCellFeatures(CellFeatures cf)
	{
		if (features != null)
		{
			logger.warn("current cell features not null - overwriting");
		}

		features = cf;
	}
}
