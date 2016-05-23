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

import org.areasy.common.data.type.Predicate;
import org.areasy.common.data.type.Transformer;

import java.io.Serializable;

/**
 * Predicate implementation that returns the result of a transformer.
 *
 * @version $Id: TransformerPredicate.java,v 1.2 2008/05/14 09:32:32 swd\stefan.damian Exp $
 */
public final class TransformerPredicate implements Predicate, Serializable
{
	/**
	 * The transformer to call
	 */
	private final Transformer iTransformer;

	/**
	 * Factory to create the predicate.
	 *
	 * @param transformer the transformer to decorate
	 * @return the predicate
	 * @throws IllegalArgumentException if the transformer is null
	 */
	public static Predicate getInstance(Transformer transformer)
	{
		if (transformer == null) throw new IllegalArgumentException("The transformer to call must not be null");

		return new TransformerPredicate(transformer);
	}

	/**
	 * Constructor that performs no validation.
	 * Use <code>getInstance</code> if you want that.
	 *
	 * @param transformer the transformer to decorate
	 */
	public TransformerPredicate(Transformer transformer)
	{
		super();
		iTransformer = transformer;
	}

	/**
	 * Evaluates the predicate returning the result of the decorated transformer.
	 *
	 * @param object the input object
	 * @return true if decorated transformer returns Boolean.TRUE
	 * @throws org.areasy.common.data.workers.functors.FunctorException
	 *          if the transformer returns an invalid type
	 */
	public boolean evaluate(Object object)
	{
		Object result = iTransformer.transform(object);
		if (result instanceof Boolean == false)
		{
			throw new FunctorException("Transformer must return an instanceof Boolean, it was a "
					+ (result == null ? "null object" : result.getClass().getName()));
		}
		return ((Boolean) result).booleanValue();
	}

	/**
	 * Gets the transformer.
	 *
	 * @return the transformer
	 */
	public Transformer getTransformer()
	{
		return iTransformer;
	}

}
