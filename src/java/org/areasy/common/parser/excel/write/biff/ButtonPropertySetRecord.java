package org.areasy.common.parser.excel.write.biff;

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

import org.areasy.common.parser.excel.biff.Type;
import org.areasy.common.parser.excel.biff.WritableRecordData;

/**
 * Any arbitrary excel record.  Used during development only
 */
class ButtonPropertySetRecord extends WritableRecordData
{
	/**
	 * The binary data
	 */
	private byte[] data;

	/**
	 * Constructor
	 */
	public ButtonPropertySetRecord(org.areasy.common.parser.excel.read.biff.ButtonPropertySetRecord bps)
	{
		super(Type.BUTTONPROPERTYSET);

		data = bps.getData();
	}

	/**
	 * Constructor
	 */
	public ButtonPropertySetRecord(ButtonPropertySetRecord bps)
	{
		super(Type.BUTTONPROPERTYSET);

		data = bps.getData();
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








