package org.areasy.common.logger.base.jdk;

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

import org.areasy.common.logger.base.LoggerManager;
import org.areasy.common.logger.LoggerException;
import org.areasy.common.data.StringUtility;

import java.util.logging.*;
import java.io.*;
import java.lang.reflect.Constructor;

/**
 * Jdk manager to initialize and to manage logger engine.
 */
public class JdkManager extends LoggerManager
{
	/**
	 * Create a new logger, discovering handler (appender) type by input parameters.
	 *
	 * @param loggerName logger name. If is null will be created root logger.
	 * @param loggerLevel accepted log level.
	 * @param handlerName handler (appender) name
	 * @param handlerFileName handler file name. If if not null will be created a file handler.
	 * @param handlerAppend specify if data iwill be appended (only for file handler).
	 * @param handlerWriter a specific writer (not used for JDK logger)
	 * @param handlerFormatterType define layout type (0 = simple text, 1 = xml format)
	 * @param handlerFormatterPattern specify string pattern for simple layout (not used for JDK logger).
	 */
	public void addLogger(String loggerName, int loggerLevel, String handlerName, String handlerFileName, boolean handlerAppend, Object handlerWriter, int handlerFormatterType, String handlerFormatterPattern) throws LoggerException
	{
		Logger logger;

		if(LoggerManager.getLogger(loggerName) != null) return;

		try
		{
			//create logger instance
			if(loggerName == null || loggerName.trim().length() == 0) loggerName = "root";
			if(StringUtility.equalsIgnoreCase("root", loggerName)) logger = Logger.getLogger("");
				else logger = Logger.getLogger(loggerName);

			//establish level.
			setLevel(logger, loggerLevel);

			//establish handler.
			Handler appender = null;
			Object handler = LoggerManager.getHandler(handlerName);

			if(handler == null)
			{
				Formatter layout = null;
				if(handlerFormatterType == FORMATTER_XML) layout = new XMLFormatter();
					else layout = new SimpleFormatter();

				if(handlerFileName != null && handlerFileName.trim().length() > 0)
				{
					File file = new File(handlerFileName);
					if(!file.getParentFile().exists()) file.getParentFile().mkdirs();

					appender = createFileHandler(loggerName, handlerFileName, handlerAppend);
					appender.setFormatter(layout);
				}
				else if(handlerWriter != null && handlerWriter instanceof OutputStream)
				{
					appender = new StreamHandler((OutputStream)handlerWriter, layout);
				}
				else
				{
					appender = new ConsoleHandler();
					appender.setFormatter(layout);
				}
			}
			else appender = (Handler)handler;

			logger.addHandler(appender);

			//append logger definition in loggers container
			LoggerDefinition definition = new LoggerDefinition(loggerName, loggerLevel, handlerName, handlerFileName, handlerAppend, handlerWriter, handlerFormatterType, handlerFormatterPattern);

			definition.setLogger(logger);
			definition.setHandler(appender);

			LoggerManager.addLoggerDefinition(definition);

			//remove default console handler
			if (!(appender instanceof ConsoleHandler))
			{
				Logger l0 = Logger.getLogger("");
				l0.removeHandler(l0.getHandlers()[0]);
			}
		}
		catch(Throwable th)
		{
			throw new LoggerException(th);
		}
	}

	protected Handler createFileHandler(String loggerName, String handlerFileName, boolean handlerAppend) throws IOException
	{
		Handler handler = null;

		String envHName = System.getProperty(loggerName + ".logger.file.handler.class");
		if(StringUtility.isEmpty(envHName)) envHName = System.getProperty("logger.file.handler.class");

		if(StringUtility.isEmpty(envHName)) handler = new FileHandler(handlerFileName, handlerAppend);
		else
		{
			try
			{
				Class hc = Class.forName(envHName);

				Class[] csignature = new Class[2];
				csignature[0] = String.class;
				csignature[1] = Boolean.class;

				Object params[] = new Object[2];
				params[0] = handlerFileName;
				params[1] = new Boolean(handlerAppend);

				Constructor constructor = hc.getConstructor(csignature);
				handler = (Handler)constructor.newInstance(params);
			}
			catch(Exception e)
			{
				System.err.println("Handler class name '" + envHName + "' could not be instantiated.");
				handler = new FileHandler(handlerFileName, handlerAppend);
			}
		}

		return handler;
	}

