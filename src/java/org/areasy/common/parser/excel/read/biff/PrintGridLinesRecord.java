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

import org.areasy.common.parser.excel.biff.RecordData;

/**
 * Contains the print grid lines option of this process
 */
class PrintGridLinesRecord extends RecordData
{
	/**
	 * print grid lines flag
	 */
	private boolean printGridLines;

	/**
	 * Constructs the value from the raw data
	 *
	 * @param pgl the raw data
	 */
	public PrintGridLinesRecord(Record pgl)
	{
		super(pgl);
		byte[] data = pgl.getData();

		printGridLines = (data[0] == 1 ? true : false);
	}

	/**
	 * Accessor for the print grid lines flag
	 *
	 * @return TRUE to print grid lines, FALSE otherwise
	 */
	public boolean getPrintGridLines()
	{
		return printGridLines;
	}

}
