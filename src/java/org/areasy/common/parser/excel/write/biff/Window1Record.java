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

import org.areasy.common.parser.excel.biff.IntegerHelper;
import org.areasy.common.parser.excel.biff.Type;
import org.areasy.common.parser.excel.biff.WritableRecordData;

/**
 * Contains workbook level windowing attributes
 */
class Window1Record extends WritableRecordData
{
	/**
	 * The binary data
	 */
	private byte[] data;

	/**
	 * The selected sheet
	 */
	private int selectedSheet;

	/**
	 * Constructor
	 */
	public Window1Record(int selSheet)
	{
		super(Type.WINDOW1);

		selectedSheet = selSheet;

		// hard code the data in for now
		data = new byte[]
				{(byte) 0x68,
						(byte) 0x1,
						(byte) 0xe,
						(byte) 0x1,
						(byte) 0x5c,
						(byte) 0x3a,
						(byte) 0xbe,
						(byte) 0x23,
						(byte) 0x38,
						(byte) 0,
						(byte) 0,
						(byte) 0,
						(byte) 0,
						(byte) 0,
						(byte) 0x1,
						(byte) 0,
						(byte) 0x58,
						(byte) 0x2};

		IntegerHelper.getTwoBytes(selectedSheet, data, 10);
	}

	/**
	 * Gets the binary data for output to file
	 *
	 * @return the binary data
	 */
	public byte[] getData()
	{
		return data;
	}
}
