package org.areasy.common.data.type.set;

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

import org.areasy.common.data.type.Predicate;

import java.util.Comparator;
import java.util.SortedSet;

/**
 * Decorates another <code>SortedSet</code> to validate that all additions
 * match a specified predicate.
 * <p/>
 * This set exists to provide validation for the decorated set.
 * It is normally created to decorate an empty set.
 * If an object cannot be added to the set, an IllegalArgumentException is thrown.
 * <p/>
 * One usage would be to ensure that no null entries are added to the set.
 * <pre>SortedSet set = PredicatedSortedSet.decorate(new TreeSet(), NotNullPredicate.INSTANCE);</pre>
 *
 * @version $Id: PredicatedSortedSet.java,v 1.2 2008/05/14 09:32:37 swd\stefan.damian Exp $
 */
public class PredicatedSortedSet extends PredicatedSet implements SortedSet
{
	/**
	 * Factory method to create a predicated (validating) sorted set.
	 * <p/>
	 * If there are any elements already in the set being decorated, they
	 * are validated.
	 *
	 * @param set       the set to decorate, must not be null
	 * @param predicate the predicate to use for validation, must not be null
	 * @throws IllegalArgumentException if set or predicate is null
	 * @throws IllegalArgumentException if the set contains invalid elements
	 */
	public static SortedSet decorate(SortedSet set, Predicate predicate)
	{
		return new PredicatedSortedSet(set, predicate);
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
	protected PredicatedSortedSet(SortedSet set, Predicate predicate)
	{
		super(set, predicate);
	}

	/**
	 * Gets the sorted set being decorated.
	 *
	 * @return the decorated sorted set
	 */
	private SortedSet getSortedSet()
	{
		return (SortedSet) getCollection();
	}

	public SortedSet subSet(Object fromElement, Object toElement)
	{
		SortedSet sub = getSortedSet().subSet(fromElement, toElement);
		return new PredicatedSortedSet(sub, predicate);
	}

	public SortedSet headSet(Object toElement)
	{
		SortedSet sub = getSortedSet().headSet(toElement);
		return new PredicatedSortedSet(sub, predicate);
	}

	public SortedSet tailSet(Object fromElement)
	{
		SortedSet sub = getSortedSet().tailSet(fromElement);
		return new PredicatedSortedSet(sub, predicate);
	}

	public Object first()
	{
		return getSortedSet().first();
	}

	public Object last()
	{
		return getSortedSet().last();
	}

	public Comparator comparator()
	{
		return getSortedSet().comparator();
	}

}
