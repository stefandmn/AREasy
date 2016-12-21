package org.areasy.common.logger.base;

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

import org.areasy.common.support.configuration.Configuration;
import org.areasy.common.support.configuration.providers.properties.BasePropertiesConfiguration;
import org.areasy.common.support.configuration.providers.properties.stream.PropertiesConfiguration;
import org.areasy.common.logger.LoggerException;
import org.areasy.common.logger.Logger;
import org.areasy.common.logger.LoggerFactory;
import org.areasy.common.data.StringUtility;

import java.util.*;

/**
 * This logger manager library could be used to initialize and to manage different implementation of logger engines.
 * With this library any implemented logger engine could be manage using an unique model and flow and could be simplified
 * at three logger components:
 * <ul>
 * <li>logger - identify basic logger entity</li>
 * <li>handler - logger fragment that will specify what output will have this logger. In the current implementation
 * are supported the following handlers: console, file and writer (stream)</li>
 * <li>formatter - handler fragment to format written records. Supported types are: simple text (in some situations
 * with a pattern) and xml output.</li>
 * </ul>
 *
 * <p>
 * <b>Sample configuration:</b>
 * </p>
 * <pre>
 * common.loggers = root
 * common.loggers = mylogger
 *
 * common.logger.[logger name].level = info
 * common.logger.[logger name].handler = test
 *
 * common.logger.handler.[handler name].filename = /log/file/name/and/path.log
 * common.logger.handler.[handler name].formatter = simple
 * common.logger.handler.[handler name].formatter.pattern = %p [%d]
 *
 * common.logger.handler.test.formatter = simple
 * <pre>
 */
public abstract class LoggerManager
{
	/** Dedicated member to store all declared loggers with complete coordinates */
	private static final transient List loggers = new Vector();

	/** Simple formatter identifier */
	public static final int FORMATTER_SIMPLE = 0;

	/** XML formatter identifier */
	public static final int FORMATTER_XML = 1;

	/**
	 * Check if logger engines was initialized.
	 */
	public final boolean isInit()
	{
		return (!loggers.isEmpty());
	}

	/**
	 * Returns a map with all defined loggers, mapped at logger names.
	 */
	public List getLoggerDefinitions()
	{
		return loggers;
	}

	/**
	 * Add a new logger definition.
	 */
	protected static void addLoggerDefinition(LoggerDefinition definition)
	{
		if(definition != null)
		{
			synchronized(loggers)
			{
				loggers.add(definition);
			}
		}
	}

	/**
	 * Returns handler by name from repository
	 */
	public static Object getLogger(String name)
	{
		if(name == null || name.trim().length() == 0) return null;

		for(int i = 0; i < loggers.size(); i++)
		{
			LoggerDefinition definition = (LoggerDefinition)loggers.get(i);
			if(StringUtility.equals(definition.getLoggerName(), name)) return definition.getLogger();
		}

		return null;
	}

	/**
	 * Returns handler by name from repository
	 */
	public static Object getHandler(String name)
	{
		if(name == null || name.trim().length() == 0)return null;

		for(int i = 0; i < loggers.size(); i++)
		{
			LoggerDefinition definition = (LoggerDefinition)loggers.get(i);
			if(StringUtility.equals(definition.getHandlerName(), name)) return definition.getHandler();
		}

		return null;
	}

	/**
	 * Returns handler file names from repository.
	 */
	public static List getHandlerFileNames()
	{
		Vector vector = new Vector();

		for(int i = 0; i < loggers.size(); i++)
		{
			LoggerDefinition definition = (LoggerDefinition)loggers.get(i);
			String file = definition.getHandlerFileName();

			if(StringUtility.isNotEmpty(file)) vector.add(file);
		}

		return vector;
	}

