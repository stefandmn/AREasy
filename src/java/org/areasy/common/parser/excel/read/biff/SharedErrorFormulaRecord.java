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
import org.areasy.common.parser.excel.CellType;
import org.areasy.common.parser.excel.ErrorCell;
import org.areasy.common.parser.excel.ErrorFormulaCell;
import org.areasy.common.parser.excel.biff.FormattingRecords;
import org.areasy.common.parser.excel.biff.FormulaData;
import org.areasy.common.parser.excel.biff.IntegerHelper;
import org.areasy.common.parser.excel.biff.WorkbookMethods;
import org.areasy.common.parser.excel.biff.formula.ExternalSheet;
import org.areasy.common.parser.excel.biff.formula.FormulaErrorCode;
import org.areasy.common.parser.excel.biff.formula.FormulaException;
import org.areasy.common.parser.excel.biff.formula.FormulaParser;

/**
 * A number formula record, manufactured out of the Shared Formula
 * "optimization"
 */
public class SharedErrorFormulaRecord extends BaseSharedFormulaRecord
		implements ErrorCell, FormulaData, ErrorFormulaCell
{
	/**
	 * The logger
	 */
	private static Logger logger =
			LoggerFactory.getLog(SharedErrorFormulaRecord.class);

	/**
	 * The error code of this cell
	 */
	private int errorCode;

	/**
	 * The raw data
	 */
	private byte[] data;

	/**
	 * The error code
	 */
	private FormulaErrorCode error;

	/**
	 * Constructs this number
	 *
	 * @param t		 the data
	 * @param excelFile the excel biff data
	 * @param v		 the errorCode
	 * @param fr		the formatting records
	 * @param es		the external sheet
	 * @param nt		the name table
	 * @param si		the sheet
	 */
	public SharedErrorFormulaRecord(Record t,
									File excelFile,
									int ec,
									FormattingRecords fr,
									ExternalSheet es,
									WorkbookMethods nt,
									DefaultSheet si)
	{
		super(t, fr, es, nt, si, excelFile.getPos());
		errorCode = ec;
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
		if (error == null)
		{
			error = FormulaErrorCode.getErrorCode(errorCode);
		}

		return error != FormulaErrorCode.UNKNOWN ?
				error.getDescription() : "ERROR " + errorCode;
	}

	/**
	 * Accessor for the cell type
	 *
	 * @return the cell type
	 */
	public CellType getType()
	{
		return CellType.FORMULA_ERROR;
	}

	/**
	 * Gets the raw bytes for the formula.  This will include the
	 * parsed tokens array.  Used when copying spreadsheets
	 *
	 * @return the raw record data
	 * @throws FormulaException
	 */
	public byte[] getFormulaData() throws FormulaException
	{
		if (!getSheet().getWorkbookBof().isBiff8())
		{
			throw new FormulaException(FormulaException.BIFF8_SUPPORTED);
		}

		// Get the tokens, taking into account the mapping from shared
		// formula specific values into normal values
		FormulaParser fp = new FormulaParser
				(getTokens(), this,
						getExternalSheet(), getNameTable(),
						getSheet().getWorkbook().getSettings());
		fp.parse();
		byte[] rpnTokens = fp.getBytes();

		byte[] data = new byte[rpnTokens.length + 22];

		// Set the standard info for this cell
		IntegerHelper.getTwoBytes(getRow(), data, 0);
		IntegerHelper.getTwoBytes(getColumn(), data, 2);
		IntegerHelper.getTwoBytes(getXFIndex(), data, 4);

		data[6] = (byte) 0x02; // indicates this cell is an error value
		data[8] = (byte) errorCode;
		data[12] = (byte) 0xff;
		data[13] = (byte) 0xff;

		// Now copy in the parsed tokens
		System.arraycopy(rpnTokens, 0, data, 22, rpnTokens.length);
		IntegerHelper.getTwoBytes(rpnTokens.length, data, 20);

		// Lop off the standard information
		byte[] d = new byte[data.length - 6];
		System.arraycopy(data, 6, d, 0, data.length - 6);

		return d;
	}
}







