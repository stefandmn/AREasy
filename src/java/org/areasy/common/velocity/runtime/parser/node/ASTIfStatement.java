package org.areasy.common.velocity.runtime.parser.node;

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

import org.areasy.common.velocity.base.MethodInvocationException;
import org.areasy.common.velocity.base.ResourceNotFoundException;
import org.areasy.common.velocity.context.InternalContextAdapter;
import org.areasy.common.velocity.runtime.parser.ParseErrorException;
import org.areasy.common.velocity.runtime.parser.Parser;

import java.io.IOException;
import java.io.Writer;


public class ASTIfStatement extends SimpleNode
{
	public ASTIfStatement(int id)
	{
		super(id);
	}

	public ASTIfStatement(Parser p, int id)
	{
		super(p, id);
	}

	/**
	 * Accept the visitor. *
	 */
	public Object jjtAccept(ParserVisitor visitor, Object data)
	{
		return visitor.visit(this, data);
	}

	public boolean render(InternalContextAdapter context, Writer writer)
			throws IOException, MethodInvocationException,
			ResourceNotFoundException, ParseErrorException
	{
		/*
		 * Check if the #if(expression) construct evaluates to true:
		 * if so render and leave immediately because there
		 * is nothing left to do!
		 */
		if (jjtGetChild(0).evaluate(context))
		{
			jjtGetChild(1).render(context, writer);
			return true;
		}

		int totalNodes = jjtGetNumChildren();

		/*
		 * Now check the remaining nodes left in the
		 * if construct. The nodes are either elseif
		 *  nodes or else nodes. Each of these node
		 * types knows how to evaluate themselves. If
		 * a node evaluates to true then the node will
		 * render itself and this method will return
		 * as there is nothing left to do.
		 */
		for (int i = 2; i < totalNodes; i++)
		{
			if (jjtGetChild(i).evaluate(context))
			{
				jjtGetChild(i).render(context, writer);
				return true;
			}
		}

		/*
		 * This is reached when an ASTIfStatement
		 * consists of an if/elseif sequence where
		 * none of the nodes evaluate to true.
		 */
		return true;
	}

	public void process(InternalContextAdapter context, ParserVisitor visitor)
	{
	}
}






