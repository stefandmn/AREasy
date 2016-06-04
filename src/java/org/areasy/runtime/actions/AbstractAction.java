package org.areasy.runtime.actions;

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

import org.areasy.runtime.RuntimeAction;
import org.areasy.runtime.RuntimeManager;
import org.areasy.runtime.engine.RuntimeLogger;
import org.areasy.runtime.engine.RuntimeServer;
import org.areasy.runtime.engine.base.AREasyException;
import org.areasy.runtime.engine.base.ServerConnection;
import org.areasy.runtime.engine.services.cache.CacheEntry;
import org.areasy.runtime.engine.services.cache.InitialObject;
import org.areasy.runtime.engine.services.status.BaseStatus;
import org.areasy.runtime.engine.structures.CoreItem;
import org.areasy.common.data.BooleanUtility;
import org.areasy.common.data.DateUtility;
import org.areasy.common.data.NumberUtility;
import org.areasy.common.data.StringUtility;
import org.areasy.common.logger.Logger;
import org.areasy.common.logger.LoggerFactory;
import org.areasy.common.support.configuration.Configuration;
import org.areasy.common.velocity.context.Context;
import org.areasy.common.velocity.context.VelocityContext;
import org.w3c.dom.Document;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.*;
import java.util.*;


/**
 * This is an abstract runtime action implementing all common methods and to let for
 * implementation only custom parts and specific actions. Also this library will make final
 * some implementation guide runtime workflow based on some standard implementation
 *
 */
public abstract class AbstractAction implements RuntimeAction
{
	/** Library logger */
	protected static Logger logger = LoggerFactory.getLog(AbstractAction.class);

	/** Action code (registered in <code>RuntimeManager</code> library) */
	private String code = null;

	/** Velocity context */
	private Context context = null;

	/** Action custom parameters */
	private Configuration configuration = null;

	/** ARS Target server connection */
	private ServerConnection target = null;

	/** AREasy Runtime manager */
	private RuntimeManager manager = null;

	/** Flag to see if the action was initialized or not */
	private boolean initialized = false;

	/** Flag to inform action's workers that the action was softly interrupted */
	private boolean interrupted = false;

	/** Help document */
	private HelpDoc help = null;

	/**
	 * Default runtime action constructor.
	 */
	public AbstractAction()
	{
		//nothing to do here
	}

	/**
	 * Set action signature.
	 *
	 * @param signature action code
	 */
	public final void setCode(String signature)
	{
		this.code = signature;
	}

	/**
	 * Initialize action instance and set all basic attributes: action signature (code),
	 * configuration structure, and the inherited runtime manager instance.
	 *
	 * <p>
	 * These parameters are inherited by all implemented actions.
	 * <p>
	 * Also this initialization phase permits to include a custom initiation task which could be
	 * described in <code>init</code> method.
	 *
	 * @param config action parameters.
	 * @param manager runtime manager instance.
	 * @throws org.areasy.runtime.engine.base.AREasyException if any error will occur
	 */
	public final void init(Configuration config, RuntimeManager manager) throws AREasyException
	{
		//translate parameters.
		this.manager = manager;
		this.configuration = config;

		//initialization
		init(true);
	}

	/**
	 * Initialize action instance and set all basic attributes which are inherited from a parent runtime action.
	 *
	 * @param action parent runtime or related action
	 */
	public final void init(RuntimeAction action) throws AREasyException
	{
		this.target = action.getServerConnection();
		this.manager = action.getManager();
		this.configuration = action.getConfiguration();

		//initialization
		init(false);
	}

	/**
	 * Logger instance for the current action
	 *
	 * @return <code>Logger</code> instance
	 */
	protected Logger getLogger()
	{
		return logger;
	}

	/**
	 * Initialize action instance and set all basic attributes: action signature (code), configuration structure, the inherited runtime
	 * manager instance and the AR server connection. This method could be used by a parent action which will use sub-sequences, generating
	 * a data update workflow.
	 *
	 * <p>
	 * Also this initialization phase permits to include a custom initiation task which could be
	 * described in <code>init</code> method.
	 *
	 * @param config action parameters.
	 * @param manager runtime manager instance.
	 * @param target ar server connection structure.
	 * @throws AREasyException if any error will occur
	 */
	public final void init(Configuration config, RuntimeManager manager, ServerConnection target) throws AREasyException
	{
		//translate parameters.
		this.target = target;
		this.manager = manager;
		this.configuration = config;

		//initialization
		init(false);
	}

	/**
	 * Initialize action instance. This method could be rewritten in system runtime actions if that action
	 * don't need to use an AR server connection.
	 *
	 * @param createConnection specify if server connection will be created
	 * @throws org.areasy.runtime.engine.base.AREasyException if any error will occur
	 */
	protected synchronized void init(boolean createConnection) throws AREasyException
	{
		//define answer structure
		RuntimeLogger.setLevel( getConfiguration().getString("loglevel", null) );
		RuntimeLogger.setCompact( getConfiguration().getBoolean("compactmode", RuntimeLogger.isCompact()) );
		getHelpObj().setLevel( getConfiguration().getInt("helplevel", 0) );

		//execute initialization workflow
		initWorkflow(createConnection);

		//mark end of initialization
		this.initialized = true;

		//initialize and load help documentation
		help = new HelpDoc(this);
	}

