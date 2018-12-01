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
 * Reverses the order of another comparator by reversing the arguments
 * to its {@link #compare(Object, Object) compare} method.
 *
 * @version $Id: ReverseComparator.java,v 1.2 2008/05/14 09:32:40 swd\stefan.damian Exp $
 * @see java.util.Collections#reverseOrder()
 */
public class ReverseComparator implements Comparator, Serializable
{
	/**
	 * The comparator being decorated.
	 */
	private Comparator comparator;

	/**
	 * Creates a comparator that compares objects based on the inverse of their
	 * natural ordering.  Using this Constructor will create a ReverseComparator
	 * that is functionally identical to the Comparator returned by
	 * java.util.Collections.<b>reverseOrder()</b>.
	 *
	 * @see java.util.Collections#reverseOrder()
	 */
	public ReverseComparator()
	{
		this(null);
	}

	/**
	 * Creates a comparator that inverts the comparison
	 * of the given comparator.  If you pass in <code>null</code>,
	 * the ReverseComparator defaults to reversing the
	 * natural order, as per
	 * {@link java.util.Collections#reverseOrder()}</b>.
	 *
	 * @param comparator Comparator to reverse
	 */
	public ReverseComparator(Comparator comparator)
	{
		if (comparator != null)
		{
			this.comparator = comparator;
		}
		else
		{
			this.comparator = ComparableComparator.getInstance();
		}
	}

	/**
	 * Compares two objects in reverse order.
	 *
	 * @param obj1 the first object to compare
	 * @param obj2 the second object to compare
	 * @return negative if obj1 is less, positive if greater, zero if equal
	 */
	public int compare(Object obj1, Object obj2)
	{
		return comparator.compare(obj2, obj1);
	}

	/**
	 * Implement a hash code for this comparator that is consistent with
	 * {@link #equals(Object) equals}.
	 *
	 * @return a suitable hash code
	 */
	public int hashCode()
	{
		return "ReverseComparator".hashCode() ^ comparator.hashCode();
	}

	/**
	 * Returns <code>true</code> iff <i>that</i> Object is
	 * is a {@link Comparator} whose ordering is known to be
	 * equivalent to mine.
	 * <p/>
	 * This implementation returns <code>true</code>
	 * iff <code><i>object</i>.{@link Object#getClass() getClass()}</code>
	 * equals <code>this.getClass()</code>, and the underlying
	 * comparators are equal.
	 * Subclasses may want to override this behavior to remain consistent
	 * with the {@link Comparator#equals(Object) equals} contract.
	 *
	 * @param object the object to compare to
	 * @return true if equal
	 */
	public boolean equals(Object object)
	{
		if (this == object)
		{
			return true;
		}
		else if (null == object)
		{
			return false;
		}
		else if (object.getClass().equals(this.getClass()))
		{
			ReverseComparator thatrc = (ReverseComparator) object;
			return comparator.equals(thatrc.comparator);
		}
		else
		{
			return false;
		}
	}

}
