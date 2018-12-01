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

/**
 * The JSP/ASP tags like &lt;%&#46;&#46;&#46;%&gt; can be identified by this class.
 */
public class JspTag extends Tag
{
	/**
	 * The set of names handled by this tag.
	 */
	private static final String[] mIds = new String[]{"%", "%=", "%@"};

	/**
	 * Create a new jsp tag.
	 */
	public JspTag()
	{
		//nothing to do
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
	 * Print the contents of the jsp tag.
	 */
	public String toString()
	{
		String guts = toHtml();
		guts = guts.substring(1, guts.length() - 2);
		return "JSP/ASP Tag : " + guts + "; begins at : " + getStartPosition() + "; ends at : " + getEndPosition();
	}
}
