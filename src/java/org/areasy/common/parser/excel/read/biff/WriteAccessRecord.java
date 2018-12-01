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

import org.areasy.common.parser.excel.WorkbookSettings;
import org.areasy.common.parser.excel.biff.RecordData;
import org.areasy.common.parser.excel.biff.StringHelper;
import org.areasy.common.parser.excel.biff.Type;

/**
 * A write access record
 */
class WriteAccessRecord extends RecordData
{
	/**
	 * The write access user name
	 */
	private String wauser;

	/**
	 * Constructor
	 *
	 * @param t	   the raw bytes
	 * @param isBiff8 Is record BIFF8 (else BIFF7)
	 */
	public WriteAccessRecord(Record t, boolean isBiff8, WorkbookSettings ws)
	{
		super(Type.WRITEACCESS);

		byte[] data = t.getData();
		if (isBiff8)
		{
			wauser = StringHelper.getUnicodeString(data, 112 / 2, 0);
		}
		else
		{
			// BIFF7 does not use unicode encoding in string
			int length = data[1];
			wauser = StringHelper.getString(data, length, 1, ws);
		}
	}

	/**
	 * Gets the binary data for output to file
	 *
	 * @return write access user name
	 */
	public String getWriteAccess()
	{
		return wauser;
	}
}
