package org.areasy.runtime.engine.structures.hybrid;

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

import com.bmc.arsys.api.AttachmentValue;
import org.areasy.common.support.configuration.Configuration;
import org.areasy.runtime.RuntimeManager;
import org.areasy.runtime.actions.PatternAction;
import org.areasy.runtime.engine.RuntimeLogger;
import org.areasy.runtime.engine.base.AREasyException;
import org.areasy.runtime.engine.base.ServerConnection;
import org.areasy.common.data.StringUtility;
import org.areasy.runtime.engine.structures.CoreItem;
import org.areasy.runtime.engine.structures.MultiPartItem;

import java.io.File;
import java.util.*;

/**
 * This is an extended library of <code>CoreItem</code> that allows you to take care about all dependencies between
 * the main structure and all related and to have possibility to transfer dta between two instance in order to support data migration.
 */
public class HybridCoreItem extends MultiPartItem implements AbstractHybrid
{
	private Configuration config = null;

	/**
	 * Method to generate target data structure.
	 *
	 * @param target structure to be populated
	 * @param map data mapping between source and target
	 * @param connection source connection instance
	 * @param config runtime configuration or more specifically, multipart configuration
	 * @throws AREasyException if any error will occur
	 */
	public void transfer(CoreItem target, Map<Integer,Object> map, ServerConnection connection, Configuration config) throws AREasyException
	{
		setTarget(target, this, map, connection);

		//set-up all related sub-structures
		if(target instanceof HybridCoreItem)
		{
			HybridCoreItem entity = (HybridCoreItem)target;
			entity.setMultiPart(this);

			//store configuration
			entity.config = config;
		}
	}

	/**
	 * Method to generate related data structures.
	 *
	 * @param connection source connection instance
	 * @param config runtime configuration or more specifically, multipart configuration
	 * @throws org.areasy.runtime.engine.base.AREasyException if any error will occur
	 */
	public void relate(ServerConnection connection, Configuration config) throws AREasyException
	{
		//nothing to do here
	}

	/**
	 * Static method to generate target data structure, designed to be used by all <code>Hybrid</code> structures (<code>Transferable</code> and <code>Related</code> structure).
	 *
	 * @param target structure to be populated
	 * @param source source data structure
	 * @param map data mapping between source and target
	 * @param connection source connection instance
	 */
	public static void setTarget(CoreItem target, CoreItem source, Map<Integer,Object> map, ServerConnection connection)
	{
		Iterator iterator = map.keySet().iterator();

		while(iterator != null && iterator.hasNext())
		{
			Integer destinationKey = (Integer) iterator.next();
			Object sourceKey = map.get(destinationKey);

			if(sourceKey instanceof Integer)
			{
				Object remoteObject = source.getAttributeValue((Integer)sourceKey);

				//handle special cases: attachments
				if(remoteObject != null && remoteObject instanceof AttachmentValue)
				{
					AttachmentValue download = (AttachmentValue) remoteObject;

					try
					{
						String attachmentName = download.getValueFileName();
						if(attachmentName.lastIndexOf("\\") >= 0) attachmentName = attachmentName.substring(attachmentName.lastIndexOf("\\") + 1);
							else if(attachmentName.lastIndexOf("/") >= 0) attachmentName = attachmentName.substring(attachmentName.lastIndexOf("/") + 1);

						File file = new File(RuntimeManager.getWorkingDirectory(), attachmentName);
						if(file.exists()) file.delete();

						connection.getContext().getEntryBlob(source.getFormName(), source.getEntryId(), (Integer)sourceKey, file.getPath());

						AttachmentValue upload = new AttachmentValue(download.getName(), file.getPath());
						target.setAttribute(destinationKey, upload);
					}
					catch(Throwable th)
					{
						RuntimeLogger.warn("Error extracting attachment from " + source.getEntryId() + " source entry id: " + th.getMessage());
						logger.debug("Exception" + th);
					}
				}
				else target.setAttribute(destinationKey, remoteObject);
			}
			else
			{
				//simple and unknown association
				target.setAttribute(destinationKey, sourceKey);
			}
		}
	}

	/**
	 * Create a new <code>HybridPeople</code> structure.
	 *
	 * @return new People instance
	 */
	public HybridCoreItem getInstance()
	{
		return new HybridCoreItem();
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
     * to transform it into a valid core item instance.
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

					logger.error(message);
					RuntimeLogger.warn(message);
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

	/**
	 * Get string representation of the current core item structure.
	 *
	 * @return string data model.
	 */
	public String toFullString()
	{
		return "Hybrid " + super.toFullString();
	}
}
