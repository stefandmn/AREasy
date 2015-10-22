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
 * Contains the cell dimensions of this worksheet
 */
class VerticalPageBreaksRecord extends RecordData
{
	/**
	 * The logger
	 */
	private final Logger logger = LoggerFactory.getLog
			(VerticalPageBreaksRecord.class);

	/**
	 * The row page breaks
	 */
	private int[] columnBreaks;

	/**
	 * Dummy indicators for overloading the constructor
	 */
	private static class Biff7
	{
	}

	;
	public static Biff7 biff7 = new Biff7();

	/**
	 * Constructs the dimensions from the raw data
	 *
	 * @param t the raw data
	 */
	public VerticalPageBreaksRecord(Record t)
	{
		super(t);

		byte[] data = t.getData();

		int numbreaks = IntegerHelper.getInt(data[0], data[1]);
		int pos = 2;
		columnBreaks = new int[numbreaks];

		for (int i = 0; i < numbreaks; i++)
		{
			columnBreaks[i] = IntegerHelper.getInt(data[pos], data[pos + 1]);
			pos += 6;
		}
	}

	/**
	 * Constructs the dimensions from the raw data
	 *
	 * @param t	 the raw data
	 * @param biff7 an indicator to initialise this record for biff 7 format
	 */
	public VerticalPageBreaksRecord(Record t, Biff7 biff7)
	{
		super(t);

		byte[] data = t.getData();
		int numbreaks = IntegerHelper.getInt(data[0], data[1]);
		int pos = 2;
		columnBreaks = new int[numbreaks];
		for (int i = 0; i < numbreaks; i++)
		{
			columnBreaks[i] = IntegerHelper.getInt(data[pos], data[pos + 1]);
			pos += 2;
		}
	}

	/**
	 * Gets the row breaks
	 *
	 * @return the row breaks on the current sheet
	 */
	public int[] getColumnBreaks()
	{
		return columnBreaks;
	}
}







