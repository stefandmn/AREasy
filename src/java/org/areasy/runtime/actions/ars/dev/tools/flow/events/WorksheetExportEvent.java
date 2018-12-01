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

import com.bmc.arsys.api.ObjectBase;
import com.bmc.arsys.api.StructItemInfo;
import org.areasy.runtime.actions.ars.dev.tools.flow.WorksheetEvent;
import org.areasy.runtime.actions.ars.dev.tools.flow.WorksheetObject;
import org.areasy.runtime.actions.ars.dev.tools.flow.DevProcessAction;
import org.areasy.runtime.RuntimeManager;
import org.areasy.runtime.actions.ars.dev.ExportAction;
import org.areasy.runtime.actions.ars.dev.wrappers.FormRelatedWrapper;
import org.areasy.runtime.actions.ars.dev.wrappers.ObjectWrapper;
import org.areasy.runtime.engine.RuntimeLogger;
import org.areasy.runtime.utilities.ZipUtility;
import org.areasy.common.support.configuration.Configuration;

import java.io.File;
import java.util.*;

/**
 * This library exports selected workflow objects into a file. The export process is guided by a
 * dictionary file or repository.
 */
public class WorksheetExportEvent extends WorksheetEvent
{
	public WorksheetExportEvent(Configuration config, List objmap)
	{
		super(config, objmap);
	}

	public void perform(DevProcessAction develop)
	{
		String outputFile = getConfiguration().getString("outputfile", null);
		String outputFolder = getConfiguration().getString("outputfolder", null);

		//export all in one file.
		if(outputFile != null)
		{
			//validate input file
			File fileOut = new File(outputFile);

			if(!fileOut.exists())
			{
				if(fileOut.getParentFile() != null && !fileOut.getParentFile().exists())
				{
					RuntimeLogger.error("Parent folder for the output file doesn't exist: " + fileOut.getParentFile().getPath());
					return;
				}
			}

			//prepare data for export
			List output = convert(develop.getServerConnection());

			try
			{
				//initiate action action
				ExportAction action = new ExportAction();
				action.init(develop);

				//execute definitions action.
				action.execute(output);
			}
			catch(Throwable th)
			{
				RuntimeLogger.error("Error running export action for all bundled definitions: " + th.getMessage());

				getLogger().error("Error running export action for all bundled definitions: " + th.getMessage());
				getLogger().debug("Exception", th);
			}
		}
		else //export each object into separate files.
		{
			//get export file format
			String format = getConfiguration().getString("format", "def").toLowerCase();

			//validate identify the location where the files will be created
			if(outputFolder == null)
			{
				String folderName = "defexp-" + RuntimeLogger.getChannelName();
				File folder = new File(RuntimeManager.getWorkingDirectory(), folderName);

				//if the folder will be automatically selected and it is already created must be deleted first.
				if(folder.exists()) removeFolder(folder);

				//create it and deliver it.
				folder.mkdir();
				outputFolder = folder.getPath();
			}
			else
			{
				File folderOut = new File(outputFolder);

				if(!folderOut.exists())
				{
					if(folderOut.getParentFile() != null && !folderOut.getParentFile().exists())
					{
						RuntimeLogger.error("Output folder doesn't exist: " + outputFolder);
						return;
					}
					else folderOut.mkdir();
				}
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

						//initiate action action
						ExportAction action = new ExportAction();
						action.init(develop);

						//get the base object instance and run the action
						String objSignature = getPluralObjectTypeNameBySignature(objtype);
						ObjectWrapper wrapper = action.getObjectWrapper(objSignature);

						if(wrapper instanceof FormRelatedWrapper)
						{
							((FormRelatedWrapper) wrapper).setFormName(objname);
							objname = devobj.getRelatedData();

							//@todo - export and import devided views
						}

						ObjectBase object = wrapper.getInstance(objname);

						//get the file name for each object
						String fileName = "AR" + getObjectTypeIdBySignature(objtype) + "_" + escapeString(objname) + "." + format;
						File outFile = new File(outputFolder, fileName);
						getConfiguration().setKey("outputfile", outFile.getPath());

						action.execute(object);
					}
					catch(Throwable th)
					{
						RuntimeLogger.error("Error running export action for a '" + objname + "' definition: " + th.getMessage());
						getLogger().debug("Exception", th);
					}
				}

				RuntimeLogger.info("Divided export for the specified development package has been done: " + outputFolder);
			}
			else
			{
				Map types = new Hashtable();
				String objdata = null;

				//prepare data for export
				for(int i = 0; getObjectsList() != null && i < getObjectsList().size(); i++)
				{
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
					//initiate export action
					ExportAction export = new ExportAction();
					Iterator datatypes = types.keySet().iterator();

					while(datatypes != null && datatypes.hasNext())
					{
						String datatype = (String) datatypes.next();
						List list = (List) types.get(datatype);

						try
						{
							File outFile = new File(outputFolder, datatype + "." + format);
							getConfiguration().setKey("outputfile", outFile.getPath());

							if(!export.isInit()) export.init(develop);
							export.execute(list);
						}
						catch(Throwable th)
						{
							RuntimeLogger.error("Error running export action for '" + datatype + "' cumulative definitions: " + th.getMessage());
							getLogger().debug("Exception", th);
						}
					}
				}
				else RuntimeLogger.warn("No object found in the specified development package");

				RuntimeLogger.info("Cumulative export for the specified development package has been done: " + outputFolder);
			}

			//remove workflow key to not transfer data to the client side
			getConfiguration().removeKey("outputfile");

			if(!getConfiguration().containsKey("outputfolder") && develop.getManager().getExecutionMode() == RuntimeManager.RUNTIME)
			{
				try
				{
					File folderOut = new File(outputFolder);
					File zipFile = new File(RuntimeManager.getWorkingDirectory(), folderOut.getName() + ".zip");
					ZipUtility.dpZip( folderOut, zipFile.getPath() );

					//transfer zip archive to the client side.
					if(develop.getManager().getExecutionMode() == RuntimeManager.SERVER) getConfiguration().setKey("outputfile", zipFile.getPath());
					RuntimeLogger.info("A zip archive has been transferred to the local file system: " + zipFile.getPath());
				}
				catch(Exception e)
				{
					RuntimeLogger.warn("Error creating zip archive for the Export output: " + e.getMessage());
					getLogger().debug("Exception", e);
				}
			}
		}
	}
}
