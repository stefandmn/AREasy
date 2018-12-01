package org.areasy.common.parser.excel.write.biff;

/*
 * Copyright (c) 2007-2018 AREasy Runtime
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
 * Record which specifies a print header for a work sheet
 */
class PLSRecord extends WritableRecordData
{
	/**
	 * The binary data
	 */
	private byte[] data;

	/**
	 * Consructor invoked when copying a spreadsheet
	 *
	 * @param hr the read header record
	 */
	public PLSRecord(org.areasy.common.parser.excel.read.biff.PLSRecord hr)
	{
		super(Type.PLS);

		data = hr.getData();
	}

	/**
	 * Consructor invoked when copying a sheets
	 *
	 * @param hr the read header record
	 */
	public PLSRecord(PLSRecord hr)
	{
		super(Type.PLS);

		data = new byte[hr.data.length];
		System.arraycopy(hr.data, 0, data, 0, data.length);
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


