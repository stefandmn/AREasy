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
import org.areasy.runtime.actions.ars.dev.tools.flow.WorksheetEvent;
import org.areasy.runtime.actions.ars.dev.tools.flow.WorksheetObject;
import org.areasy.runtime.actions.ars.dev.tools.flow.DevProcessAction;
import org.areasy.runtime.actions.ars.dev.RenameAction;
import org.areasy.runtime.actions.ars.dev.wrappers.FormRelatedWrapper;
import org.areasy.runtime.actions.ars.dev.wrappers.ObjectWrapper;
import org.areasy.runtime.engine.RuntimeLogger;
import org.areasy.common.support.configuration.Configuration;

import java.util.List;

/**
 * This class allows you to rename selected workflow objects by a dictionary files
 * or repository.
 */
public class WorksheetRenameEvent extends WorksheetEvent
{
	public WorksheetRenameEvent(Configuration config, List objmap)
	{
		super(config, objmap);
	}

	public void perform(DevProcessAction develop)
	{
		//prepare data for export
		for(int i = 0; getObjectsList() != null && i < getObjectsList().size(); i++)
		{
			String objname = null;
			String objtype = null;
			String newname = null;

			try
			{
				//read developed object
				WorksheetObject devobj = (WorksheetObject) getObjectsList().get(i);

				objname = devobj.getObjectName();
				objtype = devobj.getSignature();

				//take the corresponding action
				RenameAction action = new RenameAction();
				action.init(develop);

				//get the base object instance and run the action
				String objSignature = getPluralObjectTypeNameBySignature(objtype);
				ObjectWrapper wrapper = action.getObjectWrapper(objSignature);

				if(wrapper instanceof FormRelatedWrapper)
				{
					((FormRelatedWrapper) wrapper).setFormName(objname);
					objname = devobj.getRelatedData();

					if(objname.contains("@"))
					{
						objname = objname.substring(0, objname.indexOf("@", 0));
						newname = objname.substring(objname.indexOf("@", 0) + 1);
					}
				}
				else newname = devobj.getRelatedData();

				ObjectBase object = wrapper.getInstance(objname);

				if(newname != null)
				{
					object.setNewName(devobj.getRelatedData());
					action.execute(object);
				}
				else RuntimeLogger.warn("Object name " + devobj.getObjectName() + "' can not be changed because the new name is null");
			}
			catch(Throwable th)
			{
				RuntimeLogger.error("Error running rename action for a '" + objname + "' definition: " + th.getMessage());
				getLogger().debug("Exception", th);
			}
		}

		RuntimeLogger.info("Rename action for the specified development package has been done");
	}
}
