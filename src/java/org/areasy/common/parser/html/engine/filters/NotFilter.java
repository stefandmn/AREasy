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

/**
 * This class accepts all nodes not acceptable to the filter.
 */
public class NotFilter implements NodeFilter
{

	/**
	 * The filter to gainsay.
	 */
	protected NodeFilter mFilter;

	/**
	 * Creates a new instance of NotFilter that accepts nodes not acceptable to the filter.
	 *
	 * @param filter The filter to consult.
	 */
	public NotFilter(NodeFilter filter)
	{
		mFilter = filter;
	}

	/**
	 * Accept nodes that are not acceptable to the filter.
	 *
	 * @param node The node to check.
	 */
	public boolean accept(Node node)
	{
		return (!mFilter.accept(node));
	}
}
