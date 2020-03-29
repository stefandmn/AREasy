package org.areasy.common.data.type.list;

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
import org.areasy.common.data.type.iterator.UnmodifiableListIterator;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

/**
 * Decorates another <code>List</code> to ensure it can't be altered.
 *
 * @version $Id: UnmodifiableList.java,v 1.2 2008/05/14 09:32:35 swd\stefan.damian Exp $
 */
public final class UnmodifiableList extends AbstractSerializableListDecorator implements Unmodifiable
{

	/**
	 * Factory method to create an unmodifiable list.
	 *
	 * @param list the list to decorate, must not be null
	 * @throws IllegalArgumentException if list is null
	 */
	public static List decorate(List list)
	{
		if (list instanceof Unmodifiable)
		{
			return list;
		}
		return new UnmodifiableList(list);
	}

	/**
	 * Constructor that wraps (not copies).
	 *
	 * @param list the list to decorate, must not be null
	 * @throws IllegalArgumentException if list is null
	 */
	private UnmodifiableList(List list)
	{
		super(list);
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

	public ListIterator listIterator()
	{
		return UnmodifiableListIterator.decorate(getList().listIterator());
	}

	public ListIterator listIterator(int index)
	{
		return UnmodifiableListIterator.decorate(getList().listIterator(index));
	}

	public void add(int index, Object object)
	{
		throw new UnsupportedOperationException();
	}

	public boolean addAll(int index, Collection coll)
	{
		throw new UnsupportedOperationException();
	}

	public Object remove(int index)
	{
		throw new UnsupportedOperationException();
	}

	public Object set(int index, Object object)
	{
		throw new UnsupportedOperationException();
	}

	public List subList(int fromIndex, int toIndex)
	{
		List sub = getList().subList(fromIndex, toIndex);
		return new UnmodifiableList(sub);
	}

}
