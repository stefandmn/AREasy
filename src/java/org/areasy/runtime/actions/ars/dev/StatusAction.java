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
import org.areasy.runtime.RuntimeAction;
import org.areasy.runtime.engine.RuntimeLogger;

import java.util.List;

/**
 * Abstract runtime action to enable or disable one or more definition objects.
 *
 */
public class StatusAction extends DefinitionAction implements RuntimeAction
{
	private boolean enable = false;
	private boolean disable = false;

	/**
	 * Convert all found objects in the definition code and write this output in the definition file.
	 *
	 * @param objects a list with all found objects
	 */
	public void execute(List objects)
	{
		enable = getConfiguration().getBoolean("enable", false);
		disable = getConfiguration().getBoolean("disable", false);

		super.execute(objects);
	}

	/**
	 * Commit changes made in the specified object instance.
	 *
	 * @param object managed object instance.
	 * @throws com.bmc.arsys.api.ARException if the object factory will return an error
	 */
	public void execute(ObjectBase object) throws ARException
	{
		if(object instanceof ActiveLink)
		{
			ActiveLink data = (ActiveLink)object;

			if(hasToBecomeEnabled()) data.setEnable(true);
				else if(hasToBecomeDisabled()) data.setEnable(false);

			getServerConnection().getContext().setActiveLink(data);
			RuntimeLogger.info(getTextFromObjectBase(object) + " is now " + (hasToBecomeEnabled() ? "enabled" : "disabled"));
		}
		else if(object instanceof Filter)
		{
			Filter data = (Filter)object;

			if(hasToBecomeEnabled()) data.setEnable(true);
				else if(hasToBecomeDisabled()) data.setEnable(false);

			getServerConnection().getContext().setFilter(data);
			RuntimeLogger.info(getTextFromObjectBase(object) + " is now " + (hasToBecomeEnabled() ? "enabled" : "disabled"));
		}
		else if(object instanceof Escalation)
		{
			Escalation data = (Escalation)object;

			if(hasToBecomeEnabled()) data.setEnable(true);
				else if(hasToBecomeDisabled()) data.setEnable(false);

			getServerConnection().getContext().setEscalation(data);
			RuntimeLogger.info(getTextFromObjectBase(object) + "' is now " + (hasToBecomeEnabled() ? "enabled" : "disabled"));
		}
		else RuntimeLogger.warn("Object signature is not recognized for enable/disable action: " + getTextFromObjectBase(object));
	}

	public boolean hasToBecomeEnabled()
	{
		return enable;
	}

	public boolean hasToBecomeDisabled()
	{
		return disable;
	}

	public void setEnable()
	{
		enable = true;
		disable = false;
	}

	public void setDisable()
	{
		disable = true;
		enable = false;
	}
}
