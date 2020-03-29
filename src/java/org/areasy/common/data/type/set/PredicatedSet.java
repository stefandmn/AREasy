package org.areasy.common.data.type.set;

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

import org.areasy.common.data.type.Predicate;
import org.areasy.common.data.type.collection.PredicatedCollection;

import java.util.Set;

/**
 * Decorates another <code>Set</code> to validate that all additions
 * match a specified predicate.
 * <p/>
 * This set exists to provide validation for the decorated set.
 * It is normally created to decorate an empty set.
 * If an object cannot be added to the set, an IllegalArgumentException is thrown.
 * <p/>
 * One usage would be to ensure that no null entries are added to the set.
 * <pre>Set set = PredicatedSet.decorate(new HashSet(), NotNullPredicate.INSTANCE);</pre>
 *
 * @version $Id: PredicatedSet.java,v 1.2 2008/05/14 09:32:37 swd\stefan.damian Exp $
 */
public class PredicatedSet extends PredicatedCollection implements Set
{
	/**
	 * Factory method to create a predicated (validating) set.
	 * <p/>
	 * If there are any elements already in the set being decorated, they
	 * are validated.
	 *
	 * @param set       the set to decorate, must not be null
	 * @param predicate the predicate to use for validation, must not be null
	 * @throws IllegalArgumentException if set or predicate is null
	 * @throws IllegalArgumentException if the set contains invalid elements
	 */
	public static Set decorate(Set set, Predicate predicate)
	{
		return new PredicatedSet(set, predicate);
	}

	/**
	 * Constructor that wraps (not copies).
	 * <p/>
	 * If there are any elements already in the set being decorated, they
	 * are validated.
	 *
	 * @param set       the set to decorate, must not be null
	 * @param predicate the predicate to use for validation, must not be null
	 * @throws IllegalArgumentException if set or predicate is null
	 * @throws IllegalArgumentException if the set contains invalid elements
	 */
	protected PredicatedSet(Set set, Predicate predicate)
	{
		super(set, predicate);
	}

	/**
	 * Gets the set being decorated.
	 *
	 * @return the decorated set
	 */
	protected Set getSet()
	{
		return (Set) getCollection();
	}

}
