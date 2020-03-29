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

import org.areasy.common.data.type.Unmodifiable;

import java.util.ListIterator;

/**
 * Decorates a list iterator such that it cannot be modified.
 *
 * @version $Id: UnmodifiableListIterator.java,v 1.2 2008/05/14 09:32:39 swd\stefan.damian Exp $
 */
public final class UnmodifiableListIterator implements ListIterator, Unmodifiable
{

	/**
	 * The iterator being decorated
	 */
	private ListIterator iterator;

	/**
	 * Decorates the specified iterator such that it cannot be modified.
	 *
	 * @param iterator the iterator to decorate
	 * @throws IllegalArgumentException if the iterator is null
	 */
	public static ListIterator decorate(ListIterator iterator)
	{
		if (iterator == null)
		{
			throw new IllegalArgumentException("ListIterator must not be null");
		}
		if (iterator instanceof Unmodifiable)
		{
			return iterator;
		}
		return new UnmodifiableListIterator(iterator);
	}

	/**
	 * Constructor.
	 *
	 * @param iterator the iterator to decorate
	 */
	private UnmodifiableListIterator(ListIterator iterator)
	{
		super();
		this.iterator = iterator;
	}

	public boolean hasNext()
	{
		return iterator.hasNext();
	}

	public Object next()
	{
		return iterator.next();
	}

	public int nextIndex()
	{
		return iterator.nextIndex();
	}

	public boolean hasPrevious()
	{
		return iterator.hasPrevious();
	}

	public Object previous()
	{
		return iterator.previous();
	}

	public int previousIndex()
	{
		return iterator.previousIndex();
	}

	public void remove()
	{
		throw new UnsupportedOperationException("remove() is not supported");
	}

	public void set(Object obj)
	{
		throw new UnsupportedOperationException("set() is not supported");
	}

	public void add(Object obj)
	{
		throw new UnsupportedOperationException("add() is not supported");
	}

}
