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

import org.areasy.runtime.actions.ars.data.BaseData;
import org.areasy.runtime.engine.RuntimeLogger;
import org.areasy.runtime.engine.base.AREasyException;
import org.areasy.runtime.engine.structures.CoreItem;
import org.areasy.runtime.engine.structures.data.cmdb.ConfigurationItem;

import java.util.List;
import java.util.Map;
import java.util.Vector;


/**
 * Abstract library that is used by all CMDB actions.
 */
public abstract class BaseConfigurationItemAction extends BaseData implements ConfigurationItemAction
{
	public void run() throws AREasyException
	{
		getCron().start();

		List items = null;
		boolean loop = true;

		Map kmap = getKeyMap();
		Map qmap = getQueryFields();

		ConfigurationItem entry = new ConfigurationItem();
		entry.setClassId(getConfiguration().getString("classid", null));
		entry.setDatasetId(getConfiguration().getString("datasetid", entry.getDatasetId()));

		if(getConfiguration().containsKey("qualification"))
		{
			//specify key attributes to process search operation.
			String qualification = getTranslatedQualification(getConfiguration().getString("qualification", null));

			//get list of found CIs
			items = entry.search(getServerConnection(), qualification);
		}
		else if(qmap != null && qmap.size() > 0)
		{
			//specify key attributes to process search operation.
			entry.setData(qmap);
			entry.setIgnoreNullValues(true);
			entry.setIgnoreUnchangedValues(true);

			//get list of found CIs
			items = entry.search(getServerConnection());
		}
		else if(kmap != null && kmap.size() > 0)
		{
			//specify key attributes to process search operation.
			entry.setData(kmap);
			entry.setIgnoreNullValues(true);
			entry.setIgnoreUnchangedValues(true);

			//get list of found CIs
			items = entry.search(getServerConnection());
		}

		if(items != null && items.size() > 0)
		{
			for (int i = 0; loop && i < items.size(); i++)
			{
				//gte entry
				ConfigurationItem item = (ConfigurationItem) items.get(i);

				try
				{
					//process validation
					run(item);

				}
				catch (Throwable th)
				{
					RuntimeLogger.error("Error running action for " + item + ": " + th.getMessage());
					logger.debug("Exception", th);

					loop = isForced();
					setErrorsCounter();
				}

				setRecordsCounter();

				// check interruption and and exit if the execution was really interrupted
				if (isInterrupted())
				{
					RuntimeLogger.warn("Execution interrupted by user");
					return;
				}
			}
		}
		else
		{
			try
			{
				//process validation
				run(entry);
			}
			catch (Throwable th)
			{
				RuntimeLogger.error("Error running action for " + entry + ": " + th.getMessage());
				logger.debug("Exception", th);

				setErrorsCounter();
			}

			setRecordsCounter();
		}


		//stop cron control
		getCron().stop();
	}

	protected Map getRelationKeyMap()
	{
		List peoplerelationkeyids = getConfiguration().getVector("peoplerelationkeyids", new Vector());
		List peoplerelationkeyvalues = getConfiguration().getVector("peoplerelationkeyvalues", new Vector());

		return getMap(peoplerelationkeyids, peoplerelationkeyvalues);
	}

	protected Map getRelationDataMap()
	{
		List peoplerelationmapids = getConfiguration().getVector("peoplerelationmapids", new Vector());
		List peoplerelationmapvalues = getConfiguration().getVector("peoplerelationmapvalues", new Vector());

		return getMap(peoplerelationmapids, peoplerelationmapvalues);
	}

	/**
	 * Execute an action for a specific <code>CoreItem</code>. This item must be identified previously and then the method
	 * could be called. This method will used by standard actions which implement an workflow using these type of action
	 * which permit single change or update.
	 *
	 * @param entry <code>CoreItem</code> structure, which should be instantiated.
	 * @throws org.areasy.runtime.engine.base.AREasyException if any error will occur.
	 */
	public void run(CoreItem entry) throws AREasyException
	{
		if(entry instanceof ConfigurationItem) run( (ConfigurationItem)entry );
			else throw new AREasyException("Expected ConfigurationItem data structure: " + entry);
	}
}
