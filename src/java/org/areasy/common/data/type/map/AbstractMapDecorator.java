package org.areasy.common.data.type.map;

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

import java.util.Collection;
import java.util.Map;
import java.util.Set;

/**
 * Provides a base decorator that enables additional functionality to be added
 * to a Map via decoration.
 * <p/>
 * Methods are forwarded directly to the decorated map.
 * <p/>
 * This implementation does not perform any special processing with
 * {@link #entrySet()}, {@link #keySet()} or {@link #values()}. Instead
 * it simply returns the set/collection from the wrapped map. This may be
 * undesirable, for example if you are trying to write a validating
 * implementation it would provide a loophole around the validation.
 * But, you might want that loophole, so this class is kept simple.
 *
 * @version $Id: AbstractMapDecorator.java,v 1.3 2008/05/20 06:52:55 swd\stefan.damian Exp $
 */
public abstract class AbstractMapDecorator implements Map
{
	/**
	 * The map to decorate
	 */
	protected transient Map map;

	/**
	 * Constructor only used in deserialization, do not use otherwise.
	 *
	 */
	protected AbstractMapDecorator()
	{
		super();
	}

	/**
	 * Constructor that wraps (not copies).
	 *
	 * @param map the map to decorate, must not be null
	 * @throws IllegalArgumentException if the collection is null
	 */
	public AbstractMapDecorator(Map map)
	{
		if (map == null) throw new IllegalArgumentException("Map must not be null");

		this.map = map;
	}

	/**
	 * Gets the map being decorated.
	 *
	 * @return the decorated map
	 */
	protected Map getMap()
	{
		return map;
	}

	public void clear()
	{
		map.clear();
	}

	public boolean containsKey(Object key)
	{
		return map.containsKey(key);
	}

	public boolean containsValue(Object value)
	{
		return map.containsValue(value);
	}

	public Set entrySet()
	{
		return map.entrySet();
	}

	public Object get(Object key)
	{
		return map.get(key);
	}

	public boolean isEmpty()
	{
		return map.isEmpty();
	}

	public Set keySet()
	{
		return map.keySet();
	}

	public Object put(Object key, Object value)
	{
		return map.put(key, value);
	}

	public void putAll(Map mapToCopy)
	{
		map.putAll(mapToCopy);
	}

	public Object remove(Object key)
	{
		return map.remove(key);
	}

	public int size()
	{
		return map.size();
	}

	public Collection values()
	{
		return map.values();
	}

	public boolean equals(Object object)
	{
		return object == this || map.equals(object);
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
