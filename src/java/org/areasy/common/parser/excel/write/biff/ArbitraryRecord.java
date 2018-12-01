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

import org.areasy.common.logger.Logger;
import org.areasy.common.logger.LoggerFactory;
import org.areasy.common.parser.excel.biff.Type;
import org.areasy.common.parser.excel.biff.WritableRecordData;

/**
 * Writes out some arbitrary record data.  Used during the debug process
 */
class ArbitraryRecord extends WritableRecordData
{
	/**
	 * The logger
	 */
	private static Logger logger = LoggerFactory.getLog(ArbitraryRecord.class);

	/**
	 * The binary data
	 */
	private byte[] data;

	/**
	 * Constructor
	 *
	 * @param type the biff code
	 * @param d	the data
	 */
	public ArbitraryRecord(int type, byte[] d)
	{
		super(Type.createType(type));

		data = d;
		logger.warn("ArbitraryRecord of type " + type + " created");
	}

	/**
	 * Retrieves the data to be written to the binary file
	 *
	 * @return the binary data
	 */
	public byte[] getData()
	{
		return data;
	}
}








