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

import org.areasy.common.data.type.IterableMap;
import org.areasy.common.data.type.MapIterator;
import org.areasy.common.data.type.map.keyvalue.MultiKey;

import java.io.Serializable;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

/**
 * A <code>Map</code> implementation that uses multiple keys to map the value.
 * <p/>
 * This class is the most efficient way to uses multiple keys to map to a value.
 * The best way to use this class is via the additional map-style methods.
 * These provide <code>get</code>, <code>containsKey</code>, <code>put</code> and
 * <code>remove</code> for individual keys which operate without extra object creation.
 * <p/>
 * The additional methods are the main interface of this map.
 * As such, you will not normally hold this map in a variable of type <code>Map</code>.
 * <p/>
 * The normal map methods take in and return a {@link MultiKey}.
 * If you try to use <code>put()</code> with any other object type a
 * <code>ClassCastException</code> is thrown. If you try to use <code>null</code> as
 * the key in <code>put()</code> a <code>NullPointerException</code> is thrown.
 * <p/>
 * This map is implemented as a decorator of a <code>AbstractHashedMap</code> which
 * enables extra behaviour to be added easily.
 * <ul>
 * <li><code>MultiKeyMap.decorate(new LinkedMap())</code> creates an ordered map.
 * <li><code>MultiKeyMap.decorate(new LRUMap())</code> creates an least recently used map.
 * </ul>
 * Note that <code>IdentityMap</code> and <code>ReferenceIdentityMap</code> are unsuitable
 * for use as the key comparison would work on the whole MultiKey, not the elements within.
 * <p/>
 * As an example, consider a least recently used cache that uses a String airline code
 * and a Locale to lookup the airline's name:
 * <pre>
 * private MultiKeyMap cache = MultiKeyMap.decorate(new LRUMap(50));
 * <p/>
 * public String getAirlineName(String code, String locale) {
 *   String name = (String) cache.get(code, locale);
 *   if (name == null) {
 *     name = getAirlineNameFromDB(code, locale);
 *     cache.put(code, locale, name);
 *   }
 *   return name;
 * }
 * </pre>
 *
 * @version $Id: MultiKeyMap.java,v 1.3 2008/05/14 09:32:31 swd\stefan.damian Exp $
 */
public class MultiKeyMap implements IterableMap, Serializable
{
	/**
	 * The decorated map
	 */
	protected final AbstractHashedMap map;

	/**
	 * Decorates the specified map to add the MultiKeyMap API and fast query.
	 * The map must not be null and must be empty.
	 *
	 * @param map the map to decorate, not null
	 * @throws IllegalArgumentException if the map is null or not empty
	 */
	public static MultiKeyMap decorate(AbstractHashedMap map)
	{
		if (map == null) throw new IllegalArgumentException("Map must not be null");

		if (map.size() > 0) throw new IllegalArgumentException("Map must be empty");

		return new MultiKeyMap(map);
	}

	/**
	 * Constructs a new MultiKeyMap that decorates a <code>HashedMap</code>.
	 */
	public MultiKeyMap()
	{
		super();

		map = new HashedMap();
	}

	/**
	 * Constructor that decorates the specified map and is called from
	 * {@link #decorate(AbstractHashedMap)}.
	 * The map must not be null and should be empty or only contain valid keys.
	 * This constructor performs no validation.
	 *
	 * @param map the map to decorate
	 */
	protected MultiKeyMap(AbstractHashedMap map)
	{
		super();
		this.map = map;
	}

	/**
	 * Gets the value mapped to the specified multi-key.
	 *
	 * @param key1 the first key
	 * @param key2 the second key
	 * @return the mapped value, null if no match
	 */
	public Object get(Object key1, Object key2)
	{
		int hashCode = hash(key1, key2);
		AbstractHashedMap.HashEntry entry = map.data[map.hashIndex(hashCode, map.data.length)];

		while (entry != null)
		{
			if (entry.hashCode == hashCode && isEqualKey(entry, key1, key2))
			{
				return entry.getValue();
			}

			entry = entry.next;
		}

		return null;
	}

