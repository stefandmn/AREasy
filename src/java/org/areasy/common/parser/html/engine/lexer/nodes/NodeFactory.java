package org.areasy.common.parser.html.engine.lexer.nodes;

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
import org.areasy.common.parser.html.engine.lexer.Page;
import org.areasy.common.parser.html.utilities.ParserException;

import java.util.Vector;

/**
 * This interface defines the methods needed to create new nodes.
 * The factory is used when lexing to generate the nodes passed
 * back to the caller.
 */
public interface NodeFactory
{

	/**
	 * Create a new string node.
	 *
	 * @param page  The page the node is on.
	 * @param start The beginning position of the string.
	 * @param end   The ending positiong of the string.
	 */
	public Node createStringNode(Page page, int start, int end)
			throws
			ParserException;

	/**
	 * Create a new remark node.
	 *
	 * @param page  The page the node is on.
	 * @param start The beginning position of the remark.
	 * @param end   The ending positiong of the remark.
	 */
	public Node createRemarkNode(Page page, int start, int end)
			throws
			ParserException;

	/**
	 * Create a new tag node.
	 * Note that the attributes vector contains at least one element,
	 * which is the tag name (standalone attribute) at position zero.
	 * This can be used to decide which type of node to create, or
	 * gate other processing that may be appropriate.
	 *
	 * @param page       The page the node is on.
	 * @param start      The beginning position of the tag.
	 * @param end        The ending positiong of the tag.
	 * @param attributes The attributes contained in this tag.
	 */
	public Node createTagNode(Page page, int start, int end, Vector attributes)
			throws
			ParserException;
}
