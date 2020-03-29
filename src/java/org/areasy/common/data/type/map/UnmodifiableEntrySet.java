package org.areasy.common.data.type.map;

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
import org.areasy.common.data.type.iterator.AbstractIteratorDecorator;
import org.areasy.common.data.type.map.keyvalue.AbstractMapEntryDecorator;
import org.areasy.common.data.type.set.AbstractSetDecorator;

import java.lang.reflect.Array;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

/**
 * Decorates a map entry <code>Set</code> to ensure it can't be altered.
 *
 * @version $Id: UnmodifiableEntrySet.java,v 1.2 2008/05/14 09:32:30 swd\stefan.damian Exp $
 */
public final class UnmodifiableEntrySet
		extends AbstractSetDecorator implements Unmodifiable
{

	/**
	 * Factory method to create an unmodifiable set of Map Entry objects.
	 *
	 * @param set the set to decorate, must not be null
	 * @throws IllegalArgumentException if set is null
	 */
	public static Set decorate(Set set)
	{
		if (set instanceof Unmodifiable)
		{
			return set;
		}
		return new UnmodifiableEntrySet(set);
	}

	/**
	 * Constructor that wraps (not copies).
	 *
	 * @param set the set to decorate, must not be null
	 * @throws IllegalArgumentException if set is null
	 */
	private UnmodifiableEntrySet(Set set)
	{
		super(set);
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

	public Iterator iterator()
	{
		return new UnmodifiableEntrySetIterator(collection.iterator());
	}

	public Object[] toArray()
	{
		Object[] array = collection.toArray();
		for (int i = 0; i < array.length; i++)
		{
			array[i] = new UnmodifiableEntry((Map.Entry) array[i]);
		}
		return array;
	}

	public Object[] toArray(Object array[])
	{
		Object[] result = array;
		if (array.length > 0)
		{
			// we must create a new array to handle multi-threaded situations
			// where another thread could access data before we decorate it
			result = (Object[]) Array.newInstance(array.getClass().getComponentType(), 0);
		}
		result = collection.toArray(result);
		for (int i = 0; i < result.length; i++)
		{
			result[i] = new UnmodifiableEntry((Map.Entry) result[i]);
		}

		// check to see if result should be returned straight
		if (result.length > array.length)
		{
			return result;
		}

		// copy back into input array to fulfil the method contract
		System.arraycopy(result, 0, array, 0, result.length);
		if (array.length > result.length)
		{
			array[result.length] = null;
		}
		return array;
	}

	/**
	 * Implementation of an entry set iterator.
	 */
	final static class UnmodifiableEntrySetIterator extends AbstractIteratorDecorator
	{

		protected UnmodifiableEntrySetIterator(Iterator iterator)
		{
			super(iterator);
		}

		public Object next()
		{
			Map.Entry entry = (Map.Entry) iterator.next();
			return new UnmodifiableEntry(entry);
		}

		public void remove()
		{
			throw new UnsupportedOperationException();
		}
	}

	/**
	 * Implementation of a map entry that is unmodifiable.
	 */
	final static class UnmodifiableEntry extends AbstractMapEntryDecorator
	{

		protected UnmodifiableEntry(Map.Entry entry)
		{
			super(entry);
		}

		public Object setValue(Object obj)
		{
			throw new UnsupportedOperationException();
		}
	}

}
