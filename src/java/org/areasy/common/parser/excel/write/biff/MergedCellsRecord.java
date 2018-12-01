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

import org.areasy.common.parser.excel.Cell;
import org.areasy.common.parser.excel.Range;
import org.areasy.common.parser.excel.biff.IntegerHelper;
import org.areasy.common.parser.excel.biff.Type;
import org.areasy.common.parser.excel.biff.WritableRecordData;

import java.util.ArrayList;

/**
 * A number record.  This is stored as 8 bytes, as opposed to the
 * 4 byte RK record
 */
public class MergedCellsRecord extends WritableRecordData
{
	/**
	 * The ranges of all the cells which are merged on this sheet
	 */
	private ArrayList ranges;

	/**
	 * Constructs a merged cell record
	 *
	 * @param ws the sheet containing the merged cells
	 */
	protected MergedCellsRecord(ArrayList mc)
	{
		super(Type.MERGEDCELLS);

		ranges = mc;
	}

	/**
	 * Gets the raw data for output to file
	 *
	 * @return the data to write to file
	 */
	public byte[] getData()
	{
		byte[] data = new byte[ranges.size() * 8 + 2];

		// Set the number of ranges
		IntegerHelper.getTwoBytes(ranges.size(), data, 0);

		int pos = 2;
		Range range = null;
		for (int i = 0; i < ranges.size(); i++)
		{
			range = (Range) ranges.get(i);

			// Set the various cell records
			Cell tl = range.getTopLeft();
			Cell br = range.getBottomRight();

			IntegerHelper.getTwoBytes(tl.getRow(), data, pos);
			IntegerHelper.getTwoBytes(br.getRow(), data, pos + 2);
			IntegerHelper.getTwoBytes(tl.getColumn(), data, pos + 4);
			IntegerHelper.getTwoBytes(br.getColumn(), data, pos + 6);

			pos += 8;
		}

		return data;
	}

}







