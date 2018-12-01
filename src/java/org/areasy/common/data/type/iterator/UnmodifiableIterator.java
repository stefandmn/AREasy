package org.areasy.common.data.type.iterator;

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

import org.areasy.common.data.type.Unmodifiable;

import java.util.Iterator;

/**
 * Decorates an iterator such that it cannot be modified.
 *
 * @version $Id: UnmodifiableIterator.java,v 1.2 2008/05/14 09:32:40 swd\stefan.damian Exp $
 */
public final class UnmodifiableIterator implements Iterator, Unmodifiable
{

	/**
	 * The iterator being decorated
	 */
	private Iterator iterator;

	/**
	 * Decorates the specified iterator such that it cannot be modified.
	 * <p/>
	 * If the iterator is already unmodifiable it is returned directly.
	 *
	 * @param iterator the iterator to decorate
	 * @throws IllegalArgumentException if the iterator is null
	 */
	public static Iterator decorate(Iterator iterator)
	{
		if (iterator == null)
		{
			throw new IllegalArgumentException("Iterator must not be null");
		}
		if (iterator instanceof Unmodifiable)
		{
			return iterator;
		}
		return new UnmodifiableIterator(iterator);
	}

	/**
	 * Constructor.
	 *
	 * @param iterator the iterator to decorate
	 */
	private UnmodifiableIterator(Iterator iterator)
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

	public void remove()
	{
		throw new UnsupportedOperationException("remove() is not supported");
	}

}
