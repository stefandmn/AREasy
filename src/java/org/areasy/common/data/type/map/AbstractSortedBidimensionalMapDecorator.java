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

import org.areasy.common.data.type.SortedBidimensionalMap;

import java.util.Comparator;
import java.util.SortedMap;

/**
 * Provides a base decorator that enables additional functionality to be added
 * to a SortedBidiMap via decoration.
 * <p/>
 * Methods are forwarded directly to the decorated map.
 * <p/>
 * This implementation does not perform any special processing with the map views.
 * Instead it simply returns the inverse from the wrapped map. This may be
 * undesirable, for example if you are trying to write a validating implementation
 * it would provide a loophole around the validation.
 * But, you might want that loophole, so this class is kept simple.
 *
 * @version $Id: AbstractSortedBidimensionalMapDecorator.java,v 1.2 2008/05/14 09:32:31 swd\stefan.damian Exp $
 */
public abstract class AbstractSortedBidimensionalMapDecorator extends AbstractOrderedBidimensionalMapDecorator implements SortedBidimensionalMap
{

	/**
	 * Constructor that wraps (not copies).
	 *
	 * @param map the map to decorate, must not be null
	 * @throws IllegalArgumentException if the collection is null
	 */
	public AbstractSortedBidimensionalMapDecorator(SortedBidimensionalMap map)
	{
		super(map);
	}

	/**
	 * Gets the map being decorated.
	 *
	 * @return the decorated map
	 */
	protected SortedBidimensionalMap getSortedBidiMap()
	{
		return (SortedBidimensionalMap) map;
	}

	public SortedBidimensionalMap inverseSortedBidiMap()
	{
		return getSortedBidiMap().inverseSortedBidiMap();
	}

	public Comparator comparator()
	{
		return getSortedBidiMap().comparator();
	}

	public SortedMap subMap(Object fromKey, Object toKey)
	{
		return getSortedBidiMap().subMap(fromKey, toKey);
	}

	public SortedMap headMap(Object toKey)
	{
		return getSortedBidiMap().headMap(toKey);
	}

	public SortedMap tailMap(Object fromKey)
	{
		return getSortedBidiMap().tailMap(fromKey);
	}

}
