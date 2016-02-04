package org.areasy.runtime.actions.arserver.dev;

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

import com.bmc.arsys.api.*;
import org.areasy.runtime.RuntimeAction;
import org.areasy.runtime.actions.arserver.dev.wrappers.FormRelatedWrapper;
import org.areasy.runtime.actions.arserver.dev.wrappers.ObjectWrapper;
import org.areasy.runtime.engine.RuntimeLogger;
import org.areasy.runtime.engine.base.ServerConnection;
import org.areasy.common.data.StringUtility;

import java.io.*;
import java.util.Arrays;
import java.util.List;
import java.util.Vector;

/**
 * Runtime action to import definitions.
 *
 */
public class ImportAction extends DefinitionAction implements RuntimeAction
{
	/**
	 * Convert all found objects in the definition code and write this output in the definition file.
	 *
	 * @param objects a list with all found objects
	 */
	public void execute(List objects)
	{
		String fileName = getConfiguration().getString("inputfile", null);
		String format = getInputFileFormat(fileName);

		//validate input file name
		if(fileName == null)
		{
			RuntimeLogger.error("Input definition file is null");
			return;
		}
		else
		{
			File file = new File(fileName);

			if(!file.exists())
			{
				RuntimeLogger.error("Input definition file does not exist: " + fileName);
				return;
			}
		}

		try
		{
			if(StringUtility.equals(format, "xml") || StringUtility.equals(format, "def"))
			{
				boolean force = true;
				List<StructItemInfo> importlist = new Vector<StructItemInfo>();

				//filter only overlay objects
				if(getConfiguration().getBoolean("onlyoverlays", false))
				{
					//parse definition file to get the list of object to know in order to filter it only for overlays
					List validation = getDefinitionFileStructure(fileName);

					if(objects == null || objects.isEmpty())
					{
						if(objects == null) objects = new Vector();
						objects.addAll(validation);
						force = false;
					}

					importlist = getOnlyOverlays(objects, force);
					importlist = getValidatedList(importlist, validation);

				}
				else if(objects != null) importlist.addAll(objects);

				//run server import
				if(importlist.size() > 0)
				{
					logger.debug("Importing the following objects: " + getStructItemInfoListToString(importlist));
					getServerConnection().getContext().importDefFromFile(fileName, getImportOptions(), importlist);
				}
				else
				{
					logger.debug("Importing all objects related to the definition file: " + new File(fileName).getName());
					getServerConnection().getContext().importDefFromFile(fileName, getImportOptions());
				}

				RuntimeLogger.info("Definition file has been processed: " + fileName);
			}
			else if(StringUtility.equals(format, "bin"))
			{
				importBinary(getServerConnection(), objects, fileName);
				RuntimeLogger.info("Definition file has been processed: " + fileName);
			}
			else RuntimeLogger.error("Import file format is invalid: " + format);
		}
		catch (Throwable th)
		{
			RuntimeLogger.error("Error importing definitions: " + th.getMessage());
			getLogger().debug("Exception", th);
		}
	}

	/**
	 * Commit changes made in the specified structure info instance.
	 *
	 * @param objectInfo managed <code>StructItemInfo</code> instance.
	 * @throws com.bmc.arsys.api.ARException if the object factory will return an error
	 */
	public void execute(StructItemInfo objectInfo) throws ARException
	{
		List<StructItemInfo> objects = new Vector<StructItemInfo>();
		objects.add(objectInfo);

		execute(objects);
	}

	/**
	 * Commit changes made in the specified object instance.
	 *
	 * @param object managed object instance.
	 * @throws com.bmc.arsys.api.ARException if the object factory will return an error
	 */
	public void execute(ObjectBase object) throws ARException
	{
		List<StructItemInfo> objects = new Vector<StructItemInfo>();

		StructItemInfo info = new StructItemInfo(getObjectTypeId(object), object.getName(), null);
		objects.add(info);

		execute(objects);
	}

