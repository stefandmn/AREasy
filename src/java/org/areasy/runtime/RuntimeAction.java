package org.areasy.runtime;

/*
 * Copyright (c) 2007-2015 AREasy Runtime
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

import org.areasy.runtime.engine.base.AREasyException;
import org.areasy.runtime.engine.base.ServerConnection;
import org.areasy.runtime.engine.services.status.BaseStatus;
import org.areasy.common.support.configuration.Configuration;

/**
 * This is a template for runtime actions which will take from the runtime manager the corresponding
 * configuration structure and the action signature. Based on this tample could be implemented all necessary
 * action to do different tasks.
 *
 */
public interface RuntimeAction
{
	/**
	 * Set action signature.
	 *
	 * @param signature action code
	 */
	void setCode(String signature);

	/**
	 * Initialize action instance and set all basic attributes: action signature (code), configuration structure, and the inherited runtime
	 * manager instance. Also this method will initiate an AR server connection based on the following parameterization:
	 *
	 * <table border="1">
	 * 	<tr>
	 * 		<td><b>-arserver</b></td>
	 * 		<td>Server name - required login parameter that specifies the server to log in to</td>
	 * 	</tr>
	 * 	<tr>
	 * 		<td><b>-aruser</b></td>
	 * 		<td>Required login parameter that identifies the user account</td>
	 * 	</tr>
	 * 	<tr>
	 * 		<td><b>-arpassword</b></td>
	 * 		<td>Optional login parameter that identifies the user account. Omit the option if the user account has no password</td>
	 * 	</tr>
	 * 	<tr>
	 * 		<td><b>-arport</b></td>
	 * 		<td>TCP port number used to log in when the portmapper is turned off</td>
	 * 	</tr>
	 * 	<tr>
	 * 		<td><b>-arrpc</b></td>
	 * 		<td>RPC identifier when the portmapper is turned on</td>
	 * 	</tr>
	 * </table>
	 *
	 * <p>
	 * These parameters are inherited by all implemented actions.
	 *
	 * @param config action parameters.
	 * @param manager runtime manager instance.
	 * @throws org.areasy.runtime.engine.base.AREasyException if any error will occur
	 *
	 * @see RuntimeAction#init(Configuration, RuntimeManager, ServerConnection)
	 */
	void init(Configuration config, RuntimeManager manager) throws AREasyException;

	/**
	 * Initialize action instance and set all basic attributes: action signature (code), configuration structure, the inherited runtime
	 * manager instance and the AR server connection. This method could be used by a parent action which will use subsequences, generating
	 * a data update workflow.
	 *
	 * @param config action parameters.
	 * @param manager runtime manager instance.
	 * @param target ar server connection structure.
	 * @throws org.areasy.runtime.engine.base.AREasyException if any error will occur
	 */
	void init(Configuration config, RuntimeManager manager, ServerConnection target) throws AREasyException;

	/**
	 * Initialize action instance and set all basic attributes which are inherited from a parent runtime action.
	 *
	 * @param action parent runtime or related action
	 * @throws org.areasy.runtime.engine.base.AREasyException if any error will occur
	 *
	 * @see RuntimeAction#init(Configuration, RuntimeManager, ServerConnection)
	 */
	void init(RuntimeAction action) throws AREasyException;

	/**
	 * Run secondary initialization (from the final implementation class)
	 *
	 * @throws AREasyException if any error will occur.
	 */
	void open() throws AREasyException;

	/**
	 * Check if the current action was initialized or not
	 *
	 * @return true if the action is already initialized.
	 */
	boolean isInit();

	/**
	 * Execute the current action.
	 *
	 * @throws org.areasy.runtime.engine.base.AREasyException if any error will occur
	 */
	void run() throws AREasyException;

	/**
	 * Get a help text about syntax execution of the current action.
	 * This method should be executed without action initialization.
	 *
	 * @return text message specifying the syntax of the current action
	 */
	String help();

	/**
	 * This method it is used to execute send a notification with data and log messages. The default implementation
	 * take into consideration all configurations described in <code>areasy</code> configuration sector and <code>notification</code>
	 * parameter which decide if the standard event is executed or not. In the same time all configurations are doubled by parameters.
	 */
	void report();

	/**
	 * This method it is used to execute some particular tasks when the standard execution and custom execution fail and occur an Exception.
	 * So, the exceptions occurred during execution should handled here. This method is called only if the action was initialized.
	 *
	 * @param th throwable execution occurred when standard execution of custom execution was done.
	 */
	void throwable(Throwable th);

	/**
	 * Run secondary disposer (from the final implementation class)
	 *
	 * @throws org.areasy.runtime.engine.base.AREasyException if any error will occur
	 */
	void close() throws AREasyException;

	/**
	 * Close and dispose all objects used by this action.
	 */
	void dispose();

	/**
	 * Soft and safe interruption of action action.
	 */
	void interrupt();

	/**
	 * Check if the current action execution was softly interrupted
	 *
	 * @return tru if the action execution was interrupted
	 */
	boolean isInterrupted();

	/**
	 * Get action code
	 *
	 * @return the runtime action code (identifier)
	 */
	String getCode();

	/**
	 * Get action parameters map.
	 *
	 * @return action configuration structure.
	 */
	Configuration getConfiguration();

	/**
	 * Get runtime manager instance
	 * @return runtime manager instance defined after action's initialization
	 */
	RuntimeManager getManager();

	/**
	 * Get ARS server connection structure and context.
	 * @return <code>ServerConnection</code> instance or null.
	 */
	ServerConnection getServerConnection();

	/**
	 * Get status instance. In order to deliver a status of the current stage (for actions which the execution will take much more time)
	 * this method must be implemented.
	 *
	 * @return <code>BaseStatus</code> structure and in the current implementation is returning a null value.
	 */
	BaseStatus getCurrentStatus();
}
