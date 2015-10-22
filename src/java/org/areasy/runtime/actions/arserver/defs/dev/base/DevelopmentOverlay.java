package org.areasy.runtime.actions.arserver.defs.dev.base;

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

import com.bmc.arsys.api.ObjectBase;
import org.areasy.runtime.actions.arserver.defs.dev.BaseDevelopment;
import org.areasy.runtime.actions.arserver.defs.dev.DevelopmentPackageAction;
import org.areasy.runtime.actions.arserver.defs.dev.DevelopmentPackageObject;
import org.areasy.runtime.actions.arserver.defs.OverlayAction;
import org.areasy.runtime.actions.arserver.defs.wrappers.FormRelatedWrapper;
import org.areasy.runtime.actions.arserver.defs.wrappers.ObjectWrapper;
import org.areasy.runtime.engine.RuntimeLogger;
import org.areasy.common.support.configuration.Configuration;

import java.util.List;

/**
 * This class allows you to mark as overlay all selected workflow objects by a dictionary files
 * or repository.
 */
public class DevelopmentOverlay extends BaseDevelopment
{
	public DevelopmentOverlay(Configuration config, List objmap)
	{
		super(config, objmap);
	}

	public void perform(DevelopmentPackageAction develop)
	{
		//prepare data for export
		for(int i = 0; getObjectsList() != null && i < getObjectsList().size(); i++)
		{
			String objname = null;
			String objtype = null;

			try
			{
				//read developed object
				DevelopmentPackageObject devobj = (DevelopmentPackageObject) getObjectsList().get(i);

				objname = devobj.getObjectName();
				objtype = devobj.getSignature();

				//take the corresponding action
				OverlayAction action = new OverlayAction();
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
				RuntimeLogger.error("Error running overlay action for a '" + objname + "' definition: " + th.getMessage());
				getLogger().debug("Exception", th);
			}
		}

		RuntimeLogger.info("Overlay action for the specified development package has been done");
	}
}
