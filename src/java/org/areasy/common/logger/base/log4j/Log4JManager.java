package org.areasy.common.logger.base.log4j;

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

import org.apache.log4j.*;
import org.apache.log4j.xml.XMLLayout;
import org.areasy.common.data.NumberUtility;
import org.areasy.common.data.StringUtility;
import org.areasy.common.logger.LoggerException;
import org.areasy.common.logger.base.LoggerManager;

import java.io.File;
import java.io.OutputStream;
import java.io.Writer;

/**
 * Log4J manager to initialize and to manage logger engine.
 */
public class Log4JManager extends LoggerManager
{
	/**
	 * Create a new logger, discovering handler (appender) type by input parameters.
	 *
	 * @param loggerName logger name. If is null will be created root logger.
	 * @param loggerLevel accepted log level.
	 * @param handlerName handler (appender) name
	 * @param handlerFileName handler file name. If if not null will be created a file handler.
	 * @param handlerAppend specify if data will be appended (only for file handler).
	 * @param handlerWriter a specific writer (<code>OutputStream</code> object instance).
	 * @param handlerFormatterType define layout type (0 = simple text, 1 = xml format)
	 * @param handlerFormatterPattern specify string pattern for simple layout.
	 */
	public void addLogger(String loggerName, int loggerLevel, String handlerName, String handlerFileName, boolean handlerAppend, Object handlerWriter, int handlerFormatterType, String handlerFormatterPattern) throws LoggerException
	{
		Logger logger;
		
		if(LoggerManager.getLogger(loggerName) != null) return;

		try
		{
			//create logger
			if(loggerName == null || loggerName.trim().length() == 0) loggerName = "root";
			if(StringUtility.equalsIgnoreCase("root", loggerName)) logger = Logger.getRootLogger();
				else logger = Logger.getLogger(loggerName);

			//establish level.
			setLevel(logger, loggerLevel);

			//establish handler.
			Appender appender = null;
			Object handler = LoggerManager.getHandler(handlerName);

			if(handler == null)
			{
				//formatter
				Layout layout = null;
				if(handlerFormatterType == FORMATTER_XML) layout = new XMLLayout();
				else
				{
					if(handlerFormatterPattern != null && handlerFormatterPattern.trim().length() > 0)
						layout = new PatternLayout(handlerFormatterPattern);
						else
						layout = new PatternLayout();
				}

				//define handler
				if(handlerFileName != null && handlerFileName.trim().length() > 0)
				{
					File file = new File(handlerFileName);
					if(!file.getParentFile().exists()) file.getParentFile().mkdirs();

					appender = createFileHandler(loggerName);

					((FileAppender)appender).setFile(handlerFileName);
					((FileAppender)appender).setAppend(handlerAppend);

					appender.setLayout(layout);
					if(appender instanceof FileAppender) ((FileAppender)appender).activateOptions();
				}
				else if(handlerWriter != null && ((handlerWriter instanceof Writer) ||(handlerWriter instanceof OutputStream)))
				{
					appender = new WriterAppender();

					if(handlerWriter instanceof Writer) appender = new WriterAppender(layout, (Writer)handlerWriter);
						else if(handlerWriter instanceof OutputStream)  appender = new WriterAppender(layout, (OutputStream)handlerWriter);

					((WriterAppender)appender).activateOptions();
				}
				else
				{
					appender = new ConsoleAppender(layout, "System.out");
					((ConsoleAppender)appender).activateOptions();
				}

				appender.setName(handlerName);
			}
			else appender = (Appender)handler;

			logger.addAppender(appender);
			logger.setAdditivity(false);

			//append logger definition in loggers container
			LoggerDefinition definition = new LoggerDefinition(loggerName, loggerLevel, handlerName, handlerFileName, handlerAppend, handlerWriter, handlerFormatterType, handlerFormatterPattern);

			definition.setLogger(logger);
			definition.setHandler(appender);

			LoggerManager.addLoggerDefinition(definition);
		}
		catch(Throwable th)
		{
			throw new LoggerException(th);
		}
	}

	protected Appender createFileHandler(String loggerName)
	{
		Appender handler = null;

		String envHName = System.getProperty(loggerName + ".logger.file.handler.class");
		if(StringUtility.isEmpty(envHName)) envHName = System.getProperty("logger.file.handler.class");

		if(StringUtility.isEmpty(envHName))
		{
			handler = new RollingFileAppender();

			String MaxFileSize = System.getProperty(loggerName + ".logger.file.handler.MaxFileSize");
			if(StringUtility.isEmpty(MaxFileSize)) MaxFileSize = System.getProperty("logger.file.handler.MaxFileSize");
			if(StringUtility.isEmpty(MaxFileSize)) MaxFileSize = "10MB";

			String MaxBackupIndex = System.getProperty(loggerName + ".logger.file.handler.MaxBackupIndex");
			if(StringUtility.isEmpty(MaxBackupIndex)) MaxBackupIndex = System.getProperty("logger.file.handler.MaxBackupIndex");
			if(StringUtility.isEmpty(MaxBackupIndex)) MaxBackupIndex = "10";

			((RollingFileAppender)handler).setMaxFileSize(MaxFileSize);
			((RollingFileAppender)handler).setMaxBackupIndex(NumberUtility.toInt(MaxBackupIndex, 10));
		}
		else
		{
			try
			{
				handler = (Appender) Class.forName(envHName).newInstance();
			}
			catch(Exception e)
			{
				System.err.println("Handler class name '" + envHName + "' could not be instantiated.");
				handler = new FileAppender();
			}
		}

		return handler;
	}

	protected void setLevel(Object loggerObject, int loggerLevel)
	{
		if(!(loggerObject instanceof Logger)) return;
		Logger logger = (Logger)loggerObject;

		if(loggerLevel == org.areasy.common.logger.Logger.LOG_LEVEL_ALL) logger.setLevel(Level.ALL);
			else if(loggerLevel == org.areasy.common.logger.Logger.LOG_LEVEL_TRACE) logger.setLevel(Level.TRACE);
				else if(loggerLevel == org.areasy.common.logger.Logger.LOG_LEVEL_DEBUG) logger.setLevel(Level.DEBUG);
					else if(loggerLevel == org.areasy.common.logger.Logger.LOG_LEVEL_INFO) logger.setLevel(Level.INFO);
						else if(loggerLevel == org.areasy.common.logger.Logger.LOG_LEVEL_WARN) logger.setLevel(Level.WARN);
							else if(loggerLevel == org.areasy.common.logger.Logger.LOG_LEVEL_ERROR) logger.setLevel(Level.ERROR);
								else if(loggerLevel == org.areasy.common.logger.Logger.LOG_LEVEL_FATAL) logger.setLevel(Level.FATAL);
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
	 * @param handlerAppend specify if handler will append data
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
