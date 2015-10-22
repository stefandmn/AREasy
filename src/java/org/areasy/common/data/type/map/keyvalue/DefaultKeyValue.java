package org.areasy.common.data.type.map.keyvalue;

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

import org.areasy.common.data.type.KeyValue;

import java.util.Map;

/**
 * A mutable KeyValue pair that does not implement MapEntry.
 * <p/>
 * Note that a <code>DefaultKeyValue</code> instance may not contain
 * itself as a key or value.
 *
 * @version $Id: DefaultKeyValue.java,v 1.3 2008/05/20 06:52:55 swd\stefan.damian Exp $
 */
public class DefaultKeyValue extends AbstractKeyValue
{
	/**
	 * Constructs a new pair with a null key and null value.
	 */
	public DefaultKeyValue()
	{
		super(null, null);
	}

	/**
	 * Constructs a new pair with the specified key and given value.
	 *
	 * @param key   the key for the entry, may be null
	 * @param value the value for the entry, may be null
	 */
	public DefaultKeyValue(final Object key, final Object value)
	{
		super(key, value);
	}

	/**
	 * Constructs a new pair from the specified KeyValue.
	 *
	 * @param pair the pair to copy, must not be null
	 * @throws NullPointerException if the entry is null
	 */
	public DefaultKeyValue(final KeyValue pair)
	{
		super(pair.getKey(), pair.getValue());
	}

	/**
	 * Constructs a new pair from the specified MapEntry.
	 *
	 * @param entry the entry to copy, must not be null
	 * @throws NullPointerException if the entry is null
	 */
	public DefaultKeyValue(final Map.Entry entry)
	{
		super(entry.getKey(), entry.getValue());
	}

	/**
	 * Sets the key.
	 *
	 * @param key the new key
	 * @return the old key
	 * @throws IllegalArgumentException if key is this object
	 */
	public Object setKey(final Object key)
	{
		if (key == this) throw new IllegalArgumentException("Default key value may not contain itself as a key.");

		final Object old = this.key;
		this.key = key;

		return old;
	}

	/**
	 * Sets the value.
	 *
	 * @param value the new value
	 * @return the old value of the value
	 * @throws IllegalArgumentException if value is this object
	 */
	public Object setValue(final Object value)
	{
		if (value == this) throw new IllegalArgumentException("Default key value may not contain itself as a value.");

		final Object old = this.value;
		this.value = value;
		
		return old;
	}

	/**
	 * Returns a new <code>Map.Entry</code> object with key and value from this pair.
	 *
	 * @return a MapEntry instance
	 */
	public Map.Entry toMapEntry()
	{
		return new DefaultMapEntry(this);
	}

	/**
	 * Compares this Map Entry with another Map Entry.
	 * <p/>
	 * Returns true if the compared object is also a <code>DefaultKeyValue</code>,
	 * and its key and value are equal to this object's key and value.
	 *
	 * @param obj the object to compare to
	 * @return true if equal key and value
	 */
	public boolean equals(final Object obj)
	{
		if (obj == this) return true;

		if (!(obj instanceof DefaultKeyValue)) return false;

		DefaultKeyValue other = (DefaultKeyValue) obj;

		return (getKey() == null ? other.getKey() == null : getKey().equals(other.getKey())) &&
				(getValue() == null ? other.getValue() == null : getValue().equals(other.getValue()));
	}

	/**
	 * Gets a hashCode compatible with the equals method.
	 * <p/>
	 * Implemented per API documentation of {@link java.util.Map.Entry#hashCode()},
	 * however subclasses may override this.
	 *
	 * @return a suitable hash code
	 */
	public int hashCode()
	{
		return (getKey() == null ? 0 : getKey().hashCode()) ^
				(getValue() == null ? 0 : getValue().hashCode());
	}

}
