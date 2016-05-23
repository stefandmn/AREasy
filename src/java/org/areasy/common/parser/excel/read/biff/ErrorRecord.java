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

import org.areasy.common.parser.excel.CellType;
import org.areasy.common.parser.excel.ErrorCell;
import org.areasy.common.parser.excel.biff.FormattingRecords;

/**
 * A cell containing an error code.  This will usually be the result
 * of some error during the calculation of a formula
 */
class ErrorRecord extends CellValue implements ErrorCell
{
	/**
	 * The error code if this cell evaluates to an error, otherwise zer0
	 */
	private int errorCode;

	/**
	 * Constructs this object
	 *
	 * @param t  the raw data
	 * @param fr the formatting records
	 * @param si the sheet
	 */
	public ErrorRecord(Record t, FormattingRecords fr, DefaultSheet si)
	{
		super(t, fr, si);

		byte[] data = getRecord().getData();

		errorCode = data[6];
	}

	/**
	 * Interface method which gets the error code for this cell.  If this cell
	 * does not represent an error, then it returns 0.  Always use the
	 * method isError() to  determine this prior to calling this method
	 *
	 * @return the error code if this cell contains an error, 0 otherwise
	 */
	public int getErrorCode()
	{
		return errorCode;
	}

	/**
	 * Returns the numerical value as a string
	 *
	 * @return The numerical value of the formula as a string
	 */
	public String getContents()
	{
		return "ERROR " + errorCode;
	}

	/**
	 * Returns the cell type
	 *
	 * @return The cell type
	 */
	public CellType getType()
	{
		return CellType.ERROR;
	}
}

