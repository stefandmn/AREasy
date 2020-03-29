package org.areasy.runtime.actions.ars.dev.tools.process.events;

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

import com.bmc.arsys.api.ObjectBase;
import org.areasy.common.data.StringUtility;
import org.areasy.common.support.configuration.Configuration;
import org.areasy.runtime.actions.ars.dev.RemoveAction;
import org.areasy.runtime.actions.ars.dev.tools.ProcessWorksheetAction;
import org.areasy.runtime.actions.ars.dev.tools.process.WorksheetEvent;
import org.areasy.runtime.actions.ars.dev.tools.process.WorksheetObject;
import org.areasy.runtime.actions.ars.dev.wrappers.FormRelatedWrapper;
import org.areasy.runtime.actions.ars.dev.wrappers.ObjectWrapper;
import org.areasy.runtime.engine.RuntimeLogger;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * This class allows you to delete selected workflow objects by a dictionary files
 * or repository. The removal action is applied to all selected objects that are defined in
 * the target ARSystem server.
 */
public class WorksheetRemoveEvent extends WorksheetEvent
{
	public WorksheetRemoveEvent(Configuration config, List objmap)
	{
		super(config, objmap);
	}

	public void perform(ProcessWorksheetAction develop)
	{
		//reorder objects list to handle forms to the end of data processing procedure
		if(getObjectsList() != null && !getObjectsList().isEmpty())
		{

			List<WorksheetObject> objects = getObjectsList();
			Collections.sort(objects, new DevelopmentPackageObjectComparator());

			//run remove action for each object
			for(int i = 0; objects != null && i < objects.size(); i++)
			{
				String objname = null;
				String objtype = null;

				try
				{
					//read developed object
					WorksheetObject devobj = objects.get(i);

					objname = devobj.getObjectName();
					objtype = devobj.getSignature();

					//take the corresponding action
					RemoveAction action = new RemoveAction();
					action.init(develop);

					//get the base object instance and run the action
					String objSignature = getPluralObjectTypeNameBySignature(objtype);
					ObjectWrapper wrapper = action.getObjectWrapper(objSignature);

					if(wrapper instanceof FormRelatedWrapper)
					{
						((FormRelatedWrapper) wrapper).setFormName(objname);
						objname = devobj.getRelatedData();
					}

					ObjectBase object = wrapper.getInstance(objname);

					action.execute(object);
				}
				catch(Throwable th)
				{
					RuntimeLogger.error("Error running remove action for a '" + objname + "' definition: " + th.getMessage());
					getLogger().debug("Exception", th);
				}
			}
		}

		RuntimeLogger.info("Remove action for the specified development package has been executed");
	}

	public class DevelopmentPackageObjectComparator implements Comparator<WorksheetObject>
	{
		public int compare(WorksheetObject o1, WorksheetObject o2)
		{
			if(!StringUtility.equalsIgnoreCase(o1.getSignature(), WorksheetEvent.OBJ_FORM) && StringUtility.equalsIgnoreCase(o2.getSignature(), WorksheetEvent.OBJ_FORM)) return -1;
			else if(StringUtility.equalsIgnoreCase(o1.getSignature(), WorksheetEvent.OBJ_FORM) && !StringUtility.equalsIgnoreCase(o2.getSignature(), WorksheetEvent.OBJ_FORM)) return 1;
			else if(!StringUtility.equalsIgnoreCase(o1.getSignature(), WorksheetEvent.OBJ_FORM) && !StringUtility.equalsIgnoreCase(o2.getSignature(), WorksheetEvent.OBJ_FORM)) return 0;
			else if(StringUtility.equalsIgnoreCase(o1.getSignature(), WorksheetEvent.OBJ_FORM) && StringUtility.equalsIgnoreCase(o2.getSignature(), WorksheetEvent.OBJ_FORM)) return 0;
			else return 0;
		}
	}
}
