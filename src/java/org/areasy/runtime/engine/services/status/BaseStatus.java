package org.areasy.runtime.engine.services.status;

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

import org.areasy.common.logger.Logger;
import org.areasy.common.logger.LoggerFactory;

/**
 * Abstract status template to create and display a status message.
 *
 */
public abstract class BaseStatus
{
	private static Logger logger = LoggerFactory.getLog(BaseStatus.class);

	/**
	 * Dedicated method for implementation in the final status class to compose the status message
	 * @return a text message
	 */
	protected abstract String getMessage();

	/**
	 * Write status message into the global logger.
	 */
	public final void write()
	{
		logger.info(getMessage());
	}

	/**
	 * get status message for the current action (which has associated this notifier status instance)
	 *
	 * @return a status message.
	 */
	public final String getStatusMessage()
	{
		return getMessage();
	}
}