	/**
	 * Configure logger engine using <code>Configuration<code> structure.
	 */
	public final void configure(Configuration configuration) throws LoggerException
	{
		try
		{
			Configuration env = configuration.subset("common.logger.environment");
			
			if(env != null && !env.isEmpty())
			{
				Iterator iterator = env.getKeys();
				while(iterator != null && iterator.hasNext())
				{
					String key = (String) iterator.next();
					String value = env.getString(key);

					if(StringUtility.isNotEmpty(key)) System.setProperty(key, value);
				}
			}

			String[] loggers = configuration.getStringArray("common.loggers");

			for(int i = 0; loggers != null && i < loggers.length; i++)
			{
				String loggerName = loggers[i];

				String levelString = configuration.getString("common.logger." + loggerName + ".level", "info");
				int loggerLevel = getLoggerLevel(levelString);

				String handlerName = configuration.getString("common.logger." + loggerName + ".handler", null);
				if(handlerName != null && handlerName.trim().length() > 0)
				{
					String handlerFileName = configuration.getString("common.logger.handler." + handlerName + ".filename", null);
					boolean handlerAppend = configuration.getBoolean("common.logger.handler." + handlerName + ".append", false);

					String formatterString = configuration.getString("common.logger.handler." + handlerName + ".formatter", "simple");
					int handlerFormatter = getHandlerFormatterType(formatterString);

					String handlerFormatterPattern = configuration.getString("common.logger.handler." + handlerName + ".formatter.pattern", null);

					addLogger(loggerName, loggerLevel, handlerName, handlerFileName, handlerAppend, null, handlerFormatter, handlerFormatterPattern);
				}
			}
		}
		catch(Throwable th)
		{
			throw new LoggerException("Logger configuration error", th);
		}
	}

	/**
	 * Configure logger engine using <code>Properties</code> structure.
	 */
	public final void configure(Properties properties) throws LoggerException
	{
		Configuration configuration = BasePropertiesConfiguration.getConfiguration(properties);
		configure(configuration);
	}

	/**
	 * Configure logger engine using a configuration file name.
	 */
	public final void configure(String filename)
	{
		Configuration configuration;

		try
		{
			configuration = new PropertiesConfiguration(filename);
		}
		catch(Exception ex)
		{
			throw new LoggerException(ex);
		}

		configure(configuration);
	}

	/**
	 * Change loggers' level at runtime. With this method you can make a switch from a logger level to another.
	 * This method will change the verbosity level for all declared and registered loggers.
	 *
	 * @param level level number using Common format (see <code>Logger</code> library)
	 */
	public void changeLevel(int level)
	{
		for(int i = 0; i < loggers.size(); i++)
		{
			LoggerDefinition definition = (LoggerDefinition)loggers.get(i);

			Object loggerObject = definition.getLogger();
			setLevel(loggerObject, level);
			definition.setLevel(level);
		}
	}

	/**
	 * Change loggers' level at runtime. With this method you can make a switch from a logger level to another.
	 * This method will change the verbosity level for all declared and registered loggers.
	 *
	 * @param level level name.
	 */
	public void changeLevel(String level)
	{
		changeLevel(getLoggerLevel(level));
	}

	/**
	 * Change logger level at runtime for a specific logger name.
	 * With this method you can make a switch from a logger level to another.
	 *
	 * @param level level number using Common format (see <code>Logger</code> library)
	 * @param name logger name
	 */
	public void changeLevel(int level, String name)
	{
		for(int i = 0; i < loggers.size(); i++)
		{
			LoggerDefinition definition = (LoggerDefinition)loggers.get(i);

			if(StringUtility.equals(definition.getLoggerName(), name))
			{
				Object loggerObject = definition.getLogger();
				setLevel(loggerObject, level);
				definition.setLevel(level);
			}
		}
	}

	/**
	 * Change logger level at runtime for a specific logger name.
	 * With this method you can make a switch from a logger level to another.
	 *
	 * @param level level name.
	 * @param name logger name
	 */
	public void changeLevel(String level, String name)
	{
		changeLevel(getLoggerLevel(level), name);
	}

