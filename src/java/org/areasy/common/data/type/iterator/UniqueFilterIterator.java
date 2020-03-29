package org.areasy.common.data.type.iterator;

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

import org.areasy.common.data.workers.functors.UniquePredicate;

import java.util.Iterator;

/**
 * A FilterIterator which only returns "unique" Objects.  Internally,
 * the Iterator maintains a Set of objects it has already encountered,
 * and duplicate Objects are skipped.
 *
 * @version $Id: UniqueFilterIterator.java,v 1.2 2008/05/14 09:32:40 swd\stefan.damian Exp $
 */
public class UniqueFilterIterator extends FilterIterator
{


	/**
	 * Constructs a new <code>UniqueFilterIterator</code>.
	 *
	 * @param iterator the iterator to use
	 */
	public UniqueFilterIterator(Iterator iterator)
	{
		super(iterator, UniquePredicate.getInstance());
	}

}
