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
 * This class accepts all tags of a given class.
 */
public class NodeClassFilter implements NodeFilter
{

	/**
	 * The class to match.
	 */
	protected Class mClass;

	/**
	 * Creates a new instance of NodeClassFilter that accepts tags of the given class.
	 *
	 * @param cls The cls to match.
	 */
	public NodeClassFilter(Class cls)
	{
		mClass = cls;
	}

	/**
	 * Accept nodes that are assignable from the class provided in the constructor.
	 *
	 * @param node The node to check.
	 */
	public boolean accept(Node node)
	{
		return (mClass.isAssignableFrom(node.getClass()));
	}
}
