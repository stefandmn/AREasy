package org.areasy.common.parser.excel.biff;

/*
 * Copyright (c) 2007-2020 AREasy Runtime
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
import org.areasy.common.parser.excel.read.biff.Record;

/**
 * Range information for conditional formatting
 */
public class AutoFilterInfoRecord extends WritableRecordData
{
	// The logger
	private static Logger logger = LoggerFactory.getLog(AutoFilterInfoRecord.class);

	/**
	 * The data
	 */
	private byte[] data;


	/**
	 * Constructor
	 */
	public AutoFilterInfoRecord(Record t)
	{
		super(t);

		data = getRecord().getData();
	}

	/**
	 * Retrieves the data for output to binary file
	 *
	 * @return the data to be written
	 */
	public byte[] getData()
	{
		return data;
	}
}

