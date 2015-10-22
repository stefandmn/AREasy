package org.areasy.common.parser.excel.read.biff;

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

import org.areasy.common.parser.excel.CellType;
import org.areasy.common.parser.excel.biff.FormattingRecords;

/**
 * A blank cell.  Despite the fact that this cell has no contents, it
 * has formatting information applied to it
 */
public class BlankCell extends CellValue
{
	/**
	 * Constructs this object from the raw data
	 *
	 * @param t  the raw data
	 * @param fr the available formats
	 * @param si the sheet
	 */
	BlankCell(Record t, FormattingRecords fr, DefaultSheet si)
	{
		super(t, fr, si);
	}

	/**
	 * Returns the contents of this cell as an empty string
	 *
	 * @return the value formatted into a string
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
}



