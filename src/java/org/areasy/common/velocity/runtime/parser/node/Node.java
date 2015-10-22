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

import org.areasy.common.logger.Logger;
import org.areasy.common.logger.LoggerFactory;
import org.areasy.common.velocity.base.MethodInvocationException;
import org.areasy.common.velocity.base.ResourceNotFoundException;
import org.areasy.common.velocity.context.InternalContextAdapter;
import org.areasy.common.velocity.runtime.parser.ParseErrorException;
import org.areasy.common.velocity.runtime.parser.Token;

import java.io.IOException;
import java.io.Writer;

/**
 * All AST nodes must implement this interface.  It provides basic
 * machinery for constructing the parent and child relationships
 * between nodes.
 */

public interface Node
{

	/** Logger for any nodes*/
	static Logger logger = LoggerFactory.getLog(Node.class.getName());

	/**
	 * This method is called after the node has been made the current
	 * node.  It indicates that child nodes can now be added to it.
	 */
	public void jjtOpen();

	/**
	 * This method is called after all the child nodes have been
	 * added.
	 */
	public void jjtClose();

	/**
	 * This pair of methods are used to inform the node of its
	 * parent.
	 */
	public void jjtSetParent(Node n);

	public Node jjtGetParent();

	/**
	 * This method tells the node to add its argument to the node's
	 * list of children.
	 */
	public void jjtAddChild(Node n, int i);

	/**
	 * This method returns a child node.  The children are numbered
	 * from zero, left to right.
	 */
	public Node jjtGetChild(int i);

	/**
	 * Return the number of children the node has.
	 */
	public int jjtGetNumChildren();

	/**
	 * Accept the visitor. *
	 */
	public Object jjtAccept(ParserVisitor visitor, Object data);

	public Object childrenAccept(ParserVisitor visitor, Object data);

	// added
	public Token getFirstToken();

	public Token getLastToken();

	public int getType();

	public Object init(InternalContextAdapter context, Object data) throws Exception;

	public boolean evaluate(InternalContextAdapter context) throws MethodInvocationException;

	public Object value(InternalContextAdapter context) throws MethodInvocationException;

	public boolean render(InternalContextAdapter context, Writer writer) throws IOException, MethodInvocationException, ParseErrorException, ResourceNotFoundException;

	public Object execute(Object o, InternalContextAdapter context) throws MethodInvocationException;

	public void setInfo(int info);

	public int getInfo();

	public String literal();

	public void setInvalid();

	public boolean isInvalid();

	public int getLine();

	public int getColumn();
}
