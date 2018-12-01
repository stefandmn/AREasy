package org.areasy.runtime.actions.ars.admin;

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
import org.areasy.runtime.actions.AbstractAction;
import org.areasy.runtime.engine.RuntimeLogger;
import org.areasy.runtime.engine.base.AREasyException;
import org.areasy.runtime.engine.workflows.ProcessorLevel1Context;

/**
 * Dedicated action to encrypt and decrypt keys and passwords in order to be used in
 * AR configurations or data management.
 */
public class PasswordAdministration extends AbstractAction
{
	/**
	 * Execute action's workflow.
	 *
	 * @throws org.areasy.runtime.engine.base.AREasyException if any global error occurs.
	 */
	public void run() throws AREasyException
	{
		String operation = getConfiguration().getString("operation", null);

		//get the application role
		String password = getConfiguration().getString("password", null);

		if(StringUtility.equalsIgnoreCase(operation, "encrypt")) encrypt(password);
			else if(StringUtility.equalsIgnoreCase(operation, "decrypt")) decrypt(password);
	}

	/**
	 * Encrypt specified password. This method will return to standard console encrypted value of
	 * the specified plain text password/key.
	 *
	 * @param password plain text password
	 */
	protected void encrypt(String password)
	{
		if(password != null)
		{
			try
			{
				String encrypted = ProcessorLevel1Context.encryptARPassword(password);
				RuntimeLogger.add(encrypted);
			}
			catch(Throwable th)
			{
				RuntimeLogger.error("Error encrypting password: " + th.getMessage());
				logger.debug("Exception", th);
			}
		}
		else RuntimeLogger.warn("Password value was not specified\nUse the following syntax: areasy -action admin.password.encrypt -password <password>");
	}

	/**
	 * Decrypt specified password. This method will return to standard console decrypted value of
	 * the specified encrypted password/key.
	 *
	 * @param password encrypted text password
	 */
	protected void decrypt(String password)
	{
		if(password != null)
		{
			try
			{
				String encrypted = ProcessorLevel1Context.decryptARPassword(password);
				RuntimeLogger.add(encrypted);
			}
			catch(Throwable th)
			{
				RuntimeLogger.error("Error decrypting password: " + th.getMessage());
				logger.debug("Exception", th);
			}
		}
		else RuntimeLogger.warn("Password value was not specified\nUse the following syntax: areasy -action admin.password.decrypt -password <password>");
	}
}
