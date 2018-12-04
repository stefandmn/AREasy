package org.areasy.runtime.actions.ars.dev.tools.flow.events;

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

import com.bmc.arsys.api.StructItemInfo;
import org.areasy.common.support.configuration.Configuration;
import org.areasy.runtime.actions.ars.dev.DefinitionAction;
import org.areasy.runtime.actions.ars.dev.ImportAction;
import org.areasy.runtime.actions.ars.dev.tools.flow.DevProcessAction;
import org.areasy.runtime.actions.ars.dev.tools.flow.WorksheetEvent;
import org.areasy.runtime.actions.ars.dev.tools.flow.WorksheetObject;
import org.areasy.runtime.engine.RuntimeLogger;

import java.io.File;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.Vector;

/**
 * This library imports selected workflow objects from a file into a ARSystem server. The import process could be
 * guided by a dictionary file or repository.
 */
public class WorksheetImportEvent extends WorksheetEvent
{
	public WorksheetImportEvent(Configuration config, List objmap)
	{
		super(config, objmap);
	}

	public void perform(DevProcessAction develop)
	{
		String inputFile = getConfiguration().getString("inputfile", null);
		String inputFolder = getConfiguration().getString("inputfolder", null);

		//export all in one file.
		if(inputFile != null)
		{
			//verify the input file
			File fileIn = new File(inputFile);

			if(!fileIn.exists())
			{
				RuntimeLogger.error("Input file doesn't exist: " + inputFile);
				return;
			}

			//identify all object which have to be imported
			List output = convert(null);

			try
			{
				//initiate import action
				ImportAction action = new ImportAction();
				action.init(develop);

				//execute definitions import.
				action.execute(output);
			}
			catch(Throwable th)
			{
				RuntimeLogger.error("Error running import action for all bundled definitions: " + th.getMessage());
				getLogger().debug("Exception", th);
			}
		}
		else
		{
			//get export file format
			String format = getConfiguration().getString("format", "def").toLowerCase();

			//verify the input folder
			if(inputFolder == null)
			{
				RuntimeLogger.error("Input folder was not specified: " + inputFolder);
				return;
			}

			File folderIn = new File(inputFolder);

			if(!folderIn.exists())
			{
				RuntimeLogger.error("Input folder doesn't exist: " + inputFolder);
				return;
			}

			if(getConfiguration().getBoolean("divided", false))
			{
				//prepare data for export
				for(int i = 0; getObjectsList() != null && i < getObjectsList().size(); i++)
				{
					String objname = null;
					String objtype = null;

					try
					{
						//read developed object
						WorksheetObject devobj = (WorksheetObject) getObjectsList().get(i);

						objname = devobj.getObjectName();
						objtype = devobj.getSignature();

						if(objname.endsWith("__o")) objname = objname.substring(0, objname.length() - 3);

						//initiate export action
						ImportAction action = new ImportAction();
						action.init(develop);

						String fileName = "AR" + getObjectTypeIdBySignature(objtype) + "_" + escapeString(objname)  + "." + format;
						File inFile = new File(inputFolder, fileName);

						if(inFile.exists())
						{
							getConfiguration().setKey("inputfile", inFile.getPath());
							action.execute(devobj.getStructItemInfo(develop.getServerConnection()));
						}
						else RuntimeLogger.error("The corresponding file for '" + objname + "' can not be found: " + fileName);
					}
					catch(Throwable th)
					{
						RuntimeLogger.error("Error running import action for a '" + objname + "' definition: " + th.getMessage());
						getLogger().debug("Exception", th);
					}
				}

				RuntimeLogger.info("Divided import for the specified development package has been done: " + inputFolder);
			}
			else
			{
				//list of export action used in this workflow
				Map types = new Hashtable();

				//prepare data for export
				for(int i = 0; getObjectsList() != null && i < getObjectsList().size(); i++)
				{
					String objname = null;
					String objtype = null;
					String objdata = null;

					//read developed object
					WorksheetObject devobj = (WorksheetObject) getObjectsList().get(i);
					objdata = getPluralObjectTypeNameBySignature(devobj.getSignature());

					List queue = (List) types.get(objdata);

					if(queue == null)
					{
						queue = new Vector();
						types.put(objdata, queue);
					}

					StructItemInfo sii = devobj.getStructItemInfo( develop.getServerConnection() );
					if(sii != null) queue.add(sii);
				}

				//export data
				if(!types.isEmpty())
				{
					//reorder data types to eliminate errors regarding dependencies
					String params[] = { DefinitionAction.TYPE_FORMS, DefinitionAction.TYPE_MENUS, DefinitionAction.TYPE_ACTIVELINKS,
									   DefinitionAction.TYPE_FILTERS, DefinitionAction.TYPE_ESCALATIONS, DefinitionAction.TYPE_ACTIVELINKGUIDES,
									   DefinitionAction.TYPE_APPLICATIONS, DefinitionAction.TYPE_FILTERGUIDES, DefinitionAction.TYPE_VIEWS,
									   DefinitionAction.TYPE_IMAGES};

					for(String datatype: params)
					{
						if(!types.containsKey(datatype)) continue;

						try
						{
							//take the corresponding action
							ImportAction action = new ImportAction();
							action.init(develop);

							List list = (List) types.get(datatype);

							File inFile = new File(inputFolder, datatype + "." + format);
							getConfiguration().setKey("inputfile", inFile.getPath());

							//run import action
							if(getConfiguration().getBoolean(datatype, true))
							{
								if(!action.isInit()) action.init(develop);
								action.execute(list);
							}
							else RuntimeLogger.info("Skipped definition file: " + inFile.getName());
						}
						catch(Throwable th)
						{
							RuntimeLogger.error("Error running import action for '" + datatype + "' cumulative definitions: " + th.getMessage());
							getLogger().debug("Exception", th);
						}
					}
				}
				else RuntimeLogger.warn("No object found in the specified development package");

				RuntimeLogger.info("Cumulative import for the specified development package has been done: " + inputFolder);
			}

			//remove workflow key to not tranfer data to the client side
			getConfiguration().removeKey("inputfile");
		}
	}
}
