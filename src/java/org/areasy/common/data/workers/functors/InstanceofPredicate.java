package org.areasy.common.data.workers.functors;

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

import org.areasy.common.data.type.Predicate;

import java.io.Serializable;

/**
 * Predicate implementation that returns true if the input is an instanceof
 * the type stored in this predicate.
 *
 * @version $Id: InstanceofPredicate.java,v 1.2 2008/05/14 09:32:32 swd\stefan.damian Exp $
 */
public final class InstanceofPredicate implements Predicate, Serializable
{
	/**
	 * The type to compare to
	 */
	private final Class iType;

	/**
	 * Factory to create the identity predicate.
	 *
	 * @param type the type to check for, may not be null
	 * @return the predicate
	 * @throws IllegalArgumentException if the class is null
	 */
	public static Predicate getInstance(Class type)
	{
		if (type == null)
		{
			throw new IllegalArgumentException("The type to check instanceof must not be null");
		}
		return new InstanceofPredicate(type);
	}

	/**
	 * Constructor that performs no validation.
	 * Use <code>getInstance</code> if you want that.
	 *
	 * @param type the type to check for
	 */
	public InstanceofPredicate(Class type)
	{
		super();
		iType = type;
	}

	/**
	 * Evaluates the predicate returning true if the input object is of the correct type.
	 *
	 * @param object the input object
	 * @return true if input is of stored type
	 */
	public boolean evaluate(Object object)
	{
		return (iType.isInstance(object));
	}

	/**
	 * Gets the type to compare to.
	 *
	 * @return the type
	 */
	public Class getType()
	{
		return iType;
	}

}
