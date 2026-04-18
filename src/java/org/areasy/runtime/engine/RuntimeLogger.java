package org.areasy.runtime.engine;

/*
 *  Copyright (c) 2007-2023 AREasy Runtime
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

import org.areasy.common.data.StringUtility;
import org.areasy.common.logger.Logger;
import org.areasy.common.logger.LoggerFactory;
import org.areasy.common.logger.base.LoggerManager;
import org.areasy.runtime.RuntimeManager;
import org.areasy.runtime.engine.base.AREasyException;

import java.io.*;
import java.util.*;

/**
 * This library define the structure for a processor or action answer.
 */
public class RuntimeLogger
{
	/** Library logger */
	private Logger logger = null;

	private static String level = "info";
    private static String prefix = "answer";
	private static String channel = null;
	private static boolean active = false;

	/** A list with all data values */
	private List data = new Vector();

	/** Flag to identify if the answer will be compacted */
	private boolean compact = false;

	/** validation flags */
	private boolean hasErrors = false;
	private boolean hasWarn = false;

	/** Main repository to store */
	private static Map store = new Hashtable();

	private String lastError = null;

	/** Flag to show if the logger is off */
	private boolean isOff = false;

	/**
	 * Default answer constructor.
	 * Initialize answer structure: logger, flags and data storage
	 */
	private RuntimeLogger()
	{
		//reset flags
		this.hasErrors = false;
		this.hasWarn = false;

		//reset data and channel
		this.data.clear();

		//register this logger definition
		LoggerFactory.getLogManager().addFileLogger(getChannelPath(), LoggerManager.getLoggerLevel(getLevel()),
                getChannelName(), getChannel(), false, LoggerManager.FORMATTER_SIMPLE, "%d %-5p - %m%n");

		//create a new logger channel
		active = true;
		this.logger = LoggerFactory.getLog(getChannelPath());
        LoggerFactory.getLog(RuntimeLogger.class).debug("Initializing new runtime logger: " + getChannelPath());
	}

	/**
	 * Get runtime execution channel
	 * @return the name of the execution channel (thread)
	 */
	public static String getChannelName()
	{
		String identifier = Thread.currentThread().getName();

		if(StringUtility.equalsIgnoreCase(identifier, "main")) return System.getProperty("areasy.signature", Thread.currentThread().getName() + "-" + Thread.currentThread().getId());
			else return identifier;
	}

	private static String getChannelPath()
	{
		return RuntimeLogger.class.getName() +  "." + getChannelName();
	}

	/**
	 * Get current answer object.
	 *
	 * @return the corresponding answer structure for the current thread.
	 */
	private static RuntimeLogger getAnswer()
	{
		Object object = store.get( getChannelName() );

		if(object != null) return (RuntimeLogger)object;
		else
		{
			RuntimeLogger rdata = new RuntimeLogger();
			store.put(getChannelName(), rdata);

			return rdata;
		}
	}

	/**
	 * Reset validation flags and data list.
	 */
	public static void reset()
	{
		//get the current answer
		RuntimeLogger rdata = getAnswer();

		//reset flags
		rdata.hasErrors = false;
		rdata.hasWarn = false;

		//reset data and channel
		rdata.data.clear();

		//reset last found error
		rdata.lastError = null;

		//set logger online
		rdata.isOff = false;
	}

	/**
	 * Clear data list.
	 */
	public static void clearData()
	{
		//get the current answer
		RuntimeLogger rdata = getAnswer();

		//reset data and channel
		rdata.data.clear();
	}

	/**
	 * Dispose this logger channel and destroy it. Data array and details data file are still alive and will
	 * be reseated to the next answer <code>init</code>.
	 */
	public static void destroy()
	{
		//get the current answer
		RuntimeLogger rdata = getAnswer();

		//try to check if the corresponding logger is already defined.
		LoggerManager.removeLogger( getChannelPath() );
		rdata.logger = null;

		//remove answer structure.
		store.remove( getChannelName() );
		rdata = null;
	}

	public static void flush()
	{
		try
		{
			String job = RuntimeLogger.getChannelName();

			boolean found = false;
			RuntimeRunner runner = null;

			if(RuntimeServer.getChannelsThreadGroup() != null)
			{
				Thread[] threads = new Thread[RuntimeServer.getChannelsThreadGroup().activeCount()];
				RuntimeServer.getChannelsThreadGroup().enumerate(threads);

				for(int i = 0; !found && i < threads.length; i++)
				{
					if(threads[i] instanceof RuntimeThread)
					{
						RuntimeThread thread = (RuntimeThread) threads[i];
						Runnable wrapper = thread.getRunner();

						if(wrapper != null && wrapper instanceof RuntimeRunner)
						{
							String threadName = thread.getName();

							if(StringUtility.equalsIgnoreCase(threadName, job))
							{
								found = true;
								runner = ((RuntimeRunner)wrapper);
							}
						}
					}
				}
			}

			if(runner != null)
			{
				RuntimeBase base = runner.getCommunicationChannel();
				base.sendPartialServerAnswerByServer();
			}

			clearData();
		}
		catch(AREasyException are)
		{
			LoggerFactory.getLog(RuntimeLogger.class).error("Error flushing data content: " + are.getMessage());
			LoggerFactory.getLog(RuntimeLogger.class).debug("Exception", are);
		}
	}

