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

import org.areasy.runtime.engine.RuntimeLogger;
import org.areasy.runtime.engine.base.AREasyException;
import org.areasy.runtime.engine.structures.data.cmdb.ConfigurationItem;
import org.areasy.runtime.engine.workflows.ProcessorLevel2CmdbApp;

/**
 * Dedicated CMDB runtime action to register an asset (CI) into a specific inventory location.
 *
 */
public class InventoryRegisterAction extends BaseConfigurationItemAction implements ConfigurationItemAction
{
	/**
	 * This method execute the "Registration" action for a specific CI into an invetory location specified the parameterization
	 * of this action. The parametrization could be:
	 * <table border="1">
	 * 	<tr>
	 * 		<td nowrap width="15%"><b>-inventoryrelationlocation</b></td>
	 * 		<td width="90%">Inventory location name to identity inventory location CI and to perform the relationhsip with the specified CI. If it is null
	 * 			the inventory location will be considered the value found in the CI for attribute <code>Site</code> only if
	 * 			<code>-synchronize</code> parameter is specified</td>
	 * 	</tr>
	 * </table>
	 *
	 * @param item configuration item structure. If it is null will return an exception
	 * @throws org.areasy.runtime.engine.base.AREasyException will any error occurs.
	 */
	public void run(ConfigurationItem item) throws AREasyException
	{
		boolean ignorenullvalue = getConfiguration().getBoolean("ignorenullvalues", true);
		boolean ignoreunchangedvalues = getConfiguration().getBoolean("ignoreunchangedvalues", true);
		String inventoryrelationlocation = getConfiguration().getString("inventoryrelationlocation", null);

		//set validation flags
		item.setIgnoreNullValues(ignorenullvalue);
		item.setIgnoreUnchangedValues(ignoreunchangedvalues);

		boolean output = ProcessorLevel2CmdbApp.setInventoryLocation(getServerConnection(), item, inventoryrelationlocation);
		if(output) RuntimeLogger.debug("CI has been registered in '" + inventoryrelationlocation + "' inventory location: " + item);
	}
}
