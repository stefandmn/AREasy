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

import org.areasy.common.data.type.Predicate;

import java.util.Comparator;
import java.util.SortedMap;

/**
 * Decorates another <code>SortedMap </code> to validate that additions
 * match a specified predicate.
 * <p/>
 * This map exists to provide validation for the decorated map.
 * It is normally created to decorate an empty map.
 * If an object cannot be added to the map, an IllegalArgumentException is thrown.
 * <p/>
 * One usage would be to ensure that no null keys are added to the map.
 * <pre>SortedMap map = PredicatedSortedSet.decorate(new TreeMap(), NotNullPredicate.INSTANCE, null);</pre>
 *
 * @version $Id: PredicatedSortedMap.java,v 1.2 2008/05/14 09:32:30 swd\stefan.damian Exp $
 */
public class PredicatedSortedMap extends PredicatedMap implements SortedMap
{
	/**
	 * Factory method to create a predicated (validating) sorted map.
	 * <p/>
	 * If there are any elements already in the list being decorated, they
	 * are validated.
	 *
	 * @param map            the map to decorate, must not be null
	 * @param keyPredicate   the predicate to validate the keys, null means no check
	 * @param valuePredicate the predicate to validate to values, null means no check
	 * @throws IllegalArgumentException if the map is null
	 */
	public static SortedMap decorate(SortedMap map, Predicate keyPredicate, Predicate valuePredicate)
	{
		return new PredicatedSortedMap(map, keyPredicate, valuePredicate);
	}

	/**
	 * Constructor that wraps (not copies).
	 *
	 * @param map            the map to decorate, must not be null
	 * @param keyPredicate   the predicate to validate the keys, null means no check
	 * @param valuePredicate the predicate to validate to values, null means no check
	 * @throws IllegalArgumentException if the map is null
	 */
	protected PredicatedSortedMap(SortedMap map, Predicate keyPredicate, Predicate valuePredicate)
	{
		super(map, keyPredicate, valuePredicate);
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
		return new PredicatedSortedMap(map, keyPredicate, valuePredicate);
	}

	public SortedMap headMap(Object toKey)
	{
		SortedMap map = getSortedMap().headMap(toKey);
		return new PredicatedSortedMap(map, keyPredicate, valuePredicate);
	}

	public SortedMap tailMap(Object fromKey)
	{
		SortedMap map = getSortedMap().tailMap(fromKey);
		return new PredicatedSortedMap(map, keyPredicate, valuePredicate);
	}

}
