package org.areasy.common.velocity.runtime.directive;

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

import org.areasy.common.logger.Logger;
import org.areasy.common.logger.LoggerFactory;
import org.areasy.common.velocity.context.InternalContextAdapter;
import org.areasy.common.velocity.runtime.RuntimeService;
import org.areasy.common.velocity.runtime.parser.ParseException;
import org.areasy.common.velocity.runtime.parser.ParserTreeConstants;
import org.areasy.common.velocity.runtime.parser.Token;
import org.areasy.common.velocity.runtime.parser.node.Node;
import org.areasy.common.velocity.runtime.parser.node.NodeUtils;

import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.List;

/**
 * Macro.java
 * <p/>
 * Macro implements the macro definition directive of VTL.
 * <p/>
 * example :
 * <p/>
 * #macro( isnull $i )
 * #if( $i )
 * $i
 * #end
 * #end
 * <p/>
 * This object is used at parse time to mainly process and register the
 * macro.  It is used inline in the parser when processing a directive.
 *
 * @version $Id: Macro.java,v 1.1 2008/05/25 22:33:14 swd\stefan.damian Exp $
 */
public class Macro extends Directive
{
	/** the logger */
	private static Logger logger = LoggerFactory.getLog(Macro.class.getName());

	/**
	 * Return name of this directive.
	 */
	public String getName()
	{
		return "macro";
	}

	/**
	 * Return type of this directive.
	 */
	public int getType()
	{
		return BLOCK;
	}

	/**
	 * render() doesn't do anything in the final output rendering.
	 * There is no output from a #macro() directive.
	 */
	public boolean render(InternalContextAdapter context, Writer writer, Node node) throws IOException
	{
		/*
		 *  do nothing : We never render.  The VelocityProxy object does that
		 */

		return true;
	}

	public void init(RuntimeService rs, InternalContextAdapter context, Node node) throws Exception
	{
		super.init(rs, context, node);

		/*
		 * again, don't do squat.  We want the AST of the macro
		 * block to hang off of this but we don't want to
		 * init it... it's useless...
		 */

		return;
	}

	/**
	 * Used by Parser.java to process VMs withing the parsing process
	 * <p/>
	 * processAndRegister() doesn't actually render the macro to the output
	 * Processes the macro body into the internal representation used by the
	 * VelocityProxy objects, and if not currently used, adds it
	 * to the macro Factory
	 */
	public static void processAndRegister(RuntimeService rs, Node node, String sourceTemplate) throws ParseException
	{
		int numArgs = node.jjtGetNumChildren();

		if (numArgs < 2)
		{
			logger.error("#macro error : Velocity must have name as 1st argument to #macro(). #args = " + numArgs);

			throw new MacroParseException("First argument to #macro() must be macro name.");
		}

		/*
		 *  lets make sure that the first arg is an ASTWord
		 */

		int firstType = node.jjtGetChild(0).getType();

		if (firstType != ParserTreeConstants.JJTWORD)
		{
			Token t = node.jjtGetChild(0).getFirstToken();

			throw new MacroParseException("First argument to #macro() must be a token without surrounding \' or \", which specifies"
					+ " the macro name.  Currently it is a " + ParserTreeConstants.jjtNodeName[firstType]);

		}

		/*
		 *  get the arguments to the use of the VM
		 */

		String argArray[] = getArgArray(node);

		/*
		 *   now, try and eat the code block. Pass the root.
		 */

		List macroArray = getASTAsStringArray(node.jjtGetChild(numArgs - 1));

		/*
		 *  make a big string out of our macro
		 */

		StringBuffer temp = new StringBuffer();

		for (int i = 0; i < macroArray.size(); i++)
		{
			temp.append(macroArray.get(i));
		}

		String macroBody = temp.toString();

		/*
		 * now, try to add it.  The Factory controls permissions,
		 * so just give it a whack...
		 */

		boolean bRet = rs.addVelocityMacro(argArray[0], macroBody, argArray, sourceTemplate);

		return;
	}


	/**
	 * creates an array containing the literal
	 * strings in the macro arguement
	 */
	private static String[] getArgArray(Node node)
	{
		/*
		 *  remember : this includes the block tree
		 */

		int numArgs = node.jjtGetNumChildren();

		numArgs--;  // avoid the block tree...

		String argArray[] = new String[numArgs];

		int i = 0;

		/*
		 *  eat the args
		 */

		while (i < numArgs)
		{
			argArray[i] = node.jjtGetChild(i).getFirstToken().image;

			/*
			 *  trim off the leading $ for the args after the macro name.
			 *  saves everyone else from having to do it
			 */

			if (i > 0 && argArray[i].startsWith("$")) argArray[i] = argArray[i].substring(1, argArray[i].length());

			i++;
		}

		if (logger.isDebugEnabled())
		{
			logger.debug("Macro.getArgArray() : #args = " + numArgs);
			logger.debug(argArray[0] + "(");

			for (i = 1; i < numArgs; i++)
			{
				logger.debug(" " + argArray[i]);
			}

			logger.debug(" )");
		}

		return argArray;
	}

	/**
	 * Returns an array of the literal rep of the AST
	 */
	private static List getASTAsStringArray(Node rootNode)
	{
		Token t = rootNode.getFirstToken();
		Token tLast = rootNode.getLastToken();

		ArrayList list = new ArrayList();

		t = rootNode.getFirstToken();

		while (t != tLast)
		{
			list.add(NodeUtils.tokenLiteral(t));
			t = t.next;
		}

		list.add(NodeUtils.tokenLiteral(t));

		return list;
	}
}
