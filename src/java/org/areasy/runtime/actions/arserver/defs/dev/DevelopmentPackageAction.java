package org.areasy.runtime.actions.arserver.defs.dev;

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

import com.bmc.arsys.api.StructItemInfo;
import org.areasy.runtime.actions.arserver.defs.dev.base.*;
import org.areasy.runtime.actions.arserver.defs.DefinitionAction;
import org.areasy.runtime.actions.arserver.defs.ExportAction;
import org.areasy.runtime.engine.RuntimeLogger;
import org.areasy.runtime.engine.base.AREasyException;
import org.areasy.runtime.engine.services.parser.ParserEngine;
import org.areasy.runtime.engine.workflows.ProcessorLevel0Reader;
import org.areasy.common.data.StringUtility;

import java.io.File;
import java.util.*;

/**
 * Development package action is an AR Easy utility (part of Advanced Automation module) that allows you
 * to describe all development customizations into a specific Excel template which can become the deployment descriptor.
 * This action takes the Excel file and process it as the actions and definition objects are described there.
 * <p>
 * Attention: This action could be execute in runtime or server mode but if the execution is performed in server mode the
 * exports and import files have to reside on the server and are not transferable of the client side.
 */
public class DevelopmentPackageAction extends DefinitionAction
{
	/**
	 * Execute the current action.
	 *
	 * @throws org.areasy.runtime.engine.base.AREasyException
	 * if any error will occur
	 */
	public void run() throws AREasyException
	{
		boolean exportJob = getConfiguration().getBoolean("export", false);
		boolean importJob = getConfiguration().getBoolean("import", false) || getConfiguration().getBoolean("deploy", false);
		boolean overlayJob = getConfiguration().getBoolean("overlay", false);

		if(exportJob || importJob || overlayJob)
		{
			//consolidate input
			Map objmap = getDictionary();

			if(objmap != null && !objmap.isEmpty())
			{
				if(exportJob)
				{
					List exportObjs =  new Vector();
					List createObjs = (List) objmap.get(BaseDevelopment.ACT_CREATE);
					List modifyObjs = (List) objmap.get(BaseDevelopment.ACT_MODIFY);

					if(createObjs != null) exportObjs.addAll(createObjs);
					if(modifyObjs != null) exportObjs.addAll(modifyObjs);

					if(!exportObjs.isEmpty())
					{
						DevelopmentExport exportDev = new DevelopmentExport(getConfiguration(), exportObjs);
						exportDev.perform(this);
					}
				}
				else if(importJob)
				{
					//execute rename dev action
					List renameObjs = (List) objmap.get(BaseDevelopment.ACT_RENAME);

					if(renameObjs != null && !renameObjs.isEmpty())
					{
						DevelopmentRename renameDev = new DevelopmentRename(getConfiguration(), renameObjs);
						renameDev.perform(this);
					}

					//execute remove dev action
					List removeObjs = (List) objmap.get(BaseDevelopment.ACT_REMOVE);

					if(removeObjs != null && !removeObjs.isEmpty())
					{
						DevelopmentRemove removeDev = new DevelopmentRemove(getConfiguration(), removeObjs);
						removeDev.perform(this);
					}

					//execute import action
					List importObjs =  new Vector();
					List createObjs = (List) objmap.get(BaseDevelopment.ACT_CREATE);
					List modifyObjs = (List) objmap.get(BaseDevelopment.ACT_MODIFY);

					if(createObjs != null) importObjs.addAll(createObjs);
					if(modifyObjs != null) importObjs.addAll(modifyObjs);

					if(!importObjs.isEmpty())
					{
						DevelopmentImport importDev = new DevelopmentImport(getConfiguration(), importObjs);
						importDev.perform(this);
					}

					//execute status (enable) dev action
					List enableObjs = (List) objmap.get(BaseDevelopment.ACT_ENABLE);

					if(enableObjs != null && !enableObjs.isEmpty())
					{
						DevelopmentStatus statusDev = new DevelopmentStatus(getConfiguration(), enableObjs);
						statusDev.setEnable();
						statusDev.perform(this);
					}

					//execute status (disable) dev action
					List disableObjs = (List) objmap.get(BaseDevelopment.ACT_DISABLE);

					if(disableObjs != null && !disableObjs.isEmpty())
					{
						DevelopmentStatus statusDev = new DevelopmentStatus(getConfiguration(), disableObjs);
						statusDev.setDisable();
						statusDev.perform(this);
					}
				}
				else if(overlayJob && !importJob)
				{
					List overlayObjs =  new Vector();
					Iterator iterator = objmap.values().iterator();

					while (iterator != null && iterator.hasNext())
					{
						List list = (List) iterator.next();

						for(int i = 0; list != null && i < list.size(); i++)
						{
							DevelopmentPackageObject object = (DevelopmentPackageObject) list.get(i);
							if(object.isOverlay()) overlayObjs.add(object);
						}
					}

					if(!overlayObjs.isEmpty())
					{
						DevelopmentOverlay exportDev = new DevelopmentOverlay(getConfiguration(), overlayObjs);
						exportDev.perform(this);
					}
				}
			}
			else
			{
				if(getConfiguration().getBoolean("inventory", false) && exportJob)
				{
					//initiate export action
					ExportAction export = new ExportAction();
					export.init(this);

					//execute definitions export.
					export.run();
				}
				else if(importJob)
				{
					DevelopmentImport importDev = new DevelopmentImport(getConfiguration(), null);
					importDev.perform(this);
				}
			}
		}
		else RuntimeLogger.error("No package development action called");
	}

