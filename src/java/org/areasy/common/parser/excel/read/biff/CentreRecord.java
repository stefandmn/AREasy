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

/**
 * Record which indicates the whether the horizontal center option has been set
 */
class CentreRecord extends RecordData
{
	/**
	 * The centre flag
	 */
	private boolean centre;

	/**
	 * Constructor
	 *
	 * @param t the record to constructfrom
	 */
	public CentreRecord(Record t)
	{
		super(t);
		byte[] data = getRecord().getData();
		centre = IntegerHelper.getInt(data[0], data[1]) != 0;
	}

	/**
	 * Accessor for the centre flag
	 *
	 * @return Returns the centre flag.
	 */
	public boolean isCentre()
	{
		return centre;
	}
}
