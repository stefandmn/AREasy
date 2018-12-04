package org.areasy.runtime.engine;

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

import org.areasy.common.data.StringUtility;
import org.areasy.common.logger.Logger;
import org.areasy.common.logger.LoggerFactory;
import org.areasy.common.support.configuration.Configuration;
import org.areasy.common.support.configuration.providers.properties.stream.PropertiesConfiguration;
import org.areasy.runtime.RuntimeAction;
import org.areasy.runtime.RuntimeManager;
import org.areasy.runtime.actions.SystemAction;

import java.io.*;
import java.net.Socket;
import java.util.List;

/**
 * This library define the runtime threads which will serve client connections.
 *
 */
public class RuntimeRunner implements Runnable
{
	/** Library logger */
	protected static Logger logger =  LoggerFactory.getLog(RuntimeRunner.class);

	private RuntimeServer server = null;
	private RuntimeBase channel = null;

	private Socket connection = null;
	private Configuration config = null;

	/** Current runtime action. This instance is valid only if the action is under processing */
	private RuntimeAction action = null;

	private boolean persistent = false;
	
	/**
	 * Complete runtime thread constructor.
	 *
	 * @param server runtime server instance
	 * @param channel communication channel.
	 * @param connection client connection.
	 */
	public RuntimeRunner(RuntimeServer server, RuntimeBase channel, Socket connection)
	{
		//server the runtime server inside
		this.server = server;

		//set necessary data
		this.connection = connection;
		this.channel = channel;
		this.channel.setManager(server.getManager());
	}

	/**
	 * Complete runtime thread constructor.
	 *
	 * @param server runtime server instance
	 * @param config action configuration.
	 */
	public RuntimeRunner(RuntimeServer server, Configuration config)
	{
		//server the runtime server inside
		this.server = server;

		//set necessary data
		this.connection = null;
		this.channel = null;

		this.config = config;
	}

	public final void dispose()
	{
		synchronized (this)
		{
			if(this.connection != null && !this.connection.isClosed())
			{
				try
				{
					this.connection.close();
					this.connection = null;
				}
				catch(Exception e) { /* nothing to do here */ }
			}
			else this.connection = null;

			this.channel = null;
		}
	}

	/**
	 * Run action direct on server or launch it from client side.
	 * Actual execution part of this client thread. Also here is processed a special parameter defining the associated thread priority:
	 *
	 * <table border="1">
	 * 	<tr>
	 * 		<td>-priority</td>
	 * 		<td>Runtime thread action priority; the value could be <code>min</code>, <code>max</code> or <code>norm</code> (means normal)</td>
	 * 	</tr>
	 * <tr>
	 * 		<td>-asynchron</td>
	 * 		<td>Run this thread asynchron; that's means that this thread host will be finished without execution and a new client connection will be created</td>
	 * 	</tr>
	 * <tr>
	 * 		<td>-period</td>
	 * 		<td>Run this thread periodically, specifying a period of time for execution. All actions which will run with this parameter
	 * 			will have end of life when the runtime server instance will die</td>
	 * 	</tr>
	 * </table>
	 */
	public void run()
	{
		if(this.connection != null && channel != null) runClient();
			else if(this.connection == null && this.channel == null && config != null) runServer();
				else logger.error("Unknown runner identifier: " + this);
	}

	/**
	 * Run action directly on server
	 */
	public void runServer()
	{
		//normal action processing
		process(config);

		//destroy actual answer.
		RuntimeLogger.destroy();
	}

