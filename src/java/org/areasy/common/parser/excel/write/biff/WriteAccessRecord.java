package org.areasy.common.parser.excel.write.biff;

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

import org.areasy.common.parser.excel.Workbook;
import org.areasy.common.parser.excel.biff.StringHelper;
import org.areasy.common.parser.excel.biff.Type;
import org.areasy.common.parser.excel.biff.WritableRecordData;

/**
 * The name used when Excel was installed.
 * When writing worksheets, it uses the value from the WorkbookSettings object,
 * if this is not set (null) this is hard coded as
 * Java Excel API + Version number
 */
class WriteAccessRecord extends WritableRecordData
{
	/**
	 * The data to output to file
	 */
	private byte[] data;

	// String of length 112 characters
	/**
	 * The author of this workbook (ie. the Java Excel API)
	 */
	private final static String authorString = "Java Excel API";
	private String userName;

	/**
	 * Constructor
	 */
	public WriteAccessRecord(String userName)
	{
		super(Type.WRITEACCESS);

		data = new byte[112];
		String astring = userName != null ?
				userName :
				authorString + " v" + Workbook.getVersion();

		StringHelper.getBytes(astring, data, 0);

		// Pad out the record with space characters
		for (int i = astring.length(); i < data.length; i++)
		{
			data[i] = 0x20;
		}
	}

	/**
	 * Gets the data for output to file
	 *
	 * @return the binary data
	 */
	public byte[] getData()
	{
		return data;
	}
}
