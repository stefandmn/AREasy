package org.areasy.common.parser.excel.biff.formula;

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

import org.areasy.common.logger.Logger;
import org.areasy.common.logger.LoggerFactory;
import org.areasy.common.parser.excel.Cell;
import org.areasy.common.parser.excel.biff.CellReferenceHelper;
import org.areasy.common.parser.excel.biff.IntegerHelper;

/**
 * A 3d cell reference in a formula
 */
class CellReference3d extends Operand implements ParsedThing
{
	/**
	 * The logger
	 */
	private static Logger logger = LoggerFactory.getLog(CellReference3d.class);

	/**
	 * Indicates whether the column reference is relative or absolute
	 */
	private boolean columnRelative;

	/**
	 * Indicates whether the row reference is relative or absolute
	 */
	private boolean rowRelative;

	/**
	 * The column reference
	 */
	private int column;

	/**
	 * The row reference
	 */
	private int row;

	/**
	 * The cell containing the formula.  Stored in order to determine
	 * relative cell values
	 */
	private Cell relativeTo;

	/**
	 * The sheet which the reference is present on
	 */
	private int sheet;

	/**
	 * A handle to the container of the external sheets ie. the workbook
	 */
	private ExternalSheet workbook;

	/**
	 * Constructor
	 *
	 * @param rt the cell containing the formula
	 * @param w  the list of external sheets
	 */
	public CellReference3d(Cell rt, ExternalSheet w)
	{
		relativeTo = rt;
		workbook = w;
	}

	/**
	 * Constructs this object from a string
	 *
	 * @param s the string
	 * @param w the external sheet
	 * @throws FormulaException
	 */
	public CellReference3d(String s, ExternalSheet w) throws FormulaException
	{
		workbook = w;
		columnRelative = true;
		rowRelative = true;

		// Get the cell details
		int sep = s.indexOf('!');
		String cellString = s.substring(sep + 1);
		column = CellReferenceHelper.getColumn(cellString);
		row = CellReferenceHelper.getRow(cellString);

		// Get the sheet index
		String sheetName = s.substring(0, sep);

		// Remove single quotes, if they exist
		if (sheetName.charAt(0) == '\'' &&
				sheetName.charAt(sheetName.length() - 1) == '\'')
		{
			sheetName = sheetName.substring(1, sheetName.length() - 1);
		}
		sheet = w.getExternalSheetIndex(sheetName);

		if (sheet < 0)
		{
			throw new FormulaException(FormulaException.SHEET_REF_NOT_FOUND,
					sheetName);
		}
	}

	/**
	 * Reads the ptg data from the array starting at the specified position
	 *
	 * @param data the RPN array
	 * @param pos  the current position in the array, excluding the ptg identifier
	 * @return the number of bytes read
	 */
	public int read(byte[] data, int pos)
	{
		sheet = IntegerHelper.getInt(data[pos], data[pos + 1]);
		row = IntegerHelper.getInt(data[pos + 2], data[pos + 3]);
		int columnMask = IntegerHelper.getInt(data[pos + 4], data[pos + 5]);
		column = columnMask & 0x00ff;
		columnRelative = ((columnMask & 0x4000) != 0);
		rowRelative = ((columnMask & 0x8000) != 0);

		return 6;
	}

	/**
	 * Accessor for the column
	 *
	 * @return the column number
	 */
	public int getColumn()
	{
		return column;
	}

	/**
	 * Accessor for the row
	 *
	 * @return the row number
	 */
	public int getRow()
	{
		return row;
	}

	/**
	 * Gets the string version of this cell reference
	 *
	 * @param buf the buffer to append to
	 */
	public void getString(StringBuffer buf)
	{
		CellReferenceHelper.getCellReference(sheet, column, !columnRelative,
				row, !rowRelative,
				workbook, buf);
	}

	/**
	 * Gets the token representation of this item in RPN
	 *
	 * @return the bytes applicable to this formula
	 */
	byte[] getBytes()
	{
		byte[] data = new byte[7];
		data[0] = Token.REF3D.getCode();

		IntegerHelper.getTwoBytes(sheet, data, 1);
		IntegerHelper.getTwoBytes(row, data, 3);

		int grcol = column;

		// Set the row/column relative bits if applicable
		if (rowRelative)
		{
			grcol |= 0x8000;
		}

		if (columnRelative)
		{
			grcol |= 0x4000;
		}

		IntegerHelper.getTwoBytes(grcol, data, 5);

		return data;
	}

	/**
	 * Adjusts all the relative cell references in this formula by the
	 * amount specified.  Used when copying formulas
	 *
	 * @param colAdjust the amount to add on to each relative cell reference
	 * @param rowAdjust the amount to add on to each relative row reference
	 */
	public void adjustRelativeCellReferences(int colAdjust, int rowAdjust)
	{
		if (columnRelative)
		{
			column += colAdjust;
		}

		if (rowRelative)
		{
			row += rowAdjust;
		}
	}

	/**
	 * Called when a column is inserted on the specified sheet.  Tells
	 * the formula  parser to update all of its cell references beyond this
	 * column
	 *
	 * @param sheetIndex   the sheet on which the column was inserted
	 * @param col		  the column number which was inserted
	 * @param currentSheet TRUE if this formula is on the sheet in which the
	 *                     column was inserted, FALSE otherwise
	 */
	public void columnInserted(int sheetIndex, int col, boolean currentSheet)
	{
		if (sheetIndex != sheet)
		{
			return;
		}

		if (column >= col)
		{
			column++;
		}
	}


	/**
	 * Called when a column is inserted on the specified sheet.  Tells
	 * the formula  parser to update all of its cell references beyond this
	 * column
	 *
	 * @param sheetIndex   the sheet on which the column was removed
	 * @param col		  the column number which was removed
	 * @param currentSheet TRUE if this formula is on the sheet in which the
	 *                     column was inserted, FALSE otherwise
	 */
	void columnRemoved(int sheetIndex, int col, boolean currentSheet)
	{
		if (sheetIndex != sheet)
		{
			return;
		}

		if (column >= col)
		{
			column--;
		}
	}

	/**
	 * Called when a column is inserted on the specified sheet.  Tells
	 * the formula  parser to update all of its cell references beyond this
	 * column
	 *
	 * @param sheetIndex   the sheet on which the row was inserted
	 * @param r			the row number which was inserted
	 * @param currentSheet TRUE if this formula is on the sheet in which the
	 *                     column was inserted, FALSE otherwise
	 */
	void rowInserted(int sheetIndex, int r, boolean currentSheet)
	{
		if (sheetIndex != sheet)
		{
			return;
		}

		if (row >= r)
		{
			row++;
		}
	}

	/**
	 * Called when a column is inserted on the specified sheet.  Tells
	 * the formula  parser to update all of its cell references beyond this
	 * column
	 *
	 * @param sheetIndex   the sheet on which the row was removed
	 * @param r			the row number which was removed
	 * @param currentSheet TRUE if this formula is on the sheet in which the
	 *                     column was inserted, FALSE otherwise
	 */
	void rowRemoved(int sheetIndex, int r, boolean currentSheet)
	{
		if (sheetIndex != sheet)
		{
			return;
		}

		if (row >= r)
		{
			row--;
		}
	}

	/**
	 * If this formula was on an imported sheet, check that
	 * cell references to another sheet are warned appropriately
	 * Flags the formula as invalid
	 */
	void handleImportedCellReferences()
	{
		setInvalid();
	}

}
