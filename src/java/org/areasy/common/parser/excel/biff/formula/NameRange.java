package org.areasy.common.parser.excel.biff.formula;

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

import org.areasy.common.logger.Logger;
import org.areasy.common.logger.LoggerFactory;
import org.areasy.common.parser.excel.biff.IntegerHelper;
import org.areasy.common.parser.excel.biff.NameRangeException;
import org.areasy.common.parser.excel.biff.WorkbookMethods;
import org.areasy.common.parser.excel.common.Assert;

/**
 * A name operand
 */
class NameRange extends Operand implements ParsedThing
{
	/**
	 * The logger
	 */
	private static Logger logger = LoggerFactory.getLog(NameRange.class);

	/**
	 * A handle to the name table
	 */
	private WorkbookMethods nameTable;

	/**
	 * The string name
	 */
	private String name;

	/**
	 * The index into the name table
	 */
	private int index;

	/**
	 * Constructor
	 */
	public NameRange(WorkbookMethods nt)
	{
		nameTable = nt;
		Assert.verify(nameTable != null);
	}

	/**
	 * Constructor when parsing a string via the api
	 *
	 * @param nm the name string
	 * @param nt the name table
	 */
	public NameRange(String nm, WorkbookMethods nt) throws FormulaException
	{
		name = nm;
		nameTable = nt;

		index = nameTable.getNameIndex(name);

		if (index < 0)
		{
			throw new FormulaException(FormulaException.CELL_NAME_NOT_FOUND, name);
		}

		index += 1; // indexes are 1-based
	}

	/**
	 * Reads the ptg data from the array starting at the specified position
	 *
	 * @param data the RPN array
	 * @param pos  the current position in the array, excluding the ptg identifier
	 * @return the number of bytes read
	 */
	public int read(byte[] data, int pos) throws FormulaException
	{
		try
		{
			index = IntegerHelper.getInt(data[pos], data[pos + 1]);

			name = nameTable.getName(index - 1); // ilbl is 1-based

			return 4;
		}
		catch (NameRangeException e)
		{
			throw new FormulaException(FormulaException.CELL_NAME_NOT_FOUND, "");
		}
	}

	/**
	 * Gets the token representation of this item in RPN
	 *
	 * @return the bytes applicable to this formula
	 */
	byte[] getBytes()
	{
		byte[] data = new byte[5];

		data[0] = Token.NAMED_RANGE.getValueCode();

		if (getParseContext() == ParseContext.DATA_VALIDATION)
		{
			data[0] = Token.NAMED_RANGE.getReferenceCode();
		}

		IntegerHelper.getTwoBytes(index, data, 1);

		return data;
	}

	/**
	 * Abstract method implementation to get the string equivalent of this
	 * token
	 *
	 * @param buf the string to append to
	 */
	public void getString(StringBuffer buf)
	{
		buf.append(name);
	}


	/**
	 * If this formula was on an imported sheet, check that
	 * cell references to another sheet are warned appropriately
	 * Flags the formula as invalid
	 */
	void handleImportedCellReferences()
	{
		setInvalid();
	}

}
