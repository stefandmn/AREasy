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
import org.areasy.common.data.type.Transformer;

import java.io.Serializable;
import java.util.Iterator;
import java.util.Map;

/**
 * Transformer implementation calls the transformer whose predicate returns true,
 * like a switch statement.
 *
 * @version $Id: SwitchTransformer.java,v 1.2 2008/05/14 09:32:31 swd\stefan.damian Exp $
 */
public class SwitchTransformer implements Transformer, Serializable
{
	/**
	 * The tests to consider
	 */
	private final Predicate[] iPredicates;
	/**
	 * The matching transformers to call
	 */
	private final Transformer[] iTransformers;
	/**
	 * The default transformer to call if no tests match
	 */
	private final Transformer iDefault;

	/**
	 * Factory method that performs validation and copies the parameter arrays.
	 *
	 * @param predicates         array of predicates, cloned, no nulls
	 * @param transformers       matching array of transformers, cloned, no nulls
	 * @param defaultTransformer the transformer to use if no match, null means nop
	 * @return the <code>chained</code> transformer
	 * @throws IllegalArgumentException if array is null
	 * @throws IllegalArgumentException if any element in the array is null
	 */
	public static Transformer getInstance(Predicate[] predicates, Transformer[] transformers, Transformer defaultTransformer)
	{
		FunctorUtility.validate(predicates);
		FunctorUtility.validate(transformers);
		if (predicates.length != transformers.length)
		{
			throw new IllegalArgumentException("The predicate and transformer arrays must be the same size");
		}
		if (predicates.length == 0)
		{
			return (defaultTransformer == null ? ConstantTransformer.NULL_INSTANCE : defaultTransformer);
		}
		predicates = FunctorUtility.copy(predicates);
		transformers = FunctorUtility.copy(transformers);
		return new SwitchTransformer(predicates, transformers, defaultTransformer);
	}

	/**
	 * Create a new Transformer that calls one of the transformers depending
	 * on the predicates.
	 * <p/>
	 * The Map consists of Predicate keys and Transformer values. A transformer
	 * is called if its matching predicate returns true. Each predicate is evaluated
	 * until one returns true. If no predicates evaluate to true, the default
	 * transformer is called. The default transformer is set in the map with a
	 * null key. The ordering is that of the iterator() method on the entryset
	 * collection of the map.
	 *
	 * @param predicatesAndTransformers a map of predicates to transformers
	 * @return the <code>switch</code> transformer
	 * @throws IllegalArgumentException if the map is null
	 * @throws IllegalArgumentException if any transformer in the map is null
	 * @throws ClassCastException       if the map elements are of the wrong type
	 */
	public static Transformer getInstance(Map predicatesAndTransformers)
	{
		Transformer[] transformers = null;
		Predicate[] preds = null;
		if (predicatesAndTransformers == null)
		{
			throw new IllegalArgumentException("The predicate and transformer map must not be null");
		}
		if (predicatesAndTransformers.size() == 0)
		{
			return ConstantTransformer.NULL_INSTANCE;
		}
		// convert to array like this to guarantee iterator() ordering
		Transformer defaultTransformer = (Transformer) predicatesAndTransformers.remove(null);
		int size = predicatesAndTransformers.size();
		if (size == 0)
		{
			return (defaultTransformer == null ? ConstantTransformer.NULL_INSTANCE : defaultTransformer);
		}
		transformers = new Transformer[size];
		preds = new Predicate[size];
		int i = 0;
		for (Iterator it = predicatesAndTransformers.entrySet().iterator(); it.hasNext();)
		{
			Map.Entry entry = (Map.Entry) it.next();
			preds[i] = (Predicate) entry.getKey();
			transformers[i] = (Transformer) entry.getValue();
			i++;
		}
		return new SwitchTransformer(preds, transformers, defaultTransformer);
	}

	/**
	 * Constructor that performs no validation.
	 * Use <code>getInstance</code> if you want that.
	 *
	 * @param predicates         array of predicates, not cloned, no nulls
	 * @param transformers       matching array of transformers, not cloned, no nulls
	 * @param defaultTransformer the transformer to use if no match, null means nop
	 */
	public SwitchTransformer(Predicate[] predicates, Transformer[] transformers, Transformer defaultTransformer)
	{
		super();
		iPredicates = predicates;
		iTransformers = transformers;
		iDefault = (defaultTransformer == null ? ConstantTransformer.NULL_INSTANCE : defaultTransformer);
	}

	/**
	 * Transforms the input to result by calling the transformer whose matching
	 * predicate returns true.
	 *
	 * @param input the input object to transform
	 * @return the transformed result
	 */
	public Object transform(Object input)
	{
		for (int i = 0; i < iPredicates.length; i++)
		{
			if (iPredicates[i].evaluate(input) == true)
			{
				return iTransformers[i].transform(input);
			}
		}
		return iDefault.transform(input);
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

	/**
	 * Gets the transformers, do not modify the array.
	 *
	 * @return the transformers
	 */
	public Transformer[] getTransformers()
	{
		return iTransformers;
	}

	/**
	 * Gets the default transformer.
	 *
	 * @return the default transformer
	 */
	public Transformer getDefaultTransformer()
	{
		return iDefault;
	}

}
