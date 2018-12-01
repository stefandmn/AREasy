package org.areasy.common.logger;

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

/**
 * <p>A simple logging interface abstracting logging APIs.  In order to be
 * instantiated successfully by {@link LoggerFactory}, classes that implement
 * this interface must have a constructor that takes a single String
 * parameter representing the "name" of this Logger.</p>
 * <p/>
 * <p> The six logging levels used by <code>Logger</code> are (in order):
 * <ol>
 * <li>trace (the least serious)</li>
 * <li>debug</li>
 * <li>info</li>
 * <li>warn</li>
 * <li>error</li>
 * <li>fatal (the most serious)</li>
 * </ol>
 * The mapping of these log levels to the concepts used by the underlying
 * logging system is implementation dependent.
 * The implementation should ensure, though, that this ordering behaves
 * as expected.</p>
 * <p/>
 * <p>Performance is often a logging concern.
 * By examining the appropriate property,
 * a component can avoid expensive operations (producing information
 * to be logged).</p>
 * <p/>
 * <p> For example,
 * <code><pre>
 *    if (log.isDebugEnabled()) {
 *        ... do something expensive ...
 *        log.debug(theResult);
 *    }
 * </pre></code>
 * </p>
 * <p/>
 * <p>Configuration of the underlying logging system will generally be done
 * external to the Logging APIs, through whatever mechanism is supported by
 * that system.</p>
 */
public interface Logger
{
	/**
	 * "Trace" level logging.
	 */
	public static final int LOG_LEVEL_TRACE = 1;
	/**
	 * "Debug" level logging.
	 */
	public static final int LOG_LEVEL_DEBUG = 2;
	/**
	 * "Info" level logging.
	 */
	public static final int LOG_LEVEL_INFO = 3;
	/**
	 * "Warn" level logging.
	 */
	public static final int LOG_LEVEL_WARN = 4;
	/**
	 * "Error" level logging.
	 */
	public static final int LOG_LEVEL_ERROR = 5;
	/**
	 * "Fatal" level logging.
	 */
	public static final int LOG_LEVEL_FATAL = 6;

	/**
	 * Enable all logging levels
	 */
	public static final int LOG_LEVEL_ALL = (LOG_LEVEL_TRACE - 1);

	/**
	 * Enable no logging levels
	 */
	public static final int LOG_LEVEL_OFF = (LOG_LEVEL_FATAL + 1);


	/**
	 * The name of the system property identifying our {@link Logger}
	 * implementation class.
	 */
	public static final String LOG_PROPERTY = "org.areasy.common.logger.Logger";

	/**
	 * The name of the system property identifying our {@link org.areasy.common.logger.base.LoggerManager}
	 * implementation class.
	 */
	public static final String MANAGER_PROPERTY = "org.areasy.common.logger.Manager";

	/**
	 * <p> Is debug logging currently enabled? </p>
	 * <p/>
	 * <p> Call this method to prevent having to perform expensive operations
	 * (for example, <code>String</code> concatenation)
	 * when the log level is more than debug. </p>
	 */
	boolean isDebugEnabled();


	/**
	 * <p> Is error logging currently enabled? </p>
	 * <p/>
	 * <p> Call this method to prevent having to perform expensive operations
	 * (for example, <code>String</code> concatenation)
	 * when the log level is more than error. </p>
	 */
	boolean isErrorEnabled();


	/**
	 * <p> Is fatal logging currently enabled? </p>
	 * <p/>
	 * <p> Call this method to prevent having to perform expensive operations
	 * (for example, <code>String</code> concatenation)
	 * when the log level is more than fatal. </p>
	 */
	boolean isFatalEnabled();


	/**
	 * <p> Is info logging currently enabled? </p>
	 * <p/>
	 * <p> Call this method to prevent having to perform expensive operations
	 * (for example, <code>String</code> concatenation)
	 * when the log level is more than info. </p>
	 */
	boolean isInfoEnabled();


	/**
	 * <p> Is trace logging currently enabled? </p>
	 * <p/>
	 * <p> Call this method to prevent having to perform expensive operations
	 * (for example, <code>String</code> concatenation)
	 * when the log level is more than trace. </p>
	 */
	boolean isTraceEnabled();


	/**
	 * <p> Is warn logging currently enabled? </p>
	 * <p/>
	 * <p> Call this method to prevent having to perform expensive operations
	 * (for example, <code>String</code> concatenation)
	 * when the log level is more than warn. </p>
	 */
	boolean isWarnEnabled();


	/**
	 * <p> Logger a message with trace log level. </p>
	 *
	 * @param message log this message
	 */
	void trace(Object message);


	/**
	 * <p> Logger an error with trace log level. </p>
	 *
	 * @param message log this message
	 * @param t       log this cause
	 */
	void trace(Object message, Throwable t);


	/**
	 * <p> Logger a message with debug log level. </p>
	 *
	 * @param message log this message
	 */
	void debug(Object message);


	/**
	 * <p> Logger an error with debug log level. </p>
	 *
	 * @param message log this message
	 * @param t       log this cause
	 */
	void debug(Object message, Throwable t);


	/**
	 * <p> Logger a message with info log level. </p>
	 *
	 * @param message log this message
	 */
	void info(Object message);


	/**
	 * <p> Logger an error with info log level. </p>
	 *
	 * @param message log this message
	 * @param t       log this cause
	 */
	void info(Object message, Throwable t);


	/**
	 * <p> Logger a message with warn log level. </p>
	 *
	 * @param message log this message
	 */
	void warn(Object message);


	/**
	 * <p> Logger an error with warn log level. </p>
	 *
	 * @param message log this message
	 * @param t       log this cause
	 */
	void warn(Object message, Throwable t);


	/**
	 * <p> Logger a message with error log level. </p>
	 *
	 * @param message log this message
	 */
	void error(Object message);


	/**
	 * <p> Logger an error with error log level. </p>
	 *
	 * @param message log this message
	 * @param t       log this cause
	 */
	void error(Object message, Throwable t);


	/**
	 * <p> Logger a message with fatal log level. </p>
	 *
	 * @param message log this message
	 */
	void fatal(Object message);


	/**
	 * <p> Logger an error with fatal log level. </p>
	 *
	 * @param message log this message
	 * @param t       log this cause
	 */
	void fatal(Object message, Throwable t);

	/**
	 * Remove logger instance from all handlers and appenders and the implementation
	 * is in concordance with the specific logger manager implementation
	 */
	void remove();
}
