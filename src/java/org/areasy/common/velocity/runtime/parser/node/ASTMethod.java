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
import org.areasy.common.velocity.base.event.EventCartridge;
import org.areasy.common.velocity.context.InternalContextAdapter;
import org.areasy.common.velocity.runtime.introspection.Information;
import org.areasy.common.velocity.runtime.introspection.IntrospectionCacheData;
import org.areasy.common.velocity.runtime.introspection.VelocityMethod;
import org.areasy.common.velocity.runtime.parser.Parser;

import java.lang.reflect.InvocationTargetException;

/**
 * ASTMethod.java
 * <p/>
 * Method support for references :  $foo.method()
 * <p/>
 * NOTE :
 * <p/>
 * introspection is now done at render time.
 * <p/>
 * Please look at the Parser.jjt file which is
 * what controls the generation of this class.
 *
 * @version $Id: ASTMethod.java,v 1.1 2008/05/25 22:33:08 swd\stefan.damian Exp $
 */
public class ASTMethod extends SimpleNode
{
	private String methodName = "";
	private int paramCount = 0;

	public ASTMethod(int id)
	{
		super(id);
	}

	public ASTMethod(Parser p, int id)
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
	 * simple init - init our subtree and get what we can from
	 * the AST
	 */
	public Object init(InternalContextAdapter context, Object data)
			throws Exception
	{
		super.init(context, data);

		/*
		 *  this is about all we can do
		 */

		methodName = getFirstToken().image;
		paramCount = jjtGetNumChildren() - 1;

		return data;
	}

	/**
	 * invokes the method.  Returns null if a problem, the
	 * actual return if the method returns something, or
	 * an empty string "" if the method returns void
	 */
	public Object execute(Object o, InternalContextAdapter context)
			throws MethodInvocationException
	{
		/*
		 *  new strategy (strategery!) for introspection. Since we want
		 *  to be thread- as well as context-safe, we *must* do it now,
		 *  at execution time.  There can be no in-node caching,
		 *  but if we are careful, we can do it in the context.
		 */

		VelocityMethod method = null;

		Object[] params = new Object[paramCount];

		try
		{
			/*
			 *   check the cache
			 */

			IntrospectionCacheData icd = context.icacheGet(this);
			Class c = o.getClass();

			/*
			 *  like ASTIdentifier, if we have cache information, and the
			 *  Class of Object o is the same as that in the cache, we are
			 *  safe.
			 */

			if (icd != null && icd.contextData == c)
			{
				/*
				 * sadly, we do need recalc the values of the args, as this can
				 * change from visit to visit
				 */

				for (int j = 0; j < paramCount; j++)
				{
					params[j] = jjtGetChild(j + 1).value(context);
				}

				/*
				 * and get the method from the cache
				 */

				method = (VelocityMethod) icd.thingy;
			}
			else
			{
				/*
				 *  otherwise, do the introspection, and then
				 *  cache it
				 */

				for (int j = 0; j < paramCount; j++)
				{
					params[j] = jjtGetChild(j + 1).value(context);
				}

				method = rsvc.getUberspect().getMethod(o, methodName, params, new Information("", 1, 1));

				if (method != null)
				{
					icd = new IntrospectionCacheData();
					icd.contextData = c;
					icd.thingy = method;
					context.icachePut(this, icd);
				}
			}

			/*
			 *  if we still haven't gotten the method, either we are calling
			 *  a method that doesn't exist (which is fine...)  or I screwed
			 *  it up.
			 */

			if (method == null)
			{
				return null;
			}
		}
		catch (MethodInvocationException mie)
		{
			/*
			 *  this can come from the doIntrospection(), as the arg values
			 *  are evaluated to find the right method signature.  We just
			 *  want to propogate it here, not do anything fancy
			 */

			throw mie;
		}
		catch (Exception e)
		{
			/*
			 *  can come from the doIntropection() also, from Introspector
			 */

			logger.error("ASTMethod.execute() : exception from introspection : " + e);
			return null;
		}

		try
		{
			/*
			 *  get the returned object.  It may be null, and that is
			 *  valid for something declared with a void return type.
			 *  Since the caller is expecting something to be returned,
			 *  as long as things are peachy, we can return an empty
			 *  String so ASTReference() correctly figures out that
			 *  all is well.
			 */

			Object obj = method.invoke(o, params);

			if (obj == null)
			{
				if (method.getReturnType() == Void.TYPE)
				{
					return new String("");
				}
			}

			return obj;
		}
		catch (InvocationTargetException ite)
		{
			/*
			 *  In the event that the invocation of the method
			 *  itself throws an exception, we want to catch that
			 *  wrap it, and throw.  We don't log here as we want to figure
			 *  out which reference threw the exception, so do that
			 *  above
			 */

			EventCartridge ec = context.getEventCartridge();

			/*
			 *  if we have an event cartridge, see if it wants to veto
			 *  also, let non-Exception Throwables go...
			 */

			if (ec != null && ite.getTargetException() instanceof java.lang.Exception)
			{
				try
				{
					return ec.methodException(o.getClass(), methodName, (Exception) ite.getTargetException());
				}
				catch (Exception e)
				{
					throw new MethodInvocationException("Invocation of method '"
							+ methodName + "' in  " + o.getClass()
							+ " threw exception "
							+ e.getClass() + " : " + e.getMessage(),
							e, methodName);
				}
			}
			else
			{
				/*
				 * no event cartridge to override. Just throw
				 */

				throw new MethodInvocationException("Invocation of method '"
						+ methodName + "' in  " + o.getClass()
						+ " threw exception "
						+ ite.getTargetException().getClass() + " : "
						+ ite.getTargetException().getMessage(),
						ite.getTargetException(), methodName);
			}
		}
		catch (Exception e)
		{
			logger.error("ASTMethod.execute() : exception invoking method '"
					+ methodName + "' in " + o.getClass() + " : " + e);

			return null;
		}
	}
}
