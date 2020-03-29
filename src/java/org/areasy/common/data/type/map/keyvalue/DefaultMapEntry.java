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

import org.areasy.common.data.type.KeyValue;

import java.util.Map;

/**
 * A restricted implementation of {@link java.util.Map.Entry} that prevents
 * the MapEntry contract from being broken.
 *
 * @version $Id: DefaultMapEntry.java,v 1.2 2008/05/14 09:32:40 swd\stefan.damian Exp $
 */
public final class DefaultMapEntry extends AbstractMapEntry
{

	/**
	 * Constructs a new entry with the specified key and given value.
	 *
	 * @param key   the key for the entry, may be null
	 * @param value the value for the entry, may be null
	 */
	public DefaultMapEntry(final Object key, final Object value)
	{
		super(key, value);
	}

	/**
	 * Constructs a new entry from the specified KeyValue.
	 *
	 * @param pair the pair to copy, must not be null
	 * @throws NullPointerException if the entry is null
	 */
	public DefaultMapEntry(final KeyValue pair)
	{
		super(pair.getKey(), pair.getValue());
	}

	/**
	 * Constructs a new entry from the specified MapEntry.
	 *
	 * @param entry the entry to copy, must not be null
	 * @throws NullPointerException if the entry is null
	 */
	public DefaultMapEntry(final Map.Entry entry)
	{
		super(entry.getKey(), entry.getValue());
	}

}
