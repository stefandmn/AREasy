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

import org.areasy.common.parser.excel.biff.DoubleHelper;
import org.areasy.common.parser.excel.biff.Type;
import org.areasy.common.parser.excel.biff.WritableRecordData;

/**
 * Record which stores the maximum change value from the Options
 * dialog
 */
class DeltaRecord extends WritableRecordData
{
	/**
	 * The binary data
	 */
	private byte[] data;
	/**
	 * The number of iterations
	 */
	private double iterationValue;

	/**
	 * Constructor
	 *
	 * @param itval
	 */
	public DeltaRecord(double itval)
	{
		super(Type.DELTA);
		iterationValue = itval;

		data = new byte[8];
	}


	/**
	 * Gets the binary data for writing to the output file
	 *
	 * @return the binary data
	 */
	public byte[] getData()
	{
		DoubleHelper.getIEEEBytes(iterationValue, data, 0);

		/*    long val = Double.doubleToLongBits(iterationValue);
			data[0] = (byte) (val & 0xff);
			data[1] = (byte) ((val & 0xff00) >> 8);
			data[2] = (byte) ((val & 0xff0000) >> 16);
			data[3] = (byte) ((val & 0xff000000) >> 24);
			data[4] = (byte) ((val & 0xff00000000L) >> 32);
			data[5] = (byte) ((val & 0xff0000000000L) >> 40);
			data[6] = (byte) ((val & 0xff000000000000L) >> 48);
			data[7] = (byte) ((val & 0xff00000000000000L) >> 56) ;
			*/

		return data;
	}
}


