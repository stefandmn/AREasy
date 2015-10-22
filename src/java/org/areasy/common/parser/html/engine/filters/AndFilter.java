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

/**
 * This class accepts all nodes matching both filters (AND operation).
 */
public class AndFilter implements NodeFilter
{

	/**
	 * The left hand side.
	 */
	protected NodeFilter mLeft;

	/**
	 * The right hand side.
	 */
	protected NodeFilter mRight;

	/**
	 * Creates a new instance of AndFilter that accepts nodes acceptable to both filters.
	 *
	 * @param left  One filter.
	 * @param right The other filter.
	 */
	public AndFilter(NodeFilter left, NodeFilter right)
	{
		mLeft = left;
		mRight = right;
	}

	/**
	 * Accept nodes that are acceptable to both filters.
	 *
	 * @param node The node to check.
	 */
	public boolean accept(Node node)
	{
		return (mLeft.accept(node) && mRight.accept(node));
	}
}
