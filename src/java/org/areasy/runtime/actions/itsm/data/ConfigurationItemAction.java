package org.areasy.runtime.actions.itsm.data;

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

import org.areasy.runtime.actions.ars.data.CoreData;
import org.areasy.runtime.engine.base.AREasyException;
import org.areasy.runtime.engine.structures.data.cmdb.ConfigurationItem;

/**
 * This is a template action which permits to execute usual tasks and specific operation to manage one specific CI.
 * The difference between standard action is that this library expose an additional method to have possibility to
 * define workflows, executing a chain of actions (which are instance of this interface) for a clear identified asset (CI).
 */
public interface ConfigurationItemAction extends CoreData
{
	/**
	 * Execute an action for a specific CI. This CI must be identified previously and then the method
	 * could be called. This method will used by standard actions which implement an workflow using these type of action
	 * which permit single change or update.
	 *
	 * @param item configuration item structure, which should exist in CMDB databse.
	 * @throws AREasyException if any error will occur.
	 */
	void run(ConfigurationItem item) throws AREasyException;
}
