package org.areasy.runtime.engine.structures.data.cmdb.units;

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

import com.bmc.arsys.api.Entry;
import org.areasy.runtime.engine.RuntimeLogger;
import org.areasy.runtime.engine.base.AREasyException;
import org.areasy.runtime.engine.base.ServerConnection;
import org.areasy.runtime.engine.structures.CoreItem;
import org.areasy.runtime.engine.structures.data.itsm.foundation.Organisation;
import org.areasy.runtime.engine.structures.data.itsm.foundation.People;
import org.areasy.runtime.engine.structures.data.itsm.foundation.SupportGroup;
import org.areasy.runtime.engine.workflows.ProcessorLevel1Context;
import org.areasy.common.data.NumberUtility;
import org.areasy.common.data.StringUtility;

import java.util.List;

/**
 * This class manages the CI relationships.
 */
public class AssetPeople extends CoreItem
{
	private CoreItem sgroup = null;
	private People people = null;
	private SupportGroup support = null;
	private Organisation organisation = null;
	private CoreItem sourceConfigurationItem = null;

	/**
	 * Default CI relationship instance.
	 */
	public AssetPeople()
	{
		//nothing to do here
		super();
		setFormName("AST:AssetPeople");
	}

	/**
	 * Create a new instance of core item structure.
	 *
	 * @return new instance of <code>CoreItem</code> structure
	 */
	public CoreItem getInstance()
	{
		return new AssetPeople();
	}

	public List searchAllRelationships(ServerConnection arsession, String reconciliationInstanceId) throws AREasyException
	{
		String qualification = "'301104100' = \"" + reconciliationInstanceId + "\" AND '7' != 1";

		return search(arsession, qualification);
	}

	protected void fetch(ServerConnection arsession, Entry entry) throws AREasyException
	{
		super.fetch(arsession, entry);

		String type = getStringAttributeValue(260100013);
		String cirelid = getStringAttributeValue(301104100);

		//discover source CI
		sourceConfigurationItem = new CoreItem();
		sourceConfigurationItem.setFormName(ProcessorLevel1Context.FORM_BASEELEMENT);
		sourceConfigurationItem.setAttribute(400129200, cirelid);
		sourceConfigurationItem.read(arsession);

		//discover the target entity
		if(StringUtility.equalsIgnoreCase(type, "People"))
		{
			String requestId = getStringAttributeValue(260100006);
			this.people = new People();

			this.people.setAttribute(1, requestId);
			this.people.read(arsession);

			if(!this.people.exists())
			{
				String instanceId = getStringAttributeValue(301104200);

				this.people.clear();
				this.people.setAttribute(179, instanceId);
				this.people.read(arsession);

				if(!this.people.exists()) RuntimeLogger.warn("People structure could not be identified using the following details: Person ID = '" + requestId + "', Instance Id = " + instanceId);
			}
		}
		else if(StringUtility.equalsIgnoreCase(type, "Support Group"))
		{
			String groupId = getStringAttributeValue(260100011);

			if(groupId == null)
			{
				String requestId = getStringAttributeValue(260100006);

				if(requestId != null)
				{
					this.support = new SupportGroup();

					this.support.clear();
					this.support.setAttribute(1, requestId);
					this.support.read(arsession);

					if(!this.support.exists())
					{
						String instanceId = getStringAttributeValue(301104200);

						this.support.clear();
						this.support.setAttribute(179, instanceId);
						this.support.read(arsession);

						if(!this.support.exists()) RuntimeLogger.warn("Support Group structure could not be identified using the following details: Support Group ID = '" + requestId + "', Instance Id = " + instanceId);
					}
				}
			}
			else
			{
				this.sgroup = new CoreItem();
				this.sgroup.setFormName("Group");
				this.sgroup.setAttribute(106, NumberUtility.toInt(groupId));
				this.sgroup.read(arsession);

				if(!this.sgroup.exists()) RuntimeLogger.warn("System Group structure could not be identified using the following details: Group ID = '" + groupId + "', Name = " + getStringAttributeValue(260100003));
			}
		}
		else if(StringUtility.equalsIgnoreCase(type, "People Organization"))
		{
			String requestId = getStringAttributeValue(260100006);
			this.organisation = new Organisation();

			this.organisation.setAttribute(1, requestId);
			this.organisation.read(arsession);

			if(!this.organisation.exists())
			{
				String instanceId = getStringAttributeValue(301104200);

				this.organisation.clear();
				this.organisation.setAttribute(179, instanceId);
				this.organisation.read(arsession);

				if(!this.organisation.exists()) RuntimeLogger.warn("People Organisation structure could not be identified using the following details: Person ID = '" + requestId + "', Instance Id = " + instanceId);
			}
		}
		else throw new AREasyException("Unknown relationship entity: " + type);
	}

	public People getPeopleEntity()
	{
		return this.people;
	}

	public Organisation getOrganisationEntity()
	{
		return this.organisation;
	}

	public SupportGroup getSupportGroupEntity()
	{
		return this.support;
	}

	public CoreItem getSourceConfigurationItem()
	{
		return sourceConfigurationItem;
	}

	public CoreItem getSystemGroup()
	{
		return sgroup;
	}
}
