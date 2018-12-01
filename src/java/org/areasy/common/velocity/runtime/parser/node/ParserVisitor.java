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

public interface ParserVisitor
{
	public Object visit(SimpleNode node, Object data);

	public Object visit(ASTprocess node, Object data);

	public Object visit(ASTComment node, Object data);

	public Object visit(ASTNumberLiteral node, Object data);

	public Object visit(ASTStringLiteral node, Object data);

	public Object visit(ASTIdentifier node, Object data);

	public Object visit(ASTWord node, Object data);

	public Object visit(ASTDirective node, Object data);

	public Object visit(ASTBlock node, Object data);

	public Object visit(ASTObjectArray node, Object data);

	public Object visit(ASTMethod node, Object data);

	public Object visit(ASTReference node, Object data);

	public Object visit(ASTTrue node, Object data);

	public Object visit(ASTFalse node, Object data);

	public Object visit(ASTText node, Object data);

	public Object visit(ASTIfStatement node, Object data);

	public Object visit(ASTElseStatement node, Object data);

	public Object visit(ASTElseIfStatement node, Object data);

	public Object visit(ASTSetDirective node, Object data);

	public Object visit(ASTExpression node, Object data);

	public Object visit(ASTAssignment node, Object data);

	public Object visit(ASTOrNode node, Object data);

	public Object visit(ASTAndNode node, Object data);

	public Object visit(ASTEQNode node, Object data);

	public Object visit(ASTNENode node, Object data);

	public Object visit(ASTLTNode node, Object data);

	public Object visit(ASTGTNode node, Object data);

	public Object visit(ASTLENode node, Object data);

	public Object visit(ASTGENode node, Object data);

	public Object visit(ASTAddNode node, Object data);

	public Object visit(ASTSubtractNode node, Object data);

	public Object visit(ASTMulNode node, Object data);

	public Object visit(ASTDivNode node, Object data);

	public Object visit(ASTModNode node, Object data);

	public Object visit(ASTNotNode node, Object data);
}
