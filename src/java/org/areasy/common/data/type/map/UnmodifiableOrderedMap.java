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

import org.areasy.common.data.type.MapIterator;
import org.areasy.common.data.type.OrderedMap;
import org.areasy.common.data.type.OrderedMapIterator;
import org.areasy.common.data.type.Unmodifiable;
import org.areasy.common.data.type.collection.UnmodifiableCollection;
import org.areasy.common.data.type.iterator.UnmodifiableMapIterator;
import org.areasy.common.data.type.iterator.UnmodifiableOrderedMapIterator;
import org.areasy.common.data.type.set.UnmodifiableSet;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Collection;
import java.util.Map;
import java.util.Set;

/**
 * Decorates another <code>OrderedMap</code> to ensure it can't be altered.
 *
 * @version $Id: UnmodifiableOrderedMap.java,v 1.2 2008/05/14 09:32:30 swd\stefan.damian Exp $
 */
public final class UnmodifiableOrderedMap extends AbstractOrderedMapDecorator implements Unmodifiable, Serializable
{
	/**
	 * Factory method to create an unmodifiable sorted map.
	 *
	 * @param map the map to decorate, must not be null
	 * @throws IllegalArgumentException if map is null
	 */
	public static OrderedMap decorate(OrderedMap map)
	{
		if (map instanceof Unmodifiable) return map;

		return new UnmodifiableOrderedMap(map);
	}

	/**
	 * Constructor that wraps (not copies).
	 *
	 * @param map the map to decorate, must not be null
	 * @throws IllegalArgumentException if map is null
	 */
	private UnmodifiableOrderedMap(OrderedMap map)
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

	public MapIterator mapIterator()
	{
		MapIterator it = getOrderedMap().mapIterator();
		return UnmodifiableMapIterator.decorate(it);
	}

	public OrderedMapIterator orderedMapIterator()
	{
		OrderedMapIterator it = getOrderedMap().orderedMapIterator();
		return UnmodifiableOrderedMapIterator.decorate(it);
	}

	public void clear()
	{
		throw new UnsupportedOperationException();
	}

	public Object put(Object key, Object value)
	{
		throw new UnsupportedOperationException();
	}

	public void putAll(Map mapToCopy)
	{
		throw new UnsupportedOperationException();
	}

	public Object remove(Object key)
	{
		throw new UnsupportedOperationException();
	}

	public Set entrySet()
	{
		Set set = super.entrySet();
		return UnmodifiableEntrySet.decorate(set);
	}

	public Set keySet()
	{
		Set set = super.keySet();
		return UnmodifiableSet.decorate(set);
	}

	public Collection values()
	{
		Collection coll = super.values();
		return UnmodifiableCollection.decorate(coll);
	}

}
