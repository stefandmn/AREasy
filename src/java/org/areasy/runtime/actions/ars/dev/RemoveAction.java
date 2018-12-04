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
import org.areasy.common.data.StringUtility;
import org.areasy.runtime.RuntimeAction;
import org.areasy.runtime.engine.RuntimeLogger;

/**
 * Abstract runtime action to remove one or more definition objects.
 *
 */
public class RemoveAction extends DefinitionAction implements RuntimeAction
{
	/**
	 * Commit changes made in the specified object instance.
	 *
	 * @param object managed object instance.
	 * @throws com.bmc.arsys.api.ARException if the object factory will return an error
	 */
	public void execute(ObjectBase object) throws ARException
	{
		if(!StringUtility.equals(object.getNewName(), object.getName()))
		{
			if(object instanceof ActiveLink)
			{
				ActiveLink objdata = (ActiveLink) object;
				getServerConnection().getContext().deleteActiveLink(objdata.getName(), Constants.AR_DEFAULT_DELETE_OPTION);
			}
			else if(object instanceof Filter)
			{
				Filter objdata = (Filter) object;
				getServerConnection().getContext().deleteFilter(objdata.getName(), Constants.AR_DEFAULT_DELETE_OPTION);
			}
			else if(object instanceof Escalation)
			{
				Escalation objdata = (Escalation) object;
				getServerConnection().getContext().deleteEscalation(objdata.getName(), Constants.AR_DEFAULT_DELETE_OPTION);
			}
			else if(object instanceof Container)
			{
				Container objdata = (Container) object;
				getServerConnection().getContext().deleteContainer(objdata.getName(), Constants.AR_DEFAULT_DELETE_OPTION);
			}
			else if(object instanceof Form)
			{
				Form objdata = (Form) object;
				getServerConnection().getContext().deleteForm(objdata.getName(), Constants.AR_SCHEMA_FORCE_DELETE);
			}
			else if(object instanceof Image)
			{
				Image objdata = (Image) object;
				getServerConnection().getContext().deleteImage(objdata.getName(), true);
			}
			else if(object instanceof Menu)
			{
				Menu objdata = (Menu) object;
				getServerConnection().getContext().deleteMenu(objdata.getName(), Constants.AR_DEFAULT_DELETE_OPTION);
			}
			else if(object instanceof View)
			{
				View objdata = (View) object;
				getServerConnection().getContext().deleteView(objdata.getFormName(), objdata.getVUIId());
			}
			else if(object instanceof Field)
			{
				Field objdata = (Field) object;
				getServerConnection().getContext().deleteField(objdata.getForm(), objdata.getFieldID(), Constants.AR_FIELD_FORCE_DELETE);
			}

			RuntimeLogger.info("Object name '" + object.getName() + "' has been removed");
		}
	}
}
