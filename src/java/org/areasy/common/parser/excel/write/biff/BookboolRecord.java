package org.areasy.common.parser.excel.write.biff;

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
import org.areasy.common.parser.excel.biff.Type;
import org.areasy.common.parser.excel.biff.WritableRecordData;

/**
 * Writes out the workbook option flag (should it save the external
 * link options)
 */
class BookboolRecord extends WritableRecordData
{
	/**
	 * The external link option flag
	 */
	private boolean externalLink;
	/**
	 * The binary data to write out
	 */
	private byte[] data;

	/**
	 * Constructor
	 *
	 * @param extlink the external link options flag
	 */
	public BookboolRecord(boolean extlink)
	{
		super(Type.BOOKBOOL);

		externalLink = extlink;
		data = new byte[2];

		if (!externalLink)
		{
			IntegerHelper.getTwoBytes(1, data, 0);
		}
	}

	/**
	 * Gets the binary data to write to the output file
	 *
	 * @return the binary data
	 */
	public byte[] getData()
	{
		return data;
	}
}
