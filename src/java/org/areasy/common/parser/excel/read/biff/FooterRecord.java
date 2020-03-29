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

import org.areasy.common.parser.excel.WorkbookSettings;
import org.areasy.common.parser.excel.biff.IntegerHelper;
import org.areasy.common.parser.excel.biff.RecordData;
import org.areasy.common.parser.excel.biff.StringHelper;

/**
 * A workbook page footer record
 */
public class FooterRecord extends RecordData
{
	/**
	 * The footer
	 */
	private String footer;

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
	 * @param t  the record data
	 * @param ws the workbook settings
	 */
	FooterRecord(Record t, WorkbookSettings ws)
	{
		super(t);
		byte[] data = getRecord().getData();

		if (data.length == 0)
		{
			return;
		}

		int chars = IntegerHelper.getInt(data[0], data[1]);

		boolean unicode = data[2] == 1;

		if (unicode)
		{
			footer = StringHelper.getUnicodeString(data, chars, 3);
		}
		else
		{
			footer = StringHelper.getString(data, chars, 3, ws);
		}
	}

	/**
	 * Constructs this object from the raw data
	 *
	 * @param t	 the record data
	 * @param ws	the workbook settings
	 * @param dummy dummy record to indicate a biff7 document
	 */
	FooterRecord(Record t, WorkbookSettings ws, Biff7 dummy)
	{
		super(t);
		byte[] data = getRecord().getData();

		if (data.length == 0)
		{
			return;
		}

		int chars = data[0];
		footer = StringHelper.getString(data, chars, 1, ws);
	}

	/**
	 * Gets the footer string
	 *
	 * @return the footer string
	 */
	String getFooter()
	{
		return footer;
	}
}