	/**
	 * Checks whether the map contains the specified multi-key.
	 *
	 * @param key1 the first key
	 * @param key2 the second key
	 * @return true if the map contains the key
	 */
	public boolean containsKey(Object key1, Object key2)
	{
		int hashCode = hash(key1, key2);
		AbstractHashedMap.HashEntry entry = map.data[map.hashIndex(hashCode, map.data.length)];

		while (entry != null)
		{
			if (entry.hashCode == hashCode && isEqualKey(entry, key1, key2)) return true;
			entry = entry.next;
		}

		return false;
	}

	/**
	 * Stores the value against the specified multi-key.
	 *
	 * @param key1  the first key
	 * @param key2  the second key
	 * @param value the value to store
	 * @return the value previously mapped to this combined key, null if none
	 */
	public Object put(Object key1, Object key2, Object value)
	{
		int hashCode = hash(key1, key2);
		int index = map.hashIndex(hashCode, map.data.length);

		AbstractHashedMap.HashEntry entry = map.data[index];

		while (entry != null)
		{
			if (entry.hashCode == hashCode && isEqualKey(entry, key1, key2))
			{
				Object oldValue = entry.getValue();
				map.updateEntry(entry, value);
				return oldValue;
			}
			entry = entry.next;
		}

		map.addMapping(index, hashCode, new MultiKey(key1, key2), value);
		return null;
	}

	/**
	 * Removes the specified multi-key from this map.
	 *
	 * @param key1 the first key
	 * @param key2 the second key
	 * @return the value mapped to the removed key, null if key not in map
	 */
	public Object remove(Object key1, Object key2)
	{
		int hashCode = hash(key1, key2);
		int index = map.hashIndex(hashCode, map.data.length);

		AbstractHashedMap.HashEntry entry = map.data[index];
		AbstractHashedMap.HashEntry previous = null;

		while (entry != null)
		{
			if (entry.hashCode == hashCode && isEqualKey(entry, key1, key2))
			{
				Object oldValue = entry.getValue();
				map.removeMapping(entry, index, previous);
				return oldValue;
			}

			previous = entry;
			entry = entry.next;
		}

		return null;
	}

	/**
	 * Gets the hash code for the specified multi-key.
	 *
	 * @param key1 the first key
	 * @param key2 the second key
	 * @return the hash code
	 */
	protected int hash(Object key1, Object key2)
	{
		int h = 0;

		if (key1 != null) h ^= key1.hashCode();
		if (key2 != null) h ^= key2.hashCode();

		h += ~(h << 9);
		h ^= (h >>> 14);
		h += (h << 4);
		h ^= (h >>> 10);

		return h;
	}

	/**
	 * Is the key equal to the combined key.
	 *
	 * @param entry the entry to compare to
	 * @param key1  the first key
	 * @param key2  the second key
	 * @return true if the key matches
	 */
	protected boolean isEqualKey(AbstractHashedMap.HashEntry entry, Object key1, Object key2)
	{
		MultiKey multi = (MultiKey) entry.getKey();

		return multi.size() == 2 &&
				(key1 == null ? multi.getKey(0) == null : key1.equals(multi.getKey(0))) &&
				(key2 == null ? multi.getKey(1) == null : key2.equals(multi.getKey(1)));
	}

	/**
	 * Gets the value mapped to the specified multi-key.
	 *
	 * @param key1 the first key
	 * @param key2 the second key
	 * @param key3 the third key
	 * @return the mapped value, null if no match
	 */
	public Object get(Object key1, Object key2, Object key3)
	{
		int hashCode = hash(key1, key2, key3);
		AbstractHashedMap.HashEntry entry = map.data[map.hashIndex(hashCode, map.data.length)];

		while (entry != null)
		{
			if (entry.hashCode == hashCode && isEqualKey(entry, key1, key2, key3)) return entry.getValue();
			entry = entry.next;
		}

		return null;
	}

