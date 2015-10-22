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

import org.areasy.runtime.RuntimeAction;
import org.areasy.runtime.RuntimeManager;
import org.areasy.runtime.actions.SystemAction;
import org.areasy.runtime.engine.RuntimeLogger;
import org.areasy.runtime.engine.RuntimeRunner;
import org.areasy.runtime.engine.RuntimeServer;
import org.areasy.runtime.engine.RuntimeThread;
import org.areasy.runtime.engine.base.AREasyException;
import org.areasy.common.data.BooleanUtility;
import org.areasy.common.data.StringUtility;

/**
 * Processing shutdown internal action - shutdown server execution.
 *
 */
public class StopAction extends SystemAction implements RuntimeAction
{
	/**
	 * Execute 'shutdown' action.
	 * Processing shutdown internal action - shutdown server execution.
	 *
	 * @throws org.areasy.runtime.engine.base.AREasyException if any error will occur.
	 */
	public void run() throws AREasyException
	{
		boolean isRemote = isRemoteAction();
		boolean isPermitted = false;

		//check if the method is executed in runtime mode.
		if(getManager().getExecutionMode() == RuntimeManager.RUNTIME)
		{
			RuntimeLogger.warn("AREasy has been started in runtime mode");
			return;
		}

		//check if is remote action
		if(isRemote) isPermitted = isAdminUserAuthorized();
			else isPermitted = true;

		//evaluate permissions
		if(!isPermitted)
		{
			//forbidden shutdown action
			RuntimeLogger.warn("Shutdown action could be sent only from the local server or from an user with administrative privileges");
		}
		else
		{
			//get requested channel
			String channel = getChannelName();

			//check requested channel
			if(StringUtility.isEmpty(channel))
			{
				//processing shutdown action - stop server execution.
				RuntimeLogger.info("AREasy Runtime server shutting down");

				//set stop flag.
				if(BooleanUtility.toBoolean(channel)) RuntimeLogger.warn("Channel flag was specified but without value!");
					else getServer().shutdown();
			}
			else if(!StringUtility.equalsIgnoreCase(channel, RuntimeLogger.getChannelName()))
			{
				if(BooleanUtility.toBoolean(channel))
				{
					RuntimeLogger.warn("Channel flag was specified but without value!");
					return;
				}

				boolean found = false;

				RuntimeThread[] threads = new RuntimeThread[RuntimeServer.getChannelsThreadGroup().activeCount()];
				RuntimeServer.getChannelsThreadGroup().enumerate(threads);

				for(int i = 0; !found && i < threads.length; i++)
				{
					Runnable runner = threads[i].getRunner();

					if(runner != null)
					{
						String threadName = threads[i].getName();

						if(StringUtility.equalsIgnoreCase(threadName, channel))
						{
							found = true;
							RuntimeThread thread = threads[i];

							if(runner instanceof RuntimeRunner)
							{
								RuntimeAction action = ((RuntimeRunner)runner).getRunnerAction();
								action.interrupt();
							}
							else
							{
								//interrupt execution in maximum 1 second
								thread.interrupt();
							}

							RuntimeLogger.info("Action '" + channel + "' was interrupted");
						}
					}
				}

				if(!found)
				{
					RuntimeLogger.warn("Channel '" + channel + "' is free and no action is running here!");
				}
			}
			else
			{
				RuntimeLogger.error("You can not kill a 'shutdown' action!");
			}
		}
	}
}
