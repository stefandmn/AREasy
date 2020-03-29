package org.areasy.common.velocity.context;

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

import java.util.HashMap;
import java.util.Map;

/**
 * General purpose implemention of the application Context
 * interface for general application use.  This class should
 * be used in place of the original Context class.
 * <p/>
 * This implementation uses a HashMap  (@see java.util.HashMap )
 * for data storage.
 * <p/>
 * This context implementation cannot be shared between threads
 * without those threads synchronizing access between them, as
 * the HashMap is not synchronized, nor are some of the fundamentals
 * of AbstractContext.  If you need to share a Context between
 * threads with simultaneous access for some reason, please create
 * your own and extend the interface Context
 *
 * @version $Id: VelocityContext.java,v 1.4 2008/05/25 22:33:09 swd\stefan.damian Exp $
 * @see org.areasy.common.velocity.context.Context
 */
public class VelocityContext extends MainContext implements Cloneable
{
	/**
	 * Storage for key/value pairs.
	 */
	private Map context = null;

	/**
	 * Creates a new instance (with no inner context).
	 */
	public VelocityContext()
	{
		this(null, null);
	}

	/**
	 * Creates a new instance with the provided storage (and no inner
	 * context).
	 */
	public VelocityContext(Map context)
	{
		this(context, null);
	}

	/**
	 * Chaining constructor, used when you want to
	 * wrap a context in another.  The inner context
	 * will be 'read only' - put() calls to the
	 * wrapping context will only effect the outermost
	 * context
	 *
	 * @param innerContext The <code>Context</code> implementation to
	 *                     wrap.
	 */
	public VelocityContext(Context innerContext)
	{
		this(null, innerContext);
	}

	/**
	 * Initializes internal storage (never to <code>null</code>), and
	 * inner context.
	 *
	 * @param context      Internal storage, or <code>null</code> to
	 *                     create default storage.
	 * @param innerContext Inner context.
	 */
	public VelocityContext(Map context, Context innerContext)
	{
		super(innerContext);
		this.context = (context == null ? new HashMap() : context);
	}

	/**
	 * retrieves value for key from internal
	 * storage
	 *
	 * @param key name of value to get
	 * @return value as object
	 */
	public Object internalGet(String key)
	{
		return context.get(key);
	}

	/**
	 * stores the value for key to internal
	 * storage
	 *
	 * @param key   name of value to store
	 * @param value value to store
	 * @return previous value of key as Object
	 */
	public Object internalPut(String key, Object value)
	{
		return context.put(key, value);
	}

	/**
	 * determines if there is a value for the
	 * given key
	 *
	 * @param key name of value to check
	 * @return true if non-null value in store
	 */
	public boolean internalContainsKey(Object key)
	{
		return context.containsKey(key);
	}

	/**
	 * returns array of keys
	 *
	 * @return keys as []
	 */
	public Object[] internalGetKeys()
	{
		return context.keySet().toArray();
	}

	/**
	 * remove a key/value pair from the
	 * internal storage
	 *
	 * @param key name of value to remove
	 * @return value removed
	 */
	public Object internalRemove(Object key)
	{
		return context.remove(key);
	}

    /**
     * Removes all mappings from this map (optional operation).
     *
     * @throws UnsupportedOperationException clear is not supported by this map.
     */
	public void clearInternal()
	{
		context.clear();
	}

	/**
	 * Clones this context object.
	 *
	 * @return A deep copy of this <code>Context</code>.
	 */
	public Object clone()
	{
		VelocityContext clone = null;
		
		try
		{
			clone = (VelocityContext) super.clone();
			clone.context = new HashMap(context);
		}
		catch (CloneNotSupportedException ignored)
		{
		}

		return clone;
	}
}
