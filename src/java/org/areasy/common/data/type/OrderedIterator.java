package org.areasy.common.data.type;

/*
 * Copyright (c) 2007-2015 AREasy Runtime
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

import java.util.Iterator;

/**
 * Defines an iterator that operates over a ordered collections.
 * <p/>
 * This iterator allows both forward and reverse iteration through the collection.
 *
 * @version $Id: OrderedIterator.java,v 1.2 2008/05/14 09:32:34 swd\stefan.damian Exp $
 */
public interface OrderedIterator extends Iterator
{

	/**
	 * Checks to see if there is a previous entry that can be iterated to.
	 *
	 * @return <code>true</code> if the iterator has a previous element
	 */
	boolean hasPrevious();

	/**
	 * Gets the previous element from the collection.
	 *
	 * @return the previous key in the iteration
	 * @throws java.util.NoSuchElementException
	 *          if the iteration is finished
	 */
	Object previous();

}
