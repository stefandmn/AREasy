package org.areasy.common.parser.excel.write.biff;

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

import org.areasy.common.parser.excel.write.WriteException;

/**
 * Exception thrown when reading a biff file
 */
public class ExcelWriteException extends WriteException
{
	private static class WriteMessage
	{
		/**
		 */
		public String message;

		/**
		 * Constructs this exception with the specified message
		 *
		 * @param m the messageA
		 */
		WriteMessage(String m)
		{
			message = m;
		}
	}

	/**
	 */
	static WriteMessage formatInitialized =
			new WriteMessage("Attempt to modify a referenced format");
	/**
	 */
	static WriteMessage cellReferenced =
			new WriteMessage("Cell has already been added to a process");

	static WriteMessage maxRowsExceeded =
			new WriteMessage("The maximum number of rows permitted on a process " +
					"been exceeded");

	static WriteMessage maxColumnsExceeded =
			new WriteMessage("The maximum number of columns permitted on a " +
					"process has been exceeded");

	static WriteMessage copyPropertySets =
			new WriteMessage("Error encounted when copying additional property sets");

	/**
	 * Constructs this exception with the specified message
	 *
	 * @param m the message
	 */
	public ExcelWriteException(WriteMessage m)
	{
		super(m.message);
	}
}
