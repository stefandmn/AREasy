package org.areasy.common.parser.html.engine.tags;

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

import org.areasy.common.parser.html.engine.scanners.StyleScanner;

/**
 * A StyleTag represents a &lt;style&gt; tag.
 */
public class StyleTag extends CompositeTag
{

	/**
	 * The set of names handled by this tag.
	 */
	private static final String[] mIds = new String[]{"STYLE"};

	/**
	 * The set of end tag names that indicate the end of this tag.
	 */
	private static final String[] mEndTagEnders = new String[]{"BODY", "HTML"};

	/**
	 * Create a new style tag.
	 */
	public StyleTag()
	{
		setThisScanner(new StyleScanner());
	}

	/**
	 * Return the set of names handled by this tag.
	 *
	 * @return The names to be matched that create tags of this type.
	 */
	public String[] getIds()
	{
		return (mIds);
	}

	/**
	 * Return the set of end tag names that cause this tag to finish.
	 *
	 * @return The names of following end tags that stop further scanning.
	 */
	public String[] getEndTagEnders()
	{
		return (mEndTagEnders);
	}

	/**
	 * Get the style data in this tag.
	 *
	 * @return The HTML of the children of this tag.
	 */
	public String getStyleCode()
	{
		return (getChildrenHTML());
	}

	/**
	 * Print the contents of the style node.
	 *
	 * @return A string suitable for debugging or a printout.
	 */
	public String toString()
	{
		String guts;
		StringBuffer ret;

		ret = new StringBuffer();

		guts = toHtml();
		guts = guts.substring(1, guts.length() - 1);
		ret.append("Style node :\n");
		ret.append(guts);
		ret.append("\n");

		return (ret.toString());
	}
}
