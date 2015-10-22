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
import org.areasy.runtime.actions.SystemAction;
import org.areasy.runtime.engine.RuntimeLogger;
import org.areasy.runtime.engine.base.AREasyException;
import org.areasy.common.data.BooleanUtility;
import org.areasy.common.data.StringUtility;

import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;

/**
 * Define the runtime help job executed by server component
 *
 */
public class HelpAction extends SystemAction implements RuntimeAction
{
	/**
	 * Execute 'help' action.
	 * Define the runtime help job executed by server component
	 *
	 * @throws org.areasy.runtime.engine.base.AREasyException if any error will occur.
	 */
	public void run() throws AREasyException
	{
		boolean inventory = getConfiguration().getBoolean("inventory", false);
		String call = getConfiguration().getString("call", null);

		//call specific help
		if(StringUtility.isNotEmpty(call)) callAction(call);
			else if(inventory) callInventory();
				else callHelp();
	}

	protected void callHelp()
	{
		RuntimeLogger.add(help());
	}

	protected void callInventory()
	{
		Iterator data;

		if(getServer() != null) data = getServer().getManager().getRuntimeActions();
			else data = getManager().getRuntimeActions();

		List keys = new Vector();
		while (data.hasNext()) keys.add(data.next());

		Collections.sort(keys);
		Iterator iterator = keys.iterator();

		if(iterator != null)
		{
			int index = 1;
			RuntimeLogger.add("Runtime actions registered on the server: ");

			while(iterator.hasNext())
			{
				String actionname = (String) iterator.next();
				RuntimeLogger.add("\t" + index + ". " + actionname);

				index++;
			}
		}
	}

	protected void callAction(String action)
	{
		boolean actionBool = BooleanUtility.toBoolean(action);
		
		if(StringUtility.isEmpty(action) || actionBool)
		{
			RuntimeLogger.warn("No action name has been specified!");
			return;
		}

		RuntimeAction runtime = null;

		try
		{
			//instantiate a new runtime action
			if(SystemAction.isSystemAction(action)) runtime = SystemAction.getRuntimeAction(getServer(), action);
				else runtime = getServer() != null ? getServer().getManager().getRuntimeAction(action) : getManager().getRuntimeAction(action);

			String help = runtime.help();

			if(StringUtility.isEmpty(help)) RuntimeLogger.warn("No available help text for action '" + action + "'");
				else RuntimeLogger.add(help);
		}
		catch(Throwable th)
		{
			RuntimeLogger.error(th.getMessage());

			getLogger().error("Error reading action '" + action + "': " + th.getMessage());
			getLogger().debug("Exception", th);
		}

		runtime = null;
	}
}
