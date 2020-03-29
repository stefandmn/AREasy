package org.areasy.common.velocity.runtime.parser.node;

/*
 * Copyright (c) 2007-2020 AREasy Runtime
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
import org.areasy.common.velocity.runtime.parser.Parser;

/**
 * This class is responsible for handling the Else VTL control statement.
 * <p/>
 * Please look at the Parser.jjt file which is
 * what controls the generation of this class.
 *
 * @version $Id: ASTElseStatement.java,v 1.1 2008/05/25 22:33:08 swd\stefan.damian Exp $
 */
public class ASTElseStatement extends SimpleNode
{
	public ASTElseStatement(int id)
	{
		super(id);
	}

	public ASTElseStatement(Parser p, int id)
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
	 * An ASTElseStatement always evaluates to
	 * true. Basically behaves like an #if(true).
	 */
	public boolean evaluate(InternalContextAdapter context)
	{
		return true;
	}
}

