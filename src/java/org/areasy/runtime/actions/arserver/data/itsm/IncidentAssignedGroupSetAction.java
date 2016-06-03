package org.areasy.runtime.actions.arserver.data.itsm;

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

import com.bmc.arsys.api.Constants;
import org.areasy.runtime.actions.AbstractAction;
import org.areasy.runtime.engine.RuntimeLogger;
import org.areasy.runtime.engine.base.AREasyException;
import org.areasy.runtime.engine.structures.data.itsm.Incident;
import org.areasy.runtime.engine.structures.data.itsm.foundation.SupportGroup;

/**
 * Change assigned group of one incident using merge operation. Assigned person is left unchanged.
 * Merge operation is used to update the incident in order to prevent triggering the application workflow (status changes, notifications etc.).
 *
 */
public class IncidentAssignedGroupSetAction extends AbstractAction
{
	/**
	 * Execute the current action.
	 *
	 * @throws org.areasy.runtime.engine.base.AREasyException
	 *          if any error will occur
	 */
	public void run() throws AREasyException
	{
		String incidentNumber = getConfiguration().getString("id");
		String toGroup = getConfiguration().getString("group");
		String toOrg = getConfiguration().getString("org");
		String toCompany = getConfiguration().getString("company");

		Incident incident = new Incident();
		incident.setIncidentNumber(incidentNumber);
		incident.read(getServerConnection());

		if (!incident.exists())
		{
			throw new AREasyException("Incident "+incidentNumber+" not found");
		}

		SupportGroup sgroup = new SupportGroup();
		sgroup.setCompanyName(toCompany);
		sgroup.setOrganisationName(toOrg);
		sgroup.setSupportGroupName(toGroup);

		sgroup.read(getServerConnection());

		if (!sgroup.exists())
		{
			throw new AREasyException("Support Group " + toCompany + "/" + toOrg + "/" + toGroup + " not found");
		}

		incident.setAssignmentCompany(toCompany);
		incident.setAssignmentOrganisation(toOrg);
		incident.setAssignmentGroup(toGroup);
		incident.setAssignmentGroupId(sgroup.getEntryId());

		incident.merge(getServerConnection(), Constants.AR_MERGE_ENTRY_DUP_MERGE);

		RuntimeLogger.add("Assigned Group of incident " + incidentNumber + " has been set to " + toGroup);

	}
}
