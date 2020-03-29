package org.areasy.common.parser.excel.format;

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
 * Enumeration class which contains the various script styles available
 * within the standard Excel ScriptStyle palette
 */
public final class ScriptStyle
{
	/**
	 * The internal numerical representation of the ScriptStyle
	 */
	private int value;

	/**
	 * The display string for the script style.  Used when presenting the
	 * format information
	 */
	private String string;

	/**
	 * The list of ScriptStyles
	 */
	private static ScriptStyle[] styles = new ScriptStyle[0];


	/**
	 * Private constructor
	 *
	 * @param val
	 * @param s   the display string
	 */
	protected ScriptStyle(int val, String s)
	{
		value = val;
		string = s;

		ScriptStyle[] oldstyles = styles;
		styles = new ScriptStyle[oldstyles.length + 1];
		System.arraycopy(oldstyles, 0, styles, 0, oldstyles.length);
		styles[oldstyles.length] = this;
	}

	/**
	 * Gets the value of this style.  This is the value that is written to
	 * the generated Excel file
	 *
	 * @return the binary value
	 */
	public int getValue()
	{
		return value;
	}

	/**
	 * Gets the string description for display purposes
	 *
	 * @return the string description
	 */
	public String getDescription()
	{
		return string;
	}

	/**
	 * Gets the ScriptStyle from the value
	 *
	 * @param val
	 * @return the ScriptStyle with that value
	 */
	public static ScriptStyle getStyle(int val)
	{
		for (int i = 0; i < styles.length; i++)
		{
			if (styles[i].getValue() == val)
			{
				return styles[i];
			}
		}

		return NORMAL_SCRIPT;
	}

	// The script styles
	public static final ScriptStyle NORMAL_SCRIPT = new ScriptStyle(0, "normal");
	public static final ScriptStyle SUPERSCRIPT = new ScriptStyle(1, "super");
	public static final ScriptStyle SUBSCRIPT = new ScriptStyle(2, "sub");


}











