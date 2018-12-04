package org.areasy.runtime.engine.base;

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

import com.bmc.arsys.api.ARServerUser;
import org.areasy.common.data.StringUtility;
import org.areasy.common.data.type.credential.Credential;
import org.areasy.common.logger.Logger;
import org.areasy.common.logger.LoggerFactory;
import org.areasy.common.support.configuration.Configuration;
import org.areasy.runtime.engine.services.cache.InitialObject;

/**
 * This class is a generic API for communication with AR server.
 *
 */
public class ServerConnection extends InitialObject
{
	private static Logger logger = LoggerFactory.getLog(ServerConnection.class);

	/** AR Server user context */
	private ARServerUser context = null;

	private String serverName = null;
	private String userName = null;
	private String userPassword = null;
	private int serverPort = 0;
	private int rpcQueue = 0;

	/** define an impersonation flag */
	private boolean impersonated = false;

	private boolean overlay = false;
	private String overlayGroup = null;

	private String encoding = "en_US.UTF8";
	private String external = "";

	/**
	 * Connect to the AR Server and receive the user context. This method is using a configuration structure where
	 * should be present the following properties:
	 * <li>server.name = AR Server name</li>
	 * <li>user.name = Remedy user name</li>
	 * <li>user.password = Remedy password for specified user</li>
	 * <li>port.number = AR Server port address</li>
	 * <li>rpc.queue = AR Server RPC queue</li>
	 * <li>mode = Server connection mode: <code>overlay</code> (that means the connection will be open in best practice mode)
	 * or <code>base</code> (base development mode)</li>
	 *
	 * @param configuration configuration subset to extract user, password, server name, port number and rpc number.
	 * @throws AREasyException if any error will occur
	 */
	public final void connect(Configuration configuration) throws AREasyException
	{
		if(context != null) return;

		String arserver = configuration.getString("server.name");
		String aruser = configuration.getString("user.name");
		String arpassword = configuration.getString("user.password", "");
		int arport = configuration.getInt("port.number", 0);
		int rpcqueue = configuration.getInt("rpc.queue", 0);
		String mode = configuration.getString("mode", "base");

		connect(arserver, aruser, arpassword, arport, rpcqueue, mode, null);
	}

	/**
	 * Connect to the AR Server and receive the user context. This method is using a an inherited connection structure.
	 *
	 * @param connection an inherited connection structure which should be active.
	 * @throws AREasyException if any error will occur
	 */
	public final void connect(ServerConnection connection) throws AREasyException
	{
		if(context != null) return;

		String arserver = connection.serverName;
		String aruser = connection.getContext().getUser();
		String arpassword = connection.userPassword;
		int arport = connection.serverPort;
		int rpcqueue = connection.rpcQueue;
		boolean overlay = connection.overlay;

		if(!connection.impersonated) connect(arserver, aruser, arpassword, arport, rpcqueue, overlay, null);
			else connect(arserver, aruser, arpassword, arport, rpcqueue, overlay, getUserName());
	}

	/**
	 * Connect to the AR Server and receive the user context.
	 *
	 * @param arserver remedy server
	 * @param aruser remedy user
	 * @param arpassword remedy password
	 * @param arport connectivity direct port (optional - could be 0)
	 * @param rpcqueue RPC queue defined to the server level
	 * @param mode server connection type: overlay or base
	 * @throws AREasyException if any error will occur
	 */
	public final void connect(String arserver, String aruser, String arpassword, int arport, int rpcqueue, String mode) throws AREasyException
	{
		connect(arserver, aruser, arpassword, arport, rpcqueue, StringUtility.equalsIgnoreCase(mode, "overlay"), null);
	}

	/**
	 * Connect to the AR Server and receive the user context.
	 *
	 * @param arserver remedy server
	 * @param aruser remedy user
	 * @param arpassword remedy password
	 * @param arport connectivity direct port (optional - could be 0)
	 * @param rpcqueue RPC queue defined to the server level
	 * @param overlay true of overlay connection
	 * @throws AREasyException if any error will occur
	 */
	public final void connect(String arserver, String aruser, String arpassword, int arport, int rpcqueue, boolean overlay) throws AREasyException
	{
		connect(arserver, aruser, arpassword, arport, rpcqueue, overlay, null);
	}

	/**
	 * Connect to the AR Server and receive the user context. This method can also impersonate obtained user session for another user
	 * (here is specified in the parameter <code>impuser</code>). The entire workflow will use the impersonated user credentials and also
	 * when the server connection structure is interrogated about what user is connected will be returned the impersonated username.
	 *
	 * @param arserver remedy server
	 * @param aruser remedy user
	 * @param arpassword remedy password
	 * @param arport connectivity direct port (optional - could be 0)
	 * @param rpcqueue RPC queue defined to the server level
	 * @param mode server connection type: overlay or base
	 * @param impuser user for impersonation feature.
	 * @throws AREasyException if any error will occur
	 */
	public final void connect(String arserver, String aruser, String arpassword, int arport, int rpcqueue, String mode, String impuser) throws AREasyException
	{
		connect(arserver, aruser, arpassword, arport, rpcqueue, StringUtility.equalsIgnoreCase(mode, "overlay"), impuser);
	}

