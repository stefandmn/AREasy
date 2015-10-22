package org.areasy.common.velocity.runtime.resource;

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

import org.areasy.common.logger.Logger;
import org.areasy.common.logger.LoggerFactory;
import org.areasy.common.velocity.runtime.RuntimeService;

import java.util.Hashtable;
import java.util.Iterator;
import java.util.Map;

/**
 * Default implementation of the resource cache for the default
 * ResourceManager.
 *
 * @version $Id: DefaultResourceCache.java,v 1.1 2008/05/25 22:33:17 swd\stefan.damian Exp $
 */
public class DefaultResourceCache implements ResourceCache
{
	/** the logger */
	private static Logger logger = LoggerFactory.getLog(DefaultResourceCache.class.getName());

	/**
	 * Cache storage, assumed to be thread-safe.
	 */
	protected Map cache = new Hashtable();

	/**
	 * Runtime services, generally initialized by the
	 * <code>initialize()</code> method.
	 */
	protected RuntimeService rsvc = null;

	public void initialize(RuntimeService rs)
	{
		rsvc = rs;

		logger.debug("Resource cache initialized. (" + this.getClass() + ")");
	}

	public Resource get(Object key)
	{
		return (Resource) cache.get(key);
	}

	public Resource put(Object key, Resource value)
	{
		return (Resource) cache.put(key, value);
	}

	public Resource remove(Object key)
	{
		return (Resource) cache.remove(key);
	}

	public Iterator enumerateKeys()
	{
		return cache.keySet().iterator();
	}
}
