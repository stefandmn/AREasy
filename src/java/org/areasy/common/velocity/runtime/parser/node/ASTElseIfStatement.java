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

/**
 * This class is responsible for handling the ElseIf VTL control statement.
 * <p/>
 * Please look at the Parser.jjt file which is
 * what controls the generation of this class.
 *
 * @version $Id: ASTElseIfStatement.java,v 1.1 2008/05/25 22:33:08 swd\stefan.damian Exp $
 */
public class ASTElseIfStatement extends SimpleNode
{
	public ASTElseIfStatement(int id)
	{
		super(id);
	}

	public ASTElseIfStatement(Parser p, int id)
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
	 * An ASTElseStatement is true if the expression
	 * it contains evaluates to true. Expressions know
	 * how to evaluate themselves, so we do that
	 * here and return the value back to ASTIfStatement
	 * where this node was originally asked to evaluate
	 * itself.
	 */
	public boolean evaluate(InternalContextAdapter context) throws MethodInvocationException
	{
		return jjtGetChild(0).evaluate(context);
	}

	/**
	 * renders the block
	 */
	public boolean render(InternalContextAdapter context, Writer writer) throws IOException, MethodInvocationException, ResourceNotFoundException, ParseErrorException
	{
		return jjtGetChild(1).render(context, writer);
	}
}
