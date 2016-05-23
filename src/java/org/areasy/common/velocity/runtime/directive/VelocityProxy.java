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
import org.areasy.common.velocity.base.MethodInvocationException;
import org.areasy.common.velocity.context.InternalContextAdapter;
import org.areasy.common.velocity.context.VelocitymacroContext;
import org.areasy.common.velocity.runtime.RuntimeService;
import org.areasy.common.velocity.runtime.parser.ParserTreeConstants;
import org.areasy.common.velocity.runtime.parser.Token;
import org.areasy.common.velocity.runtime.parser.node.Node;
import org.areasy.common.velocity.runtime.parser.node.SimpleNode;
import org.areasy.common.velocity.runtime.visitor.VMReferenceMungeVisitor;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;
import java.io.Writer;
import java.util.HashMap;

/**
 * VelocityProxy.java
 * <p/>
 * a proxy Directive-derived object to fit with the current directive system
 *
 * @version $Id: VelocityProxy.java,v 1.1 2008/05/25 22:33:14 swd\stefan.damian Exp $
 */
public class VelocityProxy extends Directive
{
	/** the logger */
	private static Logger logger = LoggerFactory.getLog(VelocityProxy.class.getName());

	private String macroName = "";
	private String macroBody = "";
	private String[] argArray = null;
	private SimpleNode nodeTree = null;
	private int numMacroArgs = 0;
	private String namespace = "";

	private boolean init = false;
	private String[] callingArgs;
	private int[] callingArgTypes;
	private HashMap proxyArgHash = new HashMap();


	/**
	 * Return name of this Velocity.
	 */
	public String getName()
	{
		return macroName;
	}

	/**
	 * Velocitys are always LINE
	 * type directives.
	 */
	public int getType()
	{
		return LINE;
	}

	/**
	 * sets the directive name of this VM
	 */
	public void setName(String name)
	{
		macroName = name;
	}

	/**
	 * sets the array of arguments specified in the macro definition
	 */
	public void setArgArray(String[] arr)
	{
		argArray = arr;

		/*
		 *  get the arg count from the arg array.  remember that the arg array
		 *  has the macro name as it's 0th element
		 */

		numMacroArgs = argArray.length - 1;
	}

	public void setNodeTree(SimpleNode tree)
	{
		nodeTree = tree;
	}

	/**
	 * returns the number of ars needed for this VM
	 */
	public int getNumArgs()
	{
		return numMacroArgs;
	}

	/**
	 * Sets the orignal macro body.  This is simply the cat of the macroArray, but the
	 * Macro object creates this once during parsing, and everyone shares it.
	 * Note : it must not be modified.
	 */
	public void setMacrobody(String mb)
	{
		macroBody = mb;
	}

	public void setNamespace(String ns)
	{
		this.namespace = ns;
	}

	/**
	 * Renders the macro using the context
	 */
	public boolean render(InternalContextAdapter context, Writer writer, Node node) throws IOException, MethodInvocationException
	{
		try
		{
			if (nodeTree != null)
			{
				if (!init)
				{
					nodeTree.init(context, rsvc);
					init = true;
				}

				VelocitymacroContext vmc = new VelocitymacroContext(context, rsvc);

				for (int i = 1; i < argArray.length; i++)
				{
					VMProxyArg arg = (VMProxyArg) proxyArgHash.get(argArray[i]);
					vmc.addVMProxyArg(arg);
				}

				nodeTree.render(vmc, writer);
			}
			else logger.error("VM error : " + macroName + ". Null AST");
		}
		catch (Exception e)
		{
			if (e instanceof MethodInvocationException) throw (MethodInvocationException) e;
			logger.error("VelocityProxy.render() : exception VM = #" + macroName + "() : " + e.getMessage());
			logger.debug("Exception", e);
		}

		return true;
	}

	/**
	 * The major meat of VelocityProxy, init() checks the # of arguments, patches the
	 * macro body, renders the macro into an AST, and then inits the AST, so it is ready
	 * for quick rendering.  Note that this is only AST dependant stuff. Not context.
	 */
	public void init(RuntimeService rs, InternalContextAdapter context, Node node) throws Exception
	{
		super.init(rs, context, node);

		int i = node.jjtGetNumChildren();

		if (getNumArgs() != i)
		{
			logger.error("VM #" + macroName + ": error : too " + ((getNumArgs() > i) ? "few" : "many") + " arguments to macro. Wanted " + getNumArgs() + " got " + i);
			return;
		}

		callingArgs = getArgArray(node);

		setupMacro(callingArgs, callingArgTypes);

		return;
	}

	/**
	 * basic VM setup.  Sets up the proxy args for this
	 * use, and parses the tree
	 */
	public boolean setupMacro(String[] callArgs, int[] callArgTypes)
	{
		setupProxyArgs(callArgs, callArgTypes);
		parseTree(callArgs);

		return true;
	}

	/**
	 * parses the macro.  We need to do this here, at init time, or else
	 * the local-scope template feature is hard to get to work :)
	 */
	private void parseTree(String[] callArgs)
	{
		try
		{
			BufferedReader br = new BufferedReader(new StringReader(macroBody));

			nodeTree = rsvc.parse(br, namespace, false);

			HashMap hm = new HashMap();

			for (int i = 1; i < argArray.length; i++)
			{
				String arg = callArgs[i - 1];

				if (arg.charAt(0) == '$') hm.put(argArray[i], arg);
			}

			VMReferenceMungeVisitor v = new VMReferenceMungeVisitor(hm);
			nodeTree.jjtAccept(v, null);
		}
		catch (Exception e)
		{
			logger.error("VelocityManager.parseTree() : exception " + macroName + " : " + e.getMessage());
			logger.debug("Exception", e);
		}
	}

	private void setupProxyArgs(String[] callArgs, int[] callArgTypes)
	{
		for (int i = 1; i < argArray.length; i++)
		{
			VMProxyArg arg = new VMProxyArg(rsvc, argArray[i], callArgs[i - 1], callArgTypes[i - 1]);
			proxyArgHash.put(argArray[i], arg);
		}
	}

	/**
	 * Gets the args to the VM from the instance-use AST
	 */
	private String[] getArgArray(Node node)
	{
		int numArgs = node.jjtGetNumChildren();

		String args[] = new String[numArgs];
		callingArgTypes = new int[numArgs];

		int i = 0;
		Token t = null;
		Token tLast = null;

		while (i < numArgs)
		{
			args[i] = "";
			callingArgTypes[i] = node.jjtGetChild(i).getType();

			if (false && node.jjtGetChild(i).getType() == ParserTreeConstants.JJTSTRINGLITERAL)
			{
				args[i] += node.jjtGetChild(i).getFirstToken().image.substring(1, node.jjtGetChild(i).getFirstToken().image.length() - 1);
			}
			else
			{
				t = node.jjtGetChild(i).getFirstToken();
				tLast = node.jjtGetChild(i).getLastToken();

				while (t != tLast)
				{
					args[i] += t.image;
					t = t.next;
				}

				args[i] += t.image;
			}

			i++;
		}

		return args;
	}
}










