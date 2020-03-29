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
 * Stores the number of addmen and delmenu groups in the book stream
 */
class MMSRecord extends WritableRecordData
{
	/**
	 * The number of menu items added
	 */
	private byte numMenuItemsAdded;
	/**
	 * The number of menu items deleted
	 */
	private byte numMenuItemsDeleted;
	/**
	 * The binary data
	 */
	private byte[] data;

	/**
	 * Constructor
	 *
	 * @param menuItemsAdded   the number of menu items added
	 * @param menuItemsDeleted the number of menu items deleted
	 */
	public MMSRecord(int menuItemsAdded, int menuItemsDeleted)
	{
		super(Type.MMS);

		numMenuItemsAdded = (byte) menuItemsAdded;
		numMenuItemsDeleted = (byte) menuItemsDeleted;

		data = new byte[2];

		data[0] = numMenuItemsAdded;
		data[1] = numMenuItemsDeleted;
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