	/**
	 * Connect to the AR Server and receive the user context. This method can also impersonate obtained user session for another user
	 * (here is specified in the parameter <code>impuser</code>). The entire workflow will use the impersonated user credentials and also
	 * when the server connection structure is interrogated about what user is connected will be returned the impersonated username.
	 *
	 * @param arserver remedy server
	 * @param aruser remedy user
	 * @param arpassword remedy password
	 * @param arport connectivity direct port (optional - could be 0)
	 * @param rpcqueue RPC queue defined to the server level
	 * @param overlay true is server connection will be done in overlay (best practice) mode
	 * @param impuser user for impersonation feature.
	 * @throws AREasyException if any error will occur
	 */
	public final void connect(String arserver, String aruser, String arpassword, int arport, int rpcqueue, boolean overlay, String impuser) throws AREasyException
	{
		if (arserver == null || aruser == null) throw new AREasyException("Not enough connectivity properties!");

		//validate password parameter - if is null will be considered a blank password
		if (arpassword == null) arpassword = "";
		arpassword = Credential.getCredential(arpassword).decode();

		//check if actual connection must be impersonated
		impersonated = StringUtility.isNotEmpty(impuser) && !StringUtility.equals(impuser, aruser);

		logger.debug("Connecting to the '" + arserver + "' AR System server");
		if (arport > 0) setContext(new ARServerUser(aruser, arpassword, external, encoding, arserver, arport));
			else setContext(new ARServerUser(aruser, arpassword, external, encoding, arserver));

		//Set the override previous IP flag
		getContext().setOverridePrevIP(true);

		//se connection properties
		setServerName(arserver);
		setUserPassword(arpassword);
		setServerPort(arport);

		//set connection impersonation
		try
		{
			//set private RPC queue - if it was specified.
			if(rpcqueue > 0)
			{
				getContext().usePrivateRpcQueue(rpcqueue);
				setRpcQueue(rpcqueue);
			}

			//impersonate the current user session for specified additional user name
			if(isImpersonated())
			{
				getContext().impersonateUser(impuser);
				logger.debug("Actual session for user '" + aruser + "' has to be impersonated by user '" + impuser + "'");
			}
			else setUserName(aruser);
		}
		catch(Throwable th)
		{
			logger.error("AR System server connection impersonation error: " + th.getMessage());
			logger.debug("Exception", th);
		}

		//set overlay or base mode
		if(overlay) setOverlayMode();
			else setBaseMode();

		//connect
		try
		{
			//execute context login
			getContext().login();
			logger.debug("Connected to '" + arserver + "' AR System server: " + getContext().getServerVersion() + " - " + (isOverlayMode() ? "overlay" : "base") + " connection mode");
		}
		catch(Throwable th)
		{
			context = null;
			throw new AREasyException(th);
		}
	}

	/**
	 * Create a new server connection based on the existing connection
	 *
	 * @return a new server connection
	 * @throws AREasyException in case of any AREasy error occurs
	 */
	public ServerConnection newConnection() throws AREasyException
	{
		ServerConnection newconn = new ServerConnection();
		newconn.connect(this);

		return newconn;
	}

	/**
	 * Create a new server connection based on the existing connection, customizing it for overlay or base mode.
	 *
	 * @return a new server connection
	 * @throws AREasyException in case of any AREasy error occurs
	 */
	public ServerConnection newConnection(boolean overlay) throws AREasyException
	{
		ServerConnection newconn = new ServerConnection();
		newconn.connect(this.serverName, this.userName, this.userPassword, this.serverPort, this.rpcQueue, overlay, (getContext() != null ? getContext().getImpersonatedUser() : null));

		return newconn;
	}

	/**
	 * Check if te current connection is impersonated
	 *
	 * @return true is the connection is impersonated
	 */
	public final boolean isImpersonated()
	{
		return impersonated;
	}

	/**
	 * Get AR server context.
	 *
	 * @return AR User context structure.
	 */
	public final ARServerUser getContext()
	{
		return context;
	}

	/**
	 * Check if actual server connection structure is connected to the server.
	 * @return true is the connection is done and persist.
	 */
	public final boolean isConnected()
	{
		return context != null;
	}

	/**
	 * Set AR User context structure.
	 *
	 * @param context AR user structure.
	 */
	private void setContext(ARServerUser context)
	{
		this.context = context;
	}

