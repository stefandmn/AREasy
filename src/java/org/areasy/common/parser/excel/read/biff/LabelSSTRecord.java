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

import org.areasy.common.parser.excel.CellType;
import org.areasy.common.parser.excel.LabelCell;
import org.areasy.common.parser.excel.biff.FormattingRecords;
import org.areasy.common.parser.excel.biff.IntegerHelper;

/**
 * A label which is stored in the shared string table
 */
class LabelSSTRecord extends CellValue implements LabelCell
{
	/**
	 * The index into the shared string table
	 */
	private int index;
	/**
	 * The label
	 */
	private String string;

	/**
	 * Constructor.  Retrieves the index from the raw data and looks it up
	 * in the shared string table
	 *
	 * @param stringTable the shared string table
	 * @param t		   the raw data
	 * @param fr		  the formatting records
	 * @param si		  the sheet
	 */
	public LabelSSTRecord(Record t, SSTRecord stringTable, FormattingRecords fr,
						  DefaultSheet si)
	{
		super(t, fr, si);
		byte[] data = getRecord().getData();
		index = IntegerHelper.getInt(data[6], data[7], data[8], data[9]);
		string = stringTable.getString(index);
	}

	/**
	 * Gets the label
	 *
	 * @return the label
	 */
	public String getString()
	{
		return string;
	}

	/**
	 * Gets this cell's contents as a string
	 *
	 * @return the label
	 */
	public String getContents()
	{
		return string;
	}

	/**
	 * Returns the cell type
	 *
	 * @return the cell type
	 */
	public CellType getType()
	{
		return CellType.LABEL;
	}
}
