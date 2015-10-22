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

import org.areasy.common.data.type.Predicate;

import java.util.ListIterator;
import java.util.NoSuchElementException;

/**
 * A proxy {@link ListIterator ListIterator} which
 * takes a {@link Predicate Predicate} instance to filter
 * out objects from an underlying <code>ListIterator</code>
 * instance. Only objects for which the specified
 * <code>Predicate</code> evaluates to <code>true</code> are
 * returned by the iterator.
 *
 * @version $Id: FilterListIterator.java,v 1.2 2008/05/14 09:32:40 swd\stefan.damian Exp $
 */
public class FilterListIterator implements ListIterator
{

	/**
	 * The iterator being used
	 */
	private ListIterator iterator;

	/**
	 * The predicate being used
	 */
	private Predicate predicate;

	/**
	 * The value of the next (matching) object, when
	 * {@link #nextObjectSet} is true.
	 */
	private Object nextObject;

	/**
	 * Whether or not the {@link #nextObject} has been set
	 * (possibly to <code>null</code>).
	 */
	private boolean nextObjectSet = false;

	/**
	 * The value of the previous (matching) object, when
	 * {@link #previousObjectSet} is true.
	 */
	private Object previousObject;

	/**
	 * Whether or not the {@link #previousObject} has been set
	 * (possibly to <code>null</code>).
	 */
	private boolean previousObjectSet = false;

	/**
	 * The index of the element that would be returned by {@link #next}.
	 */
	private int nextIndex = 0;

	/**
	 * Constructs a new <code>FilterListIterator</code> that will not
	 * function until {@link #setPredicate(Predicate) setPredicate} are invoked.
	 */
	public FilterListIterator()
	{
		super();
	}

	/**
	 * Constructs a new <code>FilterListIterator</code> that will not
	 * function until {@link #setPredicate(Predicate) setPredicate} is invoked.
	 *
	 * @param iterator the iterator to use
	 */
	public FilterListIterator(ListIterator iterator)
	{
		super();
		this.iterator = iterator;
	}

	/**
	 * Constructs a new <code>FilterListIterator</code>.
	 *
	 * @param iterator  the iterator to use
	 * @param predicate the predicate to use
	 */
	public FilterListIterator(ListIterator iterator, Predicate predicate)
	{
		super();
		this.iterator = iterator;
		this.predicate = predicate;
	}

	/**
	 * Constructs a new <code>FilterListIterator</code> that will not
	 * function until
	 *
	 * @param predicate the predicate to use.
	 */
	public FilterListIterator(Predicate predicate)
	{
		super();
		this.predicate = predicate;
	}

	/**
	 * Not supported.
	 */
	public void add(Object o)
	{
		throw new UnsupportedOperationException("FilterListIterator.add(Object) is not supported.");
	}

	public boolean hasNext()
	{
		if (nextObjectSet)
		{
			return true;
		}
		else
		{
			return setNextObject();
		}
	}

	public boolean hasPrevious()
	{
		if (previousObjectSet)
		{
			return true;
		}
		else
		{
			return setPreviousObject();
		}
	}

	public Object next()
	{
		if (!nextObjectSet)
		{
			if (!setNextObject())
			{
				throw new NoSuchElementException();
			}
		}
		nextIndex++;
		Object temp = nextObject;
		clearNextObject();
		return temp;
	}

	public int nextIndex()
	{
		return nextIndex;
	}

	public Object previous()
	{
		if (!previousObjectSet)
		{
			if (!setPreviousObject())
			{
				throw new NoSuchElementException();
			}
		}
		nextIndex--;
		Object temp = previousObject;
		clearPreviousObject();
		return temp;
	}

	public int previousIndex()
	{
		return (nextIndex - 1);
	}

	/**
	 * Not supported.
	 */
	public void remove()
	{
		throw new UnsupportedOperationException("FilterListIterator.remove() is not supported.");
	}

	/**
	 * Not supported.
	 */
	public void set(Object o)
	{
		throw new UnsupportedOperationException("FilterListIterator.set(Object) is not supported.");
	}

	/**
	 * Gets the iterator this iterator is using.
	 *
	 * @return the iterator.
	 */
	public ListIterator getListIterator()
	{
		return iterator;
	}

	/**
	 * Sets the iterator for this iterator to use.
	 * If iteration has started, this effectively resets the iterator.
	 *
	 * @param iterator the iterator to use
	 */
	public void setListIterator(ListIterator iterator)
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

	private void clearNextObject()
	{
		nextObject = null;
		nextObjectSet = false;
	}

	private boolean setNextObject()
	{
		// if previousObjectSet,
		// then we've walked back one step in the
		// underlying list (due to a hasPrevious() call)
		// so skip ahead one matching object
		if (previousObjectSet)
		{
			clearPreviousObject();
			if (!setNextObject())
			{
				return false;
			}
			else
			{
				clearNextObject();
			}
		}

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

	private void clearPreviousObject()
	{
		previousObject = null;
		previousObjectSet = false;
	}

	private boolean setPreviousObject()
	{
		// if nextObjectSet,
		// then we've walked back one step in the
		// underlying list (due to a hasNext() call)
		// so skip ahead one matching object
		if (nextObjectSet)
		{
			clearNextObject();
			if (!setPreviousObject())
			{
				return false;
			}
			else
			{
				clearPreviousObject();
			}
		}

		while (iterator.hasPrevious())
		{
			Object object = iterator.previous();
			if (predicate.evaluate(object))
			{
				previousObject = object;
				previousObjectSet = true;
				return true;
			}
		}
		return false;
	}

}
