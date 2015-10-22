package org.areasy.runtime.engine.structures.hybrid.itsm.foundation;

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

import org.areasy.common.data.StringUtility;
import org.areasy.common.support.configuration.Configuration;
import org.areasy.runtime.actions.PatternAction;
import org.areasy.runtime.engine.RuntimeLogger;
import org.areasy.runtime.engine.base.AREasyException;
import org.areasy.runtime.engine.base.ServerConnection;
import org.areasy.runtime.engine.structures.CoreItem;
import org.areasy.runtime.engine.structures.data.itsm.foundation.Company;
import org.areasy.runtime.engine.structures.hybrid.AbstractHybrid;
import org.areasy.runtime.engine.structures.hybrid.HybridCoreItem;

import java.util.*;

/**
 * This is a data structure who manages <Code>Company</code> entities that includes all related sub-structures,
 * and each transaction of the main entity will handle also the parts.
 */
public class HybridCompany extends Company implements AbstractHybrid
{
	private Configuration config = null;

	public HybridCompany()
	{
		super();
	}

	/**
	 * Create a new <code>HybridCompany</code> structure.
	 *
	 * @return new People instance
	 */
	public HybridCompany getInstance()
	{
		return new HybridCompany();
	}

	public void relate(ServerConnection arsession, Configuration config) throws AREasyException
	{
		//read secondary structures.
		if(exists())
		{
			if(config.getBoolean("alias", true))
			{
				CoreItem aliases = new CoreItem();
				aliases.setAttribute(1000000072, getEntryId());
				List<CoreItem> list = aliases.search(arsession);

				for(int i = 0; list != null && i < list.size(); i++)
				{
					CoreItem item = list.get(i);

					addPartWithPrefix("alias", item);
					addPartKeyPairWithPrefix("alias", item, 1000000072, 1);
				}
			}

			if(config.getBoolean("permission", true))
			{
				//read permissions
				CoreItem permission = new CoreItem();
				permission.setFormName("CTM:SYS-Access Permission Grps");
				permission.setAttribute(1000000001, getCompanyName());
				permission.setAttribute(1000003965, getAttributeValue(1000003965));
				permission.setAttribute(1000004045, getAttributeValue(1000004045));
				permission.read(arsession);

				if(permission.exists())
				{
					addPart("permission", permission);
					addPartKeyPair("permission", 1000000001, 1000000001);
					addPartKeyPair("permission", 1000003965, 1000003965);
					addPartKeyPair("permission", 1000004045, 1000004045);

					if(config.getBoolean("group", true))
					{
						CoreItem sysgroup = new CoreItem();
						sysgroup.setFormName("Group");
						sysgroup.setAttribute(106, permission.getAttributeValue(1000001579));
						sysgroup.read(arsession);

						if(sysgroup.exists())
						{
							addPart("group", sysgroup);
							addPartKeyPair("group", 8, 1000000001);
						}
					}
				}
			}
		}
	}

	/**
	 * Method to generate target data structure.
	 *
	 * @param target structure to be populated
	 * @param map data mapping between source and target
	 * @param config runtime configuration or more specifically, multipart configuration
	 * @param arsession source connection instance
	 */
	public void transfer(CoreItem target, Map<Integer,Object> map, ServerConnection arsession, Configuration config) throws AREasyException
	{
		HybridCoreItem.setTarget(target, this, map, arsession);

		//set-up all related sub-structures
		if(target instanceof HybridCompany)
		{
			HybridCompany sgroup = (HybridCompany)target;
			sgroup.setMultiPart(this);

			//store configuration
			sgroup.config = config;
		}
	}

