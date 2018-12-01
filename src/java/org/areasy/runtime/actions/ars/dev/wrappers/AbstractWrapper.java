package org.areasy.runtime.actions.ars.dev.wrappers;

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

import com.bmc.arsys.api.ARException;
import com.bmc.arsys.api.ObjectBase;
import org.areasy.runtime.actions.ars.dev.DefinitionAction;
import org.areasy.runtime.engine.base.ServerConnection;
import org.areasy.common.support.configuration.Configuration;

/**
 * Default and abstract implementation of <code>AbstractModel</code>
 */
public abstract class AbstractWrapper implements ObjectWrapper
{
	private DefinitionAction action = null;

	protected AbstractWrapper()
	{
		//nothing to do here
	}

	/**
	 * The initialization procedure for any type of object.
	 *
	 * @param action runtime action which are calling this object
	 */
	protected final void init(DefinitionAction action)
	{
		this.action = action;
	}

	/**
	 * Get ARS server connection retrieved by the action.
	 * @return AR System server connection
	 */
	protected final ServerConnection getServerConnection()
	{
		if(action != null) return action.getServerConnection();
			else return null;
	}

	/**
	 * Get ARS server configuration retrieved by the action.
	 * @return AR System server configuration
	 */
	protected final Configuration getConfiguration()
	{
		if(action != null) return action.getConfiguration();
			else return null;
	}

	/**
	 * Check if an object instance exists with the specified name
	 *
	 * @param name object name
	 * @return true if the object exists
	 */
	public boolean exists(String name)
	{
		ObjectBase object;

		try
		{
			object = getInstance(name);
		}
	    catch(ARException are)
		{
			object = null;
		}

		return object != null;
	}
}
