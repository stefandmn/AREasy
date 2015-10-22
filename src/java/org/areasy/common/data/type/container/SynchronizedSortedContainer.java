package org.areasy.common.data.type.container;

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

import org.areasy.common.data.type.Container;
import org.areasy.common.data.type.SortedContainer;

import java.util.Comparator;

/**
 * Decorates another <code>SortedBag</code> to synchronize its behaviour
 * for a multi-threaded environment.
 * <p/>
 * Methods are synchronized, then forwarded to the decorated bag.
 * Iterators must be separately synchronized around the loop.
 *
 * @version $Id: SynchronizedSortedContainer.java,v 1.2 2008/05/14 09:32:35 swd\stefan.damian Exp $
 */
public class SynchronizedSortedContainer extends SynchronizedContainer implements SortedContainer
{
	/**
	 * Factory method to create a synchronized sorted bag.
	 *
	 * @param bag the bag to decorate, must not be null
	 * @return a new synchronized SortedBag
	 * @throws IllegalArgumentException if bag is null
	 */
	public static SortedContainer decorate(SortedContainer bag)
	{
		return new SynchronizedSortedContainer(bag);
	}

	/**
	 * Constructor that wraps (not copies).
	 *
	 * @param bag the bag to decorate, must not be null
	 * @throws IllegalArgumentException if bag is null
	 */
	protected SynchronizedSortedContainer(SortedContainer bag)
	{
		super(bag);
	}

	/**
	 * Constructor that wraps (not copies).
	 *
	 * @param container the bag to decorate, must not be null
	 * @param lock      the lock to use, must not be null
	 * @throws IllegalArgumentException if bag is null
	 */
	protected SynchronizedSortedContainer(Container container, Object lock)
	{
		super(container, lock);
	}

	/**
	 * Gets the bag being decorated.
	 *
	 * @return the decorated bag
	 */
	protected SortedContainer getSortedBag()
	{
		return (SortedContainer) collection;
	}

	public synchronized Object first()
	{
		synchronized (lock)
		{
			return getSortedBag().first();
		}
	}

	public synchronized Object last()
	{
		synchronized (lock)
		{
			return getSortedBag().last();
		}
	}

	public synchronized Comparator comparator()
	{
		synchronized (lock)
		{
			return getSortedBag().comparator();
		}
	}

}