	/**
	 * Execute the current action.
	 *
	 * @return map of development action and their related objects.
	 * @throws org.areasy.runtime.engine.base.AREasyException
	 * if any error will occur
	 */
	protected Map getDictionary() throws AREasyException
	{
		Map objmap = new Hashtable();

		ParserEngine parser = null;
		String file = getConfiguration().getString("parserfile", getConfiguration().getString("devfile", getConfiguration().getString("developmentfile", null)));

		if(StringUtility.isNotEmpty(file))
		{
			getConfiguration().setKey("pageindex", getConfiguration().getString("pageindex", "0"));
			getConfiguration().setKey("startindex", getConfiguration().getString("startindex", "1"));
			getConfiguration().setKey("endindex", getConfiguration().getString("endindex", "0"));
			getConfiguration().setKey("parsertype", "file");
			getConfiguration().setKey("parserfile", file);

			try
			{
				//initialization
				parser = new ParserEngine(getServerConnection(), getManager().getConfiguration(), getConfiguration());
				parser.init();
			}
			catch(AREasyException are)
			{
				throw are;
			}
			catch(Throwable th)
			{
				getLogger().error("Error reading data source file: " + th.getMessage());
				getLogger().debug("Exception", th);

				throw new AREasyException("Data source is not present or parsing process found an error: " + th.getMessage());
			}

			boolean empty = false;

			do
			{
				String data[] = null;
				String buffer[] = null;

				try
				{
					//get data from parser engine
					RuntimeLogger.reset();
					buffer = parser.read();
					RuntimeLogger.clearData();

					if(buffer != null && buffer.length >= 6)
					{
						data = new String[6];
						System.arraycopy(buffer, 0, data, 0, 6);
					}

					//validate data
					if(!isDataEmpty(data))
					{
						DevelopmentPackageObject object = new DevelopmentPackageObject(data);
						List list = (List) objmap.get(object.getAction());

						if(list == null)
						{
							list = new Vector();
							list.add(object);

							objmap.put(object.getAction(), list);
						}
						else list.add(object);
					}
					else empty = true;
				}
				catch(Throwable th)
				{
					String errorMsg = "Error running action using data extracted from line '" + parser.getParser().getCurrentIndex() + "'";
					errorMsg += ": " + th.getMessage();

					RuntimeLogger.error(errorMsg);

					getLogger().error(errorMsg);
					getLogger().debug("Exception", th);
				}
				finally
				{
					if(buffer != null)
					{
						for(int i = 0; i < data.length; i++)
						{
							getConfiguration().removeKey( (i + 1) + "" );
							getConfiguration().removeKey( ProcessorLevel0Reader.getGenericColumnFromIndex(i) );
						}
					}
				}
			}
			while(!empty);
		}
		else
		{
			if(getConfiguration().getBoolean("inventory", false))
			{
				if(getConfiguration().getString("inputfile", null) != null || getConfiguration().getString("inputfolder", null) != null) setObjectsMapByInputDefinitionFiles(objmap);
					else return null;
			}
			else
			{
				boolean objValidation = getConfiguration().getBoolean("import", false) || getConfiguration().getBoolean("deploy", false);
				List inputlist = getInputList(!objValidation);

				if(inputlist != null && !inputlist.isEmpty())
				{
					for(int i = 0; i < inputlist.size(); i++)
					{
						StructItemInfo info = (StructItemInfo) inputlist.get(i);
						addObjectInMap(objmap, new DevelopmentPackageObject(info));
					}
				}
				else
				{
					if((getConfiguration().getBoolean("import", false) || getConfiguration().getBoolean("deploy", false)) &&
						(getConfiguration().getString("inputfile", null) != null || getConfiguration().getString("inputfolder", null) != null))
					{
						getConfiguration().setKey("divided", "true");
						setObjectsMapByInputDefinitionFiles(objmap);
					}
					else throw new AREasyException("Data source was not specified");
				}
			}
		}

		return objmap;
	}

