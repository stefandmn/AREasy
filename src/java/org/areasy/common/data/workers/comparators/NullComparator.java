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
 * A Comparator that will compare nulls to be either lower or higher than
 * other objects.
 *
 * @version $Id: NullComparator.java,v 1.2 2008/05/14 09:32:40 swd\stefan.damian Exp $
 */
public class NullComparator implements Comparator, Serializable
{
	/**
	 * The comparator to use when comparing two non-<code>null</code> objects.
	 */
	private Comparator nonNullComparator;

	/**
	 * Specifies whether a <code>null</code> are compared as higher than
	 * non-<code>null</code> objects.
	 */
	private boolean nullsAreHigh;

	/**
	 * Construct an instance that sorts <code>null</code> higher than any
	 * non-<code>null</code> object it is compared with. When comparing two
	 * non-<code>null</code> objects, the {@link ComparableComparator} is
	 * used.
	 */
	public NullComparator()
	{
		this(ComparableComparator.getInstance(), true);
	}

	/**
	 * Construct an instance that sorts <code>null</code> higher than any
	 * non-<code>null</code> object it is compared with.  When comparing two
	 * non-<code>null</code> objects, the specified {@link Comparator} is
	 * used.
	 *
	 * @param nonNullComparator the comparator to use when comparing two
	 *                          non-<code>null</code> objects.  This argument cannot be
	 *                          <code>null</code>
	 * @throws NullPointerException if <code>nonNullComparator</code> is
	 *                              <code>null</code>
	 */
	public NullComparator(Comparator nonNullComparator)
	{
		this(nonNullComparator, true);
	}

	/**
	 * Construct an instance that sorts <code>null</code> higher or lower than
	 * any non-<code>null</code> object it is compared with.  When comparing
	 * two non-<code>null</code> objects, the {@link ComparableComparator} is
	 * used.
	 *
	 * @param nullsAreHigh a <code>true</code> value indicates that
	 *                     <code>null</code> should be compared as higher than a
	 *                     non-<code>null</code> object.  A <code>false</code> value indicates
	 *                     that <code>null</code> should be compared as lower than a
	 *                     non-<code>null</code> object.
	 */
	public NullComparator(boolean nullsAreHigh)
	{
		this(ComparableComparator.getInstance(), nullsAreHigh);
	}

	/**
	 * Construct an instance that sorts <code>null</code> higher or lower than
	 * any non-<code>null</code> object it is compared with.  When comparing
	 * two non-<code>null</code> objects, the specified {@link Comparator} is
	 * used.
	 *
	 * @param nonNullComparator the comparator to use when comparing two
	 *                          non-<code>null</code> objects. This argument cannot be
	 *                          <code>null</code>
	 * @param nullsAreHigh      a <code>true</code> value indicates that
	 *                          <code>null</code> should be compared as higher than a
	 *                          non-<code>null</code> object.  A <code>false</code> value indicates
	 *                          that <code>null</code> should be compared as lower than a
	 *                          non-<code>null</code> object.
	 * @throws NullPointerException if <code>nonNullComparator</code> is
	 *                              <code>null</code>
	 */
	public NullComparator(Comparator nonNullComparator, boolean nullsAreHigh)
	{
		this.nonNullComparator = nonNullComparator;
		this.nullsAreHigh = nullsAreHigh;

		if (nonNullComparator == null)
		{
			throw new NullPointerException("null nonNullComparator");
		}
	}

	/**
	 * Perform a comparison between two objects.  If both objects are
	 * <code>null</code>, a <code>0</code> value is returned.  If one object
	 * is <code>null</code> and the other is not, the result is determined on
	 * whether the Comparator was constructed to have nulls as higher or lower
	 * than other objects.  If neither object is <code>null</code>, an
	 * underlying comparator specified in the constructor (or the default) is
	 * used to compare the non-<code>null</code> objects.
	 *
	 * @param o1 the first object to compare
	 * @param o2 the object to compare it to.
	 * @return <code>-1</code> if <code>o1</code> is "lower" than (less than,
	 *         before, etc.) <code>o2</code>; <code>1</code> if <code>o1</code> is
	 *         "higher" than (greater than, after, etc.) <code>o2</code>; or
	 *         <code>0</code> if <code>o1</code> and <code>o2</code> are equal.
	 */
	public int compare(Object o1, Object o2)
	{
		if (o1 == o2)
		{
			return 0;
		}
		if (o1 == null)
		{
			return (this.nullsAreHigh ? 1 : -1);
		}
		if (o2 == null)
		{
			return (this.nullsAreHigh ? -1 : 1);
		}
		return this.nonNullComparator.compare(o1, o2);
	}

	/**
	 * Implement a hash code for this comparator that is consistent with
	 * {@link #equals(Object)}.
	 *
	 * @return a hash code for this comparator.
	 */
	public int hashCode()
	{
		return (nullsAreHigh ? -1 : 1) * nonNullComparator.hashCode();
	}

	/**
	 * Determines whether the specified object represents a comparator that is
	 * equal to this comparator.
	 *
	 * @param obj the object to compare this comparator with.
	 * @return <code>true</code> if the specified object is a NullComparator
	 *         with equivalent <code>null</code> comparison behavior
	 *         (i.e. <code>null</code> high or low) and with equivalent underlying
	 *         non-<code>null</code> object comparators.
	 */
	public boolean equals(Object obj)
	{
		if (obj == null)
		{
			return false;
		}
		if (obj == this)
		{
			return true;
		}
		if (!obj.getClass().equals(this.getClass()))
		{
			return false;
		}

		NullComparator other = (NullComparator) obj;

		return ((this.nullsAreHigh == other.nullsAreHigh) &&
				(this.nonNullComparator.equals(other.nonNullComparator)));
	}

}
