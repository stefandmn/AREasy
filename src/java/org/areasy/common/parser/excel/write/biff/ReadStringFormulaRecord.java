package org.areasy.common.parser.excel.write.biff;

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
import org.areasy.common.parser.excel.StringFormulaCell;
import org.areasy.common.parser.excel.biff.FormulaData;
import org.areasy.common.parser.excel.biff.IntegerHelper;
import org.areasy.common.parser.excel.biff.formula.FormulaException;
import org.areasy.common.parser.excel.biff.formula.FormulaParser;
import org.areasy.common.parser.excel.common.Assert;

/**
 * Class for read number formula records
 */
class ReadStringFormulaRecord extends ReadFormulaRecord
		implements StringFormulaCell
{
	// the logger
	private static Logger logger = LoggerFactory.getLog(ReadFormulaRecord.class);

	/**
	 * Constructor
	 *
	 * @param f
	 */
	public ReadStringFormulaRecord(FormulaData f)
	{
		super(f);
	}

	/**
	 * Gets the string contents for this cell.
	 *
	 * @return the cell contents
	 */
	public String getString()
	{
		return ((StringFormulaCell) getReadFormula()).getString();
	}

	/**
	 * String formula specific exception handling.  Can't really create
	 * a formula (as it will look for a cell of that name, so just
	 * create a STRING record containing the contents
	 *
	 * @return the bodged data
	 */
	protected byte[] handleFormulaException()
	{
		byte[] expressiondata = null;
		byte[] celldata = super.getCellData();

		// Generate an appropriate dummy formula
		DefaultWritableWorkbook w = getSheet().getWorkbook();
		FormulaParser parser = new FormulaParser("\"" + getContents() + "\"", w, w,
				w.getSettings());

		// Get the bytes for the dummy formula
		try
		{
			parser.parse();
		}
		catch (FormulaException e2)
		{
			logger.warn(e2.getMessage());
			parser = new FormulaParser("\"ERROR\"", w, w, w.getSettings());
			try
			{
				parser.parse();
			}
			catch (FormulaException e3)
			{
				Assert.verify(false);
			}
		}
		byte[] formulaBytes = parser.getBytes();
		expressiondata = new byte[formulaBytes.length + 16];
		IntegerHelper.getTwoBytes(formulaBytes.length, expressiondata, 14);
		System.arraycopy(formulaBytes, 0, expressiondata, 16,
				formulaBytes.length);

		// Set the recalculate on load bit
		expressiondata[8] |= 0x02;

		byte[] data = new byte[celldata.length +
				expressiondata.length];
		System.arraycopy(celldata, 0, data, 0, celldata.length);
		System.arraycopy(expressiondata, 0, data,
				celldata.length, expressiondata.length);

		// Set the type bits to indicate a string formula
		data[6] = 0;
		data[12] = -1;
		data[13] = -1;

		return data;
	}
}
