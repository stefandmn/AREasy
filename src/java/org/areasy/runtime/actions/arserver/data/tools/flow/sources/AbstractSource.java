package org.areasy.runtime.actions.arserver.data.tools.flow.sources;

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

import org.areasy.runtime.actions.PatternAction;
import org.areasy.runtime.engine.base.AREasyException;
import org.areasy.runtime.engine.structures.CoreItem;

import java.util.List;
import java.util.Map;

/**
 * This class is the data source table that publish all common functionalities for all data-source implemented in Advanced Automation.
 */
public abstract class AbstractSource
{
	private CoreItem item = null;
	private PatternAction action = null;

	/**
	 * Dedicated method that has to be used internally, to set and validate the data-source configuration (<code>CoreItem</code> structure)
	 *
	 * @throws AREasyException in case of any error will occur
	 */
	public abstract void init() throws AREasyException;

	/**
	 * Dedicated method to release resources that are used by a data-source
	 *
	 * @throws AREasyException in case of any error will occur
	 */
	public abstract void release() throws AREasyException;

	/**
	 * Get data-source configuration structure.
	 *
	 * @return <code>CoreItem</code> data-source configuration structure
	 */
	public final CoreItem getSourceItem()
	{
		return this.item;
	}

	/**
	 * Get Advanced Automation caller.
	 *
	 * @return <code>AdvancedAutomationAction</code> caller instance
	 */
	public final PatternAction getAction()
	{
		return this.action;
	}

	/**
	 * Set data source <Code>CoreItem</Code> structure. Actually, this entity is set by the main caller
	 * (<code>AdvancedAutomationAction</code> action)
	 *
	 * @param item <code>CoreItem</code> structure
	 */
	public final void setSourceItem(CoreItem item)
	{
		this.item = item;
	}

	/**
	 * Set caller action
	 *
	 * @param action <code>AdvancedAutomationAction</code> action instance
	 */
	public final void setAction(PatternAction action)
	{
		this.action = action;
	}

	/**
	 * Take and deliver through a <code>Map</code> structure the data headers from the selected data-source.
	 *
	 * @return a <code>Map</code> with data-source headers.
	 * @throws AREasyException in case of any error will occur
	 */
	public abstract Map getHeaders() throws AREasyException;

	/**
	 * Take and deliver through a <code>Map</code> structure the data read it from
	 * the selected data-source. If the output is null means that the data-source goes to the end.
	 *
	 * @param list this is the list of data source keys.
	 * @return a <code>Map</code> having data source indexes as keys and data as values.
	 * @throws AREasyException in case of any error will occur
	 */
	public abstract Map getNextObject(List list) throws AREasyException;

	/**
	 * Read and return the total number of records found in the data-source.
	 *
	 * @return number of records found
	 */
	public abstract int getDataCount();

	/**
	 * Returns a string representation of the object. In general, the
     * <code>toString</code> method returns a string that
     * "textually represents" this object.
	 *
	 * @return  a string representation of the object.
	 */
	public String toString()
	{
		if(item != null) return item.getStringAttributeValue(536870981) + " (" + item.getStringAttributeValue(179) + ")";
			else return super.toString();
	}
}
