package org.areasy.common.velocity.runtime.parser.node;

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

import org.areasy.common.velocity.base.MethodInvocationException;
import org.areasy.common.velocity.context.InternalContextAdapter;
import org.areasy.common.velocity.runtime.parser.Parser;

/**
 * Please look at the Parser.jjt file which is
 * what controls the generation of this class.
 *
 * @version $Id: ASTAndNode.java,v 1.1 2008/05/25 22:33:07 swd\stefan.damian Exp $
 */
public class ASTAndNode extends SimpleNode
{
	public ASTAndNode(int id)
	{
		super(id);
	}

	public ASTAndNode(Parser p, int id)
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

	/**
	 * Returns the value of the expression.
	 * Since the value of the expression is simply the boolean
	 * result of evaluate(), lets return that.
	 */
	public Object value(InternalContextAdapter context)
			throws MethodInvocationException
	{
		return new Boolean(evaluate(context));
	}

	/**
	 * logical and :
	 * null && right = false
	 * left && null = false
	 * null && null = false
	 */
	public boolean evaluate(InternalContextAdapter context)
			throws MethodInvocationException
	{
		Node left = jjtGetChild(0);
		Node right = jjtGetChild(1);

		/*
		 *  if either is null, lets log and bail
		 */

		if (left == null || right == null)
		{
			logger.error((left == null ? "Left" : "Right") + " side of '&&' operation is null. Operation not possible. "
					+ context.getCurrentTemplateName() + " [line " + getLine()
					+ ", column " + getColumn() + "]");
			return false;
		}

		if (left.evaluate(context))
		{
			if (right.evaluate(context)) return true;
		}

		return false;
	}
}