	/**
	 * This method import workflow objects exported in binary mode that means to de-serialize them and transform them
	 * into object instances and than to create or modify them in AR System server.
	 *
	 * @param session server connection used to import (create or update) the list of object instances
	 * found in the binary file - reconciled with list of object names explicitly mentioned in the development map.
	 * @param data list of <code>StructItemInfo</code> object references
	 * @param fileName file name and path that contains the workflow objects that have to be imported
	 * @throws java.io.IOException in case of any IO exception occur
	 * @throws ARException in case of any ARSystem exception will occur
	 * @throws ClassNotFoundException in case of expected object class is not found in exported file
	 */
	protected void importBinary(ServerConnection session, List data, String fileName) throws IOException, ARException, ClassNotFoundException
	{
		List<ObjectBase> objects = new Vector<ObjectBase>();
		Object object = null;

		FileInputStream in = new FileInputStream(fileName);
		ObjectInputStream reader = new ObjectInputStream(in);

		do
		{
			try
			{
				object = reader.readObject();
			}
			catch (EOFException e)
			{
				object = null;
			}

			if(object != null && object instanceof ObjectBase)
			{
				ObjectBase base = (ObjectBase)object;
				objects.add(base);
			}
		}
		while(object != null);

		//identify and validate objects that have to be processes and than create or update them in the target server connection
		importBinary(session, objects, data);
	}

	protected void importBinary(ServerConnection session, List objects, List sii)
	{
		boolean overwrite = getConfiguration().getBoolean("overwrite", false);

		for(int i = 0; i < objects.size(); i++)
		{
			boolean found = false;
			boolean formsubstructure = false;
			ObjectBase object = (ObjectBase)objects.get(i);

			if(sii != null && sii.size() > 0)
			{
				String objName =  object.getName();
				int objType = getObjectTypeId(object);

				for (int j = 0; !found && j < sii.size(); j++)
				{
					StructItemInfo info = (StructItemInfo)sii.get(j);

					if(objType != StructItemInfo.FIELD && objType != StructItemInfo.VUI)
					{
						found = objType == info.getType() && StringUtility.equals(objName, info.getName());
					}
					else
					{
						String formName = null;

						if(object instanceof Field) formName = ((Field)object).getForm();
							else if(object instanceof View) formName = ((View)object).getFormName();

						if(objType == info.getType())
						{
							List fields = Arrays.asList(info.getSelectedElements());

							found = objType == info.getType() && StringUtility.equals(formName, info.getName()) && fields.contains(objName);
						}
						else if(info.getType() == StructItemInfo.SCHEMA && StringUtility.equals(formName, info.getName())) formsubstructure = true;
					}
				}
			}
			else found = true;

			if(found || formsubstructure)
			{
				ObjectWrapper wrapper = getObjectWrapper(object);
				boolean exists = false;

				if(wrapper instanceof FormRelatedWrapper)
				{
					String formName = null;

					if(object instanceof Field) formName = ((Field)object).getForm();
						else if(object instanceof View) formName = ((View)object).getFormName();

					exists = ((FormRelatedWrapper) wrapper).exists(formName, object.getName());
				}
				else exists = wrapper.exists(object.getName());

				try
				{
					if(!exists)
					{
						if(object instanceof Form) session.getContext().createForm((Form)object);
						else if(object instanceof Field) session.getContext().createField((Field) object, ((Field) object).getFieldID() < 1000);
						else if(object instanceof ActiveLink) session.getContext().createActiveLink((ActiveLink)object);
						else if(object instanceof Filter) session.getContext().createFilter((Filter)object);
						else if(object instanceof ActiveLinkGuide) session.getContext().createContainer((ActiveLinkGuide)object);
						else if(object instanceof FilterGuide) session.getContext().createContainer((FilterGuide)object);
						else if(object instanceof Escalation) session.getContext().createEscalation((Escalation)object);
						else if(object instanceof ApplicationContainer) session.getContext().createContainer((ApplicationContainer) object);
						else if(object instanceof Menu) session.getContext().createMenu((Menu) object);
						else if(object instanceof View) session.getContext().createView((View) object);
						else if(object instanceof Image) session.getContext().createImage((Image) object);
						else if(object instanceof Container) session.getContext().createContainer((Container)object);
						else RuntimeLogger.warn("Invalid object type to create workflow object " + (object != null ? "'" + object.getName() + "'" : "") + ": " + object);
					}
					else
					{
						if(overwrite)
						{
							if(wrapper instanceof FormRelatedWrapper) object.getChangeFlags().resetCriteriaFlags();
								else object.getChangeFlags().setRetrieveAll(true);

							if(object instanceof Form) session.getContext().setForm((Form) object);
							else if(object instanceof Field) session.getContext().setField((Field) object);
							else if(object instanceof ActiveLink) session.getContext().setActiveLink((ActiveLink) object);
							else if(object instanceof Filter) session.getContext().setFilter((Filter) object);
							else if(object instanceof ActiveLinkGuide) session.getContext().setContainer((ActiveLinkGuide) object);
							else if(object instanceof FilterGuide) session.getContext().setContainer((FilterGuide) object);
							else if(object instanceof Escalation) session.getContext().setEscalation((Escalation) object);
							else if(object instanceof ApplicationContainer) session.getContext().setContainer((ApplicationContainer) object);
							else if(object instanceof Menu) session.getContext().setMenu((Menu) object);
							else if(object instanceof View) session.getContext().setView((View) object);
							else if(object instanceof Image) session.getContext().setImage((Image) object);
							else if(object instanceof Container) session.getContext().setContainer((Container) object);
							else RuntimeLogger.warn("Invalid object type to update workflow object" + (object != null ? "'" + object.getName() + "'" : "") + ": " + object);
						}
						else
						{
							if(object instanceof View) RuntimeLogger.warn("View object already exists: " + object.getName() + " on " + ((View)object).getFormName());
								else if(object instanceof Field) RuntimeLogger.warn("Field object already exists: " + object.getName() + " on " + ((Field)object).getForm());
									else RuntimeLogger.warn(StringUtility.capitalize(getObjectTypeNameByObjectTypeId(getObjectTypeId(object))) + " object already exists: " + object.getName());
						}
					}
				}
				catch(ARException are)
				{
					RuntimeLogger.error("Error importing " + getObjectTypeNameByObjectTypeId(getObjectTypeId(object)) + " object '" + object.getName() + "': " + are.getMessage());
					logger.debug("Exception", are);
				}
			}
		}
	}

