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

public class ASTNotNode extends SimpleNode
{
	public ASTNotNode(int id)
	{
		super(id);
	}

	public ASTNotNode(Parser p, int id)
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

	public boolean evaluate(InternalContextAdapter context) throws MethodInvocationException
	{
		if (jjtGetChild(0).evaluate(context))
		{
			return false;
		}
		else
		{
			return true;
		}
	}

	public Object value(InternalContextAdapter context)
			throws MethodInvocationException
	{
		return (jjtGetChild(0).evaluate(context) ? Boolean.FALSE : Boolean.TRUE);
	}
}
