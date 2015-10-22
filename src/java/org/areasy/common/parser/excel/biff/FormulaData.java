package org.areasy.common.parser.excel.biff;

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

import org.areasy.common.parser.excel.Cell;
import org.areasy.common.parser.excel.biff.formula.FormulaException;

/**
 * Interface which is used for copying formulas from a read only
 * to a writable spreadsheet
 */
public interface FormulaData extends Cell
{
	/**
	 * Gets the raw bytes for the formula.  This will include the
	 * parsed tokens array EXCLUDING the standard cell information
	 * (row, column, xfindex)
	 *
	 * @return the raw record data
	 * @throws FormulaException
	 */
	public byte[] getFormulaData() throws FormulaException;
}
