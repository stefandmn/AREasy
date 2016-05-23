package org.areasy.common.data.type.iterator;

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

import org.areasy.common.data.type.MapIterator;

/**
 * Provides basic behaviour for decorating a map iterator with extra functionality.
 * <p/>
 * All methods are forwarded to the decorated map iterator.
 *
 * @version $Id: AbstractMapIteratorDecorator.java,v 1.2 2008/05/14 09:32:40 swd\stefan.damian Exp $
 */
public class AbstractMapIteratorDecorator implements MapIterator
{

	/**
	 * The iterator being decorated
	 */
	protected final MapIterator iterator;

	/**
	 * Constructor that decorates the specified iterator.
	 *
	 * @param iterator the iterator to decorate, must not be null
	 * @throws IllegalArgumentException if the collection is null
	 */
	public AbstractMapIteratorDecorator(MapIterator iterator)
	{
		super();
		if (iterator == null)
		{
			throw new IllegalArgumentException("MapIterator must not be null");
		}
		this.iterator = iterator;
	}

	/**
	 * Gets the iterator being decorated.
	 *
	 * @return the decorated iterator
	 */
	protected MapIterator getMapIterator()
	{
		return iterator;
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
		iterator.remove();
	}

	public Object getKey()
	{
		return iterator.getKey();
	}

	public Object getValue()
	{
		return iterator.getValue();
	}

	public Object setValue(Object obj)
	{
		return iterator.setValue(obj);
	}

}
