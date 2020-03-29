package org.areasy.common.data.type.list;

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

import org.areasy.common.data.workers.functors.InstanceofPredicate;

import java.util.List;

/**
 * Decorates another <code>List</code> to validate that elements
 * added are of a specific type.
 * <p/>
 * The validation of additions is performed via an instanceof test against
 * a specified <code>Class</code>. If an object cannot be added to the
 * collection, an IllegalArgumentException is thrown.
 *
 * @version $Id: TypedList.java,v 1.2 2008/05/14 09:32:35 swd\stefan.damian Exp $
 */
public class TypedList
{

	/**
	 * Factory method to create a typed list.
	 * <p/>
	 * If there are any elements already in the list being decorated, they
	 * are validated.
	 *
	 * @param list the list to decorate, must not be null
	 * @param type the type to allow into the collection, must not be null
	 * @throws IllegalArgumentException if list or type is null
	 * @throws IllegalArgumentException if the list contains invalid elements
	 */
	public static List decorate(List list, Class type)
	{
		return new PredicatedList(list, InstanceofPredicate.getInstance(type));
	}

	/**
	 * Restrictive constructor.
	 */
	protected TypedList()
	{
	}

}
