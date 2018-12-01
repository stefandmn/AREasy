package org.areasy.common.data.type.container;

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

import org.areasy.common.data.type.SortedContainer;
import org.areasy.common.data.type.Transformer;

import java.util.Comparator;

/**
 * Decorates another <code>SortedBag</code> to transform objects that are added.
 * <p/>
 * The add methods are affected by this class.
 * Thus objects must be removed or searched for using their transformed form.
 * For example, if the transformation converts Strings to Integers, you must
 * use the Integer form to remove objects.
 *
 * @version $Id: TransformedSortedContainer.java,v 1.2 2008/05/14 09:32:35 swd\stefan.damian Exp $
 */
public class TransformedSortedContainer extends TransformedContainer implements SortedContainer
{
	/**
	 * Factory method to create a transforming sorted bag.
	 * <p/>
	 * If there are any elements already in the bag being decorated, they
	 * are NOT transformed.
	 *
	 * @param bag         the bag to decorate, must not be null
	 * @param transformer the transformer to use for conversion, must not be null
	 * @return a new transformed SortedBag
	 * @throws IllegalArgumentException if bag or transformer is null
	 */
	public static SortedContainer decorate(SortedContainer bag, Transformer transformer)
	{
		return new TransformedSortedContainer(bag, transformer);
	}

	/**
	 * Constructor that wraps (not copies).
	 * <p/>
	 * If there are any elements already in the bag being decorated, they
	 * are NOT transformed.
	 *
	 * @param bag         the bag to decorate, must not be null
	 * @param transformer the transformer to use for conversion, must not be null
	 * @throws IllegalArgumentException if bag or transformer is null
	 */
	protected TransformedSortedContainer(SortedContainer bag, Transformer transformer)
	{
		super(bag, transformer);
	}

	/**
	 * Gets the decorated bag.
	 *
	 * @return the decorated bag
	 */
	protected SortedContainer getSortedBag()
	{
		return (SortedContainer) collection;
	}

	public Object first()
	{
		return getSortedBag().first();
	}

	public Object last()
	{
		return getSortedBag().last();
	}

	public Comparator comparator()
	{
		return getSortedBag().comparator();
	}

}
