package org.areasy.common.velocity.context;

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

/**
 * Interface describing the application data context.  This set of
 * routines is used by the application to set and remove 'named' data
 * object to pass them to the template engine to use when rendering
 * a template.
 * <p/>
 * This is the same set of methods supported by the original Context
 * class
 *
 * @version $Id: Context.java,v 1.1 2008/05/25 22:33:10 swd\stefan.damian Exp $
 * @see org.areasy.common.velocity.context.MainContext
 * @see org.areasy.common.velocity.context.VelocityContext
 */
public interface Context
{
	/**
	 * Adds a name/value pair to the context.
	 *
	 * @param key   The name to key the provided value with.
	 * @param value The corresponding value.
	 */
	Object put(String key, Object value);

	/**
	 * Gets the value corresponding to the provided key from the context.
	 *
	 * @param key The name of the desired value.
	 * @return The value corresponding to the provided key.
	 */
	Object get(String key);

	/**
	 * Indicates whether the specified key is in the context.
	 *
	 * @param key The key to look for.
	 * @return Whether the key is in the context.
	 */
	boolean containsKey(Object key);

	/**
	 * Get all the keys for the values in the context
	 */
	Object[] getKeys();

	/**
	 * Removes the value associated with the specified key from the context.
	 *
	 * @param key The name of the value to remove.
	 * @return The value that the key was mapped to, or <code>null</code>
	 *         if unmapped.
	 */
	Object remove(Object key);

    /**
     * Removes all mappings from this map (optional operation).
     *
     * @throws UnsupportedOperationException clear is not supported by this map.
     */
	void clear();
}
