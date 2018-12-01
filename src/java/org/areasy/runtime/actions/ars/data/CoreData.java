package org.areasy.runtime.actions.ars.data;

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
import org.areasy.runtime.engine.base.AREasyException;
import org.areasy.runtime.engine.structures.CoreItem;

import java.util.Map;

/**
 * This is a template action which permits to execute usual tasks and specific operation to manage one specific <code>CoreItem</code> structure.
 * The difference between standard action is that this library expose an additional method to have possibility to
 * define workflows, executing a chain of actions (which are instance of this interface) for a specific data structure.
 */
public interface CoreData extends RuntimeAction
{
	/**
	 * Execute an action for a specific <code>CoreItem</code>. This item must be identified previously and then the method
	 * could be called. This method will used by standard actions which implement an workflow using these type of action
	 * which permit single change or update.
	 *
	 * @param item <code>CoreItem</code> structure, which should be instantiated.
	 * @throws org.areasy.runtime.engine.base.AREasyException if any error will occur.
	 */
	void run(CoreItem item) throws AREasyException;

	/**
	 * Collect data input parameters and publish them into a <code>CoreItem</code> structure.
	 *
	 * @param entry <code>CoreItem</code> structure.
	 * @return true if at least one corresponding configuration data has been used to map a <code>CoreItem</code> attribute.
	 * @throws AREasyException is any error will occur
	 */
	boolean setDataFields(CoreItem entry) throws AREasyException;

	/**
	 * Collect query input parameters and publish them into a <code>CoreItem</code> structure.
	 *
	 * @param entry <code>CoreItem</code> structure.
	 * @return true if at least one corresponding configuration data has been used to map a <code>CoreItem</code> attribute.
	 * @throws AREasyException is any error will occur
	 */
	boolean setQueryFields(CoreItem entry) throws AREasyException;

	/**
	 * Collect data input parameters for a base structure and publish them into a <code>Map</code> structure.
	 *
	 * @return a <code>Map</code> structure.
	 * @throws AREasyException is any error will occur
	 */
	Map getDataFields() throws AREasyException;

	/**
	 * Collect query input parameters for a base structure and publish them into a <code>Map</code> structure.
	 *
	 * @return a <code>Map</code> structure.
	 * @throws AREasyException is any error will occur
	 */
	Map getQueryFields() throws AREasyException;

	/**
	 * Collect query input parameters for a multipart structure and publish them into a <code>CoreItem</code> structure.
	 *
	 * @param entry <code>CoreItem</code> structure.
	 * @return true if at least one corresponding configuration data has been used to map a <code>CoreItem</code> attribute.
	 * @throws AREasyException is any error will occur
	 */
	boolean setMultiPartQueryFields(CoreItem entry) throws AREasyException;

	/**
	 * Collect query input parameters for a multipart structure and publish them into a <code>Map</code> structure.
	 *
	 * @return a <code>Map</code> structure.
	 * @throws AREasyException is any error will occur
	 */
	Map getMultiPartQueryFields() throws AREasyException;

	/**
	 * Collect data input parameters for a multipart structure and publish them into a <code>CoreItem</code> structure.
	 *
	 * @param entry <code>CoreItem</code> structure.
	 * @return true if at least one corresponding configuration data has been used to map a <code>CoreItem</code> attribute.
	 * @throws AREasyException is any error will occur
	 */
	boolean setMultiPartDataFields(CoreItem entry) throws AREasyException;

	/**
	 * Collect data input parameters for a multipart structure and publish them into a <code>Map</code> structure.
	 *
	 * @return a <code>Map</code> structure.
	 * @throws AREasyException is any error will occur
	 */
	Map getMultiPartDataFields() throws AREasyException;
}
