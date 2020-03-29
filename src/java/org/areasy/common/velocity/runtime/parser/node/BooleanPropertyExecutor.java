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

import org.areasy.common.velocity.runtime.introspection.Introspector;

/**
 * Handles discovery and valuation of a
 * boolean object property, of the
 * form public boolean is<property> when executed.
 * <p/>
 * We do this separately as to preserve the current
 * quasi-broken semantics of get<as is property>
 * get< flip 1st char> get("property") and now followed
 * by is<Property>
 *
 * @version $Id: BooleanPropertyExecutor.java,v 1.1 2008/05/25 22:33:09 swd\stefan.damian Exp $
 */
public class BooleanPropertyExecutor extends PropertyExecutor
{

	public BooleanPropertyExecutor(Introspector is, Class clazz, String property)
	{
		super(is, clazz, property);
	}

	protected void discover(Class clazz, String property)
	{
		try
		{
			char c;
			StringBuffer sb;

			Object[] params = {};

			sb = new StringBuffer("is");
			sb.append(property);

			c = sb.charAt(2);

			if (Character.isLowerCase(c)) sb.setCharAt(2, Character.toUpperCase(c));

			methodUsed = sb.toString();
			method = introspector.getMethod(clazz, methodUsed, params);

			if (method != null)
			{
				if (method.getReturnType() == Boolean.TYPE) return;

				method = null;
			}
		}
		catch (Exception e)
		{
			logger.error("BooleanPropertyExector() : " + e.getMessage());
			logger.debug("Exception", e);
		}
	}
}
