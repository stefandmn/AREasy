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
 * Identifies the date system as the 1904 system or not
 */
class NineteenFourRecord extends RecordData
{
	/**
	 * The base year for dates
	 */
	private boolean nineteenFour;

	/**
	 * Constructs this object from the raw data
	 *
	 * @param t the raw data
	 */
	public NineteenFourRecord(Record t)
	{
		super(t);

		byte[] data = getRecord().getData();

		nineteenFour = data[0] == 1 ? true : false;

	}

	/**
	 * Accessor to see whether this spreadsheets dates are based around
	 * 1904
	 *
	 * @return true if this workbooks dates are based around the 1904
	 *         date system
	 */
	public boolean is1904()
	{
		return nineteenFour;
	}


}


