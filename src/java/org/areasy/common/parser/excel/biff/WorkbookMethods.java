package org.areasy.common.parser.excel.biff;

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

import org.areasy.common.parser.excel.Sheet;

/**
 * An interface containing some common workbook methods.  This so that
 * objects which are re-used for both readable and writable workbooks
 * can still make the same method calls on a workbook
 */
public interface WorkbookMethods
{
	/**
	 * Gets the specified sheet within this workbook
	 *
	 * @param index the zero based index of the required sheet
	 * @return The sheet specified by the index
	 */
	public Sheet getReadSheet(int index);

	/**
	 * Gets the name at the specified index
	 *
	 * @param index the index into the name table
	 * @return the name of the cell
	 * @throws NameRangeException
	 */
	public String getName(int index) throws NameRangeException;

	/**
	 * Gets the index of the name record for the name
	 *
	 * @param name the name
	 * @return the index in the name table
	 */
	public int getNameIndex(String name);
}
