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

/**
 * A boolean operand in a formula
 */
class BooleanValue extends Operand implements ParsedThing
{
	/**
	 * The boolean value
	 */
	private boolean value;

	/**
	 * Constructor
	 */
	public BooleanValue()
	{
	}

	/**
	 * Constructor used when parsing a string formula
	 *
	 * @param s the string token, including quote marks
	 */
	public BooleanValue(String s)
	{
		// remove the quotes
		value = Boolean.valueOf(s).booleanValue();
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
		value = data[pos] == 1 ? true : false;
		return 1;
	}

	/**
	 * Gets the token representation of this item in RPN
	 *
	 * @return the bytes applicable to this formula
	 */
	byte[] getBytes()
	{
		byte[] data = new byte[2];
		data[0] = Token.BOOL.getCode();
		data[1] = (byte) (value == true ? 1 : 0);

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
		buf.append((new Boolean(value)).toString());
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
