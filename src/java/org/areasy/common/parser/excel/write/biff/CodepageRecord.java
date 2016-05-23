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

import org.areasy.common.parser.excel.biff.Type;
import org.areasy.common.parser.excel.biff.WritableRecordData;

/**
 * Stores the default character set in operation when the workbook was
 * saved
 */
class CodepageRecord extends WritableRecordData
{
	/**
	 * The binary data
	 */
	private byte[] data;

	/**
	 * Constructor
	 */
	public CodepageRecord()
	{
		super(Type.CODEPAGE);

		// Hard code inthe ANSI character set for Microsoft
		data = new byte[]{(byte) 0xe4, (byte) 0x4};
	}

	/**
	 * Retrieves the data for output to binary file
	 *
	 * @return the data to be written
	 */
	public byte[] getData()
	{
		return data;
	}
}
