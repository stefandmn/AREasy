package org.areasy.common.parser.html.engine.filters;

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

import org.areasy.common.parser.html.engine.Node;
import org.areasy.common.parser.html.engine.NodeFilter;

/**
 * This class accepts only one specific node.
 */
public class IsEqualFilter implements NodeFilter
{

	/**
	 * The node to match.
	 */
	public Node mNode;

	/**
	 * Creates a new instance of an IsEqualFilter that accepts only the node provided.
	 *
	 * @param node The node to match.
	 */
	public IsEqualFilter(Node node)
	{
		mNode = node;
	}

	/**
	 * Accept the node.
	 *
	 * @param node The node to check.
	 * @return <code>false</code> unless <code>node</code> is the one and only.
	 */
	public boolean accept(Node node)
	{
		return (mNode == node);
	}
}
