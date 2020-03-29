package org.areasy.common.logger;


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

import org.areasy.common.logger.base.LoggerManager;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.Enumeration;
import java.util.Hashtable;


/**
 * <p>Factory for creating {@link Logger} instances, with discovery and
 * configuration features similar to that employed by standard Java APIs
 * such as JAXP.</p>
 * <p/>
 * <p><strong>IMPLEMENTATION NOTE</strong> - This implementation is heavily
 * based on the SAXParserFactory and DocumentBuilderFactory implementations
 * (corresponding to the JAXP pluggability APIs)</p>
 */

public abstract class LoggerFactory
{
	/**
	 * The name of the property used to identify the LoggerFactory implementation class name.
	 */
	public static final String FACTORY_PROPERTY = "org.areasy.common.logger.LoggerFactory";

	/**
	 * The fully qualified class name of the fallback <code>LoggerFactory</code>
	 * implementation class to use, if no other can be found.
	 */
	public static final String FACTORY_DEFAULT = "org.areasy.common.logger.base.DefaultLoggerFactory";


	/**
	 * Protected constructor that is not available for public use.
	 */
	protected LoggerFactory()
	{
		//nothing to do here
	}

	/**
	 * Return the configuration attribute with the specified name (if any),
	 * or <code>null</code> if there is no such attribute.
	 *
	 * @param name Name of the attribute to return
	 */
	public abstract Object getAttribute(String name);


	/**
	 * Return an array containing the names of all currently defined
	 * configuration attributes.  If there are no such attributes, a zero
	 * length array is returned.
	 */
	public abstract String[] getAttributeNames();

	/**
	 * <p>Construct (if necessary) and return a <code>LoggerManager</code> instance,
	 * using the factory's current set of configuration attributes.</p>
	 * <p/>
	 * @throws LoggerException if a suitable <code>LoggerManager</code>  instance cannot be returned
	 */
	public abstract LoggerManager getLoggerManagerInstance() throws LoggerException;

	/**
	 * Convenience method to derive a name from the specified class and
	 * call <code>getInstance(String)</code> with it.
	 *
	 * @param clazz Class for which a suitable Logger name will be derived
	 * @throws LoggerException if a suitable <code>Logger</code>
	 *                         instance cannot be returned
	 */
	public abstract Logger getInstance(Class clazz) throws LoggerException;


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
	public abstract Logger getInstance(String name) throws LoggerException;


	/**
	 * Release any internal references to previously created {@link Logger}
	 * instances returned by this factory.  This is useful in environments
	 * like servlet containers, which implement application reloading by
	 * throwing away a ClassLoader.  Dangling references to objects in that
	 * class loader would prevent garbage collection.
	 */
	public abstract void release();


	/**
	 * Remove any configuration attribute associated with the specified name.
	 * If there is no such attribute, no action is taken.
	 *
	 * @param name Name of the attribute to remove
	 */
	public abstract void removeAttribute(String name);

	/**
	 * Set the configuration attribute with the specified name.  Calling
	 * this with a <code>null</code> value is equivalent to calling
	 * <code>removeAttribute(name)</code>.
	 *
	 * @param name  Name of the attribute to set
	 * @param value Value of the attribute to set, or <code>null</code>
	 *              to remove any setting for this attribute
	 */
	public abstract void setAttribute(String name, Object value);

	/**
	 * The previously constructed <code>LoggerFactory</code> instances, keyed by
	 * the <code>ClassLoader</code> with which it was created.
	 */
	protected static Hashtable factories = new Hashtable();

	protected static Hashtable managers = new Hashtable();

	/**
	 * Get discovered logger manager (for configuration and management) using environment
	 * variables and configuration.
	 */
	public static LoggerManager getLogManager()
	{
		return (getFactory().getLoggerManagerInstance());
	}