	/**
	 * Checks whether the map contains the specified multi-key.
	 *
	 * @param key1 the first key
	 * @param key2 the second key
	 * @param key3 the third key
	 * @return true if the map contains the key
	 */
	public boolean containsKey(Object key1, Object key2, Object key3)
	{
		int hashCode = hash(key1, key2, key3);
		AbstractHashedMap.HashEntry entry = map.data[map.hashIndex(hashCode, map.data.length)];

		while (entry != null)
		{
			if (entry.hashCode == hashCode && isEqualKey(entry, key1, key2, key3)) return true;
			entry = entry.next;
		}

		return false;
	}

	/**
	 * Stores the value against the specified multi-key.
	 *
	 * @param key1  the first key
	 * @param key2  the second key
	 * @param key3  the third key
	 * @param value the value to store
	 * @return the value previously mapped to this combined key, null if none
	 */
	public Object put(Object key1, Object key2, Object key3, Object value)
	{
		int hashCode = hash(key1, key2, key3);
		int index = map.hashIndex(hashCode, map.data.length);

		AbstractHashedMap.HashEntry entry = map.data[index];

		while (entry != null)
		{
			if (entry.hashCode == hashCode && isEqualKey(entry, key1, key2, key3))
			{
				Object oldValue = entry.getValue();
				map.updateEntry(entry, value);

				return oldValue;
			}

			entry = entry.next;
		}

		map.addMapping(index, hashCode, new MultiKey(key1, key2, key3), value);

		return null;
	}

	/**
	 * Removes the specified multi-key from this map.
	 *
	 * @param key1 the first key
	 * @param key2 the second key
	 * @param key3 the third key
	 * @return the value mapped to the removed key, null if key not in map
	 */
	public Object remove(Object key1, Object key2, Object key3)
	{
		int hashCode = hash(key1, key2, key3);
		int index = map.hashIndex(hashCode, map.data.length);

		AbstractHashedMap.HashEntry entry = map.data[index];
		AbstractHashedMap.HashEntry previous = null;

		while (entry != null)
		{
			if (entry.hashCode == hashCode && isEqualKey(entry, key1, key2, key3))
			{
				Object oldValue = entry.getValue();
				map.removeMapping(entry, index, previous);

				return oldValue;
			}

			previous = entry;
			entry = entry.next;
		}

		return null;
	}

	/**
	 * Gets the hash code for the specified multi-key.
	 *
	 * @param key1 the first key
	 * @param key2 the second key
	 * @param key3 the third key
	 * @return the hash code
	 */
	protected int hash(Object key1, Object key2, Object key3)
	{
		int h = 0;

		if (key1 != null)
		{
			h ^= key1.hashCode();
		}

		if (key2 != null)
		{
			h ^= key2.hashCode();
		}

		if (key3 != null)
		{
			h ^= key3.hashCode();
		}

		h += ~(h << 9);
		h ^= (h >>> 14);
		h += (h << 4);
		h ^= (h >>> 10);

		return h;
	}

	/**
	 * Is the key equal to the combined key.
	 *
	 * @param entry the entry to compare to
	 * @param key1  the first key
	 * @param key2  the second key
	 * @param key3  the third key
	 * @return true if the key matches
	 */
	protected boolean isEqualKey(AbstractHashedMap.HashEntry entry, Object key1, Object key2, Object key3)
	{
		MultiKey multi = (MultiKey) entry.getKey();

		return multi.size() == 3 &&
				(key1 == null ? multi.getKey(0) == null : key1.equals(multi.getKey(0))) &&
				(key2 == null ? multi.getKey(1) == null : key2.equals(multi.getKey(1))) &&
				(key3 == null ? multi.getKey(2) == null : key3.equals(multi.getKey(2)));
	}

	/**
	 * Gets the value mapped to the specified multi-key.
	 *
	 * @param key1 the first key
	 * @param key2 the second key
	 * @param key3 the third key
	 * @param key4 the fourth key
	 * @return the mapped value, null if no match
	 */
	public Object get(Object key1, Object key2, Object key3, Object key4)
	{
		int hashCode = hash(key1, key2, key3, key4);
		AbstractHashedMap.HashEntry entry = map.data[map.hashIndex(hashCode, map.data.length)];

		while (entry != null)
		{
			if (entry.hashCode == hashCode && isEqualKey(entry, key1, key2, key3, key4))
			{
				return entry.getValue();
			}

			entry = entry.next;
		}

		return null;
	}

