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

import java.util.NoSuchElementException;

/**
 * Provides an implementation of an empty iterator.
 *
 * @version $Id: AbstractEmptyIterator.java,v 1.2 2008/05/14 09:32:40 swd\stefan.damian Exp $
 */
abstract class AbstractEmptyIterator
{

	/**
	 * Constructor.
	 */
	protected AbstractEmptyIterator()
	{
		super();
	}

	public boolean hasNext()
	{
		return false;
	}

	public Object next()
	{
		throw new NoSuchElementException("Iterator contains no elements");
	}

	public boolean hasPrevious()
	{
		return false;
	}

	public Object previous()
	{
		throw new NoSuchElementException("Iterator contains no elements");
	}

	public int nextIndex()
	{
		return 0;
	}

	public int previousIndex()
	{
		return -1;
	}

	public void add(Object obj)
	{
		throw new UnsupportedOperationException("add() not supported for empty Iterator");
	}

	public void set(Object obj)
	{
		throw new IllegalStateException("Iterator contains no elements");
	}

	public void remove()
	{
		throw new IllegalStateException("Iterator contains no elements");
	}

	public Object getKey()
	{
		throw new IllegalStateException("Iterator contains no elements");
	}

	public Object getValue()
	{
		throw new IllegalStateException("Iterator contains no elements");
	}

	public Object setValue(Object value)
	{
		throw new IllegalStateException("Iterator contains no elements");
	}

	public void reset()
	{
		// do nothing
	}

}
