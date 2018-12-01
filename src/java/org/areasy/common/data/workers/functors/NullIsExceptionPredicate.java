package org.areasy.common.data.workers.functors;

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

import org.areasy.common.data.type.Predicate;

import java.io.Serializable;

/**
 * Predicate implementation that throws an exception if the input is null.
 *
 * @version $Id: NullIsExceptionPredicate.java,v 1.2 2008/05/14 09:32:32 swd\stefan.damian Exp $
 */
public final class NullIsExceptionPredicate implements Predicate, PredicateDecorator, Serializable
{
	/**
	 * The predicate to decorate
	 */
	private final Predicate iPredicate;

	/**
	 * Factory to create the null exception predicate.
	 *
	 * @param predicate the predicate to decorate, not null
	 * @return the predicate
	 * @throws IllegalArgumentException if the predicate is null
	 */
	public static Predicate getInstance(Predicate predicate)
	{
		if (predicate == null)
		{
			throw new IllegalArgumentException("Predicate must not be null");
		}
		return new NullIsExceptionPredicate(predicate);
	}

	/**
	 * Constructor that performs no validation.
	 * Use <code>getInstance</code> if you want that.
	 *
	 * @param predicate the predicate to call after the null check
	 */
	public NullIsExceptionPredicate(Predicate predicate)
	{
		super();
		iPredicate = predicate;
	}

	/**
	 * Evaluates the predicate returning the result of the decorated predicate
	 * once a null check is performed.
	 *
	 * @param object the input object
	 * @return true if decorated predicate returns true
	 * @throws FunctorException if input is null
	 */
	public boolean evaluate(Object object)
	{
		if (object == null)
		{
			throw new FunctorException("Input Object must not be null");
		}
		return iPredicate.evaluate(object);
	}

	/**
	 * Gets the predicate being decorated.
	 *
	 * @return the predicate as the only element in an array
	 */
	public Predicate[] getPredicates()
	{
		return new Predicate[]{iPredicate};
	}

}
