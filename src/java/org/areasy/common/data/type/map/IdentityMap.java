package org.areasy.common.data.type.map;

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

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Map;

/**
 * A <code>Map</code> implementation that matches keys and values based
 * on <code>==</code> not <code>equals()</code>.
 * <p/>
 * This map will violate the detail of various Map and map view contracts.
 * As a general rule, don't compare this map to other maps.
 *
 * @version $Id: IdentityMap.java,v 1.3 2008/05/20 06:52:55 swd\stefan.damian Exp $
 */
public class IdentityMap extends AbstractHashedMap implements Serializable, Cloneable
{
	/**
	 * Constructs a new empty map with default size and load factor.
	 */
	public IdentityMap()
	{
		super(DEFAULT_CAPACITY, DEFAULT_LOAD_FACTOR, DEFAULT_THRESHOLD);
	}

	/**
	 * Constructs a new, empty map with the specified initial capacity.
	 *
	 * @param initialCapacity the initial capacity
	 * @throws IllegalArgumentException if the initial capacity is less than one
	 */
	public IdentityMap(int initialCapacity)
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
	public IdentityMap(int initialCapacity, float loadFactor)
	{
		super(initialCapacity, loadFactor);
	}

	/**
	 * Constructor copying elements from another map.
	 *
	 * @param map the map to copy
	 * @throws NullPointerException if the map is null
	 */
	public IdentityMap(Map map)
	{
		super(map);
	}

	/**
	 * Gets the hash code for the key specified.
	 * This implementation uses the identity hash code.
	 *
	 * @param key the key to get a hash code for
	 * @return the hash code
	 */
	protected int hash(Object key)
	{
		return System.identityHashCode(key);
	}

	/**
	 * Compares two keys for equals.
	 * This implementation uses <code>==</code>.
	 *
	 * @param key1 the first key to compare
	 * @param key2 the second key to compare
	 * @return true if equal by identity
	 */
	protected boolean isEqualKey(Object key1, Object key2)
	{
		return (key1 == key2);
	}

	/**
	 * Compares two values for equals.
	 * This implementation uses <code>==</code>.
	 *
	 * @param value1 the first value to compare
	 * @param value2 the second value to compare
	 * @return true if equal by identity
	 */
	protected boolean isEqualValue(Object value1, Object value2)
	{
		return (value1 == value2);
	}

	/**
	 * Creates an entry to store the data.
	 * This implementation creates an IdentityEntry instance.
	 *
	 * @param next     the next entry in sequence
	 * @param hashCode the hash code to use
	 * @param key      the key to store
	 * @param value    the value to store
	 * @return the newly created entry
	 */
	protected HashEntry createEntry(HashEntry next, int hashCode, Object key, Object value)
	{
		return new IdentityEntry(next, hashCode, key, value);
	}

	/**
	 * HashEntry
	 */
	protected static class IdentityEntry extends HashEntry
	{
		protected IdentityEntry(HashEntry next, int hashCode, Object key, Object value)
		{
			super(next, hashCode, key, value);
		}

		public boolean equals(Object obj)
		{
			if (obj == this) return true;
			if (!(obj instanceof Map.Entry)) return false;

			Map.Entry other = (Map.Entry) obj;
			return (getKey() == other.getKey()) && (getValue() == other.getValue());
		}

		public int hashCode()
		{
			return System.identityHashCode(getKey()) ^
					System.identityHashCode(getValue());
		}
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
