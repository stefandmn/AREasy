package org.areasy.common.parser.excel.biff;

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

import org.areasy.common.parser.excel.write.biff.File;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;

/**
 * Class containing the CONDFMT and CF records for conditionally formatting
 * a cell
 */
public class ConditionalFormat
{
	/**
	 * The range of the format
	 */
	private ConditionalFormatRangeRecord range;

	/**
	 * The format conditions
	 */
	private ArrayList conditions;

	/**
	 * Constructor
	 */
	public ConditionalFormat(ConditionalFormatRangeRecord cfrr)
	{
		range = cfrr;
		conditions = new ArrayList();
	}

	/**
	 * Adds a condition
	 *
	 * @param cond the condition
	 */
	public void addCondition(ConditionalFormatRecord cond)
	{
		conditions.add(cond);
	}

	/**
	 * Inserts a blank column into this spreadsheet.  If the column is out of
	 * range of the columns in the sheet, then no action is taken
	 *
	 * @param col the column to insert
	 */
	public void insertColumn(int col)
	{
		range.insertColumn(col);
	}

	/**
	 * Removes a column from this spreadsheet.  If the column is out of range
	 * of the columns in the sheet, then no action is taken
	 *
	 * @param col the column to remove
	 */
	public void removeColumn(int col)
	{
		range.removeColumn(col);
	}

	/**
	 * Removes a row from this spreadsheet.  If the row is out of
	 * range of the columns in the sheet, then no action is taken
	 *
	 * @param row the row to remove
	 */
	public void removeRow(int row)
	{
		range.removeRow(row);
	}

	/**
	 * Inserts a blank row into this spreadsheet.  If the row is out of range
	 * of the rows in the sheet, then no action is taken
	 *
	 * @param row the row to insert
	 */
	public void insertRow(int row)
	{
		range.insertRow(row);
	}

	/**
	 * Writes out the data validation
	 *
	 * @param outputFile the output file
	 * @throws IOException
	 */
	public void write(File outputFile) throws IOException
	{
		outputFile.write(range);

		for (Iterator i = conditions.iterator(); i.hasNext();)
		{
			ConditionalFormatRecord cfr = (ConditionalFormatRecord) i.next();
			outputFile.write(cfr);
		}
	}
}
