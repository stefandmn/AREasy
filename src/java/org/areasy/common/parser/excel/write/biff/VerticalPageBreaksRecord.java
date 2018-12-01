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

import org.areasy.common.parser.excel.biff.IntegerHelper;
import org.areasy.common.parser.excel.biff.Type;
import org.areasy.common.parser.excel.biff.WritableRecordData;

/**
 * Contains the list of explicit horizontal page breaks on the current sheet
 */
class VerticalPageBreaksRecord extends WritableRecordData
{
	/**
	 * The row breaks
	 */
	private int[] columnBreaks;

	/**
	 * Constructor
	 *
	 * @param break the row breaks
	 */
	public VerticalPageBreaksRecord(int[] breaks)
	{
		super(Type.VERTICALPAGEBREAKS);

		columnBreaks = breaks;
	}

	/**
	 * Gets the binary data to write to the output file
	 *
	 * @return the binary data
	 */
	public byte[] getData()
	{
		byte[] data = new byte[columnBreaks.length * 6 + 2];

		// The number of breaks on the list
		IntegerHelper.getTwoBytes(columnBreaks.length, data, 0);
		int pos = 2;

		for (int i = 0; i < columnBreaks.length; i++)
		{
			IntegerHelper.getTwoBytes(columnBreaks[i], data, pos);
			IntegerHelper.getTwoBytes(0xff, data, pos + 4);
			pos += 6;
		}

		return data;
	}
}


