package org.areasy.common.velocity.runtime.resource;

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

import org.areasy.common.velocity.runtime.RuntimeService;

import java.util.Iterator;

/**
 * Interface that defines the shape of a pluggable resource cache
 * for the included ResourceManager
 *
 * @version $Id: ResourceCache.java,v 1.1 2008/05/25 22:33:17 swd\stefan.damian Exp $
 */
public interface ResourceCache
{
	/**
	 * initializes the ResourceCache.  Will be
	 * called before any utilization
	 *
	 * @param rs RuntimeServices to use for logging, etc
	 */
	public void initialize(RuntimeService rs);

	/**
	 * retrieves a Resource from the
	 * cache
	 *
	 * @param resourceKey key for Resource to be retrieved
	 * @return Resource specified or null if not found
	 */
	public Resource get(Object resourceKey);

	/**
	 * stores a Resource in the cache
	 *
	 * @param resourceKey key to associate with the Resource
	 * @param resource    Resource to be stored
	 * @return existing Resource stored under this key, or null if none
	 */
	public Resource put(Object resourceKey, Resource resource);

	/**
	 * removes a Resource from the cache
	 *
	 * @param resourceKey resource to be removed
	 */
	public Resource remove(Object resourceKey);

	/**
	 * returns an Iterator of Keys in the cache
	 */
	public Iterator enumerateKeys();
}
