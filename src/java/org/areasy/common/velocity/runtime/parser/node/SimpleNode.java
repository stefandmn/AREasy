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
import org.areasy.common.velocity.base.ResourceNotFoundException;
import org.areasy.common.velocity.context.InternalContextAdapter;
import org.areasy.common.velocity.runtime.parser.ParseErrorException;
import org.areasy.common.velocity.runtime.parser.ReferenceException;
import org.areasy.common.velocity.runtime.RuntimeService;
import org.areasy.common.velocity.runtime.parser.Parser;
import org.areasy.common.velocity.runtime.parser.Token;

import java.io.IOException;
import java.io.Writer;


public class SimpleNode implements Node
{
	protected RuntimeService rsvc = null;

	protected Node parent;
	protected Node[] children;
	protected int id;
	protected Parser parser;

	protected int info; // added
	public boolean state;
	protected boolean invalid = false;

	/* Added */
	protected Token first, last;

	public SimpleNode(int i)
	{
		id = i;
	}

	public SimpleNode(Parser p, int i)
	{
		this(i);
		parser = p;
	}

	public void jjtOpen()
	{
		first = parser.getToken(1); // added
	}

	public void jjtClose()
	{
		last = parser.getToken(0); // added
	}

	public void setFirstToken(Token t)
	{
		this.first = t;
	}

	public Token getFirstToken()
	{
		return first;
	}

	public Token getLastToken()
	{
		return last;
	}

	public void jjtSetParent(Node n)
	{
		parent = n;
	}

	public Node jjtGetParent()
	{
		return parent;
	}

	public void jjtAddChild(Node n, int i)
	{
		if (children == null)
		{
			children = new Node[i + 1];
		}
		else if (i >= children.length)
		{
			Node c[] = new Node[i + 1];
			System.arraycopy(children, 0, c, 0, children.length);
			children = c;
		}
		children[i] = n;
	}

	public Node jjtGetChild(int i)
	{
		return children[i];
	}

	public int jjtGetNumChildren()
	{
		return (children == null) ? 0 : children.length;
	}

	/**
	 * Accept the visitor. *
	 */
	public Object jjtAccept(ParserVisitor visitor, Object data)
	{
		return visitor.visit(this, data);
	}

	/**
	 * Accept the visitor. *
	 */
	public Object childrenAccept(ParserVisitor visitor, Object data)
	{
		if (children != null)
		{
			for (int i = 0; i < children.length; ++i)
			{
				children[i].jjtAccept(visitor, data);
			}
		}
		return data;
	}

	public String toString(String prefix)
	{
		return prefix + toString();
	}

	public void dump(String prefix)
	{
		System.out.println(toString(prefix));
		if (children != null)
		{
			for (int i = 0; i < children.length; ++i)
			{
				SimpleNode n = (SimpleNode) children[i];
				if (n != null)
				{
					n.dump(prefix + " ");
				}
			}
		}
	}

	public String literal()
	{
		Token t = first;
		StringBuffer sb = new StringBuffer(t.image);

		while (t != last)
		{
			t = t.next;
			sb.append(t.image);
		}

		return sb.toString();
	}

	public Object init(InternalContextAdapter context, Object data) throws Exception
	{
		rsvc = (RuntimeService) data;

		int i, k = jjtGetNumChildren();

		for (i = 0; i < k; i++)
		{
			try
			{
				jjtGetChild(i).init(context, data);
			}
			catch (ReferenceException re)
			{
				logger.error(re);
			}
		}

		return data;
	}

	public boolean evaluate(InternalContextAdapter context) throws MethodInvocationException
	{
		return false;
	}

	public Object value(InternalContextAdapter context) throws MethodInvocationException
	{
		return null;
	}

	public boolean render(InternalContextAdapter context, Writer writer) throws IOException, MethodInvocationException, ParseErrorException, ResourceNotFoundException
	{
		int i, k = jjtGetNumChildren();

		for (i = 0; i < k; i++)
		{
			jjtGetChild(i).render(context, writer);
		}

		return true;
	}

	public Object execute(Object o, InternalContextAdapter context)
			throws MethodInvocationException
	{
		return null;
	}

	public int getType()
	{
		return id;
	}

	public void setInfo(int info)
	{
		this.info = info;
	}

	public int getInfo()
	{
		return info;
	}

	public void setInvalid()
	{
		invalid = true;
	}

	public boolean isInvalid()
	{
		return invalid;
	}

	public int getLine()
	{
		return first.beginLine;
	}

	public int getColumn()
	{
		return first.beginColumn;
	}
}

