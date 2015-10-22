package org.areasy.runtime.actions.arserver.data.cmdb;

/*
 * Copyright (c) 2007-2015 AREasy Runtime
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
 * Dedicated CMDB action to replace the relationship(s) for a specific CI with another one, defined by the parametrization.
 * Practically, this action is an automated action which is running sequentially "remove" and "create" actions.
 * The replacement is done in two steps, first will be removed all defined relationships and then will recreate them based on parametrization.
 *
 */
public class RelationshipPeopleReplaceAction extends RelationshipPeopleCreateAction
{
	/**
	 * This method execute a replacement of people relationship(s) for a specific CI.
	 * The replacement is done in two steps, first will be removed all defined relationships and then will recreate them based on parametrization.
	 * To catch the parametrization please see the parameters for actions <code>cmdb.relationship.people.remove</code> and <code>cmdb.relationship.people.create</code>
	 *
	 * @see RelationshipPeopleRemoveAction#run(org.areasy.runtime.engine.structures.data.cmdb.ConfigurationItem)
	 * @see RelationshipPeopleCreateAction# run (com.snt.areasy.utilities.structures.arserver.data.itsm.applications.cmdb.ConfigurationItem)
	 * @throws org.areasy.runtime.engine.base.AREasyException if any error will occur
	 */
	public void run(ConfigurationItem item) throws AREasyException
	{
		Map relationdatamap = null;
		boolean ignorenullvalue = getConfiguration().getBoolean("ignorenullvalues", true);
		boolean ignoreunchangedvalues = getConfiguration().getBoolean("ignoreunchangedvalues", true);

		String peoplerelationrole = getConfiguration().getString("peoplerelationrole", null);

		//set validation flags
		item.setIgnoreNullValues(ignorenullvalue);
		item.setIgnoreUnchangedValues(ignoreunchangedvalues);

		if(getConfiguration().containsKey("peoplerelationdataids") && getConfiguration().containsKey("peoplerelationdatavalues"))
		{
			List peoplerelationmapids = getConfiguration().getVector("peoplerelationdataids", new Vector());
			List peoplerelationmapvalues = getConfiguration().getVector("peoplerelationdatavalues", new Vector());

			relationdatamap = getMap(peoplerelationmapids, peoplerelationmapvalues);
		}

		boolean output = ProcessorLevel2CmdbApp.removePeopleRelationships(getServerConnection(), item, relationdatamap, peoplerelationrole);
		if(output) RuntimeLogger.debug("People relationship has been removed: " + item);

		super.run(item);
	}
}
