package org.areasy.common.parser.excel.write.biff;

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

import org.areasy.common.parser.excel.biff.IntegerHelper;
import org.areasy.common.parser.excel.biff.StringHelper;
import org.areasy.common.parser.excel.biff.Type;
import org.areasy.common.parser.excel.biff.WritableRecordData;

/**
 * Record which specifies a print header for a work sheet
 */
class HeaderRecord extends WritableRecordData
{
	/**
	 * The binary data
	 */
	private byte[] data;
	/**
	 * The print header string
	 */
	private String header;

	/**
	 * Constructor
	 *
	 * @param s the header string
	 */
	public HeaderRecord(String h)
	{
		super(Type.HEADER);

		header = h;
	}

	/**
	 * Consructor invoked when copying a sheets
	 *
	 * @param hr the read header record
	 */
	public HeaderRecord(HeaderRecord hr)
	{
		super(Type.HEADER);

		header = hr.header;
	}

	/**
	 * Gets the binary data for output to file
	 *
	 * @return the binary data
	 */
	public byte[] getData()
	{
		if (header == null || header.length() == 0)
		{
			data = new byte[0];
			return data;
		}

		data = new byte[header.length() * 2 + 3];
		IntegerHelper.getTwoBytes(header.length(), data, 0);
		data[2] = (byte) 0x1;

		StringHelper.getUnicodeBytes(header, data, 3);

		return data;
	}
}


