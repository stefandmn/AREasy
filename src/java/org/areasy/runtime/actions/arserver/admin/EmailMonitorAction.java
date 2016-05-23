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

import org.areasy.runtime.RuntimeAction;
import org.areasy.runtime.engine.RuntimeLogger;
import org.areasy.runtime.engine.RuntimeServer;
import org.areasy.runtime.engine.services.cache.CacheEntry;
import org.areasy.runtime.engine.structures.CoreItem;

import java.util.List;

/**
 * Dedicated action to monitor if server instances are working.
 *
 */
public class EmailMonitorAction extends AbstractSystemMonitorAction implements RuntimeAction
{
	/**
	 * Execute action's for monitoring.
	 *
	 * @return true of false in case of monitoring procedure observed an error or not
	 */
	protected boolean monitor()
	{
		boolean error = false;

		int initRepeator = getConfiguration().getInt("repeator", 5);
		String arserver = getConfiguration().getString("arserver", getManager().getConfiguration().getString("app.server.default.arsystem.server.name", "localhost"));

		error = checkServer(arserver);
		error = error & checkUnsentMessages(initRepeator);
		error = error & checkErrorMessages(initRepeator);

		return error;
	}

	protected boolean checkServer(String arserver)
	{
		boolean error = false;

		//check primary server
		try
		{
			setServerConnection();
			RuntimeLogger.info("Active connection for '" + arserver + "' primary AR server");
		}
		catch (Throwable th)
		{
			error = true;
			RuntimeLogger.error("Invalid connection for '" + arserver + "' primary AR server: " + th.getMessage());

			//solve error execution
			execThrowsAction(0);
		}

		return error;
	}

	protected boolean checkUnsentMessages(int initRepeator)
	{
		boolean error = false;

		//check not sent messages.
		try
		{
			CoreItem item = new CoreItem();
			item.setFormName("AR System Email Messages");
			item.setAttribute(18099, new Integer(1));

			CacheEntry cache1 = null;
			CacheEntry cache2 = null;

			Integer oldcount = new Integer(0);
			Integer repeator = new Integer(0);

			List notsent = item.search(getServerConnection());
			Integer newcount = new Integer(notsent.size());

			if (RuntimeServer.getCache().contains(getCacheId() + ".unsent.count"))
			{
				cache1 = (CacheEntry) RuntimeServer.getCache().getCacheEntry(getCacheId() + ".unsent.count");
				cache2 = (CacheEntry) RuntimeServer.getCache().getCacheEntry(getCacheId() + ".unsent.repeator");

				if (cache1 != null)
				{
					oldcount = (Integer) cache1.getContent();
					if (cache2 != null) repeator = (Integer) cache2.getContent();

					if (oldcount.intValue() > 0 && newcount.intValue() > 0 && oldcount.intValue() < newcount.intValue()) repeator = new Integer(repeator.intValue() + 1);
						else if(oldcount.intValue() > newcount.intValue()) repeator = new Integer(0);

					if (repeator.intValue() >= initRepeator)
					{
						error = true;
						RuntimeLogger.error("The number of unsent email messages is growing and now the total number of records is " + newcount + ". Please check 'AR System Email Messages' form and Email Engine service(s)");

						//solve error execution
						execThrowsAction(2);

						repeator = new Integer(0);
					}
					else RuntimeLogger.info("The total number of unsent email messages is " + newcount);
				}
			}

			logger.debug("Evaluate unsent messages: current count = " + newcount + ", previous count = " + oldcount + ", retries = " + repeator);
			RuntimeServer.getCache().add(getCacheId() + ".unsent.count", newcount);
			RuntimeServer.getCache().add(getCacheId() + ".unsent.repeator", repeator);
		}
		catch (Exception e)
		{
			getLogger().error("Error checking not sent email messages from 'AR System Email Messages' form: " + e.getMessage());
			getLogger().debug("Exception", e);

			//solve error execution
			execThrowsAction(1);
		}

		return error;
	}

	protected boolean checkErrorMessages(int initRepeator)
	{
		boolean error = false;

		//check messages with errors
		try
		{
			CoreItem item = new CoreItem();
			item.setFormName("AR System Email Messages");
			item.setAttribute(18099, new Integer(2));

			CacheEntry cache1 = null;
			CacheEntry cache2 = null;

			Integer oldcount = new Integer(0);
			Integer repeator = new Integer(0);

			List witherr = item.search(getServerConnection());
			Integer newcount = new Integer(witherr.size());

			if (RuntimeServer.getCache().contains(getCacheId() + ".witherr.count"))
			{
				cache1 = (CacheEntry) RuntimeServer.getCache().getCacheEntry(getCacheId() + ".witherr.count");
				cache2 = (CacheEntry) RuntimeServer.getCache().getCacheEntry(getCacheId() + ".witherr.repeator");

				if (cache1 != null)
				{
					oldcount = (Integer) cache1.getContent();
					if (cache2 != null) repeator = (Integer) cache2.getContent();

					if (oldcount.intValue() > 0 && newcount.intValue() > 0 && oldcount.intValue() < newcount.intValue()) repeator = new Integer(repeator.intValue() + 1);
						else if(oldcount.intValue() > newcount.intValue()) repeator = new Integer(0);

					if (repeator.intValue() >= initRepeator)
					{
						error = true;
						RuntimeLogger.error("The number of email messages with errors is growing and now the total number of this type of records is " + newcount + ". Please check 'AR System Email Messages' form and Email Engine service(s)");

						execThrowsAction(3);

						repeator = new Integer(0);
					}
					else RuntimeLogger.info("The total number of email messages with error is " + newcount);
				}
			}

			logger.debug("Evaluate error messages: current count = " + newcount + ", previous count = " + oldcount + ", retries = " + repeator);
			RuntimeServer.getCache().add(getCacheId() + ".witherr.count", newcount);
			RuntimeServer.getCache().add(getCacheId() + ".witherr.repeator", repeator);
		}
		catch (Exception e)
		{
			getLogger().error("Error checking email messages with erros from 'AR System Email Messages' form: " + e.getMessage());
			getLogger().debug("Exception", e);

			//solve error execution
			execThrowsAction(1);
		}

		return error;
	}

	/**
	 * This method should be called when an error is detected!
	 *
	 * @param event the event id which occurs and could be handled somehow.
	 */
	protected void execThrowsAction(int event)
	{
		//nothing to do here
	}

	/**
	 * Get notification subject
	 *
	 * @return notification subject
	 */
	protected String getDefaultMessageSubject()
	{
		return "Email Engine Monitoring Alert";
	}

	/**
	 * Get a help text about syntaxt execution of the current action.
	 *
	 * @return text message specifying the syntaxt of the current action
	 */
	public String help()
	{
		return "[-arserver <remedy server>] [-repeator <number>]";
	}
}
