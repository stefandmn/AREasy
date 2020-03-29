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

import org.areasy.common.logger.Logger;
import org.areasy.common.logger.LoggerFactory;
import org.areasy.common.parser.excel.CellType;
import org.areasy.common.parser.excel.WorkbookSettings;
import org.areasy.common.parser.excel.biff.DoubleHelper;
import org.areasy.common.parser.excel.biff.FormattingRecords;
import org.areasy.common.parser.excel.biff.IntegerHelper;
import org.areasy.common.parser.excel.biff.WorkbookMethods;
import org.areasy.common.parser.excel.biff.formula.ExternalSheet;
import org.areasy.common.parser.excel.common.Assert;

/**
 * A formula's last calculated value
 */
class FormulaRecord extends CellValue
{
	/**
	 * The logger
	 */
	private static Logger logger = LoggerFactory.getLog(FormulaRecord.class);

	/**
	 * The "real" formula record - will be either a string a or a number
	 */
	private CellValue formula;

	/**
	 * Flag to indicate whether this is a shared formula
	 */
	private boolean shared;

	/**
	 * Static class for a dummy override, indicating that the formula
	 * passed in is not a shared formula
	 */
	private static class IgnoreSharedFormula
	{
	}

	;
	public static final IgnoreSharedFormula ignoreSharedFormula
			= new IgnoreSharedFormula();

	/**
	 * Constructs this object from the raw data.  Creates either a
	 * NumberFormulaRecord or a StringFormulaRecord depending on whether
	 * this formula represents a numerical calculation or not
	 *
	 * @param t		 the raw data
	 * @param excelFile the excel file
	 * @param fr		the formatting records
	 * @param es		the workbook, which contains the external sheet references
	 * @param nt		the name table
	 * @param si		the sheet
	 * @param ws		the workbook settings
	 */
	public FormulaRecord(Record t,
						 File excelFile,
						 FormattingRecords fr,
						 ExternalSheet es,
						 WorkbookMethods nt,
						 DefaultSheet si,
						 WorkbookSettings ws)
	{
		super(t, fr, si);

		byte[] data = getRecord().getData();

		shared = false;

		// Check to see if this forms part of a shared formula
		int grbit = IntegerHelper.getInt(data[14], data[15]);
		if ((grbit & 0x08) != 0)
		{
			shared = true;

			if (data[6] == 0 && data[12] == -1 && data[13] == -1)
			{
				// It is a shared string formula
				formula = new SharedStringFormulaRecord
						(t, excelFile, fr, es, nt, si, ws);
			}
			else if (data[6] == 3 && data[12] == -1 && data[13] == -1)
			{
				// We have a string which evaluates to null
				formula = new SharedStringFormulaRecord
						(t, excelFile, fr, es, nt, si,
								SharedStringFormulaRecord.EMPTY_STRING);
			}
			else if (data[6] == 2 &&
					data[12] == -1 &&
					data[13] == -1)
			{
				// The cell is in error
				int errorCode = data[8];
				formula = new SharedErrorFormulaRecord(t, excelFile, errorCode,
						fr, es, nt, si);
			}
			else if (data[6] == 1 &&
					data[12] == -1 &&
					data[13] == -1)
			{
				boolean value = data[8] == 1 ? true : false;
				formula = new SharedBooleanFormulaRecord
						(t, excelFile, value, fr, es, nt, si);
			}
			else
			{
				// It is a numerical formula
				double value = DoubleHelper.getIEEEDouble(data, 6);
				SharedNumberFormulaRecord snfr = new SharedNumberFormulaRecord
						(t, excelFile, value, fr, es, nt, si);
				snfr.setNumberFormat(fr.getNumberFormat(getXFIndex()));
				formula = snfr;
			}

			return;
		}

		// microsoft and their goddam magic values determine whether this
		// is a string or a number value
		if (data[6] == 0 && data[12] == -1 && data[13] == -1)
		{
			// we have a string
			formula = new StringFormulaRecord(t, excelFile, fr, es, nt, si, ws);
		}
		else if (data[6] == 1 &&
				data[12] == -1 &&
				data[13] == -1)
		{
			// We have a boolean formula
			// multiple values.  Thanks to Frank for spotting this
			formula = new BooleanFormulaRecord(t, fr, es, nt, si);
		}
		else if (data[6] == 2 &&
				data[12] == -1 &&
				data[13] == -1)
		{
			// The cell is in error
			formula = new ErrorFormulaRecord(t, fr, es, nt, si);
		}
		else if (data[6] == 3 && data[12] == -1 && data[13] == -1)
		{
			// we have a string which evaluates to null
			formula = new StringFormulaRecord(t, fr, es, nt, si);
		}
		else
		{
			// it is most assuredly a number
			formula = new NumberFormulaRecord(t, fr, es, nt, si);
		}
	}

	/**
	 * Constructs this object from the raw data.  Creates either a
	 * NumberFormulaRecord or a StringFormulaRecord depending on whether
	 * this formula represents a numerical calculation or not
	 *
	 * @param t		 the raw data
	 * @param excelFile the excel file
	 * @param fr		the formatting records
	 * @param es		the workbook, which contains the external sheet references
	 * @param nt		the name table
	 * @param i		 a dummy override to indicate that we don't want to do
	 *                  any shared formula processing
	 * @param si		the sheet impl
	 * @param ws		the workbook settings
	 */
	public FormulaRecord(Record t,
						 File excelFile,
						 FormattingRecords fr,
						 ExternalSheet es,
						 WorkbookMethods nt,
						 IgnoreSharedFormula i,
						 DefaultSheet si,
						 WorkbookSettings ws)
	{
		super(t, fr, si);
		byte[] data = getRecord().getData();

		shared = false;

		// microsoft and their magic values determine whether this
		// is a string or a number value
		if (data[6] == 0 && data[12] == -1 && data[13] == -1)
		{
			// we have a string
			formula = new StringFormulaRecord(t, excelFile, fr, es, nt, si, ws);
		}
		else if (data[6] == 1 &&
				data[12] == -1 &&
				data[13] == -1)
		{
			// We have a boolean formula
			// multiple values.  Thanks to Frank for spotting this
			formula = new BooleanFormulaRecord(t, fr, es, nt, si);
		}
		else if (data[6] == 2 &&
				data[12] == -1 &&
				data[13] == -1)
		{
			// The cell is in error
			formula = new ErrorFormulaRecord(t, fr, es, nt, si);
		}
		else
		{
			// it is most assuredly a number
			formula = new NumberFormulaRecord(t, fr, es, nt, si);
		}
	}

	/**
	 * Returns the numerical value as a string
	 *
	 * @return The numerical value of the formula as a string
	 */
	public String getContents()
	{
		Assert.verify(false);
		return "";
	}

	/**
	 * Returns the cell type
	 *
	 * @return The cell type
	 */
	public CellType getType()
	{
		Assert.verify(false);
		return CellType.EMPTY;
	}

	/**
	 * Gets the "real" formula
	 *
	 * @return the cell value
	 */
	final CellValue getFormula()
	{
		return formula;
	}

	/**
	 * Interrogates this formula to determine if it forms part of a shared
	 * formula
	 *
	 * @return TRUE if this is shared formula, FALSE otherwise
	 */
	final boolean isShared()
	{
		return shared;
	}

}






