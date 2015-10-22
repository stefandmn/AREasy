package org.areasy.common.parser.excel.biff.formula;

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

/**
 * An operand in the parse tree.  Operands represent leaf nodes in the
 * tree, and may not have children
 * Operands include numerical values, cell references and ranges
 */
abstract class Operand extends ParseItem
{
	/**
	 * Constructor
	 */
	public Operand()
	{
	}

	/**
	 * Default behaviour is to do nothing
	 *
	 * @param colAdjust the amount to add on to each relative cell reference
	 * @param rowAdjust the amount to add on to each relative row reference
	 */
	public void adjustRelativeCellReferences(int colAdjust, int rowAdjust)
	{
	}

	/**
	 * Default behaviour is to do nothing
	 *
	 * @param sheetIndex   the sheet on which the column was inserted
	 * @param col		  the column number which was inserted
	 * @param currentSheet TRUE if this formula is on the sheet in which the
	 *                     column was inserted, FALSE otherwise
	 */
	void columnInserted(int sheetIndex, int col, boolean currentSheet)
	{
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
	}

	/**
	 * Called when a column is inserted on the specified sheet.  Tells
	 * the formula  parser to update all of its cell references beyond this
	 * column
	 *
	 * @param sheetIndex   the sheet on which the row was inserted
	 * @param row		  the row number which was inserted
	 * @param currentSheet TRUE if this formula is on the sheet in which the
	 *                     column was inserted, FALSE otherwise
	 */
	void rowInserted(int sheetIndex, int row, boolean currentSheet)
	{
	}

	/**
	 * Called when a column is inserted on the specified sheet.  Tells
	 * the formula  parser to update all of its cell references beyond this
	 * column
	 *
	 * @param sheetIndex   the sheet on which the row was removed
	 * @param row		  the row number which was removed
	 * @param currentSheet TRUE if this formula is on the sheet in which the
	 *                     column was inserted, FALSE otherwise
	 */
	void rowRemoved(int sheetIndex, int row, boolean currentSheet)
	{
	}
}










