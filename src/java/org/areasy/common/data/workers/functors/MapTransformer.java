package org.areasy.common.data.workers.functors;

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

import org.areasy.common.data.type.Transformer;

import java.io.Serializable;
import java.util.Map;

/**
 * Transformer implementation that returns the value held in a specified map
 * using the input parameter as a key.
 *
 * @version $Id: MapTransformer.java,v 1.2 2008/05/14 09:32:32 swd\stefan.damian Exp $
 */
public final class MapTransformer implements Transformer, Serializable
{
	/**
	 * The map of data to lookup in
	 */
	private final Map iMap;

	/**
	 * Factory to create the transformer.
	 * <p/>
	 * If the map is null, a transformer that always returns null is returned.
	 *
	 * @param map the map, not cloned
	 * @return the transformer
	 */
	public static Transformer getInstance(Map map)
	{
		if (map == null)
		{
			return ConstantTransformer.NULL_INSTANCE;
		}
		return new MapTransformer(map);
	}

	/**
	 * Constructor that performs no validation.
	 * Use <code>getInstance</code> if you want that.
	 *
	 * @param map the map to use for lookup, not cloned
	 */
	private MapTransformer(Map map)
	{
		super();
		iMap = map;
	}

	/**
	 * Transforms the input to result by looking it up in a <code>Map</code>.
	 *
	 * @param input the input object to transform
	 * @return the transformed result
	 */
	public Object transform(Object input)
	{
		return iMap.get(input);
	}

	/**
	 * Gets the map to lookup in.
	 *
	 * @return the map
	 */
	public Map getMap()
	{
		return iMap;
	}

}