	/**
	 * Get the object instance for the corresponding object type.
	 *
	 * @return an object type for a definition object
	 */
	public ObjectWrapper getWrapperInstance()
	{
		return null;
	}

	protected int getImportOptions()
	{
		int options = Constants.AR_IMPORT_OPT_CREATE;
		boolean overwrite = getConfiguration().getBoolean("overwrite", false);
		boolean deleteExcessFields = getConfiguration().getBoolean("delexcessfields", false);
		boolean deleteExcessViews = getConfiguration().getBoolean("delexcessviews", false);
		boolean deletePermissions = getConfiguration().getBoolean("overwritepermissions", false);
		boolean replaceDisplayProperties = getConfiguration().getBoolean("replacedisplayprops", false);
		boolean replaceFullTextOption = getConfiguration().getBoolean("replacefulltextoption", false);

		String conflict = getConfiguration().getString("conflict", null);

		if(overwrite) options = Constants.AR_IMPORT_OPT_OVERWRITE;
		if(StringUtility.equalsIgnoreCase(conflict, "noaction")) options = options | Constants.AR_IMPORT_OPT_HANDLE_CONFLICT_NO_ACTION;
		else if(StringUtility.equalsIgnoreCase(conflict, "error")) options = options | Constants.AR_IMPORT_OPT_HANDLE_CONFLICT_ERROR;
		else if(StringUtility.equalsIgnoreCase(conflict, "overwrite")) options = options | Constants.AR_IMPORT_OPT_HANDLE_CONFLICT_OVERWRITE;

		if(overwrite && !deleteExcessFields) options = options | Constants.AR_IMPORT_OPT_NOT_DELETE_FIELD;
		if(overwrite && !deleteExcessViews) options = options | Constants.AR_IMPORT_OPT_NOT_DELETE_VUI;
		if(overwrite && !deletePermissions) options = options | Constants.AR_IMPORT_OPT_NOT_OVERWRITE_PERMISSION;
		if(overwrite && !replaceDisplayProperties) options = options | Constants.AR_IMPORT_OPT_OVERWRITE_DISP_PROPS;
		if(overwrite && !replaceFullTextOption) options = options | Constants.AR_IMPORT_OPT_OVERWRITE_FULL_TEXT_OPTION;

		return options;
	}

	public String help()
	{
		return super.help() + " [onlyoverlays] [-overwrite [-delexcessfields] [-delexcessviews] [-overwritepermissions]] [-conflict <noaction|error|overwrite>]";
	}
}
