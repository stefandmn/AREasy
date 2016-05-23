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
 * Record which indicates whether Excel should save backup versions of the
 * file
 */
class BackupRecord extends WritableRecordData
{
	/**
	 * Flag to indicate whether or not Excel should make backups
	 */
	private boolean backup;
	/**
	 * The data array
	 */
	private byte[] data;

	/**
	 * Constructor
	 *
	 * @param bu backup flag
	 */
	public BackupRecord(boolean bu)
	{
		super(Type.BACKUP);

		backup = bu;

		// Hard code in an unprotected workbook
		data = new byte[2];

		if (backup)
		{
			IntegerHelper.getTwoBytes(1, data, 0);
		}
	}

	/**
	 * Returns the binary data for writing to the output file
	 *
	 * @return the binary data
	 */
	public byte[] getData()
	{
		return data;
	}
}
