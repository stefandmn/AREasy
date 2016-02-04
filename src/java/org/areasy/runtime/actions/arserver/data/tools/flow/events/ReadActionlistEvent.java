package org.areasy.runtime.actions.arserver.data.tools.flow.events;

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

import org.areasy.runtime.engine.RuntimeLogger;
import org.areasy.runtime.engine.base.AREasyException;
import org.areasy.common.data.StringUtility;
import org.areasy.common.support.configuration.Configuration;

/**
 * Dedicated action to perform data import workflow.
 */
public class ReadActionlistEvent extends AbstractEvent
{
	/**
	 * Execute event
	 *
	 * @throws org.areasy.runtime.engine.base.AREasyException if any error occurs
	 */
	public void execute() throws AREasyException
	{
		boolean next = true;

		//disable notification
		getAction().getConfiguration().setKey("notification", "false");

		RuntimeLogger.clearData();
		Configuration config = getAction().getManager().getConfiguration().subset("app.runtime.action." + getAction().getCode());
		String typeValue = getAction().getConfiguration().getString("type", "label");

		for(int x = 1; next && x < 100; x++)
		{
			String actionCode = "action";

			if(x < 10) actionCode += "0" + x;
				else actionCode += x;

			String actionName = config.getString(actionCode + ".name", null);
			String actionLabel = config.getString(actionCode + ".label", null);
			String actionCatalog = config.getString(actionCode + ".catalog", null);

			if(actionCatalog != null && actionName == null && actionLabel == null)
			{
				String data[] = StringUtility.split(actionCatalog, ";");

				if(data != null && data.length == 2)
				{
					actionLabel = data[0];
					actionName = data[1];
				}
				else if(data != null) actionName = data[0];
			}

			if(actionName != null && actionLabel != null)
			{
				if(StringUtility.equals(typeValue,  "label"))
				{
					if(actionName == null && actionLabel == null) RuntimeLogger.add("");
						else RuntimeLogger.add(actionLabel);
				}
				else if(StringUtility.equals(typeValue,  "name"))
				{
					if(actionName == null && actionLabel == null) RuntimeLogger.add("");
						else RuntimeLogger.add(actionName);
				}
			}
			else next = false;
		}
	}
}
