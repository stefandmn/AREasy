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

import java.util.Map;

/**
 * Defines a map that can be iterated directly without needing to create an entry set.
 * <p/>
 * A map iterator is an efficient way of iterating over maps.
 * There is no need to access the entry set or cast to Map Entry objects.
 * <pre>
 * IterableMap map = new HashedMap();
 * MapIterator it = map.mapIterator();
 * while (it.hasNext()) {
 *   Object key = it.next();
 *   Object value = it.getValue();
 *   it.setValue("newValue");
 * }
 * </pre>
 *
 * @version $Id: IterableMap.java,v 1.2 2008/05/14 09:32:34 swd\stefan.damian Exp $
 */
public interface IterableMap extends Map
{
	/**
	 * Obtains a <code>MapIterator</code> over the map.
	 * <p/>
	 * A map iterator is an efficient way of iterating over maps.
	 * There is no need to access the entry set or cast to Map Entry objects.
	 * <pre>
	 * IterableMap map = new HashedMap();
	 * MapIterator it = map.mapIterator();
	 * while (it.hasNext()) {
	 *   Object key = it.next();
	 *   Object value = it.getValue();
	 *   it.setValue("newValue");
	 * }
	 * </pre>
	 *
	 * @return a map iterator
	 */
	MapIterator mapIterator();

}
