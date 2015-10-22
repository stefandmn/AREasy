package org.areasy.common.logger.base;


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

import org.areasy.common.logger.Logger;
import org.areasy.common.logger.LoggerException;
import org.areasy.common.logger.LoggerFactory;


import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Vector;


/**
 * <p>Concrete subclass of {@link LoggerFactory} that implements the
 * following algorithm to dynamically select a logging implementation
 * class to instantiate a wrapper for.</p>
 * <ul>
 * <li>Use a factory configuration attribute named
 * <code>com.snt.common.logger.Logger</code> to identify the
 * requested implementation class.</li>
 * <li>Use the <code>com.snt.common.logger.Logger</code> system property
 * to identify the requested implementation class.</li>
 * <li>If <em>Log4J</em> is available, return an instance of
 * <code>com.snt.common.logger.base.log4j.Log4JLogger</code>.</li>
 * <li>If <em>JDK 1.4 or later</em> is available, return an instance of
 * <code>com.snt.common.logger.base.jdk.JdkLogger</code>.</li>
 * </ul>
 * <p/>
 * <p>If the selected {@link Logger} implementation class has a
 * <code>setLogFactory()</code> method that accepts a {@link LoggerFactory}
 * parameter, this method will be called on each newly created instance
 * to identify the associated factory.  This makes factory configuration
 * attributes available to the Logger instance, if it so desires.</p>
 * <p/>
 * <p>This factory will remember previously created <code>Logger</code> instances
 * for the same name, and will return them on repeated requests to the
 * <code>getInstance()</code> method.  This implementation ignores any
 * configured attributes.</p>
 */

public class DefaultLoggerFactory extends LoggerFactory
{
	/**
	 * Configuration attributes.
	 */
	protected Hashtable attributes = new Hashtable();

	/**
	 * The {@link org.areasy.common.logger.Logger} instances that have
	 * already been created, keyed by logger name.
	 */
	protected Hashtable instances = new Hashtable();

	/** Name of the class implementing the Logger interface. */
	private String logClassName;

	/** Name of the class implementing the LogManager abstract object. */
	private String managerClassName;

	/**
	 * The one-argument constructor of the
	 * {@link org.areasy.common.logger.Logger}
	 * implementation class that will be used to create new instances.
	 * This value is initialized by <code>getLogConstructor()</code>,
	 * and then returned repeatedly.
	 */
	protected Constructor logConstructor = null;

	/**
	 * The signature of the Constructor to be used.
	 */
	protected Class logConstructorSignature[] = {java.lang.String.class};

	/**
	 * The one-argument <code>setLogFactory</code> method of the selected
	 * {@link org.areasy.common.logger.Logger} method, if it exists.
	 */
	protected Method logMethod = null;

	/**
	 * The signature of the <code>setLogFactory</code> method to be used.
	 */
	protected Class logMethodSignature[] = {LoggerFactory.class};

	/**
	 * Public no-arguments constructor required by the lookup mechanism.
	 */
	public DefaultLoggerFactory()
	{
		super();
	}

	/**
	 * Return the configuration attribute with the specified name (if any),
	 * or <code>null</code> if there is no such attribute.
	 *
	 * @param name Name of the attribute to return
	 */
	public Object getAttribute(String name)
	{
		return (attributes.get(name));
	}

	/**
	 * Return an array containing the names of all currently defined
	 * configuration attributes.  If there are no such attributes, a zero
	 * length array is returned.
	 */
	public String[] getAttributeNames()
	{

		Vector names = new Vector();

		Enumeration keys = attributes.keys();
		while (keys.hasMoreElements())
		{
			names.addElement(keys.nextElement());
		}

		String results[] = new String[names.size()];
		for (int i = 0; i < results.length; i++)
		{
			results[i] = (String) names.elementAt(i);
		}

		return (results);
	}


	/**
	 * Convenience method to derive a name from the specified class and
	 * call <code>getInstance(String)</code> with it.
	 *
	 * @param clazz Class for which a suitable Logger name will be derived
	 * @throws LoggerException if a suitable <code>Logger</code>
	 *                         instance cannot be returned
	 */
	public Logger getInstance(Class clazz) throws LoggerException
	{
		return (getInstance(clazz.getName()));
	}


