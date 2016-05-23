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
 * Record which indicates whether or not data ranges and pivot tables
 * should be refreshed when the workbook is loaded
 */
class RefreshAllRecord extends WritableRecordData
{
	/**
	 * The refresh all flag
	 */
	private boolean refreshall;
	/**
	 * The binary data
	 */
	private byte[] data;

	/**
	 * Constructor
	 *
	 * @param refresh refresh all flag
	 */
	public RefreshAllRecord(boolean refresh)
	{
		super(Type.REFRESHALL);

		refreshall = refresh;

		// Hard code in an unprotected workbook
		data = new byte[2];

		if (refreshall)
		{
			IntegerHelper.getTwoBytes(1, data, 0);
		}
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
