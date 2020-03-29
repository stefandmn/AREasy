package org.areasy.runtime.actions.ars.admin;

/*
 * Copyright (c) 2007-2020 AREasy Runtime
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
public class PasswordManagerAction extends AbstractAction
{
	/**
	 * Execute action's workflow.
	 *
	 * @throws org.areasy.runtime.engine.base.AREasyException if any global error occurs.
	 */
	public void run() throws AREasyException
	{
		String operation = getConfiguration().getString("operation", "decrypt");
		String type = getConfiguration().getString("type", "field");

		//get the application role
		String key = getConfiguration().getString("key", null);

		if(StringUtility.equalsIgnoreCase(type, "field"))
		{
			if (StringUtility.equalsIgnoreCase(operation, "encrypt")) encrypt(key);
				else if (StringUtility.equalsIgnoreCase(operation, "decrypt")) decrypt(key);
		}
		else if(StringUtility.equalsIgnoreCase(type, "system"))
		{
			if (StringUtility.equalsIgnoreCase(operation, "encrypt")) sysencrypt(key);
				else if (StringUtility.equalsIgnoreCase(operation, "decrypt")) sysdecrypt(key);
		}
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
				String encrypted = ProcessorLevel1Context.encryptFieldARPassword(password);
				RuntimeLogger.add(encrypted);
			}
			catch(Throwable th)
			{
				RuntimeLogger.error("Error encrypting password: " + th.getMessage());
				logger.debug("Exception", th);
			}
		}
		else RuntimeLogger.warn("Password value was not specified\nUse the following syntax: areasy -action password -operation encrypt -key <password>");
	}

	/**
	 * Encrypt specified password. This method will return to standard console encrypted value of
	 * the specified plain text password/key.
	 *
	 * @param password plain text password
	 */
	protected void sysencrypt(String password)
	{
		if(password != null)
		{
			try
			{
				String encrypted = ProcessorLevel1Context.encryptSystemARPassword(password);
				RuntimeLogger.add(encrypted);
			}
			catch(Throwable th)
			{
				RuntimeLogger.error("Error encrypting password: " + th.getMessage());
				logger.debug("Exception", th);
			}
		}
		else RuntimeLogger.warn("Password value was not specified\nUse the following syntax: areasy -action password -type system -operation encrypt -key <password>");
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
				String encrypted = ProcessorLevel1Context.decryptFieldARPassword(password);
				RuntimeLogger.add(encrypted);
			}
			catch(Throwable th)
			{
				RuntimeLogger.error("Error decrypting system password: " + th.getMessage());
				logger.debug("Exception", th);
			}
		}
		else RuntimeLogger.warn("Password value was not specified\nUse the following syntax: areasy -action password -operation decrypt -key <password>");
	}

	/**
	 * Decrypt specified password. This method will return to standard console decrypted value of
	 * the specified encrypted password/key.
	 *
	 * @param password encrypted text password
	 */
	protected void sysdecrypt(String password)
	{
		if(password != null)
		{
			try
			{
				String encrypted = ProcessorLevel1Context.decryptSystemARPassword(password);
				RuntimeLogger.add(encrypted);
			}
			catch(Throwable th)
			{
				RuntimeLogger.error("Error decrypting system password: " + th.getMessage());
				logger.debug("Exception", th);
			}
		}
		else RuntimeLogger.warn("Password value was not specified\nUse the following syntax: areasy -action password -type system -operation decrypt -key <password>");
	}
}
