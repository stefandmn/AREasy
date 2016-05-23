package org.areasy.runtime.engine.structures.hybrid.itsm.foundation;

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

import org.areasy.common.data.StringUtility;
import org.areasy.common.support.configuration.Configuration;
import org.areasy.runtime.actions.PatternAction;
import org.areasy.runtime.engine.RuntimeLogger;
import org.areasy.runtime.engine.base.AREasyException;
import org.areasy.runtime.engine.base.ServerConnection;
import org.areasy.runtime.engine.structures.CoreItem;
import org.areasy.runtime.engine.structures.data.itsm.foundation.ProductCategory;
import org.areasy.runtime.engine.structures.hybrid.AbstractHybrid;
import org.areasy.runtime.engine.structures.hybrid.HybridCoreItem;

import java.util.*;

/**
 * This is a data structure who manages <Code>ProductCategory</code> entities that includes all related sub-structures,
 * and each transaction of the main entity will handle also the parts.
 */
public class HybridProductCategory extends ProductCategory implements AbstractHybrid
{
	private Configuration config = null;

	public HybridProductCategory()
	{
		super();
	}

	/**
	 * Create a new <code>HybridCompany</code> structure.
	 *
	 * @return new People instance
	 */
	public HybridProductCategory getInstance()
	{
		return new HybridProductCategory();
	}

	public void relate(ServerConnection arsession, Configuration config) throws AREasyException
	{
		//read secondary structures.
		if(exists())
		{
			if(config.getBoolean("companyrelation", true))
			{
				//read permissions
				CoreItem searchRel = new CoreItem();
				searchRel.setFormName("PCT:ProductCompanyAssociation");
				searchRel.setAttribute(300724400, getAttributeValue(179));
				searchRel.setAttribute(1000000097, getAttributeValue(1));
				List<CoreItem> relations = searchRel.search(arsession);

				for(int i = 0; relations != null && i < relations.size(); i++)
				{
					CoreItem item = relations.get(i);

					addPartWithPrefix("companyrelation", item);
					addPartKeyPairWithPrefix("companyrelation", item, 300724400, 179);
					addPartKeyPairWithPrefix("companyrelation", item, 1000000097, 1);
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
		if(target instanceof HybridProductCategory)
		{
			HybridProductCategory sgroup = (HybridProductCategory)target;
			sgroup.setMultiPart(this);

			//store configuration
			sgroup.config = config;
		}
	}

	/**
	 * Commit part item instances based on specified attributes.
	 *
	 * @param arsession user session
	 * @throws org.areasy.runtime.engine.base.AREasyException if any error will occur
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
	 * to trasform it into a valid core item instance.
	 *
	 * @param arsession user session
	 * @param collection attribute's collection
	 * @throws org.areasy.runtime.engine.base.AREasyException if any error will occur
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
	 * @throws org.areasy.runtime.engine.base.AREasyException if any error will occur
	 */
	protected void update(ServerConnection arsession, Collection collection) throws AREasyException
	{
		super.update(arsession, collection);

		//commit part items
		commit(arsession);
	}

	/**
	 * Merge an entry record in the ARS server using an attribute's collection and then is read it
	 * to trasform it into a valid core item instance.
	 *
	 * @param arsession user session
	 * @param collection attribute's collection
	 * @throws org.areasy.runtime.engine.base.AREasyException if any error will occur
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