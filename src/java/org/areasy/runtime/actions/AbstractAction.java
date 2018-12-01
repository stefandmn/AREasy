package org.areasy.runtime.actions;

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

import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamReader;
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
	 * Set/load help document for the current action using the configuration of a host/wrapper action instance.
	 *
	 * @param action host action to be used to detect the current help document
	 */
	public final void setHelpObj(RuntimeAction action) throws AREasyException
	{
		if(help == null && action != null && action.isInit())
		{
			help = new HelpDoc(this, action.getConfiguration());
		}
	}

	/**
	 * Get a help document about action syntax, execution process and samples.
	 *
	 * @return text message specifying the syntax of the current action
	 */
	public final HelpDoc getHelpObj()
	{
		return this.help;
	}

	/**
	 * Get a help text about syntax execution of the current action.
	 *
	 * @return text message specifying the syntax of the current action
	 */

	public final String help() throws AREasyException
	{
		if(getHelpObj() != null) return getHelpObj().getPlainTextDocument();
			else throw new AREasyException("Action '" + getCode() + "' is not yet initialized to provide help documentation");
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

	/**
	 * Dedicated class to manage help options for a specific action
	 */
	public final class HelpDocOption
	{
		private String key = null;
		private String type = null;
		private String defvalue = null;
		private String selvalues = null;
		private String description = null;

		/**
		 * Load specific help option from and XML structure
		 *
		 * @param reader <code>XMLStreamReader</code> reader instance that contains data for a help option structure
		 */
		public HelpDocOption(XMLStreamReader reader)
		{
			for(int i = 0; i < reader.getAttributeCount(); i++)
			{
				String data = reader.getAttributeValue(i).trim();

				if(StringUtility.equalsIgnoreCase(reader.getAttributeLocalName(i), "key")) setKey(data);
				else if(StringUtility.equalsIgnoreCase(reader.getAttributeLocalName(i), "type")) setType(data);
				else if(StringUtility.equalsIgnoreCase(reader.getAttributeLocalName(i), "defvalue")) setDefaultValue(data);
				else if(StringUtility.equalsIgnoreCase(reader.getAttributeLocalName(i), "selvalues")) setSelectionValues(data);
				else if(StringUtility.equalsIgnoreCase(reader.getAttributeLocalName(i), "description")) setDescription(data);
			}
		}

		/**
		 * Get option key
		 *
		 * @return option key
		 */
		public String getKey()
		{
			return key;
		}

		/**
		 * Set option key
		 *
		 * @param key option key
		 */
		public void setKey(String key)
		{
			this.key = key;
		}

		/**
		 * Get option data type
		 *
		 * @return data type (number, string, date, bool or select) for an input option
		 */
		public String getType()
		{
			return type;
		}

		/**
		 * Set option data type
		 *
		 * @param type option data type; the possible options are: string, number, bool, data and select (for selection list)
		 */
		public void setType(String type)
		{
			this.type = type;
		}

		/**
		 * Get default value for an option
		 *
		 * @return option default value
		 */
		public String getDefaultValue()
		{
			return defvalue;
		}

		/**
		 * Set default value for for an option
		 *
		 * @param defvalue option default value
		 */
		public void setDefaultValue(String defvalue)
		{
			this.defvalue = defvalue;
		}

		/**
		 * Get list of possible options for an option
		 *
		 * @return list of possible option aggregated into a string delimited by comma
		 */
		public String getSelectionValues()
		{
			return selvalues;
		}

		/**
		 * Set  list of values for and option, aggregated into a string delimited by comma
		 *
		 * @param selvalues list of values delimited by comma
		 */
		public void setSelectionValues(String selvalues)
		{
			this.selvalues = selvalues;
		}

		/**
		 * Get action description
		 *
		 * @return action description
		 */
		public String getDescription()
		{
			return description;
		}

		/**
		 * Set action description
		 *
		 * @param description action description
		 */
		public void setDescription(String description)
		{
			this.description = description;
		}
	}

	/**
	 * Dedicated class to manage help samples for a specific action
	 */
	public final class HelpDocSample
	{
		private String code = null;
		private String description = null;

		/**
		 * Load specific help sample from and XML structure
		 *
		 * @param reader <code>XMLStreamReader</code> reader instance that contains data for a help sample structure
		 */
		public HelpDocSample(XMLStreamReader reader)
		{
			for(int i = 0; i < reader.getAttributeCount(); i++)
			{
				String data = reader.getAttributeValue(i).trim();

				if(StringUtility.equalsIgnoreCase(reader.getAttributeLocalName(i), "code")) setCode(data);
				else if(StringUtility.equalsIgnoreCase(reader.getAttributeLocalName(i), "description")) setDescription(data);
			};
		}

		/**
		 * Get sample code
		 *
		 * @return sample code
		 */
		public String getCode()
		{
			return code;
		}

		/**
		 * Set sample code
		 *
		 * @param code sample code
		 */
		public void setCode(String code)
		{
			this.code = code;
		}

		/**
		 * Get sample description correlated with the specified code
		 *
		 * @return sample code
		 */
		public String getDescription()
		{
			return description;
		}

		/**
		 * Set sample description correlated with tha sample code
		 *
		 * @param description sample description
		 */
		public void setDescription(String description)
		{
			this.description = description;
		}
	}

	/**
	 * This is the action help documentation engine that loads and returns the help documentation
	 * from AREasy libraries
	 */
	public final class HelpDoc
	{
		private String name = null;
		private String syntax = null;
		private String description = null;

		private LinkedHashMap<String,HelpDocOption> options = new LinkedHashMap<String,HelpDocOption>();
		private LinkedHashMap<String,HelpDocOption> options1 = new LinkedHashMap<String,HelpDocOption>();
		private LinkedHashMap<String,HelpDocOption> options2 = new LinkedHashMap<String,HelpDocOption>();

		private Vector<HelpDocSample> samples = new Vector<HelpDocSample>();

		private int level = 0;
		private boolean showOptions = true;
		private boolean showSamples = true;

		private int keyMaxSize = 0;
		private String loadHelpPath = null;

		/**
		 * Initialize help document engine using the current action instance to load the
		 * corresponding help structure.
		 *
		 * @param action runtime action to be used to initialize and to load help document
		 * @throws AREasyException in case any error occurs
		 */
		public HelpDoc(RuntimeAction action) throws AREasyException
		{
			this(action, action.getConfiguration());
		}
		/**
		 * Initialize help document engine using the current action instance and a specific configuration structure,
		 * perhaps from a wrapper/host action. This method has a limited access and usually is used when the associated
		 * action is not yet initialized by the wrapper (or host) action
		 *
		 * @param action runtime action to be used to initialize and to load help document
		 * @param config action configuration to be used to detect the values of help engine parameters.
		 *               This configuration could come from wrapper action in case the current action is not
		 *               yet initialized.
		 * @throws AREasyException in case any error occurs
		 */
		protected HelpDoc(RuntimeAction action, Configuration config) throws AREasyException
		{
			if(config == null && action != null && action.isInit()) config = action.getConfiguration();
			if(action == null || action.getCode() == null) throw new AREasyException("Runtime action is not loaded or initialized");
			if(config == null) throw new AREasyException("Runtime configuration is not loaded");

			//get help level from the current action
			if(config.containsKey("helplevel")) setLevel(config.getInt("helplevel", 0));
			if(config.containsKey("helpoptions")) setShowOptions(config.getBoolean("helpoptions", true));
			if(config.containsKey("helpsamples")) setShowSamples(config.getBoolean("helpsamples", true));

			//detect help path
			setActionHelpDocPath(action);
			InputStream inputStream = getHelpDocStream();

			//read and load help structure
			load(inputStream);
		}

		/**
		 * Get action path of help document (inside of AReasy library)
		 *
		 * @param action associated action to the current help structure
		 * @throws AREasyException in case any error occurs
		 */
		private void setActionHelpDocPath(RuntimeAction action) throws AREasyException
		{
			String packageName = action.getClass().getPackage().getName();
			int indexAction = packageName.indexOf("actions", 0);

			if(indexAction > 0)
			{
				this.loadHelpPath = packageName.substring(indexAction + "actions".length());
			}
			else throw new AREasyException("Help location could not be detected");

			this.loadHelpPath = RuntimeManager.getDocDirectory()+ "/help/"+ this.loadHelpPath.replace('.', '/') + "/" + action.getCode() + ".xml";
		}

		/**
		 * Get input stream instance using the full action help document path.
		 *
		 * @return input stream structure
		 * @throws AREasyException in case any error occurs
		 */
		private InputStream getHelpDocStream() throws AREasyException
		{
			return getHelpDocStream(loadHelpPath);
		}

		/**
		 * Get input stream instance using the full action help document path.
		 *
		 * @param xmlHelpDoc short help document path
		 * @return input stream structure
		 * @throws AREasyException in case any error occurs
		 */
		private InputStream getHelpDocStream(String xmlHelpDoc) throws AREasyException
		{
			if(xmlHelpDoc != null)
			{
				if(!xmlHelpDoc.endsWith(".xml")) xmlHelpDoc += ".xml";

				try
				{
					return new FileInputStream(xmlHelpDoc);
				}
				catch(IOException ioe)
				{
					throw new AREasyException("Error loading Help document: " + ioe.getMessage(), ioe);
				}
			}
			else throw new AREasyException("Help location could not be detected");
		}

		/**
		 * Load action help document from an input stream instance
		 *
		 * @param inputStream input stream from action help document path
		 * @throws AREasyException in case any error occurs
		 */
		private void load(InputStream inputStream) throws AREasyException
		{
			load(inputStream, true);
		}

		/**
		 * Load action help document from an input stream instance with specific restriction in case
		 * inheritance rules are applied
		 *
		 * @param inputStream input stream from action help document path
		 * @param rootDoc specify if the current input corresponds with the root help document
		 * @throws AREasyException in case any error occurs
		 */
		private void load(InputStream inputStream, boolean rootDoc) throws AREasyException
		{
			String inheritancePath = null;
			int optionsLevel = -1;
			boolean textConcat = false;
			String text = null;

			try
			{
				XMLInputFactory factory = XMLInputFactory.newInstance();
				XMLStreamReader reader = factory.createXMLStreamReader(inputStream);

				while (reader.hasNext())
				{
					switch (reader.next())
					{
						case XMLStreamConstants.START_ELEMENT:
							if (StringUtility.equalsIgnoreCase(reader.getLocalName(), "help"))
							{
								if (reader.getAttributeCount() > 0 && StringUtility.equalsIgnoreCase(reader.getAttributeLocalName(0), "inherits"))
								{
									inheritancePath = reader.getAttributeValue(0);
								}
							}
							else if (StringUtility.equalsIgnoreCase(reader.getLocalName(), "option"))
							{
								HelpDocOption option = new HelpDocOption(reader);
								keyMaxSize = Math.max(keyMaxSize, option.getKey().length());

								switch (optionsLevel)
								{
									case 0:
										addOption(option.getKey(), option);
										break;
									case 1:
										addOption1(option.getKey(), option);
										break;
									case 2:
										addOption2(option.getKey(), option);
										break;
								}
							}
							else if (StringUtility.equalsIgnoreCase(reader.getLocalName(), "sample") && rootDoc)
							{
								HelpDocSample sample = new HelpDocSample(reader);
								addSample(sample);
							}
							else if (StringUtility.equalsIgnoreCase(reader.getLocalName(), "options")) optionsLevel = 0;
							else if (StringUtility.equalsIgnoreCase(reader.getLocalName(), "options1")) optionsLevel = 1;
							else if (StringUtility.equalsIgnoreCase(reader.getLocalName(), "options2")) optionsLevel = 2;
							else optionsLevel = -1;
							break;
						case XMLStreamConstants.CHARACTERS:
						{
							if(textConcat) text += reader.getText();
								else text = reader.getText();

							textConcat = true;
							break;
						}
						case XMLStreamConstants.END_ELEMENT:
							if(text == null) continue;
							String data = text.trim();

							if(rootDoc)
							{
								if (StringUtility.equalsIgnoreCase(reader.getLocalName(), "name")) setName(data);
								else if (StringUtility.equalsIgnoreCase(reader.getLocalName(), "syntax")) setSyntax(data);
								else if (StringUtility.equalsIgnoreCase(reader.getLocalName(), "description")) setDescription(data);
							}

							textConcat = false;
							break;
					}
				}

				if(inheritancePath != null)
				{
					InputStream inheritanceInputStream = getHelpDocStream(inheritancePath);
					load(inheritanceInputStream, false);
				}
			}
			catch(Exception e)
			{
				throw new AREasyException("Error loading help document: " + e.getMessage(), e);
			}
		}

		/**
		 * The get name of the help document.
		 *
		 * @return name of the help document
		 */
		public String getName()
		{
			return this.name;
		}

		/**
		 * Set name of the help document
		 *
		 * @param name name of the help document
		 */
		public void setName(String name)
		{
			this.name = name;
		}

		/**
		 * Get help description
		 *
		 * @return help description
		 */
		public String getDescription()
		{
			return this.description;
		}

		/**
		 * Get help syntax
		 *
		 * @return help syntax
		 */
		public String getSyntax()
		{
			return this.syntax;
		}

		/**
		 * Set help syntax
		 *
		 * @param syntax help syntax
		 */
		public void setSyntax(String syntax)
		{
			this.syntax = syntax;
		}

		/**
		 * Set help document
		 *
		 * @param description help document
		 */
		public void setDescription(String description)
		{
			this.description = description;
		}

		/**
		 * Add an option level 0 to the current help document
		 *
		 * @param key option key
		 * @param option option details provided by <code>HelpDocOption</code> structure.
		 */
		public void addOption(String key, HelpDocOption option)
		{
			if(key != null && option != null && StringUtility.equals(key, option.getKey())) this.options.put(key, option);
		}

		/**
		 * Add an option level 1 to the current help document
		 *
		 * @param key option key
		 * @param option option details provided by <code>HelpDocOption</code> structure.
		 */
		public void addOption1(String key, HelpDocOption option)
		{
			if(key != null && option != null && StringUtility.equals(key, option.getKey()))this.options1.put(key, option);
		}

		/**
		 * Add an option level 20 to the current help document
		 *
		 * @param key option key
		 * @param option option details provided by <code>HelpDocOption</code> structure.
		 */
		public void addOption2(String key, HelpDocOption option)
		{
			if(key != null && option != null && StringUtility.equals(key, option.getKey()))this.options2.put(key, option);
		}

		/**
		 * Add sample (example) structure to be current help document
		 * @param sample sample structure provided by <code>HelpDocSample</code> structure
		 */
		public void addSample(HelpDocSample sample)
		{
			this.samples.add(sample);
		}

		/**
		 * Returns the level 0 options map of the current help document.
		 *
		 * @return map structure with level 0 options
		 */
		public Map<String,HelpDocOption> getOptions()
		{
			return this.options;
		}

		/**
		 * Returns the level 1 options map of the current help document.
		 *
		 * @return map structure with level 1 options
		 */
		public Map<String,HelpDocOption> getOptions1()
		{
			return this.options1;
		}

		/**
		 * Returns the level 2 options map of the current help document.
		 *
		 * @return map structure with level 2 options
		 */
		public Map<String,HelpDocOption> getOptions2()
		{
			return this.options2;
		}

		/**
		 * Returns the list of samples of the corresponding help document
		 *
		 * @return samples list
		 */
		public List<HelpDocSample> getSamples()
		{
			return this.samples;
		}

		/**
		 * Generates and returns the help document content in <Code>Markdown</Code> format
		 *
		 * @return help document content in <Code>Markdown</Code> format
		 */
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

			if(!getOptions().isEmpty() && isShowOptions())
			{
				content += "\n\n__OPTIONS:__\n\n";
				content += "| " + StringUtility.rightPad("Parameter", keyMaxSize) + " | " + StringUtility.rightPad("Description", keyMaxSize) + " |\n";
				content += "| " + StringUtility.rightPad("-", keyMaxSize, "-") + " | " + StringUtility.rightPad("-", keyMaxSize, "-") + " |\n";

				content += addOptionsMarkdownContent(getOptions());
				if(getLevel() >= 1 && !getOptions1().isEmpty()) content += addOptionsMarkdownContent(getOptions1());
				if(getLevel() >= 2 && !getOptions2().isEmpty()) content += addOptionsMarkdownContent(getOptions2());
			}

			if(!getSamples().isEmpty() && isShowSamples())
			{
				content += "\n\n__EXAMPLES__\n\n";

				for (HelpDocSample sample : getSamples())
				{
					content += "\t" + sample.getCode() + "\n";
					content += "= " + sample.getDescription() + "\n\n";
				}
			}

			return content;
		}

		/**
		 * Generates and returns the help document content in <Code>HTML</Code> format
		 *
		 * @return help document content in <Code>HTML</Code> format
		 */
		public String getHTMLDocument()
		{
			String content = "<html>\n\t<head>\n";
			if(getName() != null) content += "\t\t<title>" + getName() + "</title>\n";
			content += "\t</head>\n\t<body>\n";

			if(getName() != null) content += "\t\t<h1>" + getName() + "</h1>\n";
			if(getDescription() != null ) content += "\t\t<p>" + getDescription() + "</p>\n";

			if(getSyntax() != null)
			{
				content += "\n\t\t<h3>SYNTAX:</h3>\n";
				content += "\t\t<pre>\n" + "\t" + getSyntax() + "\n\t\t</pre>\n";
			}

			if(!getOptions().isEmpty() && isShowOptions())
			{
				content += "\n\t\t<h3>OPTIONS:</h3>\n";
				content += "\t\t<table border=1>\n\t\t\t<tr>\n\t\t\t\t<th>Parameter</th>\n\t\t\t\t<th>Description</th>\n\t\t\t</tr>\n";

				content += addOptionsHTMLContent(getOptions());
				if(getLevel() >= 1 && !getOptions1().isEmpty()) content += addOptionsHTMLContent(getOptions1());
				if(getLevel() >= 2 && !getOptions2().isEmpty()) content += addOptionsHTMLContent(getOptions2());

				content += "\t\t</table>\n";
			}

			if(!getSamples().isEmpty() && isShowSamples())
			{
				content += "\n\t\t<h3>EXAMPLES:</h3>\n";
				content += "\t\t\t<dl>\n";

				for (HelpDocSample sample : getSamples())
				{
					content += "\t\t\t\t<dt><code>" + sample.getCode() + "</code></dt>\n";
					content += "\t\t\t\t<dd><b>=</b> " + sample.getDescription() + "</dd>\n";
				}

				content += "\t\t\t</dl>\n";
			}

			content += "\t</body>\n<html>";
			return content;
		}

		/**
		 * Generates and returns the help document content in plain text format
		 *
		 * @return help document content in plain text format
		 */
		public String getPlainTextDocument()
		{
			String content = "";

			if(getName() != null) content += getName() + "\n";
			if(getDescription() != null ) content += "\n" + getDescription() + "\n";

			if(getSyntax() != null)
			{
				content += "\n\nSYNTAX:\n\n";
				content += "\t" + getSyntax() + "\n";
			}

			if(!getOptions().isEmpty() && isShowOptions())
			{
				content += "\n\nOPTIONS:\n\n";

				content += addOptionsPlainTextContent(getOptions()) + "\n";
				if(getLevel() >= 1 && !getOptions1().isEmpty()) content += addOptionsPlainTextContent(getOptions1()) + "\n";
				if(getLevel() >= 2 && !getOptions2().isEmpty()) content += addOptionsPlainTextContent(getOptions2());
			}

			if(!getSamples().isEmpty() && isShowSamples())
			{
				content += "\n\nEXAMPLES:\n\n";

				for (HelpDocSample sample : getSamples())
				{
					content += " > " + sample.getCode() + "\n";
					content += "\t= " + sample.getDescription() + "\n\n";
				}
			}

			return content;
		}

		/**
		 * Dedicated method to generate the string content of help options map in <code>Markdown</code> format
		 *
		 * @param options map of help document options
		 * @return options map content in <code>Markdown</code> format
		 */
		private String addOptionsMarkdownContent(Map<String,HelpDocOption> options)
		{
			String content = "";

			if(options != null && !options.isEmpty())
			{
				for (String optKey : options.keySet())
				{
					HelpDocOption option = options.get(optKey);
					content += "| " + StringUtility.rightPad("`" + optKey + "`", keyMaxSize) + " | " + option.getDescription() + " |\n";
				}
			}

			return content;
		}

		/**
		 * Dedicated method to generate the string content of help options map in <code>HTML</code> format
		 *
		 * @param options map of help document options
		 * @return options map content in <code>HTML</code> format
		 */
		private String addOptionsHTMLContent(Map<String,HelpDocOption> options)
		{
			String content = "";

			if(options != null && !options.isEmpty())
			{
				for (String optKey : options.keySet())
				{
					HelpDocOption option = options.get(optKey);

					content += "\t\t\t<tr>\n";
					content += "\t\t\t\t<td><code>" + optKey + "</code></td>\n";
					content += "\t\t\t\t<td>" + option.getDescription() + "</td>\n";
					content += "\t\t\t</tr>\n";
				}
			}

			return content;
		}

		/**
		 * Dedicated method to generate the string content of help options map in plain text format
		 *
		 * @param options map of help document options
		 * @return options map content in plain text format
		 */		private String addOptionsPlainTextContent(Map<String,HelpDocOption> options)
		{
			String content = "";

			if(options != null && !options.isEmpty())
			{
				for (String optKey : options.keySet())
				{
					HelpDocOption option = options.get(optKey);
					content += " -" + StringUtility.rightPad(optKey, keyMaxSize) + " = " + option.getDescription() + "\n";
				}
			}

			return content;
		}

		/**
		 * Get help document level. This parameter typically is read from the action's configuration
		 *
		 * @return help level
		 */
		public int getLevel()
		{
			return level;
		}

		/**
		 * Set help document level
		 *
		 * @param level help document level
		 */
		public void setLevel(int level)
		{
			this.level = level;
		}

		/**
		 * Specified if the options are included in the help document. Also this parameter is typically read from
		 * action's configuration
		 *
		 * @return true is the options are include in the document
		 */
		public boolean isShowOptions()
		{
			return showOptions;
		}

		/**
		 * Set help document engine to include or not in the help printout also the options of the current
		 * help document structure.
		 *
		 * @param showOptions flag to specify if the options are include or not
		 */
		public void setShowOptions(boolean showOptions)
		{
			this.showOptions = showOptions;
		}

		/**
		 * Specified if the samples are included in the help document. Also this parameter is typically read from
		 * action's configuration
		 *
		 * @return true is the samples are include in the document
		 */
		public boolean isShowSamples()
		{
			return showSamples;
		}

		/**
		 * Set help document engine to include or not in the help printout also the samples of the current
		 * help document structure.
		 *
		 * @param showSamples flag to specify if the samples are include or not
		 */
		public void setShowSamples(boolean showSamples)
		{
			this.showSamples = showSamples;
		}
	}
}