	/**
	 * Change logger level at runtime for all defined and registered loggers,
	 * using a Common library logger configuration. Level expressed in configuration structure will be
	 * compared with actual logger level and if are different will be applied the newly level.
	 *
	 * With this method you can make a switch from a logger level to another.
	 *
	 * @param configuration Logger <code>Configuration</code> structure
	 */
	public void changeLevel(Configuration configuration)
	{
		for(int i = 0; i < loggers.size(); i++)
		{
			LoggerDefinition definition = (LoggerDefinition)loggers.get(i);

			String loggerName = definition.getLoggerName();
			int actualLevel = definition.getLoggerLevel();

			Object loggerObject = definition.getLogger();

			String levelString = configuration.getString("common.logger." + loggerName + ".level", null);
			if(StringUtility.isNotEmpty(levelString))
			{
				int newLevel = getLoggerLevel(levelString);
				if(newLevel != actualLevel)
				{
					setLevel(loggerObject, newLevel);
					definition.setLevel(newLevel);
				}
			}
		}
	}

	/**
	 * Gel logger level identifier, parsing level codes.
	 *
	 * @param level level name(trace, debug, info, warn, error, fatal, off)
	 * @return level identifier.
	 */
	public static int getLoggerLevel(String level)
	{
		if ("trace".equalsIgnoreCase(level)) return Logger.LOG_LEVEL_TRACE;
		else if ("debug".equalsIgnoreCase(level)) return Logger.LOG_LEVEL_DEBUG;
		else if ("info".equalsIgnoreCase(level)) return Logger.LOG_LEVEL_INFO;
		else if ("warn".equalsIgnoreCase(level)) return Logger.LOG_LEVEL_WARN;
		else if ("error".equalsIgnoreCase(level)) return Logger.LOG_LEVEL_ERROR;
		else if ("fatal".equalsIgnoreCase(level)) return Logger.LOG_LEVEL_FATAL;
		else if ("off".equalsIgnoreCase(level)) return Logger.LOG_LEVEL_OFF;
		else return Logger.LOG_LEVEL_ALL;
	}

	/**
	 * Get handler formatter type.
	 */
	public static int getHandlerFormatterType(String formatter)
	{
		if ("xml".equalsIgnoreCase(formatter)) return LoggerManager.FORMATTER_XML;
			else return LoggerManager.FORMATTER_SIMPLE;
	}

	public static void removeLogger(String name)
	{
		if(name == null || name.trim().length() == 0) return;

		//remove logger channel.
		Logger logger = LoggerFactory.getLog(name);
		if(logger != null) logger.remove();

		synchronized(loggers)
		{
			boolean found = false;
			
			for(int i = 0; !found && i < loggers.size(); i++)
			{
				LoggerDefinition definition = (LoggerDefinition)loggers.get(i);

				if(StringUtility.equals(definition.getLoggerName(), name))
				{
					loggers.remove(i);
					found = true;
				}
			}
		}
	}

	/**
	 * Create a new logger, discovering handlers (appenders) type by input parameters.
	 *
	 * @param loggerName logger name. If is null will be created root logger.
	 * @param loggerLevel accepted log level.
	 * @param handlerName handler (appender) name
	 * @param handlerFileName handler file name. If if not null will be created a file handler.
	 * @param handlerAppend specify if data iwill be appended (only for file handler).
	 * @param handlerWriter a specific writer (<code>OutputStream</code> object instance).
	 * @param handlerFormatterType define layout type (0 = simple text, 1 = xml format)
	 * @param handlerFormatterPattern specify string pattern for simple layout.
	 */
	public abstract void addLogger(String loggerName, int loggerLevel, String handlerName, String handlerFileName, boolean handlerAppend, Object handlerWriter, int handlerFormatterType, String handlerFormatterPattern) throws LoggerException;

	/**
	 * Create a console logger. This logger will define a new handler, having output standard IO console.
	 * @param loggerName logger name
	 * @param loggerLevel logger level.
	 * @param handlerName handler name
	 */
	public abstract void addConsoleLogger(String loggerName, int loggerLevel, String handlerName, String handlerFormatterPattern) throws LoggerException;

