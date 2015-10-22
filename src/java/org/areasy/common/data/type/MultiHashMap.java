package org.areasy.common.data.type;

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

import org.areasy.common.data.type.iterator.EmptyIterator;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.*;

/**
 * <code>MultiHashMap</code> is the default implementation of the
 * {@link org.areasy.common.data.type.MultiMap MultiMap} interface.
 * <p/>
 * A <code>MultiMap</code> is a Map with slightly different semantics.
 * Putting a value into the map will add the value to a Collection at that key.
 * Getting a value will return a Collection, holding all the values put to that key.
 * <p/>
 * This implementation uses an <code>ArrayList</code> as the collection.
 * The internal storage list is made available without cloning via the
 * <code>get(Object)</code> and <code>entrySet()</code> methods.
 * The implementation returns <code>null</code> when there are no values mapped to a key.
 * <p/>
 * For example:
 * <pre>
 * MultiMap mhm = new MultiHashMap();
 * mhm.put(key, "A");
 * mhm.put(key, "B");
 * mhm.put(key, "C");
 * List list = (List) mhm.get(key);</pre>
 * <p/>
 * <code>list</code> will be a list containing "A", "B", "C".
 *
 * @version $Id: MultiHashMap.java,v 1.2 2008/05/14 09:32:34 swd\stefan.damian Exp $
 */
public class MultiHashMap extends HashMap implements MultiMap
{
	// backed values collection
	private transient Collection values = null;

	/**
	 * Constructor.
	 */
	public MultiHashMap()
	{
		super();
	}

	/**
	 * Constructor.
	 *
	 * @param initialCapacity the initial map capacity
	 */
	public MultiHashMap(int initialCapacity)
	{
		super(initialCapacity);
	}

	/**
	 * Constructor.
	 *
	 * @param initialCapacity the initial map capacity
	 * @param loadFactor      the amount 0.0-1.0 at which to resize the map
	 */
	public MultiHashMap(int initialCapacity, float loadFactor)
	{
		super(initialCapacity, loadFactor);
	}

	/**
	 * Constructor that copies the input map creating an independent copy.
	 * <p/>
	 * This method performs different behaviour depending on whether the map
	 * specified is a MultiMap or not. If a MultiMap is specified, each internal
	 * collection is also cloned. If the specified map only implements Map, then
	 * the values are not cloned.
	 *
	 * @param mapToCopy a Map to copy
	 */
	public MultiHashMap(Map mapToCopy)
	{
		// be careful of JDK 1.3 vs 1.4 differences
		super((int) (mapToCopy.size() * 1.4f));

		if (mapToCopy instanceof MultiMap)
		{
			for (Iterator it = mapToCopy.entrySet().iterator(); it.hasNext();)
			{
				Map.Entry entry = (Map.Entry) it.next();
				Collection coll = (Collection) entry.getValue();
				Collection newColl = createCollection(coll);

				super.put(entry.getKey(), newColl);
			}
		}
		else
		{
			putAll(mapToCopy);
		}
	}

	/**
	 * Read the object during deserialization.
	 */
	private void readObject(ObjectInputStream s) throws IOException, ClassNotFoundException
	{
		// default read object
		s.defaultReadObject();

		// problem only with jvm <1.4
		String version = "1.2";

		try
		{
			version = System.getProperty("java.version");
		}
		catch (SecurityException ex)
		{
			// ignore and treat as 1.2/1.3
		}

		if (version.startsWith("1.2") || version.startsWith("1.3"))
		{
			for (Iterator iterator = entrySet().iterator(); iterator.hasNext();)
			{
				Map.Entry entry = (Map.Entry) iterator.next();
				// put has created a extra collection level, remove it
				super.put(entry.getKey(), ((Collection) entry.getValue()).iterator().next());
			}
		}
	}

	/**
	 * Gets the total size of the map by counting all the values.
	 *
	 * @return the total size of the map counting all values
	 */
	public int totalSize()
	{
		int total = 0;
		Collection values = super.values();

		for (Iterator it = values.iterator(); it.hasNext();)
		{
			Collection coll = (Collection) it.next();
			total += coll.size();
		}

		return total;
	}

	/**
	 * Gets the collection mapped to the specified key.
	 * This method is a convenience method to typecast the result of <code>get(key)</code>.
	 *
	 * @param key the key to retrieve
	 * @return the collection mapped to the key, null if no mapping
	 */
	public Collection getCollection(Object key)
	{
		return (Collection) get(key);
	}

	/**
	 * Gets the size of the collection mapped to the specified key.
	 *
	 * @param key the key to get size for
	 * @return the size of the collection at the key, zero if key not in map
	 */
	public int size(Object key)
	{
		Collection coll = getCollection(key);

		if (coll == null)
		{
			return 0;
		}

		return coll.size();
	}

	/**
	 * Gets an iterator for the collection mapped to the specified key.
	 *
	 * @param key the key to get an iterator for
	 * @return the iterator of the collection at the key, empty iterator if key not in map
	 */
	public Iterator iterator(Object key)
	{
		Collection coll = getCollection(key);

		if (coll == null)
		{
			return EmptyIterator.INSTANCE;
		}

		return coll.iterator();
	}

	/**
	 * Adds the value to the collection associated with the specified key.
	 * <p/>
	 * Unlike a normal <code>Map</code> the previous value is not replaced.
	 * Instead the new value is added to the collection stored against the key.
	 *
	 * @param key   the key to store against
	 * @param value the value to add to the collection at the key
	 * @return the value added if the map changed and null if the map did not change
	 */
	public Object put(Object key, Object value)
	{
		Collection coll = getCollection(key);

		if (coll == null)
		{
			coll = createCollection(null);
			super.put(key, coll);
		}

		boolean results = coll.add(value);

		return (results ? value : null);
	}