	/**
	 * <p>Construct (if necessary) and return a <code>LoggerFactory</code>
	 * instance, using the following ordered lookup procedure to determine
	 * the name of the implementation class to be loaded.</p>
	 * <ul>
	 * <li>The <code>org.areasy.common.logger.LoggerFactory</code> system
	 * property.</li>
	 * <li>The JDK 1.3 Service Discovery mechanism</li>
	 * <li>Use the properties file <code>commons-logging.properties</code>
	 * file, if found in the class path of this class.  The configuration
	 * file is in standard <code>java.util.Properties</code> format and
	 * contains the fully qualified name of the implementation class
	 * with the key being the system property defined above.</li>
	 * <li>Fall back to a default implementation class
	 * (<code>org.areasy.common.logger.base.DefaultLoggerFactory</code>).</li>
	 * </ul>
	 * <p/>
	 * <p><em>NOTE</em> - If the properties file method of identifying the
	 * <code>LoggerFactory</code> implementation class is utilized, all of the
	 * properties defined in this file will be set as configuration attributes
	 * on the corresponding <code>LoggerFactory</code> instance.</p>
	 *
	 * @throws LoggerException if the implementation class is not
	 *                         available or cannot be instantiated.
	 */
	public static LoggerFactory getFactory() throws LoggerException
	{
		// Identify the class loader we will be using
		ClassLoader contextClassLoader = (ClassLoader) AccessController.doPrivileged(new PrivilegedAction()
		{
			public Object run()
			{
				return getContextClassLoader();
			}
		});

		// Return any previously registered factory for this class loader
		LoggerFactory factory = getCachedFactory(contextClassLoader);
		if (factory != null) return factory;


		// First, try the system property
		try
		{
			String factoryClass = System.getProperty(FACTORY_PROPERTY);
			if (factoryClass != null) factory = newFactory(factoryClass, contextClassLoader);
		}
		catch (SecurityException e) { }

		// try the fallback implementation class
		if (factory == null) factory = newFactory(FACTORY_DEFAULT, LoggerFactory.class.getClassLoader());


		//Always cache using context class loader.
		if (factory != null) cacheFactory(contextClassLoader, factory);

		return factory;
	}


	/**
	 * Convenience method to return a named logger, without the application
	 * having to care about factories.
	 *
	 * @param clazz Class from which a log name will be derived
	 * @throws LoggerException if a suitable <code>Logger</code>
	 *                         instance cannot be returned
	 */
	public static Logger getLog(Class clazz) throws LoggerException
	{
		return (getFactory().getInstance(clazz));
	}


	/**
	 * Convenience method to return a named logger, without the application
	 * having to care about factories.
	 *
	 * @param name Logical name of the <code>Logger</code> instance to be
	 *             returned (the meaning of this name is only known to the underlying
	 *             logging implementation that is being wrapped)
	 * @throws LoggerException if a suitable <code>Logger</code>
	 *                         instance cannot be returned
	 */
	public static Logger getLog(String name) throws LoggerException
	{
		return (getFactory().getInstance(name));
	}


	/**
	 * Release any internal references to previously created {@link LoggerFactory}
	 * instances that have been associated with the specified class loader
	 * (if any), after calling the instance method <code>release()</code> on
	 * each of them.
	 *
	 * @param classLoader ClassLoader for which to release the LoggerFactory
	 */
	public static void release(ClassLoader classLoader)
	{
		synchronized (factories)
		{
			LoggerFactory factory = (LoggerFactory) factories.get(classLoader);
			if (factory != null)
			{
				factory.release();
				factories.remove(classLoader);
			}
		}

	}


	/**
	 * Release any internal references to previously created {@link LoggerFactory}
	 * instances, after calling the instance method <code>release()</code> on
	 * each of them.  This is useful in environments like servlet containers,
	 * which implement application reloading by throwing away a ClassLoader.
	 * Dangling references to objects in that class loader would prevent
	 * garbage collection.
	 */
	public static void releaseAll()
	{
		synchronized (factories)
		{
			Enumeration elements = factories.elements();
			while (elements.hasMoreElements())
			{
				LoggerFactory element = (LoggerFactory) elements.nextElement();
				element.release();
			}

			factories.clear();
		}
	}