	/**
	 * Commit part item instances based on specified attributes.
	 *
	 * @param arsession user session
	 * @throws AREasyException if any error will occur
	 */
	protected void commit(ServerConnection arsession) throws AREasyException
	{
		if(exists())
		{
			//make sync between parts and base structure
			synch();

			Iterator iterator = getPartInstances();

			while(iterator != null && iterator.hasNext())
			{
				CoreItem item = (CoreItem) iterator.next();

				try
				{
					item.readByNonCoreFloating(arsession);

					if(item.exists())
					{
						item.update(arsession);
					}
					else
					{
						item.resetEntryId();
						if(item.getAttribute(1) != null) item.deleteAttribute(1);

						item.setChanged();
						item.create(arsession);
					}
				}
				catch(Throwable th)
				{
					String message = "Error during execution of part commit action: " + th.getMessage();
					message += ". Part " + item;
					message += ". " + this;

					RuntimeLogger.warn(message);
					logger.debug("Exception", th);
				}
			}
		}
	}

	/**
	 * Create an entry record in the ARS server using an attribute's collection and then is read it
	 * to transform it into a valid core item instance.
	 *
	 * @param arsession user session
	 * @param collection attribute's collection
	 * @throws AREasyException if any error will occur
	 */
	protected void create(ServerConnection arsession, Collection collection) throws AREasyException
	{
		super.create(arsession, collection);

		//commit part items
		commit(arsession);
	}

	/**
	 * Update the current core item instance based on specified attributes.
	 *
	 * @param arsession user session
	 * @param collection attribute's collection
	 * @throws AREasyException if any error will occur
	 */
	protected void update(ServerConnection arsession, Collection collection) throws AREasyException
	{
		super.update(arsession, collection);

		//commit part items
		commit(arsession);
	}

	/**
	 * Merge an entry record in the ARS server using an attribute's collection and then is read it
	 * to transform it into a valid core item instance.
	 *
	 * @param arsession user session
	 * @param collection attribute's collection
	 * @throws AREasyException if any error will occur
	 * @param nMergeType merge type for conflicts
	 */
	protected void merge(ServerConnection arsession, Collection collection, int nMergeType, List mergeQualList) throws AREasyException
	{
		if(mergeQualList != null)
		{
			String baseIdentifier = "base@";
			List mergeBaseQualList = new Vector();

			for(int i = 0; i < mergeQualList.size(); i++)
			{
				String fid = (String) mergeQualList.get(i);
				if(StringUtility.isNotEmpty(fid) && (!fid.contains("@") || fid.startsWith(baseIdentifier)))
				{
					if(fid.startsWith(baseIdentifier)) mergeBaseQualList.add( fid.substring(baseIdentifier.length()) );
					else mergeBaseQualList.add(fid);
				}
			}

			super.merge(arsession, collection, nMergeType, mergeBaseQualList);
		}
		else super.merge(arsession, collection, nMergeType);

		if(exists())
		{
			//make sync between parts and base structure
			synch();

			Iterator iterator = getPartCodes();

			while(iterator != null && iterator.hasNext())
			{
				String code = (String) iterator.next();
				CoreItem item = getPartInstance(code);

				try
				{
					if(code.indexOf("-") > 0) code = code.substring(0, code.indexOf("-", 0));
					int partMergeType = PatternAction.getMergeTypeAndOptions(this.config.subset(code));
					if(partMergeType == 0) partMergeType = nMergeType;

					if(mergeQualList != null)
					{
						String partIdentifier = code + "@";
						List mergePartQualList = new Vector();

						for(int i = 0; i < mergeQualList.size(); i++)
						{
							String fid = (String) mergeQualList.get(i);

							if(StringUtility.isNotEmpty(fid) && fid.startsWith(partIdentifier))
							{
								mergePartQualList.add( fid.substring(partIdentifier.length()) );
							}
						}

						item.merge(arsession, nMergeType, mergePartQualList);
					}
					else item.merge(arsession, partMergeType);
				}
				catch(Throwable th)
				{
					String message = "Error during execution of part merge action: " + th.getMessage();
					message += ". Part " + item;
					message += ". " + this;

					RuntimeLogger.warn(message);
					logger.debug("Exception", th);
				}
			}
		}
	}

	/**
	 * Get string representation of the current core item structure.
	 *
	 * @return string data model.
	 */
	public String toString()
	{
		return "Hybrid " + super.toString();
	}
}
