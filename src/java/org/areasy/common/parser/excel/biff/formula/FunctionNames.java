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

import org.areasy.common.logger.Logger;
import org.areasy.common.logger.LoggerFactory;

import java.util.HashMap;
import java.util.Locale;
import java.util.ResourceBundle;

/**
 * A class which contains the function names for the current workbook. The
 * function names can potentially vary from workbook to workbook depending
 * on the locale
 */
public class FunctionNames
{
	/**
	 * The logger class
	 */
	private static Logger logger = LoggerFactory.getLog(FunctionNames.class);

	/**
	 * A hash mapping keyed on the function and returning its locale specific
	 * name
	 */
	private HashMap names;

	/**
	 * A hash mapping keyed on the locale specific name and returning the
	 * function
	 */
	private HashMap functions;

	/**
	 * Constructor
	 *
	 * @param l the locale
	 */
	public FunctionNames(Locale l)
	{
		ResourceBundle rb = ResourceBundle.getBundle("functions", l);
		Function[] allfunctions = Function.getFunctions();
		names = new HashMap(allfunctions.length);
		functions = new HashMap(allfunctions.length);

		// Iterate through all the functions, adding them to the hash maps
		Function f = null;
		String n = null;
		String propname = null;
		for (int i = 0; i < allfunctions.length; i++)
		{
			f = allfunctions[i];
			propname = f.getPropertyName();

			n = propname.length() != 0 ? rb.getString(propname) : null;

			if (n != null)
			{
				names.put(f, n);
				functions.put(n, f);
			}
		}
	}

	/**
	 * Gets the function for the specified name
	 *
	 * @param s the string
	 * @return the function
	 */
	Function getFunction(String s)
	{
		return (Function) functions.get(s);
	}

	/**
	 * Gets the name for the function
	 *
	 * @param f the function
	 * @return the string
	 */
	String getName(Function f)
	{
		return (String) names.get(f);
	}
}
