package org.areasy.common.parser.excel.common;

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
 * Simple assertion mechanism for use during development
 */
public final class Assert
{
	/**
	 * Throws an AssertionFailed exception if the specified condition is
	 * false
	 *
	 * @param condition The assertion condition which must be true
	 */
	public static void verify(boolean condition)
	{
		if (!condition) throw new AssertionFailed();
	}

	/**
	 * If the condition evaluates to false, an AssertionFailed is thrown
	 *
	 * @param message   A message thrown with the failed assertion
	 * @param condition If this evaluates to false, an AssertionFailed is thrown
	 */
	public static void verify(boolean condition, String message)
	{
		if (!condition) throw new AssertionFailed(message);
	}
}







