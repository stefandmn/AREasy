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

import org.areasy.common.parser.excel.biff.IntegerHelper;

import java.util.Stack;

/**
 * Base class for those tokens which encapsulate a subexpression
 */
abstract class SubExpression extends Operand implements ParsedThing
{
	/**
	 * The number of bytes in the subexpression
	 */
	private int length;

	/**
	 * The sub expression
	 */
	private ParseItem[] subExpression;

	/**
	 * Constructor
	 */
	protected SubExpression()
	{
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
		length = IntegerHelper.getInt(data[pos], data[pos + 1]);
		return 2;
	}

	/**
	 * Gets the operands for this operator from the stack
	 */
	public void getOperands(Stack s)
	{
	}

	/**
	 * Gets the token representation of this item in RPN.  The Attribute
	 * token is a special case, which overrides anything useful we could do
	 * in the base class
	 *
	 * @return the bytes applicable to this formula
	 */
	byte[] getBytes()
	{
		return null;
	}


	/**
	 * Gets the precedence for this operator.  Operator precedents run from
	 * 1 to 5, one being the highest, 5 being the lowest
	 *
	 * @return the operator precedence
	 */
	int getPrecedence()
	{
		return 5;
	}

	/**
	 * Accessor for the length
	 *
	 * @return the length of the subexpression
	 */
	public int getLength()
	{
		return length;
	}

	protected final void setLength(int l)
	{
		length = l;
	}

	public void setSubExpression(ParseItem[] pi)
	{
		subExpression = pi;
	}

	protected ParseItem[] getSubExpression()
	{
		return subExpression;
	}
}
