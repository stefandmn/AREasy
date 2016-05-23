package org.areasy.common.parser.html.engine.tags;

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

import org.areasy.common.parser.html.engine.lexer.Page;
import org.areasy.common.parser.html.utilities.ParserException;

/**
 * BaseHrefTag represents an &lt;Base&gt; tag.
 * It extends a basic tag by providing an accessor to the HREF attribute.
 */
public class BaseHrefTag extends Tag
{

	/**
	 * The set of names handled by this tag.
	 */
	private static final String[] mIds = new String[]{"BASE"};

	/**
	 * Create a new base tag.
	 */
	public BaseHrefTag()
	{
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
	 * Get the value of the HREF attribute, if any.
	 *
	 * @return The HREF value, with the last slash removed, if any.
	 */
	public String getBaseUrl()
	{
		String base;

		base = getAttribute("HREF");
		if (base != null && base.length() > 0)
		{
			base = base.trim();
		}
		base = (null == base) ? "" : base;

		return (base);
	}

	public void setBaseUrl(String base)
	{
		setAttribute("HREF", base);
	}

	/**
	 * Perform the meaning of this tag.
	 * This sets the base URL to use for the rest of the page.
	 */
	public void doSemanticAction() throws ParserException
	{
		Page page;

		page = getPage();
		if (null != page)
		{
			page.getLinkProcessor().setBaseUrl(getBaseUrl());
		}
	}
}
