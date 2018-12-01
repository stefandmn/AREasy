package org.areasy.common.data.type.map;

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

import org.areasy.common.data.type.MapIterator;
import org.areasy.common.data.type.OrderedMap;
import org.areasy.common.data.type.OrderedMapIterator;

/**
 * Provides a base decorator that enables additional functionality to be added
 * to an OrderedMap via decoration.
 * <p/>
 * Methods are forwarded directly to the decorated map.
 * <p/>
 * This implementation does not perform any special processing with the map views.
 * Instead it simply returns the set/collection from the wrapped map. This may be
 * undesirable, for example if you are trying to write a validating implementation
 * it would provide a loophole around the validation.
 * But, you might want that loophole, so this class is kept simple.
 *
 * @version $Id: AbstractOrderedMapDecorator.java,v 1.2 2008/05/14 09:32:30 swd\stefan.damian Exp $
 */
public abstract class AbstractOrderedMapDecorator
		extends AbstractMapDecorator implements OrderedMap
{

	/**
	 * Constructor only used in deserialization, do not use otherwise.
	 *
	 */
	protected AbstractOrderedMapDecorator()
	{
		super();
	}

	/**
	 * Constructor that wraps (not copies).
	 *
	 * @param map the map to decorate, must not be null
	 * @throws IllegalArgumentException if the collection is null
	 */
	public AbstractOrderedMapDecorator(OrderedMap map)
	{
		super(map);
	}

	/**
	 * Gets the map being decorated.
	 *
	 * @return the decorated map
	 */
	protected OrderedMap getOrderedMap()
	{
		return (OrderedMap) map;
	}

	public Object firstKey()
	{
		return getOrderedMap().firstKey();
	}

	public Object lastKey()
	{
		return getOrderedMap().lastKey();
	}

	public Object nextKey(Object key)
	{
		return getOrderedMap().nextKey(key);
	}

	public Object previousKey(Object key)
	{
		return getOrderedMap().previousKey(key);
	}

	public MapIterator mapIterator()
	{
		return getOrderedMap().mapIterator();
	}

	public OrderedMapIterator orderedMapIterator()
	{
		return getOrderedMap().orderedMapIterator();
	}

}
