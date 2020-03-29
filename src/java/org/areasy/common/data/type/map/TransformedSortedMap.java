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

import org.areasy.common.data.type.Transformer;

import java.util.Comparator;
import java.util.SortedMap;

/**
 * Decorates another <code>SortedMap </code> to transform objects that are added.
 * <p/>
 * The Map put methods and Map.Entry setValue method are affected by this class.
 * Thus objects must be removed or searched for using their transformed form.
 * For example, if the transformation converts Strings to Integers, you must
 * use the Integer form to remove objects.
 *
 * @version $Id: TransformedSortedMap.java,v 1.2 2008/05/14 09:32:30 swd\stefan.damian Exp $
 */
public class TransformedSortedMap extends TransformedMap implements SortedMap
{
	/**
	 * Factory method to create a transforming sorted map.
	 * <p/>
	 * If there are any elements already in the map being decorated, they
	 * are NOT transformed.
	 *
	 * @param map              the map to decorate, must not be null
	 * @param keyTransformer   the predicate to validate the keys, null means no transformation
	 * @param valueTransformer the predicate to validate to values, null means no transformation
	 * @throws IllegalArgumentException if the map is null
	 */
	public static SortedMap decorate(SortedMap map, Transformer keyTransformer, Transformer valueTransformer)
	{
		return new TransformedSortedMap(map, keyTransformer, valueTransformer);
	}

	/**
	 * Constructor that wraps (not copies).
	 * <p/>
	 * If there are any elements already in the collection being decorated, they
	 * are NOT transformed.</p>
	 *
	 * @param map              the map to decorate, must not be null
	 * @param keyTransformer   the predicate to validate the keys, null means no transformation
	 * @param valueTransformer the predicate to validate to values, null means no transformation
	 * @throws IllegalArgumentException if the map is null
	 */
	protected TransformedSortedMap(SortedMap map, Transformer keyTransformer, Transformer valueTransformer)
	{
		super(map, keyTransformer, valueTransformer);
	}

	/**
	 * Gets the map being decorated.
	 *
	 * @return the decorated map
	 */
	protected SortedMap getSortedMap()
	{
		return (SortedMap) map;
	}

	public Object firstKey()
	{
		return getSortedMap().firstKey();
	}

	public Object lastKey()
	{
		return getSortedMap().lastKey();
	}

	public Comparator comparator()
	{
		return getSortedMap().comparator();
	}

	public SortedMap subMap(Object fromKey, Object toKey)
	{
		SortedMap map = getSortedMap().subMap(fromKey, toKey);
		return new TransformedSortedMap(map, keyTransformer, valueTransformer);
	}

	public SortedMap headMap(Object toKey)
	{
		SortedMap map = getSortedMap().headMap(toKey);
		return new TransformedSortedMap(map, keyTransformer, valueTransformer);
	}

	public SortedMap tailMap(Object fromKey)
	{
		SortedMap map = getSortedMap().tailMap(fromKey);
		return new TransformedSortedMap(map, keyTransformer, valueTransformer);
	}

}
