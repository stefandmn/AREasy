package org.areasy.runtime.engine.services.cache;

/*
 * Copyright (c) 2007-2016 AREasy Runtime
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

import org.areasy.common.data.StringUtility;
import org.areasy.common.data.type.map.ListOrderedMap;
import org.areasy.common.data.type.map.StaticBucketMap;
import org.areasy.common.logger.Logger;
import org.areasy.common.logger.LoggerFactory;

import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * Data cache layer for runtime server, having capability to define an unlimited or limited cache layer depending by
 * object age.
 *
 */
public class DataCache
{
	/** Library logger */
	private static Logger logger =  LoggerFactory.getLog(DataCache.class);

	/** Stores cached objects */
	private ListOrderedMap buffer = null;

	/** Store keys aliases */
	private StaticBucketMap aliases = null;

	/** Cache instance size */
	private int size = 0;

	/**
	 * Constructs a new <code>DataCache</code> big enough to hold 32 elements.
	 */
	public DataCache()
	{
		this.buffer = new ListOrderedMap();
		this.aliases = new StaticBucketMap();
	}

	/**
	 * Default cache buffer constructor specifying the length of the container.
	 *
	 * @param size buffer size
	 */
	public DataCache(int size)
	{
		this.buffer = new ListOrderedMap();
		this.aliases = new StaticBucketMap();

		this.size = size;
	}

	/**
	 * Get the entire buffer with all cached objects. This is an internal method available only for inherited classes
	 * @return the map structure with all cached objects.
	 */
	protected ListOrderedMap getBuffer()
	{
		return this.buffer;
	}

	/**
	 * Get the map between primary key and his aliases
	 * @return an <code>Map</code> object.
	 */
	protected Map getAliases()
	{
		return this.aliases;
	}

	/**
	 * Get site of the cache buffer.
	 *
	 * @return size of the cache buffer
	 */
	public int size()
	{
		return this.size;
	}

	/**
     * Returns <tt>true</tt> if this map contains a mapping for the specified
     * key.  More formally, returns <tt>true</tt> if and only if
     * this map contains a mapping for a key <tt>k</tt> such that
     * <tt>(key==null ? k==null : key.equals(k))</tt>.  (There can be
     * at most one such mapping.)
     *
     * @param key key whose presence in this map is to be tested.
     * @return <tt>true</tt> if this map contains a mapping for the specified key.
	 */
	public boolean contains(String key)
	{
		return getBuffer().containsKey(key) || getAliases().containsValue(key);
	}

	/**
	 * Add a new object in the buffer cache.
	 *
	 * @param key object key
	 * @param object object instance
	 */
	public void add(String key, Object object)
	{
		add(key, object, CacheEntry.DEFAULT);
	}

	/**
	 * Add a new object in the buffer cache.
	 *
	 * @param key object key
	 * @param object object instance
	 * @param ttl time to live for the current cache entry
	 */
	public void add(String key, Object object, int ttl)
	{
		CacheEntry cache = null;
		if(object == null) return;

		if(!(object instanceof CacheEntry)) cache = new CacheEntry(object, ttl);
			else cache = (CacheEntry) object;

		getBuffer().putFirst(key, cache);
		removeLast();

		if(size() > 0 && getBuffer().size() > size()) logger.warn("Cache buffer is greater then specified limit: " + size() + " (actual size is " + getBuffer().size() + ")");
	}

	/**
	 * Add an alias key to the primary key for the corresponding cache entry.
	 *
	 * @param key primary for the cache entry
	 * @param alias alias key
	 */
	public void addAlias(String key, String alias)
	{
		if(key != null && alias != null && !StringUtility.equals(alias, key) && !getBuffer().containsKey(alias) && getBuffer().containsKey(key)) getAliases().put(alias, key);
	}

	/**
	 * Remove last object from the cache.
	 */
	protected void removeLast()
	{
		if(size() > 0 && getBuffer().size() >= size())
		{
			String deletion = (String) getBuffer().lastKey();

			if(deletion != null)
			{
				remove(deletion);
				removeLast();
			}
		}
	}

	/**
	 * Remove all expired objects from the cache layer.
	 */
	public void removeExpiredObjects()
	{
		List list = getBuffer().asList();

		for(int i = 0; list != null && i < list.size(); i++)
		{
			String key = (String) list.get(i);
			Object object = getBuffer().get(key);

			if(object instanceof CacheEntry)
			{
				CacheEntry cache = (CacheEntry)object;
				if(cache.isExpired()) remove(key);
			}
		}
	}

	/**
	 * Get an object instance stored in cache container.
	 *
	 * @param key object key identifier
	 * @return cache entry instance that encapsulate the object instance
	 */
	public CacheEntry getCacheEntry(String key)
	{
		CacheEntry entry = null;

	    if(getBuffer().containsKey(key)) entry = (CacheEntry) getBuffer().get(key);
		else if(getAliases().containsKey(key))
		{
			String alias = (String)getAliases().get(key);
			entry = (CacheEntry) getBuffer().get(alias);
		}

		return entry;
	}

	/**
	 * Get an object instance stored in cache container.
	 *
	 * @param key object key identifier
	 * @return object object instance.
	 */
	public Object get(String key)
	{
		CacheEntry entry = getCacheEntry(key);

		if(entry != null) return entry.getContent();
			else return null;
	}

	/**
	 * Remove an object instance stored in the cache container.
	 *
	 * @param key object key identifier
	 */
	public void remove(String key)
	{
		if(key == null) return;

		//destroy preloaded objects
		Object preload = get(key);
		if(preload != null && preload instanceof InitialObject)
		{
			logger.debug("Destroy preloaded object: " + preload.getClass().getName());
			((InitialObject) preload).destroy();
		}
		
		if(getAliases().containsKey(key))
		{
			String alias = (String)getAliases().get(key);

			getBuffer().remove(alias);
			getAliases().remove(key);

			logger.debug("Remove object from cache aliases: " + key);
		}
		else
		{
			getBuffer().remove(key);
			logger.debug("Remove object from cache buffer: " + key);

			if(getAliases().containsValue(key))
			{
				Iterator iterator = getAliases().keySet().iterator();

				while(iterator != null && iterator.hasNext())
				{
					Object object = iterator.next();

					if(object != null && object instanceof String)
					{
						String alias = (String) object;
						if(StringUtility.equals((String) getAliases().get(alias), key)) getAliases().remove(alias);
					}
				}
			}
		}
	}

	/**
	 * Remove all objects from the cache container.
	 */
	public void clear()
	{
		getBuffer().clear();
		getAliases().clear();
	}

	/**
	 * Set new size of the current cache buffer
	 *
	 * @param size length of the cache buffer.
	 */
	public void setSize(int size)
	{
		this.size = size;
	}

	public void setDefaultAge(int age)
	{
		CacheEntry.DEFAULT = age;
	}
}
