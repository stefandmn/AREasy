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
import org.areasy.common.parser.excel.biff.Type;
import org.areasy.common.parser.excel.biff.WritableRecordData;

/**
 * A record which indicates whether or the 1904 date system is
 * in use
 */
class NineteenFourRecord extends WritableRecordData
{
	/**
	 * Flag which indicates whether the 1904 date system is being used
	 */
	private boolean nineteenFourDate;

	/**
	 * The binary data for output to file
	 */
	private byte[] data;

	/**
	 * Constructor
	 *
	 * @param oldDate flag indicating whether the 1904 date system is in use
	 */
	public NineteenFourRecord(boolean oldDate)
	{
		super(Type.NINETEENFOUR);

		nineteenFourDate = oldDate;
		data = new byte[2];

		if (nineteenFourDate)
		{
			IntegerHelper.getTwoBytes(1, data, 0);
		}
	}

	/**
	 * The binary data for output to file
	 *
	 * @return the binary data
	 */
	public byte[] getData()
	{
		return data;
	}
}
