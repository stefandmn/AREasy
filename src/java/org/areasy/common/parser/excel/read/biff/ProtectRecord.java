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

import org.areasy.common.parser.excel.biff.IntegerHelper;
import org.areasy.common.parser.excel.biff.RecordData;

/**
 * A record detailing whether the sheet is protected
 */
class ProtectRecord extends RecordData
{
	/**
	 * Protected flag
	 */
	private boolean prot;

	/**
	 * Constructs this object from the raw data
	 *
	 * @param t the raw data
	 */
	ProtectRecord(Record t)
	{
		super(t);
		byte[] data = getRecord().getData();

		int protflag = IntegerHelper.getInt(data[0], data[1]);

		prot = (protflag == 1);
	}

	/**
	 * Returns the protected flag
	 *
	 * @return TRUE if this is protected, FALSE otherwise
	 */
	boolean isProtected()
	{
		return prot;
	}


}









