package org.areasy.common.parser.html.engine.filters;

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

import org.areasy.common.parser.html.engine.Node;
import org.areasy.common.parser.html.engine.NodeFilter;
import org.areasy.common.parser.html.engine.lexer.nodes.TagNode;

import java.util.Locale;

/**
 * This class accepts all tags matching the tag name.
 */
public class TagNameFilter implements NodeFilter
{
	/**
	 * The tag name to match.
	 */
	protected String mName;

	/**
	 * Creates a new instance of TagNameFilter that accepts tags with the given name.
	 *
	 * @param name The tag name to match.
	 */
	public TagNameFilter(String name)
	{
		mName = name.toUpperCase(Locale.ENGLISH);
	}

	/**
	 * Accept nodes that are tags and have a matching tag name.
	 * This discards non-tag nodes and end tags.
	 * The end tags are available on the enclosing non-end tag.
	 *
	 * @param node The node to check.
	 */
	public boolean accept(Node node)
	{
		return ((node instanceof TagNode) &&
				!((TagNode) node).isEndTag() &&
				((TagNode) node).getTagName().equals(mName));
	}
}