	/**
	 * Get answer details.
	 *
	 * @return text message from libraries which will use this answer instance
	 */
	public static String getMessages()
	{
		String content = null;
		File fileIn = new File(RuntimeLogger.channel);

		try
		{
			char allElem[];

			if (fileIn.exists())
			{
				InputStreamReader in = new InputStreamReader(new FileInputStream(fileIn.getAbsolutePath()));
				allElem = new char[(int) fileIn.length()];
				in.read(allElem);

				content = String.valueOf(allElem);
			}

			allElem = null;
		}
		catch (Exception e)
		{
			content = null;
		}

		return content;
	}

	/**
	 * Write a text message which is cumming from another answer.
	 * @param text data content
	 */
	public static void setMessages(String text)
	{
		RuntimeLogger rdata = getAnswer();
		File fileIn = new File(RuntimeLogger.channel);

		try
		{
			OutputStreamWriter out = new OutputStreamWriter(new FileOutputStream(fileIn.getAbsolutePath()));
			out.write(text);
			out.flush();

			if(rdata == null) out.close();
		}
		catch (Exception e)
		{
			LoggerFactory.getLog(RuntimeLogger.class).error("Error writing bundled answer: " + e.getMessage());
			LoggerFactory.getLog(RuntimeLogger.class).debug("Exception", e);
		}
	}

	/**
	 * Add a new data answer.
	 *
	 * @param data data to be added
	 */
	public static void add(String data)
	{
		getAnswer().data.add(data);
	}

	public static boolean active()
	{
		return RuntimeLogger.active;
	}

	/**
	 * Add a list of new data answers.
	 *
	 * @param list list of data to be added
	 */
	public static void add(List list)
	{
		getAnswer().data.addAll(list);
	}

	/**
	 * Add an array of new data answers.
	 *
	 * @param array an array of data to be added
	 */
	public static void add(String[] array)
	{
		getAnswer().data.addAll(Arrays.asList(array));
	}

	/**
	 * Add new debugging message. Will be added only is <code>verbose</code> flag is configured.
	 *
	 * @param message informational message to be added
	 */
	public static void debug(String message)
	{
		if(!getAnswer().isOff && StringUtility.isNotEmpty(message))
		{
			if(getAnswer().logger.isDebugEnabled())
			{
				getAnswer().logger.debug(message);
				RuntimeManager.getLogger().debug(message);

				System.out.println(new Date() + " - DEBUG - " + message);
				System.out.flush();
			}
		}
	}

	/**
	 * Add new debugging message. Will be added only is <code>verbose</code> flag is configured.
	 *
	 * @param message informational message to be added
	 */
	public static void trace(String message)
	{
		if(!getAnswer().isOff && StringUtility.isNotEmpty(message))
		{
			if(getAnswer().logger.isDebugEnabled())
			{
				getAnswer().logger.debug(message);
				RuntimeManager.getLogger().debug(message);

				System.out.println(new Date() + " - TRACE - " + message);
				System.out.flush();
			}
		}
	}

	/**
	 * Add new debugging message. Will be added only is <code>verbose</code> flag is configured.
	 *
	 * @param message informational message to be added
	 */
	public static void debug4error(String message)
	{
		if(!getAnswer().isOff && StringUtility.isNotEmpty(message))
		{
			if(getAnswer().logger.isDebugEnabled())
			{
				getAnswer().logger.error(message);
				RuntimeManager.getLogger().error(message);

				System.out.println(new Date() + " - DEBUG - " + message);
				System.out.flush();
			}
		}
	}


	/**
	 * Add new informational message.
	 *
	 * @param message informational message to be added
	 */
	public static void info(String message)
	{
		if(!getAnswer().isOff && StringUtility.isNotEmpty(message))
		{
			if(getAnswer().logger.isInfoEnabled())
			{
				getAnswer().logger.info(message);
				RuntimeManager.getLogger().info(message);

				System.out.println(new Date() + " - INFO - " + message);
				System.out.flush();
			}
		}
	}

	public static void infoData()
	{
		String data = "Action Data Output:\n\n" + getCompactData();

		if(!getAnswer().isOff && hasData())
		{
			getAnswer().logger.info(data);
		}
	}

