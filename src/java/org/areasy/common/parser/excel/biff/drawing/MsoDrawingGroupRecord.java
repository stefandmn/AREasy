package org.areasy.common.parser.excel.biff.drawing;

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

import org.areasy.common.parser.excel.biff.Type;
import org.areasy.common.parser.excel.biff.WritableRecordData;
import org.areasy.common.parser.excel.read.biff.Record;

/**
 * A record which merely holds the MSODRAWINGGROUP data.  Used when copying
 * files  which contain images
 */
public class MsoDrawingGroupRecord extends WritableRecordData
{
	/**
	 * The binary data
	 */
	private byte[] data;

	/**
	 * Constructs this object from the raw data
	 *
	 * @param t the raw data
	 */
	public MsoDrawingGroupRecord(Record t)
	{
		super(t);
		data = t.getData();
	}

	/**
	 * Constructor
	 *
	 * @param d the data
	 */
	MsoDrawingGroupRecord(byte[] d)
	{
		super(Type.MSODRAWINGGROUP);
		data = d;
	}

	/**
	 * Expose the protected function to the DefaultSheet in this package
	 *
	 * @return the raw record data
	 */
	public byte[] getData()
	{
		return data;
	}
}




