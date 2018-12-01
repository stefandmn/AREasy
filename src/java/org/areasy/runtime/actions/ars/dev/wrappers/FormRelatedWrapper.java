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

import java.util.Date;
import java.util.List;

/**
 * This is an extended class that figure out all wrappers for workflow objects that are related to a form.
 * The class expose an additional method to return object instances
 */
public interface FormRelatedWrapper extends ObjectWrapper
{
	/**
	 * Get related form name
	 *
	 * @return related form name
	 */
	String getFormName();

	/**
	 * Set related form name.
	 *
	 * @param formName form name
	 */
	void setFormName(String formName);

	/**
	 * Get an array with the key for all found objects
	 *
	 * @return object's structure arrays
	 */
	List findObjectsByForm();

	/**
	 * Get an array with the key for all found objects
	 *
	 * @param since changed since
	 * @return object's structure arrays
	 */
	List findObjectsByForm(Date since);

	/**
	* Get an object instance based on primary coordinates.
	*
	* @param form related form name
	* @param name object name
	* @return an object instances
	* @throws com.bmc.arsys.api.ARException if core exception will occur
	*/
	ObjectBase getInstance(String form, String name) throws ARException;

	/**
	* Get an array of object instances.
	*
	* @param form related form name
	* @param names array of object names
	* @return an array of object instances
	* @throws com.bmc.arsys.api.ARException if core exception will occur
	*/
	ObjectBase[] getInstance(String form, String names[]) throws ARException;

	/**
	 * Check if an object instance exists with the specified name
	 *
	 * @param form related form name
	 * @param name object name
	 * @return true if the object exists
	 */
	boolean exists(String form, String name);
}
