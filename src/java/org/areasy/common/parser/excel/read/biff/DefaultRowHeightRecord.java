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

import org.areasy.common.parser.excel.biff.IntegerHelper;
import org.areasy.common.parser.excel.biff.RecordData;

/**
 * Contains the default column width for cells in this sheet
 */
class DefaultRowHeightRecord extends RecordData
{
	/**
	 * The default row height, in 1/20ths of a point
	 */
	private int height;

	/**
	 * Constructs the def col width from the raw data
	 *
	 * @param t the raw data
	 */
	public DefaultRowHeightRecord(Record t)
	{
		super(t);
		byte[] data = t.getData();

		if (data.length > 2)
		{
			height = IntegerHelper.getInt(data[2], data[3]);
		}
	}

	/**
	 * Accessor for the default height
	 *
	 * @return the height
	 */
	public int getHeight()
	{
		return height;
	}
}







