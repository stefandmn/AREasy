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
 * Stores a flag which indicates whether the file is a double stream
 * file.  For files written by JExcelAPI, this FALSE
 */
class DSFRecord extends WritableRecordData
{
	/**
	 * The data to be written to the binary file
	 */
	private byte[] data;

	/**
	 * Constructor
	 */
	public DSFRecord()
	{
		super(Type.DSF);

		// Hard code the fact that this is most assuredly not a double
		// stream file
		data = new byte[]{0, 0};
	}

	/**
	 * The binary data to be written out
	 *
	 * @return the binary data
	 */
	public byte[] getData()
	{
		return data;
	}
}
