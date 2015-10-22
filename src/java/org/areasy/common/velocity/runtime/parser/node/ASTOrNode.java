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
import org.areasy.common.velocity.context.InternalContextAdapter;
import org.areasy.common.velocity.runtime.parser.Parser;

/**
 * Please look at the Parser.jjt file which is
 * what controls the generation of this class.
 *
 * @version $Id: ASTOrNode.java,v 1.1 2008/05/25 22:33:08 swd\stefan.damian Exp $
 */
public class ASTOrNode extends SimpleNode
{
	public ASTOrNode(int id)
	{
		super(id);
	}

	public ASTOrNode(Parser p, int id)
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
	 * the logical or :
	 * the rule :
	 * left || null -> left
	 * null || right -> right
	 * null || null -> false
	 * left || right ->  left || right
	 */
	public boolean evaluate(InternalContextAdapter context)
			throws MethodInvocationException
	{
		Node left = jjtGetChild(0);
		Node right = jjtGetChild(1);

		/*
		 *  if the left is not null and true, then true
		 */

		if (left != null && left.evaluate(context))
		{
			return true;
		}

		/*
		 *  same for right
		 */

		if (right != null && right.evaluate(context))
		{
			return true;
		}

		return false;
	}
}





