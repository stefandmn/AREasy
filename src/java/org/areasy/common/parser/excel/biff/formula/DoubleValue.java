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
import org.areasy.common.parser.excel.biff.DoubleHelper;

/**
 * A cell reference in a formula
 */
class DoubleValue extends NumberValue implements ParsedThing
{
	/**
	 * The logger
	 */
	private static Logger logger = LoggerFactory.getLog(DoubleValue.class);

	/**
	 * The value of this double in the formula
	 */
	private double value;

	/**
	 * Constructor
	 */
	public DoubleValue()
	{
	}

	/**
	 * Constructor - invoked when writing an integer value that's out
	 * of range for a short
	 *
	 * @param v the double value
	 */
	DoubleValue(double v)
	{
		value = v;
	}

	/**
	 * Constructor for a double value when reading from a string
	 *
	 * @param s the string representation of this token
	 */
	public DoubleValue(String s)
	{
		try
		{
			value = Double.parseDouble(s);
		}
		catch (NumberFormatException e)
		{
			logger.warn(e, e);
			value = 0;
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
		value = DoubleHelper.getIEEEDouble(data, pos);

		return 8;
	}

	/**
	 * Gets the token representation of this item in RPN
	 *
	 * @return the bytes applicable to this formula
	 */
	byte[] getBytes()
	{
		byte[] data = new byte[9];
		data[0] = Token.DOUBLE.getCode();

		DoubleHelper.getIEEEBytes(value, data, 1);

		return data;
	}

	/**
	 * The double value
	 *
	 * @return the value
	 */
	public double getValue()
	{
		return value;
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
