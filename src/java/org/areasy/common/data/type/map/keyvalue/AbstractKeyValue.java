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

/**
 * Abstract pair class to assist with creating KeyValue and MapEntry implementations.
 *
 * @version $Id: AbstractKeyValue.java,v 1.2 2008/05/14 09:32:40 swd\stefan.damian Exp $
 */
public abstract class AbstractKeyValue implements KeyValue
{

	/**
	 * The key
	 */
	protected Object key;
	/**
	 * The value
	 */
	protected Object value;

	/**
	 * Constructs a new pair with the specified key and given value.
	 *
	 * @param key   the key for the entry, may be null
	 * @param value the value for the entry, may be null
	 */
	protected AbstractKeyValue(Object key, Object value)
	{
		super();
		this.key = key;
		this.value = value;
	}

	/**
	 * Gets the key from the pair.
	 *
	 * @return the key
	 */
	public Object getKey()
	{
		return key;
	}

	/**
	 * Gets the value from the pair.
	 *
	 * @return the value
	 */
	public Object getValue()
	{
		return value;
	}

	/**
	 * Gets a debugging String view of the pair.
	 *
	 * @return a String view of the entry
	 */
	public String toString()
	{
		return new StringBuffer()
				.append(getKey())
				.append('=')
				.append(getValue())
				.toString();
	}

}