	/**
	 * This method execute the initialization workflow, and only this method (from initialization process) could be
	 * overwritten by other abstract action (for example system action template). So, this method is called by
	 * actions' initialization procedures.
	 *
	 * @param createConnection specify if server connection will be created
	 * @throws org.areasy.runtime.engine.base.AREasyException if any error will occur
	 */
	protected void initWorkflow(boolean createConnection) throws AREasyException
	{
		if(createConnection)
		{
			//define target server connection
			setServerConnection();
		}

		//run secondary initialization (from the final implementation class).
		open();
	}

	/**
	 * Check if the current action was initialized or not
	 *
	 * @return true if the action is already initialized.
	 */
	public boolean isInit()
	{
		return initialized;
	}

	/**
	 * Get a help document about action syntax, execution process and examples.
	 *
	 * @return text message specifying the syntax of the current action
	 */
	protected final HelpDoc getHelpObj()
	{
		return this.help;
	}

	/**
	 * Get a help text about syntax execution of the current action.
	 *
	 * @return text message specifying the syntax of the current action
	 */

	public final String help()
	{
		return getHelpObj().getPlainTextDocument();
	}

	/**
	 * Close and dispose all objects used by this action.
	 *
	 * <p>
	 * Also this disposing phase permits to include a custom disposal task which could be
	 * described in <code>close</code> method.
	 */
	public synchronized final void dispose()
	{
		//run secondary disposer (from the final implementation class)
		try
		{
			close();
		}
		catch(Throwable th)
		{
			logger.error("Error disposing action: " + th.getMessage());
			if(logger.isDebugEnabled()) logger.debug("Exception", th);
		}

		//dispose server connection.
		if(target != null && !target.isPersistent())
		{
			if(logger.isDebugEnabled()) logger.debug("Disposing server connection for action: " + getCode() + "@" + target);

			if(target.isConnected()) target.disconnect();
			target = null;
		}
		else if(target != null && target.isPersistent()) logger.debug("Server connection will not be disposed because is persistent: " + getCode() + "@" + target);

		//dispose all object from the context (if is instantiated).
		if(context != null)
		{
			if(logger.isDebugEnabled()) logger.debug("Disposing context for action " + getCode());
			context.clear();
			context = null;
		}
	}

	/**
	 * Soft and safe interruption of action action.
	 */
	public final void interrupt()
	{
		interrupted = true;

		try
		{
			Thread.currentThread().interrupt();
		}
		catch(Exception e) { }
	}

	/**
	 * Check if the current action execution was softly interrupted
	 *
	 * @return tru if the action execution was interrupted
	 */
	public final boolean isInterrupted()
	{
		return interrupted;
	}

	/**
	 * This method it is used to execute send a notification with data and log messages. The default implementation
	 * take into consideration all configurations described in <code>areasy</code> configuration sector and <code>notification</code>
	 * parameter which decide if the standard event is executed or not. In the same time all configurations are doubled by parameters.
	 */
	public final void report()
	{
		try
		{
			if(isNotified())
			{
				String body = getReportContent();
				if(StringUtility.isNotEmpty(body)) sendReport(body);
			}
		}
		catch(Throwable th)
		{
			RuntimeLogger.error("Error sending status report notification: " + th.getMessage());
			if(logger.isDebugEnabled()) logger.debug("Exception", th);
		}
	}

	/**
	 * Check if the current action permit notifications
	 *
	 * @return true of false if the action has specified <code>notification</code> flag.
	 */
	protected boolean isNotified() throws AREasyException
	{
		return getConfiguration().getBoolean("notification", getManager().getConfiguration().getBoolean("app.runtime.notification.enable", false));
	}

	/**
	 * Get report content containing data and log message (according with specified log level for the current action).
	 *
	 * @return content for this notification
	 */
	protected String getReportContent() throws AREasyException
	{
		StringBuffer body = new StringBuffer();

		String data[] = RuntimeLogger.getData();
		String logtext = RuntimeLogger.getMessages();
		Configuration config = getConfiguration();

		//write data & objects.
		if(!config.getBoolean("reportnodata", false) && data != null && data.length > 0)
		{
			for(int i = 0; i < data.length; i++)
			{
				if(StringUtility.isNotEmpty(data[i]))
				{
					body.append(data[i]);
					body.append("\n");
				}
				else body.append("\n");
			}
		}

		if(!config.getBoolean("reportnolog", false) && StringUtility.isNotEmpty(logtext))
		{
			if(!config.getBoolean("reportnodata", false) && data != null && data.length > 0)
			{
				body.append("\n");
				body.append("=== AREasy Log Message ===");
				body.append("\n").append("\n");
			}

			body.append(logtext.trim());
		}

		return body.toString();
	}

