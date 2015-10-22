package org.areasy.common.parser.excel.format;

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
 * Interface which exposes the user font display information to the user
 */
public interface Font
{
	/**
	 * Gets the name of this font
	 *
	 * @return the name of this font
	 */
	public String getName();

	/**
	 * Gets the point size for this font, if the font hasn't been initialized
	 *
	 * @return the point size
	 */
	public int getPointSize();

	/**
	 * Gets the bold weight for this font
	 *
	 * @return the bold weight for this font
	 */
	public int getBoldWeight();

	/**
	 * Returns the italic flag
	 *
	 * @return TRUE if this font is italic, FALSE otherwise
	 */
	public boolean isItalic();

	/**
	 * Returns the strike-out flag
	 *
	 * @return TRUE if this font is struck-out, FALSE otherwise
	 */
	public boolean isStruckout();

	/**
	 * Gets the underline style for this font
	 *
	 * @return the underline style
	 */
	public UnderlineStyle getUnderlineStyle();

	/**
	 * Gets the colour for this font
	 *
	 * @return the colour
	 */
	public Colour getColour();

	/**
	 * Gets the script style
	 *
	 * @return the script style
	 */
	public ScriptStyle getScriptStyle();
}

