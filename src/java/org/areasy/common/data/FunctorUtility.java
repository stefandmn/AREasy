package org.areasy.common.data;

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

import org.areasy.common.data.type.Closure;
import org.areasy.common.data.type.Predicate;
import org.areasy.common.data.type.Transformer;

import java.util.Collection;
import java.util.Iterator;

/**
 * Internal utilities for functors.
 *
 * @version $Id: FunctorUtility.java,v 1.2 2008/05/14 09:32:29 swd\stefan.damian Exp $
 */
public class FunctorUtility
{

	/**
	 * Restricted constructor.
	 */
	private FunctorUtility()
	{
		super();
	}

	/**
	 * Clone the predicates to ensure that the internal reference can't be messed with.
	 *
	 * @param predicates the predicates to copy
	 * @return the cloned predicates
	 */
	public static Predicate[] copy(Predicate[] predicates)
	{
		if (predicates == null)
		{
			return null;
		}
		return (Predicate[]) predicates.clone();
	}

	/**
	 * Validate the predicates to ensure that all is well.
	 *
	 * @param predicates the predicates to validate
	 */
	public static void validate(Predicate[] predicates)
	{
		if (predicates == null)
		{
			throw new IllegalArgumentException("The predicate array must not be null");
		}
		for (int i = 0; i < predicates.length; i++)
		{
			if (predicates[i] == null)
			{
				throw new IllegalArgumentException("The predicate array must not contain a null predicate, index " + i + " was null");
			}
		}
	}

	/**
	 * Validate the predicates to ensure that all is well.
	 *
	 * @param predicates the predicates to validate
	 */
	public static void validateMin2(Predicate[] predicates)
	{
		if (predicates == null)
		{
			throw new IllegalArgumentException("The predicate array must not be null");
		}
		if (predicates.length < 2)
		{
			throw new IllegalArgumentException("At least 2 predicates must be specified in the predicate array, size was " + predicates.length);
		}
		for (int i = 0; i < predicates.length; i++)
		{
			if (predicates[i] == null)
			{
				throw new IllegalArgumentException("The predicate array must not contain a null predicate, index " + i + " was null");
			}
		}
	}

	/**
	 * Validate the predicates to ensure that all is well.
	 *
	 * @param predicates the predicates to validate
	 * @return predicate array
	 */
	public static Predicate[] validate(Collection predicates)
	{
		if (predicates == null)
		{
			throw new IllegalArgumentException("The predicate collection must not be null");
		}
		if (predicates.size() < 2)
		{
			throw new IllegalArgumentException("At least 2 predicates must be specified in the predicate collection, size was " + predicates.size());
		}
		// convert to array like this to guarantee iterator() ordering
		Predicate[] preds = new Predicate[predicates.size()];
		int i = 0;
		for (Iterator it = predicates.iterator(); it.hasNext();)
		{
			preds[i] = (Predicate) it.next();
			if (preds[i] == null)
			{
				throw new IllegalArgumentException("The predicate collection must not contain a null predicate, index " + i + " was null");
			}
			i++;
		}
		return preds;
	}

	/**
	 * Clone the closures to ensure that the internal reference can't be messed with.
	 *
	 * @param closures the closures to copy
	 * @return the cloned closures
	 */
	public static Closure[] copy(Closure[] closures)
	{
		if (closures == null)
		{
			return null;
		}
		return (Closure[]) closures.clone();
	}

	/**
	 * Validate the closures to ensure that all is well.
	 *
	 * @param closures the closures to validate
	 */
	public static void validate(Closure[] closures)
	{
		if (closures == null)
		{
			throw new IllegalArgumentException("The closure array must not be null");
		}
		for (int i = 0; i < closures.length; i++)
		{
			if (closures[i] == null)
			{
				throw new IllegalArgumentException("The closure array must not contain a null closure, index " + i + " was null");
			}
		}
	}

	/**
	 * Copy method
	 *
	 * @param transformers the transformers to copy
	 * @return a clone of the transformers
	 */
	public static Transformer[] copy(Transformer[] transformers)
	{
		if (transformers == null)
		{
			return null;
		}
		return (Transformer[]) transformers.clone();
	}

	/**
	 * Validate method
	 *
	 * @param transformers the transformers to validate
	 */
	public static void validate(Transformer[] transformers)
	{
		if (transformers == null)
		{
			throw new IllegalArgumentException("The transformer array must not be null");
		}
		for (int i = 0; i < transformers.length; i++)
		{
			if (transformers[i] == null)
			{
				throw new IllegalArgumentException("The transformer array must not contain a null transformer, index " + i + " was null");
			}
		}
	}

}
