package org.areasy.common.data.type.collection;

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

import java.util.Collection;
import java.util.Iterator;

/**
 * Decorates another <code>Collection</code> to provide additional behaviour.
 * <p/>
 * Each method call made on this <code>Collection</code> is forwarded to the
 * decorated <code>Collection</code>. This class is used as a framework on which
 * to build to extensions such as synchronized and unmodifiable behaviour. The
 * main advantage of decoration is that one decorator can wrap any implementation
 * of <code>Collection</code>, whereas sub-classing requires a new class to be
 * written for each implementation.
 * <p/>
 * This implementation does not perform any special processing with
 * {@link #iterator()}. Instead it simply returns the value from the
 * wrapped collection. This may be undesirable, for example if you are trying
 * to write an unmodifiable implementation it might provide a loophole.
 *
 * @version $Id: AbstractCollectionDecorator.java,v 1.2 2008/05/14 09:32:34 swd\stefan.damian Exp $
 */
public abstract class AbstractCollectionDecorator implements Collection
{

	/**
	 * The collection being decorated
	 */
	protected Collection collection;

	/**
	 * Constructor only used in deserialization, do not use otherwise.
	 *
	 */
	protected AbstractCollectionDecorator()
	{
		super();
	}

	/**
	 * Constructor that wraps (not copies).
	 *
	 * @param coll the collection to decorate, must not be null
	 * @throws IllegalArgumentException if the collection is null
	 */
	protected AbstractCollectionDecorator(Collection coll)
	{
		if (coll == null)
		{
			throw new IllegalArgumentException("Collection must not be null");
		}
		this.collection = coll;
	}

	/**
	 * Gets the collection being decorated.
	 *
	 * @return the decorated collection
	 */
	protected Collection getCollection()
	{
		return collection;
	}

	public boolean add(Object object)
	{
		return collection.add(object);
	}

	public boolean addAll(Collection coll)
	{
		return collection.addAll(coll);
	}

	public void clear()
	{
		collection.clear();
	}

	public boolean contains(Object object)
	{
		return collection.contains(object);
	}

	public boolean isEmpty()
	{
		return collection.isEmpty();
	}

	public Iterator iterator()
	{
		return collection.iterator();
	}

	public boolean remove(Object object)
	{
		return collection.remove(object);
	}

	public int size()
	{
		return collection.size();
	}

	public Object[] toArray()
	{
		return collection.toArray();
	}

	public Object[] toArray(Object[] object)
	{
		return collection.toArray(object);
	}

	public boolean containsAll(Collection coll)
	{
		return collection.containsAll(coll);
	}

	public boolean removeAll(Collection coll)
	{
		return collection.removeAll(coll);
	}

	public boolean retainAll(Collection coll)
	{
		return collection.retainAll(coll);
	}

	public boolean equals(Object object)
	{
		if (object == this)
		{
			return true;
		}
		return collection.equals(object);
	}

	public int hashCode()
	{
		return collection.hashCode();
	}

	public String toString()
	{
		return collection.toString();
	}

}
