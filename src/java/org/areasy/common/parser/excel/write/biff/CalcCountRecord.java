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
 * Record which stores the maximum iterations option from the Options
 * dialog box
 */
class CalcCountRecord extends WritableRecordData
{
	/**
	 * The iteration count
	 */
	private int calcCount;
	/**
	 * The binary data to write to the output file
	 */
	private byte[] data;

	/**
	 * Constructor
	 *
	 * @param cnt the count indicator
	 */
	public CalcCountRecord(int cnt)
	{
		super(Type.CALCCOUNT);
		calcCount = cnt;
	}


	/**
	 * Gets the data to write out to the file
	 *
	 * @return the binary data
	 */
	public byte[] getData()
	{
		byte[] data = new byte[2];

		IntegerHelper.getTwoBytes(calcCount, data, 0);

		return data;
	}
}