	/**
	 * <p>Construct (if necessary) and return a <code>Logger</code> instance,
	 * using the factory's current set of configuration attributes.</p>
	 * <p/>
	 * <p><strong>NOTE</strong> - Depending upon the implementation of
	 * the <code>LoggerFactory</code> you are using, the <code>Logger</code>
	 * instance you are returned may or may not be local to the current
	 * application, and may or may not be returned again on a subsequent
	 * call with the same name argument.</p>
	 *
	 * @param name Logical name of the <code>Logger</code> instance to be
	 *             returned (the meaning of this name is only known to the underlying
	 *             logging implementation that is being wrapped)
	 * @throws LoggerException if a suitable <code>Logger</code>
	 *                         instance cannot be returned
	 */
	public Logger getInstance(String name) throws LoggerException
	{
		Logger instance = (Logger) instances.get(name);

		if (instance == null)
		{
			instance = newInstance(name);
			instances.put(name, instance);
		}

		return (instance);
	}


	/**
	 * <p>Construct (if necessary) and return a <code>LoggerManager</code> instance,
	 * using the factory's current set of configuration attributes.</p>
	 * <p/>
	 * @throws LoggerException if a suitable <code>LoggerManager</code>  instance cannot be returned
	 */
	public LoggerManager getLoggerManagerInstance() throws LoggerException
	{
		LoggerManager instance = null;

		// Return the previously identified class name (if any)
		if (managerClassName == null)
		{
			managerClassName = (String) getAttribute(Logger.MANAGER_PROPERTY);

			if (managerClassName == null)
			{
				try
				{
					managerClassName = System.getProperty(Logger.MANAGER_PROPERTY);
				}
				catch (SecurityException e) {}
			}

			if ((managerClassName == null) && isLog4JAvailable()) managerClassName = "com.snt.common.logger.base.log4j.Log4JManager";
				else if ((managerClassName == null) && isJdkAvailable()) managerClassName = "com.snt.common.logger.base.jdk.JdkManager";
		}

		if(managerClassName != null)
		{
			try
			{
				instance = (LoggerManager) loadClass(managerClassName).newInstance();
			}
			catch (Throwable t)
			{
				throw new LoggerException(t);
			}
		}
		else throw new LoggerException("No suitable LoggerManager implementation.");

		return instance;
	}

	/**
	 * Release any internal references to previously created
	 * {@link org.areasy.common.logger.Logger}
	 * instances returned by this factory.  This is useful in environments
	 * like servlet containers, which implement application reloading by
	 * throwing away a ClassLoader.  Dangling references to objects in that
	 * class loader would prevent garbage collection.
	 */
	public void release()
	{
		instances.clear();
	}


	/**
	 * Remove any configuration attribute associated with the specified name.
	 * If there is no such attribute, no action is taken.
	 *
	 * @param name Name of the attribute to remove
	 */
	public void removeAttribute(String name)
	{
		attributes.remove(name);
	}


	/**
	 * Set the configuration attribute with the specified name.  Calling
	 * this with a <code>null</code> value is equivalent to calling
	 * <code>removeAttribute(name)</code>.
	 *
	 * @param name  Name of the attribute to set
	 * @param value Value of the attribute to set, or <code>null</code>
	 *              to remove any setting for this attribute
	 */
	public void setAttribute(String name, Object value)
	{
		if (value == null) attributes.remove(name);
			else attributes.put(name, value);
	}

	/**
	 * Return the fully qualified Java classname of the {@link Logger}
	 * implementation we will be using.
	 */
	protected String getLogClassName()
	{
		// Return the previously identified class name (if any)
		if (logClassName != null) return logClassName;

		logClassName = (String) getAttribute(Logger.LOG_PROPERTY);

		if (logClassName == null)
		{
			try
			{
				logClassName = System.getProperty(Logger.LOG_PROPERTY);
			}
			catch (SecurityException e) {}
		}

		if ((logClassName == null) && isLog4JAvailable()) logClassName = "com.snt.common.logger.base.log4j.Log4JLogger";
			else if ((logClassName == null) && isJdkAvailable()) logClassName = "com.snt.common.logger.base.jdk.JdkLogger";

		return (logClassName);
	}

