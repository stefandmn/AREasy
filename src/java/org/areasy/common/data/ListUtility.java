package org.areasy.common.data;

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

import org.areasy.common.data.type.Factory;
import org.areasy.common.data.type.Predicate;
import org.areasy.common.data.type.Transformer;
import org.areasy.common.data.type.list.*;

import java.util.*;

/**
 * Provides utility methods and decorators for {@link List} instances.
 *
 * @version $Id: ListUtility.java,v 1.2 2008/05/14 09:32:29 swd\stefan.damian Exp $
 */
public class ListUtility
{

	/**
	 * An empty unmodifiable list.
	 * This uses the {@link Collections Collections} implementation
	 * and is provided for completeness.
	 */
	public static final List EMPTY_LIST = Collections.EMPTY_LIST;

	/**
	 * <code>ListUtility</code> should not normally be instantiated.
	 */
	public ListUtility()
	{
		//nothing to do
	}

	/**
	 * Returns a new list containing all elements that are contained in
	 * both given lists.
	 *
	 * @param list1 the first list
	 * @param list2 the second list
	 * @return the intersection of those two lists
	 * @throws NullPointerException if either list is null
	 */
	public static List intersection(final List list1, final List list2)
	{
		final ArrayList result = new ArrayList();
		final Iterator iterator = list2.iterator();

		while (iterator.hasNext())
		{
			final Object o = iterator.next();

			if (list1.contains(o)) result.add(o);
		}

		return result;
	}

	/**
	 * Subtracts all elements in the second list from the first list,
	 * placing the results in a new list.
	 * <p/>
	 * This differs from {@link List#removeAll(Collection)} in that
	 * cardinality is respected; if <Code>list1</Code> contains two
	 * occurrences of <Code>null</Code> and <Code>list2</Code> only
	 * contains one occurrence, then the returned list will still contain
	 * one occurrence.
	 *
	 * @param list1 the list to subtract from
	 * @param list2 the list to subtract
	 * @return a new list containing the results
	 * @throws NullPointerException if either list is null
	 */
	public static List subtract(final List list1, final List list2)
	{
		final ArrayList result = new ArrayList(list1);
		final Iterator iterator = list2.iterator();

		while (iterator.hasNext())
		{
			result.remove(iterator.next());
		}

		return result;
	}

	/**
	 * Returns the sum of the given lists.  This is their intersection
	 * subtracted from their union.
	 *
	 * @param list1 the first list
	 * @param list2 the second list
	 * @return a new list containing the sum of those lists
	 * @throws NullPointerException if either list is null
	 */
	public static List sum(final List list1, final List list2)
	{
		return subtract(union(list1, list2), intersection(list1, list2));
	}

	/**
	 * Returns a new list containing the second list appended to the
	 * first list.  The {@link List#addAll(Collection)} operation is
	 * used to append the two given lists into a new list.
	 *
	 * @param list1 the first list
	 * @param list2 the second list
	 * @return a new list containing the union of those lists
	 * @throws NullPointerException if either list is null
	 */
	public static List union(final List list1, final List list2)
	{
		final ArrayList result = new ArrayList(list1);

		result.addAll(list2);

		return result;
	}

	/**
	 * Tests two lists for value-equality as per the equality contract in
	 * {@link java.util.List#equals(java.lang.Object)}.
	 * <p/>
	 * This method is useful for implementing <code>List</code> when you cannot
	 * extend AbstractList. The method takes Collection instances to enable other
	 * collection types to use the List implementation algorithm.
	 * <p/>
	 * The relevant text (slightly paraphrased as this is a static method) is:
	 * <blockquote>
	 * Compares the two list objects for equality.  Returns
	 * <tt>true</tt> if and only if both
	 * lists have the same size, and all corresponding pairs of elements in
	 * the two lists are <i>equal</i>.  (Two elements <tt>e1</tt> and
	 * <tt>e2</tt> are <i>equal</i> if <tt>(e1==null ? e2==null :
	 * e1.equals(e2))</tt>.)  In other words, two lists are defined to be
	 * equal if they contain the same elements in the same order.  This
	 * definition ensures that the equals method works properly across
	 * different implementations of the <tt>List</tt> interface.
	 * </blockquote>
	 * <p/>
	 * <b>Note:</b> The behaviour of this method is undefined if the lists are
	 * modified during the equals comparison.
	 *
	 * @param list1 the first list, may be null
	 * @param list2 the second list, may be null
	 * @return whether the lists are equal by value comparison
	 * @see java.util.List
	 */
	public static boolean isEqualList(final Collection list1, final Collection list2)
	{
		if (list1 == list2) return true;
		if (list1 == null || list2 == null || list1.size() != list2.size()) return false;

		Iterator it1 = list1.iterator();
		Iterator it2 = list2.iterator();
		Object obj1 = null;
		Object obj2 = null;

		while (it1.hasNext() && it2.hasNext())
		{
			obj1 = it1.next();
			obj2 = it2.next();

			if (!(obj1 == null ? obj2 == null : obj1.equals(obj2))) return false;
		}

		return !(it1.hasNext() || it2.hasNext());
	}

