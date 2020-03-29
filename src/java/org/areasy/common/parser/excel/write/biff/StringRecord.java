package org.areasy.common.parser.excel.write.biff;

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

import org.areasy.common.parser.excel.biff.IntegerHelper;
import org.areasy.common.parser.excel.biff.StringHelper;
import org.areasy.common.parser.excel.biff.Type;
import org.areasy.common.parser.excel.biff.WritableRecordData;

/**
 * Stores the string result of a formula calculation.  This record
 * occurs immediately after the formula
 */
class StringRecord extends WritableRecordData
{
	/**
	 * The string value
	 */
	private String value;

	/**
	 * Constructor
	 */
	public StringRecord(String val)
	{
		super(Type.STRING);

		value = val;
	}

	/**
	 * The binary data to be written out
	 *
	 * @return the binary data
	 */
	public byte[] getData()
	{
		byte[] data = new byte[value.length() * 2 + 3];
		IntegerHelper.getTwoBytes(value.length(), data, 0);
		data[2] = 0x01; // unicode
		StringHelper.getUnicodeBytes(value, data, 3);

		return data;
	}
}
