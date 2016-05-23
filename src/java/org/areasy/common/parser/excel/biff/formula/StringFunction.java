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
import org.areasy.common.parser.excel.WorkbookSettings;

/**
 * Class used to hold a function when reading it in from a string.  At this
 * stage it is unknown whether it is a BuiltInFunction or a VariableArgFunction
 */
class StringFunction extends StringParseItem
{
	/**
	 * The logger
	 */
	private static Logger logger = LoggerFactory.getLog(StringFunction.class);

	/**
	 * The function
	 */
	private Function function;

	/**
	 * The function string
	 */
	private String functionString;

	/**
	 * Constructor
	 *
	 * @param s the lexically parsed stirng
	 */
	StringFunction(String s)
	{
		functionString = s.substring(0, s.length() - 1);
	}

	/**
	 * Accessor for the function
	 *
	 * @param ws the workbook settings
	 * @return the function
	 */
	Function getFunction(WorkbookSettings ws)
	{
		if (function == null)
		{
			function = Function.getFunction(functionString, ws);
		}
		return function;
	}
}
