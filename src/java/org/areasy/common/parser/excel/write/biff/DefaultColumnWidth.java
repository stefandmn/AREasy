package org.areasy.common.parser.excel.write.biff;

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
import org.areasy.common.parser.excel.biff.Type;
import org.areasy.common.parser.excel.biff.WritableRecordData;

/**
 * The default column width for a workbook
 */
class DefaultColumnWidth extends WritableRecordData
{
	/**
	 * The default column width
	 */
	private int width;
	/**
	 * The binary data
	 */
	private byte[] data;

	/**
	 * Constructor
	 *
	 * @param w the default column width
	 */
	public DefaultColumnWidth(int w)
	{
		super(Type.DEFCOLWIDTH);
		width = w;
		data = new byte[2];
		IntegerHelper.getTwoBytes(width, data, 0);
	}

	/**
	 * Gets the binary data for writing to the stream
	 *
	 * @return the binary data
	 */
	protected byte[] getData()
	{
		return data;
	}
}