	/**
	 * Adds a collection of values to the collection associated with the specified key.
	 *
	 * @param key    the key to store against
	 * @param values the values to add to the collection at the key, null ignored
	 * @return true if this map changed
	 */
	public boolean putAll(Object key, Collection values)
	{
		if (values == null || values.size() == 0)
		{
			return false;
		}

		Collection coll = getCollection(key);

		if (coll == null)
		{
			coll = createCollection(values);
			if (coll.size() == 0)
			{
				return false;
			}

			super.put(key, coll);

			return true;
		}
		else
		{
			return coll.addAll(values);
		}
	}

	/**
	 * Checks whether the map contains the value specified.
	 * <p/>
	 * This checks all collections against all keys for the value, and thus could be slow.
	 *
	 * @param value the value to search for
	 * @return true if the map contains the value
	 */
	public boolean containsValue(Object value)
	{
		Set pairs = super.entrySet();

		if (pairs == null)
		{
			return false;
		}

		Iterator pairsIterator = pairs.iterator();

		while (pairsIterator.hasNext())
		{
			Map.Entry keyValuePair = (Map.Entry) pairsIterator.next();
			Collection coll = (Collection) keyValuePair.getValue();

			if (coll.contains(value))
			{
				return true;
			}
		}

		return false;
	}

	/**
	 * Checks whether the collection at the specified key contains the value.
	 *
	 * @param value the value to search for
	 * @return true if the map contains the value
	 */
	public boolean containsValue(Object key, Object value)
	{
		Collection coll = getCollection(key);

		if (coll == null)
		{
			return false;
		}

		return coll.contains(value);
	}

	/**
	 * Removes a specific value from map.
	 * <p/>
	 * The item is removed from the collection mapped to the specified key.
	 * Other values attached to that key are unaffected.
	 * <p/>
	 * If the last value for a key is removed, <code>null</code> will be returned
	 * from a subsequant <code>get(key)</code>.
	 *
	 * @param key  the key to remove from
	 * @param item the value to remove
	 * @return the value removed (which was passed in), null if nothing removed
	 */
	public Object remove(Object key, Object item)
	{
		Collection valuesForKey = getCollection(key);

		if (valuesForKey == null)
		{
			return null;
		}

		valuesForKey.remove(item);

		// remove the list if it is now empty (saves space, and allows equals to work)
		if (valuesForKey.isEmpty())
		{
			remove(key);
		}

		return item;
	}

	/**
	 * Clear the map.
	 * <p/>
	 * This clears each collection in the map, and so may be slow.
	 */
	public void clear()
	{
		// For gc, clear each list in the map
		Set pairs = super.entrySet();
		Iterator pairsIterator = pairs.iterator();

		while (pairsIterator.hasNext())
		{
			Map.Entry keyValuePair = (Map.Entry) pairsIterator.next();
			Collection coll = (Collection) keyValuePair.getValue();
			coll.clear();
		}

		super.clear();
	}

	/**
	 * Gets a collection containing all the values in the map.
	 * <p/>
	 * This returns a collection containing the combination of values from all keys.
	 *
	 * @return a collection view of the values contained in this map
	 */
	public Collection values()
	{
		Collection vs = values;
		return (vs != null ? vs : (values = new Values()));
	}

	/**
	 * Inner class to view the elements.
	 */
	private class Values extends AbstractCollection
	{
		public Iterator iterator()
		{
			return new ValueIterator();
		}

		public int size()
		{
			int compt = 0;
			Iterator it = iterator();

			while (it.hasNext())
			{
				it.next();
				compt++;
			}

			return compt;
		}

		public void clear()
		{
			MultiHashMap.this.clear();
		}
	}

	/**
	 * Inner iterator to view the elements.
	 */
	private class ValueIterator implements Iterator
	{
		private Iterator backedIterator;
		private Iterator tempIterator;

		private ValueIterator()
		{
			backedIterator = MultiHashMap.super.values().iterator();
		}

		private boolean searchNextIterator()
		{
			while (tempIterator == null || tempIterator.hasNext() == false)
			{
				if (backedIterator.hasNext() == false)
				{
					return false;
				}

				tempIterator = ((Collection) backedIterator.next()).iterator();
			}

			return true;
		}

		public boolean hasNext()
		{
			return searchNextIterator();
		}

		public Object next()
		{
			if (searchNextIterator() == false)
			{
				throw new NoSuchElementException();
			}

			return tempIterator.next();
		}

		public void remove()
		{
			if (tempIterator == null)
			{
				throw new IllegalStateException();
			}

			tempIterator.remove();
		}

	}

	/**
	 * Clones the map creating an independent copy.
	 * <p/>
	 * The clone will shallow clone the collections as well as the map.
	 *
	 * @return the cloned map
	 */
	public Object clone()
	{
		MultiHashMap cloned = (MultiHashMap) super.clone();

		// clone each Collection container
		for (Iterator it = cloned.entrySet().iterator(); it.hasNext();)
		{
			Map.Entry entry = (Map.Entry) it.next();
			Collection coll = (Collection) entry.getValue();
			Collection newColl = createCollection(coll);
			entry.setValue(newColl);
		}

		return cloned;
	}

	/**
	 * Creates a new instance of the map value Collection container.
	 * <p/>
	 * This method can be overridden to use your own collection type.
	 *
	 * @param coll the collection to copy, may be null
	 * @return the new collection
	 */
	protected Collection createCollection(Collection coll)
	{
		if (coll == null)
		{
			return new ArrayList();
		}
		else
		{
			return new ArrayList(coll);
		}
	}

}
