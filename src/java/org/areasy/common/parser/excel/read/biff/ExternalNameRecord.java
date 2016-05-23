package org.areasy.common.parser.excel.read.biff;

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

import org.areasy.common.logger.Logger;
import org.areasy.common.logger.LoggerFactory;
import org.areasy.common.parser.excel.WorkbookSettings;
import org.areasy.common.parser.excel.biff.IntegerHelper;
import org.areasy.common.parser.excel.biff.RecordData;
import org.areasy.common.parser.excel.biff.StringHelper;


/**
 * A row  record
 */
public class ExternalNameRecord extends RecordData
{
	/**
	 * The logger
	 */
	private static Logger logger = LoggerFactory.getLog(ExternalNameRecord.class);

	/**
	 * The name
	 */
	private String name;

	/**
	 * Add in function flag
	 */
	private boolean addInFunction;

	/**
	 * Constructs this object from the raw data
	 *
	 * @param t  the raw data
	 * @param ws the workbook settings
	 */
	ExternalNameRecord(Record t, WorkbookSettings ws)
	{
		super(t);

		byte[] data = getRecord().getData();
		int options = IntegerHelper.getInt(data[0], data[1]);

		if (options == 0)
		{
			addInFunction = true;
		}

		if (!addInFunction)
		{
			return;
		}

		int length = data[6];

		boolean unicode = (data[7] != 0);

		if (unicode)
		{
			name = StringHelper.getUnicodeString(data, length, 8);
		}
		else
		{
			name = StringHelper.getString(data, length, 8, ws);
		}
	}

	/**
	 * Queries whether this name record refers to an external record
	 *
	 * @return TRUE if this name record is an add in function, FALSE otherwise
	 */
	public boolean isAddInFunction()
	{
		return addInFunction;
	}

	/**
	 * Gets the name
	 *
	 * @return the name
	 */
	public String getName()
	{
		return name;
	}
}


