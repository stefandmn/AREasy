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

import org.areasy.common.parser.excel.ExcelException;

/**
 * Exception thrown when parsing a formula
 */
public class FormulaException extends ExcelException
{
	/**
	 * Inner class containing the message
	 */
	private static class FormulaMessage
	{
		/**
		 * The message
		 */
		private String message;

		/**
		 * Constructs this exception with the specified message
		 *
		 * @param m the message
		 */
		FormulaMessage(String m)
		{
			message = m;
		}

		/**
		 * Accessor for the message
		 *
		 * @return the message
		 */
		public String getMessage()
		{
			return message;
		}
	}

	/**
	 */
	static final FormulaMessage UNRECOGNIZED_TOKEN =
			new FormulaMessage("Unrecognized token");

	/**
	 */
	static final FormulaMessage UNRECOGNIZED_FUNCTION =
			new FormulaMessage("Unrecognized function");

	/**
	 */
	public static final FormulaMessage BIFF8_SUPPORTED =
			new FormulaMessage("Only biff8 formulas are supported");

	/**
	 */
	static final FormulaMessage LEXICAL_ERROR =
			new FormulaMessage("Lexical error:  ");

	/**
	 */
	static final FormulaMessage INCORRECT_ARGUMENTS =
			new FormulaMessage("Incorrect arguments supplied to function");

	/**
	 */
	static final FormulaMessage SHEET_REF_NOT_FOUND =
			new FormulaMessage("Could not find sheet");

	/**
	 */
	static final FormulaMessage CELL_NAME_NOT_FOUND =
			new FormulaMessage("Could not find named cell");


	/**
	 * Constructs this exception with the specified message
	 *
	 * @param m the message
	 */
	public FormulaException(FormulaMessage m)
	{
		super(m.message);
	}

	/**
	 * Constructs this exception with the specified message
	 *
	 * @param m   the message
	 * @param val the value
	 */
	public FormulaException(FormulaMessage m, int val)
	{
		super(m.message + " " + val);
	}

	/**
	 * Constructs this exception with the specified message
	 *
	 * @param m   the message
	 * @param val the value
	 */
	public FormulaException(FormulaMessage m, String val)
	{
		super(m.message + " " + val);
	}
}
