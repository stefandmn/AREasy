package org.areasy.common.data.type;

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

import java.util.Collection;
import java.util.Map;

/**
 * Defines a map that holds a collection of values against each key.
 * <p/>
 * A <code>MultiMap</code> is a Map with slightly different semantics.
 * Putting a value into the map will add the value to a Collection at that key.
 * Getting a value will return a Collection, holding all the values put to that key.
 * <p/>
 * For example:
 * <pre>
 * MultiMap mhm = new MultiHashMap();
 * mhm.put(key, "A");
 * mhm.put(key, "B");
 * mhm.put(key, "C");
 * Collection coll = (Collection) mhm.get(key);</pre>
 * <p/>
 * <code>coll</code> will be a collection containing "A", "B", "C".
 *
 * @version $Id: MultiMap.java,v 1.2 2008/05/14 09:32:34 swd\stefan.damian Exp $
 */
public interface MultiMap extends Map
{
	/**
	 * Removes a specific value from map.
	 * <p/>
	 * The item is removed from the collection mapped to the specified key.
	 * Other values attached to that key are unaffected.
	 * <p/>
	 * If the last value for a key is removed, implementations typically
	 * return <code>null</code> from a subsequant <code>get(Object)</code>, however
	 * they may choose to return an empty collection.
	 *
	 * @param key  the key to remove from
	 * @param item the item to remove
	 * @return the value removed (which was passed in), null if nothing removed
	 * @throws UnsupportedOperationException if the map is unmodifiable
	 * @throws ClassCastException            if the key or value is of an invalid type
	 * @throws NullPointerException          if the key or value is null and null is invalid
	 */
	public boolean remove(Object key, Object item);

	/**
	 * Gets the number of keys in this map.
	 * <p/>
	 * Implementations typically return only the count of keys in the map
	 * This cannot be mandated due to backwards compatability of this interface.
	 *
	 * @return the number of key-collection mappings in this map
	 */
	int size();

	/**
	 * Gets the collection of values associated with the specified key.
	 * <p/>
	 * The returned value will implement <code>Collection</code>. Implementations
	 * are free to declare that they return <code>Collection</code> subclasses
	 * such as <code>List</code> or <code>Set</code>.
	 * <p/>
	 * Implementations typically return <code>null</code> if no values have
	 * been mapped to the key, however the implementation may choose to
	 * return an empty collection.
	 * <p/>
	 * Implementations may choose to return a clone of the internal collection.
	 *
	 * @param key the key to retrieve
	 * @return the <code>Collection</code> of values, implementations should
	 *         return <code>null</code> for no mapping, but may return an empty collection
	 * @throws ClassCastException   if the key is of an invalid type
	 * @throws NullPointerException if the key is null and null keys are invalid
	 */
	Object get(Object key);

	/**
	 * Checks whether the map contains the value specified.
	 * <p/>
	 * Implementations typically check all collections against all keys for the value.
	 * This cannot be mandated due to backwards compatability of this interface.
	 *
	 * @param value the value to search for
	 * @return true if the map contains the value
	 * @throws ClassCastException   if the value is of an invalid type
	 * @throws NullPointerException if the value is null and null value are invalid
	 */
	boolean containsValue(Object value);

	/**
	 * Adds the value to the collection associated with the specified key.
	 * <p/>
	 * Unlike a normal <code>Map</code> the previous value is not replaced.
	 * Instead the new value is added to the collection stored against the key.
	 * The collection may be a <code>List</code>, <code>Set</code> or other
	 * collection dependent on implementation.
	 *
	 * @param key   the key to store against
	 * @param value the value to add to the collection at the key
	 * @return typically the value added if the map changed and null if the map did not change
	 * @throws UnsupportedOperationException if the map is unmodifiable
	 * @throws ClassCastException            if the key or value is of an invalid type
	 * @throws NullPointerException          if the key or value is null and null is invalid
	 * @throws IllegalArgumentException      if the key or value is invalid
	 */
	Object put(Object key, Object value);

	/**
	 * Removes all values associated with the specified key.
	 * <p/>
	 * Implementations typically return <code>null</code> from a subsequant
	 * <code>get(Object)</code>, however they may choose to return an empty collection.
	 *
	 * @param key the key to remove values from
	 * @return the <code>Collection</code> of values removed, implementations should
	 *         return <code>null</code> for no mapping found, but may return an empty collection
	 * @throws UnsupportedOperationException if the map is unmodifiable
	 * @throws ClassCastException            if the key is of an invalid type
	 * @throws NullPointerException          if the key is null and null keys are invalid
	 */
	Object remove(Object key);

	/**
	 * Gets a collection containing all the values in the map.
	 * <p/>
	 * Inplementations typically return a collection containing the combination
	 * of values from all keys.
	 * This cannot be mandated due to backwards compatability of this interface.
	 *
	 * @return a collection view of the values contained in this map
	 */
	Collection values();

}
