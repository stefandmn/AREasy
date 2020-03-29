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

import org.areasy.common.velocity.base.MethodInvocationException;
import org.areasy.common.velocity.base.event.EventCartridge;
import org.areasy.common.velocity.context.InternalContextAdapter;
import org.areasy.common.velocity.runtime.introspection.Information;
import org.areasy.common.velocity.runtime.introspection.IntrospectionCacheData;
import org.areasy.common.velocity.runtime.introspection.VelocityPropertyGet;
import org.areasy.common.velocity.runtime.parser.Parser;

import java.lang.reflect.InvocationTargetException;

/**
 * ASTIdentifier.java
 * <p/>
 * Method support for identifiers :  $foo
 * <p/>
 * mainly used by ASTRefrence
 * <p/>
 * Introspection is now moved to 'just in time' or at render / execution
 * time. There are many reasons why this has to be done, but the
 * primary two are   thread safety, to remove any context-derived
 * information from class member  variables.
 *
 * @version $Id: ASTIdentifier.java,v 1.1 2008/05/25 22:33:08 swd\stefan.damian Exp $
 */
public class ASTIdentifier extends SimpleNode
{
	private String identifier = "";

	/**
	 * This is really immutable after the init, so keep one for this node
	 */
	protected Information uberInformation;

	public ASTIdentifier(int id)
	{
		super(id);
	}

	public ASTIdentifier(Parser p, int id)
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
	 * simple init - don't do anything that is context specific.
	 * just get what we need from the AST, which is static.
	 */
	public Object init(InternalContextAdapter context, Object data)
			throws Exception
	{
		super.init(context, data);

		identifier = getFirstToken().image;

		uberInformation = new Information(context.getCurrentTemplateName(),
				getLine(), getColumn());

		return data;
	}

	/**
	 * invokes the method on the object passed in
	 */
	public Object execute(Object o, InternalContextAdapter context)
			throws MethodInvocationException
	{

		VelocityPropertyGet vg = null;

		try
		{
			Class c = o.getClass();

			/*
			 *  first, see if we have this information cached.
			 */

			IntrospectionCacheData icd = context.icacheGet(this);

			/*
			 * if we have the cache data and the class of the object we are
			 * invoked with is the same as that in the cache, then we must
			 * be allright.  The last 'variable' is the method name, and
			 * that is fixed in the template :)
			 */

			if (icd != null && icd.contextData == c)
			{
				vg = (VelocityPropertyGet) icd.thingy;
			}
			else
			{
				vg = rsvc.getUberspect().getPropertyGet(o, identifier, uberInformation);

				if (vg != null && vg.isCacheable())
				{
					icd = new IntrospectionCacheData();
					icd.contextData = c;
					icd.thingy = vg;
					context.icachePut(this, icd);
				}
			}
		}
		catch (Exception e)
		{
			logger.error("ASTIdentifier.execute() : identifier = " + identifier + " : " + e.getMessage());
			logger.debug("Exception", e);
		}

		/*
		 *  we have no getter... punt...
		 */

		if (vg == null)
		{
			return null;
		}

		/*
		 *  now try and execute.  If we get a MIE, throw that
		 *  as the app wants to get these.  If not, log and punt.
		 */
		try
		{
			return vg.invoke(o);
		}
		catch (InvocationTargetException ite)
		{
			EventCartridge ec = context.getEventCartridge();

			/*
			 *  if we have an event cartridge, see if it wants to veto
			 *  also, let non-Exception Throwables go...
			 */

			if (ec != null
					&& ite.getTargetException() instanceof java.lang.Exception)
			{
				try
				{
					return ec.methodException(o.getClass(), vg.getMethodName(),
							(Exception) ite.getTargetException());
				}
				catch (Exception e)
				{
					throw new MethodInvocationException("Invocation of method '" + vg.getMethodName() + "'"
							+ " in  " + o.getClass()
							+ " threw exception "
							+ ite.getTargetException().getClass() + " : "
							+ ite.getTargetException().getMessage(),
							ite.getTargetException(), vg.getMethodName());
				}
			}
			else
			{
				/*
				 * no event cartridge to override. Just throw
				 */

				throw  new MethodInvocationException("Invocation of method '" + vg.getMethodName() + "'"
						+ " in  " + o.getClass()
						+ " threw exception "
						+ ite.getTargetException().getClass() + " : "
						+ ite.getTargetException().getMessage(),
						ite.getTargetException(), vg.getMethodName());


			}
		}
		catch (IllegalArgumentException iae)
		{
			return null;
		}
		catch (Exception e)
		{
			logger.error("ASTIdentifier() : exception invoking method "
					+ "for identifier '" + identifier + "' in "
					+ o.getClass() + " : " + e);
		}

		return null;
	}
}
