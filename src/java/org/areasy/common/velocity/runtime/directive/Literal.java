package org.areasy.common.velocity.runtime.directive;

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

import org.areasy.common.velocity.context.InternalContextAdapter;
import org.areasy.common.velocity.runtime.RuntimeService;
import org.areasy.common.velocity.runtime.parser.node.Node;

import java.io.IOException;
import java.io.Writer;

/**
 * A very simple directive that leverages the Node.literal()
 * to grab the literal rendition of a node. We basically
 * grab the literal value on init(), then repeatedly use
 * that during render().
 *
 * @version $Id: Literal.java,v 1.1 2008/05/25 22:33:14 swd\stefan.damian Exp $
 */
public class Literal extends Directive
{
	String literalText;

	/**
	 * Return name of this directive.
	 */
	public String getName()
	{
		return "literal";
	}

	/**
	 * Return type of this directive.
	 */
	public int getType()
	{
		return BLOCK;
	}

	/**
	 * Store the literal rendition of a node using
	 * the Node.literal().
	 */
	public void init(RuntimeService rs, InternalContextAdapter context, Node node) throws Exception
	{
		super.init(rs, context, node);

		literalText = node.jjtGetChild(0).literal();
	}

	/**
	 * Throw the literal rendition of the block between
	 * #literal()/#end into the writer.
	 */
	public boolean render(InternalContextAdapter context, Writer writer, Node node) throws IOException
	{
		writer.write(literalText);
		return true;
	}
}
