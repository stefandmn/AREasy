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
import org.areasy.common.parser.excel.write.Number;

import java.util.List;

/**
 * Contains an array of RK numbers
 */
class MulRKRecord extends WritableRecordData
{
	/**
	 * The row  containing these numbers
	 */
	private int row;
	/**
	 * The first column these rk number occur on
	 */
	private int colFirst;
	/**
	 * The last column these rk number occur on
	 */
	private int colLast;
	/**
	 * The array of rk numbers
	 */
	private int[] rknumbers;
	/**
	 * The array of xf indices
	 */
	private int[] xfIndices;

	/**
	 * Constructs the rk numbers from the integer cells
	 *
	 * @param numbers A list of com.snt.parser.documents.excel.write.Number objects
	 */
	public MulRKRecord(List numbers)
	{
		super(Type.MULRK);
		row = ((Number) numbers.get(0)).getRow();
		colFirst = ((Number) numbers.get(0)).getColumn();
		colLast = colFirst + numbers.size() - 1;

		rknumbers = new int[numbers.size()];
		xfIndices = new int[numbers.size()];

		for (int i = 0; i < numbers.size(); i++)
		{
			rknumbers[i] = (int) ((Number) numbers.get(i)).getValue();
			xfIndices[i] = ((CellValue) numbers.get(i)).getXFIndex();
		}
	}

	/**
	 * Gets the binary data for output to file
	 *
	 * @return the binary data
	 */
	public byte[] getData()
	{
		byte[] data = new byte[rknumbers.length * 6 + 6];

		// Set up the row and the first column
		IntegerHelper.getTwoBytes(row, data, 0);
		IntegerHelper.getTwoBytes(colFirst, data, 2);

		// Add all the rk numbers
		int pos = 4;
		int rkValue = 0;
		byte[] rkBytes = new byte[4];
		for (int i = 0; i < rknumbers.length; i++)
		{
			IntegerHelper.getTwoBytes(xfIndices[i], data, pos);

			// To represent an int as an Excel RK value, we have to
			// undergo some outrageous jiggery pokery, as follows:

			// Gets the  bit representation of the number
			rkValue = rknumbers[i] << 2;

			// Set the integer bit
			rkValue |= 0x2;
			IntegerHelper.getFourBytes(rkValue, data, pos + 2);

			pos += 6;
		}

		// Write the number of rk numbers in this record
		IntegerHelper.getTwoBytes(colLast, data, pos);

		return data;
	}
}




