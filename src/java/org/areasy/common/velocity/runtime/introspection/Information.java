package org.areasy.common.velocity.runtime.introspection;

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

/**
 * Little class to carry in info such as template name, line and column
 * for information error reporting from the uberspector implementations
 *
 * @version $Id: Information.java,v 1.1 2008/05/25 22:33:14 swd\stefan.damian Exp $
 */
public class Information
{
	private int line;
	private int column;
	private String templateName;

	public Information(String tn, int l, int c)
	{
		templateName = tn;
		line = l;
		column = c;
	}

	public String getTemplateName()
	{
		return templateName;
	}

	public int getLine()
	{
		return line;
	}

	public int getColumn()
	{
		return column;
	}
}
