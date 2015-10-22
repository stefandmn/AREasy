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

import org.areasy.common.parser.excel.biff.Type;
import org.areasy.common.parser.excel.biff.WritableRecordData;

/**
 * Record containing the obj proj record
 */
class ObjProjRecord extends WritableRecordData
{
	/**
	 * The binary data
	 */
	private byte[] data;

	/**
	 * Constructor
	 */
	public ObjProjRecord()
	{
		super(Type.OBJPROJ);

		data = new byte[4];
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








