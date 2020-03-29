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
 * Ambiguously defined plus operator, used as a place holder when parsing
 * string formulas.  At this stage it could be either
 * a unary or binary operator - the string parser will deduce which and
 * create the appropriate type
 */
class Plus extends StringOperator
{
	/**
	 * Constructor
	 */
	public Plus()
	{
		super();
	}

	/**
	 * Abstract method which gets the binary version of this operator
	 */
	Operator getBinaryOperator()
	{
		return new Add();
	}

	/**
	 * Abstract method which gets the unary version of this operator
	 */
	Operator getUnaryOperator()
	{
		return new UnaryPlus();
	}

	/**
	 * If this formula was on an imported sheet, check that
	 * cell references to another sheet are warned appropriately
	 * Does nothing, as operators don't have cell references
	 */
	void handleImportedCellReferences()
	{
	}

}
