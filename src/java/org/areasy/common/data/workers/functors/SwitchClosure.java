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

import org.areasy.common.data.FunctorUtility;
import org.areasy.common.data.type.Closure;
import org.areasy.common.data.type.Predicate;

import java.io.Serializable;
import java.util.Iterator;
import java.util.Map;

/**
 * Closure implementation calls the closure whose predicate returns true,
 * like a switch statement.
 *
 * @version $Id: SwitchClosure.java,v 1.2 2008/05/14 09:32:32 swd\stefan.damian Exp $
 */
public class SwitchClosure implements Closure, Serializable
{
	/**
	 * The tests to consider
	 */
	private final Predicate[] iPredicates;
	/**
	 * The matching closures to call
	 */
	private final Closure[] iClosures;
	/**
	 * The default closure to call if no tests match
	 */
	private final Closure iDefault;

	/**
	 * Factory method that performs validation and copies the parameter arrays.
	 *
	 * @param predicates     array of predicates, cloned, no nulls
	 * @param closures       matching array of closures, cloned, no nulls
	 * @param defaultClosure the closure to use if no match, null means nop
	 * @return the <code>chained</code> closure
	 * @throws IllegalArgumentException if array is null
	 * @throws IllegalArgumentException if any element in the array is null
	 */
	public static Closure getInstance(Predicate[] predicates, Closure[] closures, Closure defaultClosure)
	{
		FunctorUtility.validate(predicates);
		FunctorUtility.validate(closures);
		if (predicates.length != closures.length)
		{
			throw new IllegalArgumentException("The predicate and closure arrays must be the same size");
		}
		if (predicates.length == 0)
		{
			return (defaultClosure == null ? NOPClosure.INSTANCE : defaultClosure);
		}
		predicates = FunctorUtility.copy(predicates);
		closures = FunctorUtility.copy(closures);
		return new SwitchClosure(predicates, closures, defaultClosure);
	}

	/**
	 * Create a new Closure that calls one of the closures depending
	 * on the predicates.
	 * <p/>
	 * The Map consists of Predicate keys and Closure values. A closure
	 * is called if its matching predicate returns true. Each predicate is evaluated
	 * until one returns true. If no predicates evaluate to true, the default
	 * closure is called. The default closure is set in the map with a
	 * null key. The ordering is that of the iterator() method on the entryset
	 * collection of the map.
	 *
	 * @param predicatesAndClosures a map of predicates to closures
	 * @return the <code>switch</code> closure
	 * @throws IllegalArgumentException if the map is null
	 * @throws IllegalArgumentException if any closure in the map is null
	 * @throws ClassCastException       if the map elements are of the wrong type
	 */
	public static Closure getInstance(Map predicatesAndClosures)
	{
		Closure[] closures = null;
		Predicate[] preds = null;
		if (predicatesAndClosures == null)
		{
			throw new IllegalArgumentException("The predicate and closure map must not be null");
		}
		if (predicatesAndClosures.size() == 0)
		{
			return NOPClosure.INSTANCE;
		}
		// convert to array like this to guarantee iterator() ordering
		Closure defaultClosure = (Closure) predicatesAndClosures.remove(null);
		int size = predicatesAndClosures.size();
		if (size == 0)
		{
			return (defaultClosure == null ? NOPClosure.INSTANCE : defaultClosure);
		}
		closures = new Closure[size];
		preds = new Predicate[size];
		int i = 0;
		for (Iterator it = predicatesAndClosures.entrySet().iterator(); it.hasNext();)
		{
			Map.Entry entry = (Map.Entry) it.next();
			preds[i] = (Predicate) entry.getKey();
			closures[i] = (Closure) entry.getValue();
			i++;
		}
		return new SwitchClosure(preds, closures, defaultClosure);
	}

	/**
	 * Constructor that performs no validation.
	 * Use <code>getInstance</code> if you want that.
	 *
	 * @param predicates     array of predicates, not cloned, no nulls
	 * @param closures       matching array of closures, not cloned, no nulls
	 * @param defaultClosure the closure to use if no match, null means nop
	 */
	public SwitchClosure(Predicate[] predicates, Closure[] closures, Closure defaultClosure)
	{
		super();
		iPredicates = predicates;
		iClosures = closures;
		iDefault = (defaultClosure == null ? NOPClosure.INSTANCE : defaultClosure);
	}

	/**
	 * Executes the closure whose matching predicate returns true
	 *
	 * @param input the input object
	 */
	public void execute(Object input)
	{
		for (int i = 0; i < iPredicates.length; i++)
		{
			if (iPredicates[i].evaluate(input) == true)
			{
				iClosures[i].execute(input);
				return;
			}
		}
		iDefault.execute(input);
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
	 * Gets the closures, do not modify the array.
	 *
	 * @return the closures
	 */
	public Closure[] getClosures()
	{
		return iClosures;
	}

	/**
	 * Gets the default closure.
	 *
	 * @return the default closure
	 */
	public Closure getDefaultClosure()
	{
		return iDefault;
	}

}
