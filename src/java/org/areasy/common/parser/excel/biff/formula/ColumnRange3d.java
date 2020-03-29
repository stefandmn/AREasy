package org.areasy.common.parser.excel.biff.formula;

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
import org.areasy.common.parser.excel.biff.CellReferenceHelper;
import org.areasy.common.parser.excel.common.Assert;

/**
 * A nested class to hold range information
 */
class ColumnRange3d extends Area3d
{
	/**
	 * The logger
	 */
	private static Logger logger = LoggerFactory.getLog(ColumnRange3d.class);

	/**
	 * A handle to the workbook
	 */
	private ExternalSheet workbook;

	/**
	 * The sheet number
	 */
	private int sheet;

	/**
	 * Constructor
	 *
	 * @param es the external sheet
	 */
	ColumnRange3d(ExternalSheet es)
	{
		super(es);
		workbook = es;
	}

	/**
	 * Constructor invoked when parsing a string formula
	 *
	 * @param s  the string to parse
	 * @param es the external sheet
	 * @throws FormulaException
	 */
	ColumnRange3d(String s, ExternalSheet es) throws FormulaException
	{
		super(es);
		workbook = es;
		int seppos = s.lastIndexOf(":");
		Assert.verify(seppos != -1);
		String startcell = s.substring(0, seppos);
		String endcell = s.substring(seppos + 1);

		// Get the the start cell details
		int sep = s.indexOf('!');
		String cellString = s.substring(sep + 1, seppos);
		int columnFirst = CellReferenceHelper.getColumn(cellString);
		int rowFirst = 0;

		// Get the sheet index
		String sheetName = s.substring(0, sep);
		int sheetNamePos = sheetName.lastIndexOf(']');

		// Remove single quotes, if they exist
		if (sheetName.charAt(0) == '\'' &&
				sheetName.charAt(sheetName.length() - 1) == '\'')
		{
			sheetName = sheetName.substring(1, sheetName.length() - 1);
		}

		sheet = es.getExternalSheetIndex(sheetName);

		if (sheet < 0)
		{
			throw new FormulaException(FormulaException.SHEET_REF_NOT_FOUND,
					sheetName);
		}

		// Get the last cell index
		int columnLast = CellReferenceHelper.getColumn(endcell);
		int rowLast = 0xffff;

		boolean columnFirstRelative = true;
		boolean rowFirstRelative = true;
		boolean columnLastRelative = true;
		boolean rowLastRelative = true;

		setRangeData(sheet, columnFirst, columnLast, rowFirst, rowLast,
				columnFirstRelative, rowFirstRelative,
				columnLastRelative, rowLastRelative);
	}

	/**
	 * Gets the string representation of this column range
	 *
	 * @param buf the string buffer to append to
	 */
	public void getString(StringBuffer buf)
	{
		buf.append('\'');
		buf.append(workbook.getExternalSheetName(sheet));
		buf.append('\'');
		buf.append('!');

		CellReferenceHelper.getColumnReference(getFirstColumn(), buf);
		buf.append(':');
		CellReferenceHelper.getColumnReference(getLastColumn(), buf);
	}
}
