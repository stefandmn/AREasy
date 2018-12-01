package org.areasy.common.parser.excel.read.biff;

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
import org.areasy.common.parser.excel.biff.RecordData;
import org.areasy.common.parser.excel.biff.Type;

/**
 * A password record
 */
class PasswordRecord extends RecordData
{
	/**
	 * The password
	 */
	private String password;
	/**
	 * The binary data
	 */
	private int passwordHash;

	/**
	 * Constructor
	 *
	 * @param t the raw bytes
	 */
	public PasswordRecord(Record t)
	{
		super(Type.PASSWORD);

		byte[] data = t.getData();
		passwordHash = IntegerHelper.getInt(data[0], data[1]);
	}

	/**
	 * Gets the binary data for output to file
	 *
	 * @return the password hash
	 */
	public int getPasswordHash()
	{
		return passwordHash;
	}
}