	/**
	 * Send the notification corresponding with actual action execution.
	 *
	 * @param body notification content
	 * @return notification <code>CoreItem</code> data structure
	 */
	protected CoreItem sendReport(String body) throws AREasyException
	{
		CoreItem notification = null;

		//server connection validation
		if(getServerConnection() == null || !getServerConnection().isConnected())
		{
			logger.warn("Server connection is not performed and the notification could be sent");
			return notification;
		}

		String defaultRecipientTo = getManager().getConfiguration().getString("app.runtime.notification.recipient.to", null);
		String defaultRecipientCc = getManager().getConfiguration().getString("app.runtime.notification.recipient.cc", null);
		String defaultMailboxname = getManager().getConfiguration().getString("app.runtime.notification.mailbox", null);
		String defaultSubject = getManager().getConfiguration().getString("app.runtime.notification.subject", null) + " (" + getServerConnection().getServerName() + ")";

		String mailboxname = getConfiguration().getString("notificationmailbox", defaultMailboxname);
		String recipientto = getConfiguration().getString("notificationrecipientto", null);
		String recipientcc = getConfiguration().getString("notificationrecipientcc", null);
		String subject = getConfiguration().getString("notificationsubject", defaultSubject);
		String action = getConfiguration().getString("notificationaction", "Yes");

		String mailboxkey = (StringUtility.isNotEmpty(mailboxname) ? mailboxname : "default") + "@" + getServerConnection().getServerName();
		CacheEntry cache = (CacheEntry) RuntimeServer.getCache().getCacheEntry(mailboxkey);
		CoreItem mailbox = cache != null ? (CoreItem) cache.getContent() : null;

		//get the mailbox structure
		if(mailbox == null)
		{
			mailbox = getMailbox(mailboxname);

			if(mailbox != null) RuntimeServer.getCache().add(mailboxkey, mailbox);
				else throw new AREasyException("No active outgoing mailbox found in the configuration of 'AR System Email Mailbox Configuration' form");
		}

		//evaluate recipients
		if(StringUtility.isEmpty(recipientto))
		{
			recipientto = defaultRecipientTo;
			if(StringUtility.isEmpty(recipientcc)) recipientcc = defaultRecipientCc;
		}

		//save a new entry in AR server to generate notification event.
		if(StringUtility.isNotEmpty(recipientto) || StringUtility.isNotEmpty(recipientcc))
		{
			notification = new CoreItem();
			notification.setFormName("AR System Email Messages");

			notification.setAttribute(18092, "Outgoing");
			notification.setAttribute(18099, action);
			notification.setAttribute(18086, mailboxname);
			notification.setAttribute(18139, mailbox.getStringAttributeValue(18056));	//from - display name
			notification.setAttribute(18087, mailbox.getStringAttributeValue(18058));	//reply - reply to address
			notification.setAttribute(18103, mailbox.getStringAttributeValue(18059));	//organisation
			notification.setAttribute(18085, recipientto);
			notification.setAttribute(18088, recipientcc);
			notification.setAttribute(18090, subject);
			notification.setAttribute(18091, body);

			notification.create(getServerConnection());
		}
		else logger.warn("Notification could not be sent because recipients are not defines");

		return notification;
	}

	public CoreItem getMailbox(String mailbox) throws AREasyException
	{
		CoreItem mailconfig = new CoreItem();

		mailconfig.setFormName("AR System Email Mailbox Configuration");
		mailconfig.setAttribute(7, new Integer(0));
		mailconfig.setAttribute(18049, new Integer(1));
		
		if(StringUtility.isNotEmpty(mailbox)) mailconfig.setAttribute(18139, mailbox);
			else mailconfig.setAttribute(18147, new Integer(1));

		List list = mailconfig.search(getServerConnection());

		if(list != null && !list.isEmpty()) return (CoreItem) list.get(0);
			else return null;
	}

	/**
	 * This method it is used to execute some particular tasks when the standard execution and custom execution fail and occur an Exception.
	 * So, the exception occurs during execution should handled here. This method is called only if the action was initialized.
	 *
	 * @param th throwable execution occurs when standard execution of custom execution was done.
	 */
	public void throwable(Throwable th)
	{
		RuntimeLogger.error("Error running action '" + getCode() + "': " + (th.getMessage() != null ? th.getMessage() : th.getCause() != null ? th.getCause().getMessage() : th.toString()));
	}

	/**
	 * Run secondary initialization (from the final implementation class)
	 *
	 * @throws AREasyException if any error will occur.
	 */
	public void open() throws AREasyException
	{
		//nothing to do here
	}

	/**
	 * Set initial data for configuration or for connectivity. This method has the same purpose like <code>open</code> method
	 * but is executed before to initiate server connection
	 *
	 * @throws AREasyException if any error will occur.
	 */
	protected void initConnectionData() throws AREasyException
	{
		//nothing to do here
	}

	/**
	 * Run secondary disposer (from the final implementation class)
	 */
	public void close() throws AREasyException
	{
		//nothing to do here
	}

	/**
	 * Get action code
	 *
	 * @return the runtime action code (identifier)
	 */
	public final String getCode()
	{
		return this.code;
	}

