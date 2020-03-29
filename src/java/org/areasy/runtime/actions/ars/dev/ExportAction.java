package org.areasy.runtime.actions.ars.dev;

/*
 * Copyright (c) 2007-2020 AREasy Runtime
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
import org.areasy.common.data.StringUtility;
import org.areasy.runtime.RuntimeAction;
import org.areasy.runtime.RuntimeManager;
import org.areasy.runtime.actions.AbstractAction;
import org.areasy.runtime.actions.ars.dev.wrappers.FormRelatedWrapper;
import org.areasy.runtime.actions.ars.dev.wrappers.ObjectWrapper;
import org.areasy.runtime.engine.RuntimeLogger;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.List;
import java.util.Vector;

/**
 * Runtime action to export server abstract objects into a definitions file.
 */
public class ExportAction extends DefinitionAction implements RuntimeAction
{
	/**
	 * Convert all found objects in the definition code and write this output in the definition file.
	 *
	 * @param objects a list with all found objects
	 */
	public void execute(List objects)
	{
		boolean inventory = getConfiguration().getBoolean("inventory", false);
		boolean append = getConfiguration().getBoolean("append", false);
		String format = getOutputFileFormat();
		String fileName = getOutputFileName(format);

		//validate objects list
		if(objects == null || objects.isEmpty())
		{
			RuntimeLogger.error("Objects list is null");
			return;
		}

		try
		{
			if(inventory)
			{
				String inventoryContent = getInventory(fileName, append, objects);

				//write inventory
				AbstractAction.writeTextFile(fileName, inventoryContent);
				RuntimeLogger.info("Workflow inventory has been exported in file: " + fileName);
			}
			else
			{
				if(StringUtility.equals(format, "xml") || StringUtility.equals(format, "def"))
				{
					boolean asxml = format.equals("xml");
					recognizer(objects);

					getServerConnection().getContext().exportDefToFile(objects, asxml, fileName, !append);
					RuntimeLogger.info("Workflow objects have been exported in file: " + fileName);
				}
				else if(StringUtility.equals(format, "bin"))
				{
					exportBinary(objects, fileName);
					RuntimeLogger.info("Workflow objects have been exported in file: " + fileName);
				}
				else RuntimeLogger.error("Export file format is invalid: " + format);
			}
		}
		catch (Throwable th)
		{
			RuntimeLogger.error("Error exporting definitions: " + th.getMessage());
			getLogger().debug("Exception", th);
		}
	}

	/**
	 * Commit changes made in the specified object instance.
	 *
	 * @param object managed object instance.
	 * @throws com.bmc.arsys.api.ARException if the object factory will return an error
	 */
	public void execute(ObjectBase object) throws ARException
	{
		if(object != null)
		{
			boolean append = getConfiguration().getBoolean("append", false);
			String format = getOutputFileFormat();
			String fileName = getOutputFileName(format);

			try
			{
				List<StructItemInfo> objects = new Vector<StructItemInfo>();
				StructItemInfo info = new StructItemInfo(getObjectTypeId(object), object.getName(), null);
				objects.add(info);

				if(StringUtility.equals(format, "xml") || StringUtility.equals(format, "def"))
				{
					boolean asxml = format.equals("xml");

					getServerConnection().getContext().exportDefToFile(objects, asxml, fileName, !append);
					RuntimeLogger.debug("Workflow object '" + object.getName() + "' is exported in file: " + fileName);
				}
				else if(StringUtility.equals(format, "bin"))
				{
					exportBinary(objects, fileName);
					RuntimeLogger.debug("Workflow object '" + object.getName() + "' is exported in file: " + fileName);
				}
				else RuntimeLogger.error("Export file format is invalid: " + format);
			}
			catch (Throwable th)
			{
				RuntimeLogger.error("Error exporting '" + object.getName() + "' definition object: " + th.getMessage());
				getLogger().debug("Exception", th);
			}
		}
	}

