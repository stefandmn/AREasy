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

import org.areasy.common.parser.excel.biff.IntegerHelper;

/**
 * Indicates that the function doesn't evaluate to a constant reference
 */
class MemArea extends SubExpression
{
	/**
	 * Constructor
	 */
	public MemArea()
	{
	}

	public void getString(StringBuffer buf)
	{
		ParseItem[] subExpression = getSubExpression();

		if (subExpression.length == 1)
		{
			subExpression[0].getString(buf);
		}
		else if (subExpression.length == 2)
		{
			subExpression[1].getString(buf);
			buf.append(':');
			subExpression[0].getString(buf);
		}
	}

	/**
	 * Reads the ptg data from the array starting at the specified position
	 *
	 * @param data the RPN array
	 * @param pos  the current position in the array, excluding the ptg identifier
	 * @return the number of bytes read
	 */
	public int read(byte[] data, int pos)
	{
		// For mem areas, the first four bytes are not used
		setLength(IntegerHelper.getInt(data[pos + 4], data[pos + 5]));
		return 6;
	}

	/**
	 * If this formula was on an imported sheet, check that
	 * cell references to another sheet are warned appropriately
	 * Does nothing
	 */
	void handleImportedCellReferences()
	{
	}
}

