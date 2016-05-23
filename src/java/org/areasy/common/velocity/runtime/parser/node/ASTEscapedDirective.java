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

import org.areasy.common.velocity.context.InternalContextAdapter;
import org.areasy.common.velocity.runtime.parser.Parser;

import java.io.IOException;
import java.io.Writer;

/**
 * This class is responsible for handling EscapedDirectives
 * in VTL.
 * <p/>
 * Please look at the Parser.jjt file which is
 * what controls the generation of this class.
 *
 * @version $Id: ASTEscapedDirective.java,v 1.1 2008/05/25 22:33:08 swd\stefan.damian Exp $
 */
public class ASTEscapedDirective extends SimpleNode
{
	public ASTEscapedDirective(int id)
	{
		super(id);
	}

	public ASTEscapedDirective(Parser p, int id)
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

	public boolean render(InternalContextAdapter context, Writer writer)
			throws IOException
	{
		writer.write(getFirstToken().image);
		return true;
	}

}