	/**
	 * Generates a hash code using the algorithm specified in
	 * {@link java.util.List#hashCode()}.
	 * <p/>
	 * This method is useful for implementing <code>List</code> when you cannot
	 * extend AbstractList. The method takes Collection instances to enable other
	 * collection types to use the List implementation algorithm.
	 *
	 * @param list the list to generate the hashCode for, may be null
	 * @return the hash code
	 * @see java.util.List#hashCode()
	 */
	public static int hashCodeForList(final Collection list)
	{
		if (list == null) return 0;

		int hashCode = 1;
		Iterator it = list.iterator();
		Object obj = null;

		while (it.hasNext())
		{
			obj = it.next();
			hashCode = 31 * hashCode + (obj == null ? 0 : obj.hashCode());
		}

		return hashCode;
	}

	/**
	 * Returns a synchronized list backed by the given list.
	 * <p/>
	 * You must manually synchronize on the returned buffer's iterator to
	 * avoid non-deterministic behavior:
	 * <p/>
	 * <pre>
	 * List list = ListUtility.synchronizedList(myList);
	 * synchronized (list) {
	 *     Iterator i = list.iterator();
	 *     while (i.hasNext()) {
	 *         process (i.next());
	 *     }
	 * }
	 * </pre>
	 * <p/>
	 * This method uses the implementation in the decorators subpackage.
	 *
	 * @param list the list to synchronize, must not be null
	 * @return a synchronized list backed by the given list
	 * @throws IllegalArgumentException if the list is null
	 */
	public static List synchronizedList(List list)
	{
		return SynchronizedList.decorate(list);
	}

	/**
	 * Returns an unmodifiable list backed by the given list.
	 * <p/>
	 * This method uses the implementation in the decorators subpackage.
	 *
	 * @param list the list to make unmodifiable, must not be null
	 * @return an unmodifiable list backed by the given list
	 * @throws IllegalArgumentException if the list is null
	 */
	public static List unmodifiableList(List list)
	{
		return UnmodifiableList.decorate(list);
	}

	/**
	 * Returns a predicated (validating) list backed by the given list.
	 * <p/>
	 * Only objects that pass the test in the given predicate can be added to the list.
	 * Trying to add an invalid object results in an IllegalArgumentException.
	 * It is important not to use the original list after invoking this method,
	 * as it is a backdoor for adding invalid objects.
	 *
	 * @param list      the list to predicate, must not be null
	 * @param predicate the predicate for the list, must not be null
	 * @return a predicated list backed by the given list
	 * @throws IllegalArgumentException if the List or Predicate is null
	 */
	public static List predicatedList(List list, Predicate predicate)
	{
		return PredicatedList.decorate(list, predicate);
	}

	/**
	 * Returns a typed list backed by the given list.
	 * <p/>
	 * Only objects of the specified type can be added to the list.
	 *
	 * @param list the list to limit to a specific type, must not be null
	 * @param type the type of objects which may be added to the list
	 * @return a typed list backed by the specified list
	 */
	public static List typedList(List list, Class type)
	{
		return TypedList.decorate(list, type);
	}

	/**
	 * Returns a transformed list backed by the given list.
	 * <p/>
	 * Each object is passed through the transformer as it is added to the
	 * List. It is important not to use the original list after invoking this
	 * method, as it is a backdoor for adding untransformed objects.
	 *
	 * @param list        the list to predicate, must not be null
	 * @param transformer the transformer for the list, must not be null
	 * @return a transformed list backed by the given list
	 * @throws IllegalArgumentException if the List or Transformer is null
	 */
	public static List transformedList(List list, Transformer transformer)
	{
		return TransformedList.decorate(list, transformer);
	}

	/**
	 * Returns a "slow" list whose elements will be created on demand.
	 * <p/>
	 * When the index passed to the returned list's {@link List#get(int) get}
	 * method is greater than the list's size, then the factory will be used
	 * to create a new object and that object will be inserted at that index.
	 * <p/>
	 * For instance:
	 * <p/>
	 * <pre>
	 * Factory factory = new Factory() {
	 *     public Object create() {
	 *         return new Date();
	 *     }
	 * }
	 * List lazy = ListUtility.lazyList(new ArrayList(), factory);
	 * Object obj = lazy.get(3);
	 * </pre>
	 * <p/>
	 * After the above code is executed, <code>obj</code> will contain
	 * a new <code>Date</code> instance.  Furthermore, that <code>Date</code>
	 * instance is the fourth element in the list.  The first, second,
	 * and third element are all set to <code>null</code>.
	 *
	 * @param list    the list to make lazy, must not be null
	 * @param factory the factory for creating new objects, must not be null
	 * @return a lazy list backed by the given list
	 * @throws IllegalArgumentException if the List or Factory is null
	 */
	public static List slowList(List list, Factory factory)
	{
		return SlowList.decorate(list, factory);
	}

	/**
	 * Returns a fixed-sized list backed by the given list.
	 * Elements may not be added or removed from the returned list, but
	 * existing elements can be changed (for instance, via the
	 * {@link List#set(int,Object)} method).
	 *
	 * @param list the list whose size to fix, must not be null
	 * @return a fixed-size list backed by that list
	 * @throws IllegalArgumentException if the List is null
	 */
	public static List fixedSizeList(List list)
	{
		return FixedSizeList.decorate(list);
	}
}
