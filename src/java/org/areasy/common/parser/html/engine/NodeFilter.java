package org.areasy.common.parser.html.engine;

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

/**
 * Implement this interface to select particular nodes.
 *
 * @version $Id: NodeFilter.java,v 1.1 2008/05/25 17:26:06 swd\stefan.damian Exp $
 */
public interface NodeFilter
{

	/**
	 * Predicate to determine whether or not to keep the given node.
	 * The behaviour based on this outcome is determined by the context
	 * in which it is called. It may lead to the node being added to a list
	 * or printed out. See the calling routine for details.
	 *
	 * @return <code>true</code> if the node is to be kept, <code>false</code>
	 *         if it is to be discarded.
	 */
	boolean accept(Node node);
}
