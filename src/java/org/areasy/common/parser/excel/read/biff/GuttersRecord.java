package org.areasy.common.parser.excel.read.biff;

/*
 * Copyright (c) 2007-2016 AREasy Runtime
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

/**
 * The gutters record
 */
public class GuttersRecord extends RecordData
{
	private int width;
	private int height;
	private int rowOutlineLevel;
	private int columnOutlineLevel;

	/**
	 * Constructs this object from the raw data
	 *
	 * @param r the raw data
	 */
	public GuttersRecord(Record r)
	{
		super(r);

		byte[] data = getRecord().getData();
		width = IntegerHelper.getInt(data[0], data[1]);
		height = IntegerHelper.getInt(data[2], data[3]);
		rowOutlineLevel = IntegerHelper.getInt(data[4], data[5]);
		columnOutlineLevel = IntegerHelper.getInt(data[6], data[7]);
	}

	int getRowOutlineLevel()
	{
		return rowOutlineLevel;
	}

	int getColumnOutlineLevel()
	{
		return columnOutlineLevel;
	}

}
