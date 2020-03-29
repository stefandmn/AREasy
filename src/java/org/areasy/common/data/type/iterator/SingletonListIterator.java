package org.areasy.common.data.type.iterator;

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

import org.areasy.common.data.type.ResettableListIterator;

import java.util.ListIterator;
import java.util.NoSuchElementException;

/**
 * <code>SingletonIterator</code> is an {@link ListIterator} over a single
 * object instance.
 *
 * @version $Id: SingletonListIterator.java,v 1.2 2008/05/14 09:32:40 swd\stefan.damian Exp $
 */
public class SingletonListIterator implements ListIterator, ResettableListIterator
{

	private boolean beforeFirst = true;
	private boolean nextCalled = false;
	private boolean removed = false;
	private Object object;

	/**
	 * Constructs a new <code>SingletonListIterator</code>.
	 *
	 * @param object the single object to return from the iterator
	 */
	public SingletonListIterator(Object object)
	{
		super();
		this.object = object;
	}

	/**
	 * Is another object available from the iterator?
	 * <p/>
	 * This returns true if the single object hasn't been returned yet.
	 *
	 * @return true if the single object hasn't been returned yet
	 */
	public boolean hasNext()
	{
		return beforeFirst && !removed;
	}

	/**
	 * Is a previous object available from the iterator?
	 * <p/>
	 * This returns true if the single object has been returned.
	 *
	 * @return true if the single object has been returned
	 */
	public boolean hasPrevious()
	{
		return !beforeFirst && !removed;
	}

	/**
	 * Returns the index of the element that would be returned by a subsequent
	 * call to <tt>next</tt>.
	 *
	 * @return 0 or 1 depending on current state.
	 */
	public int nextIndex()
	{
		return (beforeFirst ? 0 : 1);
	}

	/**
	 * Returns the index of the element that would be returned by a subsequent
	 * call to <tt>previous</tt>. A return value of -1 indicates that the iterator is currently at
	 * the start.
	 *
	 * @return 0 or -1 depending on current state.
	 */
	public int previousIndex()
	{
		return (beforeFirst ? -1 : 0);
	}

	/**
	 * Get the next object from the iterator.
	 * <p/>
	 * This returns the single object if it hasn't been returned yet.
	 *
	 * @return the single object
	 * @throws NoSuchElementException if the single object has already
	 *                                been returned
	 */
	public Object next()
	{
		if (!beforeFirst || removed)
		{
			throw new NoSuchElementException();
		}
		beforeFirst = false;
		nextCalled = true;
		return object;
	}

	/**
	 * Get the previous object from the iterator.
	 * <p/>
	 * This returns the single object if it has been returned.
	 *
	 * @return the single object
	 * @throws NoSuchElementException if the single object has not already
	 *                                been returned
	 */
	public Object previous()
	{
		if (beforeFirst || removed)
		{
			throw new NoSuchElementException();
		}
		beforeFirst = true;
		return object;
	}

	/**
	 * Remove the object from this iterator.
	 *
	 * @throws IllegalStateException if the <tt>next</tt> or <tt>previous</tt>
	 *                               method has not yet been called, or the <tt>remove</tt> method
	 *                               has already been called after the last call to <tt>next</tt>
	 *                               or <tt>previous</tt>.
	 */
	public void remove()
	{
		if (!nextCalled || removed)
		{
			throw new IllegalStateException();
		}
		else
		{
			object = null;
			removed = true;
		}
	}

	/**
	 * Add always throws {@link UnsupportedOperationException}.
	 *
	 * @throws UnsupportedOperationException always
	 */
	public void add(Object obj)
	{
		throw new UnsupportedOperationException("add() is not supported by this iterator");
	}

	/**
	 * Set sets the value of the singleton.
	 *
	 * @param obj the object to set
	 * @throws IllegalStateException if <tt>next</tt> has not been called
	 *                               or the object has been removed
	 */
	public void set(Object obj)
	{
		if (!nextCalled || removed)
		{
			throw new IllegalStateException();
		}
		this.object = obj;
	}

	/**
	 * Reset the iterator back to the start.
	 */
	public void reset()
	{
		beforeFirst = true;
		nextCalled = false;
	}

}
