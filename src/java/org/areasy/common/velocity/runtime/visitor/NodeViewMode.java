package org.areasy.common.velocity.runtime.visitor;

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

import org.areasy.common.velocity.runtime.parser.Token;
import org.areasy.common.velocity.runtime.parser.node.*;

/**
 * This class is simply a visitor implementation
 * that traverses the AST, produced by the Velocity
 * parsing process, and creates a visual structure
 * of the AST. This is primarily used for
 * debugging, but it useful for documentation
 * as well.
 *
 * @version $Id: NodeViewMode.java,v 1.1 2008/05/25 22:33:14 swd\stefan.damian Exp $
 */
public class NodeViewMode extends BaseVisitor
{
	private int indent = 0;
	private boolean showTokens = true;

	/**
	 * Indent child nodes to help visually identify
	 * the structure of the AST.
	 */
	private String indentString()
	{
		StringBuffer sb = new StringBuffer();
		for (int i = 0; i < indent; ++i)
		{
			sb.append("  ");
		}
		return sb.toString();
	}

	/**
	 * Display the type of nodes and optionally the
	 * first token.
	 */
	private Object showNode(Node node, Object data)
	{
		String tokens = "";
		String special = "";
		Token t;

		if (showTokens)
		{
			t = node.getFirstToken();

			if (t.specialToken != null && !t.specialToken.image.startsWith("##"))
			{
				special = t.specialToken.image;
			}

			tokens = " -> " + special + t.image;
		}

		System.out.println(indentString() + node + tokens);
		++indent;
		data = node.childrenAccept(this, data);
		--indent;
		return data;
	}

	/**
	 * Display a SimpleNode
	 */
	public Object visit(SimpleNode node, Object data)
	{
		return showNode(node, data);
	}

	/**
	 * Display an ASTprocess node
	 */
	public Object visit(ASTprocess node, Object data)
	{
		return showNode(node, data);
	}

	/**
	 * Display an ASTExpression node
	 */
	public Object visit(ASTExpression node, Object data)
	{
		return showNode(node, data);
	}

	/**
	 * Display an ASTAssignment node ( = )
	 */
	public Object visit(ASTAssignment node, Object data)
	{
		return showNode(node, data);
	}

	/**
	 * Display an ASTOrNode ( || )
	 */
	public Object visit(ASTOrNode node, Object data)
	{
		return showNode(node, data);
	}

	/**
	 * Display an ASTAndNode ( && )
	 */
	public Object visit(ASTAndNode node, Object data)
	{
		return showNode(node, data);
	}

	/**
	 * Display an ASTEQNode ( == )
	 */
	public Object visit(ASTEQNode node, Object data)
	{
		return showNode(node, data);
	}

	/**
	 * Display an ASTNENode ( != )
	 */
	public Object visit(ASTNENode node, Object data)
	{
		return showNode(node, data);
	}

	/**
	 * Display an ASTLTNode ( < )
	 */
	public Object visit(ASTLTNode node, Object data)
	{
		return showNode(node, data);
	}

	/**
	 * Display an ASTGTNode ( > )
	 */
	public Object visit(ASTGTNode node, Object data)
	{
		return showNode(node, data);
	}

	/**
	 * Display an ASTLENode ( <= )
	 */
	public Object visit(ASTLENode node, Object data)
	{
		return showNode(node, data);
	}

	/**
	 * Display an ASTGENode ( >= )
	 */
	public Object visit(ASTGENode node, Object data)
	{
		return showNode(node, data);
	}

	/**
	 * Display an ASTAddNode ( + )
	 */
	public Object visit(ASTAddNode node, Object data)
	{
		return showNode(node, data);
	}

	/**
	 * Display an ASTSubtractNode ( - )
	 */
	public Object visit(ASTSubtractNode node, Object data)
	{
		return showNode(node, data);
	}

	/**
	 * Display an ASTMulNode ( * )
	 */
	public Object visit(ASTMulNode node, Object data)
	{
		return showNode(node, data);
	}

	/**
	 * Display an ASTDivNode ( / )
	 */
	public Object visit(ASTDivNode node, Object data)
	{
		return showNode(node, data);
	}

	/**
	 * Display an ASTModNode ( % )
	 */
	public Object visit(ASTModNode node, Object data)
	{
		return showNode(node, data);
	}

	/**
	 * Display an ASTNotNode ( ! )
	 */
	public Object visit(ASTNotNode node, Object data)
	{
		return showNode(node, data);
	}

	/**
	 * Display an ASTNumberLiteral node
	 */
	public Object visit(ASTNumberLiteral node, Object data)
	{
		return showNode(node, data);
	}

	/**
	 * Display an ASTStringLiteral node
	 */
	public Object visit(ASTStringLiteral node, Object data)
	{
		return showNode(node, data);
	}

	/**
	 * Display an ASTIdentifier node
	 */
	public Object visit(ASTIdentifier node, Object data)
	{
		return showNode(node, data);
	}

	/**
	 * Display an ASTMethod node
	 */
	public Object visit(ASTMethod node, Object data)
	{
		return showNode(node, data);
	}

	/**
	 * Display an ASTReference node
	 */
	public Object visit(ASTReference node, Object data)
	{
		return showNode(node, data);
	}

	/**
	 * Display an ASTTrue node
	 */
	public Object visit(ASTTrue node, Object data)
	{
		return showNode(node, data);
	}

	/**
	 * Display an ASTFalse node
	 */
	public Object visit(ASTFalse node, Object data)
	{
		return showNode(node, data);
	}

	/**
	 * Display an ASTBlock node
	 */
	public Object visit(ASTBlock node, Object data)
	{
		return showNode(node, data);
	}

	/**
	 * Display an ASTText node
	 */
	public Object visit(ASTText node, Object data)
	{
		return showNode(node, data);
	}

	/**
	 * Display an ASTIfStatement node
	 */
	public Object visit(ASTIfStatement node, Object data)
	{
		return showNode(node, data);
	}

	/**
	 * Display an ASTElseStatement node
	 */
	public Object visit(ASTElseStatement node, Object data)
	{
		return showNode(node, data);
	}

	/**
	 * Display an ASTElseIfStatement node
	 */
	public Object visit(ASTElseIfStatement node, Object data)
	{
		return showNode(node, data);
	}

	public Object visit(ASTObjectArray node, Object data)
	{
		return showNode(node, data);
	}

	public Object visit(ASTDirective node, Object data)
	{
		return showNode(node, data);
	}

	public Object visit(ASTWord node, Object data)
	{
		return showNode(node, data);
	}

	public Object visit(ASTSetDirective node, Object data)
	{
		return showNode(node, data);
	}
}
