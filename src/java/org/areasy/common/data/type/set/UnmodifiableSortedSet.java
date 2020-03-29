package org.areasy.common.data.type.set;

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

import org.areasy.common.data.type.Unmodifiable;
import org.areasy.common.data.type.iterator.UnmodifiableIterator;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Collection;
import java.util.Iterator;
import java.util.SortedSet;

/**
 * Decorates another <code>SortedSet</code> to ensure it can't be altered.
 *
 * @version $Id: UnmodifiableSortedSet.java,v 1.2 2008/05/14 09:32:37 swd\stefan.damian Exp $
 */
public final class UnmodifiableSortedSet extends AbstractSortedSetDecorator implements Unmodifiable, Serializable
{

	/**
	 * Factory method to create an unmodifiable set.
	 *
	 * @param set the set to decorate, must not be null
	 * @throws IllegalArgumentException if set is null
	 */
	public static SortedSet decorate(SortedSet set)
	{
		if (set instanceof Unmodifiable) return set;

		return new UnmodifiableSortedSet(set);
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

	/**
	 * Constructor that wraps (not copies).
	 *
	 * @param set the set to decorate, must not be null
	 * @throws IllegalArgumentException if set is null
	 */
	private UnmodifiableSortedSet(SortedSet set)
	{
		super(set);
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

	public SortedSet subSet(Object fromElement, Object toElement)
	{
		SortedSet sub = getSortedSet().subSet(fromElement, toElement);
		return new UnmodifiableSortedSet(sub);
	}

	public SortedSet headSet(Object toElement)
	{
		SortedSet sub = getSortedSet().headSet(toElement);
		return new UnmodifiableSortedSet(sub);
	}

	public SortedSet tailSet(Object fromElement)
	{
		SortedSet sub = getSortedSet().tailSet(fromElement);
		return new UnmodifiableSortedSet(sub);
	}

}
