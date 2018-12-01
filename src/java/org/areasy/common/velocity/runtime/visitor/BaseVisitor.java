package org.areasy.common.velocity.runtime.visitor;

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

import org.areasy.common.velocity.context.InternalContextAdapter;
import org.areasy.common.velocity.runtime.parser.node.*;

import java.io.Writer;

/**
 * This is the base class for all visitors.
 * For each AST node, this class will provide
 * a bare-bones method for traversal.
 *
 * @version $Id: BaseVisitor.java,v 1.1 2008/05/25 22:33:14 swd\stefan.damian Exp $
 */
public abstract class BaseVisitor implements ParserVisitor
{
	/**
	 * Context used during traversal
	 */
	protected InternalContextAdapter context;

	/**
	 * Writer used as the output sink
	 */
	protected Writer writer;

	public void setWriter(Writer writer)
	{
		this.writer = writer;
	}

	public void setContext(InternalContextAdapter context)
	{
		this.context = context;
	}

	public Object visit(SimpleNode node, Object data)
	{
		data = node.childrenAccept(this, data);
		return data;
	}

	public Object visit(ASTprocess node, Object data)
	{
		data = node.childrenAccept(this, data);
		return data;
	}

	public Object visit(ASTExpression node, Object data)
	{
		data = node.childrenAccept(this, data);
		return data;
	}

	public Object visit(ASTAssignment node, Object data)
	{
		data = node.childrenAccept(this, data);
		return data;
	}

	public Object visit(ASTOrNode node, Object data)
	{
		data = node.childrenAccept(this, data);
		return data;
	}

	public Object visit(ASTAndNode node, Object data)
	{
		data = node.childrenAccept(this, data);
		return data;
	}

	public Object visit(ASTEQNode node, Object data)
	{
		data = node.childrenAccept(this, data);
		return data;
	}

	public Object visit(ASTNENode node, Object data)
	{
		data = node.childrenAccept(this, data);
		return data;
	}

	public Object visit(ASTLTNode node, Object data)
	{
		data = node.childrenAccept(this, data);
		return data;
	}

	public Object visit(ASTGTNode node, Object data)
	{
		data = node.childrenAccept(this, data);
		return data;
	}

	public Object visit(ASTLENode node, Object data)
	{
		data = node.childrenAccept(this, data);
		return data;
	}

	public Object visit(ASTGENode node, Object data)
	{
		data = node.childrenAccept(this, data);
		return data;
	}

	public Object visit(ASTAddNode node, Object data)
	{
		data = node.childrenAccept(this, data);
		return data;
	}

	public Object visit(ASTSubtractNode node, Object data)
	{
		data = node.childrenAccept(this, data);
		return data;
	}

	public Object visit(ASTMulNode node, Object data)
	{
		data = node.childrenAccept(this, data);
		return data;
	}

	public Object visit(ASTDivNode node, Object data)
	{
		data = node.childrenAccept(this, data);
		return data;
	}

	public Object visit(ASTModNode node, Object data)
	{
		data = node.childrenAccept(this, data);
		return data;
	}

	public Object visit(ASTNotNode node, Object data)
	{
		data = node.childrenAccept(this, data);
		return data;
	}

	public Object visit(ASTNumberLiteral node, Object data)
	{
		data = node.childrenAccept(this, data);
		return data;
	}

	public Object visit(ASTStringLiteral node, Object data)
	{
		data = node.childrenAccept(this, data);
		return data;
	}

	public Object visit(ASTIdentifier node, Object data)
	{
		data = node.childrenAccept(this, data);
		return data;
	}

	public Object visit(ASTMethod node, Object data)
	{
		data = node.childrenAccept(this, data);
		return data;
	}

	public Object visit(ASTReference node, Object data)
	{
		data = node.childrenAccept(this, data);
		return data;
	}

	public Object visit(ASTTrue node, Object data)
	{
		data = node.childrenAccept(this, data);
		return data;
	}

	public Object visit(ASTFalse node, Object data)
	{
		data = node.childrenAccept(this, data);
		return data;
	}

	public Object visit(ASTBlock node, Object data)
	{
		data = node.childrenAccept(this, data);
		return data;
	}

	public Object visit(ASTText node, Object data)
	{
		data = node.childrenAccept(this, data);
		return data;
	}

	public Object visit(ASTIfStatement node, Object data)
	{
		data = node.childrenAccept(this, data);
		return data;
	}

	public Object visit(ASTElseStatement node, Object data)
	{
		data = node.childrenAccept(this, data);
		return data;
	}

	public Object visit(ASTElseIfStatement node, Object data)
	{
		data = node.childrenAccept(this, data);
		return data;
	}

	public Object visit(ASTComment node, Object data)
	{
		data = node.childrenAccept(this, data);
		return data;
	}

	public Object visit(ASTObjectArray node, Object data)
	{
		data = node.childrenAccept(this, data);
		return data;
	}

	public Object visit(ASTWord node, Object data)
	{
		data = node.childrenAccept(this, data);
		return data;
	}

	public Object visit(ASTSetDirective node, Object data)
	{
		data = node.childrenAccept(this, data);
		return data;
	}

	public Object visit(ASTDirective node, Object data)
	{
		data = node.childrenAccept(this, data);
		return data;
	}
}
