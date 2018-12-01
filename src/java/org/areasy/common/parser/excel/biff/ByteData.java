package org.areasy.common.parser.excel.biff;

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

/**
 * Interface which provides a method for transferring chunks of binary
 * data from one Excel file (read in) to another (written out)
 */
public interface ByteData
{
	/**
	 * Used when writing out records
	 *
	 * @return the full data to be included in the final compound file
	 */
	public byte[] getBytes();
}
