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

import org.areasy.common.parser.excel.biff.StringHelper;
import org.areasy.common.parser.excel.biff.Type;
import org.areasy.common.parser.excel.biff.WritableRecordData;

/**
 * Record which specifies sort dialog box values
 */
class SortRecord extends WritableRecordData
{
	private String column1Name;
	private String column2Name;
	private String column3Name;
	private boolean sortColumns;
	private boolean sortKey1Desc;
	private boolean sortKey2Desc;
	private boolean sortKey3Desc;
	private boolean sortCaseSensitive;

	/**
	 * Constructor
	 *
	 * @param a	Sort Column 1 Name
	 * @param b	Sort Column 2 Name
	 * @param c	Sort Column 3 Name
	 * @param sc   Sort Columns
	 * @param sk1d Sort Key 1 Descending
	 * @param sk2d Sort Key 2 Descending
	 * @param sk3d Sort Key 3 Descending
	 * @param scs  Sort Case Sensitive
	 */
	public SortRecord(String a, String b, String c,
					  boolean sc, boolean sk1d,
					  boolean sk2d, boolean sk3d, boolean scs)
	{
		super(Type.SORT);

		column1Name = a;
		column2Name = b;
		column3Name = c;
		sortColumns = sc;
		sortKey1Desc = sk1d;
		sortKey2Desc = sk2d;
		sortKey3Desc = sk3d;
		sortCaseSensitive = scs;
	}

	/**
	 * Gets the binary data for output to file
	 *
	 * @return the binary data
	 */
	public byte[] getData()
	{
		int byteCount = 5 + (column1Name.length() * 2) + 1;
		if (column2Name.length() > 0)
		{
			byteCount += (column2Name.length() * 2) + 1;
		}
		if (column3Name.length() > 0)
		{
			byteCount += (column3Name.length() * 2) + 1;
		}
		byte[] data = new byte[byteCount + 1];
		// there is supposed to be an extra "unused" byte at the end
		int optionFlag = 0;
		if (sortColumns)
		{
			optionFlag = optionFlag | 0x01;
		}
		if (sortKey1Desc)
		{
			optionFlag = optionFlag | 0x02;
		}
		if (sortKey2Desc)
		{
			optionFlag = optionFlag | 0x04;
		}
		if (sortKey3Desc)
		{
			optionFlag = optionFlag | 0x08;
		}
		if (sortCaseSensitive)
		{
			optionFlag = optionFlag | 0x10;
		}

		data[0] = (byte) optionFlag;
		// data[1] is an index for sorting by a list - not implemented
		data[2] = (byte) column1Name.length();
		data[3] = (byte) column2Name.length();
		data[4] = (byte) column3Name.length();
		// always write the headings in unicode
		data[5] = 0x01;
		StringHelper.getUnicodeBytes(column1Name, data, 6);
		int curPos = 6 + (column1Name.length() * 2);
		if (column2Name.length() > 0)
		{
			data[curPos++] = 0x01;
			StringHelper.getUnicodeBytes(column2Name, data, curPos);
			curPos += column2Name.length() * 2;
		}
		if (column3Name.length() > 0)
		{
			data[curPos++] = 0x01;
			StringHelper.getUnicodeBytes(column3Name, data, curPos);
			curPos += column3Name.length() * 2;
		}

		return data;
	}
}