	/**
	 * Actual execution part of this client thread. Also here is processed a special parameter defining the associated thread priority:
	 *
	 * <table border="1">
	 * 	<tr>
	 * 		<td>-priority</td>
	 * 		<td>Runtime thread action priority; the value could be <code>min</code>, <code>max</code> or <code>norm</code> (means normal)</td>
	 * 	</tr>
	 * <tr>
	 * 		<td>-asynchron</td>
	 * 		<td>Run this thread asynchron; that's means that this thread host will be finished without execution and a new client connection will be created</td>
	 * 	</tr>
	 * <tr>
	 * 		<td>-period</td>
	 * 		<td>Run this thread periodically, specifying a period of time for execution. All actions which will run with this parameter
	 * 			will have end of life when the runtime server instance will die</td>
	 * 	</tr>
	 * </table>
	 */
	public void runClient()
	{
		try
		{
			logger.debug("Connection received from " + connection.getInetAddress().toString());

			//get Input and Output streams
			channel.setOut(new PrintWriter(new OutputStreamWriter(connection.getOutputStream())));
			channel.getOut().flush();

			channel.setIn(new BufferedReader(new InputStreamReader(connection.getInputStream())));

			//The two parts communicate via the input and output streams
			config = new PropertiesConfiguration();
			channel.readClientRequestByServer(config);

			//validate client configuration
			if(!config.isEmpty())
			{
				if(config.containsKey(RuntimeBase.flagSignature) && config.getBoolean(RuntimeBase.flagSignature))
				{
					//handle signature request
					Configuration signature = getRunnerSignature();

					config.merge(signature);
					config.removeKey(RuntimeBase.flagSignature);

					//send server signature answer.
					channel.sendServerAnswerByServer(config);

					//wait and read client request
					channel.readClientRequestByServer(config);
				}

				//handle normal requests
				if(config.containsKey("priority"))
				{
					//check and set thread priority
					String priority = config.getString("priority", "min");
					RuntimeThread thread = server.getHostThread(this);

					if(thread != null)
					{
						int threadPriority = Thread.MIN_PRIORITY;

						if(StringUtility.equalsIgnoreCase(priority, "min")) threadPriority = Thread.MIN_PRIORITY;
							else if(StringUtility.equalsIgnoreCase(priority, "max")) threadPriority = Thread.MAX_PRIORITY;
								else if(StringUtility.equalsIgnoreCase(priority, "norm")) threadPriority = Thread.NORM_PRIORITY;

						thread.setPriority(threadPriority);
						logger.info("Thread priority was changed to '" + priority + "'");
					}
				}

				//check if actual action will run asynchron
				if(config.getBoolean("asynchron", false))
				{
					//remove asynchronous flag
					config.removeKey("asynchron");

					//set the current answer
					RuntimeLogger.info("The action '"+ config.getString("action", null) + "' is running asynchronous!");

					RuntimeRunner runner = new RuntimeRunner(server, config);
					server.process(runner);
				}
				else process(config); //normal action processing

				//send answer.
				channel.sendServerAnswerByServer( config );

				//destroy actual answer.
				RuntimeLogger.destroy();
			}
			else logger.debug("Client runtime sent a null request");
		}
		catch(Throwable th)
		{
			logger.error("Error using socket server: " + th.getMessage());
			logger.debug("Exception", th);
		}
		finally
		{
			//close client connection.
			if(connection != null)
			{
				try
				{
					connection.close();
				}
				catch(IOException e) { /* nothing to do here */ }
			}
		}
	}

	/**
	 * Actual processing method which is called in <code>run</code> method.
	 *
	 * @param config action configuration
	 */
	protected void process(Configuration config)
	{
		try
		{
			String action = config.getString("action", null);

			if(SystemAction.isSystemAction(action)) this.action = SystemAction.getRuntimeAction(server, action);
				else if(this.action == null) this.action = server.getManager().getRuntimeAction(action);

			server.getManager().process(this.action, config);

		}
		catch(Throwable th)
		{
			RuntimeLogger.error("Error processing request: " + th.getMessage());
			logger.debug("Exception", th);
		}
	}

	/**
	 * Get current runtime action instance and structure. In of this the manager class didn't start the processing will return a null value.
	 * So, this method will return only the current action instance which is under processing (action execution is started)
	 *
	 * @return the current runtime action <code>RuntimeAction</code> instance
	 */
	public final RuntimeAction getRunnerAction()
	{
		return this.action;
	}

	/**
	 * Get runtime runner configuration used to start runtime action execution.
	 *
	 * @return <code>Configuration</code> structure
	 */
	public Configuration getConfiguration()
	{
		return this.config;
	}

	/**
	 * Get communication channel for the current runner thread.
	 *
	 * @return <code>RuntimeBase</code> instance that is the communication channel between server and client
	 */
	public RuntimeBase getCommunicationChannel()
	{
		return this.channel;
	}

	/**
	 * Get runtime runner socket connection used to start runtime action execution.
	 *
	 * @return <code>Socket</code> instance
	 */
	public final Socket getConnection()
	{
		return this.connection;
	}

	public Configuration getRunnerSignature()
	{
		Configuration signature = new PropertiesConfiguration();

		List hosts = channel.getManager().getHostsIds();
		List versions = channel.getManager().getRuntimeModules();

		signature.setKey(RuntimeBase.flagRunnerSignatureHostIds, hosts);
		signature.setKey(RuntimeBase.flagRunnerSignatureModules, versions);
		signature.setKey(RuntimeBase.flagRunnerSignatureHomePath, RuntimeManager.getHomeDirectory());
		signature.setKey(RuntimeBase.flagRunnerSignatureLibsPath, RuntimeManager.getLibsDirectory());
		signature.setKey(RuntimeBase.flagRunnerSignatureWorkPath, RuntimeManager.getWorkingDirectory());

		return signature;
	}

	/**
	 * Check if the Runner is persistent or not in the thread that will host this object
	 * @return true if the object is persistent and <code>RuntimeServer.release</code> will not remove it from the thread.
	 */
	public boolean isPersistent()
	{
		return persistent;
	}
	/**
	 * Set the persistence property for the current Runner. This property should be handle from the thread manager
	 */
	public void setPersistent()
	{
		this.persistent = true;
	}
}
