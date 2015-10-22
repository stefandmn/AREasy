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
import org.areasy.common.velocity.context.InternalContextAdapter;
import org.areasy.common.velocity.runtime.introspection.Introspector;

import java.lang.reflect.InvocationTargetException;


/**
 * Executor that simply tries to execute a get(key)
 * operation. This will try to find a get(key) method
 * for any type of object, not just objects that
 * implement the Map interface as was previously
 * the case.
 *
 * @version $Id: GetExecutor.java,v 1.1 2008/05/25 22:33:09 swd\stefan.damian Exp $
 */
public class GetExecutor extends AbstractExecutor
{
	/**
	 * Container to hold the 'key' part of
	 * get(key).
	 */
	private Object[] args = new Object[1];

	/**
	 * Default constructor.
	 */
	public GetExecutor(Introspector ispect, Class c, String key) throws Exception
	{
		args[0] = key;
		method = ispect.getMethod(c, "get", args);
	}

	/**
	 * Execute method against context.
	 */
	public Object execute(Object o)
			throws IllegalAccessException, InvocationTargetException
	{
		if (method == null)
		{
			return null;
		}

		return method.invoke(o, args);
	}

	/**
	 * Execute method against context.
	 */
	public Object OLDexecute(Object o, InternalContextAdapter context)
			throws IllegalAccessException, MethodInvocationException
	{
		if (method == null)
		{
			return null;
		}

		try
		{
			return method.invoke(o, args);
		}
		catch (InvocationTargetException ite)
		{
			/*
			 *  the method we invoked threw an exception.
			 *  package and pass it up
			 */

			throw  new MethodInvocationException("Invocation of method 'get(\"" + args[0] + "\")'"
					+ " in  " + o.getClass()
					+ " threw exception "
					+ ite.getTargetException().getClass(),
					ite.getTargetException(), "get");
		}
		catch (IllegalArgumentException iae)
		{
			return null;
		}
	}
}









