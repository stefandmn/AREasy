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

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Map;

/**
 * A case-insensitive <code>Map</code>.
 * <p/>
 * As entries are added to the map, keys are converted to all lowercase. A new
 * key is compared to existing keys by comparing <code>newKey.toString().toLower()</code>
 * to the lowercase values in the current <code>KeySet.</code>
 * <p/>
 * Null keys are supported.
 * <p/>
 * The <code>keySet()</code> method returns all lowercase keys, or nulls.
 * <p/>
 * Example:
 * <pre><code>
 *  Map map = new CaseInsensitiveMap();
 *  map.put("One", "One");
 *  map.put("Two", "Two");
 *  map.put(null, "Three");
 *  map.put("one", "Four");
 * </code></pre>
 * creates a <code>CaseInsensitiveMap</code> with three entries.<br>
 * <code>map.get(null)</code> returns <code>"Three"</code> and <code>map.get("ONE")</code>
 * returns <code>"Four".</code>  The <code>Set</code> returned by <code>keySet()</code>
 * equals <code>{"one", "two", null}.</code>
 *
 * @version $Id: CaseInsensitiveMap.java,v 1.2 2008/05/14 09:32:31 swd\stefan.damian Exp $
 */
public class CaseInsensitiveMap extends AbstractHashedMap implements Serializable, Cloneable
{
	/**
	 * Constructs a new empty map with default size and load factor.
	 */
	public CaseInsensitiveMap()
	{
		super(DEFAULT_CAPACITY, DEFAULT_LOAD_FACTOR, DEFAULT_THRESHOLD);
	}

	/**
	 * Constructs a new, empty map with the specified initial capacity.
	 *
	 * @param initialCapacity the initial capacity
	 * @throws IllegalArgumentException if the initial capacity is less than one
	 */
	public CaseInsensitiveMap(int initialCapacity)
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
	public CaseInsensitiveMap(int initialCapacity, float loadFactor)
	{
		super(initialCapacity, loadFactor);
	}

	/**
	 * Constructor copying elements from another map.
	 * <p/>
	 * Keys will be converted to lower case strings, which may cause
	 * some entries to be removed (if string representation of keys differ
	 * only by character case).
	 *
	 * @param map the map to copy
	 * @throws NullPointerException if the map is null
	 */
	public CaseInsensitiveMap(Map map)
	{
		super(map);
	}

	/**
	 * Overrides convertKey() from {@link AbstractHashedMap} to convert keys to
	 * lower case.
	 * <p/>
	 * Returns null if key is null.
	 *
	 * @param key the key convert
	 * @return the converted key
	 */
	protected Object convertKey(Object key)
	{
		if (key != null)
		{
			return key.toString().toLowerCase();
		}
		else
		{
			return AbstractHashedMap.NULL;
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
