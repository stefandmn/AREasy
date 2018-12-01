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
 * The default row height for cells in the workbook
 */
class DefaultRowHeightRecord extends WritableRecordData
{
	/**
	 * The binary data
	 */
	private byte[] data;
	/**
	 * The default row height
	 */
	private int rowHeight;

	/**
	 * Indicates whether or not the default row height has been changed
	 */
	private boolean changed;

	/**
	 * Constructor
	 *
	 * @param height the default row height
	 * @param ch	 TRUE if the default value has been changed, false
	 *               otherwise
	 */
	public DefaultRowHeightRecord(int h, boolean ch)
	{
		super(Type.DEFAULTROWHEIGHT);
		data = new byte[4];
		rowHeight = h;
		changed = ch;
	}

	/**
	 * Gets the binary data for writing to the output stream
	 *
	 * @return the binary data
	 */
	public byte[] getData()
	{
		if (changed)
		{
			data[0] |= 0x1;
		}

		IntegerHelper.getTwoBytes(rowHeight, data, 2);
		return data;
	}
}


