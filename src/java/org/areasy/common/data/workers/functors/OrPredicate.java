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

/**
 * Predicate implementation that returns true if either of the predicates return true.
 *
 * @version $Id: OrPredicate.java,v 1.2 2008/05/14 09:32:32 swd\stefan.damian Exp $
 */
public final class OrPredicate implements Predicate, PredicateDecorator, Serializable
{
	/**
	 * The array of predicates to call
	 */
	private final Predicate iPredicate1;
	/**
	 * The array of predicates to call
	 */
	private final Predicate iPredicate2;

	/**
	 * Factory to create the predicate.
	 *
	 * @param predicate1 the first predicate to check, not null
	 * @param predicate2 the second predicate to check, not null
	 * @return the <code>and</code> predicate
	 * @throws IllegalArgumentException if either predicate is null
	 */
	public static Predicate getInstance(Predicate predicate1, Predicate predicate2)
	{
		if (predicate1 == null || predicate2 == null)
		{
			throw new IllegalArgumentException("Predicate must not be null");
		}
		return new OrPredicate(predicate1, predicate2);
	}

	/**
	 * Constructor that performs no validation.
	 * Use <code>getInstance</code> if you want that.
	 *
	 * @param predicate1 the first predicate to check, not null
	 * @param predicate2 the second predicate to check, not null
	 */
	public OrPredicate(Predicate predicate1, Predicate predicate2)
	{
		super();
		iPredicate1 = predicate1;
		iPredicate2 = predicate2;
	}

	/**
	 * Evaluates the predicate returning true if either predicate returns true.
	 *
	 * @param object the input object
	 * @return true if either decorated predicate returns true
	 */
	public boolean evaluate(Object object)
	{
		return (iPredicate1.evaluate(object) || iPredicate2.evaluate(object));
	}

	/**
	 * Gets the two predicates being decorated as an array.
	 *
	 * @return the predicates
	 */
	public Predicate[] getPredicates()
	{
		return new Predicate[]{iPredicate1, iPredicate2};
	}

}
