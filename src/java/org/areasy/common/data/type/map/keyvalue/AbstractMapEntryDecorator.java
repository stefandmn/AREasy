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
 * Provides a base decorator that allows additional functionality to be added
 * to a Map Entry.
 *
 * @version $Id: AbstractMapEntryDecorator.java,v 1.2 2008/05/14 09:32:40 swd\stefan.damian Exp $
 */
public abstract class AbstractMapEntryDecorator implements Map.Entry, KeyValue
{

	/**
	 * The <code>Map.Entry</code> to decorate
	 */
	protected final Map.Entry entry;

	/**
	 * Constructor that wraps (not copies).
	 *
	 * @param entry the <code>Map.Entry</code> to decorate, must not be null
	 * @throws IllegalArgumentException if the collection is null
	 */
	public AbstractMapEntryDecorator(Map.Entry entry)
	{
		if (entry == null)
		{
			throw new IllegalArgumentException("Map Entry must not be null");
		}
		this.entry = entry;
	}

	/**
	 * Gets the map being decorated.
	 *
	 * @return the decorated map
	 */
	protected Map.Entry getMapEntry()
	{
		return entry;
	}

	public Object getKey()
	{
		return entry.getKey();
	}

	public Object getValue()
	{
		return entry.getValue();
	}

	public Object setValue(Object object)
	{
		return entry.setValue(object);
	}

	public boolean equals(Object object)
	{
		if (object == this)
		{
			return true;
		}
		return entry.equals(object);
	}

	public int hashCode()
	{
		return entry.hashCode();
	}

	public String toString()
	{
		return entry.toString();
	}

}
