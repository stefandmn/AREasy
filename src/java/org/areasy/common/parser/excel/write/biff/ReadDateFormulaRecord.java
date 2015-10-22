package org.areasy.common.parser.excel.write.biff;

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

import org.areasy.common.parser.excel.DateFormulaCell;
import org.areasy.common.parser.excel.biff.FormulaData;

import java.text.DateFormat;
import java.util.Date;

/**
 * Class for read number formula records
 */
class ReadDateFormulaRecord extends ReadFormulaRecord
		implements DateFormulaCell
{
	/**
	 * Constructor
	 *
	 * @param f
	 */
	public ReadDateFormulaRecord(FormulaData f)
	{
		super(f);
	}

	/**
	 * Gets the Date contents for this cell.
	 *
	 * @return the cell contents
	 */
	public Date getDate()
	{
		return ((DateFormulaCell) getReadFormula()).getDate();
	}

	/**
	 * Indicates whether the date value contained in this cell refers to a date,
	 * or merely a time
	 *
	 * @return TRUE if the value refers to a time
	 */
	public boolean isTime()
	{
		return ((DateFormulaCell) getReadFormula()).isTime();
	}


	/**
	 * Gets the DateFormat used to format this cell.  This is the java
	 * equivalent of the Excel format
	 *
	 * @return the DateFormat used to format the cell
	 */
	public DateFormat getDateFormat()
	{
		return ((DateFormulaCell) getReadFormula()).getDateFormat();
	}
}