	/**
	 * Checks whether the map contains the specified multi-key.
	 *
	 * @param key1 the first key
	 * @param key2 the second key
	 * @param key3 the third key
	 * @param key4 the fourth key
	 * @return true if the map contains the key
	 */
	public boolean containsKey(Object key1, Object key2, Object key3, Object key4)
	{
		int hashCode = hash(key1, key2, key3, key4);
		AbstractHashedMap.HashEntry entry = map.data[map.hashIndex(hashCode, map.data.length)];

		while (entry != null)
		{
			if (entry.hashCode == hashCode && isEqualKey(entry, key1, key2, key3, key4))
			{
				return true;
			}

			entry = entry.next;
		}

		return false;
	}

	/**
	 * Stores the value against the specified multi-key.
	 *
	 * @param key1  the first key
	 * @param key2  the second key
	 * @param key3  the third key
	 * @param key4  the fourth key
	 * @param value the value to store
	 * @return the value previously mapped to this combined key, null if none
	 */
	public Object put(Object key1, Object key2, Object key3, Object key4, Object value)
	{
		int hashCode = hash(key1, key2, key3, key4);
		int index = map.hashIndex(hashCode, map.data.length);

		AbstractHashedMap.HashEntry entry = map.data[index];

		while (entry != null)
		{
			if (entry.hashCode == hashCode && isEqualKey(entry, key1, key2, key3, key4))
			{
				Object oldValue = entry.getValue();
				map.updateEntry(entry, value);

				return oldValue;
			}

			entry = entry.next;
		}

		map.addMapping(index, hashCode, new MultiKey(key1, key2, key3, key4), value);
		return null;
	}

	/**
	 * Removes the specified multi-key from this map.
	 *
	 * @param key1 the first key
	 * @param key2 the second key
	 * @param key3 the third key
	 * @param key4 the fourth key
	 * @return the value mapped to the removed key, null if key not in map
	 */
	public Object remove(Object key1, Object key2, Object key3, Object key4)
	{
		int hashCode = hash(key1, key2, key3, key4);
		int index = map.hashIndex(hashCode, map.data.length);

		AbstractHashedMap.HashEntry entry = map.data[index];
		AbstractHashedMap.HashEntry previous = null;

		while (entry != null)
		{
			if (entry.hashCode == hashCode && isEqualKey(entry, key1, key2, key3, key4))
			{
				Object oldValue = entry.getValue();
				map.removeMapping(entry, index, previous);

				return oldValue;
			}

			previous = entry;
			entry = entry.next;
		}

		return null;
	}

	/**
	 * Gets the hash code for the specified multi-key.
	 *
	 * @param key1 the first key
	 * @param key2 the second key
	 * @param key3 the third key
	 * @param key4 the fourth key
	 * @return the hash code
	 */
	protected int hash(Object key1, Object key2, Object key3, Object key4)
	{
		int h = 0;

		if (key1 != null)
		{
			h ^= key1.hashCode();
		}

		if (key2 != null)
		{
			h ^= key2.hashCode();
		}

		if (key3 != null)
		{
			h ^= key3.hashCode();
		}

		if (key4 != null)
		{
			h ^= key4.hashCode();
		}

		h += ~(h << 9);
		h ^= (h >>> 14);
		h += (h << 4);
		h ^= (h >>> 10);

		return h;
	}

