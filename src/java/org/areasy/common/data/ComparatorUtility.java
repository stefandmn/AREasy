package org.areasy.common.data;

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

import org.areasy.common.data.type.Transformer;
import org.areasy.common.data.workers.comparators.*;

import java.util.Collection;
import java.util.Comparator;

/**
 * Provides convenient static utility methods for <Code>Comparator</Code>
 * objects.
 * <p/>
 * Most of the functionality in this class can also be found in the
 * <code>comparators</code> package. This class merely provides a
 * convenient central place if you have use for more than one class
 * in the <code>comparators</code> subpackage.
 *
 * @version $Id: ComparatorUtility.java,v 1.2 2008/05/14 09:32:29 swd\stefan.damian Exp $
 */
public class ComparatorUtility
{

	/**
	 * ComparatorUtility should not normally be instantiated.
	 */
	public ComparatorUtility()
	{
		//nothing to do here.
	}

	/**
	 * Comparator for natural sort order.
	 *
	 * @see ComparableComparator#getInstance
	 */
	public static final Comparator NATURAL_COMPARATOR = ComparableComparator.getInstance();

	/**
	 * Gets a comparator that uses the natural order of the objects.
	 *
	 * @return a comparator which uses natural order
	 */
	public static Comparator naturalComparator()
	{
		return NATURAL_COMPARATOR;
	}

	/**
	 * Gets a comparator that compares using two {@link Comparator}s.
	 * <p/>
	 * The second comparator is used if the first comparator returns equal.
	 *
	 * @param comparator1 the first comparator to use, not null
	 * @param comparator2 the first comparator to use, not null
	 * @return a {@link ComparatorChain} formed from the two comparators
	 * @throws NullPointerException if either comparator is null
	 * @see ComparatorChain
	 */
	public static Comparator chainedComparator(Comparator comparator1, Comparator comparator2)
	{
		return chainedComparator(new Comparator[]{comparator1, comparator2});
	}

	/**
	 * Gets a comparator that compares using an array of {@link Comparator}s, applied
	 * in sequence until one returns not equal or the array is exhausted.
	 *
	 * @param comparators the comparators to use, not null or empty or containing nulls
	 * @return a {@link ComparatorChain} formed from the input comparators
	 * @throws NullPointerException if comparators array is null or contains a null
	 * @see ComparatorChain
	 */
	public static Comparator chainedComparator(Comparator[] comparators)
	{
		ComparatorChain chain = new ComparatorChain();
		for (int i = 0; i < comparators.length; i++)
		{
			if (comparators[i] == null)
			{
				throw new NullPointerException("Comparator cannot be null");
			}
			chain.addComparator(comparators[i]);
		}
		return chain;
	}

	/**
	 * Gets a comparator that compares using a collection of {@link Comparator}s,
	 * applied in (default iterator) sequence until one returns not equal or the
	 * collection is exhausted.
	 *
	 * @param comparators the comparators to use, not null or empty or containing nulls
	 * @return a {@link ComparatorChain} formed from the input comparators
	 * @throws NullPointerException if comparators collection is null or contains a null
	 * @throws ClassCastException   if the comparators collection contains the wrong object type
	 * @see ComparatorChain
	 */
	public static Comparator chainedComparator(Collection comparators)
	{
		return chainedComparator((Comparator[]) comparators.toArray(new Comparator[comparators.size()]));
	}

	/**
	 * Gets a comparator that reverses the order of the given comparator.
	 *
	 * @param comparator the comparator to reverse
	 * @return a comparator that reverses the order of the input comparator
	 * @see ReverseComparator
	 */
	public static Comparator reversedComparator(Comparator comparator)
	{
		if (comparator == null)
		{
			comparator = NATURAL_COMPARATOR;
		}
		return new ReverseComparator(comparator);
	}

	/**
	 * Gets a Comparator that can sort Boolean objects.
	 * <p/>
	 * The parameter specifies whether true or false is sorted first.
	 * <p/>
	 * The comparator throws NullPointerException if a null value is compared.
	 *
	 * @param trueFirst when <code>true</code>, sort
	 *                  <code>true</code> {@link Boolean}s before
	 *                  <code>false</code> {@link Boolean}s.
	 * @return a comparator that sorts booleans
	 */
	public static Comparator booleanComparator(boolean trueFirst)
	{
		return BooleanComparator.getBooleanComparator(trueFirst);
	}

	/**
	 * Gets a Comparator that controls the comparison of <code>null</code> values.
	 * <p/>
	 * The returned comparator will consider a null value to be less than
	 * any nonnull value, and equal to any other null value.  Two nonnull
	 * values will be evaluated with the given comparator.
	 *
	 * @param comparator the comparator that wants to allow nulls
	 * @return a version of that comparator that allows nulls
	 * @see NullComparator
	 */
	public static Comparator nullLowComparator(Comparator comparator)
	{
		if (comparator == null)
		{
			comparator = NATURAL_COMPARATOR;
		}
		return new NullComparator(comparator, false);
	}

	/**
	 * Gets a Comparator that controls the comparison of <code>null</code> values.
	 * <p/>
	 * The returned comparator will consider a null value to be greater than
	 * any nonnull value, and equal to any other null value.  Two nonnull
	 * values will be evaluated with the given comparator.
	 *
	 * @param comparator the comparator that wants to allow nulls
	 * @return a version of that comparator that allows nulls
	 * @see NullComparator
	 */
	public static Comparator nullHighComparator(Comparator comparator)
	{
		if (comparator == null)
		{
			comparator = NATURAL_COMPARATOR;
		}
		return new NullComparator(comparator, true);
	}

	/**
	 * Gets a Comparator that passes transformed objects to the given comparator.
	 * <p/>
	 * Objects passed to the returned comparator will first be transformed
	 * by the given transformer before they are compared by the given
	 * comparator.
	 *
	 * @param comparator  the sort order to use
	 * @param transformer the transformer to use
	 * @return a comparator that transforms its input objects before comparing them
	 * @see TransformingComparator
	 */
	public static Comparator transformedComparator(Comparator comparator, Transformer transformer)
	{
		if (comparator == null)
		{
			comparator = NATURAL_COMPARATOR;
		}
		return new TransformingComparator(transformer, comparator);
	}

	/**
	 * Returns the smaller of the given objects according to the given
	 * comparator, returning the second object if the comparator
	 * returns equal.
	 *
	 * @param o1         the first object to compare
	 * @param o2         the second object to compare
	 * @param comparator the sort order to use
	 * @return the smaller of the two objects
	 */
	public static Object min(Object o1, Object o2, Comparator comparator)
	{
		if (comparator == null)
		{
			comparator = NATURAL_COMPARATOR;
		}
		int c = comparator.compare(o1, o2);
		return (c < 0) ? o1 : o2;
	}

	/**
	 * Returns the larger of the given objects according to the given
	 * comparator, returning the second object if the comparator
	 * returns equal.
	 *
	 * @param o1         the first object to compare
	 * @param o2         the second object to compare
	 * @param comparator the sort order to use
	 * @return the larger of the two objects
	 */
	public static Object max(Object o1, Object o2, Comparator comparator)
	{
		if (comparator == null)
		{
			comparator = NATURAL_COMPARATOR;
		}
		int c = comparator.compare(o1, o2);
		return (c > 0) ? o1 : o2;
	}

}
