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

import org.areasy.runtime.RuntimeAction;
import org.areasy.runtime.actions.ars.admin.AbstractSystemMonitor;

/**
 * Dedicated action to monitor if server instances are working.
 * todo - implement me
 *
 */
public class MidtierMonitorAction extends AbstractSystemMonitor implements RuntimeAction
{
	/**
	 * Execute action's for monitoring.
	 *
	 * @return true of false in case of monitoring procedure observed an error or not
	 */
	protected boolean monitor()
	{
		return false;
	}

	/**
	 * Get notification subject
	 *
	 * @return notification subject
	 */
	protected String getDefaultMessageSubject()
	{
		return "MidTier Monitoring Alert";
	}
}