	/**
	 * Is the key equal to the combined key.
	 *
	 * @param entry the entry to compare to
	 * @param key1  the first key
	 * @param key2  the second key
	 * @param key3  the third key
	 * @param key4  the fourth key
	 * @return true if the key matches
	 */
	protected boolean isEqualKey(AbstractHashedMap.HashEntry entry, Object key1, Object key2, Object key3, Object key4)
	{
		MultiKey multi = (MultiKey) entry.getKey();
		return multi.size() == 4 &&
				(key1 == null ? multi.getKey(0) == null : key1.equals(multi.getKey(0))) &&
				(key2 == null ? multi.getKey(1) == null : key2.equals(multi.getKey(1))) &&
				(key3 == null ? multi.getKey(2) == null : key3.equals(multi.getKey(2))) &&
				(key4 == null ? multi.getKey(3) == null : key4.equals(multi.getKey(3)));
	}

	/**
	 * Gets the value mapped to the specified multi-key.
	 *
	 * @param key1 the first key
	 * @param key2 the second key
	 * @param key3 the third key
	 * @param key4 the fourth key
	 * @param key5 the fifth key
	 * @return the mapped value, null if no match
	 */
	public Object get(Object key1, Object key2, Object key3, Object key4, Object key5)
	{
		int hashCode = hash(key1, key2, key3, key4, key5);
		AbstractHashedMap.HashEntry entry = map.data[map.hashIndex(hashCode, map.data.length)];

		while (entry != null)
		{
			if (entry.hashCode == hashCode && isEqualKey(entry, key1, key2, key3, key4, key5)) return entry.getValue();

			entry = entry.next;
		}

		return null;
	}

	/**
	 * Checks whether the map contains the specified multi-key.
	 *
	 * @param key1 the first key
	 * @param key2 the second key
	 * @param key3 the third key
	 * @param key4 the fourth key
	 * @param key5 the fifth key
	 * @return true if the map contains the key
	 */
	public boolean containsKey(Object key1, Object key2, Object key3, Object key4, Object key5)
	{
		int hashCode = hash(key1, key2, key3, key4, key5);
		AbstractHashedMap.HashEntry entry = map.data[map.hashIndex(hashCode, map.data.length)];

		while (entry != null)
		{
			if (entry.hashCode == hashCode && isEqualKey(entry, key1, key2, key3, key4, key5)) return true;

			entry = entry.next;
		}

		return false;
	}

	/**
	 * Stores the value against the specified multi-key.
	 *
	 * @param key1  the first key
	 * @param key2  the second key
	 * @param key3  the third key
	 * @param key4  the fourth key
	 * @param key5  the fifth key
	 * @param value the value to store
	 * @return the value previously mapped to this combined key, null if none
	 */
	public Object put(Object key1, Object key2, Object key3, Object key4, Object key5, Object value)
	{
		int hashCode = hash(key1, key2, key3, key4, key5);
		int index = map.hashIndex(hashCode, map.data.length);

		AbstractHashedMap.HashEntry entry = map.data[index];

		while (entry != null)
		{
			if (entry.hashCode == hashCode && isEqualKey(entry, key1, key2, key3, key4, key5))
			{
				Object oldValue = entry.getValue();
				map.updateEntry(entry, value);

				return oldValue;
			}

			entry = entry.next;
		}

		map.addMapping(index, hashCode, new MultiKey(key1, key2, key3, key4, key5), value);
		return null;
	}

	/**
	 * Removes the specified multi-key from this map.
	 *
	 * @param key1 the first key
	 * @param key2 the second key
	 * @param key3 the third key
	 * @param key4 the fourth key
	 * @param key5 the fifth key
	 * @return the value mapped to the removed key, null if key not in map
	 */
	public Object remove(Object key1, Object key2, Object key3, Object key4, Object key5)
	{
		int hashCode = hash(key1, key2, key3, key4, key5);
		int index = map.hashIndex(hashCode, map.data.length);

		AbstractHashedMap.HashEntry entry = map.data[index];
		AbstractHashedMap.HashEntry previous = null;

		while (entry != null)
		{
			if (entry.hashCode == hashCode && isEqualKey(entry, key1, key2, key3, key4, key5))
			{
				Object oldValue = entry.getValue();
				map.removeMapping(entry, index, previous);
				return oldValue;
			}
			previous = entry;
			entry = entry.next;
		}

		return null;
	}

