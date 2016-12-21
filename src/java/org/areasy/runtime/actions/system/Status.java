package org.areasy.runtime.actions.system;

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
import org.areasy.runtime.RuntimeManager;
import org.areasy.runtime.actions.SystemAction;
import org.areasy.runtime.engine.RuntimeLogger;
import org.areasy.runtime.engine.RuntimeRunner;
import org.areasy.runtime.engine.RuntimeThread;
import org.areasy.runtime.engine.base.AREasyException;
import org.areasy.runtime.engine.services.status.BaseStatus;
import org.areasy.common.data.BooleanUtility;
import org.areasy.common.data.StringUtility;
import org.areasy.common.support.configuration.Configuration;

import java.text.NumberFormat;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.Vector;

/**
 * Status message action implementation.
 * Status action displays the environment details of the runtime instance or displays the current status of the AREasy server instance.
 */
public class Status extends SystemAction implements RuntimeAction
{
	/**
	 * Execute 'status' action.
	 * Processing status internal action - status server execution
	 *
	 * @throws org.areasy.runtime.engine.base.AREasyException if any error will occur.
	 */
	public void run() throws AREasyException
	{
		boolean signal = getConfiguration().getBoolean("signal", false);
		int sleep = getConfiguration().getInt("sleep", 0);
		int repeat = getConfiguration().getInt("repeat", 1);

		boolean varBoolChannels = false;
		List varListChannels = getConfiguration().getList("channels", new Vector());

		//validate channels variables
		if(!varListChannels.isEmpty() && varListChannels.size() == 1 && (StringUtility.equalsIgnoreCase("true", (String) varListChannels.get(0)) || StringUtility.equalsIgnoreCase("false", (String) varListChannels.get(0))))
		{
			varBoolChannels = BooleanUtility.toBoolean( (String)varListChannels.get(0));
			varListChannels = null;
		}

		String varStringCall = getConfiguration().getString("call", null);
		String varStringChannel = getChannelName();

		//get environment variable
		boolean varBoolEnvironment = getConfiguration().getBoolean("env", getConfiguration().getBoolean("environment", !(varBoolChannels || !varListChannels.isEmpty() || varStringChannel != null || varStringCall != null)));

		//display environment information
		if(varBoolEnvironment) printEnvironment();

		//skip runtime mode
		if(getManager().getExecutionMode() != RuntimeManager.RUNTIME)
		{
			boolean next;
			int times = 0;

			do
			{
				//init loop decision
				next = false;

				//display channels
				if(varBoolChannels) printChannelsList();
					else if(varListChannels != null) next |= printChannelsStatus(varListChannels);

				//display channel status output
				if(StringUtility.isNotEmpty(varStringChannel)) next |= printChannelStatus(varStringChannel, signal);

				//display action status output
				if(StringUtility.isNotEmpty(varStringCall)) next |= printCommandStatus(varStringCall, signal);

				//check if sleep is required.
				if(sleep > 0)
				{
					try
					{
						Thread.sleep(sleep);
					}
					catch(InterruptedException e)
					{
						getLogger().error("Action couldn't be frozen for " + sleep + "milliseconds");
					}
				}

				times++;

				//find decision to continue the loop or not
				if(repeat > 0 && times < repeat) next = true;
				if(repeat > 0 && times >= repeat) next = false;

				//send to teh client partial content
				if(next) RuntimeLogger.flush();
			}
			while(next);
		}
	}

	protected void printEnvironment()
	{
		StringBuffer buffer = new StringBuffer();
		Configuration config = getManager().getConfiguration();

		if(config.containsKey("app.server.host")) buffer.append("AREasy Runtime: ").append(config.getString("app.server.host")).append(":").append(config.getString("app.server.port")).append("\n");
		if(config.containsKey("app.server.default.arsystem.user.name")) buffer.append("AR System Server: ").append(config.getString("app.server.default.arsystem.user.name")).append("@").append(config.getString("app.server.default.arsystem.server.name")).append(config.getInt("app.server.default.arsystem.port.number", 0) > 0 ? (":" + config.getString("app.server.default.arsystem.port.number")) : "").append("\n\n");

		NumberFormat number = NumberFormat.getInstance();
		number.setMaximumFractionDigits(2);

		buffer.append("Allocatable memory: ").append(number.format(Runtime.getRuntime().maxMemory() / 1024)).append(" KB" + "\n");
		buffer.append("Total memory: ").append(number.format(Runtime.getRuntime().totalMemory() / 1024)).append(" KB" + "\n");
		buffer.append("Free memory: ").append(number.format(Runtime.getRuntime().freeMemory() / 1024)).append(" KB" + "\n\n");

		//display queue details only if the call is made over client-server protocol.
		if(getManager().getExecutionMode() > 0)
		{
			int actionsInQueue = getServer().getQueueLength();
			int processedActions = getServer().getProcessedActionsCount();
			int availableChannels = getServer().getAvailableChannelsCount();
			int createdChannels =getServer().getChannelsCount();

			buffer.append("Waiting actions in queue: ").append(number.format(actionsInQueue)).append("\n");
			buffer.append("Processed actions: ").append(number.format(processedActions)).append("\n");
			buffer.append("Available channels: ").append(number.format(availableChannels)).append("\n");
			buffer.append("Created channels: ").append(number.format(createdChannels)).append("\n");
			buffer.append("Running channels: ").append(number.format(createdChannels - availableChannels)).append("\n\n");
		}

		RuntimeLogger.add(buffer.toString());
	}

