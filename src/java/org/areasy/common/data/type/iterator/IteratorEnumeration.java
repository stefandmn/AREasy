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

import java.util.Enumeration;
import java.util.Iterator;

/**
 * Adapter to make an {@link Iterator Iterator} instance appear to be
 * an {@link Enumeration Enumeration} instance.
 *
 * @version $Id: IteratorEnumeration.java,v 1.2 2008/05/14 09:32:40 swd\stefan.damian Exp $
 */
public class IteratorEnumeration implements Enumeration
{

	/**
	 * The iterator being decorated.
	 */
	private Iterator iterator;

	/**
	 * Constructs a new <code>IteratorEnumeration</code> that will not
	 * function until {@link #setIterator(Iterator) setIterator} is
	 * invoked.
	 */
	public IteratorEnumeration()
	{
		super();
	}

	/**
	 * Constructs a new <code>IteratorEnumeration</code> that will use
	 * the given iterator.
	 *
	 * @param iterator the iterator to use
	 */
	public IteratorEnumeration(Iterator iterator)
	{
		super();
		this.iterator = iterator;
	}

	// Iterator interface

	/**
	 * Returns true if the underlying iterator has more elements.
	 *
	 * @return true if the underlying iterator has more elements
	 */
	public boolean hasMoreElements()
	{
		return iterator.hasNext();
	}

	/**
	 * Returns the next element from the underlying iterator.
	 *
	 * @return the next element from the underlying iterator.
	 * @throws java.util.NoSuchElementException
	 *          if the underlying iterator has no
	 *          more elements
	 */
	public Object nextElement()
	{
		return iterator.next();
	}

	// Properties

	/**
	 * Returns the underlying iterator.
	 *
	 * @return the underlying iterator
	 */
	public Iterator getIterator()
	{
		return iterator;
	}

	/**
	 * Sets the underlying iterator.
	 *
	 * @param iterator the new underlying iterator
	 */
	public void setIterator(Iterator iterator)
	{
		this.iterator = iterator;
	}

}
