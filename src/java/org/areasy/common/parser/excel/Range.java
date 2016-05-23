package org.areasy.common.parser.excel;

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

/**
 * Represents a 3-D range of cells in a workbook.  This object is
 * returned by the method findByName in a workbook
 */
public interface Range
{
	/**
	 * Gets the cell at the top left of this range
	 *
	 * @return the cell at the top left
	 */
	public Cell getTopLeft();

	/**
	 * Gets the cell at the bottom right of this range
	 *
	 * @return the cell at the bottom right
	 */
	public Cell getBottomRight();

	/**
	 * Gets the index of the first sheet in the range
	 *
	 * @return the index of the first sheet in the range
	 */
	public int getFirstSheetIndex();

	/**
	 * Gets the index of the last sheet in the range
	 *
	 * @return the index of the last sheet in the range
	 */
	public int getLastSheetIndex();
}