	/**
	 * Create a custom logger, defining a special appender to write to a dedicated writer or output stream.
	 *
	 * @param loggerName logger name
	 * @param loggerLevel logger level
	 * @param handlerName handler name
	 * @param handlerWriter handler writer
	 */
	public abstract void addWriterLogger(String loggerName, int loggerLevel, String handlerName, Object handlerWriter, String handlerFormatterPattern) throws LoggerException;

	/**
	 * Create a new logger with a file handler.
	 *
	 * @param loggerName logger name
	 * @param loggerLevel logger level
	 * @param handlerName handler name
	 * @param handlerFileName handler file name
	 * @param handlerAppend specify if handler will append data.
	 */
	public abstract void addFileLogger(String loggerName, int loggerLevel, String handlerName, String handlerFileName, boolean handlerAppend) throws LoggerException;

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
	 */
	public abstract void addFileLogger(String loggerName, int loggerLevel, String handlerName, String handlerFileName, boolean handlerAppend, int handlerFormatterType, String handlerFormatterPattern) throws LoggerException;

	/**
	 * Set logger level ar runtime.
	 */
	protected abstract void setLevel(Object loggerObject, int loggerLevel);

	/**
	 * Dedicated library to identify loggers coordinates.
	 */
	public final class LoggerDefinition
	{
		private String loggerName = null;
		private int loggerLevel = 0;
		private String handlerName = null;
		private String handlerFileName = null;
		private boolean handlerAppend = false;
		private Object handlerWriter = null;
		private int handlerFormatterType = 0;
		private String handlerFormatterPattern = null;

		private Object logger;
		private Object handler;

		public LoggerDefinition(String loggerName, int loggerLevel, String handlerName, String handlerFileName, boolean handlerAppend, Object handlerWriter, int handlerFormatterType, String handlerFormatterPattern)
		{
			this.loggerName = loggerName;
			this.loggerLevel = loggerLevel;
			this.handlerName = handlerName;
			this.handlerFileName = handlerFileName;
			this.handlerAppend = handlerAppend;
			this.handlerWriter = handlerWriter;
			this.handlerFormatterType = handlerFormatterType;
			this.handlerFormatterPattern = handlerFormatterPattern;
		}

		/**
		 * Get logger name.
		 */
		public String getLoggerName()
		{
			return loggerName;
		}

		/**
		 * Get logger level.
		 */
		public int getLoggerLevel()
		{
			return loggerLevel;
		}

		/**
		 * Get handler name for associated logger.
		 */
		public String getHandlerName()
		{
			return handlerName;
		}

		/**
		 * Get handler file name for associated logger.
		 */
		public String getHandlerFileName()
		{
			return handlerFileName;
		}

		/**
		 * Check if associated handler will append data.
		 */
		public boolean isHandlerAppend()
		{
			return handlerAppend;
		}

		/**
		 * Get handler writer object with associated logger.
		 */
		public Object getHandlerWriter()
		{
			return handlerWriter;
		}

		/**
		 * Get associated formatter type with the current handler.
		 */
		public int getHandlerFormatterType()
		{
			return handlerFormatterType;
		}

		/**
		 * Get associated formatter pattern with the current handler.
		 */
		public String getHandlerFormatterPattern()
		{
			return handlerFormatterPattern;
		}

		/**
		 * Get logger object.
		 */
		public Object getLogger()
		{
			return logger;
		}

		/**
		 * Set logger objcet.
		 */
		public void setLogger(Object logger)
		{
			this.logger = logger;
		}

		/**
		 * Get handler object.
		 */
		public Object getHandler()
		{
			return handler;
		}

		/**
		 * Set handler object.
		 */
		public void setHandler(Object handler)
		{
			this.handler = handler;
		}

		protected void setLevel(int level)
		{
			loggerLevel = level;
		}
	}
}
