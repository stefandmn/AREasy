package org.areasy.common.data.type.iterator;

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

import org.areasy.common.data.type.ResettableIterator;

import java.util.Iterator;
import java.util.NoSuchElementException;

/**
 * <code>SingletonIterator</code> is an {@link Iterator} over a single
 * object instance.
 *
 * @version $Id: SingletonIterator.java,v 1.2 2008/05/14 09:32:40 swd\stefan.damian Exp $
 */
public class SingletonIterator
		implements Iterator, ResettableIterator
{

	/**
	 * Whether remove is allowed
	 */
	private final boolean removeAllowed;
	/**
	 * Is the cursor before the first element
	 */
	private boolean beforeFirst = true;
	/**
	 * Has the element been removed
	 */
	private boolean removed = false;
	/**
	 * The object
	 */
	private Object object;

	/**
	 * Constructs a new <code>SingletonIterator</code> where <code>remove</code>
	 * is a permitted operation.
	 *
	 * @param object the single object to return from the iterator
	 */
	public SingletonIterator(Object object)
	{
		this(object, true);
	}

	/**
	 * Constructs a new <code>SingletonIterator</code> optionally choosing if
	 * <code>remove</code> is a permitted operation.
	 *
	 * @param object        the single object to return from the iterator
	 * @param removeAllowed true if remove is allowed
	 */
	public SingletonIterator(Object object, boolean removeAllowed)
	{
		super();
		this.object = object;
		this.removeAllowed = removeAllowed;
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
		return (beforeFirst && !removed);
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
		return object;
	}

	/**
	 * Remove the object from this iterator.
	 *
	 * @throws IllegalStateException         if the <tt>next</tt> method has not
	 *                                       yet been called, or the <tt>remove</tt> method has already
	 *                                       been called after the last call to the <tt>next</tt>
	 *                                       method.
	 * @throws UnsupportedOperationException if remove is not supported
	 */
	public void remove()
	{
		if (removeAllowed)
		{
			if (removed || beforeFirst)
			{
				throw new IllegalStateException();
			}
			else
			{
				object = null;
				removed = true;
			}
		}
		else
		{
			throw new UnsupportedOperationException();
		}
	}

	/**
	 * Reset the iterator to the start.
	 */
	public void reset()
	{
		beforeFirst = true;
	}

}
