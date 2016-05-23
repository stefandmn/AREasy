package org.areasy.common.velocity.runtime.directive;

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

import java.util.Stack;

/**
 * Exception for #parse() problems
 *
 * @version $Id: ParseDirectiveException.java,v 1.1 2008/05/25 22:33:14 swd\stefan.damian Exp $
 */
public class ParseDirectiveException extends Exception
{
	private Stack filenameStack = new Stack();
	private String msg = "";
	private int depthCount = 0;

	/**
	 * Constructor
	 */
	ParseDirectiveException(String m, int i)
	{
		msg = m;
		depthCount = i;
	}

	/**
	 * Get a message.
	 */
	public String getMessage()
	{
		String returnStr = "#parse() exception : depth = " + depthCount + " -> " + msg;

		returnStr += " File stack : ";

		try
		{
			while (!filenameStack.empty())
			{
				returnStr += (String) filenameStack.pop();
				returnStr += " -> ";
			}
		}
		catch (Exception e)
		{
		}

		return returnStr;
	}

	/**
	 * Add a file to the filename stack
	 */
	public void addFile(String s)
	{
		filenameStack.push(s);
	}

}
