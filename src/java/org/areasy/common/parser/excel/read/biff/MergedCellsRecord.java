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

import org.areasy.common.parser.excel.Range;
import org.areasy.common.parser.excel.Sheet;
import org.areasy.common.parser.excel.biff.IntegerHelper;
import org.areasy.common.parser.excel.biff.RecordData;
import org.areasy.common.parser.excel.biff.DefaultSheetRange;

/**
 * A merged cells record for a given sheet
 */
public class MergedCellsRecord extends RecordData
{
	/**
	 * The ranges of the cells merged on this sheet
	 */
	private Range[] ranges;

	/**
	 * Constructs this object from the raw data
	 *
	 * @param t the raw data
	 * @param s the sheet
	 */
	MergedCellsRecord(Record t, Sheet s)
	{
		super(t);

		byte[] data = getRecord().getData();

		int numRanges = IntegerHelper.getInt(data[0], data[1]);

		ranges = new Range[numRanges];

		int pos = 2;
		int firstRow = 0;
		int lastRow = 0;
		int firstCol = 0;
		int lastCol = 0;

		for (int i = 0; i < numRanges; i++)
		{
			firstRow = IntegerHelper.getInt(data[pos], data[pos + 1]);
			lastRow = IntegerHelper.getInt(data[pos + 2], data[pos + 3]);
			firstCol = IntegerHelper.getInt(data[pos + 4], data[pos + 5]);
			lastCol = IntegerHelper.getInt(data[pos + 6], data[pos + 7]);

			ranges[i] = new DefaultSheetRange(s, firstCol, firstRow,
					lastCol, lastRow);

			pos += 8;
		}
	}

	/**
	 * Gets the ranges which have been merged in this sheet
	 *
	 * @return the ranges of cells which have been merged
	 */
	public Range[] getRanges()
	{
		return ranges;
	}
}







