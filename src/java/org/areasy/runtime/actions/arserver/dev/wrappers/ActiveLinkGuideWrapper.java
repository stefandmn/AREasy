package org.areasy.runtime.actions.arserver.dev.wrappers;

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

import com.bmc.arsys.api.ActiveLinkGuide;
import com.bmc.arsys.api.ContainerType;
import com.bmc.arsys.api.ObjectBase;
import org.areasy.runtime.actions.arserver.dev.DefinitionAction;

/**
 * Active Link Guide definition object implementation.
 */
public class ActiveLinkGuideWrapper extends ContainerWrapper implements ObjectWrapper
{
	/**
	 * Dedicated method to instantiate a definition object
	 *
	 * @param action runtime action which are calling this object
	 */
	public ActiveLinkGuideWrapper(DefinitionAction action)
	{
		init(action);
	}

	/**
	 * Get a new object instance and it will be initialized the specified name
	 *
	 * @param name object name
	 * @return object instance
	 */
	public ObjectBase newInstance(String name)
	{
		ObjectBase object = new ActiveLinkGuide();
		object.setName(name);

		return object;
	}

	/**
	 * Get container type in order to differentiate applications by guides or by packages
	 *
	 * @return an array of container types
	 */
	public int[] getContainerTypes()
	{
		int types[] = new int[1];
		types[0] = ContainerType.GUIDE.toInt();

		return types;
	}

	/**
	 * Get the object type id
	 *
	 * @return object type id
	 */
	public int getObjectTypeId()
	{
		return DefinitionAction.ACTIVELINKGUIDE;
	}

	/**
	 * Get the wrapper name (signature code)
	 *
	 * @return wrapper signature code
	 */
	public String getPluralObjectTypeName()
	{
		return DefinitionAction.TYPE_ACTIVELINKGUIDES;
	}
}
