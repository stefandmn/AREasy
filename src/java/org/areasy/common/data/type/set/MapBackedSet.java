package org.areasy.common.data.type.set;

/*
 * Copyright (c) 2007-2015 AREasy Runtime
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

import java.io.Serializable;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

/**
 * Decorates a <code>Map</code> to obtain <code>Set</code> behaviour.
 * <p/>
 * This class is used to create a <code>Set</code> with the same properties as
 * the key set of any map. Thus, a ReferenceSet can be created by wrapping a
 * <code>ReferenceEntry</code> in an instance of this class.
 * <p/>
 * Most map implementation can be used to create a set by passing in dummy values.
 * Exceptions include <code>BidiMap</code> implementations, as they require unique values.
 *
 * @version $Id: MapBackedSet.java,v 1.2 2008/05/14 09:32:37 swd\stefan.damian Exp $
 */
public final class MapBackedSet implements Set, Serializable
{
	/**
	 * The map being used as the backing store
	 */
	protected final Map map;

	/**
	 * The dummyValue to use
	 */
	protected final Object dummyValue;

	/**
	 * Factory method to create a set from a map.
	 *
	 * @param map the map to decorate, must not be null
	 * @throws IllegalArgumentException if set is null
	 */
	public static Set decorate(Map map)
	{
		return decorate(map, null);
	}

	/**
	 * Factory method to create a set from a map.
	 *
	 * @param map        the map to decorate, must not be null
	 * @param dummyValue the dummy value to use
	 * @throws IllegalArgumentException if map is null
	 */
	public static Set decorate(Map map, Object dummyValue)
	{
		if (map == null) throw new IllegalArgumentException("The map must not be null");

		return new MapBackedSet(map, dummyValue);
	}

	/**
	 * Constructor that wraps (not copies).
	 *
	 * @param map        the map to decorate, must not be null
	 * @param dummyValue the dummy value to use
	 * @throws IllegalArgumentException if map is null
	 */
	private MapBackedSet(Map map, Object dummyValue)
	{
		super();

		this.map = map;
		this.dummyValue = dummyValue;
	}

	public int size()
	{
		return map.size();
	}

	public boolean isEmpty()
	{
		return map.isEmpty();
	}

	public Iterator iterator()
	{
		return map.keySet().iterator();
	}

	public boolean contains(Object obj)
	{
		return map.containsKey(obj);
	}

	public boolean containsAll(Collection coll)
	{
		return map.keySet().containsAll(coll);
	}

	public boolean add(Object obj)
	{
		int size = map.size();
		map.put(obj, dummyValue);
		return (map.size() != size);
	}

	public boolean addAll(Collection coll)
	{
		int size = map.size();
		for (Iterator it = coll.iterator(); it.hasNext();)
		{
			Object obj = (Object) it.next();
			map.put(obj, dummyValue);
		}

		return (map.size() != size);
	}

	public boolean remove(Object obj)
	{
		int size = map.size();
		map.remove(obj);

		return (map.size() != size);
	}

	public boolean removeAll(Collection coll)
	{
		return map.keySet().removeAll(coll);
	}

	public boolean retainAll(Collection coll)
	{
		return map.keySet().retainAll(coll);
	}

	public void clear()
	{
		map.clear();
	}

	public Object[] toArray()
	{
		return map.keySet().toArray();
	}

	public Object[] toArray(Object[] array)
	{
		return map.keySet().toArray(array);
	}

	public boolean equals(Object obj)
	{
		return map.keySet().equals(obj);
	}

	public int hashCode()
	{
		return map.keySet().hashCode();
	}

}
