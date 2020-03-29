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
import org.areasy.common.parser.excel.WorkbookSettings;
import org.areasy.common.parser.excel.biff.FormattingRecords;
import org.areasy.common.parser.excel.biff.IntegerHelper;
import org.areasy.common.parser.excel.biff.StringHelper;

/**
 * A label which is stored in the cell
 */
class LabelRecord extends CellValue implements LabelCell
{
	/**
	 * The length of the label in characters
	 */
	private int length;

	/**
	 * The label
	 */
	private String string;

	/**
	 * Dummy indicators for overloading the constructor
	 */
	private static class Biff7
	{
	}

	;
	public static Biff7 biff7 = new Biff7();

	/**
	 * Constructs this object from the raw data
	 *
	 * @param t  the raw data
	 * @param fr the formatting records
	 * @param si the sheet
	 * @param ws the workbook settings
	 */
	public LabelRecord(Record t, FormattingRecords fr,
					   DefaultSheet si, WorkbookSettings ws)
	{
		super(t, fr, si);
		byte[] data = getRecord().getData();
		length = IntegerHelper.getInt(data[6], data[7]);

		if (data[8] == 0x0)
		{
			string = StringHelper.getString(data, length, 9, ws);
		}
		else
		{
			string = StringHelper.getUnicodeString(data, length, 9);
		}
	}

	/**
	 * Constructs this object from the raw data
	 *
	 * @param t	 the raw data
	 * @param fr	the formatting records
	 * @param si	the sheet
	 * @param ws	the workbook settings
	 * @param dummy dummy overload to indicate a biff 7 workbook
	 */
	public LabelRecord(Record t, FormattingRecords fr, DefaultSheet si,
					   WorkbookSettings ws, Biff7 dummy)
	{
		super(t, fr, si);
		byte[] data = getRecord().getData();
		length = IntegerHelper.getInt(data[6], data[7]);

		string = StringHelper.getString(data, length, 8, ws);
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
	 * Gets the cell contents as a string
	 *
	 * @return the label
	 */
	public String getContents()
	{
		return string;
	}

	/**
	 * Accessor for the cell type
	 *
	 * @return the cell type
	 */
	public CellType getType()
	{
		return CellType.LABEL;
	}
}
