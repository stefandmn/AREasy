package org.areasy.runtime.actions.arserver.admin;

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
 * Action for creation of new ITSM Support Groups.
 */
public class SupportGroupSetAction extends AbstractAction
{
	/**
	 * Execute the current action.
	 *
	 * @throws org.areasy.runtime.engine.base.AREasyException
	 *          if any error will occur
	 */
	public void run() throws AREasyException
	{
		String name = getConfiguration().getString("groupname");
		String company = getConfiguration().getString("company");
		String organisation = getConfiguration().getString("organisation");
		String role = getConfiguration().getString("role");
		String status = getConfiguration().getString("status", null);
		List aliases = getConfiguration().getList("alias", new ArrayList());

		boolean enableIfExist = getConfiguration().getBoolean("enable",false);
		boolean updateIfExist = getConfiguration().getBoolean("update",false);

		SupportGroup group = new SupportGroup();
		group.setSupportGroupName(name);
		group.read(getServerConnection());

		if (group.exists())
		{
			if (enableIfExist)
			{
				group.setAttribute(7, "Enabled");
			}

			if (updateIfExist)
			{
				group.setCompanyName(company);
				group.setOrganisationName(organisation);
				group.setRole(role);
				if(status != null) group.setAttribute(7, status);
			}

			if (enableIfExist || updateIfExist) group.update(getServerConnection());

			if (enableIfExist && !updateIfExist) RuntimeLogger.add("Support group '" + name + "' enabled");
				else if (!enableIfExist && updateIfExist) RuntimeLogger.add("Support group '" + name + "' updated");
					else if (enableIfExist && updateIfExist) RuntimeLogger.add("Support group '" + name + "' enabled and updated");
						else RuntimeLogger.warn("Support group '" + name + "' already exists");
		}
		else
		{
			group.setCompanyName(company);
			group.setOrganisationName(organisation);
			group.setSupportGroupName(name);
			group.setRole(role);
			if(status != null) group.setAttribute(7, status);

			group.create(getServerConnection());
			RuntimeLogger.add("Support group '" + name + "' has been created");
		}

		if (updateIfExist || enableIfExist)
		{
			for (Iterator i=aliases.iterator(); i.hasNext();)
			{
				String alias = (String) i.next();
				createAlias(group.getEntryId(), alias, enableIfExist);
			}
		}
	}

	/**
	 * Create alias for support group. If alias exists and enableIfExists is true it is enabled.
	 * If alias exists and enableIfExists is false nothing is done.
	 * @param supportGroupId Id of support group
	 * @param alias alias
	 * @param enableIfExist true if existing alias should be enabled; false otherwise
	 * @throws AREasyException when error occurs
	 */
	private void createAlias(String supportGroupId, String alias, boolean enableIfExist) throws AREasyException
	{
		CoreItem ci = new CoreItem();
		ci.setFormName("CTM:Support Group Alias");
		ci.setAttribute(ARDictionary.CTM_SGROUPID, supportGroupId);
		ci.setAttribute(ARDictionary.CTM_SGROUPALIAS, alias);

		ci.read(getServerConnection());

		if (ci.exists())
		{
			if (enableIfExist)
			{
				ci.setAttribute(7, "Enabled");
				ci.update(getServerConnection());
				RuntimeLogger.add("Support Group Alias " + alias + " enabled");
			}
		}
		else
		{
			ci.create(getServerConnection());
			RuntimeLogger.add("Support Group Alias " + alias + " created");
		}

	}

	/**
	 * Get a help text about syntax execution of the current action.
	 *
	 * @return text message specifying the syntax of the current action
	 */
	public String help()
	{
		return "-groupname <group name> -company <support company> -organisation <support organisation> -role <Help Desk|Tier 1|Tier 2|Tier 3> [-enable] [-update] [-alias <list of aliases>]";
	}
}
