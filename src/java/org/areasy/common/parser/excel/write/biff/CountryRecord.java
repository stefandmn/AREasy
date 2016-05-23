package org.areasy.common.parser.excel.write.biff;

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

import org.areasy.common.parser.excel.biff.CountryCode;
import org.areasy.common.parser.excel.biff.IntegerHelper;
import org.areasy.common.parser.excel.biff.Type;
import org.areasy.common.parser.excel.biff.WritableRecordData;


/**
 * Record containing the localization information
 */
class CountryRecord extends WritableRecordData
{
	/**
	 * The user interface language
	 */
	private int language;

	/**
	 * The regional settings
	 */
	private int regionalSettings;

	/**
	 * Constructor
	 */
	public CountryRecord(CountryCode lang, CountryCode r)
	{
		super(Type.COUNTRY);

		language = lang.getValue();
		regionalSettings = r.getValue();
	}

	public CountryRecord(org.areasy.common.parser.excel.read.biff.CountryRecord cr)
	{
		super(Type.COUNTRY);

		language = cr.getLanguageCode();
		regionalSettings = cr.getRegionalSettingsCode();
	}

	/**
	 * Retrieves the data to be written to the binary file
	 *
	 * @return the binary data
	 */
	public byte[] getData()
	{
		byte[] data = new byte[4];

		IntegerHelper.getTwoBytes(language, data, 0);
		IntegerHelper.getTwoBytes(regionalSettings, data, 2);

		return data;
	}
}








