package org.areasy.common.data.type.collection;

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

import org.areasy.common.data.type.BoundedCollection;
import org.areasy.common.data.type.iterator.UnmodifiableIterator;

import java.util.Collection;
import java.util.Iterator;

/**
 * <code>UnmodifiableBoundedCollection</code> decorates another
 * <code>BoundedCollection</code> to ensure it can't be altered.
 * <p/>
 * If a BoundedCollection is first wrapped in some other collection decorator,
 * such as synchronized or predicated, the BoundedCollection methods are no
 * longer accessible.
 * The factory on this class will attempt to retrieve the bounded nature by
 * examining the package scope variables.
 *
 * @version $Id: UnmodifiableBoundedCollection.java,v 1.2 2008/05/14 09:32:34 swd\stefan.damian Exp $
 */
public final class UnmodifiableBoundedCollection extends AbstractSerializableCollectionDecorator implements BoundedCollection
{
	/**
	 * Factory method to create an unmodifiable bounded collection.
	 *
	 * @param coll the <code>BoundedCollection</code> to decorate, must not be null
	 * @return a new unmodifiable bounded collection
	 * @throws IllegalArgumentException if bag is null
	 */
	public static BoundedCollection decorate(BoundedCollection coll)
	{
		return new UnmodifiableBoundedCollection(coll);
	}

	/**
	 * Factory method to create an unmodifiable bounded collection.
	 * <p/>
	 * This method is capable of drilling down through up to 1000 other decorators
	 * to find a suitable BoundedCollection.
	 *
	 * @param coll the <code>BoundedCollection</code> to decorate, must not be null
	 * @return a new unmodifiable bounded collection
	 * @throws IllegalArgumentException if bag is null
	 */
	public static BoundedCollection decorateUsing(Collection coll)
	{
		if (coll == null)
		{
			throw new IllegalArgumentException("The collection must not be null");
		}

		// handle decorators
		for (int i = 0; i < 1000; i++)
		{  // counter to prevent infinite looping
			if (coll instanceof BoundedCollection)
			{
				break;  // normal loop exit
			}
			else if (coll instanceof AbstractCollectionDecorator)
			{
				coll = ((AbstractCollectionDecorator) coll).collection;
			}
			else if (coll instanceof SynchronizedCollection)
			{
				coll = ((SynchronizedCollection) coll).collection;
			}
			else
			{
				break;  // normal loop exit
			}
		}

		if (coll instanceof BoundedCollection == false)
		{
			throw new IllegalArgumentException("The collection is not a bounded collection");
		}
		return new UnmodifiableBoundedCollection((BoundedCollection) coll);
	}

	/**
	 * Constructor that wraps (not copies).
	 *
	 * @param coll the collection to decorate, must not be null
	 * @throws IllegalArgumentException if coll is null
	 */
	private UnmodifiableBoundedCollection(BoundedCollection coll)
	{
		super(coll);
	}

	public Iterator iterator()
	{
		return UnmodifiableIterator.decorate(getCollection().iterator());
	}

	public boolean add(Object object)
	{
		throw new UnsupportedOperationException();
	}

	public boolean addAll(Collection coll)
	{
		throw new UnsupportedOperationException();
	}

	public void clear()
	{
		throw new UnsupportedOperationException();
	}

	public boolean remove(Object object)
	{
		throw new UnsupportedOperationException();
	}

	public boolean removeAll(Collection coll)
	{
		throw new UnsupportedOperationException();
	}

	public boolean retainAll(Collection coll)
	{
		throw new UnsupportedOperationException();
	}

	public boolean isFull()
	{
		return ((BoundedCollection) collection).isFull();
	}

	public int maxSize()
	{
		return ((BoundedCollection) collection).maxSize();
	}

}
