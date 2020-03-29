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

import java.io.Serializable;

/**
 * This class is the abstract base class for all conventional
 * Velocity Context  implementations.  Simply extend this class
 * and implement the abstract routines that access your preferred
 * storage method.
 * <p/>
 * Takes care of context chaining.
 * <p/>
 * Also handles / enforces policy on null keys and values :
 * <p/>
 * <ul>
 * <li> Null keys and values are accepted and basically dropped.
 * <li> If you place an object into the context with a null key, it
 * will be ignored and logged.
 * <li> If you try to place a null into the context with any key, it
 * will be dropped and logged.
 * </ul>
 * <p/>
 * The default implementation of this for application use is
 * org.areasy.common.parser.engines.velocity.context.VelocityContext.
 * <p/>
 * All thanks to Fedor for the chaining idea.
 *
 * @version $Id: MainContext.java,v 1.1 2008/05/25 22:33:10 swd\stefan.damian Exp $
 */

public abstract class MainContext extends InternalContextBase implements Context, Serializable
{
	/**
	 * the chained Context if any
	 */
	private Context innerContext = null;

	/**
	 * Implement to return a value from the context storage.
	 * <br><br>
	 * The implementation of this method is required for proper
	 * operation of a Context implementation in general
	 * Velocity use.
	 *
	 * @param key key whose associated value is to be returned
	 * @return object stored in the context
	 */
	public abstract Object internalGet(String key);

	/**
	 * Implement to put a value into the context storage.
	 * <br><br>
	 * The implementation of this method is required for
	 * proper operation of a Context implementation in
	 * general Velocity use.
	 *
	 * @param key   key with which to associate the value
	 * @param value value to be associated with the key
	 * @return previously stored value if exists, or null
	 */
	public abstract Object internalPut(String key, Object value);

	/**
	 * Implement to determine if a key is in the storage.
	 * <br><br>
	 * Currently, this method is not used internally by
	 * the Velocity core.
	 *
	 * @param key key to test for existance
	 * @return true if found, false if not
	 */
	public abstract boolean internalContainsKey(Object key);

	/**
	 * Implement to return an object array of key
	 * strings from your storage.
	 * <br><br>
	 * Currently, this method is not used internally by
	 * the Velocity core.
	 *
	 * @return array of keys
	 */
	public abstract Object[] internalGetKeys();

	/**
	 * I mplement to remove an item from your storage.
	 * <br><br>
	 * Currently, this method is not used internally by
	 * the Velocity core.
	 *
	 * @param key key to remove
	 * @return object removed if exists, else null
	 */
	public abstract Object internalRemove(Object key);

    /**
     * Removes all mappings from this map (optional operation).
     *
     * @throws UnsupportedOperationException clear is not supported by this map.
     */
	public abstract void clearInternal();

	/**
	 * default CTOR
	 */
	public MainContext()
	{
		//nothing to do here.
	}

	/**
	 * Chaining constructor accepts a Context argument.
	 * It will relay get() operations into this Context
	 * in the even the 'local' get() returns null.
	 *
	 * @param inner context to be chained
	 */
	public MainContext(Context inner)
	{
		innerContext = inner;

		if (innerContext instanceof InternalEventContext) attachEventCartridge(((InternalEventContext) innerContext).getEventCartridge());
	}

	/**
	 * Adds a name/value pair to the context.
	 *
	 * @param key   The name to key the provided value with.
	 * @param value The corresponding value.
	 * @return Object that was replaced in the the Context if
	 *         applicable or null if not.
	 */
	public Object put(String key, Object value)
	{
		if (key == null) return null;
			else if (value == null) return null;

		return internalPut(key, value);
	}

	/**
	 * Gets the value corresponding to the provided key from the context.
	 * <p/>
	 * Supports the chaining context mechanism.  If the 'local' context
	 * doesn't have the value, we try to get it from the chained context.
	 *
	 * @param key The name of the desired value.
	 * @return The value corresponding to the provided key or null if
	 *         the key param is null.
	 */
	public Object get(String key)
	{
		if (key == null) return null;

		Object o = internalGet(key);

		if (o == null && innerContext != null) o = innerContext.get(key);

		return o;
	}

	/**
	 * Indicates whether the specified key is in the context.  Provided for
	 * debugging purposes.
	 *
	 * @param key The key to look for.
	 * @return true if the key is in the context, false if not.
	 */
	public boolean containsKey(Object key)
	{
		if (key == null) return false;

		return internalContainsKey(key);
	}

	/**
	 * Get all the keys for the values in the context
	 *
	 * @return Object[] of keys in the Context. Does not return
	 *         keys in chained context.
	 */
	public Object[] getKeys()
	{
		return internalGetKeys();
	}

	/**
	 * Removes the value associated with the specified key from the context.
	 *
	 * @param key The name of the value to remove.
	 * @return The value that the key was mapped to, or <code>null</code>
	 *         if unmapped.
	 */
	public Object remove(Object key)
	{
		if (key == null) return null;

		return internalRemove(key);
	}

	/**
	 * returns innerContext if one is chained
	 *
	 * @return Context if chained, <code>null</code> if not
	 */
	public Context getChainedContext()
	{
		return innerContext;
	}

    /**
     * Removes all mappings from this map (optional operation).
     *
     * @throws UnsupportedOperationException clear is not supported by this map.
     */
	public void clear()
	{
		clearInternal();
	}
}



