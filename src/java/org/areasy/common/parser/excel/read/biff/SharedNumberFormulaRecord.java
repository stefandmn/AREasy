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

import org.areasy.common.logger.Logger;
import org.areasy.common.logger.LoggerFactory;
import org.areasy.common.parser.excel.CellType;
import org.areasy.common.parser.excel.NumberCell;
import org.areasy.common.parser.excel.NumberFormulaCell;
import org.areasy.common.parser.excel.biff.*;
import org.areasy.common.parser.excel.biff.formula.ExternalSheet;
import org.areasy.common.parser.excel.biff.formula.FormulaException;
import org.areasy.common.parser.excel.biff.formula.FormulaParser;

import java.text.DecimalFormat;
import java.text.NumberFormat;

/**
 * A number formula record, manufactured out of the Shared Formula
 * "optimization"
 */
public class SharedNumberFormulaRecord extends BaseSharedFormulaRecord
		implements NumberCell, FormulaData, NumberFormulaCell
{
	/**
	 * The logger
	 */
	private static Logger logger =
			LoggerFactory.getLog(SharedNumberFormulaRecord.class);
	/**
	 * The value of this number
	 */
	private double value;
	/**
	 * The cell format
	 */
	private NumberFormat format;
	/**
	 * A handle to the formatting records
	 */
	private FormattingRecords formattingRecords;

	/**
	 * The string format for the double value
	 */
	private static DecimalFormat defaultFormat = new DecimalFormat("#.###");

	/**
	 * Constructs this number
	 *
	 * @param t		 the data
	 * @param excelFile the excel biff data
	 * @param v		 the value
	 * @param fr		the formatting records
	 * @param es		the external sheet
	 * @param nt		the name table
	 * @param si		the sheet
	 */
	public SharedNumberFormulaRecord(Record t,
									 File excelFile,
									 double v,
									 FormattingRecords fr,
									 ExternalSheet es,
									 WorkbookMethods nt,
									 DefaultSheet si)
	{
		super(t, fr, es, nt, si, excelFile.getPos());
		value = v;
		format = defaultFormat;	// format is set up later from the
		// SharedFormulaRecord
	}

	/**
	 * Sets the format for the number based on the Excel spreadsheets' format.
	 * This is called from DefaultSheet when it has been definitely established
	 * that this cell is a number and not a date
	 *
	 * @param f the format
	 */
	final void setNumberFormat(NumberFormat f)
	{
		if (f != null)
		{
			format = f;
		}
	}

	/**
	 * Accessor for the value
	 *
	 * @return the value
	 */
	public double getValue()
	{
		return value;
	}

	/**
	 * Accessor for the contents as a string
	 *
	 * @return the value as a string
	 */
	public String getContents()
	{
		return !Double.isNaN(value) ? format.format(value) : "";
	}

	/**
	 * Accessor for the cell type
	 *
	 * @return the cell type
	 */
	public CellType getType()
	{
		return CellType.NUMBER_FORMULA;
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
		DoubleHelper.getIEEEBytes(value, data, 6);

		// Now copy in the parsed tokens
		System.arraycopy(rpnTokens, 0, data, 22, rpnTokens.length);
		IntegerHelper.getTwoBytes(rpnTokens.length, data, 20);

		// Lop off the standard information
		byte[] d = new byte[data.length - 6];
		System.arraycopy(data, 6, d, 0, data.length - 6);

		return d;
	}

	/**
	 * Gets the NumberFormat used to format this cell.  This is the java
	 * equivalent of the Excel format
	 *
	 * @return the NumberFormat used to format the cell
	 */
	public NumberFormat getNumberFormat()
	{
		return format;
	}
}









