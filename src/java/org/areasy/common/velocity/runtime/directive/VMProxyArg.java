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
import org.areasy.common.velocity.context.DefaultInternalContextAdapter;
import org.areasy.common.velocity.context.InternalContextAdapter;
import org.areasy.common.velocity.context.VelocityContext;
import org.areasy.common.velocity.runtime.RuntimeService;
import org.areasy.common.velocity.runtime.parser.ParserTreeConstants;
import org.areasy.common.velocity.runtime.parser.node.ASTReference;
import org.areasy.common.velocity.runtime.parser.node.SimpleNode;

import java.io.BufferedReader;
import java.io.StringReader;
import java.io.StringWriter;

/**
 * The function of this class is to proxy for the calling parameter to the VM.
 * <p/>
 * This class is designed to be used in conjunction with the VMContext class
 * which knows how to get and set values via it, rather than a simple get()
 * or put() from a hashtable-like object.
 * <p/>
 * There is probably a lot of undocumented subtlty here, so step lightly.
 * <p/>
 * We rely on the observation that an instance of this object has a constant
 * state throughout its lifetime as it's bound to the use-instance of a VM.
 * In other words, it's created by the VelocityProxy class, to represent
 * one of the arguments to a VM in a specific template.  Since the template
 * is fixed (it's a file...), we don't have to worry that the args to the VM
 * will change.  Yes, the VM will be called in other templates, or in other
 * places on the same template, bit those are different use-instances.
 * <p/>
 * These arguments can be, in the lingo of
 * the parser, one of :
 * <ul>
 * <li> Reference() : anything that starts with '$'
 * <li> StringLiteral() : something like "$foo" or "hello geir"
 * <li> NumberLiteral() : 1, 2 etc
 * <li> IntegerRange() : [ 1..2] or [$foo .. $bar]
 * <li> ObjectArray() : [ "a", "b", "c"]
 * <li> True() : true
 * <li> False() : false
 * <li>Word() : not likely - this is simply allowed by the parser so we can have
 * syntactical sugar like #foreach($a in $b)  where 'in' is the Word
 * </ul>
 * Now, Reference(), StringLit, NumberLit, IntRange, ObjArr are all dynamic things, so
 * their value is gotten with the use of a context.  The others are constants.  The trick
 * we rely on is that the context rather than this class really represents the
 * state of the argument. We are simply proxying for the thing, returning the proper value
 * when asked, and storing the proper value in the appropriate context when asked.
 * <p/>
 * So, the hope here, so an instance of this can be shared across threads, is to
 * keep any dynamic stuff out of it, relying on trick of having the appropriate
 * context handed to us, and when a constant argument, letting VMContext punch that
 * into a local context.
 *
 * @version $Id: VMProxyArg.java,v 1.1 2008/05/25 22:33:13 swd\stefan.damian Exp $
 */
public class VMProxyArg
{

	/** the logger */
	private static Logger logger = LoggerFactory.getLog(VMProxyArg.class.getName());

	/**
	 * type of arg I will have
	 */
	private int type = 0;

	/**
	 * the AST if the type is such that it's dynamic (ex. JJTREFERENCE )
	 */
	private SimpleNode nodeTree = null;

	/**
	 * reference for the object if we proxy for a static arg like an NumberLiteral
	 */
	private Object staticObject = null;

	/**
	 * number of children in our tree if a reference
	 */
	private int numTreeChildren = 0;

	/**
	 * our identity in the current context
	 */
	private String contextReference = null;

	/**
	 * the reference we are proxying for
	 */
	private String callerReference = null;

	/**
	 * the 'de-dollared' reference if we are a ref but don't have a method attached
	 */
	private String singleLevelRef = null;

	/**
	 * by default, we are dynamic.  safest
	 */
	private boolean constant = false;

	/**
	 * in the event our type is switched - we don't care really what it is
	 */
	private final int GENERALSTATIC = -1;

	private RuntimeService rsvc = null;

	/**
	 * ctor for current impl
	 * <p/>
	 * takes the reference literal we are proxying for, the literal
	 * the VM we are for is called with...
	 *
	 * @param contextRef reference arg in the definition of the VM, used in the VM
	 * @param callerRef  reference used by the caller as an arg to the VM
	 * @param t          type of arg : JJTREFERENCE, JJTTRUE, etc
	 */
	public VMProxyArg(RuntimeService rs, String contextRef, String callerRef, int t)
	{
		rsvc = rs;

		contextReference = contextRef;
		callerReference = callerRef;
		type = t;

		setup();

		if (nodeTree != null) numTreeChildren = nodeTree.jjtGetNumChildren();

		if (type == ParserTreeConstants.JJTREFERENCE)
		{
			if (numTreeChildren == 0) singleLevelRef = ((ASTReference) nodeTree).getRootString();
		}
	}

	/**
	 * tells if arg we are poxying for is
	 * dynamic or constant.
	 *
	 * @return true of constant, false otherwise
	 */
	public boolean isConstant()
	{
		return constant;
	}

