package org.areasy.common.data.bean;

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

import java.util.Map;
import java.util.WeakHashMap;

/**
 * A value that is provided per (thread) context classloader.
 * Patterned after ThreadLocal.
 * There is a separate value used when Thread.getContextClassLoader() is null.
 * This mechanism provides isolation for web apps deployed in the same container.
 * <strong>Note:</strong> A WeakHashMap bug in several 1.3 JVMs results in a memory leak
 * for those JVMs.
 *
 * @version $Id: ContextClassLoaderLocal.java,v 1.2 2008/05/14 09:32:37 swd\stefan.damian Exp $
 */
public class ContextClassLoaderLocal
{
	private Map valueByClassLoader = new WeakHashMap();
	private boolean globalValueInitialized = false;
	private Object globalValue;

	public ContextClassLoaderLocal()
	{
		super();
	}

	/**
	 * Returns the initial value for this ContextClassLoaderLocal
	 * variable. This method will be called once per Context ClassLoader for
	 * each ContextClassLoaderLocal, the first time it is accessed
	 * with get or set.  If the programmer desires ContextClassLoaderLocal variables
	 * to be initialized to some value other than null, ContextClassLoaderLocal must
	 * be subclassed, and this method overridden.  Typically, an anonymous
	 * inner class will be used.  Typical implementations of initialValue
	 * will call an appropriate constructor and return the newly constructed
	 * object.
	 *
	 * @return a new Object to be used as an initial value for this ContextClassLoaderLocal
	 */
	protected Object initialValue()
	{
		return null;
	}

	/**
	 * Gets the instance which provides the functionality for {@link BeanUtility}.
	 * This is a pseudo-singleton - an single instance is provided per (thread) context classloader.
	 * This mechanism provides isolation for web apps deployed in the same container.
	 *
	 * @return the object currently associated with the
	 */
	public synchronized Object get()
	{
		// make sure that the map is given a change to purge itself
		valueByClassLoader.isEmpty();
		
		try
		{
			ClassLoader contextClassLoader = Thread.currentThread().getContextClassLoader();
			if (contextClassLoader != null)
			{
				Object value = valueByClassLoader.get(contextClassLoader);
				if ((value == null) && !valueByClassLoader.containsKey(contextClassLoader))
				{
					value = initialValue();
					valueByClassLoader.put(contextClassLoader, value);
				}

				return value;
			}
		}
		catch (SecurityException e)
		{
			//nothing?!
		}

		// if none or exception, return the globalValue
		if (!globalValueInitialized)
		{
			globalValue = initialValue();
			globalValueInitialized = true;
		}

		return globalValue;
	}

	/**
	 * Sets the value - a value is provided per (thread) context classloader.
	 * This mechanism provides isolation for web apps deployed in the same container.
	 *
	 * @param value the object to be associated with the entrant thread's context classloader
	 */
	public synchronized void set(Object value)
	{
		// make sure that the map is given a change to purge itself
		valueByClassLoader.isEmpty();
		try
		{
			ClassLoader contextClassLoader = Thread.currentThread().getContextClassLoader();
			if (contextClassLoader != null)
			{
				valueByClassLoader.put(contextClassLoader, value);
				return;
			}
		}
		catch (SecurityException e)
		{
			//nothing?
		}

		// if in doubt, set the global value
		globalValue = value;
		globalValueInitialized = true;
	}

	/**
	 * Unsets the value associated with the current thread's context classloader
	 */
	public synchronized void unset()
	{
		try
		{
			ClassLoader contextClassLoader = Thread.currentThread().getContextClassLoader();
			unset(contextClassLoader);
		}
		catch (SecurityException e)
		{
			//nothing?
		}
	}

	/**
	 * Unsets the value associated with the given classloader
	 */
	public synchronized void unset(ClassLoader classLoader)
	{
		valueByClassLoader.remove(classLoader);
	}
}