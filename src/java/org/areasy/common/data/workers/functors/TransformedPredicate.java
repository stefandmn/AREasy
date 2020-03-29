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
import org.areasy.common.data.type.Transformer;

import java.io.Serializable;

/**
 * Predicate implementation that transforms the given object before invoking
 * another <code>Predicate</code>.
 *
 * @version $Id: TransformedPredicate.java,v 1.2 2008/05/14 09:32:32 swd\stefan.damian Exp $
 */
public final class TransformedPredicate implements Predicate, PredicateDecorator, Serializable
{
	/**
	 * The transformer to call
	 */
	private final Transformer iTransformer;
	/**
	 * The predicate to call
	 */
	private final Predicate iPredicate;

	/**
	 * Factory to create the predicate.
	 *
	 * @param transformer the transformer to call
	 * @param predicate   the predicate to call with the result of the transform
	 * @return the predicate
	 * @throws IllegalArgumentException if the transformer or the predicate is null
	 */
	public static Predicate getInstance(Transformer transformer, Predicate predicate)
	{
		if (transformer == null)
		{
			throw new IllegalArgumentException("The transformer to call must not be null");
		}
		if (predicate == null)
		{
			throw new IllegalArgumentException("The predicate to call must not be null");
		}
		return new TransformedPredicate(transformer, predicate);
	}

	/**
	 * Constructor that performs no validation.
	 * Use <code>getInstance</code> if you want that.
	 *
	 * @param transformer the transformer to use
	 * @param predicate   the predicate to decorate
	 */
	public TransformedPredicate(Transformer transformer, Predicate predicate)
	{
		iTransformer = transformer;
		iPredicate = predicate;
	}

	/**
	 * Evaluates the predicate returning the result of the decorated predicate
	 * once the input has been transformed
	 *
	 * @param object the input object which will be transformed
	 * @return true if decorated predicate returns true
	 */
	public boolean evaluate(Object object)
	{
		Object result = iTransformer.transform(object);
		return iPredicate.evaluate(result);
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

	/**
	 * Gets the transformer in use.
	 *
	 * @return the transformer
	 */
	public Transformer getTransformer()
	{
		return iTransformer;
	}

}