	/**
	 * Get status notifier instance. In order to deliver a status notifier (for actions which the execution will take much more time)
	 * this method must be rewrote
	 *
	 * @return <code>BaseStatus</code> structure and in the current implementation is returning a null value. Notifier thread will be
	 * activated only if this method is returning a not null answer. 
	 */
	public BaseStatus getCurrentStatus()
	{
		return null;
	}

	/**
	 * Get action parameters map.
	 *
	 * @return action configuration structure.
	 */
	public final Configuration getConfiguration()
	{
		return configuration;
	}

	/**
	 * Set action parameters map.
	 *
	 * @param configuration configuration structure.
	 */
	public final void setConfiguration(Configuration configuration)
	{
		this.configuration = configuration;
	}

	/**
	 * Get runtime manager instance
	 * @return runtime manager instance defined after action's initialization
	 */
	public final RuntimeManager getManager()
	{
		return manager;
	}

	/**
	 * Get ARS server connection structure and context.
	 * @return <code>ServerConnection</code> instance or null.
	 */
	public final ServerConnection getServerConnection()
	{
		return this.target;
	}

	/**
	 * Get an input parameter (from the action configuration structure) in string format.
	 * This method is capable to validate if the parameter exist but hasn't a specified value
	 * (and in this situation the parser will consider that this paremetr is a boolean parameter)
	 *
	 * @param name name of the input parameter
	 * @return found string value for the specified input parameter
	 */
	protected String getStringInputParameter(String name)
	{
		return getStringInputParameter(name, null);
	}

	/**
	 * Get an input parameter (from the action configuration structure) in string format.
	 * This method is capable to validate if the parameter exist but hasn't a specified value
	 * (and in this situation the parser will consider that this paremetr is a boolean parameter)
	 *
	 * @param name name of the input parameter
	 * @param defaultValue default value in case of the discovered value is null
	 * @return found string value for the specified input parameter
	 */
	protected String getStringInputParameter(String name, String defaultValue)
	{
		if(name == null) return null;

		String value = getConfiguration().getString(name, null);
		boolean valueBool = BooleanUtility.toBoolean(value);

		if(StringUtility.isEmpty(value)) return defaultValue;
			else if(valueBool) return defaultValue;
				return value;
	}

	/**
	 * Create AR server connection instance based on specified parameters (for more details
	 * about these parameter please see method <code>initialize</code>)
	 * 
	 * @throws AREasyException if any error will occur.
	 */
	protected final void setServerConnection() throws AREasyException
	{
		//check server connection.
		if(target == null || !target.isConnected())
		{
			//init connection data if is necessary
			initConnectionData();

			//get direct server name
			String arserver = getConfiguration().getString("arserver", getManager().getConfiguration().getString("app.server.default.arsystem.server.name", "localhost"));

			if(StringUtility.isNotEmpty(arserver))
			{
				boolean arpasswordbool = false;
				String impuser = null;

				String aruser = getConfiguration().getString("aruser", null);
				String arpassword = getConfiguration().getString("arpassword", null);

				//validate password value and see if it is about user impersonation
				if(arpassword != null && StringUtility.equalsIgnoreCase(arpassword, "true"))
				{
					arpasswordbool = BooleanUtility.toBoolean(arpassword);
					arpassword = "";
				}

				String armode = getConfiguration().getString("armode", getManager().getConfiguration().getString("app.server.default.arsystem.mode", "base"));
				int arport = getConfiguration().getInt("arport", getManager().getConfiguration().getInt("app.server.default.arsystem.port.number", 0));
				int arrpc = getConfiguration().getInt("arrpc", getManager().getConfiguration().getInt("app.server.default.arsystem.rpc.queue", 0));

				//check impersonation features.
				if(StringUtility.isEmpty(arpassword) && !arpasswordbool)
				{
					impuser = aruser;

					aruser = getManager().getConfiguration().getString("app.server.default.arsystem.user.name", null);
					arpassword = getManager().getConfiguration().getString("app.server.default.arsystem.user.password", null);
				}

				//if is not an impersonated connection check if the connection could be taken from cache
				if(impuser == null)
				{
					//check if the target connection could be read from persistent layer
					if(!getConfiguration().containsKey("arserver")) target = (ServerConnection) InitialObject.getObjectInstance(ServerConnection.class);

					if(target != null)
					{
						if(!target.isConnected()) target.prepare();
						logger.debug("Server connection taken from persistent layer: " + target);
					}
					else
					{
						target = new ServerConnection();
						target.connect(arserver, aruser, arpassword, arport, arrpc, armode);
					}
				}
				else
				{
					target = new ServerConnection();
					target.connect(arserver, aruser, arpassword, arport, arrpc, armode, impuser);
				}
			}
			else logger.warn("AR server connection couldn't be created because '-arserver' option is null");
		}
	}

