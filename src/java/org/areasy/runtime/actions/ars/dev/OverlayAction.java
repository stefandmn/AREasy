package org.areasy.runtime.actions.ars.dev;

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

import com.bmc.arsys.api.*;
import org.areasy.runtime.RuntimeAction;
import org.areasy.runtime.actions.ars.dev.wrappers.FormRelatedWrapper;
import org.areasy.runtime.actions.ars.dev.wrappers.ObjectWrapper;
import org.areasy.runtime.engine.RuntimeLogger;

import java.util.Arrays;
import java.util.List;

/**
 * Abstract runtime action to mark as overlay one or more definition objects.
 *
 */
public class OverlayAction extends DefinitionAction implements RuntimeAction
{
	/**
	 * Commit changes made in the specified structure info instance.
	 *
	 * @param objectInfo managed <code>StructItemInfo</code> instance.
	 * @throws com.bmc.arsys.api.ARException if the object factory will return an error
	 */
	public void execute(StructItemInfo objectInfo) throws ARException
	{
		ObjectWrapper wrapper = getObjectWrapper(objectInfo);

		if(wrapper instanceof FormRelatedWrapper)
		{
			String formname = objectInfo.getName();
			String fields[] = objectInfo.getSelectedElements();

			ObjectBase objects[] = ((FormRelatedWrapper)wrapper).getInstance(formname, fields);
			execute(Arrays.asList(objects));
		}
		else
		{
			ObjectBase object = wrapper.getInstance(objectInfo.getName());
			execute(object);
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
		if(object == null) return;

		String objectName = object.getName();
		int objectType = getObjectTypeId(object);
		String formName = object instanceof Field ? ((Field)object).getForm() : object instanceof View ? ((View)object).getFormName() : null;

		OverlaidInfo overlay = new OverlaidInfo(objectName, objectType, formName, 1, null);
		ARServerUser connectedUserInfo = getServerConnection().getContext();

		connectedUserInfo.createOverlay(overlay);
	}

	/**
	 * Execute general action using bulk data-structures. This method will execute the corresponding method from the subclass.
	 *
	 * @param objects a list with all found objects
	 */
	public void execute(List objects)
	{
		for (int i = 0; objects!= null && i < objects.size(); i++)
		{
			Object object = objects.get(i);

			try
			{
				if(object instanceof StructItemInfo) execute((StructItemInfo)object);
					else if(object instanceof ObjectBase) execute((ObjectBase)object);
						else RuntimeLogger.warn("Object signature is not recognized to be processed for overlay: " + object);
			}
			catch (Throwable th)
			{
				RuntimeLogger.error("Error processing '" + object + "' object: " + th.getMessage());
				getLogger().debug("Exception", th);
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
}
