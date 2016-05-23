package org.areasy.common.data.type.iterator;

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

import org.areasy.common.data.type.OrderedMapIterator;
import org.areasy.common.data.type.ResettableIterator;

/**
 * Provides an implementation of an empty ordered map iterator.
 *
 * @version $Id: EmptyOrderedMapIterator.java,v 1.2 2008/05/14 09:32:40 swd\stefan.damian Exp $
 */
public class EmptyOrderedMapIterator extends AbstractEmptyIterator implements OrderedMapIterator, ResettableIterator
{

	/**
	 * Singleton instance of the iterator.
	 *
	 */
	public static final OrderedMapIterator INSTANCE = new EmptyOrderedMapIterator();

	/**
	 * Constructor.
	 */
	protected EmptyOrderedMapIterator()
	{
		super();
	}

}
