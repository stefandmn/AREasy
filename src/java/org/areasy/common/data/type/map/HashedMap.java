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

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Map;

/**
 * A <code>Map</code> implementation that is a general purpose alternative
 * to <code>HashMap</code>.
 * <p/>
 * This implementation improves on the JDK1.4 HashMap by adding the
 * {@link org.areasy.common.data.type.MapIterator MapIterator}
 * functionality and many methods for subclassing.
 * <p/>
 *
 * @version $Id: HashedMap.java,v 1.2 2008/05/14 09:32:30 swd\stefan.damian Exp $
 */
public class HashedMap extends AbstractHashedMap implements Serializable, Cloneable
{
	/**
	 * Constructs a new empty map with default size and load factor.
	 */
	public HashedMap()
	{
		super(DEFAULT_CAPACITY, DEFAULT_LOAD_FACTOR, DEFAULT_THRESHOLD);
	}

	/**
	 * Constructs a new, empty map with the specified initial capacity.
	 *
	 * @param initialCapacity the initial capacity
	 * @throws IllegalArgumentException if the initial capacity is less than one
	 */
	public HashedMap(int initialCapacity)
	{
		super(initialCapacity);
	}

	/**
	 * Constructs a new, empty map with the specified initial capacity and
	 * load factor.
	 *
	 * @param initialCapacity the initial capacity
	 * @param loadFactor      the load factor
	 * @throws IllegalArgumentException if the initial capacity is less than one
	 * @throws IllegalArgumentException if the load factor is less than zero
	 */
	public HashedMap(int initialCapacity, float loadFactor)
	{
		super(initialCapacity, loadFactor);
	}

	/**
	 * Constructor copying elements from another map.
	 *
	 * @param map the map to copy
	 * @throws NullPointerException if the map is null
	 */
	public HashedMap(Map map)
	{
		super(map);
	}

	/**
	 * Clones the map without cloning the keys or values.
	 *
	 * @return a shallow clone
	 */
	public Object clone()
	{
		return super.clone();
	}

	/**
	 * Write the map out using a custom routine.
	 */
	private void writeObject(ObjectOutputStream out) throws IOException
	{
		out.defaultWriteObject();
		doWriteObject(out);
	}

	/**
	 * Read the map in using a custom routine.
	 */
	private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException
	{
		in.defaultReadObject();
		doReadObject(in);
	}

}
