package org.areasy.common.data.type.list;

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

import org.areasy.common.data.type.collection.SynchronizedCollection;

import java.util.Collection;
import java.util.List;
import java.util.ListIterator;

/**
 * Decorates another <code>List</code> to synchronize its behaviour
 * for a multi-threaded environment.
 * <p/>
 * Methods are synchronized, then forwarded to the decorated list.
 *
 * @version $Id: SynchronizedList.java,v 1.2 2008/05/14 09:32:35 swd\stefan.damian Exp $
 */
public class SynchronizedList extends SynchronizedCollection implements List
{
	/**
	 * Factory method to create a synchronized list.
	 *
	 * @param list the list to decorate, must not be null
	 * @throws IllegalArgumentException if list is null
	 */
	public static List decorate(List list)
	{
		return new SynchronizedList(list);
	}

	/**
	 * Constructor that wraps (not copies).
	 *
	 * @param list the list to decorate, must not be null
	 * @throws IllegalArgumentException if list is null
	 */
	protected SynchronizedList(List list)
	{
		super(list);
	}

	/**
	 * Constructor that wraps (not copies).
	 *
	 * @param list the list to decorate, must not be null
	 * @param lock the lock to use, must not be null
	 * @throws IllegalArgumentException if list is null
	 */
	protected SynchronizedList(List list, Object lock)
	{
		super(list, lock);
	}

	/**
	 * Gets the decorated list.
	 *
	 * @return the decorated list
	 */
	protected List getList()
	{
		return (List) collection;
	}

	public void add(int index, Object object)
	{
		synchronized (lock)
		{
			getList().add(index, object);
		}
	}

	public boolean addAll(int index, Collection coll)
	{
		synchronized (lock)
		{
			return getList().addAll(index, coll);
		}
	}

	public Object get(int index)
	{
		synchronized (lock)
		{
			return getList().get(index);
		}
	}

	public int indexOf(Object object)
	{
		synchronized (lock)
		{
			return getList().indexOf(object);
		}
	}

	public int lastIndexOf(Object object)
	{
		synchronized (lock)
		{
			return getList().lastIndexOf(object);
		}
	}

	/**
	 * Iterators must be manually synchronized.
	 * <pre>
	 * synchronized (coll) {
	 *   ListIterator it = coll.listIterator();
	 *   // do stuff with iterator
	 * }
	 *
	 * @return an iterator that must be manually synchronized on the collection
	 */
	public ListIterator listIterator()
	{
		return getList().listIterator();
	}

	/**
	 * Iterators must be manually synchronized.
	 * <pre>
	 * synchronized (coll) {
	 *   ListIterator it = coll.listIterator(3);
	 *   // do stuff with iterator
	 * }
	 *
	 * @return an iterator that must be manually synchronized on the collection
	 */
	public ListIterator listIterator(int index)
	{
		return getList().listIterator(index);
	}

	public Object remove(int index)
	{
		synchronized (lock)
		{
			return getList().remove(index);
		}
	}

	public Object set(int index, Object object)
	{
		synchronized (lock)
		{
			return getList().set(index, object);
		}
	}

	public List subList(int fromIndex, int toIndex)
	{
		synchronized (lock)
		{
			List list = getList().subList(fromIndex, toIndex);
			return new SynchronizedList(list, lock);
		}
	}

}
