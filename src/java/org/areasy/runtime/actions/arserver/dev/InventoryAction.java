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

import com.bmc.arsys.api.StructItemInfo;
import org.areasy.runtime.RuntimeAction;
import org.areasy.runtime.actions.AbstractAction;
import org.areasy.runtime.engine.RuntimeLogger;
import org.areasy.common.data.StringUtility;

import java.io.File;
import java.util.List;

/**
 * Runtime action to export server abstract objects into a definitions file.
 */
public class InventoryAction extends DefinitionAction implements RuntimeAction
{
	/**
	 * Convert all found objects in the definition code and write this output in the definition file.
	 *
	 * @param objects a list with all found objects
	 */
	public void execute(List objects)
	{
		//validate objects list
		if(objects == null || objects.isEmpty())
		{
			RuntimeLogger.error("Objects list is null");
			return;
		}

		setInventory2File(objects);
	}

	/**
	 * Get workflow object inventory content to be saved in a TXT file.
	 *
	 * @param objects list of object that have to be published in inventory
	 * <code>[object type]:\t[object name]</code>
	 */
	protected void setInventory2File(List objects)
	{
		boolean append = getConfiguration().getBoolean("append", false);
		StringBuilder inventoryContent = new StringBuilder();
		String fileName = getOutputFileName("txt");

		if(append)
		{
			File file = new File(fileName);

			if(file.exists())
			{
				inventoryContent.append(AbstractAction.readTextFile(fileName).trim());
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

			//append objects in file, one by one.
			String objType = getObjectTypeNameByObjectTypeId(info.getType()) + ":";
			inventoryContent.append(objType);
			for(int o = 1; o < Math.max(4 - objType.length() / 4, 1); o++) inventoryContent.append("\t");
			inventoryContent.append(text);
			inventoryContent.append("\n");
		}

		try
		{
			//write inventory
			AbstractAction.writeTextFile(fileName, inventoryContent.toString());
			RuntimeLogger.info("Workflow inventory has been exported in file: " + fileName);
		}
		catch (Throwable th)
		{
			RuntimeLogger.error("Error exporting definitions: " + th.getMessage());
			getLogger().debug("Exception", th);
		}
	}

	public String help()
	{
		return super.help() + " -inventory";
	}
}
