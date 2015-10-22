package org.areasy.runtime.actions.system;

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

import org.areasy.runtime.RuntimeManager;
import org.areasy.runtime.actions.SystemAction;
import org.areasy.runtime.engine.RuntimeLogger;
import org.areasy.runtime.engine.base.AREasyException;
import org.areasy.common.data.StringUtility;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

/**
 * Run an external process.
 */
public class ProcessAction extends SystemAction
{
	/**
	 * Execute the current action.
	 *
	 * @throws org.areasy.runtime.engine.base.AREasyException
	 *          if any error will occur
	 */
	public void run() throws AREasyException
	{
		boolean isRemote = isRemoteAction();
		boolean isPermitted = false;

		//check if the method is executed in runtime mode.
		if(getManager().getExecutionMode() != RuntimeManager.RUNTIME)
		{
			//check if is remote action
			if(isRemote) isPermitted = isAdminUserAuthorized();
				else isPermitted = true;
		}
		else isPermitted = true;

		if(!isPermitted)
		{
			//forbidden shutdown command
			RuntimeLogger.warn("Process action could be executed only from the local server or from an user with administrative privileges");
			getLogger().warn("Process action could be executed only from the local server or from an user with administrative privileges");
		}
		else
		{
			String command = getConfiguration().getString("call", null);

			if(command != null) exec(command);
				else RuntimeLogger.warn("Process command line is null");
		}
	}

	protected void exec(String process)
	{
		RuntimeLogger.debug("Calling process: " + process);

		try
		{
			Process proc = Runtime.getRuntime().exec(process);
			String outString = convertStreamToString(proc.getInputStream());
			String errString = convertStreamToString(proc.getErrorStream());

			if(StringUtility.isNotEmpty(outString))
			{
				RuntimeLogger.add(outString);
				RuntimeLogger.info(outString);
			}

			if(StringUtility.isNotEmpty(errString))
			{
				RuntimeLogger.error(errString);
			}
		}
		catch(Exception e)
		{
			RuntimeLogger.error("Error running process: " + e.getMessage());
			getLogger().debug("Exception", e);
		}
	}

	public String convertStreamToString(InputStream is) throws IOException
	{
		if (is != null)
		{
			StringBuffer writer = new StringBuffer();

			try
			{
				String line;
				BufferedReader reader = new BufferedReader(new InputStreamReader(is, "UTF-8"));

				while ((line = reader.readLine()) != null)
				{
					writer.append(line);
					writer.append("\n");
				}
			}
			finally
			{
				is.close();
			}

			return writer.toString();
		}
		else return null;
	}
}
