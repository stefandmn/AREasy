package org.areasy.common.data.type.map.keyvalue;

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

import org.areasy.common.data.type.KeyValue;

import java.io.Serializable;
import java.util.Map;

/**
 * A Map Entry tied to a map underneath.
 * <p/>
 * This can be used to enable a map entry to make changes on the underlying
 * map, however this will probably mess up any iterators.
 *
 * @version $Id: TiedMapEntry.java,v 1.2 2008/05/14 09:32:40 swd\stefan.damian Exp $
 */
public class TiedMapEntry implements Map.Entry, KeyValue, Serializable
{
	/**
	 * The map underlying the entry/iterator
	 */
	private final Map map;
	
	/**
	 * The key
	 */
	private final Object key;

	/**
	 * Constructs a new entry with the given Map and key.
	 *
	 * @param map the map
	 * @param key the key
	 */
	public TiedMapEntry(Map map, Object key)
	{
		super();
		this.map = map;
		this.key = key;
	}

	// Map.Entry interface
	/**
	 * Gets the key of this entry
	 *
	 * @return the key
	 */
	public Object getKey()
	{
		return key;
	}

	/**
	 * Gets the value of this entry direct from the map.
	 *
	 * @return the value
	 */
	public Object getValue()
	{
		return map.get(key);
	}

	/**
	 * Sets the value associated with the key direct onto the map.
	 *
	 * @param value the new value
	 * @return the old value
	 * @throws IllegalArgumentException if the value is set to this map entry
	 */
	public Object setValue(Object value)
	{
		if (value == this)
		{
			throw new IllegalArgumentException("Cannot set value to this map entry");
		}

		return map.put(key, value);
	}

	/**
	 * Compares this Map Entry with another Map Entry.
	 * <p/>
	 * Implemented per API documentation of {@link java.util.Map.Entry#equals(Object)}
	 *
	 * @param obj the object to compare to
	 * @return true if equal key and value
	 */
	public boolean equals(Object obj)
	{
		if (obj == this)
		{
			return true;
		}

		if (obj instanceof Map.Entry == false)
		{
			return false;
		}

		Map.Entry other = (Map.Entry) obj;
		Object value = getValue();

		return (key == null ? other.getKey() == null : key.equals(other.getKey())) &&
			(value == null ? other.getValue() == null : value.equals(other.getValue()));
	}

	/**
	 * Gets a hashCode compatible with the equals method.
	 * <p/>
	 * Implemented per API documentation of {@link java.util.Map.Entry#hashCode()}
	 *
	 * @return a suitable hash code
	 */
	public int hashCode()
	{
		Object value = getValue();
		return (getKey() == null ? 0 : getKey().hashCode()) ^
				(value == null ? 0 : value.hashCode());
	}

	/**
	 * Gets a string version of the entry.
	 *
	 * @return entry as a string
	 */
	public String toString()
	{
		return getKey() + "=" + getValue();
	}

}
