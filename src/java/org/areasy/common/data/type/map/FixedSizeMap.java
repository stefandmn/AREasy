package org.areasy.common.data.type.map;

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

import org.areasy.common.data.type.BoundedMap;
import org.areasy.common.data.type.collection.UnmodifiableCollection;
import org.areasy.common.data.type.set.UnmodifiableSet;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

/**
 * Decorates another <code>Map</code> to fix the size, preventing add/remove.
 * <p/>
 * Any action that would change the size of the map is disallowed.
 * The put method is allowed to change the value associated with an existing
 * key however.
 * <p/>
 * If trying to remove or clear the map, an UnsupportedOperationException is
 * thrown. If trying to put a new mapping into the map, an
 * IllegalArgumentException is thrown. This is because the put method can
 * succeed if the mapping's key already exists in the map, so the put method
 * is not always unsupported.
 *
 * @version $Id: FixedSizeMap.java,v 1.3 2008/05/20 06:52:55 swd\stefan.damian Exp $
 */
public class FixedSizeMap extends AbstractMapDecorator implements Map, BoundedMap, Serializable
{
	/**
	 * Factory method to create a fixed size map.
	 *
	 * @param map the map to decorate, must not be null
	 * @throws IllegalArgumentException if map is null
	 */
	public static Map decorate(Map map)
	{
		return new FixedSizeMap(map);
	}

	/**
	 * Constructor that wraps (not copies).
	 *
	 * @param map the map to decorate, must not be null
	 * @throws IllegalArgumentException if map is null
	 */
	protected FixedSizeMap(Map map)
	{
		super(map);
	}

	/**
	 * Write the map out using a custom routine.
	 *
	 * @param out the output stream
	 * @throws IOException
	 */
	private void writeObject(ObjectOutputStream out) throws IOException
	{
		out.defaultWriteObject();
		out.writeObject(map);
	}

	/**
	 * Read the map in using a custom routine.
	 *
	 * @param in the input stream
	 * @throws IOException
	 * @throws ClassNotFoundException
	 */
	private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException
	{
		in.defaultReadObject();
		map = (Map) in.readObject();
	}

	public Object put(Object key, Object value)
	{
		if (!map.containsKey(key)) throw new IllegalArgumentException("Cannot put new key/value pair - Map is fixed size");

		return map.put(key, value);
	}

	public void putAll(Map mapToCopy)
	{
		for (Iterator it = mapToCopy.keySet().iterator(); it.hasNext();)
		{
			if (!mapToCopy.containsKey(it.next())) throw new IllegalArgumentException("Cannot put new key/value pair - Map is fixed size");
		}

		map.putAll(mapToCopy);
	}

	public void clear()
	{
		throw new UnsupportedOperationException("Map is fixed size");
	}

	public Object remove(Object key)
	{
		throw new UnsupportedOperationException("Map is fixed size");
	}

	public Set entrySet()
	{
		Set set = map.entrySet();

		// unmodifiable set will still allow modification via Map.Entry objects
		return UnmodifiableSet.decorate(set);
	}

	public Set keySet()
	{
		Set set = map.keySet();
		return UnmodifiableSet.decorate(set);
	}

	public Collection values()
	{
		Collection coll = map.values();
		return UnmodifiableCollection.decorate(coll);
	}

	public boolean isFull()
	{
		return true;
	}

	public int maxSize()
	{
		return size();
	}

}
