package org.areasy.common.data.type.map;

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

import org.areasy.common.data.workers.functors.InstanceofPredicate;

import java.util.SortedMap;

/**
 * Decorates another <code>SortedMap</code> to validate that elements added
 * are of a specific type.
 * <p/>
 * The validation of additions is performed via an instanceof test against
 * a specified <code>Class</code>. If an object cannot be added to the
 * collection, an IllegalArgumentException is thrown.
 *
 * @version $Id: TypedSortedMap.java,v 1.2 2008/05/14 09:32:30 swd\stefan.damian Exp $
 */
public class TypedSortedMap
{
	/**
	 * Factory method to create a typed sorted map.
	 * <p/>
	 * If there are any elements already in the map being decorated, they
	 * are validated.
	 *
	 * @param map       the map to decorate, must not be null
	 * @param keyType   the type to allow as keys, must not be null
	 * @param valueType the type to allow as values, must not be null
	 * @throws IllegalArgumentException if list or type is null
	 * @throws IllegalArgumentException if the list contains invalid elements
	 */
	public static SortedMap decorate(SortedMap map, Class keyType, Class valueType)
	{
		return new PredicatedSortedMap(map, InstanceofPredicate.getInstance(keyType), InstanceofPredicate.getInstance(valueType));
	}

	/**
	 * Restrictive constructor.
	 */
	protected TypedSortedMap()
	{
		//nothing to do here..
	}

}
