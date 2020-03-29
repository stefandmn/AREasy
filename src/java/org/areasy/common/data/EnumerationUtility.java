package org.areasy.common.data;

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

import org.areasy.common.data.type.iterator.EnumerationIterator;

import java.util.Enumeration;
import java.util.List;

/**
 * Provides utility methods for {@link Enumeration} instances.
 *
 * @version $Id: EnumerationUtility.java,v 1.3 2008/05/14 09:32:29 swd\stefan.damian Exp $
 */
public class EnumerationUtility
{
	/**
	 * EnumerationUtility is not normally instantiated.
	 */
	public EnumerationUtility()
	{
		// no init.
	}

	/**
	 * Creates a list based on an enumeration.
	 * <p/>
	 * <p>As the enumeration is traversed, an ArrayList of its values is
	 * created. The new list is returned.</p>
	 *
	 * @param enumeration the enumeration to traverse, which should not be <code>null</code>.
	 * @throws NullPointerException if the enumeration parameter is <code>null</code>.
	 */
	public static List toList(Enumeration enumeration)
	{
		return IteratorUtility.toList(new EnumerationIterator(enumeration));
	}

}
