package org.areasy.common.parser.excel.read.biff;

/*
 * Copyright (c) 2007-2020 AREasy Runtime
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
 * A Beginning Of File record, found at the commencement of all substreams
 * within a biff8 file
 */
public class BOFRecord extends RecordData
{
	/**
	 * The logger
	 */
	private static Logger logger = LoggerFactory.getLog(BOFRecord.class);

	/**
	 * The code used for biff8 files
	 */
	private static final int Biff8 = 0x600;
	/**
	 * The code used for biff8 files
	 */
	private static final int Biff7 = 0x500;
	/**
	 * The code used for workbook globals
	 */
	private static final int WorkbookGlobals = 0x5;
	/**
	 * The code used for worksheets
	 */
	private static final int Worksheet = 0x10;
	/**
	 * The code used for charts
	 */
	private static final int Chart = 0x20;
	/**
	 * The code used for macro sheets
	 */
	private static final int MacroSheet = 0x40;

	/**
	 * The biff version of this substream
	 */
	private int version;
	/**
	 * The type of this substream
	 */
	private int substreamType;

	/**
	 * Constructs this object from the raw data
	 *
	 * @param t the raw data
	 */
	BOFRecord(Record t)
	{
		super(t);
		byte[] data = getRecord().getData();
		version = IntegerHelper.getInt(data[0], data[1]);
		substreamType = IntegerHelper.getInt(data[2], data[3]);
	}

	/**
	 * Interrogates this object to see if it is a biff8 substream
	 *
	 * @return TRUE if this substream is biff8, false otherwise
	 */
	public boolean isBiff8()
	{
		return version == Biff8;
	}

	/**
	 * Interrogates this object to see if it is a biff7 substream
	 *
	 * @return TRUE if this substream is biff7, false otherwise
	 */
	public boolean isBiff7()
	{
		return version == Biff7;
	}


	/**
	 * Interrogates this substream to see if it represents the commencement of
	 * the workbook globals substream
	 *
	 * @return TRUE if this is the commencement of a workbook globals substream,
	 *         FALSE otherwise
	 */
	boolean isWorkbookGlobals()
	{
		return substreamType == WorkbookGlobals;
	}

	/**
	 * Interrogates the substream to see if it is the commencement of a process
	 *
	 * @return TRUE if this substream is the beginning of a process, FALSE
	 *         otherwise
	 */
	public boolean isWorksheet()
	{
		return substreamType == Worksheet;
	}

	/**
	 * Interrogates the substream to see if it is the commencement of a process
	 *
	 * @return TRUE if this substream is the beginning of a process, FALSE
	 *         otherwise
	 */
	public boolean isMacroSheet()
	{
		return substreamType == MacroSheet;
	}

	/**
	 * Interrogates the substream to see if it is a chart
	 *
	 * @return TRUE if this substream is the beginning of a process, FALSE
	 *         otherwise
	 */
	public boolean isChart()
	{
		return substreamType == Chart;
	}

	/**
	 * Gets the length of the data portion of this record
	 * Used to adjust when reading sheets which contain just a chart
	 *
	 * @return the length of the data portion of this record
	 */
	int getLength()
	{
		return getRecord().getLength();
	}

}









