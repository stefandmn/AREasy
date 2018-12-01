package org.areasy.common.data.type.set;

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

import org.areasy.common.data.type.collection.AbstractCollectionDecorator;

import java.util.Set;

/**
 * Decorates another <code>Set</code> to provide additional behaviour.
 * <p/>
 * Methods are forwarded directly to the decorated set.
 *
 * @version $Id: AbstractSetDecorator.java,v 1.2 2008/05/14 09:32:37 swd\stefan.damian Exp $
 */
public abstract class AbstractSetDecorator extends AbstractCollectionDecorator implements Set
{

	/**
	 * Constructor only used in deserialization, do not use otherwise.
	 *
	 */
	protected AbstractSetDecorator()
	{
		super();
	}

	/**
	 * Constructor that wraps (not copies).
	 *
	 * @param set the set to decorate, must not be null
	 * @throws IllegalArgumentException if set is null
	 */
	protected AbstractSetDecorator(Set set)
	{
		super(set);
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
