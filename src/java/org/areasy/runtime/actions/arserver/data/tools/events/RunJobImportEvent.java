package org.areasy.runtime.actions.arserver.data.tools.events;

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

import com.bmc.arsys.apiext.data.*;
import org.areasy.runtime.engine.RuntimeLogger;
import org.areasy.runtime.engine.base.ARDictionary;
import org.areasy.runtime.engine.base.AREasyException;
import org.areasy.runtime.engine.structures.CoreItem;
import org.areasy.runtime.engine.workflows.ProcessorLevel1Context;

import java.io.File;

/**
 * Dedicated event (from data process workflow) to import all related data to a job
 * in order to be migrated to another ARSystem server where is installed AAR engine.
 */
public class RunJobImportEvent extends AbstractEvent
{
	/**
	 * Execute event
	 *
	 * @throws org.areasy.runtime.engine.base.AREasyException if any error occurs
	 */
	public void execute() throws AREasyException
	{
		//disable notification
		getAction().getConfiguration().setKey("notification", "false");

		String insatnceId = getAction().getConfiguration().getString("instanceid", null);
		if(insatnceId == null) throw new AREasyException("Job file identifier is null");

		CoreItem output = new CoreItem(ARDictionary.FORM_RUNTIME_OUTPUT);
		output.setAttribute(7, new Integer(0));
		output.setAttribute(179, insatnceId);
		output.read(getAction().getServerConnection());

		if(!output.exists()) throw new AREasyException("Job file could not be found using '" + insatnceId + "' instance Id");

		File file = ProcessorLevel1Context.getDataFile(getAction().getServerConnection(), ARDictionary.FORM_RUNTIME_OUTPUT, "536870914", "179", insatnceId);
		setImportData(file);
	}

	protected void setImportData(File file) throws AREasyException
	{
		if(file == null || !file.exists()) throw new AREasyException("Job file could not be handled");

		try
		{
			boolean impersonated = false;

			//prepare user connection
			if(getAction().getServerConnection().isImpersonated())
			{
				impersonated = true;
				getAction().getServerConnection().getContext().impersonateUser(null);
			}

			DataImport dataImport = new DataImport();
			ImportOptions importOptions = new ImportOptions();
			DataOptions dataOptions = new DataOptions();
			FileOptions fileOptions = new FileOptions();

			dataOptions.setAllowTooFew(true);
			dataOptions.setAllowTooMany(true);
			//dataOptions.setAllowNull(true);
			dataOptions.setDisablePattern(true);
			dataOptions.setDisableRequired(true);
			dataOptions.setSuppressFilters(true);
			dataOptions.setSuppressDefaults(0);
			dataOptions.setDuplicateIdHandling(DataOptions.DuplicateHandling.DUP_NEW_ID);
			dataOptions.setBadRecordHandling(DataOptions.BadRecords.STOP);

			fileOptions.setDataFile(file.getPath());
			fileOptions.setType(FileOptions.FileType.ARX);

			importOptions.setImportAllDataSets(true);
			importOptions.setContext(getAction().getServerConnection().getContext());
			importOptions.setDataOptions(dataOptions);
			importOptions.setFileOptions(fileOptions);

			dataImport.setLogInitialized(true);
			dataImport.setOptions(importOptions);

			dataImport.startImport();
			ImportResults results = importOptions.getResults();

			RuntimeLogger.info("Data import workflow precessed " + results.getRecordCount() + " record(s), " + results.getSuccessCount() + " with success and " + results.getErrorCount() + " with errors");

			//set back end-user connection
			if(impersonated) getAction().getServerConnection().getContext().impersonateUser(getAction().getServerConnection().getUserName());
		}
		catch(Throwable th)
		{
			throw new AREasyException("Error during data import native workflow: " + th.getMessage(), th);
		}
	}
}
