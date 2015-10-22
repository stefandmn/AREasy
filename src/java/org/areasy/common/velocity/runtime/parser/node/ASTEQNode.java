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
 * Handles the equivalence operator
 * <p/>
 * <arg1>  == <arg2>
 * <p/>
 * This operator requires that the LHS and RHS are both of the
 * same Class.
 *
 * @version $Id: ASTEQNode.java,v 1.1 2008/05/25 22:33:08 swd\stefan.damian Exp $
 */
public class ASTEQNode extends SimpleNode
{

	public ASTEQNode(int id)
	{
		super(id);
	}

	public ASTEQNode(Parser p, int id)
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
	 * Calculates the value of the logical expression
	 * <p/>
	 * arg1 == arg2
	 * <p/>
	 * All class types are supported.   Uses equals() to
	 * determine equivalence.  This should work as we represent
	 * with the types we already support, and anything else that
	 * implements equals() to mean more than identical references.
	 *
	 * @param context internal context used to evaluate the LHS and RHS
	 * @return true if equivalent, false if not equivalent,
	 *         false if not compatible arguments, or false
	 *         if either LHS or RHS is null
	 */
	public boolean evaluate(InternalContextAdapter context)
			throws MethodInvocationException
	{
		Object left = jjtGetChild(0).value(context);
		Object right = jjtGetChild(1).value(context);

		/*
		 *  they could be null if they are references and not in the context
		 */

		if (left == null || right == null)
		{
			logger.error((left == null ? "Left" : "Right")
					+ " side ("
					+ jjtGetChild((left == null ? 0 : 1)).literal()
					+ ") of '==' operation "
					+ "has null value. "
					+ "If a reference, it may not be in the context."
					+ " Operation not possible. "
					+ context.getCurrentTemplateName() + " [line " + getLine()
					+ ", column " + getColumn() + "]");
			return false;
		}

		/*
		 *  check to see if they are the same class.  I don't think this is slower
		 *  as I don't think that getClass() results in object creation, and we can
		 *  extend == to handle all classes
		 */

		if (left.getClass().equals(right.getClass()))
		{
			return left.equals(right);
		}
		else
		{
			logger.error("Error in evaluation of == expression."
					+ " Both arguments must be of the same Class."
					+ " Currently left = " + left.getClass() + ", right = "
					+ right.getClass() + ". "
					+ context.getCurrentTemplateName() + " [line " + getLine()
					+ ", column " + getColumn() + "] (ASTEQNode)");
		}

		return false;
	}

	public Object value(InternalContextAdapter context)
			throws MethodInvocationException
	{
		boolean val = evaluate(context);

		return val ? Boolean.TRUE : Boolean.FALSE;
	}

}
