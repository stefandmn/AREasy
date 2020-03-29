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

import org.areasy.common.parser.html.engine.lexer.Page;
import org.areasy.common.parser.html.engine.visitors.NodeVisitor;

/**
 * Normal text in the html document is identified and represented by this class.
 *
 * @version $Id: StringNode.java,v 1.1 2008/05/25 17:26:05 swd\stefan.damian Exp $
 */
public class StringNode extends org.areasy.common.parser.html.engine.lexer.nodes.StringNode
{

	/**
	 * Constructor takes in the text string, beginning and ending posns.
	 *
	 * @param page  The page this string is on.
	 * @param start The beginning position of the string.
	 * @param end   The ending positiong of the string.
	 */
	public StringNode(Page page, int start, int end)
	{
		super(page, start, end);
	}

	/**
	 * String visiting code.
	 *
	 * @param visitor The <code>NodeVisitor</code> object to invoke
	 *                <code>visitStringNode()</code> on.
	 */
	public void accept(Object visitor)
	{
		((NodeVisitor) visitor).visitStringNode(this);
	}
}
