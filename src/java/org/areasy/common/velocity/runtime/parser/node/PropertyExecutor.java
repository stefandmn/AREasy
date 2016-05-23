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

import org.areasy.common.velocity.runtime.introspection.Introspector;

import java.lang.reflect.InvocationTargetException;

/**
 * Returned the value of object property when executed.
 */
public class PropertyExecutor extends AbstractExecutor
{
	protected Introspector introspector = null;

	protected String methodUsed = null;

	public PropertyExecutor(Introspector ispctr, Class clazz, String property)
	{
		introspector = ispctr;

		discover(clazz, property);
	}

	protected void discover(Class clazz, String property)
	{
		try
		{
			char c;
			StringBuffer sb;

			Object[] params = {};

			sb = new StringBuffer("get");
			sb.append(property);

			methodUsed = sb.toString();

			method = introspector.getMethod(clazz, methodUsed, params);

			if (method != null) return;

			sb = new StringBuffer("get");
			sb.append(property);

			c = sb.charAt(3);

			if (Character.isLowerCase(c)) sb.setCharAt(3, Character.toUpperCase(c));
				else sb.setCharAt(3, Character.toLowerCase(c));

			methodUsed = sb.toString();
			method = introspector.getMethod(clazz, methodUsed, params);

			if (method != null) return;

		}
		catch (Exception e)
		{
			logger.error("Property executor error: " + e.getMessage());
			logger.debug("Exception", e);
		}
	}


	/**
	 * Execute method against context.
	 */
	public Object execute(Object o) throws IllegalAccessException, InvocationTargetException
	{
		if (method == null) return null;

		return method.invoke(o, null);
	}
}


