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

import org.areasy.common.parser.excel.biff.RecordData;

/**
 * Contains the print grid lines option of this worksheet
 */
class PrintHeadersRecord extends RecordData
{
	/**
	 * print grid lines flag
	 */
	private boolean printHeaders;

	/**
	 * Constructs the value from the raw data
	 *
	 * @param ph the raw data
	 */
	public PrintHeadersRecord(Record ph)
	{
		super(ph);
		byte[] data = ph.getData();

		printHeaders = (data[0] == 1 ? true : false);
	}

	/**
	 * Accessor for the print headers flag
	 *
	 * @return TRUE to print headers, FALSE otherwise
	 */
	public boolean getPrintHeaders()
	{
		return printHeaders;
	}

}
