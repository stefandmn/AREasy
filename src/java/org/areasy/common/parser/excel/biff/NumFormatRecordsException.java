package org.areasy.common.parser.excel.biff;

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
 * Excel places a constraint on the number of format records that
 * are allowed.  This exception is thrown when that number is exceeded
 * This is a static exception and  should be handled internally
 */
public class NumFormatRecordsException extends Exception
{
	/**
	 * Constructor
	 */
	public NumFormatRecordsException()
	{
		super("Internal error:  max number of FORMAT records exceeded");
	}
}