	protected void printChannelsList()
	{
		Map map = new Hashtable();
		List list = getRuntimeChannels();
		StringBuffer buffer = new StringBuffer();

		map.put("running", new Vector());
		map.put("waiting", new Vector());
		map.put("closing", new Vector());

		for(int i = 0; i < list.size(); i++)
		{
			RuntimeThread thread = (RuntimeThread)list.get(i);
			Runnable runner = thread.getRunner();

			if(runner != null && (runner instanceof RuntimeRunner))
			{
				String actionName = null;
				Configuration actionConfig = null;
				String threadName = thread.getName();

				actionConfig = ((RuntimeRunner)runner).getConfiguration();

				if(actionConfig != null)
				{
					actionName = actionConfig.getString("action", "...");
					String message = threadName + ": " + actionName;

					if(thread.isRunning()) ((List)map.get("running")).add(message);
					if(thread.isWaiting()) ((List)map.get("waiting")).add(message);
					if(thread.isClosing()) ((List)map.get("closing")).add(message);
				}
			}
		}

		//get running actions
		List running = (List) map.get("running");
		if(running != null && running.size() > 0)
		{
			buffer.append("Running actions:").append("\n");
			for(int i = 0; i < running.size(); i++) buffer.append("\t").append(running.get(i)).append("\n");
		}

		//get waiting actions
		List waiting = (List) map.get("waiting");
		if(waiting != null && waiting.size() > 0)
		{
			buffer.append("Waiting actions:").append("\n");
			for(int i = 0; i < waiting.size(); i++) buffer.append("\t").append(waiting.get(i)).append("\n");
		}

		//get closing actions
		List closing = (List) map.get("closing");
		if(closing != null && closing.size() > 0)
		{
			buffer.append("Closing actions:").append("\n");
			for(int i = 0; i < closing.size(); i++) buffer.append("\t").append(closing.get(i)).append("\n");
		}

		buffer.append("\n");
		RuntimeLogger.add(buffer.toString());
	}

	protected boolean printChannelsStatus(List channels)
	{
		boolean result = false;

		for(int i = 0; i < channels.size(); i++)
		{
			int channel = getChannelId((String) channels.get(i));
			result |= printChannelStatus(getChannelName(channel), false);
		}

		return result;
	}

	protected boolean printChannelStatus(String varStringChannel, boolean signal)
	{
		boolean found = false;
		List list = getRuntimeChannels();
		StringBuffer buffer = new StringBuffer();

		//validate channel name
		if(varStringChannel == null) return false;

		//define prefix data for output message.
		String prefix = "Channel " + varStringChannel + ": ";

		//interrogate all available channels
		for(int i = 0; !found && i < list.size(); i++)
		{
			RuntimeThread thread = (RuntimeThread)list.get(i);
			Runnable runner = thread.getRunner();

			if(runner != null && runner instanceof RuntimeRunner)
			{
				String threadName = thread.getName();

				if(StringUtility.equalsIgnoreCase(threadName, varStringChannel))
				{
					found = true;

					RuntimeRunner runtime = ((RuntimeRunner)runner);
					RuntimeAction action = runtime.getRunnerAction();

					if(signal)
					{
						if(action != null)
						{
							buffer.append(String.valueOf(0));
							BaseStatus status = action.getCurrentStatus();

							if(status != null) RuntimeLogger.info(prefix + status.getStatusMessage());
								else RuntimeLogger.warn(prefix + "No status message implemented for action '" + action.getCode() + "'");
						}
					}
					else
					{
						if(action != null)
						{
							BaseStatus status = action.getCurrentStatus();

							if(status != null) buffer.append(prefix).append(status.getStatusMessage()).append("\n");
								else RuntimeLogger.warn(prefix + "No status message implemented for action '" + action.getCode() + "'");
						}
					}
				}
			}
		}

		if(!found)
		{
			RuntimeLogger.warn("No running action assigned to the '" + varStringChannel + "' execution channel");

			return false;
		}
		else
		{
			if(buffer.length() > 0 && !signal)
			{
				buffer.append("\n");
				RuntimeLogger.add(buffer.toString());
			}
			else if(buffer.length() > 0 && signal) RuntimeLogger.add(buffer.toString());

			return true;
		}
	}

	protected boolean printCommandStatus(String varStringCommand, boolean signal)
	{
		boolean found = false;
		List list = getRuntimeChannels();
		StringBuffer buffer = new StringBuffer();

		for(int i = 0; i < list.size(); i++)
		{
			RuntimeThread thread = (RuntimeThread)list.get(i);
			Runnable runner = thread.getRunner();

			//define prefix data for output message.
			String prefix = "Channel " + thread.getId() + ": ";

			if(runner != null && runner instanceof RuntimeRunner)
			{
				RuntimeRunner runtime = ((RuntimeRunner)runner);
				RuntimeAction action = runtime.getRunnerAction();

				if(action != null && StringUtility.equalsIgnoreCase(action.getCode(), varStringCommand))
				{
					found = true;

					if(signal)
					{
						buffer.append(String.valueOf(0));
						BaseStatus status = action.getCurrentStatus();

						if(status != null) RuntimeLogger.info(prefix + status.getStatusMessage());
							else RuntimeLogger.warn(prefix + "No status message implemented for action '" + action.getCode() + "'");
					}
					else
					{
						BaseStatus status = action.getCurrentStatus();

						if(status != null) buffer.append(prefix).append(status.getStatusMessage()).append("\n");
							else RuntimeLogger.warn(prefix + "No status message implemented for action '" + action.getCode() + "'");
					}
				}
			}
		}

		if(!found)
		{
			RuntimeLogger.warn("No '" + varStringCommand + "' action found in execution!");
			return false;
		}
		else
		{
			if(buffer.length() > 0 && !signal)
			{
				buffer.append("\n");
				RuntimeLogger.add(buffer.toString());
			}
			else if(buffer.length() > 0 && signal) RuntimeLogger.add(buffer.toString());

			return true;
		}
	}
}
