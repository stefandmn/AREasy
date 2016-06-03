package org.areasy.runtime.actions.arserver.admin;

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

import org.areasy.runtime.actions.data.admin.AbstractUserEnrollment;
import org.areasy.runtime.engine.RuntimeLogger;
import org.areasy.runtime.engine.base.AREasyException;
import org.areasy.runtime.engine.workflows.ProcessorLevel1Context;

/**
 * Dedicated action to encrypt a plain string password in order to be used in AR configurations
 */
public class SystemPasswordEncryptAction extends AbstractUserEnrollment
{
	/**
	 * Execute action's workflow.
	 *
	 * @throws org.areasy.runtime.engine.base.AREasyException if any global error occurs.
	 */
	public void run() throws AREasyException
	{
		//get the application role
		String password = getConfiguration().getString("password", null);

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
}
