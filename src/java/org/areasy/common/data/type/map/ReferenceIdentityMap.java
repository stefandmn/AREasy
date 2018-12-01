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
import java.lang.ref.Reference;

/**
 * A <code>Map</code> implementation that allows mappings to be
 * removed by the garbage collector and matches keys and values based
 * on <code>==</code> not <code>equals()</code>.
 * <p/>
 * <p/>
 * When you construct a <code>ReferenceIdentityMap</code>, you can specify what kind
 * of references are used to store the map's keys and values.
 * If non-hard references are used, then the garbage collector can remove
 * mappings if a key or value becomes unreachable, or if the JVM's memory is
 * running low. For information on how the different reference types behave,
 * see {@link Reference}.
 * <p/>
 * Different types of references can be specified for keys and values.
 * The default constructor uses hard keys and soft values, providing a
 * memory-sensitive cache.
 * <p/>
 * This map will violate the detail of various Map and map view contracts.
 * As a general rule, don't compare this map to other maps.
 * <p/>
 * This {@link Map} implementation does <i>not</i> allow null elements.
 * Attempting to add a null key or value to the map will raise a <code>NullPointerException</code>.
 * <p/>
 * This implementation is not synchronized.
 * You can use {@link java.util.Collections#synchronizedMap} to
 * provide synchronized access to a <code>ReferenceIdentityMap</code>.
 * Remember that synchronization will not stop the garbage collecter removing entries.
 * <p/>
 * All the available iterators can be reset back to the start by casting to
 * <code>ResettableIterator</code> and calling <code>reset()</code>.
 *
 * @version $Id: ReferenceIdentityMap.java,v 1.2 2008/05/14 09:32:30 swd\stefan.damian Exp $
 * @see java.lang.ref.Reference
 */
public class ReferenceIdentityMap extends AbstractReferenceMap implements Serializable
{
	/**
	 * Constructs a new <code>ReferenceIdentityMap</code> that will
	 * use hard references to keys and soft references to values.
	 */
	public ReferenceIdentityMap()
	{
		super(HARD, SOFT, DEFAULT_CAPACITY, DEFAULT_LOAD_FACTOR, false);
	}

	/**
	 * Constructs a new <code>ReferenceIdentityMap</code> that will
	 * use the specified types of references.
	 *
	 * @param keyType   the type of reference to use for keys;
	 *                  must be {@link #HARD}, {@link #SOFT}, {@link #WEAK}
	 * @param valueType the type of reference to use for values;
	 *                  must be {@link #HARD}, {@link #SOFT}, {@link #WEAK}
	 */
	public ReferenceIdentityMap(int keyType, int valueType)
	{
		super(keyType, valueType, DEFAULT_CAPACITY, DEFAULT_LOAD_FACTOR, false);
	}

	/**
	 * Constructs a new <code>ReferenceIdentityMap</code> that will
	 * use the specified types of references.
	 *
	 * @param keyType     the type of reference to use for keys;
	 *                    must be {@link #HARD}, {@link #SOFT}, {@link #WEAK}
	 * @param valueType   the type of reference to use for values;
	 *                    must be {@link #HARD}, {@link #SOFT}, {@link #WEAK}
	 * @param purgeValues should the value be automatically purged when the
	 *                    key is garbage collected
	 */
	public ReferenceIdentityMap(int keyType, int valueType, boolean purgeValues)
	{
		super(keyType, valueType, DEFAULT_CAPACITY, DEFAULT_LOAD_FACTOR, purgeValues);
	}

	/**
	 * Constructs a new <code>ReferenceIdentityMap</code> with the
	 * specified reference types, load factor and initial capacity.
	 *
	 * @param keyType    the type of reference to use for keys;
	 *                   must be {@link #HARD}, {@link #SOFT}, {@link #WEAK}
	 * @param valueType  the type of reference to use for values;
	 *                   must be {@link #HARD}, {@link #SOFT}, {@link #WEAK}
	 * @param capacity   the initial capacity for the map
	 * @param loadFactor the load factor for the map
	 */
	public ReferenceIdentityMap(int keyType, int valueType, int capacity, float loadFactor)
	{
		super(keyType, valueType, capacity, loadFactor, false);
	}

	/**
	 * Constructs a new <code>ReferenceIdentityMap</code> with the
	 * specified reference types, load factor and initial capacity.
	 *
	 * @param keyType     the type of reference to use for keys;
	 *                    must be {@link #HARD}, {@link #SOFT}, {@link #WEAK}
	 * @param valueType   the type of reference to use for values;
	 *                    must be {@link #HARD}, {@link #SOFT}, {@link #WEAK}
	 * @param capacity    the initial capacity for the map
	 * @param loadFactor  the load factor for the map
	 * @param purgeValues should the value be automatically purged when the
	 *                    key is garbage collected
	 */
	public ReferenceIdentityMap(int keyType, int valueType, int capacity,
								float loadFactor, boolean purgeValues)
	{
		super(keyType, valueType, capacity, loadFactor, purgeValues);
	}

	/**
	 * Gets the hash code for the key specified.
	 * <p/>
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
	 * Gets the hash code for a MapEntry.
	 * <p/>
	 * This implementation uses the identity hash code.
	 *
	 * @param key   the key to get a hash code for, may be null
	 * @param value the value to get a hash code for, may be null
	 * @return the hash code, as per the MapEntry specification
	 */
	protected int hashEntry(Object key, Object value)
	{
		return System.identityHashCode(key) ^
				System.identityHashCode(value);
	}

	/**
	 * Compares two keys for equals.
	 * <p/>
	 * This implementation converts the key from the entry to a real reference
	 * before comparison and uses <code>==</code>.
	 *
	 * @param key1 the first key to compare passed in from outside
	 * @param key2 the second key extracted from the entry via <code>entry.key</code>
	 * @return true if equal by identity
	 */
	protected boolean isEqualKey(Object key1, Object key2)
	{
		key2 = (keyType > HARD ? ((Reference) key2).get() : key2);
		return (key1 == key2);
	}

	/**
	 * Compares two values for equals.
	 * <p/>
	 * This implementation uses <code>==</code>.
	 *
	 * @param value1 the first value to compare passed in from outside
	 * @param value2 the second value extracted from the entry via <code>getValue()</code>
	 * @return true if equal by identity
	 */
	protected boolean isEqualValue(Object value1, Object value2)
	{
		return (value1 == value2);
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
