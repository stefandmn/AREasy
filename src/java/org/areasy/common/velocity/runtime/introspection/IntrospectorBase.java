package org.areasy.common.velocity.runtime.introspection;

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

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

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
 * @version $Id: IntrospectorBase.java,v 1.1 2008/05/25 22:33:15 swd\stefan.damian Exp $
 */
public class IntrospectorBase
{
	/**
	 * Holds the method maps for the classes we know about, keyed by
	 * Class object.
	 */
	protected Map classMethodMaps = new HashMap();

	/**
	 * Holds the qualified class names for the classes
	 * we hold in the classMethodMaps hash
	 */
	protected Set cachedClassNames = new HashSet();

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
	public Method getMethod(Class c, String name, Object[] params) throws Exception
	{
		if (c == null) throw new Exception("Introspector.getMethod(): Class method key was null: " + name);

		ClassMap classMap = null;

		synchronized (classMethodMaps)
		{
			classMap = (ClassMap) classMethodMaps.get(c);

			if (classMap == null)
			{
				if (cachedClassNames.contains(c.getName())) clearCache();

				classMap = createClassMap(c);
			}
		}

		return classMap.findMethod(name, params);
	}

	/**
	 * Creates a class map for specific class and registers it in the
	 * cache.  Also adds the qualified name to the name->class map
	 * for later Classloader change detection.
	 */
	protected ClassMap createClassMap(Class c)
	{
		ClassMap classMap = new ClassMap(c);
		classMethodMaps.put(c, classMap);
		cachedClassNames.add(c.getName());

		return classMap;
	}

	/**
	 * Clears the classmap and classname
	 * caches
	 */
	protected void clearCache()
	{
		/*
		 *  since we are synchronizing on this
		 *  object, we have to clear it rather than
		 *  just dump it.
		 */
		classMethodMaps.clear();

		/*
		 * for speed, we can just make a new one
		 * and let the old one be GC'd
		 */
		cachedClassNames = new HashSet();
	}
}
