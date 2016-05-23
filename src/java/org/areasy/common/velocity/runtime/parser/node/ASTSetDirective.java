package org.areasy.common.velocity.runtime.parser.node;

/*
 * Copyright (c) 2007-2016 AREasy Runtime
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
import org.areasy.common.velocity.base.event.EventCartridge;
import org.areasy.common.velocity.context.InternalContextAdapter;
import org.areasy.common.velocity.runtime.parser.Parser;

import java.io.IOException;
import java.io.Writer;

/**
 * Node for the #set directive
 *
 * @version $Id: ASTSetDirective.java,v 1.1 2008/05/25 22:33:09 swd\stefan.damian Exp $
 */
public class ASTSetDirective extends SimpleNode
{
	private String leftReference = "";
	private Node right;
	private ASTReference left;

	public ASTSetDirective(int id)
	{
		super(id);
	}

	public ASTSetDirective(Parser p, int id)
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
	 * simple init.  We can get the RHS and LHS as the the tree structure is static
	 */
	public Object init(InternalContextAdapter context, Object data) throws Exception
	{
		super.init(context, data);

		right = getRightHandSide();
		left = getLeftHandSide();

		/*
		 *  grab this now.  No need to redo each time
		 */
		leftReference = left.getFirstToken().image.substring(1);

		return data;
	}

	/**
	 * puts the value of the RHS into the context under the key of the LHS
	 */
	public boolean render(InternalContextAdapter context, Writer writer) throws IOException, MethodInvocationException
	{
		Object value = right.value(context);

		if (value == null)
		{
			if (logger.isDebugEnabled())
			{
				EventCartridge ec = context.getEventCartridge();

				boolean doit = true;
				if (ec != null) doit = ec.shouldLogOnNullSet(left.literal(), right.literal());

				if (doit) logger.debug("RHS of #set statement is null. Context will not be modified. "
							+ context.getCurrentTemplateName() + " [line " + getLine()
							+ ", column " + getColumn() + "] - [left part: " + leftReference + "]");
			}

			return false;
		}

		if (left.jjtGetNumChildren() == 0) context.put(leftReference, value);
			else left.setValue(context, value);

		return true;
	}

	/**
	 * returns the ASTReference that is the LHS of the set statememt
	 */
	private ASTReference getLeftHandSide()
	{
		return (ASTReference) jjtGetChild(0);
	}

	/**
	 * returns the RHS Node of the set statement
	 */
	private Node getRightHandSide()
	{
		return jjtGetChild(1);
	}
}
