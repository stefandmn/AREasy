package org.areasy.common.logger.base.jdk;

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

import java.io.Serializable;
import java.util.logging.Level;
import java.util.logging.Handler;

/**
 * <p>Implementation of the <code>org.areasy.common.logger.Logger</code>
 * interface that wraps the standard JDK logging mechanisms that were
 * introduced in the Merlin release (JDK 1.4).</p>
 */

public class JdkLogger implements org.areasy.common.logger.Logger, Serializable
{
	public JdkLogger()
	{
		//nothing to do here
	}

	/**
	 * Construct a named instance of this Logger.
	 *
	 * @param name Name of the logger to be constructed
	 */
	public JdkLogger(String name)
	{
		this.name = name;
		logger = getLogger();
	}

	/**
	 * The underlying Logger implementation we are using.
	 */
	protected transient java.util.logging.Logger logger = null;

	/**
	 * The name of the logger we are wrapping.
	 */
	protected String name = null;


	private void log(Level level, String msg, Throwable ex)
	{

		java.util.logging.Logger logger = getLogger();
		if (logger.isLoggable(level))
		{
			// Hack (?) to get the stack trace.
			Throwable dummyException = new Throwable();
			StackTraceElement locations[] = dummyException.getStackTrace();

			// Caller will be the third element
			String cname = "unknown";
			String method = "unknown";

			if (locations != null && locations.length > 2)
			{
				StackTraceElement caller = locations[2];
				cname = caller.getClassName();
				method = caller.getMethodName();
			}
			
			if (ex == null) logger.logp(level, cname, method, msg);
				else logger.logp(level, cname, method, msg, ex);
		}
	}

	/**
	 * Logger a message with debug log level.
	 */
	public void debug(Object message)
	{
		log(Level.FINE, String.valueOf(message), null);
	}

	/**
	 * Logger a message and exception with debug log level.
	 */
	public void debug(Object message, Throwable exception)
	{
		log(Level.FINE, String.valueOf(message), exception);
	}

	/**
	 * Logger a message with error log level.
	 */
	public void error(Object message)
	{
		log(Level.SEVERE, String.valueOf(message), null);
	}

	/**
	 * Logger a message and exception with error log level.
	 */
	public void error(Object message, Throwable exception)
	{
		log(Level.SEVERE, String.valueOf(message), exception);
	}

	/**
	 * Logger a message with fatal log level.
	 */
	public void fatal(Object message)
	{
		log(Level.SEVERE, String.valueOf(message), null);
	}

	/**
	 * Logger a message and exception with fatal log level.
	 */
	public void fatal(Object message, Throwable exception)
	{
		log(Level.SEVERE, String.valueOf(message), exception);
	}

	/**
	 * Return the native Logger instance we are using.
	 */
	public java.util.logging.Logger getLogger()
	{
		if (logger == null) logger = java.util.logging.Logger.getLogger(name);

		return (logger);
	}

	/**
	 * Logger a message with info log level.
	 */
	public void info(Object message)
	{
		log(Level.INFO, String.valueOf(message), null);
	}

	/**
	 * Logger a message and exception with info log level.
	 */
	public void info(Object message, Throwable exception)
	{
		log(Level.INFO, String.valueOf(message), exception);
	}

	/**
	 * Is debug logging currently enabled?
	 */
	public boolean isDebugEnabled()
	{
		return (getLogger().isLoggable(Level.FINE));
	}

	/**
	 * Is error logging currently enabled?
	 */
	public boolean isErrorEnabled()
	{
		return (getLogger().isLoggable(Level.SEVERE));
	}

	/**
	 * Is fatal logging currently enabled?
	 */
	public boolean isFatalEnabled()
	{
		return (getLogger().isLoggable(Level.SEVERE));
	}

	/**
	 * Is info logging currently enabled?
	 */
	public boolean isInfoEnabled()
	{
		return (getLogger().isLoggable(Level.INFO));
	}

	/**
	 * Is trace logging currently enabled?
	 */
	public boolean isTraceEnabled()
	{
		return (getLogger().isLoggable(Level.FINEST));
	}

	/**
	 * Is warn logging currently enabled?
	 */
	public boolean isWarnEnabled()
	{
		return (getLogger().isLoggable(Level.WARNING));
	}

	/**
	 * Logger a message with trace log level.
	 */
	public void trace(Object message)
	{
		log(Level.FINEST, String.valueOf(message), null);
	}

	/**
	 * Logger a message and exception with trace log level.
	 */
	public void trace(Object message, Throwable exception)
	{
		log(Level.FINEST, String.valueOf(message), exception);
	}

	/**
	 * Logger a message with warn log level.
	 */
	public void warn(Object message)
	{
		log(Level.WARNING, String.valueOf(message), null);
	}

	/**
	 * Logger a message and exception with warn log level.
	 */
	public void warn(Object message, Throwable exception)
	{
		log(Level.WARNING, String.valueOf(message), exception);
	}

	/**
	 * Remove logger instance in concordance with actual logger manager implementation
	 */
	public void remove()
	{
		Handler[] handlers = getLogger().getHandlers();

		for(int i = 0; handlers != null && i < handlers.length; i++)
		{
			logger.removeHandler(handlers[i]);
		}

		logger = null;
	}
}
