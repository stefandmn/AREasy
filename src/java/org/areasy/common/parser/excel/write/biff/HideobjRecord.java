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

import org.areasy.common.parser.excel.biff.IntegerHelper;
import org.areasy.common.parser.excel.biff.Type;
import org.areasy.common.parser.excel.biff.WritableRecordData;

/**
 * Stores options selected in the Options dialog box
 * <p/>
 * =2 if the Hide All option is turned on
 * =1 if the Show Placeholders option is turned on
 * =0 if the Show All option is turned on
 */
class HideobjRecord extends WritableRecordData
{
	/**
	 * Hide object mode
	 */
	private int hidemode;

	/**
	 * The binary data
	 */
	private byte[] data;

	/**
	 * Constructor
	 *
	 * @param newHideMode the hide all flag
	 */
	public HideobjRecord(int newHideMode)
	{
		super(Type.HIDEOBJ);

		hidemode = newHideMode;
		data = new byte[2];

		IntegerHelper.getTwoBytes(hidemode, data, 0);
	}

	/**
	 * Gets the binary data for output to file
	 *
	 * @return the binary data
	 */
	public byte[] getData()
	{
		return data;
	}
}