	/**
	 * Gets the hash code for the specified multi-key.
	 *
	 * @param key1 the first key
	 * @param key2 the second key
	 * @param key3 the third key
	 * @param key4 the fourth key
	 * @param key5 the fifth key
	 * @return the hash code
	 */
	protected int hash(Object key1, Object key2, Object key3, Object key4, Object key5)
	{
		int h = 0;
		if (key1 != null) h ^= key1.hashCode();

		if (key2 != null) h ^= key2.hashCode();

		if (key3 != null) h ^= key3.hashCode();

		if (key4 != null) h ^= key4.hashCode();

		if (key5 != null) h ^= key5.hashCode();

		h += ~(h << 9);
		h ^= (h >>> 14);
		h += (h << 4);
		h ^= (h >>> 10);

		return h;
	}

	/**
	 * Is the key equal to the combined key.
	 *
	 * @param entry the entry to compare to
	 * @param key1  the first key
	 * @param key2  the second key
	 * @param key3  the third key
	 * @param key4  the fourth key
	 * @param key5  the fifth key
	 * @return true if the key matches
	 */
	protected boolean isEqualKey(AbstractHashedMap.HashEntry entry, Object key1, Object key2, Object key3, Object key4, Object key5)
	{
		MultiKey multi = (MultiKey) entry.getKey();

		return multi.size() == 5 &&
				(key1 == null ? multi.getKey(0) == null : key1.equals(multi.getKey(0))) &&
				(key2 == null ? multi.getKey(1) == null : key2.equals(multi.getKey(1))) &&
				(key3 == null ? multi.getKey(2) == null : key3.equals(multi.getKey(2))) &&
				(key4 == null ? multi.getKey(3) == null : key4.equals(multi.getKey(3))) &&
				(key5 == null ? multi.getKey(4) == null : key5.equals(multi.getKey(4)));
	}

	/**
	 * Removes all mappings where the first key is that specified.
	 * <p/>
	 * This method removes all the mappings where the <code>MultiKey</code>
	 * has one or more keys, and the first matches that specified.
	 *
	 * @param key1 the first key
	 * @return true if any elements were removed
	 */
	public boolean removeAll(Object key1)
	{
		boolean modified = false;
		MapIterator it = mapIterator();
		
		while (it.hasNext())
		{
			MultiKey multi = (MultiKey) it.next();
			if (multi.size() >= 1 && (key1 == null ? multi.getKey(0) == null : key1.equals(multi.getKey(0))))
			{
				it.remove();
				modified = true;
			}
		}

		return modified;
	}

	/**
	 * Removes all mappings where the first two keys are those specified.
	 * <p/>
	 * This method removes all the mappings where the <code>MultiKey</code>
	 * has two or more keys, and the first two match those specified.
	 *
	 * @param key1 the first key
	 * @param key2 the second key
	 * @return true if any elements were removed
	 */
	public boolean removeAll(Object key1, Object key2)
	{
		boolean modified = false;
		MapIterator it = mapIterator();

		while (it.hasNext())
		{
			MultiKey multi = (MultiKey) it.next();
			if (multi.size() >= 2 && (key1 == null ? multi.getKey(0) == null : key1.equals(multi.getKey(0))) &&
					(key2 == null ? multi.getKey(1) == null : key2.equals(multi.getKey(1))))
			{
				it.remove();
				modified = true;
			}
		}

		return modified;
	}

	/**
	 * Removes all mappings where the first three keys are those specified.
	 * <p/>
	 * This method removes all the mappings where the <code>MultiKey</code>
	 * has three or more keys, and the first three match those specified.
	 *
	 * @param key1 the first key
	 * @param key2 the second key
	 * @param key3 the third key
	 * @return true if any elements were removed
	 */
	public boolean removeAll(Object key1, Object key2, Object key3)
	{
		boolean modified = false;
		MapIterator it = mapIterator();

		while (it.hasNext())
		{
			MultiKey multi = (MultiKey) it.next();
			if (multi.size() >= 3 && (key1 == null ? multi.getKey(0) == null : key1.equals(multi.getKey(0))) &&
					(key2 == null ? multi.getKey(1) == null : key2.equals(multi.getKey(1))) &&
					(key3 == null ? multi.getKey(2) == null : key3.equals(multi.getKey(2))))
			{
				it.remove();
				modified = true;
			}
		}
		return modified;
	}

