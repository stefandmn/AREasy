package org.areasy.runtime.actions.arserver.data.cmdb;

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
import org.areasy.runtime.engine.structures.data.cmdb.ConfigurationItem;
import org.areasy.runtime.engine.workflows.ProcessorLevel2CmdbApp;

/**
 * Dedicated CMDB runtime action to release or to reserve an asset (CI) from an inventory location.
 *
 */
public class InventoryReserveAction extends BaseConfigurationItemAction implements ConfigurationItemAction
{
	/**
	 * This method execute "In use/Reservation" action for a specific CI.
	 * This action could contains the following parametrization:
	 * <table border="1">
	 * 	<tr>
	 * 		<td nowrap width="15%">-simulation</td>
	 * 		<td width="90%">Optional parameter which specify that this execution is just a simulation and the result is not commited in the CMDB database</td>
	 * 	</tr>
	 * </table>
	 *
	 * @param item configuration item structure. If it is null will return an exception
	 * @throws AREasyException will any error occurs.
	 */
	public void run(ConfigurationItem item) throws AREasyException
	{
		boolean ignorenullvalue = getConfiguration().getBoolean("ignorenullvalues", true);
		boolean ignoreunchangedvalues = getConfiguration().getBoolean("ignoreunchangedvalues", true);

		//set validation flags
		item.setIgnoreNullValues(ignorenullvalue);
		item.setIgnoreUnchangedValues(ignoreunchangedvalues);

		boolean output = ProcessorLevel2CmdbApp.unsetLocation(getServerConnection(), item);
		if(output) RuntimeLogger.debug("CI has been reserved: " + item);
	}
}
