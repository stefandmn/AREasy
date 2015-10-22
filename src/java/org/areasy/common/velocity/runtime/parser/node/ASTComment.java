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
import org.areasy.common.velocity.runtime.parser.Token;

import java.io.IOException;
import java.io.Writer;

/**
 * Represents all comments...
 *
 * @version $Id: ASTComment.java,v 1.1 2008/05/25 22:33:08 swd\stefan.damian Exp $
 */
public class ASTComment extends SimpleNode
{
	private static final char[] ZILCH = "".toCharArray();

	private char[] carr;

	public ASTComment(int id)
	{
		super(id);
	}

	public ASTComment(Parser p, int id)
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
	 * We need to make sure we catch any of the dreaded MORE tokens.
	 */
	public Object init(InternalContextAdapter context, Object data) throws Exception
	{
		Token t = getFirstToken();

		int loc1 = t.image.indexOf("##");
		int loc2 = t.image.indexOf("#*");

		if (loc1 == -1 && loc2 == -1)
		{
			carr = ZILCH;
		}
		else
		{
			carr = t.image.substring(0, (loc1 == -1) ? loc2 : loc1).toCharArray();
		}

		return data;
	}

	public boolean render(InternalContextAdapter context, Writer writer) throws IOException, MethodInvocationException, ParseErrorException, ResourceNotFoundException
	{
		writer.write(carr);

		return true;
	}

}