	/**
	 * Add new warning message.
	 *
	 * @param message warning message
	 */
	public static void warn(String message)
	{
		if(!getAnswer().isOff && StringUtility.isNotEmpty(message))
		{
			//get the current answer
			RuntimeLogger rdata = getAnswer();

			//mark that here is an warning
			rdata.hasWarn = true;

			if(getAnswer().logger.isWarnEnabled())
			{
				rdata.logger.warn(message);
				RuntimeManager.getLogger().warn(message);

				System.out.println(new Date() + " - WARN - " + message);
				System.out.flush();
			}
		}
	}

	/**
	 * Add new error message.
	 *
	 * @param message error message to be added
	 */
	public static void error(String message)
	{
		if(!getAnswer().isOff && StringUtility.isNotEmpty(message))
		{
			//get the current answer
			RuntimeLogger rdata = getAnswer();

			//mark that here is an warning
			rdata.hasErrors = true;
			rdata.lastError = message;

			if(getAnswer().logger.isErrorEnabled())
			{
				rdata.logger.error(message);
				RuntimeManager.getLogger().error(message);

				System.out.println(new Date() + " - ERROR - " + message);
				System.out.flush();
			}
			else if(getAnswer().logger.isFatalEnabled())
			{
				rdata.logger.fatal(message);
				RuntimeManager.getLogger().fatal(message);

				System.out.println(new Date() + " - FATAL - " + message);
				System.out.flush();
			}
		}
	}

	/**
	 * Get last met error during processing.
	 *
	 * @return last error message
	 */
	public static String getLastError()
	{
		//get the current answer
		RuntimeLogger rdata = getAnswer();

		String error = rdata.lastError;
		if(error != null) rdata.lastError = null;

		return error;
	}

	/**
	 * Reset last met error during processing.
	 */
	public static void resetLastError()
	{
		//get the current answer
		RuntimeLogger rdata = getAnswer();
		rdata.lastError = null;
	}

	/**
	 * Get data string array from this answer instance.
	 *
	 * @return an array of string with all data or null.
	 */
	public static String[] getData()
	{
		//get the current answer
		RuntimeLogger rdata = getAnswer();
		return (String[]) rdata.data.toArray(new String[rdata.data.size()]);
	}

	public static String getCompactData()
	{
		//get the current answer
		return StringUtility.join(getData(), "\n");
	}

	/**
	 * Get data strings into a list format.
	 *
	 * @return a list with data
	 */
	public static List getDataList()
	{
		return getAnswer().data;
	}

	/**
	 * Get data strings into a list format.
	 *
	 * @return a list with data
	 */
	public static boolean hasData()
	{
		return !getAnswer().data.isEmpty();
	}

	/**
	 * Check is the answer it has errors and no warnings.
	 *
	 * @return true if the action has errors and no warnings
	 */
	public static boolean isExact()
	{
		//get the current answer
		RuntimeLogger rdata = getAnswer();

		return !rdata.hasErrors && !rdata.hasWarn;
	}

	/**
	 * Check is the answer it has no error messages.
	 *
	 * @return true if the action has no error messages
	 */
	public static boolean isTruthful()
	{
		return !getAnswer().hasErrors;
	}

	/**
	 * Check is the answer it has error messages.
	 *
	 * @return true if the action has error messages
	 */
	public static boolean hasErrors()
	{
		return getAnswer().hasErrors;
	}

	/**
	 * Check is the answer should be compacted when will be sent to the client. Compact answer means to send only
	 * one parameter and the message will be separated by carriage return character.
	 *
	 * @return true
	 */
	public static boolean isCompact()
	{
		//get the current answer
		return getAnswer().compact;
	}

	/**
	 * Set compact mode for this answer instance.
	 *
	 * @param compact flag to identify if the answer will be compacted
	 */
	public static void setCompact(boolean compact)
	{
		getAnswer().compact = compact;
	}

	/**
	 * Check if the logger channel is off or online.
	 *
	 * @return true if the logger is off.
	 */
	public static boolean isOff()
	{
		return getAnswer().isOff;
	}

	/**
	 * Set logger channel to work in offline mode.
	 */
	public static void Off()
	{
		getAnswer().isOff = true;
	}

	/**
	 * Set logger channel to work in online mode.
	 */
	public static void On()
	{
		getAnswer().isOff = false;
	}

	/**
	 * Check if the answer will store trace, debug, info, error and warning messages.
	 *
	 * @return true if the answer will request logger level.
	 */
	public static boolean isTraceLevel()
	{
		return StringUtility.equalsIgnoreCase(RuntimeLogger.level, "trace");
	}

