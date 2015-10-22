package org.areasy.runtime.actions.arserver.defs.wrappers;

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

import com.bmc.arsys.api.*;
import org.areasy.runtime.actions.arserver.defs.DefinitionAction;
import org.areasy.runtime.engine.RuntimeLogger;

import java.util.Date;
import java.util.List;

/**
 * Container definition object implementation.
 * Container object means: guide (filters or active links), applications or other type of non standard object.
 *
 */
public class ContainerWrapper extends AbstractWrapper implements ObjectWrapper
{
	public ContainerWrapper()
	{
		//nothing to do here
	}

	/**
	 * Dedicated method to instantiate a definition object
	 *
	 * @param action runtime action which are calling this object
	 */
	public ContainerWrapper(DefinitionAction action)
	{
		init(action);
	}

	/**
	 * Get an array with the keys for all found objects
	 *
	 * @param since changed since
	 * @return object's keys list
	 */
	public List<String> find(Date since)
	{
		try
		{
			return getServerConnection().getContext().getListContainer((since != null ? new Timestamp(since).getValue() : 0), getContainerTypes(), true, null, null);
		}
		catch(ARException are)
		{
			return null;
		}
	}

	/**
	 * Get the object name instance. In case of the instance is not defined the workflow
	 * will return an exception.
	 *
	 * @param name object name
	 * @return object instance
	 * @throws ARException if object is not returned or any other exception
	 */
	public ObjectBase getInstance(String name) throws ARException
	{
		return getServerConnection().getContext().getContainer(name);
	}

	/**
	 * Get a new object instance and it will be initialized the specified name
	 *
	 * @param name object name
	 * @return object instance
	 */
	public ObjectBase newInstance(String name)
	{
		RuntimeLogger.warn("Abstract container couldn't be retrieved");
		return null;
	}

	/**
	 * Get container type in order to differentiate applications by guides or by packages
	 *
	 * @return an array of container types
	 */
	public int[] getContainerTypes()
	{
		int types[] = new int[1];
		types[0] = ContainerType.ALL.toInt();

		return types;
	}

	/**
	 * Get the object type id
	 *
	 * @return object type id
	 */
	public int getObjectTypeId()
	{
		return StructItemInfo.CONTAINER;
	}

	/**
	 * Get the wrapper name (signature code)
	 *
	 * @return wrapper signature code
	 */
	public String getPluralObjectTypeName()
	{
		return DefinitionAction.TYPE_CONTAINERS;
	}
}
