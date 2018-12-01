package org.areasy.common.data.type.collection;

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

import java.util.Collection;

/**
 * Decorates a <code>Collection</code> to validate that elements added are of a specific type.
 * <p/>
 * The validation of additions is performed via an instanceof test against
 * a specified <code>Class</code>. If an object cannot be added to the
 * collection, an IllegalArgumentException is thrown.
 *
 * @version $Id: TypedCollection.java,v 1.2 2008/05/14 09:32:34 swd\stefan.damian Exp $
 */
public class TypedCollection
{

	/**
	 * Factory method to create a typed collection.
	 * <p/>
	 * If there are any elements already in the collection being decorated, they
	 * are validated.
	 *
	 * @param coll the collection to decorate, must not be null
	 * @param type the type to allow into the collection, must not be null
	 * @return a new typed collection
	 * @throws IllegalArgumentException if collection or type is null
	 * @throws IllegalArgumentException if the collection contains invalid elements
	 */
	public static Collection decorate(Collection coll, Class type)
	{
		return new PredicatedCollection(coll, InstanceofPredicate.getInstance(type));
	}

	/**
	 * Restrictive constructor.
	 */
	protected TypedCollection()
	{
		super();
	}

}