	/**
	 * Disconnect server connection instance from AR server and relase AR USer context structure.
	 */
	public final void disconnect()
	{
		try
		{
			if(isConnected())
			{
				logger.debug("Disconnecting from AR server: " + this);
				getContext().logout();
			}

			setContext(null);
		}
		catch(Exception e)
		{
			logger.debug("Error disposing AR Context object: " + e.getMessage());
			logger.trace("Exception", e);
		}
	}

	/**
	 * Get AR System server name
	 *
	 * @return server name used in connectivity workflow
	 */
	public final String getServerName()
	{
		return serverName;
	}

	/**
	 * Set AR System server name or IP address
	 *
	 * @param serverName server name or IP address
	 */
	public void setServerName(String serverName)
	{
		this.serverName = serverName;
	}

	/**
	 * Get user name (login Id) used to connect to AR System server
	 * @return AR System login id
	 */
	public final String getUserName()
	{
		return userName;
	}

	/**
	 * Set user name (login id) to be used to connect to AR System server
	 * @param userName AR System login id
	 */
	public void setUserName(String userName)
	{
		this.userName = userName;
	}

	public final String getUserPassword()
	{
		return userPassword;
	}

	public void setUserPassword(String userPassword)
	{
		this.userPassword = userPassword;
	}

	public final int getServerPort()
	{
		return serverPort;
	}

	public final void setServerPort(int serverPort)
	{
		this.serverPort = serverPort;
	}

	public final int getRpcQueue()
	{
		return rpcQueue;
	}

	public final void setRpcQueue(int rpcQueue)
	{
		this.rpcQueue = rpcQueue;
	}
	/**
	 * Check is the current connection instance is persistent or not, that means
	 * is the connection is created by <code>InitialObject</code> super-class and
	 * preloaded by the <code>RuntimeServer</code>
	 *
	 * @return true is the connection is persistent
	 */
	public final boolean isPersistent()
	{
		return getManager() != null;
	}

	public void setOverlayMode()
	{
		this.overlay = true;
		setOverlayGroup("1");

		getContext().setOverlayFlag(this.overlay);
	}

	public void setBaseMode()
	{
		this.overlay = false;
		this.overlayGroup = null;

		getContext().setOverlayFlag(this.overlay);
	}

	public void setOverlayGroup(String overlayGroup)
	{
		if(getContext() != null)
		{
			this.overlayGroup = overlayGroup;

			getContext().setDesignOverlayGroup(this.overlayGroup);
			getContext().setOverlayGroup(this.overlayGroup);
		}
	}

	public boolean isOverlayMode()
	{
		return this.overlay;
	}

	public boolean isBaseMode()
	{
		return !this.overlay;
	}

	public String getMode()
	{
		return isBaseMode() ? "base" : "overlay";
	}

	/**
	 * Get overlay group id
	 *
	 * @return overlay group id or null for base mode
	 */
	public String getOverlayGroup()
	{
		return this.overlayGroup;
	}

	/**
	 * Prepare AR System connection to be stored in cache layer and used to initialize all actions
	 * to use it from there.
	 */
	public void prepare() throws AREasyException
	{
		//get direct server name
		String arserver = getManager().getConfiguration().getString("app.server.default.arsystem.server.name", "localhost");
		int arport = getManager().getConfiguration().getInt("app.server.default.arsystem.port.number", 0);
		int rpcqueue = getManager().getConfiguration().getInt("app.server.default.arsystem.rpc.queue", 0);
		String aruser = getManager().getConfiguration().getString("app.server.default.arsystem.user.name", null);
		String arpassword = getManager().getConfiguration().getString("app.server.default.arsystem.user.password", null);
		String mode = getManager().getConfiguration().getString("app.server.default.arsystem.mode", "base");

		connect(arserver, aruser, arpassword, arport, rpcqueue, mode);
		logger.debug("Persistent AR System server connection: " + this);
	}

	/**
	 * Destroy AR System connection when the instance is released.
	 */
	public void destroy()
	{
		setManager(null);
		if(isConnected()) disconnect();
	}

	/**
	 * Set TTL property for this object is cache.
	 *
	 * @return TTL value.
	 */
	public int getTimeToLive()
	{
		return TTL_FOREVER;
	}

	/**
	 * Returns a string representation of the object.
	 *
	 * @return  a string representation of the object.
	 */
	public String toString()
	{
		return "AR System Connection [ Server = " + getServerName() + ", Server Port = " + getServerPort() + ", RPC Queue = " + getRpcQueue() + ", User Name = " + getUserName() +", Password = ********" + (isOverlayMode() ? ", Overlay Mode" : ", Base Mode") + "]";
	}
}
