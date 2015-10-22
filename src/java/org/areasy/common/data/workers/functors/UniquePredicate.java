package org.areasy.common.data.workers.functors;

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

import org.areasy.common.data.type.Predicate;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

/**
 * Predicate implementation that returns true the first time an object is
 * passed into the predicate.
 *
 * @version $Id: UniquePredicate.java,v 1.2 2008/05/14 09:32:32 swd\stefan.damian Exp $
 */
public final class UniquePredicate implements Predicate, Serializable
{
	/**
	 * The set of previously seen objects
	 */
	private final Set iSet = new HashSet();

	/**
	 * Factory to create the predicate.
	 *
	 * @return the predicate
	 * @throws IllegalArgumentException if the predicate is null
	 */
	public static Predicate getInstance()
	{
		return new UniquePredicate();
	}

	/**
	 * Constructor that performs no validation.
	 * Use <code>getInstance</code> if you want that.
	 */
	public UniquePredicate()
	{
		super();
	}

	/**
	 * Evaluates the predicate returning true if the input object hasn't been
	 * received yet.
	 *
	 * @param object the input object
	 * @return true if this is the first time the object is seen
	 */
	public boolean evaluate(Object object)
	{
		return iSet.add(object);
	}

}
