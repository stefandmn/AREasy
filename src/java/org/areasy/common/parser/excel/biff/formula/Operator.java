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

import java.util.Stack;

/**
 * An operator is a node in a parse tree.  Its children can be other
 * operators or operands
 * Arithmetic operators and functions are all considered operators
 */
abstract class Operator extends ParseItem
{
	/**
	 * The items which this operator manipulates. There will be at most two
	 */
	private ParseItem[] operands;

	/**
	 * Constructor
	 */
	public Operator()
	{
		operands = new ParseItem[0];
	}

	/**
	 * Tells the operands to use the alternate code
	 */
	protected void setOperandAlternateCode()
	{
		for (int i = 0; i < operands.length; i++)
		{
			operands[i].setAlternateCode();
		}
	}

	/**
	 * Adds operands to this item
	 */
	protected void add(ParseItem n)
	{
		n.setParent(this);

		// Grow the array
		ParseItem[] newOperands = new ParseItem[operands.length + 1];
		System.arraycopy(operands, 0, newOperands, 0, operands.length);
		newOperands[operands.length] = n;
		operands = newOperands;
	}

	/**
	 * Gets the operands for this operator from the stack
	 */
	public abstract void getOperands(Stack s);

	/**
	 * Gets the operands ie. the children of the node
	 */
	protected ParseItem[] getOperands()
	{
		return operands;
	}

	/**
	 * Gets the precedence for this operator.  Operator precedents run from
	 * 1 to 5, one being the highest, 5 being the lowest
	 *
	 * @return the operator precedence
	 */
	abstract int getPrecedence();

}
