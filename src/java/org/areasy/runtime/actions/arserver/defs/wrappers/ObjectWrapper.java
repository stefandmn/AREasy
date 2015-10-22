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

import com.bmc.arsys.api.ARException;
import com.bmc.arsys.api.ObjectBase;

import java.util.Date;
import java.util.List;

/**
 * Model of ARS definition object.
 *
 */
public interface ObjectWrapper
{
	/**
	 * Get an array with the keys for all found objects
	 *
	 * @param since changed since
	 * @return object's keys list
	 */
	List<String> find(Date since);

	/**
	 * Check if an object instance exists with the specified name
	 *
	 * @param name object name
	 * @return true if the object exists
	 */
	boolean exists(String name);

	/**
	 * Get the object name instance. In case of the instance is not defined the workflow
	 * will return an exception.
	 *
	 * @param name object name
	 * @return object instance
	 * @throws ARException if object is not returned or any other exception
	 */
	ObjectBase getInstance(String name) throws ARException;

	/**
	 * Get a new object instance and it will be initialized the specified name
	 *
	 * @param name object name
	 * @return object instance
	 */
	ObjectBase newInstance(String name);

	/**
	 * Get the object type id
	 *
	 * @return object type id
	 */
	int getObjectTypeId();

	/**
	 * Get the wrapper name (signature code)
	 *
	 * @return wrapper signature code
	 */
	String getPluralObjectTypeName();
}
