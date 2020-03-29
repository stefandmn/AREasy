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

import org.areasy.common.data.type.Predicate;

import java.util.Iterator;
import java.util.NoSuchElementException;

/**
 * Decorates an iterator such that only elements matching a predicate filter
 * are returned.
 *
 * @version $Id: FilterIterator.java,v 1.3 2008/05/14 09:32:40 swd\stefan.damian Exp $
 */
public class FilterIterator implements Iterator
{
	/**
	 * The iterator being used
	 */
	private Iterator iterator;
	/**
	 * The predicate being used
	 */
	private Predicate predicate;
	/**
	 * The next object in the iteration
	 */
	private Object nextObject;
	/**
	 * Whether the next object has been calculated yet
	 */
	private boolean nextObjectSet = false;

	/**
	 * Constructs a new <code>FilterIterator</code> that will not function
	 * until {@link #setIterator(Iterator) setIterator} is invoked.
	 */
	public FilterIterator()
	{
		super();
	}

	/**
	 * Constructs a new <code>FilterIterator</code> that will not function
	 * until {@link #setPredicate(Predicate) setPredicate} is invoked.
	 *
	 * @param iterator the iterator to use
	 */
	public FilterIterator(Iterator iterator)
	{
		super();
		this.iterator = iterator;
	}

	/**
	 * Constructs a new <code>FilterIterator</code> that will use the
	 * given iterator and predicate.
	 *
	 * @param iterator  the iterator to use
	 * @param predicate the predicate to use
	 */
	public FilterIterator(Iterator iterator, Predicate predicate)
	{
		super();

		this.iterator = iterator;
		this.predicate = predicate;
	}

	/**
	 * Returns true if the underlying iterator contains an object that
	 * matches the predicate.
	 *
	 * @return true if there is another object that matches the predicate
	 */
	public boolean hasNext()
	{
		if (nextObjectSet) return true;
			else return setNextObject();
	}

	/**
	 * Returns the next object that matches the predicate.
	 *
	 * @return the next object which matches the given predicate
	 * @throws NoSuchElementException if there are no more elements that
	 *                                match the predicate
	 */
	public Object next()
	{
		if (!nextObjectSet)
		{
			if (!setNextObject()) throw new NoSuchElementException();
		}
		
		nextObjectSet = false;
		return nextObject;
	}

	/**
	 * Removes from the underlying collection of the base iterator the last
	 * element returned by this iterator.
	 * This method can only be called
	 * if <code>next()</code> was called, but not after
	 * <code>hasNext()</code>, because the <code>hasNext()</code> call
	 * changes the base iterator.
	 *
	 * @throws IllegalStateException if <code>hasNext()</code> has already
	 *                               been called.
	 */
	public void remove()
	{
		if (nextObjectSet) throw new IllegalStateException("remove() cannot be called");

		iterator.remove();
	}

	/**
	 * Gets the iterator this iterator is using.
	 *
	 * @return the iterator.
	 */
	public Iterator getIterator()
	{
		return iterator;
	}

	/**
	 * Sets the iterator for this iterator to use.
	 * If iteration has started, this effectively resets the iterator.
	 *
	 * @param iterator the iterator to use
	 */
	public void setIterator(Iterator iterator)
	{
		this.iterator = iterator;
	}

	/**
	 * Gets the predicate this iterator is using.
	 *
	 * @return the predicate.
	 */
	public Predicate getPredicate()
	{
		return predicate;
	}

	/**
	 * Sets the predicate this the iterator to use.
	 *
	 * @param predicate the transformer to use
	 */
	public void setPredicate(Predicate predicate)
	{
		this.predicate = predicate;
	}

	/**
	 * Set nextObject to the next object. If there are no more
	 * objects then return false. Otherwise, return true.
	 */
	private boolean setNextObject()
	{
		while (iterator.hasNext())
		{
			Object object = iterator.next();
			if (predicate.evaluate(object))
			{
				nextObject = object;
				nextObjectSet = true;

				return true;
			}
		}

		return false;
	}
}
