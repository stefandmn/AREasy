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
 * Predicate implementation that returns true if the input is the same object
 * as the one stored in this predicate by equals.
 *
 * @version $Id: EqualPredicate.java,v 1.2 2008/05/14 09:32:32 swd\stefan.damian Exp $
 */
public final class EqualPredicate implements Predicate, Serializable
{
	/**
	 * The value to compare to
	 */
	private final Object iValue;

	/**
	 * Factory to create the identity predicate.
	 *
	 * @param object the object to compare to
	 * @return the predicate
	 * @throws IllegalArgumentException if the predicate is null
	 */
	public static Predicate getInstance(Object object)
	{
		if (object == null)
		{
			return NullPredicate.INSTANCE;
		}
		return new EqualPredicate(object);
	}

	/**
	 * Constructor that performs no validation.
	 * Use <code>getInstance</code> if you want that.
	 *
	 * @param object the object to compare to
	 */
	public EqualPredicate(Object object)
	{
		super();
		iValue = object;
	}

	/**
	 * Evaluates the predicate returning true if the input equals the stored value.
	 *
	 * @param object the input object
	 * @return true if input object equals stored value
	 */
	public boolean evaluate(Object object)
	{
		return (iValue.equals(object));
	}

	/**
	 * Gets the value.
	 *
	 * @return the value
	 */
	public Object getValue()
	{
		return iValue;
	}

}
