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

/**
 * Defines a map that maintains order and allows both forward and backward
 * iteration through that order.
 *
 * @version $Id: OrderedMap.java,v 1.2 2008/05/14 09:32:34 swd\stefan.damian Exp $
 */
public interface OrderedMap extends IterableMap
{
	/**
	 * Obtains an <code>OrderedMapIterator</code> over the map.
	 * <p/>
	 * A ordered map iterator is an efficient way of iterating over maps
	 * in both directions.
	 * <pre>
	 * BidiMap map = new TreeBidiMap();
	 * MapIterator it = map.mapIterator();
	 * while (it.hasNext()) {
	 *   Object key = it.next();
	 *   Object value = it.getValue();
	 *   it.setValue("newValue");
	 *   Object previousKey = it.previous();
	 * }
	 * </pre>
	 *
	 * @return a map iterator
	 */
	OrderedMapIterator orderedMapIterator();

	/**
	 * Gets the first key currently in this map.
	 *
	 * @return the first key currently in this map
	 * @throws java.util.NoSuchElementException
	 *          if this map is empty
	 */
	public Object firstKey();

	/**
	 * Gets the last key currently in this map.
	 *
	 * @return the last key currently in this map
	 * @throws java.util.NoSuchElementException
	 *          if this map is empty
	 */
	public Object lastKey();

	/**
	 * Gets the next key after the one specified.
	 *
	 * @param key the key to search for next from
	 * @return the next key, null if no match or at end
	 */
	public Object nextKey(Object key);

	/**
	 * Gets the previous key before the one specified.
	 *
	 * @param key the key to search for previous from
	 * @return the previous key, null if no match or at start
	 */
	public Object previousKey(Object key);

}