	private void setObjectsMapByInputDefinitionFiles(Map objmap) throws AREasyException
	{
		List foundlist = null;
		String inputFile = getConfiguration().getString("inputfile", null);
		String inputFolder = getConfiguration().getString("inputfolder", null);

		//read list of object from input file or files (from input folder)
		if(inputFile != null)
		{
			foundlist = getDefinitionFileStructure(inputFile);
		}
		else if(inputFolder != null)
		{
			File files[] = new File(inputFolder).listFiles();

			for(int i = 0; files != null && i < files.length; i++)
			{
				if(files[i].isFile() )
				{
					if(foundlist == null) foundlist = getDefinitionFileStructure(files[i].getPath());
						else foundlist.addAll( getDefinitionFileStructure(files[i].getPath()) );
				}
			}
		}

		//filter the list in case of is required to processed only overlays
		if(getConfiguration().getBoolean("onlyoverlays", false)) foundlist = getOnlyOverlays(foundlist, false);

		//fill in the map
		for(int i = 0; foundlist != null && i < foundlist.size(); i++)
		{
			StructItemInfo info = (StructItemInfo) foundlist.get(i);
			addObjectInMap(objmap, new DevelopmentPackageObject(info));
		}
	}

	private boolean isDataEmpty(String[] data)
	{
		if(data == null || data.length == 0) return true;
		else
		{
			boolean empty = true;

			for (int i = 0; empty && i < data.length; i++)
			{
				empty = StringUtility.isEmpty(data[i]);
			}

			return empty;
		}
	}

	private void addObjectInMap(Map objmap, DevelopmentPackageObject object)
	{
		if(object != null)
		{
			List list = (List) objmap.get(object.getAction());

			if(list == null)
			{
				list = new Vector();
				list.add(object);

				objmap.put(object.getAction(), list);
			}
			else list.add(object);
		}
	}

	public String help()
	{
		return  "[-parserfile <excel file>] [-export|-import] [-outputfile|-outputfolder <definition file|path> " +
				"[divided]] [-inputfile|-inputfolder <definition file|path> [divided]] [-overlay] " +
				super.help();
	}
}
