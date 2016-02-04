package org.areasy.runtime.actions.arserver.defs;

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
import org.areasy.common.data.StringUtility;
import org.areasy.runtime.RuntimeAction;
import org.areasy.runtime.engine.RuntimeLogger;

import java.util.List;

/**
 * Associate forms with workflow objects (filters, escalation, active links and also containers)
 */
public class SetFormsAction extends DefinitionAction implements RuntimeAction
{
	private String addForm = null;
	private String delForm = null;

	/**
	 * Convert all found objects in the definition code and write this output in the definition file.
	 *
	 * @param objects a list with all found objects
	 */
	public void execute(List objects)
	{
		addForm = getConfiguration().getString("addform", null);
		delForm = getConfiguration().getString("delform", null);

		super.execute(objects);
	}

	/**
	 * Commit changes made in the specified object instance.
	 *
	 * @param object managed object instance.
	 * @throws ARException if the object factory will return an error
	 */
	public void execute(ObjectBase object) throws ARException
	{
		boolean changed = false;

		if(object instanceof ActiveLink)
		{
			ActiveLink data = (ActiveLink)object;
			List forms = data.getFormList();

			if(StringUtility.isNotEmpty(addForm) && !forms.contains(addForm))
			{
				forms.add(addForm);
				changed = true;
			}

			if(StringUtility.isNotEmpty(delForm) && forms.contains(delForm))
			{
				forms.remove(delForm);
				changed = true;
			}

			if(changed)
			{
				data.setFormList(forms);
				getServerConnection().getContext().setActiveLink(data);
			}
		}
		else if(object instanceof Filter)
		{
			Filter data = (Filter)object;
			List forms = data.getFormList();

			if(StringUtility.isNotEmpty(addForm) && !forms.contains(addForm))
			{
				forms.add(addForm);
				changed = true;
			}

			if(StringUtility.isNotEmpty(delForm) && forms.contains(delForm))
			{
				forms.remove(delForm);
				changed = true;
			}

			if(changed)
			{
				data.setFormList(forms);
				getServerConnection().getContext().setFilter(data);
			}
		}
		else if(object instanceof Escalation)
		{
			Escalation data = (Escalation)object;
			List forms = data.getFormList();

			if(StringUtility.isNotEmpty(addForm) && !forms.contains(addForm))
			{
				forms.add(addForm);
				changed = true;
			}

			if(StringUtility.isNotEmpty(delForm) && forms.contains(delForm))
			{
				forms.remove(delForm);
				changed = true;
			}

			if(changed)
			{
				data.setFormList(forms);
				getServerConnection().getContext().setEscalation(data);
			}
		}
		else if(object instanceof Container)
		{
			Container data = (Container)object;
			List forms = data.getContainerOwner();

			if (StringUtility.isNotEmpty(addForm))
			{
				ContainerOwner co = new ContainerOwner(ContainerOwner.SCHEMA, addForm);

				if(!forms.contains(co))
				{
					forms.add(co);
					changed = true;
				}
			}

			if (StringUtility.isNotEmpty(delForm) && forms.contains(delForm))
			{
				ContainerOwner co = new ContainerOwner(ContainerOwner.SCHEMA, delForm);

				if(forms.contains(co))
				{
					forms.remove(co);
					changed = true;
				}
			}

			if (changed)
			{
				data.setContainerOwner(forms);
				getServerConnection().getContext().setContainer(data);
			}
		}
		else RuntimeLogger.warn("Object signature is not recognized for associating forms action: " + getTextFromObjectBase(object));

		if (changed) RuntimeLogger.info(getTextFromObjectBase(object) + (StringUtility.isNotEmpty(addForm) ? " has been associated to '" + addForm + "' form" : " has been de-associated by '" + delForm + "' form"));
			else if(object instanceof ActiveLink || object instanceof Filter || object instanceof Escalation || object instanceof Container) RuntimeLogger.debug(getTextFromObjectBase(object) + (StringUtility.isNotEmpty(addForm) ? " is already associated to '" + addForm + "' form" : " is already de-associated by '" + delForm + "' form"));
	}
}
