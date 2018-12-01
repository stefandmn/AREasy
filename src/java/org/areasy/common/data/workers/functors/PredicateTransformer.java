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
import org.areasy.common.data.type.Transformer;

import java.io.Serializable;

/**
 * Transformer implementation that calls a Predicate using the input object
 * and then returns the input.
 *
 * @version $Id: PredicateTransformer.java,v 1.2 2008/05/14 09:32:32 swd\stefan.damian Exp $
 */
public class PredicateTransformer implements Transformer, Serializable
{
	/**
	 * The closure to wrap
	 */
	private final Predicate iPredicate;

	/**
	 * Factory method that performs validation.
	 *
	 * @param predicate the predicate to call, not null
	 * @return the <code>predicate</code> transformer
	 * @throws IllegalArgumentException if the predicate is null
	 */
	public static Transformer getInstance(Predicate predicate)
	{
		if (predicate == null) throw new IllegalArgumentException("Predicate must not be null");

		return new PredicateTransformer(predicate);
	}

	/**
	 * Constructor that performs no validation.
	 * Use <code>getInstance</code> if you want that.
	 *
	 * @param predicate the predicate to call, not null
	 */
	public PredicateTransformer(Predicate predicate)
	{
		super();
		iPredicate = predicate;
	}

	/**
	 * Transforms the input to result by calling a predicate.
	 *
	 * @param input the input object to transform
	 * @return the transformed result
	 */
	public Object transform(Object input)
	{
		return (iPredicate.evaluate(input) ? Boolean.TRUE : Boolean.FALSE);
	}

	/**
	 * Gets the predicate.
	 *
	 * @return the predicate
	 */
	public Predicate getPredicate()
	{
		return iPredicate;
	}

}
