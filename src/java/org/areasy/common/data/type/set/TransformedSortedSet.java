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

import org.areasy.common.data.type.Transformer;

import java.util.Comparator;
import java.util.SortedSet;

/**
 * Decorates another <code>SortedSet</code> to transform objects that are added.
 * <p/>
 * The add methods are affected by this class.
 * Thus objects must be removed or searched for using their transformed form.
 * For example, if the transformation converts Strings to Integers, you must
 * use the Integer form to remove objects.
 *
 * @version $Id: TransformedSortedSet.java,v 1.2 2008/05/14 09:32:37 swd\stefan.damian Exp $
 */
public class TransformedSortedSet extends TransformedSet implements SortedSet
{
	/**
	 * Factory method to create a transforming sorted set.
	 * <p/>
	 * If there are any elements already in the set being decorated, they
	 * are NOT transformed.
	 *
	 * @param set         the set to decorate, must not be null
	 * @param transformer the transformer to use for conversion, must not be null
	 * @throws IllegalArgumentException if set or transformer is null
	 */
	public static SortedSet decorate(SortedSet set, Transformer transformer)
	{
		return new TransformedSortedSet(set, transformer);
	}

	/**
	 * Constructor that wraps (not copies).
	 * <p/>
	 * If there are any elements already in the set being decorated, they
	 * are NOT transformed.
	 *
	 * @param set         the set to decorate, must not be null
	 * @param transformer the transformer to use for conversion, must not be null
	 * @throws IllegalArgumentException if set or transformer is null
	 */
	protected TransformedSortedSet(SortedSet set, Transformer transformer)
	{
		super(set, transformer);
	}

	/**
	 * Gets the decorated set.
	 *
	 * @return the decorated set
	 */
	protected SortedSet getSortedSet()
	{
		return (SortedSet) collection;
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

	public SortedSet subSet(Object fromElement, Object toElement)
	{
		SortedSet set = getSortedSet().subSet(fromElement, toElement);
		return new TransformedSortedSet(set, transformer);
	}

	public SortedSet headSet(Object toElement)
	{
		SortedSet set = getSortedSet().headSet(toElement);
		return new TransformedSortedSet(set, transformer);
	}

	public SortedSet tailSet(Object fromElement)
	{
		SortedSet set = getSortedSet().tailSet(fromElement);
		return new TransformedSortedSet(set, transformer);
	}

}
