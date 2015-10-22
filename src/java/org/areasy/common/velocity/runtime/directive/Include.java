package org.areasy.common.velocity.runtime.directive;

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
import org.areasy.common.velocity.runtime.RuntimeService;
import org.areasy.common.velocity.runtime.parser.ParserTreeConstants;
import org.areasy.common.velocity.runtime.parser.node.Node;
import org.areasy.common.velocity.runtime.resource.Resource;

import java.io.IOException;
import java.io.Writer;

/**
 * Pluggable directive that handles the #include() statement in VTL.
 * This #include() can take multiple arguments of either
 * StringLiteral or Reference.
 * <p/>
 * Notes:
 * -----
 * 1) The included source material can only come from somewhere in
 * the TemplateRoot tree for security reasons. There is no way
 * around this.  If you want to include content from elsewhere on
 * your disk, use a link from somwhere under Template Root to that
 * content.
 * <p/>
 * 2) By default, there is no output to the render stream in the event of
 * a problem.  You can override this behavior with two property values :
 * include.output.errormsg.start
 * include.output.errormsg.end
 * If both are defined in velocity.properties, they will be used to
 * in the render output to bracket the arg string that caused the
 * problem.
 * Ex. : if you are working in html then
 * include.output.errormsg.start=<!-- #include error :
 * include.output.errormsg.end= -->
 * might be an excellent way to start...
 * <p/>
 * 3) As noted above, #include() can take multiple arguments.
 * Ex : #include( "foo.vm" "bar.vm" $foo )
 * will simply include all three if valid to output w/o any
 * special separator.
 *
 * @version $Id: Include.java,v 1.1 2008/05/25 22:33:13 swd\stefan.damian Exp $
 */
public class Include extends InputBase
{
	/** the logger */
	private static Logger logger = LoggerFactory.getLog(Include.class.getName());

	private String outputMsgStart = "";
	private String outputMsgEnd = "";

	/**
	 * Return name of this directive.
	 */
	public String getName()
	{
		return "include";
	}

	/**
	 * Return type of this directive.
	 */
	public int getType()
	{
		return LINE;
	}

	/**
	 * simple init - init the tree and get the elementKey from
	 * the AST
	 */
	public void init(RuntimeService rs, InternalContextAdapter context,
					 Node node)
			throws Exception
	{
		super.init(rs, context, node);

		/*
		 *  get the msg, and add the space so we don't have to
		 *  do it each time
		 */
		outputMsgStart = rsvc.getConfiguration().getString("engine.directive.include.output.errormsg.start");
		outputMsgStart = outputMsgStart + " ";

		outputMsgEnd = rsvc.getConfiguration().getString("engine.directive.include.output.errormsg.end");
		outputMsgEnd = " " + outputMsgEnd;
	}

	/**
	 * iterates through the argument list and renders every
	 * argument that is appropriate.  Any non appropriate
	 * arguments are logged, but render() continues.
	 */
	public boolean render(InternalContextAdapter context, Writer writer, Node node) throws IOException, MethodInvocationException, ResourceNotFoundException
	{
		int argCount = node.jjtGetNumChildren();

		for (int i = 0; i < argCount; i++)
		{
			Node n = node.jjtGetChild(i);

			if (n.getType() == ParserTreeConstants.JJTSTRINGLITERAL ||
					n.getType() == ParserTreeConstants.JJTREFERENCE)
			{
				if (!renderOutput(n, context, writer))
				{
					outputErrorToStream(writer, "error with arg " + i + " please see log.");
				}
			}
			else
			{
				logger.error("#include() error : invalid argument type : " + n.toString());
				outputErrorToStream(writer, "error with arg " + i + " please see log.");
			}
		}

		return true;
	}

	/**
	 * does the actual rendering of the included file
	 *
	 * @param node    AST argument of type StringLiteral or Reference
	 * @param context valid context so we can render References
	 * @param writer  output Writer
	 * @return boolean success or failure.  failures are logged
	 */
	private boolean renderOutput(Node node, InternalContextAdapter context, Writer writer) throws IOException, MethodInvocationException, ResourceNotFoundException
	{
		String arg = "";

		if (node == null)
		{
			logger.error("#include() error :  null argument");
			return false;
		}

		Object value = node.value(context);
		if (value == null)
		{
			logger.error("#include() error :  null argument");
			return false;
		}

		arg = value.toString();

		Resource resource = null;

		try
		{
			resource = rsvc.getContent(arg, getInputEncoding(context));
		}
		catch (ResourceNotFoundException rnfe)
		{
			logger.error("#include(): cannot find resource '" + arg + "', called from template " + context.getCurrentTemplateName() + " at (" + getLine() + ", " + getColumn() + ")");
			throw rnfe;
		}

		catch (Exception e)
		{
			logger.error("#include(): arg = '" + arg + "', called from template " + context.getCurrentTemplateName() + " at (" + getLine() + ", " + getColumn() + ") : " + e);
		}

		if (resource == null) return false;

		writer.write((String) resource.getData());
		return true;
	}

	/**
	 * Puts a message to the render output stream if ERRORMSG_START / END
	 * are valid property strings.  Mainly used for end-user template
	 * debugging.
	 */
	private void outputErrorToStream(Writer writer, String msg) throws IOException
	{
		if (outputMsgStart != null && outputMsgEnd != null)
		{
			writer.write(outputMsgStart);
			writer.write(msg);
			writer.write(outputMsgEnd);
		}
		
		return;
	}
}
