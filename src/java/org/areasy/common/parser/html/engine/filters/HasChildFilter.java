package org.areasy.common.parser.html.engine.filters;

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

import org.areasy.common.parser.html.engine.Node;
import org.areasy.common.parser.html.engine.NodeFilter;
import org.areasy.common.parser.html.engine.tags.CompositeTag;
import org.areasy.common.parser.html.utilities.NodeList;

/**
 * This class accepts all tags that have a child acceptable to the filter.
 */
public class HasChildFilter implements NodeFilter
{
	/**
	 * The filter to apply to children.
	 */
	protected NodeFilter mFilter;

	/**
	 * Creates a new instance of HasChildFilter that accepts tags with children acceptable to the filter.
	 * Similar to asking for the parent of a node returned by the given
	 * filter, but where multiple children may be acceptable, this class
	 * will only accept the parent once.
	 *
	 * @param filter The filter to apply to children.
	 */
	public HasChildFilter(NodeFilter filter)
	{
		mFilter = filter;
	}

	/**
	 * Accept tags with children acceptable to the filter.
	 *
	 * @param node The node to check.
	 */
	public boolean accept(Node node)
	{
		CompositeTag tag;
		NodeList children;
		boolean ret;

		ret = false;
		if (node instanceof CompositeTag)
		{
			tag = (CompositeTag) node;
			children = tag.getChildren();
			if (null != children)
			{
				for (int i = 0; i < children.size(); i++)
				{
					if (mFilter.accept(children.elementAt(i)))
					{
						ret = true;
						break;
					}
				}
			}
		}

		return (ret);
	}
}
