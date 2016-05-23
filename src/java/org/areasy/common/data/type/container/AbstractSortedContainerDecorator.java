package org.areasy.common.data.type.container;

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

import org.areasy.common.data.type.SortedContainer;

import java.util.Comparator;

/**
 * Decorates another <code>SortedBag</code> to provide additional behaviour.
 * <p/>
 * Methods are forwarded directly to the decorated bag.
 *
 * @version $Id: AbstractSortedContainerDecorator.java,v 1.2 2008/05/14 09:32:35 swd\stefan.damian Exp $
 */
public abstract class AbstractSortedContainerDecorator
		extends AbstractContainerDecorator implements SortedContainer
{

	/**
	 * Constructor only used in deserialization, do not use otherwise.
	 *
	 */
	protected AbstractSortedContainerDecorator()
	{
		super();
	}

	/**
	 * Constructor that wraps (not copies).
	 *
	 * @param bag the bag to decorate, must not be null
	 * @throws IllegalArgumentException if list is null
	 */
	protected AbstractSortedContainerDecorator(SortedContainer bag)
	{
		super(bag);
	}

	/**
	 * Gets the bag being decorated.
	 *
	 * @return the decorated bag
	 */
	protected SortedContainer getSortedBag()
	{
		return (SortedContainer) getCollection();
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