	/**
	 * Create Velocity context. The context will include the following objects: <br/>
	 * <b>$action</b> = action class (<i>this</i> java operator) </br>
	 * <b>dates</br> = org.areasy.common.data.DateUtility instance </br>
	 * <b>strings</b> = org.areasy.common.data.StringUtility instance </br>
	 * <b>numbers</b> = org.areasy.common.data.NumberUtility instance </br>
	 * <b>config</b> = org.areasy.common.structure.configuration.Configuration instance that consists action
	 * configuration (all parameters set to the execution runtime)
	 */
	protected void initContext()
	{
		//create context.
		this.context = new VelocityContext();

		//put in context this library
		this.context.put("action", this);

		//put in context DateUtility library
		this.context.put("dates", new DateUtility());

		//put in context StringUtility library
		this.context.put("strings", new StringUtility());

		//put in context NumberUtility library
		this.context.put("numbers", new NumberUtility());

		//put in context configuration structure
		this.context.put("config", this.configuration);
	}

	/**
	 * Get Velocity engine context to parse different expressions. If it is not initialized will instantiated here
	 * and also will fill all additional objects.
	 *
	 * @return velocity context
	 */
	public final Context getContext()
	{
		if(this.context == null) initContext();

		return this.context;
	}

	public final void resetContext()
	{
		if(this.context != null) this.context.clear();
	}

	public final void setNullContext()
	{
		this.context = null;
	}

	/**
	 * Get a map from a list of keys and a list with values.
	 *
	 * @param keys list of keys.
	 * @param values list of values
	 * @return a map between keys and values.
	 */
	protected Map getMap(List keys, List values)
	{
		Map map = new Hashtable();

		for(int i = 0; keys != null && i < keys.size(); i++)
		{
			String mapobj = (String) keys.get(i);
			String mapvalue = "";

			if(values != null && values.size() > i) mapvalue = (String) values.get(i);

			map.put(mapobj, mapvalue);
		}

		return map;
	}

	/**
	 * Get the action's map for CI keys (field ids and values which will be used to search CIs)
	 *
	 * @return a map with field ids and values.
	 */
	protected Map getKeyMap()
	{
		return getKeyMap(getConfiguration());
	}

	/**
	 * Get the action's map for CI keys (field ids and values which will be used to search CIs)
	 *
	 * @param config configuration structure
	 * @return a map with field ids and values.
	 */
	protected Map getKeyMap(Configuration config)
	{
		String keyid = config.getString("keyid", null);
		String keyvalue = config.getString("keyvalue", null);
		List keyids = config.getVector("keyids", new Vector());
		List keyvalues = config.getVector("keyvalues", new Vector());

		if(StringUtility.isNotEmpty(keyid) && !keyids.contains(keyid))
		{
			keyids.add(keyid);

			if(StringUtility.isNotEmpty(keyvalue)) keyvalues.add(keyvalue);
				else keyvalues.add("");
		}

		return getMap(keyids, keyvalues);
	}

	/**
	 * Get the action's map for CI data (field ids and values which will be used to fill CIs with data)
	 *
	 * @return a map with field ids and values.
	 */
	protected Map getDataMap()
	{
		List datamapids = getConfiguration().getVector("dataids", new Vector());
		List datamapvalues = getConfiguration().getVector("datavalues", new Vector());

		Map map = getMap(datamapids, datamapvalues);

		return map;
	}

	/**
	 * Get maximum memory used by the current virtual machine instance.
	 * @return heap length in megabytes.
	 */
	public long getMaxMemory()
	{
		 return Runtime.getRuntime().maxMemory()/(1024 * 1024);
	}

	/**
	 * Get maximum memory used by the current virtual machine instance.
	 * @return heap length in megabytes.
	 */
	public long getUsedMemory()
	{
		long total = Runtime.getRuntime().totalMemory()/(1024 * 1024);
		return total - Runtime.getRuntime().freeMemory()/(1024 * 1024);
	}

	/**
	 * eturns a string representation of the object. In general, the
     * <code>toString</code> method returns a string that
     * "textually represents" this object.
	 * 
	 * @return  a string representation of the object.
	 */
	public String toString()
	{
		return getCode() != null ? getCode() + "@" + getClass() : getClass().getName();
	}

	/**
	 * Copy resources from a source to a destination.
	 * <p/>
	 * If destination doesn't exists will be created. <br/>
	 * If destination is directory will be appeded source file name and will be created.
	 *
	 * @param source source file path
	 * @param destination destination file path
	 * @return the destination location
	 * @throws IOException if any I/O error will occur
	 */
	public static String copy(String source, String destination) throws IOException
	{
		byte[] buffer = new byte[4096];
		BufferedInputStream input;
		BufferedOutputStream output;

		File destloc = new File(destination);

		//check if destination is directory.
		if (destloc.exists() && destloc.isDirectory())
		{
			destination += File.separator + new File(source).getName();
			destloc = new File(destination);
		}

		//check if destination location doesn't exists.
		if (!destloc.getParentFile().exists()) destloc.getParentFile().mkdirs();

		//check if destination file exists.
		if (!destloc.exists()) destloc.createNewFile();

		input = new BufferedInputStream(new FileInputStream(source));
		output = new BufferedOutputStream(new FileOutputStream(destination));

		copyStream(input, output, buffer);

		input.close();
		output.close();

		return destination;
	}

