package org.areasy.common.data.type.set;

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

import org.areasy.common.data.workers.functors.InstanceofPredicate;

import java.util.Set;

/**
 * Decorates another <code>Set</code> to validate that elements
 * added are of a specific type.
 * <p/>
 * The validation of additions is performed via an instanceof test against
 * a specified <code>Class</code>. If an object cannot be added to the
 * collection, an IllegalArgumentException is thrown.
 *
 * @version $Id: TypedSet.java,v 1.3 2008/05/20 06:52:55 swd\stefan.damian Exp $
 */
public class TypedSet
{
	/**
	 * Factory method to create a typed set.
	 * <p/>
	 * If there are any elements already in the set being decorated, they
	 * are validated.
	 *
	 * @param set  the set to decorate, must not be null
	 * @param type the type to allow into the collection, must not be null
	 * @throws IllegalArgumentException if set or type is null
	 * @throws IllegalArgumentException if the set contains invalid elements
	 */
	public static Set decorate(Set set, Class type)
	{
		return new PredicatedSet(set, InstanceofPredicate.getInstance(type));
	}

	/**
	 * Restrictive constructor.
	 */
	protected TypedSet()
	{
		//nothing to do here
	}
}
