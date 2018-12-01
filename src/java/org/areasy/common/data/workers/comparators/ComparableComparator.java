package org.areasy.common.data.workers.comparators;

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

import java.io.Serializable;
import java.util.Comparator;

/**
 * A {@link Comparator Comparator} that compares
 * {@link Comparable Comparable} objects.
 * <p />
 * This Comparator is useful, for example,
 * for enforcing the natural order in custom implementations
 * of SortedSet and SortedMap.
 *
 * @version $Id: ComparableComparator.java,v 1.2 2008/05/14 09:32:40 swd\stefan.damian Exp $
 * @see java.util.Collections#reverseOrder()
 */
public class ComparableComparator implements Comparator, Serializable
{
	/**
	 * The singleton instance.
	 */
	private static final ComparableComparator instance = new ComparableComparator();

	/**
	 * Gets the singleton instance of a ComparableComparator.
	 * <p/>
	 * Developers are encouraged to use the comparator returned from this method
	 * instead of constructing a new instance to reduce allocation and GC overhead
	 * when multiple comparable comparators may be used in the same VM.
	 *
	 * @return the singleton ComparableComparator
	 */
	public static ComparableComparator getInstance()
	{
		return instance;
	}

	/**
	 * Constructor whose use should be avoided.
	 * <p/>
	 * Please use the {@link #getInstance()} method whenever possible.
	 */
	public ComparableComparator()
	{
		super();
	}

	/**
	 * Compare the two {@link Comparable Comparable} arguments.
	 * This method is equivalent to:
	 * <pre>((Comparable)obj1).compareTo(obj2)</pre>
	 *
	 * @param obj1 the first object to compare
	 * @param obj2 the second object to compare
	 * @return negative if obj1 is less, positive if greater, zero if equal
	 * @throws NullPointerException when <i>obj1</i> is <code>null</code>,
	 *                              or when <code>((Comparable)obj1).compareTo(obj2)</code> does
	 * @throws ClassCastException   when <i>obj1</i> is not a <code>Comparable</code>,
	 *                              or when <code>((Comparable)obj1).compareTo(obj2)</code> does
	 */
	public int compare(Object obj1, Object obj2)
	{
		return ((Comparable) obj1).compareTo(obj2);
	}

	/**
	 * Implement a hash code for this comparator that is consistent with
	 * {@link #equals(Object) equals}.
	 *
	 * @return a hash code for this comparator.
	 */
	public int hashCode()
	{
		return "ComparableComparator".hashCode();
	}

	/**
	 * Returns <code>true</code> iff <i>that</i> Object is
	 * is a {@link Comparator Comparator} whose ordering is
	 * known to be equivalent to mine.
	 * <p/>
	 * This implementation returns <code>true</code>
	 * iff <code><i>object</i>.{@link Object#getClass() getClass()}</code>
	 * equals <code>this.getClass()</code>.
	 * Subclasses may want to override this behavior to remain consistent
	 * with the {@link Comparator#equals(Object)} contract.
	 *
	 * @param object the object to compare with
	 * @return true if equal
	 */
	public boolean equals(Object object)
	{
		return (this == object) ||
				((null != object) && (object.getClass().equals(this.getClass())));
	}

}
