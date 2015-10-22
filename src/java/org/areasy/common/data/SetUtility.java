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

import org.areasy.common.data.type.Predicate;
import org.areasy.common.data.type.Transformer;
import org.areasy.common.data.type.set.*;

import java.util.*;

/**
 * Provides utility methods and decorators for
 * {@link Set} and {@link SortedSet} instances.
 *
 * @version $Id: SetUtility.java,v 1.2 2008/05/14 09:32:29 swd\stefan.damian Exp $
 */
public class SetUtility
{

	/**
	 * An empty unmodifiable set.
	 * This uses the {@link Collections} implementation
	 * and is provided for completeness.
	 */
	public static final Set EMPTY_SET = Collections.EMPTY_SET;
	/**
	 * An empty unmodifiable sorted set.
	 * This is not provided in the JDK.
	 */
	public static final SortedSet EMPTY_SORTED_SET = UnmodifiableSortedSet.decorate(new TreeSet());

	/**
	 * <code>SetUtility</code> should not normally be instantiated.
	 */
	public SetUtility()
	{
		//nothing to do..
	}

	/**
	 * Tests two sets for equality as per the <code>equals()</code> contract
	 * in {@link java.util.Set#equals(java.lang.Object)}.
	 * <p/>
	 * This method is useful for implementing <code>Set</code> when you cannot
	 * extend AbstractSet. The method takes Collection instances to enable other
	 * collection types to use the Set implementation algorithm.
	 * <p/>
	 * The relevant text (slightly paraphrased as this is a static method) is:
	 * <blockquote>
	 * <p>Two sets are considered equal if they have
	 * the same size, and every member of the first set is contained in
	 * the second. This ensures that the <tt>equals</tt> method works
	 * properly across different implementations of the <tt>Set</tt>
	 * interface.</p>
	 * <p/>
	 * <p/>
	 * This implementation first checks if the two sets are the same object:
	 * if so it returns <tt>true</tt>.  Then, it checks if the two sets are
	 * identical in size; if not, it returns false. If so, it returns
	 * <tt>a.containsAll((Collection) b)</tt>.</p>
	 * </blockquote>
	 *
	 * @param set1 the first set, may be null
	 * @param set2 the second set, may be null
	 * @return whether the sets are equal by value comparison
	 * @see java.util.Set
	 */
	public static boolean isEqualSet(final Collection set1, final Collection set2)
	{
		if (set1 == set2) return true;

		if (set1 == null || set2 == null || set1.size() != set2.size()) return false;

		return set1.containsAll(set2);
	}

	/**
	 * Generates a hash code using the algorithm specified in
	 * {@link java.util.Set#hashCode()}.
	 * <p/>
	 * This method is useful for implementing <code>Set</code> when you cannot
	 * extend AbstractSet. The method takes Collection instances to enable other
	 * collection types to use the Set implementation algorithm.
	 *
	 * @param set the set to calculate the hash code for, may be null
	 * @return the hash code
	 * @see java.util.Set#hashCode()
	 */
	public static int hashCodeForSet(final Collection set)
	{
		if (set == null) return 0;

		int hashCode = 0;
		Iterator it = set.iterator();
		Object obj = null;

		while (it.hasNext())
		{
			obj = it.next();
			if (obj != null) hashCode += obj.hashCode();
		}
		
		return hashCode;
	}

	/**
	 * Returns a synchronized set backed by the given set.
	 * <p/>
	 * You must manually synchronize on the returned buffer's iterator to
	 * avoid non-deterministic behavior:
	 * <p/>
	 * <pre>
	 * Set s = SetUtility.synchronizedSet(mySet);
	 * synchronized (s) {
	 *     Iterator i = s.iterator();
	 *     while (i.hasNext()) {
	 *         process (i.next());
	 *     }
	 * }
	 * </pre>
	 * <p/>
	 * This method uses the implementation in the decorators subpackage.
	 *
	 * @param set the set to synchronize, must not be null
	 * @return a synchronized set backed by the given set
	 * @throws IllegalArgumentException if the set is null
	 */
	public static Set synchronizedSet(Set set)
	{
		return SynchronizedSet.decorate(set);
	}

	/**
	 * Returns an unmodifiable set backed by the given set.
	 * <p/>
	 * This method uses the implementation in the decorators subpackage.
	 *
	 * @param set the set to make unmodifiable, must not be null
	 * @return an unmodifiable set backed by the given set
	 * @throws IllegalArgumentException if the set is null
	 */
	public static Set unmodifiableSet(Set set)
	{
		return UnmodifiableSet.decorate(set);
	}

	/**
	 * Returns a predicated (validating) set backed by the given set.
	 * <p/>
	 * Only objects that pass the test in the given predicate can be added to the set.
	 * Trying to add an invalid object results in an IllegalArgumentException.
	 * It is important not to use the original set after invoking this method,
	 * as it is a backdoor for adding invalid objects.
	 *
	 * @param set       the set to predicate, must not be null
	 * @param predicate the predicate for the set, must not be null
	 * @return a predicated set backed by the given set
	 * @throws IllegalArgumentException if the Set or Predicate is null
	 */
	public static Set predicatedSet(Set set, Predicate predicate)
	{
		return PredicatedSet.decorate(set, predicate);
	}

