package org.areasy.common.data.type.collection;

/*
 * Copyright (c) 2007-2016 AREasy Runtime
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
import java.util.Collection;
import java.util.Iterator;

/**
 * Decorates another <code>Collection</code> to synchronize its behaviour
 * for a multi-threaded environment.
 * <p/>
 * Iterators must be manually synchronized:
 * <pre>
 * synchronized (coll) {
 *   Iterator it = coll.iterator();
 *   // do stuff with iterator
 * }
 * </pre>
 *
 * @version $Id: SynchronizedCollection.java,v 1.3 2008/05/14 09:32:34 swd\stefan.damian Exp $
 */
public class SynchronizedCollection implements Collection, Serializable
{
	/**
	 * The collection to decorate
	 */
	protected final Collection collection;
	/**
	 * The object to lock on, needed for List/SortedSet views
	 */
	protected final Object lock;

	/**
	 * Factory method to create a synchronized collection.
	 *
	 * @param coll the collection to decorate, must not be null
	 * @return a new synchronized collection
	 * @throws IllegalArgumentException if collection is null
	 */
	public static Collection decorate(Collection coll)
	{
		return new SynchronizedCollection(coll);
	}

	/**
	 * Constructor that wraps (not copies).
	 *
	 * @param collection the collection to decorate, must not be null
	 * @throws IllegalArgumentException if the collection is null
	 */
	protected SynchronizedCollection(Collection collection)
	{
		if (collection == null) throw new IllegalArgumentException("Collection must not be null");

		this.collection = collection;
		this.lock = this;
	}

	/**
	 * Constructor that wraps (not copies).
	 *
	 * @param collection the collection to decorate, must not be null
	 * @param lock       the lock object to use, must not be null
	 * @throws IllegalArgumentException if the collection is null
	 */
	protected SynchronizedCollection(Collection collection, Object lock)
	{
		if (collection == null) throw new IllegalArgumentException("Collection must not be null");

		this.collection = collection;
		this.lock = lock;
	}

	public boolean add(Object object)
	{
		synchronized (lock)
		{
			return collection.add(object);
		}
	}

	public boolean addAll(Collection coll)
	{
		synchronized (lock)
		{
			return collection.addAll(coll);
		}
	}

	public void clear()
	{
		synchronized (lock)
		{
			collection.clear();
		}
	}

	public boolean contains(Object object)
	{
		synchronized (lock)
		{
			return collection.contains(object);
		}
	}

	public boolean containsAll(Collection coll)
	{
		synchronized (lock)
		{
			return collection.containsAll(coll);
		}
	}

	public boolean isEmpty()
	{
		synchronized (lock)
		{
			return collection.isEmpty();
		}
	}

	/**
	 * Iterators must be manually synchronized.
	 * <pre>
	 * synchronized (coll) {
	 *   Iterator it = coll.iterator();
	 *   // do stuff with iterator
	 * }
	 *
	 * @return an iterator that must be manually synchronized on the collection
	 */
	public Iterator iterator()
	{
		return collection.iterator();
	}

	public Object[] toArray()
	{
		synchronized (lock)
		{
			return collection.toArray();
		}
	}

	public Object[] toArray(Object[] object)
	{
		synchronized (lock)
		{
			return collection.toArray(object);
		}
	}

	public boolean remove(Object object)
	{
		synchronized (lock)
		{
			return collection.remove(object);
		}
	}

	public boolean removeAll(Collection coll)
	{
		synchronized (lock)
		{
			return collection.removeAll(coll);
		}
	}

	public boolean retainAll(Collection coll)
	{
		synchronized (lock)
		{
			return collection.retainAll(coll);
		}
	}

	public int size()
	{
		synchronized (lock)
		{
			return collection.size();
		}
	}

	public boolean equals(Object object)
	{
		synchronized (lock)
		{
			if (object == this)
			{
				return true;
			}
			return collection.equals(object);
		}
	}

	public int hashCode()
	{
		synchronized (lock)
		{
			return collection.hashCode();
		}
	}

	public String toString()
	{
		synchronized (lock)
		{
			return collection.toString();
		}
	}
}
