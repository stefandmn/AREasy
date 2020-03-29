package org.areasy.runtime.actions.itsm.data;

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

import org.areasy.runtime.engine.RuntimeLogger;
import org.areasy.runtime.engine.base.AREasyException;
import org.areasy.runtime.engine.structures.data.cmdb.ConfigurationItem;
import org.areasy.runtime.engine.workflows.ProcessorLevel2CmdbApp;

/**
 * Dedicated CMDB runtime action to relocate an asset (CI) into another inventory location than is registered now.
 * Using a special configuration the relocation procedure could be force even if the identified CI is not registered
 * into an inventory location. For more details please check action parameterization.
 *
 */
public class InventoryRelocateAction extends BaseConfigurationItemAction implements ConfigurationItemAction
{
	/**
	 * This method execute a "Relocation" action for a specific CI.
	 * This action could contain the following parametrization:
	 * <table border="1">
	 * 	<tr>
	 * 		<td><b>-inventoryrelationlocation</b></td>
	 * 		<td width="90%">Inventory location name to identity inventory location CI which will be used for relation and to perform the relationship with the specified CI.
	 * 			If it is null the inventory location will be considered the value found in the CI for attribute <code>Site</code> only if
	 * 			<code>-synchronize</code> parameter is specified</td>
	 * 	</tr>
	 * 	<tr>
	 * 		<td nowrap width="15%"><b>-forcing</b></td>
	 * 		<td width="90%">If the specified CI is not found into an inventory location this parameter will continue the process forcing a clean registration</td>
	 * 	</tr>
	 * </table>
	 *
	 * @param item configuration item structure. If it is null will return an exception
	 * @throws org.areasy.runtime.engine.base.AREasyException will any error occurs.
	 */
	public void run(ConfigurationItem item) throws AREasyException
	{
		boolean force = getConfiguration().getBoolean("force", false);
		boolean ignorenullvalue = getConfiguration().getBoolean("ignorenullvalues", true);
		boolean ignoreunchangedvalues = getConfiguration().getBoolean("ignoreunchangedvalues", true);
		String inventoryrelationlocation = getConfiguration().getString("inventoryrelationlocation", null);

		//set validation flags
		item.setIgnoreNullValues(ignorenullvalue);
		item.setIgnoreUnchangedValues(ignoreunchangedvalues);

		try
		{
			//make reservation
			ProcessorLevel2CmdbApp.unsetLocation(getServerConnection(), item);
		}
		catch(AREasyException are)
		{
			if(force) RuntimeLogger.warn("Reservation failed: " + are.getMessage());
				else throw are;
		}

		//make registration
		boolean output = ProcessorLevel2CmdbApp.setInventoryLocation(getServerConnection(), item, inventoryrelationlocation);
		if(output) RuntimeLogger.debug("CI has been registered in '" + inventoryrelationlocation + "' inventory location: " + item);
	}
}