	/**
	 * Generic copy from a input stream to an output stream.
	 *
	 * @param input  The source input stream.
	 * @param output The destination output stream.
	 * @param buffer The user provided buffer.
	 * @throws java.io.IOException When an IO error occurs, this exception is thrown.
	 */
	public static void copyStream(InputStream input, OutputStream output, byte[] buffer) throws IOException
	{
		int bytesRead;

		while ((bytesRead = input.read(buffer)) != -1)
		{
			output.write(buffer, 0, bytesRead);
		}
	}

	/**
	 * Write (create or rewrite) a text in a file.
	 * 
	 * @param fileOut output file location
	 * @param text file content
	 * @return true if operation succeeded
	 */
	public static boolean writeTextFile(File fileOut, String text)
	{
		if(fileOut == null) return false;

		try
		{
			if(fileOut.exists()) fileOut.createNewFile();
			
			OutputStreamWriter out = new OutputStreamWriter(new FileOutputStream(fileOut.getAbsolutePath()), "UTF-8");
			out.write(text);
			out.close();

			return true;
		}
		catch (Exception e)
		{
			return false;
		}
	}

	/**
	 * Write (create or rewrite) a text in a file.
	 *
	 * @param URI file location
	 * @param text file content
	 * @return true if operation succeded
	 */
	public static boolean writeTextFile(String URI, String text)
	{
		return writeTextFile(new File(URI), text);
	}

	/**
	 * Get string content from file.
	 *
	 * @param URI - file location.
	 * @return file content
	 */
	public static String readTextFile(String URI)
	{
		return readTextFile(new File(URI));
	}