	/**
	 * Returns a typed set backed by the given set.
	 * <p/>
	 * Only objects of the specified type can be added to the set.
	 *
	 * @param set  the set to limit to a specific type, must not be null
	 * @param type the type of objects which may be added to the set
	 * @return a typed set backed by the specified set
	 */
	public static Set typedSet(Set set, Class type)
	{
		return TypedSet.decorate(set, type);
	}

	/**
	 * Returns a transformed set backed by the given set.
	 * <p/>
	 * Each object is passed through the transformer as it is added to the
	 * Set. It is important not to use the original set after invoking this
	 * method, as it is a backdoor for adding untransformed objects.
	 *
	 * @param set         the set to transform, must not be null
	 * @param transformer the transformer for the set, must not be null
	 * @return a transformed set backed by the given set
	 * @throws IllegalArgumentException if the Set or Transformer is null
	 */
	public static Set transformedSet(Set set, Transformer transformer)
	{
		return TransformedSet.decorate(set, transformer);
	}

	/**
	 * Returns a set that maintains the order of elements that are added
	 * backed by the given set.
	 * <p/>
	 * If an element is added twice, the order is determined by the first add.
	 * The order is observed through the iterator or toArray.
	 *
	 * @param set the set to order, must not be null
	 * @return an ordered set backed by the given set
	 * @throws IllegalArgumentException if the Set is null
	 */
	public static Set orderedSet(Set set)
	{
		return ListOrderedSet.decorate(set);
	}

	/**
	 * Returns a synchronized sorted set backed by the given sorted set.
	 * <p/>
	 * You must manually synchronize on the returned buffer's iterator to
	 * avoid non-deterministic behavior:
	 * <p/>
	 * <pre>
	 * Set s = SetUtility.synchronizedSet(mySet);
	 * synchronized (s) {
	 *     Iterator i = s.iterator();
	 *     while (i.hasNext()) {
	 *         process (i.next());
	 *     }
	 * }
	 * </pre>
	 * <p/>
	 * This method uses the implementation in the decorators subpackage.
	 *
	 * @param set the sorted set to synchronize, must not be null
	 * @return a synchronized set backed by the given set
	 * @throws IllegalArgumentException if the set is null
	 */
	public static SortedSet synchronizedSortedSet(SortedSet set)
	{
		return SynchronizedSortedSet.decorate(set);
	}

	/**
	 * Returns an unmodifiable sorted set backed by the given sorted set.
	 * <p/>
	 * This method uses the implementation in the decorators subpackage.
	 *
	 * @param set the sorted set to make unmodifiable, must not be null
	 * @return an unmodifiable set backed by the given set
	 * @throws IllegalArgumentException if the set is null
	 */
	public static SortedSet unmodifiableSortedSet(SortedSet set)
	{
		return UnmodifiableSortedSet.decorate(set);
	}

	/**
	 * Returns a predicated (validating) sorted set backed by the given sorted set.
	 * <p/>
	 * Only objects that pass the test in the given predicate can be added to the set.
	 * Trying to add an invalid object results in an IllegalArgumentException.
	 * It is important not to use the original set after invoking this method,
	 * as it is a backdoor for adding invalid objects.
	 *
	 * @param set       the sorted set to predicate, must not be null
	 * @param predicate the predicate for the sorted set, must not be null
	 * @return a predicated sorted set backed by the given sorted set
	 * @throws IllegalArgumentException if the Set or Predicate is null
	 */
	public static SortedSet predicatedSortedSet(SortedSet set, Predicate predicate)
	{
		return PredicatedSortedSet.decorate(set, predicate);
	}

	/**
	 * Returns a typed sorted set backed by the given set.
	 * <p/>
	 * Only objects of the specified type can be added to the set.
	 *
	 * @param set  the set to limit to a specific type, must not be null
	 * @param type the type of objects which may be added to the set
	 * @return a typed set backed by the specified set
	 */
	public static SortedSet typedSortedSet(SortedSet set, Class type)
	{
		return TypedSortedSet.decorate(set, type);
	}

	/**
	 * Returns a transformed sorted set backed by the given set.
	 * <p/>
	 * Each object is passed through the transformer as it is added to the
	 * Set. It is important not to use the original set after invoking this
	 * method, as it is a backdoor for adding untransformed objects.
	 *
	 * @param set         the set to transform, must not be null
	 * @param transformer the transformer for the set, must not be null
	 * @return a transformed set backed by the given set
	 * @throws IllegalArgumentException if the Set or Transformer is null
	 */
	public static SortedSet transformedSortedSet(SortedSet set, Transformer transformer)
	{
		return TransformedSortedSet.decorate(set, transformer);
	}

}
