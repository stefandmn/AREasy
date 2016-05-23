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

import java.util.List;
import java.util.Map;
import java.util.Vector;

/**
 *  Dedicated CMDB action to remove people relationship(s) for a specific CI.
 *
 */
public class RelationshipPeopleRemoveAction extends BaseConfigurationItemAction implements ConfigurationItemAction
{
	/**
	 * This method could create a relationship between a group or a person and the specified CI. The parameterization could be:
	 * <table border="1">
	 * 	<tr>
	 * 		<td nowrap width="15%">-simulation</td>
	 * 		<td width="90%">Optional parameter which specify that this execution is just a simulation and the result is not ccommitted in the CMDB database</td>
	 * 	</tr>
	 * 	<tr>
	 * 		<td nowrap width="15%"><b>-peoplerelationrole</b></td>
	 * 		<td width="90%">Specify the role value for indicated entity type. The possible values are: "<code>approved by<code>", "<code>created by</code>", "<code>managed by</code>",
	 * 			"<code>owned by</code>", "<code>supported by</code>" and "</code>used by</code>".If this parameter will not be specified the action will consider an implicit
	 * 			role which is "</code>used by</code>".</td>
	 * 	</tr>
	 * </table>
	 *
	 * @param item configuration item structure. If it is null will return an exception
	 * @throws org.areasy.runtime.engine.base.AREasyException will any error occurs.
	 */
	public void run(ConfigurationItem item) throws AREasyException
	{
		Map relationdatamap = null;
		boolean ignorenullvalue = getConfiguration().getBoolean("ignorenullvalues", true);
		boolean ignoreunchangedvalues = getConfiguration().getBoolean("ignoreunchangedvalues", true);

		String peoplerelationrole = getConfiguration().getString("peoplerelationrole", null);

		if(getConfiguration().containsKey("peoplerelationdataids") && getConfiguration().containsKey("peoplerelationdatavalues"))
		{
			List peoplerelationmapids = getConfiguration().getVector("peoplerelationdataids", new Vector());
			List peoplerelationmapvalues = getConfiguration().getVector("peoplerelationdatavalues", new Vector());

			relationdatamap = getMap(peoplerelationmapids, peoplerelationmapvalues);
		}

		//set validation flags
		item.setIgnoreNullValues(ignorenullvalue);
		item.setIgnoreUnchangedValues(ignoreunchangedvalues);

		boolean ouptut = ProcessorLevel2CmdbApp.removePeopleRelationships(getServerConnection(), item, relationdatamap, peoplerelationrole);
		if(ouptut) RuntimeLogger.debug("People relationship has been removed: " + item);
	}
}