	/**
	 * Invoked by VMContext when Context.put() is called for a proxied reference.
	 *
	 * @param context context to modify via direct placement, or AST.setValue()
	 * @param o       new value of reference
	 * @return Object currently null
	 */
	public Object setObject(InternalContextAdapter context, Object o)
	{
		if (type == ParserTreeConstants.JJTREFERENCE)
		{
			if (numTreeChildren > 0)
			{
				try
				{
					((ASTReference) nodeTree).setValue(context, o);
				}
				catch (MethodInvocationException mie)
				{
					logger.error("VMProxyArg.getObject() : method invocation error setting value : " + mie);
				}
			}
			else context.put(singleLevelRef, o);
		}
		else
		{
			type = GENERALSTATIC;
			staticObject = o;

			logger.error("VMProxyArg.setObject() : Programmer error : I am a constant!  No setting! : " + contextReference + " / " + callerReference);
		}

		return null;
	}


	/**
	 * returns the value of the reference.  Generally, this is only
	 * called for dynamic proxies, as the static ones should have
	 * been stored in the VMContext's localcontext store
	 *
	 * @param context Context to use for getting current value
	 * @return Object value
	 */
	public Object getObject(InternalContextAdapter context)
	{
		try
		{
			Object retObject = null;

			if (type == ParserTreeConstants.JJTREFERENCE)
			{
				if (numTreeChildren == 0) retObject = context.get(singleLevelRef);
					else retObject = nodeTree.execute(null, context);
			}
			else if (type == ParserTreeConstants.JJTOBJECTARRAY) retObject = nodeTree.value(context);
			else if (type == ParserTreeConstants.JJTINTEGERRANGE) retObject = nodeTree.value(context);
			else if (type == ParserTreeConstants.JJTTRUE) retObject = staticObject;
			else if (type == ParserTreeConstants.JJTFALSE) retObject = staticObject;
			else if (type == ParserTreeConstants.JJTSTRINGLITERAL) retObject = nodeTree.value(context);
			else if (type == ParserTreeConstants.JJTNUMBERLITERAL) retObject = staticObject;
			else if (type == ParserTreeConstants.JJTTEXT)
			{
				try
				{
					StringWriter writer = new StringWriter();
					nodeTree.render(context, writer);

					retObject = writer;
				}
				catch (Exception e)
				{
					logger.error("VMProxyArg.getObject(): error rendering reference : " + e);
				}
			}
			else if (type == GENERALSTATIC)
			{
				retObject = staticObject;
			}
			else
			{
				logger.error("Unsupported VM arg type: VM arg = " + callerReference + " type = " + type + "( VMProxyArg.getObject() )");
			}

			return retObject;
		}
		catch (MethodInvocationException mie)
		{
			logger.error("VMProxyArg.getObject(): method invocation error getting value : " + mie);

			return null;
		}
	}

	/**
	 * does the housekeeping upon creationg.  If a dynamic type
	 * it needs to make an AST for further get()/set() operations
	 * Anything else is constant.
	 */
	private void setup()
	{
		switch (type)
		{

			case ParserTreeConstants.JJTINTEGERRANGE:
			case ParserTreeConstants.JJTREFERENCE:
			case ParserTreeConstants.JJTOBJECTARRAY:
			case ParserTreeConstants.JJTSTRINGLITERAL:
			case ParserTreeConstants.JJTTEXT:
			{
				constant = false;

				try
				{
					String buff = "#include(" + callerReference + " ) ";

					BufferedReader br = new BufferedReader(new StringReader(buff));

					nodeTree = rsvc.parse(br, "VMProxyArg:" + callerReference, true);

					nodeTree = (SimpleNode) nodeTree.jjtGetChild(0).jjtGetChild(0);

					if (nodeTree != null && nodeTree.getType() != type) logger.error("VMProxyArg.setup() : programmer error : type doesn't match node type.");

					InternalContextAdapter ica = new DefaultInternalContextAdapter(new VelocityContext());

					ica.pushCurrentTemplateName("VMProxyArg : " + ParserTreeConstants.jjtNodeName[type]);

					nodeTree.init(ica, rsvc);
				}
				catch (Exception e)
				{
					logger.error("VMProxyArg.setup() : exception " + callerReference + " : " + e.getMessage());
					logger.debug("Exception", e);
				}

				break;
			}

			case ParserTreeConstants.JJTTRUE:
			{
				constant = true;
				staticObject = new Boolean(true);
				break;
			}

			case ParserTreeConstants.JJTFALSE:
			{
				constant = true;
				staticObject = new Boolean(false);
				break;
			}

			case ParserTreeConstants.JJTNUMBERLITERAL:
			{
				constant = true;
				staticObject = new Integer(callerReference);
				break;
			}

			case ParserTreeConstants.JJTWORD:
			{
				logger.error("Unsupported arg type : " + callerReference + "  You most likely intended to call a VM with a string literal, so enclose with ' or \" characters. (VMProxyArg.setup())");
				constant = true;
				staticObject = new String(callerReference);

				break;
			}

			default :
			{
				logger.error(" VMProxyArg.setup() : unsupported type : " + callerReference);
			}
		}
	}

	public String getCallerReference()
	{
		return callerReference;
	}

	public String getContextReference()
	{
		return contextReference;
	}

	public SimpleNode getNodeTree()
	{
		return nodeTree;
	}

	public Object getStaticObject()
	{
		return staticObject;
	}

	public int getType()
	{
		return type;
	}
}
