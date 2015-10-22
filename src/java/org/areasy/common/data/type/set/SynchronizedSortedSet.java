package org.areasy.common.data.type.set;

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

import org.areasy.common.data.type.collection.SynchronizedCollection;

import java.util.Comparator;
import java.util.SortedSet;

/**
 * Decorates another <code>SortedSet</code> to synchronize its behaviour
 * for a multi-threaded environment.
 * <p/>
 * Methods are synchronized, then forwarded to the decorated set.
 *
 * @version $Id: SynchronizedSortedSet.java,v 1.2 2008/05/14 09:32:37 swd\stefan.damian Exp $
 */
public class SynchronizedSortedSet extends SynchronizedCollection implements SortedSet
{
	/**
	 * Factory method to create a synchronized set.
	 *
	 * @param set the set to decorate, must not be null
	 * @throws IllegalArgumentException if set is null
	 */
	public static SortedSet decorate(SortedSet set)
	{
		return new SynchronizedSortedSet(set);
	}

	/**
	 * Constructor that wraps (not copies).
	 *
	 * @param set the set to decorate, must not be null
	 * @throws IllegalArgumentException if set is null
	 */
	protected SynchronizedSortedSet(SortedSet set)
	{
		super(set);
	}

	/**
	 * Constructor that wraps (not copies).
	 *
	 * @param set  the set to decorate, must not be null
	 * @param lock the lock object to use, must not be null
	 * @throws IllegalArgumentException if set is null
	 */
	protected SynchronizedSortedSet(SortedSet set, Object lock)
	{
		super(set, lock);
	}

	/**
	 * Gets the decorated set.
	 *
	 * @return the decorated set
	 */
	protected SortedSet getSortedSet()
	{
		return (SortedSet) collection;
	}

	public SortedSet subSet(Object fromElement, Object toElement)
	{
		synchronized (lock)
		{
			SortedSet set = getSortedSet().subSet(fromElement, toElement);
			// the lock is passed into the constructor here to ensure that the
			// subset is synchronized on the same lock as the parent
			return new SynchronizedSortedSet(set, lock);
		}
	}

	public SortedSet headSet(Object toElement)
	{
		synchronized (lock)
		{
			SortedSet set = getSortedSet().headSet(toElement);
			// the lock is passed into the constructor here to ensure that the
			// headset is synchronized on the same lock as the parent
			return new SynchronizedSortedSet(set, lock);
		}
	}

	public SortedSet tailSet(Object fromElement)
	{
		synchronized (lock)
		{
			SortedSet set = getSortedSet().tailSet(fromElement);
			// the lock is passed into the constructor here to ensure that the
			// tailset is synchronized on the same lock as the parent
			return new SynchronizedSortedSet(set, lock);
		}
	}

	public Object first()
	{
		synchronized (lock)
		{
			return getSortedSet().first();
		}
	}

	public Object last()
	{
		synchronized (lock)
		{
			return getSortedSet().last();
		}
	}

	public Comparator comparator()
	{
		synchronized (lock)
		{
			return getSortedSet().comparator();
		}
	}

}
