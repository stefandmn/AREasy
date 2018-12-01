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

public class ASTModNode extends SimpleNode
{
	public ASTModNode(int id)
	{
		super(id);
	}

	public ASTModNode(Parser p, int id)
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

	public Object value(InternalContextAdapter context)
			throws MethodInvocationException
	{
		/*
		 *  get the two args
		 */

		Object left = jjtGetChild(0).value(context);
		Object right = jjtGetChild(1).value(context);

		/*
		 *  if either is null, lets log and bail
		 */

		if (left == null || right == null)
		{
			logger.error((left == null ? "Left" : "Right") + " side ("
					+ jjtGetChild((left == null ? 0 : 1)).literal()
					+ ") of modulus operation has null value."
					+ " Operation not possible. "
					+ context.getCurrentTemplateName() + " [line " + getLine()
					+ ", column " + getColumn() + "]");
			return null;
		}

		/*
		 *  if not an Integer, not much we can do either
		 */

		if (!(left instanceof Integer) || !(right instanceof Integer))
		{
			logger.error((!(left instanceof Integer) ? "Left" : "Right")
					+ " side of modulus operation is not a valid type. "
					+ "Currently only integers (1,2,3...) and Integer type is supported. "
					+ context.getCurrentTemplateName() + " [line " + getLine()
					+ ", column " + getColumn() + "]");

			return null;
		}

		/*
		 *  check for divide by 0
		 */

		if (((Integer) right).intValue() == 0)
		{
			logger.error("Right side of modulus operation is zero. Must be non-zero. "
					+ context.getCurrentTemplateName() + " [line " + getLine()
					+ ", column " + getColumn() + "]");

			return null;
		}

		return new Integer(((Integer) left).intValue() % ((Integer) right).intValue());
	}
}

