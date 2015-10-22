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

import org.areasy.common.parser.excel.CellType;
import org.areasy.common.parser.excel.ErrorCell;
import org.areasy.common.parser.excel.ErrorFormulaCell;
import org.areasy.common.parser.excel.biff.FormattingRecords;
import org.areasy.common.parser.excel.biff.FormulaData;
import org.areasy.common.parser.excel.biff.WorkbookMethods;
import org.areasy.common.parser.excel.biff.formula.ExternalSheet;
import org.areasy.common.parser.excel.biff.formula.FormulaErrorCode;
import org.areasy.common.parser.excel.biff.formula.FormulaException;
import org.areasy.common.parser.excel.biff.formula.FormulaParser;
import org.areasy.common.parser.excel.common.Assert;

/**
 * An error resulting from the calculation of a formula
 */
class ErrorFormulaRecord extends CellValue
		implements ErrorCell, FormulaData, ErrorFormulaCell
{
	/**
	 * The error code of this cell
	 */
	private int errorCode;

	/**
	 * A handle to the class needed to access external sheets
	 */
	private ExternalSheet externalSheet;

	/**
	 * A handle to the name table
	 */
	private WorkbookMethods nameTable;

	/**
	 * The formula as an excel string
	 */
	private String formulaString;

	/**
	 * The raw data
	 */
	private byte[] data;

	/**
	 * The error code
	 */
	private FormulaErrorCode error;

	/**
	 * Constructs this object from the raw data
	 *
	 * @param t  the raw data
	 * @param fr the formatting records
	 * @param es the external sheet
	 * @param nt the name table
	 * @param si the sheet
	 */
	public ErrorFormulaRecord(Record t, FormattingRecords fr, ExternalSheet es,
							  WorkbookMethods nt,
							  DefaultSheet si)
	{
		super(t, fr, si);

		externalSheet = es;
		nameTable = nt;
		data = getRecord().getData();

		Assert.verify(data[6] == 2);

		errorCode = data[8];
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
	 * Returns the cell type
	 *
	 * @return The cell type
	 */
	public CellType getType()
	{
		return CellType.FORMULA_ERROR;
	}

	/**
	 * Gets the raw bytes for the formula.  This will include the
	 * parsed tokens array
	 *
	 * @return the raw record data
	 */
	public byte[] getFormulaData() throws FormulaException
	{
		if (!getSheet().getWorkbookBof().isBiff8())
		{
			throw new FormulaException(FormulaException.BIFF8_SUPPORTED);
		}

		// Lop off the standard information
		byte[] d = new byte[data.length - 6];
		System.arraycopy(data, 6, d, 0, data.length - 6);

		return d;
	}

	/**
	 * Gets the formula as an excel string
	 *
	 * @return the formula as an excel string
	 * @throws FormulaException
	 */
	public String getFormula() throws FormulaException
	{
		if (formulaString == null)
		{
			byte[] tokens = new byte[data.length - 22];
			System.arraycopy(data, 22, tokens, 0, tokens.length);
			FormulaParser fp = new FormulaParser
					(tokens, this, externalSheet, nameTable,
							getSheet().getWorkbook().getSettings());
			fp.parse();
			formulaString = fp.getFormula();
		}

		return formulaString;
	}
}

