package org.areasy.common.velocity.runtime.directive;

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

import org.areasy.common.logger.Logger;
import org.areasy.common.logger.LoggerFactory;
import org.areasy.common.velocity.base.MethodInvocationException;
import org.areasy.common.velocity.base.ResourceNotFoundException;
import org.areasy.common.velocity.base.Template;
import org.areasy.common.velocity.context.InternalContextAdapter;
import org.areasy.common.velocity.runtime.parser.ParseErrorException;
import org.areasy.common.velocity.runtime.parser.node.Node;
import org.areasy.common.velocity.runtime.parser.node.SimpleNode;

import java.io.IOException;
import java.io.Writer;

/**
 * Pluggable directive that handles the <code>#parse()</code>
 * statement in VTL.
 * <p/>
 * <pre>
 * Notes:
 * -----
 *  1) The parsed source material can only come from somewhere in
 *    the TemplateRoot tree for security reasons. There is no way
 *    around this.  If you want to include content from elsewhere on
 *    your disk, use a link from somwhere under Template Root to that
 *    content.
 * <p/>
 *  2) There is a limited parse depth.  It is set as a property
 *    "parse_directive.maxdepth = 10"  for example.  There is a 20 iteration
 *    safety in the event that the parameter isn't set.
 * </pre>
 *
 * @version $Id: Parse.java,v 1.1 2008/05/25 22:33:14 swd\stefan.damian Exp $
 */
public class Parse extends InputBase
{
	/** the logger */
	private static Logger logger = LoggerFactory.getLog(Parse.class.getName());

	/**
	 * Return name of this directive.
	 */
	public String getName()
	{
		return "parse";
	}

	/**
	 * Return type of this directive.
	 */
	public int getType()
	{
		return LINE;
	}

	/**
	 * iterates through the argument list and renders every
	 * argument that is appropriate.  Any non appropriate
	 * arguments are logged, but render() continues.
	 */
	public boolean render(InternalContextAdapter context, Writer writer, Node node) throws IOException, ResourceNotFoundException, ParseErrorException, MethodInvocationException
	{
		if (node.jjtGetChild(0) == null)
		{
			logger.error("#parse() error :  null argument");
			return false;
		}

		Object value = node.jjtGetChild(0).value(context);

		if (value == null)
		{
			logger.error("#parse() error :  null argument");
			return false;
		}

		String arg = value.toString();
		Object[] templateStack = context.getTemplateNameStack();

		if (templateStack.length >= rsvc.getConfiguration().getInt("engine.directive.parse.max.depth", 20))
		{
			StringBuffer path = new StringBuffer();

			for (int i = 0; i < templateStack.length; ++i)
			{
				path.append(" > " + templateStack[i]);
			}

			logger.error("Max recursion depth reached (" + templateStack.length + ")" + " File stack:" + path);
			return false;
		}

		Template t = null;

		try
		{
			t = rsvc.getTemplate(arg, getInputEncoding(context));
		}
		catch (ResourceNotFoundException rnfe)
		{
			logger.error("#parse(): cannot find template '" + arg + "', called from template " + context.getCurrentTemplateName() + " at (" + getLine() + ", " + getColumn() + ")");
			throw rnfe;
		}
		catch (ParseErrorException pee)
		{
			logger.error("#parse(): syntax error in #parse()-ed template '" + arg + "', called from template " + context.getCurrentTemplateName() + " at (" + getLine() + ", " + getColumn() + ")");

			throw pee;
		}
		catch (Exception e)
		{
			logger.error("#parse() : arg = " + arg + ".  Exception : " + e);
			return false;
		}

		try
		{
			context.pushCurrentTemplateName(arg);
			((SimpleNode) t.getData()).render(context, writer);
		}
		catch (Exception e)
		{
			if (e instanceof MethodInvocationException) throw (MethodInvocationException) e;

			logger.error("Exception rendering #parse( " + arg + " )  : " + e);
			return false;
		}
		finally
		{
			context.popCurrentTemplateName();
		}

		return true;
	}
}

