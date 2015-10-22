package org.areasy.common.parser.excel.read.biff;

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

import org.areasy.common.logger.Logger;
import org.areasy.common.logger.LoggerFactory;
import org.areasy.common.parser.excel.biff.IntegerHelper;
import org.areasy.common.parser.excel.biff.RecordData;

/**
 * Contains the cell dimensions of this worksheet
 */
public class CountryRecord extends RecordData
{
	/**
	 * The logger
	 */
	private static Logger logger = LoggerFactory.getLog(CountryRecord.class);

	/**
	 * The user interface language
	 */
	private int language;

	/**
	 * The regional settings
	 */
	private int regionalSettings;

	/**
	 * Constructs the dimensions from the raw data
	 *
	 * @param t the raw data
	 */
	public CountryRecord(Record t)
	{
		super(t);
		byte[] data = t.getData();

		language = IntegerHelper.getInt(data[0], data[1]);
		regionalSettings = IntegerHelper.getInt(data[2], data[3]);
	}

	/**
	 * Accessor for the language code
	 *
	 * @return the language code
	 */
	public int getLanguageCode()
	{
		return language;
	}

	/**
	 * Accessor for the regional settings code
	 *
	 * @return the regional settings code
	 */
	public int getRegionalSettingsCode()
	{
		return regionalSettings;
	}

}







