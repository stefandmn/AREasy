package org.areasy.common.parser.excel.biff.drawing;

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

/**
 * The SpGr escher atom
 */
class Spgr extends EscherAtom
{
	/**
	 * The binary data
	 */
	private byte[] data;

	/**
	 * Constructor
	 *
	 * @param erd the raw escher record data
	 */
	public Spgr(EscherRecordData erd)
	{
		super(erd);
	}

	/**
	 * Constructor
	 */
	public Spgr()
	{
		super(EscherRecordType.SPGR);
		setVersion(1);
		data = new byte[16];
	}

	/**
	 * Gets the binary data
	 *
	 * @return the binary data
	 */
	byte[] getData()
	{
		return setHeaderData(data);
	}
}
