package org.areasy.common.parser.excel.common;

/*
 * Copyright (c) 2007-2015 AREasy Runtime
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

/**
 * An exception thrown when an assert (from the Assert class) fails
 */
public class AssertionFailed extends RuntimeException
{
	/**
	 * The logger
	 */
	private static Logger logger = LoggerFactory.getLog(AssertionFailed.class);

	/**
	 * Default constructor
	 * Prints the stack trace
	 */
	public AssertionFailed()
	{
		super();
		
		logger.debug("Exception", this);
	}

	/**
	 * Constructor with message
	 * Prints the stack trace
	 *
	 * @param s Message thrown with the assertion
	 */
	public AssertionFailed(String s)
	{
		super(s);
	}
}
