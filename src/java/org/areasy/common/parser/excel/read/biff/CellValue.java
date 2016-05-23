package org.areasy.common.parser.excel.read.biff;

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

import org.areasy.common.logger.Logger;
import org.areasy.common.logger.LoggerFactory;
import org.areasy.common.parser.excel.Cell;
import org.areasy.common.parser.excel.CellFeatures;
import org.areasy.common.parser.excel.biff.FormattingRecords;
import org.areasy.common.parser.excel.biff.IntegerHelper;
import org.areasy.common.parser.excel.biff.RecordData;
import org.areasy.common.parser.excel.biff.XFRecord;
import org.areasy.common.parser.excel.format.CellFormat;

/**
 * Abstract class for all records which actually contain cell values
 */
public abstract class CellValue extends RecordData
		implements Cell, CellFeaturesAccessor
{
	/**
	 * The logger
	 */
	private static Logger logger = LoggerFactory.getLog(CellValue.class);

	/**
	 * The row number of this cell record
	 */
	private int row;

	/**
	 * The column number of this cell record
	 */
	private int column;

	/**
	 * The XF index
	 */
	private int xfIndex;

	/**
	 * A handle to the formatting records, so that we can
	 * retrieve the formatting information
	 */
	private FormattingRecords formattingRecords;

	/**
	 * A lazy initialize flag for the cell format
	 */
	private boolean initialized;

	/**
	 * The cell format
	 */
	private XFRecord format;

	/**
	 * A handle back to the sheet
	 */
	private DefaultSheet sheet;

	/**
	 * The cell features
	 */
	private CellFeatures features;

	/**
	 * Constructs this object from the raw cell data
	 *
	 * @param t  the raw cell data
	 * @param fr the formatting records
	 * @param si the sheet containing this cell
	 */
	protected CellValue(Record t, FormattingRecords fr, DefaultSheet si)
	{
		super(t);
		byte[] data = getRecord().getData();
		row = IntegerHelper.getInt(data[0], data[1]);
		column = IntegerHelper.getInt(data[2], data[3]);
		xfIndex = IntegerHelper.getInt(data[4], data[5]);
		sheet = si;
		formattingRecords = fr;
		initialized = false;
	}

	/**
	 * Interface method which returns the row number of this cell
	 *
	 * @return the zero base row number
	 */
	public final int getRow()
	{
		return row;
	}

	/**
	 * Interface method which returns the column number of this cell
	 *
	 * @return the zero based column number
	 */
	public final int getColumn()
	{
		return column;
	}

	/**
	 * Gets the XFRecord corresponding to the index number.  Used when
	 * copying a spreadsheet
	 *
	 * @return the xf index for this cell
	 */
	public final int getXFIndex()
	{
		return xfIndex;
	}

	/**
	 * Gets the CellFormat object for this cell.  Used by the WritableWorkbook
	 * API
	 *
	 * @return the CellFormat used for this cell
	 */
	public CellFormat getCellFormat()
	{
		if (!initialized)
		{
			format = formattingRecords.getXFRecord(xfIndex);
			initialized = true;
		}

		return format;
	}

	/**
	 * Determines whether or not this cell has been hidden
	 *
	 * @return TRUE if this cell has been hidden, FALSE otherwise
	 */
	public boolean isHidden()
	{
		ColumnInfoRecord cir = sheet.getColumnInfo(column);

		if (cir != null && (cir.getWidth() == 0 || cir.getHidden()))
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
	 * Accessor for the sheet
	 *
	 * @return the sheet
	 */
	protected DefaultSheet getSheet()
	{
		return sheet;
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

