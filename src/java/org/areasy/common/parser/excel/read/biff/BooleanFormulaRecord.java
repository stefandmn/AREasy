package org.areasy.common.parser.excel.read.biff;

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

import org.areasy.common.parser.excel.BooleanCell;
import org.areasy.common.parser.excel.BooleanFormulaCell;
import org.areasy.common.parser.excel.CellType;
import org.areasy.common.parser.excel.biff.FormattingRecords;
import org.areasy.common.parser.excel.biff.FormulaData;
import org.areasy.common.parser.excel.biff.WorkbookMethods;
import org.areasy.common.parser.excel.biff.formula.ExternalSheet;
import org.areasy.common.parser.excel.biff.formula.FormulaException;
import org.areasy.common.parser.excel.biff.formula.FormulaParser;
import org.areasy.common.parser.excel.common.Assert;

/**
 * A boolean formula's last calculated value
 */
class BooleanFormulaRecord extends CellValue
		implements BooleanCell, FormulaData, BooleanFormulaCell
{
	/**
	 * The boolean value of this cell.  If this cell represents an error,
	 * this will be false
	 */
	private boolean value;

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
	 * Constructs this object from the raw data
	 *
	 * @param t  the raw data
	 * @param fr the formatting records
	 * @param si the sheet
	 * @param es the sheet
	 * @param nt the name table
	 */
	public BooleanFormulaRecord(Record t, FormattingRecords fr,
								ExternalSheet es, WorkbookMethods nt,
								DefaultSheet si)
	{
		super(t, fr, si);
		externalSheet = es;
		nameTable = nt;
		value = false;

		data = getRecord().getData();

		Assert.verify(data[6] != 2);

		value = data[8] == 1 ? true : false;
	}

	/**
	 * Interface method which Gets the boolean value stored in this cell.  If
	 * this cell contains an error, then returns FALSE.  Always query this cell
	 * type using the accessor method isError() prior to calling this method
	 *
	 * @return TRUE if this cell contains TRUE, FALSE if it contains FALSE or
	 *         an error code
	 */
	public boolean getValue()
	{
		return value;
	}

	/**
	 * Returns the numerical value as a string
	 *
	 * @return The numerical value of the formula as a string
	 */
	public String getContents()
	{
		// return Boolean.toString(value) - only available in 1.4 or later
		return (new Boolean(value)).toString();
	}

	/**
	 * Returns the cell type
	 *
	 * @return The cell type
	 */
	public CellType getType()
	{
		return CellType.BOOLEAN_FORMULA;
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