	/**
	 * Removes all mappings where the first four keys are those specified.
	 * <p/>
	 * This method removes all the mappings where the <code>MultiKey</code>
	 * has four or more keys, and the first four match those specified.
	 *
	 * @param key1 the first key
	 * @param key2 the second key
	 * @param key3 the third key
	 * @param key4 the fourth key
	 * @return true if any elements were removed
	 */
	public boolean removeAll(Object key1, Object key2, Object key3, Object key4)
	{
		boolean modified = false;
		MapIterator it = mapIterator();

		while (it.hasNext())
		{
			MultiKey multi = (MultiKey) it.next();

			if (multi.size() >= 4 && (key1 == null ? multi.getKey(0) == null : key1.equals(multi.getKey(0))) &&
					(key2 == null ? multi.getKey(1) == null : key2.equals(multi.getKey(1))) &&
					(key3 == null ? multi.getKey(2) == null : key3.equals(multi.getKey(2))) &&
					(key4 == null ? multi.getKey(3) == null : key4.equals(multi.getKey(3))))
			{
				it.remove();
				modified = true;
			}
		}

		return modified;
	}

	/**
	 * Check to ensure that input keys are valid MultiKey objects.
	 *
	 * @param key the key to check
	 */
	protected void checkKey(Object key)
	{
		if (key == null)
		{
			throw new NullPointerException("Key must not be null");
		}

		if (!(key instanceof MultiKey))
		{
			throw new ClassCastException("Key must be a MultiKey");
		}
	}

	/**
	 * Clones the map without cloning the keys or values.
	 *
	 * @return a shallow clone
	 */
	public Object clone()
	{
		return new MultiKeyMap((AbstractHashedMap) map.clone());
	}

	/**
	 * Puts the key and value into the map, where the key must be a non-null
	 * MultiKey object.
	 *
	 * @param key   the non-null MultiKey object
	 * @param value the value to store
	 * @return the previous value for the key
	 * @throws NullPointerException if the key is null
	 * @throws ClassCastException   if the key is not a MultiKey
	 */
	public Object put(Object key, Object value)
	{
		checkKey(key);
		return map.put(key, value);
	}

	/**
	 * Puts all the keys and values into this map.
	 * Each key must be non-null and a MultiKey object.
	 *
	 * @throws NullPointerException if the mapToCopy or any key within is null
	 * @throws ClassCastException   if any key is not a MultiKey
	 */
	public void putAll(Map mapToCopy)
	{
		for (Iterator it = mapToCopy.keySet().iterator(); it.hasNext();)
		{
			Object key = it.next();
			checkKey(key);
		}

		map.putAll(mapToCopy);
	}

	public MapIterator mapIterator()
	{
		return map.mapIterator();
	}

	public int size()
	{
		return map.size();
	}

	public boolean isEmpty()
	{
		return map.isEmpty();
	}

	public boolean containsKey(Object key)
	{
		return map.containsKey(key);
	}

	public boolean containsValue(Object value)
	{
		return map.containsValue(value);
	}

	public Object get(Object key)
	{
		return map.get(key);
	}

	public Object remove(Object key)
	{
		return map.remove(key);
	}

	public boolean removeByValue(Object key)
	{
		return map.removeByValue(key);
	}

	public void clear()
	{
		map.clear();
	}

	public Set keySet()
	{
		return map.keySet();
	}

	public Collection values()
	{
		return map.values();
	}

	public Set entrySet()
	{
		return map.entrySet();
	}

	public boolean equals(Object obj)
	{
		if (obj == this)
		{
			return true;
		}

		return map.equals(obj);
	}

	public int hashCode()
	{
		return map.hashCode();
	}

	public String toString()
	{
		return map.toString();
	}
}
