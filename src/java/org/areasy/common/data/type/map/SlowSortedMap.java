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

import org.areasy.common.data.type.Factory;
import org.areasy.common.data.type.Transformer;

import java.util.Comparator;
import java.util.SortedMap;

/**
 * Decorates another <code>SortedMap</code> to create objects in the map on demand.
 * <p/>
 * When the {@link #get(Object)} method is called with a key that does not
 * exist in the map, the factory is used to create the object. The created
 * object will be added to the map using the requested key.
 * <p/>
 * For instance:
 * <pre>
 * Factory factory = new Factory() {
 *     public Object create() {
 *         return new Date();
 *     }
 * }
 * SortedMap lazy = Lazy.sortedMap(new HashMap(), factory);
 * Object obj = lazy.get("NOW");
 * </pre>
 * <p/>
 * After the above code is executed, <code>obj</code> will contain
 * a new <code>Date</code> instance. Furthermore, that <code>Date</code>
 * instance is mapped to the "NOW" key in the map.
 *
 * @version $Id: SlowSortedMap.java,v 1.2 2008/05/14 09:32:30 swd\stefan.damian Exp $
 */
public class SlowSortedMap extends SlowMap implements SortedMap
{

	/**
	 * Factory method to create a lazily instantiated sorted map.
	 *
	 * @param map     the map to decorate, must not be null
	 * @param factory the factory to use, must not be null
	 * @throws IllegalArgumentException if map or factory is null
	 */
	public static SortedMap decorate(SortedMap map, Factory factory)
	{
		return new SlowSortedMap(map, factory);
	}

	/**
	 * Factory method to create a lazily instantiated sorted map.
	 *
	 * @param map     the map to decorate, must not be null
	 * @param factory the factory to use, must not be null
	 * @throws IllegalArgumentException if map or factory is null
	 */
	public static SortedMap decorate(SortedMap map, Transformer factory)
	{
		return new SlowSortedMap(map, factory);
	}

	/**
	 * Constructor that wraps (not copies).
	 *
	 * @param map     the map to decorate, must not be null
	 * @param factory the factory to use, must not be null
	 * @throws IllegalArgumentException if map or factory is null
	 */
	protected SlowSortedMap(SortedMap map, Factory factory)
	{
		super(map, factory);
	}

	/**
	 * Constructor that wraps (not copies).
	 *
	 * @param map     the map to decorate, must not be null
	 * @param factory the factory to use, must not be null
	 * @throws IllegalArgumentException if map or factory is null
	 */
	protected SlowSortedMap(SortedMap map, Transformer factory)
	{
		super(map, factory);
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
		return new SlowSortedMap(map, factory);
	}

	public SortedMap headMap(Object toKey)
	{
		SortedMap map = getSortedMap().headMap(toKey);
		return new SlowSortedMap(map, factory);
	}

	public SortedMap tailMap(Object fromKey)
	{
		SortedMap map = getSortedMap().tailMap(fromKey);
		return new SlowSortedMap(map, factory);
	}

}
