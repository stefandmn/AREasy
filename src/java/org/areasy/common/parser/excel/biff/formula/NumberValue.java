package org.areasy.common.parser.excel.biff.formula;

/*
 * Copyright (c) 2007-2018 AREasy Runtime
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
 * A constant numerical value in a formula
 */
abstract class NumberValue extends Operand implements ParsedThing
{
	protected NumberValue()
	{
	}

	public abstract double getValue();

	public void getString(StringBuffer buf)
	{
		buf.append(Double.toString(getValue()));
	}
}
