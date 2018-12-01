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

import org.areasy.common.velocity.base.MethodInvocationException;
import org.areasy.common.velocity.base.ResourceNotFoundException;
import org.areasy.common.velocity.context.InternalContextAdapter;
import org.areasy.common.velocity.runtime.parser.ParseErrorException;
import org.areasy.common.velocity.runtime.directive.Directive;
import org.areasy.common.velocity.runtime.parser.Parser;

import java.io.IOException;
import java.io.Writer;

/**
 * This class is responsible for handling the pluggable
 * directives in VTL. ex.  #foreach()
 * <p/>
 * Please look at the Parser.jjt file which is
 * what controls the generation of this class.
 *
 * @version $Id: ASTDirective.java,v 1.1 2008/05/25 22:33:09 swd\stefan.damian Exp $
 */
public class ASTDirective extends SimpleNode
{
	private Directive directive;
	private String directiveName = "";
	private boolean isDirective;

	public ASTDirective(int id)
	{
		super(id);
	}

	public ASTDirective(Parser p, int id)
	{
		super(p, id);
	}

	/**
	 * Accept the visitor.
	 */
	public Object jjtAccept(ParserVisitor visitor, Object data)
	{
		return visitor.visit(this, data);
	}

	public Object init(InternalContextAdapter context, Object data) throws Exception
	{
		super.init(context, data);

		if (parser.isDirective(directiveName))
		{
			isDirective = true;

			directive = (Directive) parser.getDirective(directiveName).getClass().newInstance();

			directive.init(rsvc, context, this);

			directive.setLocation(getLine(), getColumn());
		}
		else if (rsvc.isVelocityMacro(directiveName, context.getCurrentTemplateName()))
		{
			isDirective = true;
			directive = (Directive) rsvc.getVelocityMacro(directiveName, context.getCurrentTemplateName());

			directive.init(rsvc, context, this);
			directive.setLocation(getLine(), getColumn());
		}
		else isDirective = false;

		return data;
	}

	public boolean render(InternalContextAdapter context, Writer writer) throws IOException, MethodInvocationException, ResourceNotFoundException, ParseErrorException
	{
		if (isDirective) directive.render(context, writer, this);
		else
		{
			writer.write("#");
			writer.write(directiveName);
		}

		return true;
	}

	/**
	 * Sets the directive name.  Used by the parser.  This keeps us from having to
	 * dig it out of the token stream and gives the parse the change to override.
	 */
	public void setDirectiveName(String str)
	{
		directiveName = str;
		return;
	}

	/**
	 * Gets the name of this directive.
	 */
	public String getDirectiveName()
	{
		return directiveName;
	}
}


