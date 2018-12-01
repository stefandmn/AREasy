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

import org.areasy.common.data.type.Container;
import org.areasy.common.data.type.collection.AbstractCollectionDecorator;

import java.util.Set;

/**
 * Decorates another <code>Bag</code> to provide additional behaviour.
 * <p/>
 * Methods are forwarded directly to the decorated bag.
 *
 * @version $Id: AbstractContainerDecorator.java,v 1.2 2008/05/14 09:32:35 swd\stefan.damian Exp $
 */
public abstract class AbstractContainerDecorator
		extends AbstractCollectionDecorator implements Container
{

	/**
	 * Constructor only used in deserialization, do not use otherwise.
	 *
	 */
	protected AbstractContainerDecorator()
	{
		super();
	}

	/**
	 * Constructor that wraps (not copies).
	 *
	 * @param container the bag to decorate, must not be null
	 * @throws IllegalArgumentException if list is null
	 */
	protected AbstractContainerDecorator(Container container)
	{
		super(container);
	}

	/**
	 * Gets the bag being decorated.
	 *
	 * @return the decorated bag
	 */
	protected Container getBag()
	{
		return (Container) getCollection();
	}

	public int getCount(Object object)
	{
		return getBag().getCount(object);
	}

	public boolean add(Object object, int count)
	{
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

}
