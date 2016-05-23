package org.areasy.common.data.type;

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

import java.util.Comparator;

/**
 * Defines a type of <code>Bag</code> that maintains a sorted order among
 * its unique representative members.
 *
 * @version $Id: SortedContainer.java,v 1.2 2008/05/14 09:32:34 swd\stefan.damian Exp $
 */
public interface SortedContainer extends Container
{

	/**
	 * Returns the comparator associated with this sorted set, or null
	 * if it uses its elements' natural ordering.
	 *
	 * @return the comparator in use, or null if natural ordering
	 */
	public Comparator comparator();

	/**
	 * Returns the first (lowest) member.
	 *
	 * @return the first element in the sorted bag
	 */
	public Object first();

	/**
	 * Returns the last (highest) member.
	 *
	 * @return the last element in the sorted bag
	 */
	public Object last();

}
