package org.areasy.common.parser.excel.biff.drawing;

/*
 * Copyright (c) 2007-2016 AREasy Runtime
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
 * Split menu colours escher record
 */
class SplitMenuColors extends EscherAtom
{
	/**
	 * The binary data
	 */
	private byte[] data;

	/**
	 * Constructor
	 *
	 * @param erd escher record data
	 */
	public SplitMenuColors(EscherRecordData erd)
	{
		super(erd);
	}

	/**
	 * Constructor
	 */
	public SplitMenuColors()
	{
		super(EscherRecordType.SPLIT_MENU_COLORS);
		setVersion(0);
		setInstance(4);

		data = new byte[]
				{(byte) 0x0d, (byte) 0x00, (byte) 0x00, (byte) 0x08,
						(byte) 0x0c, (byte) 0x00, (byte) 0x00, (byte) 0x08,
						(byte) 0x17, (byte) 0x00, (byte) 0x00, (byte) 0x08,
						(byte) 0xf7, (byte) 0x00, (byte) 0x00, (byte) 0x10};
	}

	/**
	 * The binary data
	 *
	 * @return the binary data
	 */
	byte[] getData()
	{
		return setHeaderData(data);
	}
}