	/**
	 * Return the thread context class loader if available.
	 * Otherwise return null.
	 * <p/>
	 * The thread context class loader is available for JDK 1.2
	 * or later, if certain security conditions are met.
	 *
	 * @throws LoggerException if a suitable class loader
	 *                         cannot be identified.
	 */
	protected static ClassLoader getContextClassLoader() throws LoggerException
	{
		ClassLoader classLoader = null;

		try
		{
			Method method = Thread.class.getMethod("getContextClassLoader", null);

			// Get the thread context class loader (if there is one)
			try
			{
				classLoader = (ClassLoader) method.invoke(Thread.currentThread(), null);
			}
			catch (IllegalAccessException e)
			{
				throw new LoggerException("Unexpected IllegalAccessException", e);
			}
			catch (InvocationTargetException e)
			{
				if (!(e.getTargetException() instanceof SecurityException))
					throw new LoggerException("Unexpected InvocationTargetException", e.getTargetException());
			}
		}
		catch (NoSuchMethodException e)
		{
			// Assume we are running on JDK 1.1
			classLoader = LoggerFactory.class.getClassLoader();
		}

		// Return the selected class loader
		return classLoader;
	}

	/**
	 * Check cached factories (keyed by contextClassLoader)
	 */
	private static LoggerFactory getCachedFactory(ClassLoader contextClassLoader)
	{
		LoggerFactory factory = null;

		if (contextClassLoader != null) factory = (LoggerFactory) factories.get(contextClassLoader);

		return factory;
	}

	private static void cacheFactory(ClassLoader classLoader, LoggerFactory factory)
	{
		if (classLoader != null && factory != null) factories.put(classLoader, factory);
	}

	/**
	 * Return a new instance of the specified <code>LoggerFactory</code>
	 * implementation class, loaded by the specified class loader.
	 * If that fails, try the class loader used to load this
	 * (abstract) LoggerFactory.
	 *
	 * @param factoryClass Fully qualified name of the <code>LoggerFactory</code>
	 *                     implementation class
	 * @param classLoader  ClassLoader from which to load this class
	 * @throws LoggerException if a suitable instance
	 *                         cannot be created
	 */
	protected static LoggerFactory newFactory(final String factoryClass, final ClassLoader classLoader) throws LoggerException
	{
		Object result = AccessController.doPrivileged(new PrivilegedAction()
		{
			public Object run()
			{
				Class logFactoryClass = null;

				try
				{
					if (classLoader != null)
					{
						try
						{
							logFactoryClass = classLoader.loadClass(factoryClass);
							return (LoggerFactory) logFactoryClass.newInstance();

						}
						catch (ClassNotFoundException ex)
						{
							if (classLoader == LoggerFactory.class.getClassLoader()) throw ex;
							// ignore exception, continue
						}
						catch (NoClassDefFoundError e)
						{
							if (classLoader == LoggerFactory.class.getClassLoader()) throw e;
						}
						catch (ClassCastException e)
						{
							if (classLoader == LoggerFactory.class.getClassLoader()) throw e;
						}

					}

					logFactoryClass = Class.forName(factoryClass);

					return (LoggerFactory) logFactoryClass.newInstance();
				}
				catch (Exception e)
				{
					// Check to see if we've got a bad configuration
					if (logFactoryClass != null && !LoggerFactory.class.isAssignableFrom(logFactoryClass)) return new LoggerException("Chosen Logger implementation does not extend LoggerFactory. Please check your configuration.", e);

					return new LoggerException(e);
				}
			}
		});

		if (result instanceof LoggerException) throw (LoggerException) result;

		return (LoggerFactory) result;
	}

	/**
	 * Check whether Log4J is used or not
	 *
	 * @return true if Log4J is the primary logging channel used by current AREasy instance
	 */
	public abstract boolean isLog4JLoggerUsed();

	/**
	 * Check whether JDKLogger is used or not
	 *
	 * @return true if JDK logger is the primary logging channel used by current AREasy instance
	 */
	public abstract boolean isJdkLoggerUsed();

	/**
	 * Check if Log4J is already initialized within the environment.
	 *
	 * @return true if the Log4J is already initialized and configured
	 */
	public abstract boolean isLog4JShared();
}