	protected void setLevel(Object loggerObject, int loggerLevel)
	{
		if(!(loggerObject instanceof Logger)) return;
		Logger logger = (Logger)loggerObject;

		if(loggerLevel == org.areasy.common.logger.Logger.LOG_LEVEL_ALL) logger.setLevel(Level.ALL);
			else if(loggerLevel == org.areasy.common.logger.Logger.LOG_LEVEL_TRACE) logger.setLevel(Level.FINEST);
				else if(loggerLevel == org.areasy.common.logger.Logger.LOG_LEVEL_DEBUG) logger.setLevel(Level.FINE);
					else if(loggerLevel == org.areasy.common.logger.Logger.LOG_LEVEL_INFO) logger.setLevel(Level.INFO);
						else if(loggerLevel == org.areasy.common.logger.Logger.LOG_LEVEL_WARN) logger.setLevel(Level.WARNING);
							else if(loggerLevel == org.areasy.common.logger.Logger.LOG_LEVEL_ERROR) logger.setLevel(Level.SEVERE);
								else if(loggerLevel == org.areasy.common.logger.Logger.LOG_LEVEL_FATAL) logger.setLevel(Level.SEVERE);
									else if(loggerLevel == org.areasy.common.logger.Logger.LOG_LEVEL_OFF) logger.setLevel(Level.OFF);
	}

	/**
	 * Create a new logger, discovering handlers (appenders) type by input parameters.
	 *
	 * @param loggerName logger name. If is null will be created root logger.
	 * @param loggerLevel accepted log level.
	 * @param handlerName handler (appender) name
	 * @param handlerFormatterPattern specify string pattern for simple layout.
	 * @throws LoggerException
	 */
	public void addConsoleLogger(String loggerName, int loggerLevel, String handlerName, String handlerFormatterPattern) throws LoggerException
	{
		addLogger(loggerName, loggerLevel, handlerName, null, false, null, FORMATTER_SIMPLE, handlerFormatterPattern);
	}

	/**
	 * Create a custom logger, defining a special appender to write to a dedicated writer or output stream.
	 *
	 * @param loggerName logger name
	 * @param loggerLevel logger level
	 * @param handlerName handler name
	 * @param handlerWriter handler writer
	 * @throws LoggerException
	 */
	public void addWriterLogger(String loggerName, int loggerLevel, String handlerName, Object handlerWriter, String handlerFormatterPattern) throws LoggerException
	{
		addLogger(loggerName, loggerLevel, handlerName, null, false, handlerWriter, FORMATTER_SIMPLE, handlerFormatterPattern);
	}

	/**
	 * Create a new logger with a file handler.
	 *
	 * @param loggerName logger name
	 * @param loggerLevel logger level
	 * @param handlerName handler name
	 * @param handlerFileName handler file name
	 * @param handlerAppend specify if handler will append data.
	 * @throws LoggerException
	 */
	public void addFileLogger(String loggerName, int loggerLevel, String handlerName, String handlerFileName, boolean handlerAppend) throws LoggerException
	{
		addLogger(loggerName, loggerLevel, handlerName, handlerFileName, handlerAppend, null, FORMATTER_SIMPLE, null);
	}

	/**
	 * Create a new logger with a file handler.
	 *
	 * @param loggerName logger name
	 * @param loggerLevel logger level
	 * @param handlerName handler name
	 * @param handlerFileName handler file name
	 * @param handlerAppend specify if hadler will append data
	 * @param handlerFormatterType handler type (simple or xml)
	 * @param handlerFormatterPattern handler pattern (only for simple handler type)
	 *
	 * @throws LoggerException
	 */
	public void addFileLogger(String loggerName, int loggerLevel, String handlerName, String handlerFileName, boolean handlerAppend, int handlerFormatterType, String handlerFormatterPattern) throws LoggerException
	{
		addLogger(loggerName, loggerLevel, handlerName, handlerFileName, handlerAppend, null, handlerFormatterType, handlerFormatterPattern);
	}
}
