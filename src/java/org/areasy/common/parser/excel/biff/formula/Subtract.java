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

/**
 * A cell reference in a formula
 */
class Subtract extends BinaryOperator implements ParsedThing
{
	/**
	 * Constructor
	 */
	public Subtract()
	{
	}

	public String getSymbol()
	{
		return "-";
	}

	/**
	 * Abstract method which gets the token for this operator
	 *
	 * @return the string symbol for this token
	 */
	Token getToken()
	{
		return Token.SUBTRACT;
	}

	/**
	 * Gets the precedence for this operator.  Operator precedents run from
	 * 1 to 5, one being the highest, 5 being the lowest
	 *
	 * @return the operator precedence
	 */
	int getPrecedence()
	{
		return 4;
	}
}
