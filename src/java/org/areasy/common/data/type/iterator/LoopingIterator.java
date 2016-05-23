package org.areasy.common.data.type.iterator;

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

import org.areasy.common.data.type.ResettableIterator;

import java.util.Collection;
import java.util.Iterator;
import java.util.NoSuchElementException;

/**
 * An Iterator that restarts when it reaches the end.
 * <p/>
 * The iterator will loop continuously around the provided elements, unless
 * there are no elements in the collection to begin with, or all the elements
 * have been {@link #remove removed}.
 * <p/>
 * Concurrent modifications are not directly supported, and for most collection
 * implementations will throw a ConcurrentModificationException.
 *
 * @version $Id: LoopingIterator.java,v 1.2 2008/05/14 09:32:40 swd\stefan.damian Exp $
 */
public class LoopingIterator implements ResettableIterator
{

	/**
	 * The collection to base the iterator on
	 */
	private Collection collection;
	/**
	 * The current iterator
	 */
	private Iterator iterator;

	/**
	 * Constructor that wraps a collection.
	 * <p/>
	 * There is no way to reset an Iterator instance without recreating it from
	 * the original source, so the Collection must be passed in.
	 *
	 * @param coll the collection to wrap
	 * @throws NullPointerException if the collection is null
	 */
	public LoopingIterator(Collection coll)
	{
		if (coll == null)
		{
			throw new NullPointerException("The collection must not be null");
		}
		collection = coll;
		reset();
	}

	/**
	 * Has the iterator any more elements.
	 * <p/>
	 * Returns false only if the collection originally had zero elements, or
	 * all the elements have been {@link #remove removed}.
	 *
	 * @return <code>true</code> if there are more elements
	 */
	public boolean hasNext()
	{
		return (collection.size() > 0);
	}

	/**
	 * Returns the next object in the collection.
	 * <p/>
	 * If at the end of the collection, return the first element.
	 *
	 * @throws NoSuchElementException if there are no elements
	 *                                at all.  Use {@link #hasNext} to avoid this error.
	 */
	public Object next()
	{
		if (collection.size() == 0)
		{
			throw new NoSuchElementException("There are no elements for this iterator to loop on");
		}
		if (iterator.hasNext() == false)
		{
			reset();
		}
		return iterator.next();
	}

	/**
	 * Removes the previously retrieved item from the underlying collection.
	 * <p/>
	 * This feature is only supported if the underlying collection's
	 * {@link Collection#iterator iterator} method returns an implementation
	 * that supports it.
	 * <p/>
	 * This method can only be called after at least one {@link #next} method call.
	 * After a removal, the remove method may not be called again until another
	 * next has been performed. If the {@link #reset} is called, then remove may
	 * not be called until {@link #next} is called again.
	 */
	public void remove()
	{
		iterator.remove();
	}

	/**
	 * Resets the iterator back to the start of the collection.
	 */
	public void reset()
	{
		iterator = collection.iterator();
	}

	/**
	 * Gets the size of the collection underlying the iterator.
	 *
	 * @return the current collection size
	 */
	public int size()
	{
		return collection.size();
	}

}
