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
 * Dedicated action execute different AR System internal objects (filter processes and/or escalations).
 */
public class SystemCallerAction extends AbstractAction
{
	/**
	 * Execute action's workflow.
	 *
	 * @throws AREasyException if any global error occurs.
	 */
	public void run() throws AREasyException
	{
		String process = getConfiguration().getString("process", null);
		String escalation = getConfiguration().getString("escalation", null);

		if(StringUtility.isNotEmpty(process)) process(process);
		if(StringUtility.isNotEmpty(escalation)) escalation(escalation);
	}

	protected void process(String process)
	{
		boolean wait = getConfiguration().getBoolean("wait", true);
		String params = getConfiguration().getString("params", "");

		try
		{
			params = getTranslatedCondition(params);
			String output = ProcessorLevel1Context.runFilterProcess(getServerConnection(), process, params, wait);

			RuntimeLogger.add(output);
		}
		catch(Throwable th)
		{
			RuntimeLogger.error("Error running Filter process: " + th.getMessage());
			logger.debug("Exception", th);
		}
	}

	protected void escalation(String escalation)
	{
		try
		{
			ProcessorLevel1Context.runEscalation(getServerConnection(), escalation);
		}
		catch(Throwable th)
		{
			RuntimeLogger.error("Error running Escalation object: " + th.getMessage());
			logger.debug("Exception", th);
		}
	}
}
