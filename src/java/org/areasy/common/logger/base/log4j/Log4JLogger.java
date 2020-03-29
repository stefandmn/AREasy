package org.areasy.common.logger.base.log4j;

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

import org.apache.log4j.Level;
import org.apache.log4j.Priority;

import java.io.Serializable;

/**
 * <p>Implementation of {@link org.apache.log4j.Logger} that maps directly to a Log4J
 * <strong>Logger</strong>.  Initial configuration of the corresponding
 * Logger instances should be done in the usual manner, as outlined in
 * the Log4J documentation.</p>
 */
public class Log4JLogger implements org.areasy.common.logger.Logger, Serializable
{
	/**
	 * The fully qualified name of the DefaultLog4JLogger class.
	 */
	private static final String FQCN = Log4JLogger.class.getName();

	private static final boolean is12 = Priority.class.isAssignableFrom(Level.class);

	/**
	 * Logger to this logger
	 */
	private transient org.apache.log4j.Logger logger = null;

	/**
	 * Logger name
	 */
	private String name = null;

	public Log4JLogger()
	{
		//nothing to do here
	}

	/**
	 * Base constructor.
	 */
	public Log4JLogger(String name)
	{
		this.name = name;
		this.logger = getLogger();
	}

	/**
	 * For use with a log4j factory.
	 */
	public Log4JLogger(org.apache.log4j.Logger logger)
	{
		this.name = logger.getName();
		this.logger = logger;
	}

	/**
	 * Logger a message to the Log4j Logger with <code>TRACE</code> priority.
	 * Currently logs to <code>DEBUG</code> level in Log4J.
	 */
	public void trace(Object message)
	{
		if (is12) getLogger().log(FQCN, Level.ALL, message, null);
			else getLogger().log(FQCN, Level.ALL, message, null);
	}

	/**
	 * Logger an error to the Log4j Logger with <code>TRACE</code> priority.
	 * Currently logs to <code>DEBUG</code> level in Log4J.
	 */
	public void trace(Object message, Throwable t)
	{
		if (is12) getLogger().log(FQCN, Level.ALL, message, t);
			else getLogger().log(FQCN, Level.ALL, message, t);
	}

	/**
	 * Logger a message to the Log4j Logger with <code>DEBUG</code> priority.
	 */
	public void debug(Object message)
	{
		if (is12) getLogger().log(FQCN, Level.DEBUG, message, null);
			else getLogger().log(FQCN, Level.DEBUG, message, null);
	}

	/**
	 * Logger an error to the Log4j Logger with <code>DEBUG</code> priority.
	 */
	public void debug(Object message, Throwable t)
	{
		if (is12) getLogger().log(FQCN, Level.DEBUG, message, t);
			else getLogger().log(FQCN, Level.DEBUG, message, t);
	}

	/**
	 * Logger a message to the Log4j Logger with <code>INFO</code> priority.
	 */
	public void info(Object message)
	{
		if (is12) getLogger().log(FQCN, Level.INFO, message, null);
			else getLogger().log(FQCN, Level.INFO, message, null);
	}

	/**
	 * Logger an error to the Log4j Logger with <code>INFO</code> priority.
	 */
	public void info(Object message, Throwable t)
	{
		if (is12)getLogger().log(FQCN, Level.INFO, message, t);
			else getLogger().log(FQCN, Level.INFO, message, t);
	}

	/**
	 * Logger a message to the Log4j Logger with <code>WARN</code> priority.
	 */
	public void warn(Object message)
	{
		if (is12) getLogger().log(FQCN, Level.WARN, message, null);
			else getLogger().log(FQCN, Level.WARN, message, null);
	}

	/**
	 * Logger an error to the Log4j Logger with <code>WARN</code> priority.
	 */
	public void warn(Object message, Throwable t)
	{
		if (is12) getLogger().log(FQCN, Level.WARN, message, t);
			else getLogger().log(FQCN, Level.WARN, message, t);
	}

	/**
	 * Logger a message to the Log4j Logger with <code>ERROR</code> priority.
	 */
	public void error(Object message)
	{
		if (is12) getLogger().log(FQCN, Level.ERROR, message, null);
			else getLogger().log(FQCN, Level.ERROR, message, null);
	}

	/**
	 * Logger an error to the Log4j Logger with <code>ERROR</code> priority.
	 */
	public void error(Object message, Throwable t)
	{
		if (is12) getLogger().log(FQCN, Level.ERROR, message, t);
			else getLogger().log(FQCN, Level.ERROR, message, t);
	}

	/**
	 * Logger a message to the Log4j Logger with <code>FATAL</code> priority.
	 */
	public void fatal(Object message)
	{
		if (is12) getLogger().log(FQCN, Level.FATAL, message, null);
			else getLogger().log(FQCN, Level.FATAL, message, null);
	}

	/**
	 * Logger an error to the Log4j Logger with <code>FATAL</code> priority.
	 */
	public void fatal(Object message, Throwable t)
	{
		if (is12) getLogger().log(FQCN, Level.FATAL, message, t);
			else getLogger().log(FQCN, Level.FATAL, message, t);
	}

	/**
	 * Return the native Logger instance we are using.
	 */
	public org.apache.log4j.Logger getLogger()
	{
		if (logger == null) logger = org.apache.log4j.Logger.getLogger(name);

		return (this.logger);
	}

	/**
	 * Check whether the Log4j Logger used is enabled for <code>DEBUG</code> priority.
	 */
	public boolean isDebugEnabled()
	{
		return getLogger().isDebugEnabled();
	}

	/**
	 * Check whether the Log4j Logger used is enabled for <code>ERROR</code> priority.
	 */
	public boolean isErrorEnabled()
	{
		if (is12) return getLogger().isEnabledFor(Level.ERROR);
			else return getLogger().isEnabledFor(Level.ERROR);
	}

	/**
	 * Check whether the Log4j Logger used is enabled for <code>FATAL</code> priority.
	 */
	public boolean isFatalEnabled()
	{
		if (is12) return getLogger().isEnabledFor(Level.FATAL);
			else return getLogger().isEnabledFor(Level.FATAL);
	}

	/**
	 * Check whether the Log4j Logger used is enabled for <code>INFO</code> priority.
	 */
	public boolean isInfoEnabled()
	{
		return getLogger().isInfoEnabled();
	}

	/**
	 * Check whether the Log4j Logger used is enabled for <code>TRACE</code> priority.
	 * For Log4J, this returns the value of <code>isDebugEnabled()</code>
	 */
	public boolean isTraceEnabled()
	{
		return getLogger().isEnabledFor(Level.ALL);
	}

	/**
	 * Check whether the Log4j Logger used is enabled for <code>WARN</code> priority.
	 */
	public boolean isWarnEnabled()
	{
		if (is12) return getLogger().isEnabledFor(Level.WARN);
			else return getLogger().isEnabledFor(Level.WARN);
	}

	/**
	 * Remove logger instance in concordance with actual logger manager implementation
	 */
	public void remove()
	{
		getLogger().removeAllAppenders();
	}
}
