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

import org.areasy.runtime.actions.data.BaseData;
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

		ConfigurationItem search = new ConfigurationItem();
		search.setClassId(getConfiguration().getString("classid", null));
		search.setDatasetId(getConfiguration().getString("datasetid", search.getDatasetId()));

		if(getConfiguration().containsKey("qualification"))
		{
			//specify key attributes to process search operation.
			String qualification = getTranslatedQualification(getConfiguration().getString("qualification", null));

			//get list of found CIs
			items = search.search(getServerConnection(), qualification);
		}
		else if(qmap != null && qmap.size() > 0)
		{
			//specify key attributes to process search operation.
			search.setData(qmap);
			search.setIgnoreNullValues(true);
			search.setIgnoreUnchangedValues(true);

			//get list of found CIs
			items = search.search(getServerConnection());
		}
		else if(kmap != null && kmap.size() > 0)
		{
			//specify key attributes to process search operation.
			search.setData(kmap);
			search.setIgnoreNullValues(true);
			search.setIgnoreUnchangedValues(true);

			//get list of found CIs
			items = search.search(getServerConnection());
		}

		for(int i = 0; loop && items != null && i < items.size(); i++)
		{
			//gte entry
			ConfigurationItem item = (ConfigurationItem)items.get(i);

			try
			{
				//process validation
				run(item);

			}
			catch(Throwable th)
			{
				RuntimeLogger.error("Error running action for " + item + ": " + th.getMessage());
				logger.debug("Exception", th);

				loop = isForced();
				setErrorsCounter();
			}

			setRecordsCounter();

			// check interruption and and exit if the execution was really interrupted
			if(isInterrupted())
			{
				RuntimeLogger.warn("Execution interrupted by user");
				return;
			}
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
