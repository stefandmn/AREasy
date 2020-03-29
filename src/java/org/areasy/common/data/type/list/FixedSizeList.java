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

import org.areasy.common.data.type.BoundedCollection;
import org.areasy.common.data.type.iterator.AbstractListIteratorDecorator;
import org.areasy.common.data.type.iterator.UnmodifiableIterator;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

/**
 * Decorates another <code>List</code> to fix the size preventing add/remove.
 * <p/>
 * The add, remove, clear and retain operations are unsupported.
 * The set method is allowed (as it doesn't change the list size).
 * <p/>
 *
 * @version $Id: FixedSizeList.java,v 1.2 2008/05/14 09:32:35 swd\stefan.damian Exp $
 */
public class FixedSizeList extends AbstractSerializableListDecorator implements BoundedCollection
{
	/**
	 * Factory method to create a fixed size list.
	 *
	 * @param list the list to decorate, must not be null
	 * @throws IllegalArgumentException if list is null
	 */
	public static List decorate(List list)
	{
		return new FixedSizeList(list);
	}

	/**
	 * Constructor that wraps (not copies).
	 *
	 * @param list the list to decorate, must not be null
	 * @throws IllegalArgumentException if list is null
	 */
	protected FixedSizeList(List list)
	{
		super(list);
	}

	public boolean add(Object object)
	{
		throw new UnsupportedOperationException("List is fixed size");
	}

	public void add(int index, Object object)
	{
		throw new UnsupportedOperationException("List is fixed size");
	}

	public boolean addAll(Collection coll)
	{
		throw new UnsupportedOperationException("List is fixed size");
	}

	public boolean addAll(int index, Collection coll)
	{
		throw new UnsupportedOperationException("List is fixed size");
	}

	public void clear()
	{
		throw new UnsupportedOperationException("List is fixed size");
	}

	public Object get(int index)
	{
		return getList().get(index);
	}

	public int indexOf(Object object)
	{
		return getList().indexOf(object);
	}

	public Iterator iterator()
	{
		return UnmodifiableIterator.decorate(getCollection().iterator());
	}

	public int lastIndexOf(Object object)
	{
		return getList().lastIndexOf(object);
	}

	public ListIterator listIterator()
	{
		return new FixedSizeListIterator(getList().listIterator(0));
	}

	public ListIterator listIterator(int index)
	{
		return new FixedSizeListIterator(getList().listIterator(index));
	}

	public Object remove(int index)
	{
		throw new UnsupportedOperationException("List is fixed size");
	}

	public boolean remove(Object object)
	{
		throw new UnsupportedOperationException("List is fixed size");
	}

	public boolean removeAll(Collection coll)
	{
		throw new UnsupportedOperationException("List is fixed size");
	}

	public boolean retainAll(Collection coll)
	{
		throw new UnsupportedOperationException("List is fixed size");
	}

	public Object set(int index, Object object)
	{
		return getList().set(index, object);
	}

	public List subList(int fromIndex, int toIndex)
	{
		List sub = getList().subList(fromIndex, toIndex);
		return new FixedSizeList(sub);
	}

	/**
	 * List iterator that only permits changes via set()
	 */
	static class FixedSizeListIterator extends AbstractListIteratorDecorator
	{
		protected FixedSizeListIterator(ListIterator iterator)
		{
			super(iterator);
		}

		public void remove()
		{
			throw new UnsupportedOperationException("List is fixed size");
		}

		public void add(Object object)
		{
			throw new UnsupportedOperationException("List is fixed size");
		}
	}

	public boolean isFull()
	{
		return true;
	}

	public int maxSize()
	{
		return size();
	}

}