	/**
	 * Check if the answer will store debug, info, error and warning messages.
	 *
	 * @return true if the answer will request logger level.
	 */
	public static boolean isDebugLevel()
	{
		return StringUtility.equalsIgnoreCase(RuntimeLogger.level, "debug");
	}

	/**
	 * Check if the answer will store only info, error and warning messages.
	 *
	 * @return true if the answer will requeste logger level.
	 */
	public static boolean isInfoLevel()
	{
		return StringUtility.equalsIgnoreCase(RuntimeLogger.level, "info");
	}

	/**
	 * Check if the answer will store only error and warning messages.
	 *
	 * @return true if the answer will requeste logger level.
	 */
	public static boolean isWarnLevel()
	{
		return StringUtility.equalsIgnoreCase(RuntimeLogger.level, "warn");
	}

	/**
	 * Check if the answer will store only error messages.
	 *
	 * @return true if the answer will requeste logger level.
	 */
	public static boolean isErrorLevel()
	{
		return StringUtility.equalsIgnoreCase(RuntimeLogger.level, "error");
	}

	/**
	 * Set prefix name/level for this <code>Answer</code> instance.
	 *
	 * @param prefix flag to say what type of messages will be kept in this answer structure.
	 */
	public static void setPrefix(String prefix)
	{
		if(prefix != null) RuntimeLogger.prefix = prefix;
	}

	/**
	 * Set channel name/level for this <code>Answer</code> instance.
	 *
	 * @param channel flag to say what type of messages will be kept in this answer structure.
	 */
	public static void setChannel(String channel)
	{
		if(channel == null) return;

		try
		{
			File f = new File(channel);

			if(f.isDirectory())
			{
				if(RuntimeLogger.prefix == null) RuntimeLogger.prefix = "answer";
				File ff = new File(f, RuntimeLogger.prefix + "-" + getChannelName() + ".log");
				RuntimeLogger.channel = ff.getAbsolutePath();
			}
			else
			{
				f.getParentFile().mkdirs();
				RuntimeLogger.channel = f.getAbsolutePath();
			}

			LoggerFactory.getLog(RuntimeLogger.class).debug("Setting up the logging channel: " + RuntimeLogger.channel);
		}
		catch(Exception e)
		{
			LoggerFactory.getLog(RuntimeLogger.class).debug("Error setting up the logging channel: "+ e.getMessage());
		}
	}

    public static String getChannel()
    {
        if(RuntimeLogger.channel == null)
		{
			if ((StringUtility.indexOf(RuntimeManager.getReference(), "JAR@") == 0) || (StringUtility.indexOf(RuntimeManager.getReference(), "CFG@") == 0))
			{
				RuntimeLogger.channel = RuntimeManager.getLogsDirectory().getAbsolutePath() + File.separator + RuntimeLogger.prefix + ".log";
			}
			else
			{
				RuntimeLogger.channel = RuntimeManager.getLogsDirectory().getAbsolutePath() + File.separator + RuntimeLogger.prefix + "-" + getChannelName() + ".log";
			}
		}

		return RuntimeLogger.channel;
    }

	/**
	 * Set verbose mode for this <code>Answer</code> instance.
	 *
	 * @param level flag to say what type of messages will be kept in this answer structure.
	 */
	public static void setLevel(String level)
	{
		if(level == null) return;

		if(!StringUtility.equalsIgnoreCase(level, RuntimeLogger.level))
		{
			if(StringUtility.equalsIgnoreCase(level, "off")) getAnswer().isOff = true;
			else
			{
				RuntimeLogger.level = level.toLowerCase();
				getAnswer().isOff = false;

				if (StringUtility.equalsIgnoreCase(level, "debug")) LoggerFactory.getLogManager().changeLevel("debug", getChannelPath());
                else if (StringUtility.equalsIgnoreCase(level, "trace")) LoggerFactory.getLogManager().changeLevel("trace", getChannelPath());
				else if (StringUtility.equalsIgnoreCase(level, "info")) LoggerFactory.getLogManager().changeLevel("info", getChannelPath());
				else if (StringUtility.equalsIgnoreCase(level, "warn")) LoggerFactory.getLogManager().changeLevel("warn", getChannelPath());
				else if (StringUtility.equalsIgnoreCase(level, "error")) LoggerFactory.getLogManager().changeLevel("error", getChannelPath());

				LoggerFactory.getLog(RuntimeLogger.class).debug("Setting up a new logging level: " + getChannelPath() + "->" + RuntimeLogger.level);
			}
		}
	}

	/** Flag to say what is the level of logging */ /**
	 * Get verbose mode for this <code>Answer</code> instance.
	 *
	 * @return the name (or the code) of verbosity mode.
	 */
	public static String getLevel()
	{
		return RuntimeLogger.level;
	}
}
