package org.areasy.common.parser.excel.read.biff;

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

import org.areasy.common.logger.Logger;
import org.areasy.common.logger.LoggerFactory;
import org.areasy.common.parser.excel.biff.IntegerHelper;
import org.areasy.common.parser.excel.biff.RecordData;

/**
 * Contains an array of Blank, formatted cells
 */
class MulBlankRecord extends RecordData
{
	/**
	 * The logger
	 */
	private static Logger logger = LoggerFactory.getLog(MulBlankRecord.class);

	/**
	 * The row  containing these numbers
	 */
	private int row;
	/**
	 * The first column these rk number occur on
	 */
	private int colFirst;
	/**
	 * The last column these blank numbers occur on
	 */
	private int colLast;
	/**
	 * The number of blank numbers contained in this record
	 */
	private int numblanks;
	/**
	 * The array of xf indices
	 */
	private int[] xfIndices;

	/**
	 * Constructs the blank records from the raw data
	 *
	 * @param t the raw data
	 */
	public MulBlankRecord(Record t)
	{
		super(t);
		byte[] data = getRecord().getData();
		int length = getRecord().getLength();
		row = IntegerHelper.getInt(data[0], data[1]);
		colFirst = IntegerHelper.getInt(data[2], data[3]);
		colLast = IntegerHelper.getInt(data[length - 2], data[length - 1]);
		numblanks = colLast - colFirst + 1;
		xfIndices = new int[numblanks];

		readBlanks(data);
	}

	/**
	 * Reads the blanks from the raw data
	 *
	 * @param data the raw data
	 */
	private void readBlanks(byte[] data)
	{
		int pos = 4;
		for (int i = 0; i < numblanks; i++)
		{
			xfIndices[i] = IntegerHelper.getInt(data[pos], data[pos + 1]);
			pos += 2;
		}
	}

	/**
	 * Accessor for the row
	 *
	 * @return the row of containing these blank numbers
	 */
	public int getRow()
	{
		return row;
	}

	/**
	 * The first column containing the blank numbers
	 *
	 * @return the first column
	 */
	public int getFirstColumn()
	{
		return colFirst;
	}

	/**
	 * Accessor for the number of blank values
	 *
	 * @return the number of blank values
	 */
	public int getNumberOfColumns()
	{
		return numblanks;
	}

	/**
	 * Return a specific formatting index
	 *
	 * @param index the cell index in the group
	 * @return the formatting index
	 */
	public int getXFIndex(int index)
	{
		return xfIndices[index];
	}
}




