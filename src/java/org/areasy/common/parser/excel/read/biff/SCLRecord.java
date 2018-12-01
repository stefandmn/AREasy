package org.areasy.common.parser.excel.read.biff;

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
import org.areasy.common.parser.excel.biff.RecordData;
import org.areasy.common.parser.excel.biff.Type;

/**
 * Class containing the zoom factor for display
 */
class SCLRecord extends RecordData
{
	/**
	 * The numerator of the zoom
	 */
	private int numerator;

	/**
	 * The denominator of the zoom
	 */
	private int denominator;

	/**
	 * Constructs this record from the raw data
	 *
	 * @param r the record
	 */
	protected SCLRecord(Record r)
	{
		super(Type.SCL);

		byte[] data = r.getData();

		numerator = IntegerHelper.getInt(data[0], data[1]);
		denominator = IntegerHelper.getInt(data[2], data[3]);
	}

	/**
	 * Accessor for the zoom factor
	 *
	 * @return the zoom factor as the nearest integer percentage
	 */
	public int getZoomFactor()
	{
		return numerator * 100 / denominator;
	}
}
