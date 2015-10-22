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
import org.areasy.common.parser.html.engine.lexer.nodes.Attribute;
import org.areasy.common.parser.html.engine.lexer.nodes.TagNode;

import java.util.Locale;

/**
 * This class accepts all tags that have a certain attribute, and optionally, with a certain value.
 */
public class HasAttributeFilter implements NodeFilter
{

	/**
	 * The attribute to check for.
	 */
	protected String mAttribute;

	/**
	 * The value to check for.
	 */
	protected String mValue;

	/**
	 * Creates a new instance of HasAttributeFilter that accepts tags with the given attribute.
	 *
	 * @param attribute The attribute to search for.
	 */
	public HasAttributeFilter(String attribute)
	{
		this(attribute, null);
	}

	/**
	 * Creates a new instance of HasAttributeFilter that accepts tags with the given attribute.
	 *
	 * @param attribute The attribute to search for.
	 * @param value     The value that must be matched, or null if any value will match.
	 */
	public HasAttributeFilter(String attribute, String value)
	{
		mAttribute = attribute.toUpperCase(Locale.ENGLISH);
		mValue = value;
	}

	/**
	 * Accept tags with a certain attribute.
	 *
	 * @param node The node to check.
	 */
	public boolean accept(Node node)
	{
		TagNode tag;
		Attribute attribute;
		boolean ret;

		ret = false;
		if (node instanceof TagNode)
		{
			tag = (TagNode) node;
			attribute = tag.getAttributeEx(mAttribute);
			ret = null != attribute;
			if (ret && (null != mValue))
			{
				ret = mValue.equals(attribute.getValue());
			}
		}

		return (ret);
	}
}
