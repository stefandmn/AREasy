package org.areasy.common.parser.excel;

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

import org.areasy.common.parser.excel.format.CellFormat;

/**
 * Represents an individual Cell within a Sheet.  May be queried for its
 * type and its content
 */
public interface Cell
{
	/**
	 * Returns the row number of this cell
	 *
	 * @return the row number of this cell
	 */
	public int getRow();

	/**
	 * Returns the column number of this cell
	 *
	 * @return the column number of this cell
	 */
	public int getColumn();

	/**
	 * Returns the content type of this cell
	 *
	 * @return the content type for this cell
	 */
	public CellType getType();

	/**
	 * Indicates whether or not this cell is hidden, by virtue of either
	 * the entire row or column being collapsed
	 *
	 * @return TRUE if this cell is hidden, FALSE otherwise
	 */
	public boolean isHidden();

	/**
	 * Quick and dirty function to return the contents of this cell as a string.
	 * For more complex manipulation of the contents, it is necessary to cast
	 * this interface to correct subinterface
	 *
	 * @return the contents of this cell as a string
	 */
	public String getContents();

	/**
	 * Gets the cell format which applies to this cell
	 * Note that for cell with a cell type of EMPTY, which has no formatting
	 * information, this method will return null.  Some empty cells (eg. on
	 * template spreadsheets) may have a cell type of EMPTY, but will
	 * actually contain formatting information
	 *
	 * @return the cell format applied to this cell, or NULL if this is an
	 *         empty cell
	 */
	public CellFormat getCellFormat();

	/**
	 * Gets any special cell features, such as comments (notes) or cell
	 * validation present for this cell
	 *
	 * @return the cell features, or NULL if this cell has no special features
	 */
	public CellFeatures getCellFeatures();
}
