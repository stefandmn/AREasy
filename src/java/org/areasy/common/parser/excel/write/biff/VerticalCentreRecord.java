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

import org.areasy.common.parser.excel.biff.Type;
import org.areasy.common.parser.excel.biff.WritableRecordData;

/**
 * Indicates whether the centre vertically on page option has been
 * set from the options dialog box
 */
class VerticalCentreRecord extends WritableRecordData
{
	/**
	 * The binary data for output to file
	 */
	private byte[] data;
	/**
	 * The centre flag
	 */
	private boolean centre;

	/**
	 * Constructor
	 *
	 * @param ce the centre flag
	 */
	public VerticalCentreRecord(boolean ce)
	{
		super(Type.VCENTER);

		centre = ce;

		data = new byte[2];

		if (centre)
		{
			data[0] = 1;
		}
	}

	/**
	 * Gets the data for output to file
	 *
	 * @return the binary data
	 */
	public byte[] getData()
	{
		return data;
	}
}


