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
 * Contains the default column width for cells in this sheet
 */
class DefaultColumnWidthRecord extends RecordData
{
	/**
	 * The default columns width, in characters
	 */
	private int width;

	/**
	 * Constructs the def col width from the raw data
	 *
	 * @param t the raw data
	 */
	public DefaultColumnWidthRecord(Record t)
	{
		super(t);
		byte[] data = t.getData();

		width = IntegerHelper.getInt(data[0], data[1]);
	}


	/**
	 * Accessor for the default width
	 *
	 * @return the width
	 */
	public int getWidth()
	{
		return width;
	}
}







