package org.areasy.common.data.type.map.keyvalue;

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

import java.io.Serializable;
import java.util.Arrays;

/**
 * A <code>MultiKey</code> allows multiple map keys to be merged together.
 * <p/>
 * The purpose of this class is to avoid the need to write code to handle
 * maps of maps. An example might be the need to lookup a filename by
 * key and locale. The typical solution might be nested maps. This class
 * can be used instead by creating an instance passing in the key and locale.
 * <p/>
 * Example usage:
 * <pre>
 * // populate map with data mapping key+locale to localizedText
 * Map map = new HashMap();
 * MultiKey multiKey = new MultiKey(key, locale);
 * map.put(multiKey, localizedText);
 * <p/>
 * // later retireve the localized text
 * MultiKey multiKey = new MultiKey(key, locale);
 * String localizedText = (String) map.get(multiKey);
 * </pre>
 *
 * @version $Id: MultiKey.java,v 1.2 2008/05/14 09:32:40 swd\stefan.damian Exp $
 */
public class MultiKey implements Serializable
{
	/**
	 * The individual keys
	 */
	private final Object[] keys;
	
	/**
	 * The cached hashCode
	 */
	private final int hashCode;

	/**
	 * Constructor taking two keys.
	 * <p/>
	 * The keys should be immutable
	 * If they are not then they must not be changed after adding to the MultiKey.
	 *
	 * @param key1 the first key
	 * @param key2 the second key
	 */
	public MultiKey(Object key1, Object key2)
	{
		this(new Object[]{key1, key2}, false);
	}

	/**
	 * Constructor taking three keys.
	 * <p/>
	 * The keys should be immutable
	 * If they are not then they must not be changed after adding to the MultiKey.
	 *
	 * @param key1 the first key
	 * @param key2 the second key
	 * @param key3 the third key
	 */
	public MultiKey(Object key1, Object key2, Object key3)
	{
		this(new Object[]{key1, key2, key3}, false);
	}

	/**
	 * Constructor taking four keys.
	 * <p/>
	 * The keys should be immutable
	 * If they are not then they must not be changed after adding to the MultiKey.
	 *
	 * @param key1 the first key
	 * @param key2 the second key
	 * @param key3 the third key
	 * @param key4 the fourth key
	 */
	public MultiKey(Object key1, Object key2, Object key3, Object key4)
	{
		this(new Object[]{key1, key2, key3, key4}, false);
	}

	/**
	 * Constructor taking five keys.
	 * <p/>
	 * The keys should be immutable
	 * If they are not then they must not be changed after adding to the MultiKey.
	 *
	 * @param key1 the first key
	 * @param key2 the second key
	 * @param key3 the third key
	 * @param key4 the fourth key
	 * @param key5 the fifth key
	 */
	public MultiKey(Object key1, Object key2, Object key3, Object key4, Object key5)
	{
		this(new Object[]{key1, key2, key3, key4, key5}, false);
	}

	/**
	 * Constructor taking an array of keys which is cloned.
	 * <p/>
	 * The keys should be immutable
	 * If they are not then they must not be changed after adding to the MultiKey.
	 * <p/>
	 * This is equivalent to <code>new MultiKey(keys, true)</code>.
	 *
	 * @param keys the array of keys, not null
	 * @throws IllegalArgumentException if the key array is null
	 */
	public MultiKey(Object[] keys)
	{
		this(keys, true);
	}

	/**
	 * Constructor taking an array of keys, optionally choosing whether to clone.
	 * <p/>
	 * <b>If the array is not cloned, then it must not be modified.</b>
	 * <p/>
	 * This method is public for performance reasons only, to avoid a clone.
	 * The hashcode is calculated once here in this method.
	 * Therefore, changing the array passed in would not change the hashcode but
	 * would change the equals method, which is a bug.
	 * <p/>
	 * This is the only fully safe usage of this constructor, as the object array
	 * is never made available in a variable:
	 * <pre>
	 * new MultiKey(new Object[] {...}, false);
	 * </pre>
	 * <p/>
	 * The keys should be immutable
	 * If they are not then they must not be changed after adding to the MultiKey.
	 *
	 * @param keys      the array of keys, not null
	 * @param makeClone true to clone the array, false to assign it
	 * @throws IllegalArgumentException if the key array is null
	 */
	public MultiKey(Object[] keys, boolean makeClone)
	{
		super();
		if (keys == null)
		{
			throw new IllegalArgumentException("The array of keys must not be null");
		}
		if (makeClone)
		{
			this.keys = (Object[]) keys.clone();
		}
		else
		{
			this.keys = keys;
		}

		int total = 0;
		for (int i = 0; i < keys.length; i++)
		{
			if (keys[i] != null)
			{
				total ^= keys[i].hashCode();
			}
		}
		hashCode = total;
	}

	/**
	 * Gets a clone of the array of keys.
	 * <p/>
	 * The keys should be immutable
	 * If they are not then they must not be changed.
	 *
	 * @return the individual keys
	 */
	public Object[] getKeys()
	{
		return (Object[]) keys.clone();
	}

	/**
	 * Gets the key at the specified index.
	 * <p/>
	 * The key should be immutable.
	 * If it is not then it must not be changed.
	 *
	 * @param index the index to retrieve
	 * @return the key at the index
	 * @throws IndexOutOfBoundsException if the index is invalid
	 */
	public Object getKey(int index)
	{
		return keys[index];
	}

	/**
	 * Gets the size of the list of keys.
	 *
	 * @return the size of the list of keys
	 */
	public int size()
	{
		return keys.length;
	}

	/**
	 * Compares this object to another.
	 * <p/>
	 * To be equal, the other object must be a <code>MultiKey</code> with the
	 * same number of keys which are also equal.
	 *
	 * @param other the other object to compare to
	 * @return true if equal
	 */
	public boolean equals(Object other)
	{
		if (other == this)
		{
			return true;
		}
		if (other instanceof MultiKey)
		{
			MultiKey otherMulti = (MultiKey) other;
			return Arrays.equals(keys, otherMulti.keys);
		}
		return false;
	}

	/**
	 * Gets the combined hash code that is computed from all the keys.
	 * <p/>
	 * This value is computed once and then cached, so elements should not
	 * change their hash codes once created (note that this is the same
	 * constraint that would be used if the individual keys elements were
	 * themselves {@link java.util.Map Map} keys.
	 *
	 * @return the hash code
	 */
	public int hashCode()
	{
		return hashCode;
	}

	/**
	 * Gets a debugging string version of the key.
	 *
	 * @return a debugging string
	 */
	public String toString()
	{
		return "MultiKey" + Arrays.asList(keys).toString();
	}

}
