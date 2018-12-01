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

import org.areasy.common.parser.excel.biff.Type;
import org.areasy.common.parser.excel.biff.WritableRecordData;

/**
 * Marks the beginning of the user interface record
 */
class InterfaceHeaderRecord extends WritableRecordData
{
	/**
	 * Constructor
	 */
	public InterfaceHeaderRecord()
	{
		super(Type.INTERFACEHDR);
	}

	/**
	 * Gets the binary data
	 *
	 * @return the binary data
	 */
	public byte[] getData()
	{
		// Return the character encoding
		byte[] data = new byte[]
				{(byte) 0xb0, (byte) 0x04};
		return data;
	}
}


