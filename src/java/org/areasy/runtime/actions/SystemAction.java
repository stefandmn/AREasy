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
import org.areasy.runtime.engine.RuntimeLogger;
import org.areasy.runtime.engine.RuntimeRunner;
import org.areasy.runtime.engine.RuntimeServer;
import org.areasy.runtime.engine.RuntimeThread;
import org.areasy.runtime.engine.base.AREasyException;
import org.areasy.common.data.NumberUtility;
import org.areasy.common.data.StringUtility;
import org.areasy.common.data.type.credential.Credential;

import java.lang.reflect.Constructor;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.Vector;

/**
 * Runtime system action which is different than a normal action because can have access to the server instance.
 *
 */
public abstract class SystemAction extends AbstractAction implements RuntimeAction
{
	private static Map actions = new Hashtable();

	static
	{
		actions.put("echo", "org.areasy.runtime.actions.system.Echo");
		actions.put("help", "org.areasy.runtime.actions.system.Help");
		actions.put("status", "org.areasy.runtime.actions.system.Status");
		actions.put("config", "org.areasy.runtime.actions.system.Config");
		actions.put("clean", "org.areasy.runtime.actions.system.Clean");
		actions.put("start", "org.areasy.runtime.actions.system.Start");
		actions.put("stop", "org.areasy.runtime.actions.system.Stop");
		actions.put("version", "org.areasy.runtime.actions.system.Version");
		actions.put("process","org.areasy.runtime.actions.system.Process");
		actions.put("install","org.areasy.runtime.actions.system.Install");
	}

	/** Runtime server instance */
	private RuntimeServer server = null;

	/**
	 * Set server instance
	 *
	 * @param server runtime server instance
	 */
	private void setServer(RuntimeServer server)
	{
		this.server = server;
	}

	/**
	 * Get runtime server instance.
	 * 
	 * @return runtime server instance
	 */
	protected RuntimeServer getServer()
	{
		return this.server;
	}

	/**
	 * Check is this action name is a system action
	 * @param action action name (system or normal)
	 * @return true if the specified action name is registered like a system action
	 */
	public static boolean isSystemAction(String action)
	{
		return action!= null && actions.get(action) != null;
	}

	/**
	 * Get an empty system runtime action instance and structure.
	 *
	 * @param server server instance (which can be used by any system action)
	 * @param action action name (registered in the configuration sectors)
	 * @return <code>RuntimeAction</code>
	 * @throws org.areasy.runtime.engine.base.AREasyException runtime action instance and structure
	 */
	public static RuntimeAction getRuntimeAction(RuntimeServer server, String action) throws AREasyException
	{
		SystemAction runtime;

		if(StringUtility.isNotEmpty(action))
		{
			String actionClassName = (String) actions.get(action);
			if(actionClassName == null) throw new AREasyException("System action '" + action  + "' is not registered in the runtime server dictionary");

			try
			{
				Class actionClass = Class.forName(actionClassName);
				Constructor contructor = actionClass.getConstructor(null);

				runtime = (SystemAction) contructor.newInstance(null);

				runtime.setCode(action);
				runtime.setServer(server);
			}
			catch(Throwable th)
			{
				throw new AREasyException("Runtime initialization error for system action '" + action + "'", th);
			}
		}
		else throw new AREasyException("Unknown system runtime action!");

		return runtime;
	}

	/**
	 * This method execute the initialization workflow fr system actions.
	 *
	 * @param createConnection specify if server connection will be created
	 * @throws AREasyException if any error will occur
	 */
	protected void initWorkflow(boolean createConnection) throws AREasyException
	{
		//run secondary initialization (from the final implementation class).
		open();
	}

	/**
	 * Get the list of current active channels.
	 *
	 * @return a list with all runtime channels.
	 */
	protected final List getRuntimeChannels()
	{
		List list = new Vector();

		Thread[] threads = new Thread[RuntimeServer.getChannelsThreadGroup().activeCount()];
		RuntimeServer.getChannelsThreadGroup().enumerate(threads);

		for(int i = 0; i < threads.length; i++)
		{
			if(threads[i] != null && threads[i] instanceof RuntimeThread) list.add(threads[i]);
		}

		return list;
	}

	/**
	 * Get runtime runner (action wrapper) for the current action execution.
	 *
	 * @return <code>RuntimeRunner</code> instance.
	 */
	protected final RuntimeRunner getRuntimeRunner()
	{
		String job = RuntimeLogger.getChannelName();

		boolean found = false;
		RuntimeRunner runner = null;

		List list = getRuntimeChannels();

		for(int i = 0; !found && i < list.size(); i++)
		{
			RuntimeThread thread = (RuntimeThread) list.get(i);
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

		if(!found) return null;
			else return runner;
	}

	/**
	 * Check if the executed action is called from a remote host.
	 *
	 * @return true if the local host is not the same with server host.
	 */
	protected boolean isRemoteAction()
	{
		if(getServer() == null) return false;

		String serverhost = getServer().getManager().getConfiguration().getString("app.server.host", null);
		Socket socket = getRuntimeRunner().getConnection();

		InetSocketAddress local = (InetSocketAddress) socket.getLocalSocketAddress();
		InetSocketAddress remote = (InetSocketAddress) socket.getRemoteSocketAddress();

		return !(StringUtility.equalsIgnoreCase(local.getHostName(), remote.getHostName()) || StringUtility.equalsIgnoreCase(local.getHostName(), serverhost));
	}

	protected final boolean isAdminUserAuthorized()
	{
		if(getConfiguration() != null)
		{
			if(getServer() == null) return false;

			String user = getConfiguration().getString("aruser", null);
			String password = getConfiguration().getString("arpassword", null);

			if(user == null || password == null) throw new RuntimeException("Administrative credentials are not specified");
			password = Credential.getCredential(password).decode();

			String aruser = getServer().getManager().getConfiguration().getString("app.server.default.arsystem.user.name", null);
			String arpassword = getServer().getManager().getConfiguration().getString("app.server.default.arsystem.user.password", null);
			arpassword = Credential.getCredential(arpassword).decode();

			return StringUtility.equals(user, aruser) && StringUtility.equals(arpassword, password);
		}
		else return false;
	}

	protected String getChannelName()
	{
		String channel = getConfiguration().getString("channel", null);

		if(StringUtility.isNotEmpty(channel) && channel.startsWith("areasy-")) return channel;
			else if(StringUtility.isNotEmpty(channel) && NumberUtility.toInt(channel) > 0) return getChannelName(NumberUtility.toInt(channel));
				else return null;
	}

	protected String getChannelName(int channel)
	{
		if(channel > 0) return "areasy-" + channel;
			else return null;
	}

	protected Integer getChannelId(String channel)
	{
		if(channel != null) return NumberUtility.toInt(StringUtility.replace(channel.trim(), "areasy-", ""));
			else return null;
	}
}
