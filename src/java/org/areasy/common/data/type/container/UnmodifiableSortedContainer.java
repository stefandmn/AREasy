package org.areasy.common.data.type.container;

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

import org.areasy.common.data.type.SortedContainer;
import org.areasy.common.data.type.Unmodifiable;
import org.areasy.common.data.type.iterator.UnmodifiableIterator;
import org.areasy.common.data.type.set.UnmodifiableSet;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Collection;
import java.util.Iterator;
import java.util.Set;

/**
 * Decorates another <code>SortedBag</code> to ensure it can't be altered.
 *
 * @version $Id: UnmodifiableSortedContainer.java,v 1.2 2008/05/14 09:32:35 swd\stefan.damian Exp $
 */
public final class UnmodifiableSortedContainer extends AbstractSortedContainerDecorator implements Unmodifiable, Serializable
{

	/**
	 * Factory method to create an unmodifiable bag.
	 * <p/>
	 * If the bag passed in is already unmodifiable, it is returned.
	 *
	 * @param bag the bag to decorate, must not be null
	 * @return an unmodifiable SortedBag
	 * @throws IllegalArgumentException if bag is null
	 */
	public static SortedContainer decorate(SortedContainer bag)
	{
		if (bag instanceof Unmodifiable)
		{
			return bag;
		}
		return new UnmodifiableSortedContainer(bag);
	}

	/**
	 * Constructor that wraps (not copies).
	 *
	 * @param bag the bag to decorate, must not be null
	 * @throws IllegalArgumentException if bag is null
	 */
	private UnmodifiableSortedContainer(SortedContainer bag)
	{
		super(bag);
	}

	/**
	 * Write the collection out using a custom routine.
	 *
	 * @param out the output stream
	 * @throws IOException
	 */
	private void writeObject(ObjectOutputStream out) throws IOException
	{
		out.defaultWriteObject();
		out.writeObject(collection);
	}

	/**
	 * Read the collection in using a custom routine.
	 *
	 * @param in the input stream
	 * @throws IOException
	 * @throws ClassNotFoundException
	 */
	private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException
	{
		in.defaultReadObject();
		collection = (Collection) in.readObject();
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

	public boolean add(Object object, int count)
	{
		throw new UnsupportedOperationException();
	}

	public boolean remove(Object object, int count)
	{
		throw new UnsupportedOperationException();
	}

	public Set uniqueSet()
	{
		Set set = getBag().uniqueSet();
		return UnmodifiableSet.decorate(set);
	}

}
