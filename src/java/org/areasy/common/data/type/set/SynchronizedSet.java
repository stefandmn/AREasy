package org.areasy.common.data.type.set;

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

import org.areasy.common.data.type.collection.SynchronizedCollection;

import java.util.Set;

/**
 * Decorates another <code>Set</code> to synchronize its behaviour for a
 * multi-threaded environment.
 * <p/>
 * Methods are synchronized, then forwarded to the decorated set.
 *
 * @version $Id: SynchronizedSet.java,v 1.2 2008/05/14 09:32:37 swd\stefan.damian Exp $
 */
public class SynchronizedSet extends SynchronizedCollection implements Set
{
	/**
	 * Factory method to create a synchronized set.
	 *
	 * @param set the set to decorate, must not be null
	 * @throws IllegalArgumentException if set is null
	 */
	public static Set decorate(Set set)
	{
		return new SynchronizedSet(set);
	}

	/**
	 * Constructor that wraps (not copies).
	 *
	 * @param set the set to decorate, must not be null
	 * @throws IllegalArgumentException if set is null
	 */
	protected SynchronizedSet(Set set)
	{
		super(set);
	}

	/**
	 * Constructor that wraps (not copies).
	 *
	 * @param set  the set to decorate, must not be null
	 * @param lock the lock object to use, must not be null
	 * @throws IllegalArgumentException if set is null
	 */
	protected SynchronizedSet(Set set, Object lock)
	{
		super(set, lock);
	}

	/**
	 * Gets the decorated set.
	 *
	 * @return the decorated set
	 */
	protected Set getSet()
	{
		return (Set) collection;
	}

}