	/**
	 * Get workflow object inventory content to be saved in a TXT file.
	 *
	 * @param fileName file name that will contain this inventory
	 * @param append in case of the exists, the content will be appended
	 * @param objects list of object that have to be published in inventory
	 * @return inventory content containing line by line the workflow object in the following format:
	 * <code>[object type]:\t[object name]</code>
	 */
	protected String getInventory(String fileName, boolean append, List objects)
	{
		StringBuffer inventoryContent = new StringBuffer();

		if(append)
		{
			File file = new File(fileName);

			if(file.exists())
			{
				inventoryContent.append(AbstractAction.readTextFile(fileName));
				inventoryContent.append("\n");
			}
		}

		//append data
		for (int i = 0; objects != null && i < objects.size(); i++)
		{
			StructItemInfo info = (StructItemInfo) objects.get(i);
			String text = null;

			if(info.getType() == StructItemInfo.VUI || info.getType() == StructItemInfo.FIELD)
			{
				text = info.getName();
				String elements[] = info.getSelectedElements();

				text += " (" + StringUtility.join(elements, ',') + ")";
			}
			else text = info.getName();

			//append objects one by one.
			inventoryContent.append(getObjectTypeNameByObjectTypeId(info.getType())).append(":\t").append(text);
			inventoryContent.append("\n");
		}

		return inventoryContent.toString();
	}

	/**
	 * This method export workflow objects in binary mode that means to serialize them and than to
	 * save them into a binary file.
	 *
	 * @param data list of <code>StructItemInfo</code> object references
	 * @param fileName file name and path that should store the workflow objects
	 * @throws IOException in case of any IO exception occur
	 * @throws ARException in case of any ARSystem exception will occur
	 */
	protected void exportBinary(List data, String fileName) throws IOException, ARException
	{
		boolean formsubstructures = getConfiguration().getBoolean("formsubstructures", false);

		// Serialize data object to a file
		ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(fileName));

		for(int i = 0; data != null && i < data.size(); i++)
		{
			StructItemInfo info = (StructItemInfo) data.get(i);
			ObjectWrapper wrapper = getObjectWrapper(info);

			if(wrapper instanceof FormRelatedWrapper)
			{
				Object objects[] = ((FormRelatedWrapper)wrapper).getInstance(info.getName(), info.getSelectedElements());

				for(int x = 0; objects != null && x < objects.length; x++ )
				{
					out.writeObject(objects[x]);
				}
			}
			else
			{
				Object object = wrapper.getInstance(info.getName());
				out.writeObject(object);

				if(formsubstructures && (object instanceof Form))
				{
					ViewCriteria criteria = new ViewCriteria();
					criteria.setRetrieveAll(true);

					List views = getServerConnection().getContext().getListViewObjects(((Form)object).getName(), 0, criteria);
					for(int j = 0; views != null && j < views.size(); j++) out.writeObject(views.get(j));

					List fields = getServerConnection().getContext().getListFieldObjects(((Form) object).getName());
					for(int j = 0; fields != null && j < fields.size(); j++) out.writeObject(fields.get(j));
				}
			}

			out.flush();
		}

		out.close();
	}

	/**
	 * Read or detect the export format. This format could be explicitly specified using <code>-format [def|xml|bin]</code> option
	 * or could be detected based on the output file extension.
	 *
	 * @return the format of export file, that could be: def, xml or bin
	 */
	protected String getOutputFileFormat()
	{
		String format = getConfiguration().getString("format", null);

		if(format == null)
		{
			String fileName = getConfiguration().getString("outputfile", null);

			if(fileName != null)
			{
				int index = fileName.lastIndexOf('.');
				String ext = index < fileName.length() - 1 ? fileName.substring(index + 1) : null;

				if(ext != null && (StringUtility.equalsIgnoreCase(ext, "def") || StringUtility.equalsIgnoreCase(ext, "xml") || StringUtility.equalsIgnoreCase(ext, "bin"))) format = ext.toLowerCase();
			}

			if(format == null) format = "def";
		}
		else format = format.toLowerCase();

		return format;
	}

	/**
	 * get and set the file name that have to be considered for export action. Several validations will be done
	 * <ul>
	 *     <li>file name in case of the input is null</li>
	 *     <li>file name for export format or in case of inventory</li>
	 *     <li>file object in case of append operation</li>
	 * </ul>
	 * @param format file format (or file extension)
	 * @return validated file name
	 */
	protected String getOutputFileName(String format)
	{
		String fileName = getConfiguration().getString("outputfile", null);
		boolean inventory = getConfiguration().getBoolean("inventory", false);
		boolean append = getConfiguration().getBoolean("append", false);

		if(fileName == null)
		{
			fileName = RuntimeManager.getWorkingDirectory() + File.separator + "out-" + RuntimeLogger.getChannelName();
			if(inventory) fileName += ".txt";
		}

		if(!inventory && !fileName.endsWith(format)) fileName += "." + format;

		if(!append)
		{
			File file = new File(fileName);
			if(file.exists()) file.delete();
		}

		return fileName;
	}
}
