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
import org.areasy.runtime.RuntimeManager;
import org.areasy.runtime.engine.base.AREasyException;

import java.io.*;
import java.net.Socket;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;

/**
 * Runtime built-in client module.
 */
public class RuntimeClient extends RuntimeBase
{
	/** Library logger */
	protected static Logger logger = LoggerFactory.getLog(RuntimeClient.class);

	private Socket client = null;

	public RuntimeClient(RuntimeManager manager)
	{
		setManager(manager);
	}

	public void run(Configuration config)
	{
		logger.info("Running AREasy Runtime Client - '" + config.getString("action", null) + "' action.");

		try
		{
			//validate client configuration structure.
			if(config == null) throw new AREasyException("Client configuration is null");

			//check if the client made a normal request or he asked to start a local runtime server
			if(StringUtility.equalsIgnoreCase(config.getString("action", null), "start") || StringUtility.equalsIgnoreCase(config.getString("action", null), "startup"))
			{
				//execute client action
				getManager().process(config);
			}
			else
			{
				//get connectivity data
				String host = config.getString("host", getManager().getConfiguration().getString("app.server.host", "127.0.0.1"));
				int port = config.getInt("port", getManager().getConfiguration().getInt("app.server.port", 6506));

				//if any kind of issue appear try to fix connectivity coordinates
				if(StringUtility.equalsIgnoreCase(host, "true")) host = getManager().getConfiguration().getString("app.server.host", "127.0.0.1");

				//remove unnecessary parameters
				config.removeKey("host");
				config.removeKey("port");

				//1. creating a socket to connect to the server
				logger.debug("Initializing client connection to " + host + ":" + port);
				client = new Socket(host, port);

				//2. get Input and Output streams
				setOut(new PrintWriter(new OutputStreamWriter(client.getOutputStream())));
				getOut().flush();

				//3: Communicating with the server
				setIn(new BufferedReader(new InputStreamReader(client.getInputStream())));

				//4: Send and receive signature
				Configuration signature = getClientSignature();
				sendClientRequestByClient(signature);
				readServerAnswerByClient(signature);

				//5: send command, execute it and receive back the execution answer
				config.merge(signature);
				sendClientRequestByClient(config);

				boolean flush = false;
				boolean stop = StringUtility.equalsIgnoreCase(config.getString("action"), "stop") && !config.containsKey("channel") && getManager().getExecutionMode() != RuntimeManager.RUNTIME;

				do
				{
					if(!stop) readServerAnswerByClient(config);
					flush = config.getBoolean(flagFlush, false);

					if(flush)
					{
						getManager().print(config);
						RuntimeLogger.clearData();
					}
					else if(stop)
					{
						RuntimeLogger.info("AREasy Runtime server shutting down");
					}
				}
				while(flush);
			}
		}
		catch (Exception e)
		{
			if(!RuntimeLogger.isOff())
			{
				RuntimeLogger.error("Error running client request: " + e.getMessage());
				logger.debug("Exception", e);
			}
			else logger.debug("Error executing client request: " + e.getMessage());
		}
		finally
		{
			//4: Closing connection
			try
			{
				if(getIn() != null) getIn().close();
				if(getOut() != null) getOut().close();
				if(client != null) client.close();
			}
			catch (IOException e)
			{
				logger.error("Error closing client socket: " + e.getMessage());
				logger.debug("Exception", e);
			}
		}
	}

	/**
	 * Transform a configuration structure into a stream command line (the separator between parameters is CR).
	 *
	 * @param config action parameters
	 * @return translated configuration structure from input arguments.
	 */
	protected String[] getClientRequest(Configuration config)
	{
		List output = new Vector();

		//compose the command
		Iterator iterator = config.getKeys();

		while(iterator != null && iterator.hasNext())
		{
			String key = (String) iterator.next();
			List list = config.getVector(key, null);

			output.add("-" + key);
			if(list != null && !list.isEmpty()) output.addAll(list);
		}

		return (String[])output.toArray(new String[output.size()]);
	}

	public Configuration getClientSignature()
	{
		Configuration signature = new PropertiesConfiguration();

		List hosts = getManager().getHostsIds();
		List versions = getManager().getRuntimeModules();

		signature.setKey(flagSignature, Boolean.TRUE);
		signature.setKey(flagClientSignatureHostIds, hosts);
		signature.setKey(flagClientSignatureModules, versions);
		signature.setKey(flagClientSignatureHomePath, RuntimeManager.getHomeDirectory());
		signature.setKey(flagClientSignatureLibsPath, RuntimeManager.getLibsDirectory());
		signature.setKey(flagClientSignatureWorkPath, RuntimeManager.getWorkingDirectory());

		return signature;
	}
}

