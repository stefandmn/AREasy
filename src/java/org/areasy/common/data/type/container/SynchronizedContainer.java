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
import org.areasy.common.data.type.collection.SynchronizedCollection;
import org.areasy.common.data.type.set.SynchronizedSet;

import java.util.Set;

/**
 * Decorates another <code>Bag</code> to synchronize its behaviour
 * for a multi-threaded environment.
 * <p/>
 * Methods are synchronized, then forwarded to the decorated bag.
 * Iterators must be separately synchronized around the loop.
 *
 * @version $Id: SynchronizedContainer.java,v 1.2 2008/05/14 09:32:35 swd\stefan.damian Exp $
 */
public class SynchronizedContainer extends SynchronizedCollection implements Container
{
	/**
	 * Factory method to create a synchronized bag.
	 *
	 * @param container the bag to decorate, must not be null
	 * @return a new synchronized Bag
	 * @throws IllegalArgumentException if bag is null
	 */
	public static Container decorate(Container container)
	{
		return new SynchronizedContainer(container);
	}

	/**
	 * Constructor that wraps (not copies).
	 *
	 * @param container the bag to decorate, must not be null
	 * @throws IllegalArgumentException if bag is null
	 */
	protected SynchronizedContainer(Container container)
	{
		super(container);
	}

	/**
	 * Constructor that wraps (not copies).
	 *
	 * @param container the bag to decorate, must not be null
	 * @param lock      the lock to use, must not be null
	 * @throws IllegalArgumentException if bag is null
	 */
	protected SynchronizedContainer(Container container, Object lock)
	{
		super(container, lock);
	}

	/**
	 * Gets the bag being decorated.
	 *
	 * @return the decorated bag
	 */
	protected Container getBag()
	{
		return (Container) collection;
	}

	public boolean add(Object object, int count)
	{
		synchronized (lock)
		{
			return getBag().add(object, count);
		}
	}

	public boolean remove(Object object, int count)
	{
		synchronized (lock)
		{
			return getBag().remove(object, count);
		}
	}

	public Set uniqueSet()
	{
		synchronized (lock)
		{
			Set set = getBag().uniqueSet();
			return new SynchronizedBagSet(set, lock);
		}
	}

	public int getCount(Object object)
	{
		synchronized (lock)
		{
			return getBag().getCount(object);
		}
	}

	/**
	 * Synchronized Set for the Bag class.
	 */
	class SynchronizedBagSet extends SynchronizedSet
	{
		/**
		 * Constructor.
		 *
		 * @param set  the set to decorate
		 * @param lock the lock to use, shared with the bag
		 */
		SynchronizedBagSet(Set set, Object lock)
		{
			super(set, lock);
		}
	}

}
