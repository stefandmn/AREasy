package org.areasy.common.parser.excel.biff;

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
 * The interface implemented by the various number and date format styles.
 * The methods on this interface are called internally when generating a
 * spreadsheet
 */
public interface DisplayFormat
{
	/**
	 * Accessor for the index style of this format
	 *
	 * @return the index for this format
	 */
	public int getFormatIndex();

	/**
	 * Accessor to see whether this format has been initialized
	 *
	 * @return TRUE if initialized, FALSE otherwise
	 */
	public boolean isInitialized();

	/**
	 * Initializes this format with the specified index number
	 *
	 * @param pos the position of this format record in the workbook
	 */
	public void initialize(int pos);

	/**
	 * Accessor to determine whether or not this format is built in
	 *
	 * @return TRUE if this format is a built in format, FALSE otherwise
	 */
	public boolean isBuiltIn();
}
