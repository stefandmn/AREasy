package org.areasy.runtime.actions.arserver.admin;

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

import org.areasy.runtime.actions.AbstractAction;
import org.areasy.runtime.engine.RuntimeLogger;
import org.areasy.runtime.engine.base.ARDictionary;
import org.areasy.runtime.engine.base.AREasyException;
import org.areasy.runtime.engine.structures.CoreItem;
import org.areasy.runtime.engine.structures.data.itsm.foundation.SupportGroup;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Action for management of ITSM Support Groups.
 */
public class SupportGroupAdministration extends AbstractAction
{
	/**
	 * Execute the current action.
	 *
	 * @throws org.areasy.runtime.engine.base.AREasyException
	 *          if any error will occur
	 */
	public void run() throws AREasyException
	{
		String company = getConfiguration().getString("sgroupcompany", getConfiguration().getString("supportgroupcompany", null));
		String organisation = getConfiguration().getString("sgrouporganisation", getConfiguration().getString("supportgrouporganisation", null));
		String groupname = getConfiguration().getString("sgroup", getConfiguration().getString("sgroupname", getConfiguration().getString("supportgroup", getConfiguration().getString("supportgroupname", null))));
		String groupid = getConfiguration().getString("sgroupid", getConfiguration().getString("supportgroupid", null));
		String grouprole = getConfiguration().getString("role", null);
		String status = getConfiguration().getString("status", null);
		List aliases = getConfiguration().getList("alias", new ArrayList());

		SupportGroup group = new SupportGroup();
		if(company != null) group.setCompanyName(company);
		if(organisation != null) group.setOrganisationName(organisation);
		if(groupname != null) group.setSupportGroupName(groupname);
		if(groupid != null) group.setAttribute(1, groupid);
		group.read(getServerConnection());

		if(group.exists())
		{
			group.setCompanyName(company);
			group.setOrganisationName(organisation);
			if(grouprole != null) group.setRole(grouprole);
			if(status != null) group.setAttribute(7, status);

			group.update(getServerConnection());
			RuntimeLogger.add("Support group '" + groupname + "' updated");
		}
		else
		{
			group.setCompanyName(company);
			group.setOrganisationName(organisation);
			group.setSupportGroupName(groupname);
			if(grouprole != null) group.setRole(grouprole);
			if(status != null) group.setAttribute(7, status);

			group.create(getServerConnection());
			RuntimeLogger.add("Support group '" + groupname + "' has been created");
		}

		if(aliases != null && !aliases.isEmpty())
		{
			for (Object aliasObj : aliases)
			{
				String alias = (String) aliasObj;
				createAlias(group.getEntryId(), alias);
			}
		}
	}

	/**
	 * Create alias for support group. If alias exists and enableIfExists is true it is enabled.
	 * If alias exists and enableIfExists is false nothing is done.
	 * @param supportGroupId Id of support group
	 * @param alias alias
	 * @throws AREasyException when error occurs
	 */
	private void createAlias(String supportGroupId, String alias) throws AREasyException
	{
		CoreItem entry = new CoreItem();
		entry.setFormName("CTM:Support Group Alias");
		entry.setAttribute(ARDictionary.CTM_SGROUPID, supportGroupId);
		entry.setAttribute(ARDictionary.CTM_SGROUPALIAS, alias);

		entry.read(getServerConnection());

		if (entry.exists())
		{
			entry.setAttribute(7, "Enabled");
			entry.update(getServerConnection());
			RuntimeLogger.add("Support Group Alias " + alias + " enabled");
		}
		else
		{
			entry.create(getServerConnection());
			RuntimeLogger.add("Support Group Alias " + alias + " created");
		}
	}
}
