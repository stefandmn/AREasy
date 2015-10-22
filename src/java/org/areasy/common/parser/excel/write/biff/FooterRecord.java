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
 * Places a string at the bottom of each page when the file is printed.
 * JExcelApi sets this to be blank
 */
class FooterRecord extends WritableRecordData
{
	/**
	 * The binary data
	 */
	private byte[] data;
	/**
	 * The footer string
	 */
	private String footer;

	/**
	 * Consructor
	 *
	 * @param s the footer
	 */
	public FooterRecord(String s)
	{
		super(Type.FOOTER);

		footer = s;
	}

	/**
	 * Consructor invoked when copying a sheets
	 *
	 * @param fr the read footer record
	 */
	public FooterRecord(FooterRecord fr)
	{
		super(Type.FOOTER);

		footer = fr.footer;
	}

	/**
	 * Gets the binary data to write to the output file
	 *
	 * @return the binary data
	 */
	public byte[] getData()
	{
		if (footer == null || footer.length() == 0)
		{
			data = new byte[0];
			return data;
		}

		data = new byte[footer.length() * 2 + 3];
		IntegerHelper.getTwoBytes(footer.length(), data, 0);
		data[2] = (byte) 0x1;

		StringHelper.getUnicodeBytes(footer, data, 3);

		return data;
	}
}


