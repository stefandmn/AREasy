package org.areasy.runtime.engine.services.cache;

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

/**
 * Wrapper for an object you want to store in a cache for a period of time.
 *
 */
public class CacheEntry implements java.io.Serializable
{
	/** Cache the object with the Default TTL is 72h*/
	static int DEFAULT = 259200;

	/** Do not expire the object */
	static final int FOREVER = -1;

	/** The object to be cached. */
	private Object content = null;

	/** When created. */
	protected long creationTimestamp = 0;

	/** When it expires. */
	private long expireTime = 0;

	/**
	 * Constructor; sets the object to expire in the default time (30
	 * minutes).
	 *
	 * @param o The object you want to cache.
	 */
	public CacheEntry(Object o)
	{
		this.content = o;
		this.expireTime = DEFAULT;
		this.creationTimestamp = System.currentTimeMillis();
	}

	/**
	 * Constructor.
	 *
	 * @param o	   The object to cache.
	 * @param expiresIn How long before the object expires, in ms,
	 *                e.g. 1000 = 1 second.
	 */
	public CacheEntry(Object o, long expiresIn)
	{
		this.content = o;
		this.expireTime = expiresIn;
		this.creationTimestamp = System.currentTimeMillis();
	}

	/**
	 * Constructor.
	 *
	 * @param o	   The object to cache.
	 * @param forever say if the object is kept forever or is a normal object
	 */
	public CacheEntry(Object o, boolean forever)
	{
		this.content = o;
		this.creationTimestamp = System.currentTimeMillis();

		if(forever) this.expireTime = FOREVER;
			else this.expireTime = DEFAULT;
	}

	/**
	 * Returns the cached object.
	 *
	 * @return The cached object.
	 */
	public Object getContent()
	{
		return content;
	}

	/**
	 * Returns the creation time for the object.
	 *
	 * @return When the object was created.
	 */
	public long getCreationTimestamp()
	{
		return creationTimestamp;
	}

	/**
	 * Returns the expiration time for the object.
	 *
	 * @return When the object expires.
	 */
	public long getExpireTime()
	{
		return expireTime;
	}

	/**
	 * Set the expiration interval for the object.
	 *
	 * @param expireTime Expiration interval in seconds
	 */
	public void setExpireTime(long expireTime)
	{
		this.expireTime = expireTime;
	}

	/**
	 * Make actual object expired.
	 */
	public void setForever()
	{
		this.expireTime = FOREVER;
	}

	/**
	 * Make actual object expired.
	 */
	public void setExpired()
	{
		this.expireTime = (System.currentTimeMillis() - creationTimestamp)/1000;
	}

	/**
	 * Check if actual object is expired.
	 *
	 * @return True if the object is stale.
	 */
	public synchronized boolean isExpired()
	{
		return expireTime != FOREVER && ((System.currentTimeMillis() - creationTimestamp) > (expireTime * 1000));
	}
}
