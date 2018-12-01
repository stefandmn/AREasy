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
 * The calculation mode for the workbook, as set from the Options
 * dialog box
 */
class CalcModeRecord extends WritableRecordData
{
	/**
	 * The calculation mode (manual, automatic)
	 */
	private CalcMode calculationMode;

	private static class CalcMode
	{
		/**
		 * The indicator as written to the output file
		 */
		int value;

		/**
		 * Constructor
		 *
		 * @param m
		 */
		public CalcMode(int m)
		{
			value = m;
		}
	}

	/**
	 * Manual calculation
	 */
	static CalcMode manual = new CalcMode(0);
	/**
	 * Automatic calculation
	 */
	static CalcMode automatic = new CalcMode(1);
	/**
	 * Automatic calculation, except tables
	 */
	static CalcMode automaticNoTables = new CalcMode(-1);

	/**
	 * Constructor
	 *
	 * @param cm the calculation mode
	 */
	public CalcModeRecord(CalcMode cm)
	{
		super(Type.CALCMODE);
		calculationMode = cm;
	}


	/**
	 * Gets the binary to data to write to the output file
	 *
	 * @return the binary data
	 */
	public byte[] getData()
	{
		byte[] data = new byte[2];

		IntegerHelper.getTwoBytes(calculationMode.value, data, 0);

		return data;
	}
}


