package org.areasy.common.parser.excel.format;

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
 * The location of a border
 */
public /*final*/ class Border
{
	/**
	 * The string description
	 */
	private String string;

	/**
	 * Constructor
	 */
	protected Border(String s)
	{
		string = s;
	}

	/**
	 * Gets the description
	 */
	public String getDescription()
	{
		return string;
	}

	public final static Border NONE = new Border("none");
	public final static Border ALL = new Border("all");
	public final static Border TOP = new Border("top");
	public final static Border BOTTOM = new Border("bottom");
	public final static Border LEFT = new Border("left");
	public final static Border RIGHT = new Border("right");
}

