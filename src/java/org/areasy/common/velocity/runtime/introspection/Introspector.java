package org.areasy.common.velocity.runtime.introspection;

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

import java.lang.reflect.Method;

/**
 * This basic function of this class is to return a Method
 * object for a particular class given the name of a method
 * and the parameters to the method in the form of an Object[]
 * <p/>
 * The first time the Introspector sees a
 * class it creates a class method map for the
 * class in question. Basically the class method map
 * is a Hastable where Method objects are keyed by a
 * concatenation of the method name and the names of
 * classes that make up the parameters.
 * <p/>
 * For example, a method with the following signature:
 * <p/>
 * public void method(String a, StringBuffer b)
 * <p/>
 * would be mapped by the key:
 * <p/>
 * "method" + "java.lang.String" + "java.lang.StringBuffer"
 * <p/>
 * This mapping is performed for all the methods in a class
 * and stored for
 *
 * @version $Id: Introspector.java,v 1.1 2008/05/25 22:33:15 swd\stefan.damian Exp $
 */
public class Introspector extends IntrospectorBase
{

	/** the logger */
	private static Logger logger = LoggerFactory.getLog(Introspector.class.getName());
	/**
	 * define a public string so that it can be looked for
	 * if interested
	 */

	/**
	 * Recieves our RuntimeServices object
	 */
	public Introspector()
	{
		//nothing to do here
	}

	/**
	 * Gets the method defined by <code>name</code> and
	 * <code>params</code> for the Class <code>c</code>.
	 *
	 * @param c      Class in which the method search is taking place
	 * @param name   Name of the method being searched for
	 * @param params An array of Objects (not Classes) that describe the
	 *               the parameters
	 * @return The desired Method object.
	 */
	public Method getMethod(Class c, String name, Object[] params)
			throws Exception
	{
		/*
		 *  just delegate to the base class
		 */

		try
		{
			return super.getMethod(c, name, params);
		}
		catch (MethodMap.AmbiguousException ae)
		{
			String msg = "Introspection error : ambiguous method invocation " + name + "( ";

			for (int i = 0; i < params.length; i++)
			{
				if (i > 0) msg = msg + ", ";

				msg = msg + params[i].getClass().getName();
			}

			msg = msg + ") for class " + c;

			logger.error(msg);
		}

		return null;
	}

	/**
	 * Clears the classmap and classname
	 * caches, and logs that we did so
	 */
	protected void clearCache()
	{
		super.clearCache();

		logger.debug("Detected classloader change. Dumping cache");
	}
}