	/**
	 * <p>Return the <code>Constructor</code> that can be called to instantiate
	 * new {@link org.areasy.common.logger.Logger} instances.</p>
	 * <p/>
	 * <p><strong>IMPLEMENTATION NOTE</strong> - Race conditions caused by
	 * calling this method from more than one thread are ignored, because
	 * the same <code>Constructor</code> instance will ultimately be derived
	 * in all circumstances.</p>
	 *
	 * @throws LoggerException if a suitable constructor
	 *                         cannot be returned
	 */
	protected Constructor getLogConstructor() throws LoggerException
	{

		// Return the previously identified Constructor (if any)
		if (logConstructor != null) return logConstructor;

		String logClassName = getLogClassName();

		// Attempt to load the Logger implementation class
		Class logClass = null;
		Class logInterface = null;

		try
		{
			logInterface = this.getClass().getClassLoader().loadClass(Logger.LOG_PROPERTY);
			logClass = loadClass(logClassName);

			if (logClass == null) throw new LoggerException("No suitable Logger implementation for " + logClassName);

			if (!logInterface.isAssignableFrom(logClass))
			{
				Class interfaces[] = logClass.getInterfaces();
				for (int i = 0; i < interfaces.length; i++)
				{
					if (Logger.LOG_PROPERTY.equals(interfaces[i].getName())) throw new LoggerException("Invalid class loader hierarchy. You have more than one version of '" + Logger.LOG_PROPERTY + "' visible, which is not allowed.");
				}

				throw new LoggerException("Class " + logClassName + " does not implement '" + Logger.LOG_PROPERTY + "'.");
			}
		}
		catch (Throwable t)
		{
			throw new LoggerException(t);
		}

		// Identify the <code>setLogFactory</code> method (if there is one)
		try
		{
			logMethod = logClass.getMethod("setLogFactory", logMethodSignature);
		}
		catch (Throwable t)
		{
			logMethod = null;
		}

		// Identify the corresponding constructor to be used
		try
		{
			logConstructor = logClass.getConstructor(logConstructorSignature);
			return (logConstructor);
		}
		catch (Throwable t)
		{
			throw new LoggerException("No suitable Logger constructor " + logConstructorSignature + " for " + logClassName, t);
		}
	}

	/**
	 * MUST KEEP THIS METHOD PRIVATE.
	 * <p/>
	 * <p>Exposing this method outside of
	 * <code>com.snt.common.logger.DefaultLoggerFactory</code>
	 * will create a security violation:
	 * This method uses <code>AccessController.doPrivileged()</code>.
	 * </p>
	 * <p/>
	 * Load a class, try first the thread class loader, and
	 * if it fails use the loader that loaded this class.
	 */
	private static Class loadClass(final String name) throws ClassNotFoundException
	{
		Object result = AccessController.doPrivileged(new PrivilegedAction()
		{
			public Object run()
			{
				ClassLoader threadCL = getContextClassLoader();
				if (threadCL != null)
				{
					try
					{
						return threadCL.loadClass(name);
					}
					catch (ClassNotFoundException ex)
					{
						// ignore
					}
				}
				try
				{
					return Class.forName(name);
				}
				catch (ClassNotFoundException e)
				{
					return e;
				}
			}
		});

		if (result instanceof Class) return (Class) result;


		throw (ClassNotFoundException) result;
	}


	/**
	 * <p>Return <code>true</code> if <em>JDK 1.4 or later</em> logging
	 * is available.  Also checks that the <code>Throwable</code> class
	 * supports <code>getStackTrace()</code>, which is required by
	 * DefaultJdk14Logger.</p>
	 */
	protected boolean isJdkAvailable()
	{
		try
		{
			loadClass("java.util.logging.Logger");
			loadClass("com.snt.common.logger.base.jdk.JdkLogger");

			Class throwable = loadClass("java.lang.Throwable");

			if (throwable.getDeclaredMethod("getStackTrace", null) == null) return false;

			return true;
		}
		catch (Throwable t)
		{
			return false;
		}
	}


	/**
	 * Is a <em>Log4J</em> implementation available?
	 */
	protected boolean isLog4JAvailable()
	{
		try
		{
			loadClass("org.apache.log4j.Logger");
			loadClass("com.snt.common.logger.base.log4j.Log4JLogger");

			return true;
		}
		catch (Throwable t)
		{
			return false;
		}
	}

	/**
	 * Create and return a new {@link org.areasy.common.logger.Logger}
	 * instance for the specified name.
	 *
	 * @param name Name of the new logger
	 * @throws LoggerException if a new instance cannot
	 *                         be created
	 */
	protected Logger newInstance(String name) throws LoggerException
	{
		Logger instance = null;

		try
		{
			Object params[] = new Object[1];
			params[0] = name;
			instance = (Logger) getLogConstructor().newInstance(params);

			if (logMethod != null)
			{
				params[0] = this;
				logMethod.invoke(instance, params);
			}

			return (instance);
		}
		catch (InvocationTargetException e)
		{
			Throwable c = e.getTargetException();
			if (c != null) throw new LoggerException(c);
				else throw new LoggerException(e);
		}
		catch (Throwable t)
		{
			throw new LoggerException(t);
		}
	}
}
