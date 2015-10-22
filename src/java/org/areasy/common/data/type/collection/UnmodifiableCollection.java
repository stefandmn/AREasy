package org.areasy.common.data.type.collection;

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

import org.areasy.common.data.type.Unmodifiable;
import org.areasy.common.data.type.iterator.UnmodifiableIterator;

import java.util.Collection;
import java.util.Iterator;

/**
 * Decorates another <code>Collection</code> to ensure it can't be altered.
 *
 * @version $Id: UnmodifiableCollection.java,v 1.2 2008/05/14 09:32:34 swd\stefan.damian Exp $
 */
public final class UnmodifiableCollection extends AbstractSerializableCollectionDecorator implements Unmodifiable
{

	/**
	 * Factory method to create an unmodifiable collection.
	 * <p/>
	 * If the collection passed in is already unmodifiable, it is returned.
	 *
	 * @param coll the collection to decorate, must not be null
	 * @return an unmodifiable collection
	 * @throws IllegalArgumentException if collection is null
	 */
	public static Collection decorate(Collection coll)
	{
		if (coll instanceof Unmodifiable)
		{
			return coll;
		}
		return new UnmodifiableCollection(coll);
	}

	/**
	 * Constructor that wraps (not copies).
	 *
	 * @param coll the collection to decorate, must not be null
	 * @throws IllegalArgumentException if collection is null
	 */
	private UnmodifiableCollection(Collection coll)
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

}
