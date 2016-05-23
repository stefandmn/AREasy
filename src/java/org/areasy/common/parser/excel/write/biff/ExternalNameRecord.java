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

import org.areasy.common.logger.Logger;
import org.areasy.common.logger.LoggerFactory;
import org.areasy.common.parser.excel.biff.StringHelper;
import org.areasy.common.parser.excel.biff.Type;
import org.areasy.common.parser.excel.biff.WritableRecordData;

/**
 * An external sheet record, used to maintain integrity when formulas
 * are copied from read databases
 */
class ExternalNameRecord extends WritableRecordData
{
	/**
	 * The logger
	 */
	Logger logger = LoggerFactory.getLog(ExternalNameRecord.class);

	/**
	 * The name of the addin
	 */
	private String name;

	/**
	 * Constructor used for writable workbooks
	 */
	public ExternalNameRecord(String n)
	{
		super(Type.EXTERNNAME);
		name = n;
	}

	/**
	 * Gets the binary data for output to file
	 *
	 * @return the binary data
	 */
	public byte[] getData()
	{
		byte[] data = new byte[name.length() * 2 + 12];

		data[6] = (byte) name.length();
		data[7] = 0x1;
		StringHelper.getUnicodeBytes(name, data, 8);

		int pos = 8 + name.length() * 2;
		data[pos] = 0x2;
		data[pos + 1] = 0x0;
		data[pos + 2] = 0x1c;
		data[pos + 3] = 0x17;

		return data;
	}
}