	/**
	 * Get string content from file.
	 *
	 * @param fileIn - input file location.
	 * @return file content
	 */
	public static String readTextFile(File fileIn)
	{
		String content = null;
		if(fileIn == null) return content;

		try
		{
			char allElem[];
			if (fileIn.exists())
			{
				InputStreamReader in = new InputStreamReader(new FileInputStream(fileIn.getAbsolutePath()), "UTF-8");
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

	public ServerConnection getRemoteServerConnection() throws AREasyException
	{
		ServerConnection connection = null;

		if(getConfiguration().getString("arremoteserver", null) != null && !StringUtility.equalsIgnoreCase(getConfiguration().getString("arremoteserver", null), getServerConnection().getServerName()))
		{
			String arserver = getConfiguration().getString("arremoteserver");
			String aruser = getConfiguration().getString("arremoteuser", getServerConnection().getUserName());
			String arpassword = getConfiguration().getString("arremotepassword", getServerConnection().getUserPassword());
			int arport = getConfiguration().getInt("arremoteport", getServerConnection().getServerPort());
			int arrpc = getConfiguration().getInt("arremoterpc", getServerConnection().getRpcQueue());
			String armode = getConfiguration().getString("arremotemode", getServerConnection().getMode());

			connection = new ServerConnection();
			connection.connect(arserver, aruser, arpassword, arport, arrpc, armode);

			if(logger.isDebugEnabled()) logger.debug("Connected to remote server: " + connection);
		}
		else connection = getServerConnection();

		return connection;
	}

	protected ServerConnection[] getRemoteServerConnections() throws AREasyException
	{
		return getRemoteServerConnections(false);
	}

	protected ServerConnection[] getRemoteServerConnections(boolean handlingerrors) throws AREasyException
	{
		List arservers = getConfiguration().getVector("arremoteserver", null);
		List list = new Vector();

		if(arservers != null && !arservers.isEmpty())
		{
			List arusers = getConfiguration().getVector("arremoteuser", null);
			List arpasswords = getConfiguration().getVector("arremotepassword", null);
			List arports = getConfiguration().getVector("arremoteport", null);
			List arrpcs = getConfiguration().getVector("arremoterpc", null);
			List armodes = getConfiguration().getVector("arremotemode", null);

			for(int i = 0; i < arservers.size(); i++)
			{
				String arserver = (String) arservers.get(i);

				String aruser = getServerConnection().getUserName();
				if(arusers != null && arusers.size() > i) aruser = (String) arusers.get(i);

				String arpassword = getServerConnection().getUserPassword();
				if(arpasswords != null && arpasswords.size() > i) arpassword = (String) arpasswords.get(i);

				int arport = getServerConnection().getServerPort();
				if(arports != null && arports.size() > i) arport = NumberUtility.toInt((String)arports.get(i), 0);

				int arrpc = getServerConnection().getRpcQueue();
				if(arrpcs != null && arrpcs.size() > i) arrpc = NumberUtility.toInt((String)arrpcs.get(i), 0);

				String armode = getServerConnection().getMode();
				if(armode != null && armodes.size() > i) armode = (String) armodes.get(i);

				ServerConnection connection = new ServerConnection();

				try
				{
					connection.connect(arserver, aruser, arpassword, arport, arrpc, armode);
					list.add(connection);
				}
				catch(AREasyException are)
				{
					if(handlingerrors) logger.warn("Error performing server connection: " + connection + ": " + are.getMessage());
						else throw are;
				}
			}
		}
		else list.add(getServerConnection());

		return (ServerConnection[]) list.toArray(new ServerConnection[list.size()]);
	}

	public void disconnectRemoteServerConnection(ServerConnection connection)
	{
		//disconnected second connection
		if(connection != null && !getServerConnection().equals(connection)) connection.disconnect();
	}

	public void disconnectRemoteServerConnections(ServerConnection connections[])
	{
		//disconnected additional connection
		for(int i = 0; connections != null && i < connections.length; i++) disconnectRemoteServerConnection(connections[i]);
	}

	protected String getTargetServerNameOrAlias()
	{
		String targetServerName = getConfiguration().getString("arserveralias", null);

		if(targetServerName == null)
		{
			List list = getConfiguration().getVector("arremoteserver", null);

			if(list != null && !list.isEmpty()) targetServerName = (String) list.get(0);
				else targetServerName = getConfiguration().getString("arserver", getConfiguration().getString("arserver", getManager().getConfiguration().getString("app.server.default.arsystem.server.name", "localhost")));

		}

		return targetServerName;
	}

	public final class HelpDocOption
	{
		private String key = null;
		private String type = null;
		private String defvalue = null;
		private String selvalues = null;
		private String description = null;

		public HelpDocOption(String key, String type)
		{
			this.setKey(key);
			this.setType(type);
		}

		public HelpDocOption(String key, String type, String defvalue, String selvalues, String description)
		{
			this.setKey(key);
			this.setType(type);
			if(defvalue != null) this.setDefvalue(defvalue);
			if(description != null) this.setDescription(description);
			if(StringUtility.equalsIgnoreCase(type, "select")) this.setSelvalues(selvalues);
		}

		public String getKey()
		{
			return key;
		}

		public void setKey(String key)
		{
			this.key = key;
		}

		public String getType()
		{
			return type;
		}

		public void setType(String type)
		{
			this.type = type;
		}

		public String getDefvalue()
		{
			return defvalue;
		}

		public void setDefvalue(String defvalue)
		{
			this.defvalue = defvalue;
		}

		public String getSelvalues()
		{
			return selvalues;
		}

		public void setSelvalues(String selvalues)
		{
			this.selvalues = selvalues;
		}

		public String getDescription()
		{
			return description;
		}

		public void setDescription(String description)
		{
			this.description = description;
		}
	}
	public final class HelpDoc
	{
		private String name = null;
		private String description = null;

		private String syntax = null;
		private HashMap options = new HashMap();
		private HashMap optionsC1 = new HashMap();
		private HashMap optionsC2 = new HashMap();
		private HashMap optionsE1 = new HashMap();
		private HashMap optionsE2 = new HashMap();
		private HashMap examples = new HashMap();

		private int level = 0;

		public HelpDoc(RuntimeAction action) throws AREasyException
		{
			if(action == null || action.getCode() == null) throw new AREasyException("Runtime action is not loaded or initialized");

			String pathLocation = getActionHelpDocPath(action);
			InputStream inputStream = getHelpDocStream(pathLocation);

			try
			{
				DocumentBuilderFactory builderFactory = DocumentBuilderFactory.newInstance();
				DocumentBuilder builder = builderFactory.newDocumentBuilder();
				Document xmlDocument = builder.parse(inputStream);

			}
			catch(Exception e)
			{

			}
		}

		private void load(InputStream inputStream)
		{
			load(inputStream, false);
		}

		private void load(InputStream inputStream, Boolean inheritance)
		{
			try
			{
				DocumentBuilderFactory builderFactory = DocumentBuilderFactory.newInstance();
				DocumentBuilder builder = builderFactory.newDocumentBuilder();
				Document xmlDocument = builder.parse(inputStream);

			}
			catch(Exception e)
			{

			}
		}

		private String getActionHelpDocPath(RuntimeAction action) throws AREasyException
		{
			String pathLocation = null;

			String packageName = action.getClass().getPackage().getName();
			int indexAction = packageName.indexOf("actions", 0);

			if(indexAction > 0)
			{
				pathLocation = packageName.substring(indexAction + "actions".length() + 1);
			}
			else
			{
				indexAction = packageName.indexOf("areasy", 0);
				if(indexAction > 0) pathLocation = packageName.substring(indexAction + "areasy".length() + 1);
					else throw new AREasyException("Help location could not be found");
			}

			return pathLocation + "." + action.getCode() + ".xml";
		}

		private InputStream getHelpDocStream(String xmlHelpDoc)
		{
			String pathLocation = "org.areasy.runtime.utilities.resources.help." + xmlHelpDoc;
			if(xmlHelpDoc.endsWith(".xml")) pathLocation += ".xml";

			pathLocation =  pathLocation.substring(0, pathLocation.indexOf(".xml")).replace('.', '/') + pathLocation.substring(pathLocation.indexOf(".xml"));
			return AbstractAction.class.getClassLoader().getResourceAsStream(pathLocation);
		}

		public String getName()
		{
			return this.name;
		}

		public void setName(String name)
		{
			this.name = name;
		}

		public String getDescription()
		{
			return this.description;
		}

		public String getSyntax()
		{
			return this.syntax;
		}

		public void setSyntax(String syntax)
		{
			this.syntax = syntax;
		}

		public void setDescription(String description)
		{
			this.description = description;
		}

		public void addOption(String option, String details)
		{
			this.options.put(option, details);
		}

		public void addOptionC1(String option, String details)
		{
			this.optionsC1.put(option, details);
		}

		public void addOptionC2(String option, String details)
		{
			this.optionsC2.put(option, details);
		}

		public void addOptionE1(String option, String details)
		{
			this.optionsE1.put(option, details);
		}

		public void addOptionE2(String option, String details)
		{
			this.optionsE2.put(option, details);
		}

		public void addExample(String example, String documentation)
		{
			this.examples.put(example, documentation);
		}

		public String getOptionDetails(String option)
		{
			return (String) this.options.get(option);
		}

		public Map getOptions()
		{
			return this.options;
		}

		public Map getOptionsC1()
		{
			return this.optionsC1;
		}

		public Map getOptionsC2()
		{
			return this.optionsC2;
		}

		public Map getOptionsE1()
		{
			return this.optionsE1;
		}

		public Map getOptionsE2()
		{
			return this.optionsE2;
		}

		public Map getExamples()
		{
			return this.examples;
		}

		public String getMarkdownDocument()
		{
			String content = "";

			if(getName() != null) content += "# " + getName() + "\n";
			if(getDescription() != null ) content += "\n" + getDescription() + "\n";

			if(getSyntax() != null)
			{
				content += "\n\n__SYNTAX:__\n\n";
				content += "\t" + getSyntax() + "\n";
			}

			if(getOptions().isEmpty())
			{
				content += "\n\n__OPTIONS:__\n\n";
				content += "| Parameter | Description |\n";
				content += "| --------- | ----------- |\n";

				content += addOptionsMarkdownContent(getOptions());

				if(getLevel() <= 1 && !getOptionsC1().isEmpty()) content += addOptionsMarkdownContent(getOptionsC1());
				if(getLevel() <= 1 && !getOptionsE1().isEmpty()) content += addOptionsMarkdownContent(getOptionsE1());
				if(getLevel() <= 2 && !getOptionsC2().isEmpty()) content += addOptionsMarkdownContent(getOptionsC2());
				if(getLevel() <= 2 && !getOptionsE2().isEmpty()) content += addOptionsMarkdownContent(getOptionsE2());
			}

			if(!getExamples().isEmpty())
			{
				content += "\n\n__EXAMPLES__\n\n";

				for (Object exObj : getExamples().keySet())
				{
					String exampleCommand = (String) exObj;
					String exampleDocumentation = getOptionDetails(exampleCommand);

					content += "\t" + exampleCommand + "\n";
					content += "= " + exampleDocumentation + "\n\n";
				}
			}

			return content;
		}

		public String getPlainTextDocument()
		{
			String content = "";

			if(getName() != null) content += getName() + "\n";
			if(getDescription() != null ) content += "\n" + getDescription() + "\n";

			if(getSyntax() != null)
			{
				content += "\n\nSYNTAX:\n\n";
				content += getSyntax() + "\n";
			}

			if(!getOptions().isEmpty())
			{
				content += "\n\nOPTIONS:\n\n";

				content += addOptionsPlainTextContent(getOptions());

				if(getLevel() <= 1 && !getOptionsC1().isEmpty()) content += addOptionsPlainTextContent(getOptionsC1());
				if(getLevel() <= 1 && !getOptionsE1().isEmpty()) content += addOptionsPlainTextContent(getOptionsE1());
				if(getLevel() <= 2 && !getOptionsC2().isEmpty()) content += addOptionsPlainTextContent(getOptionsC2());
				if(getLevel() <= 2 && !getOptionsE2().isEmpty()) content += addOptionsPlainTextContent(getOptionsE2());
			}

			if(!getExamples().isEmpty())
			{
				content += "\n\nEXAMPLES:\n\n";

				for (Object exObj : getExamples().keySet())
				{
					String exampleCommand = (String) exObj;
					String exampleDocumentation = getOptionDetails(exampleCommand);

					content += " > " + exampleCommand + "\n";
					content += " = " + exampleDocumentation + "\n\n";
				}
			}

			return content;
		}

		private String addOptionsMarkdownContent(Map options)
		{
			String content = "";

			if(options != null && !options.isEmpty())
			{
				for (Object optObj : options.keySet())
				{
					String optionParameter = (String) optObj;
					String optionDetails = getOptionDetails(optionParameter);

					content += "| " + optionParameter + " | " + optionDetails + " |\n";
				}
			}

			return content;
		}

		private String addOptionsPlainTextContent(Map options)
		{
			String content = "";

			if(options != null && !options.isEmpty())
			{
				for (Object optObj : getOptionsC1().keySet())
				{
					String optionParameter = (String) optObj;
					String optionDetails = getOptionDetails(optionParameter);

					content += "-" + optionParameter + "\n\t= " + optionDetails + "\n";
				}
			}

			return content;
		}

		public int getLevel()
		{
			return level;
		}

		public void setLevel(int level)
		{
			this.level = level;
		}
	}
}
