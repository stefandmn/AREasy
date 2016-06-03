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

import org.areasy.runtime.actions.arserver.data.tools.flow.FlowPatternAction;
import org.areasy.runtime.actions.arserver.data.tools.flow.sources.AbstractSource;
import org.areasy.runtime.engine.base.ARDictionary;
import org.areasy.runtime.engine.base.AREasyException;
import org.areasy.common.logger.Logger;
import org.areasy.common.logger.LoggerFactory;

/**
 * Because <code>MappedDataImportAction</code> should deliver many types of content the executions blocks are segregated in separate
 * classes called <code>Event</code>s. This class is the abstract model of an event and also the event loaded called
 * by the <code>called</code> (<code>MappedDataImportAction</code> action).
 */
public abstract class AbstractEvent implements ARDictionary
{
	/** Library logger */
	protected static Logger logger = LoggerFactory.getLog(AbstractEvent.class);

	/** Caller action instance */
	private FlowPatternAction action = null;

	/** Associated data source */
	private AbstractSource source = null;

	/**
	 * Execute event
	 *
	 * @throws AREasyException if any error occurs
	 */
	public abstract void execute() throws AREasyException;

	/**
	 * Get caller action
	 *
	 * @return <code>AdvancedAutomationAction</code> action instance
	 */
	public final FlowPatternAction getAction()
	{
		return action;
	}

	/**
	 * Set caller action
	 *
	 * @param action <code>AdvancedAutomationAction</code> action instance
	 */
	public final void setAction(FlowPatternAction action)
	{
		this.action = action;
	}

	/**
	 * Get related data source.
	 *
	 * @return <code>AbstractSource</code> data source signature
	 */
	public final AbstractSource getSource()
	{
		return source;
	}

	/**
	 * Set related data source. This method called by the main action, so it is not necessary to rediscovery the action initial data source.
	 *
	 * @param source <code>AbstractSource</code> data source signature
	 */
	public final void setSource(AbstractSource source)
	{
		this.source = source;
	}
}
