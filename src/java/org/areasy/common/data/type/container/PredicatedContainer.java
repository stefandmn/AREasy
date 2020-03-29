package org.areasy.common.data.type.container;

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

import org.areasy.common.data.type.Container;
import org.areasy.common.data.type.Predicate;
import org.areasy.common.data.type.collection.PredicatedCollection;

import java.util.Set;

/**
 * Decorates another <code>Bag</code> to validate that additions
 * match a specified predicate.
 * <p/>
 * This bag exists to provide validation for the decorated bag.
 * It is normally created to decorate an empty bag.
 * If an object cannot be added to the bag, an IllegalArgumentException is thrown.
 * <p/>
 * One usage would be to ensure that no null entries are added to the bag.
 * <pre>Bag bag = PredicatedBag.decorate(new HashBag(), NotNullPredicate.INSTANCE);</pre>
 *
 * @version $Id: PredicatedContainer.java,v 1.2 2008/05/14 09:32:35 swd\stefan.damian Exp $
 */
public class PredicatedContainer extends PredicatedCollection implements Container
{
	/**
	 * Factory method to create a predicated (validating) bag.
	 * <p/>
	 * If there are any elements already in the bag being decorated, they
	 * are validated.
	 *
	 * @param container the bag to decorate, must not be null
	 * @param predicate the predicate to use for validation, must not be null
	 * @return a new predicated Bag
	 * @throws IllegalArgumentException if bag or predicate is null
	 * @throws IllegalArgumentException if the bag contains invalid elements
	 */
	public static Container decorate(Container container, Predicate predicate)
	{
		return new PredicatedContainer(container, predicate);
	}

	/**
	 * Constructor that wraps (not copies).
	 * <p/>
	 * If there are any elements already in the bag being decorated, they
	 * are validated.
	 *
	 * @param container the bag to decorate, must not be null
	 * @param predicate the predicate to use for validation, must not be null
	 * @throws IllegalArgumentException if bag or predicate is null
	 * @throws IllegalArgumentException if the bag contains invalid elements
	 */
	protected PredicatedContainer(Container container, Predicate predicate)
	{
		super(container, predicate);
	}

	/**
	 * Gets the decorated bag.
	 *
	 * @return the decorated bag
	 */
	protected Container getBag()
	{
		return (Container) getCollection();
	}

	public boolean add(Object object, int count)
	{
		validate(object);
		return getBag().add(object, count);
	}

	public boolean remove(Object object, int count)
	{
		return getBag().remove(object, count);
	}

	public Set uniqueSet()
	{
		return getBag().uniqueSet();
	}

	public int getCount(Object object)
	{
		return getBag().getCount(object);
	}

}
