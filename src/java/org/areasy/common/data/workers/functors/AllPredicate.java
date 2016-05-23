package org.areasy.common.data.workers.functors;

/*
 * Copyright (c) 2007-2016 AREasy Runtime
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

import org.areasy.common.data.FunctorUtility;
import org.areasy.common.data.type.Predicate;

import java.io.Serializable;
import java.util.Collection;

/**
 * Predicate implementation that returns true if all the predicates return true.
 *
 * @version $Id: AllPredicate.java,v 1.2 2008/05/14 09:32:31 swd\stefan.damian Exp $
 */
public final class AllPredicate implements Predicate, PredicateDecorator, Serializable
{
	/**
	 * The array of predicates to call
	 */
	private final Predicate[] iPredicates;

	/**
	 * Factory to create the predicate.
	 *
	 * @param predicates the predicates to check, cloned, not null
	 * @return the <code>all</code> predicate
	 * @throws IllegalArgumentException if the predicates array is null
	 * @throws IllegalArgumentException if the predicates array has less than 2 elements
	 * @throws IllegalArgumentException if any predicate in the array is null
	 */
	public static Predicate getInstance(Predicate[] predicates)
	{
		FunctorUtility.validateMin2(predicates);
		predicates = FunctorUtility.copy(predicates);
		return new AllPredicate(predicates);
	}

	/**
	 * Factory to create the predicate.
	 *
	 * @param predicates the predicates to check, cloned, not null
	 * @return the <code>all</code> predicate
	 * @throws IllegalArgumentException if the predicates array is null
	 * @throws IllegalArgumentException if any predicate in the array is null
	 * @throws IllegalArgumentException if the predicates array has less than 2 elements
	 */
	public static Predicate getInstance(Collection predicates)
	{
		Predicate[] preds = FunctorUtility.validate(predicates);
		return new AllPredicate(preds);
	}

	/**
	 * Constructor that performs no validation.
	 * Use <code>getInstance</code> if you want that.
	 *
	 * @param predicates the predicates to check, not cloned, not null
	 */
	public AllPredicate(Predicate[] predicates)
	{
		super();
		iPredicates = predicates;
	}

	/**
	 * Evaluates the predicate returning true if all predicates return true.
	 *
	 * @param object the input object
	 * @return true if all decorated predicates return true
	 */
	public boolean evaluate(Object object)
	{
		for (int i = 0; i < iPredicates.length; i++)
		{
			if (iPredicates[i].evaluate(object) == false)
			{
				return false;
			}
		}
		return true;
	}

	/**
	 * Gets the predicates, do not modify the array.
	 *
	 * @return the predicates
	 */
	public Predicate[] getPredicates()
	{
		return iPredicates;
	}

}
