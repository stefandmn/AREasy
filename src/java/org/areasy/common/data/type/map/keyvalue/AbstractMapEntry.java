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

import java.util.Map;

/**
 * Abstract Pair class to assist with creating correct Map Entry implementations.
 *
 * @version $Id: AbstractMapEntry.java,v 1.2 2008/05/14 09:32:40 swd\stefan.damian Exp $
 */
public abstract class AbstractMapEntry extends AbstractKeyValue implements Map.Entry
{

	/**
	 * Constructs a new entry with the given key and given value.
	 *
	 * @param key   the key for the entry, may be null
	 * @param value the value for the entry, may be null
	 */
	protected AbstractMapEntry(Object key, Object value)
	{
		super(key, value);
	}

	// Map.Entry interface
	/**
	 * Sets the value stored in this Map Entry.
	 * <p/>
	 * This Map Entry is not connected to a Map, so only the local data is changed.
	 *
	 * @param value the new value
	 * @return the previous value
	 */
	public Object setValue(Object value)
	{
		Object answer = this.value;
		this.value = value;
		return answer;
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
		return
				(getKey() == null ? other.getKey() == null : getKey().equals(other.getKey())) &&
				(getValue() == null ? other.getValue() == null : getValue().equals(other.getValue()));
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
		return (getKey() == null ? 0 : getKey().hashCode()) ^
				(getValue() == null ? 0 : getValue().hashCode());
	}

}
