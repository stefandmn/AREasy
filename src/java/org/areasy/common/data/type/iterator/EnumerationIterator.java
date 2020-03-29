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

import java.util.Collection;
import java.util.Enumeration;
import java.util.Iterator;

/**
 * Adapter to make {@link Enumeration Enumeration} instances appear
 * to be {@link Iterator Iterator} instances.
 *
 * @version $Id: EnumerationIterator.java,v 1.2 2008/05/14 09:32:40 swd\stefan.damian Exp $
 */
public class EnumerationIterator implements Iterator
{

	/**
	 * The collection to remove elements from
	 */
	private Collection collection;
	/**
	 * The enumeration being converted
	 */
	private Enumeration enumeration;
	/**
	 * The last object retrieved
	 */
	private Object last;

	// Constructors
	/**
	 * Constructs a new <code>EnumerationIterator</code> that will not
	 * function until {@link #setEnumeration(Enumeration)} is called.
	 */
	public EnumerationIterator()
	{
		this(null, null);
	}

	/**
	 * Constructs a new <code>EnumerationIterator</code> that provides
	 * an iterator view of the given enumeration.
	 *
	 * @param enumeration the enumeration to use
	 */
	public EnumerationIterator(final Enumeration enumeration)
	{
		this(enumeration, null);
	}

	/**
	 * Constructs a new <code>EnumerationIterator</code> that will remove
	 * elements from the specified collection.
	 *
	 * @param enumeration the enumeration to use
	 * @param collection  the collection to remove elements form
	 */
	public EnumerationIterator(final Enumeration enumeration, final Collection collection)
	{
		super();
		this.enumeration = enumeration;
		this.collection = collection;
		this.last = null;
	}

	// Iterator interface
	/**
	 * Returns true if the underlying enumeration has more elements.
	 *
	 * @return true if the underlying enumeration has more elements
	 * @throws NullPointerException if the underlying enumeration is null
	 */
	public boolean hasNext()
	{
		return enumeration.hasMoreElements();
	}

	/**
	 * Returns the next object from the enumeration.
	 *
	 * @return the next object from the enumeration
	 * @throws NullPointerException if the enumeration is null
	 */
	public Object next()
	{
		last = enumeration.nextElement();
		return last;
	}

	/**
	 * Removes the last retrieved element if a collection is attached.
	 * <p/>
	 * Functions if an associated <code>Collection</code> is known.
	 * If so, the first occurrence of the last returned object from this
	 * iterator will be removed from the collection.
	 *
	 * @throws IllegalStateException         <code>next()</code> not called.
	 * @throws UnsupportedOperationException if no associated collection
	 */
	public void remove()
	{
		if (collection != null)
		{
			if (last != null)
			{
				collection.remove(last);
			}
			else
			{
				throw new IllegalStateException("next() must have been called for remove() to function");
			}
		}
		else
		{
			throw new UnsupportedOperationException("No Collection associated with this Iterator");
		}
	}

	// Properties
	/**
	 * Returns the underlying enumeration.
	 *
	 * @return the underlying enumeration
	 */
	public Enumeration getEnumeration()
	{
		return enumeration;
	}

	/**
	 * Sets the underlying enumeration.
	 *
	 * @param enumeration the new underlying enumeration
	 */
	public void setEnumeration(final Enumeration enumeration)
	{
		this.enumeration = enumeration;
	}

}
