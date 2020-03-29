package org.areasy.common.data.type.list;

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

import java.util.ArrayList;

/**
 * List with unique entries. UniqueList does not allow null nor duplicates.
 *
 * @version $Id: UniqueList.java,v 1.2 2008/05/14 09:32:35 swd\stefan.damian Exp $
 */
public class UniqueList extends ArrayList
{
	/**
	 * Adds an Object to the list.
	 *
	 * @param o the Object to add
	 * @return true if the Object is added
	 */
	public boolean add(Object o)
	{
		if (o != null && !contains(o)) return super.add(o);
		return false;
	}
}
