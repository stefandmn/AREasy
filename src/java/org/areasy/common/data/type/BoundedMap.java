package org.areasy.common.data.type;

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

import java.util.Map;

/**
 * Defines a map that is bounded in size.
 * <p/>
 * The size of the map can vary, but it can never exceed a preset
 * maximum number of elements. This interface allows the querying of details
 * associated with the maximum number of elements.
 *
 * @version $Id: BoundedMap.java,v 1.2 2008/05/14 09:32:35 swd\stefan.damian Exp $
 */
public interface BoundedMap extends Map
{

	/**
	 * Returns true if this map is full and no new elements can be added.
	 *
	 * @return <code>true</code> if the map is full
	 */
	boolean isFull();

	/**
	 * Gets the maximum size of the map (the bound).
	 *
	 * @return the maximum number of elements the map can hold
	 */
	int maxSize();

}
