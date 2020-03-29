package org.areasy.common.parser.excel.biff.drawing;

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
 * Checked exception thrown when the drawing data is corrupt eg. when
 * the drawing number exceeds the number of SpContainers.  This exception
 * is handled within the drawing package, and usually causes drawings to be
 * disabled for the remainder of the workbook
 */
public class DrawingDataException extends RuntimeException
{
	private static String message =
			"Drawing number exceeds available SpContainers";

	DrawingDataException()
	{
		super(message);
	}
}
