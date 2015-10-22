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

import java.util.*;

/**
 * <p>A customized implementation of <code>java.util.Hashtable</code> designed
 * to operate in a multithreaded environment. The specific of this class is that can simulate working with two
 * keys are manipulates <code>Map</code>s as value.</p>
 */
public class MapHashtable extends Hashtable
{
	public void put(Object key1, Object key2, Object value)
	{
		if(containsKey(key1))
		{
			Map map = (Map) super.get(key1);
			map.put(key2, value);
		}
		else
		{
			Map map = new FastHashMap();
			map.put(key2, value);
			super.put(key1, map);
		}
	}

	public Object get(Object key1, Object key2)
	{
		if(containsKey(key1))
		{
			Map map = (Map) super.get(key1);

			if(map.containsKey(key2)) return map.get(key2);
				else return null;
		}
		else return null;
	}


	public Map get(Object key1)
	{
		if(containsKey(key1)) return (Map) super.get(key1);
			else return null;
	}

	/**
	 * Clones the map without cloning the keys or values.
	 *
	 * @return a shallow clone
	 */
	public Object clone()
	{
		Map map = new MapHashtable();
		map.putAll( (Map) super.clone() );

		return map;
	}
}