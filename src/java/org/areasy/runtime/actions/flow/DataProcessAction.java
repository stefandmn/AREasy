package org.areasy.runtime.actions.flow;

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
import org.areasy.runtime.actions.flow.events.AbstractEvent;
import org.areasy.runtime.actions.flow.sources.AbstractSource;
import org.areasy.runtime.engine.base.AREasyException;

import java.lang.reflect.Constructor;
import java.util.Hashtable;
import java.util.Map;

/**
 * This is an Advanced Automation class that is called from Remedy GUI to import data according to the configuration
 * managed by Remedy forms from AAR application.
 */
public class DataProcessAction extends FlowPatternAction
{
	private static Map events = new Hashtable();

	static
	{
		events.put("readheaders", "org.areasy.runtime.actions.process.events.ReadHeadersEvent");
		events.put("actionlist", "org.areasy.runtime.actions.process.events.ReadActionlistEvent");
		events.put("runworkflow", "org.areasy.runtime.actions.process.events.RunWorkflowEvent");
		events.put("loadmetadata", "org.areasy.runtime.actions.process.events.LoadMetadataEvent");
		events.put("runscheduler", "org.areasy.runtime.actions.process.events.RunSchedulerEvent");
		events.put("runautomap", "org.areasy.runtime.actions.process.events.RunAutomapEvent");
		events.put("runjobexport", "org.areasy.runtime.actions.process.events.RunJobExportEvent");
		events.put("runjobimport", "org.areasy.runtime.actions.process.events.RunJobImportEvent");
	}

	/**
	 * Execute the current action.
	 *
	 * @throws org.areasy.runtime.engine.base.AREasyException
	 * if any error will occur
	 */
	public void run() throws AREasyException
    {
		AbstractEvent event = null;

		//start cron
		getCron().start();

		try
		{
			event = getEvent(this);
			event.execute();
		}
		finally
		{
			//stop cron
			getCron().stop();

			//release data source events
			if(event != null && event.getSource() != null)
			{
				event.getSource().release();
				logger.debug("Event data source has been released");
			}
		}
    }

	/**
	 * Get data import execution event.
	 *
	 * @param action <code>AdvancedAutomationAction</code>action name (registered in the configuration sector)
	 * @return a final data import event having signature <code>AbstractEvent</code> structure
	 * @throws org.areasy.runtime.engine.base.AREasyException if any error occurs
	 */
	protected final AbstractEvent getEvent(FlowPatternAction action) throws AREasyException
	{
		AbstractEvent event = null;

		if(action == null) throw new AREasyException("Runtime action is null");

		String eventName = action.getConfiguration().getString("event", null);

		if(StringUtility.isNotEmpty(eventName))
		{
			String sourceClassName = (String) events.get(eventName);
			if(sourceClassName == null) throw new AREasyException("Data import event instance '" + eventName  + "' is not registered");

			try
			{
				Class sourceClass = Class.forName(sourceClassName);
				Constructor contructor = sourceClass.getConstructor(null);

				//get event instance
				event = (AbstractEvent) contructor.newInstance(null);
				event.setAction(action);

				//get data source and initialize it
				AbstractSource source = getSource();

				//set data source in the event structure
				if(source != null)
				{
					source.init();
					event.setSource(source);
				}
			}
			catch(Throwable th)
			{
				throw new AREasyException("Data import event ('" + eventName + "') initialization error: " + th.getMessage(), th);
			}
		}
		else throw new AREasyException("Data import event is null");

		return event;
	}
}
