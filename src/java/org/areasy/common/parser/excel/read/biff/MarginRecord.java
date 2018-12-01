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

import org.areasy.common.parser.excel.biff.DoubleHelper;
import org.areasy.common.parser.excel.biff.RecordData;
import org.areasy.common.parser.excel.biff.Type;

/**
 * Abstract class containing the margin value for top,left,right and bottom
 * margins
 */
abstract class MarginRecord extends RecordData
{
	/**
	 * The size of the margin
	 */
	private double margin;

	/**
	 * Constructs this record from the raw data
	 *
	 * @param t the type
	 * @param r the record
	 */
	protected MarginRecord(Type t, Record r)
	{
		super(t);

		byte[] data = r.getData();

		margin = DoubleHelper.getIEEEDouble(data, 0);
	}

	/**
	 * Accessor for the margin
	 *
	 * @return the margin
	 */
	double getMargin()
	{
		return margin;
	}
}
