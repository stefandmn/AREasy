package org.areasy.common.data.type.map;

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

import org.areasy.common.data.type.BidimensionalMap;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

/**
 * Implementation of <code>BidiMap</code> that uses two <code>HashMap</code> instances.
 * <p/>
 * Two <code>HashMap</code> instances are used in this class.
 * This provides fast lookups at the expense of storing two sets of map entries.
 *
 * @version $Id: DualHashBidimensionalMap.java,v 1.2 2008/05/14 09:32:30 swd\stefan.damian Exp $
 */
public class DualHashBidimensionalMap extends AbstractDualBidimensionalMap implements Serializable
{
	/**
	 * Creates an empty <code>HashBidiMap</code>.
	 */
	public DualHashBidimensionalMap()
	{
		super(new HashMap(), new HashMap());
	}

	/**
	 * Constructs a <code>HashBidiMap</code> and copies the mappings from
	 * specified <code>Map</code>.
	 *
	 * @param map the map whose mappings are to be placed in this map
	 */
	public DualHashBidimensionalMap(Map map)
	{
		super(new HashMap(), new HashMap());
		putAll(map);
	}

	/**
	 * Constructs a <code>HashBidiMap</code> that decorates the specified maps.
	 *
	 * @param normalMap               the normal direction map
	 * @param reverseMap              the reverse direction map
	 * @param inverseBidimensionalMap the inverse BidiMap
	 */
	protected DualHashBidimensionalMap(Map normalMap, Map reverseMap, BidimensionalMap inverseBidimensionalMap)
	{
		super(normalMap, reverseMap, inverseBidimensionalMap);
	}

	/**
	 * Creates a new instance of this object.
	 *
	 * @param normalMap               the normal direction map
	 * @param reverseMap              the reverse direction map
	 * @param inverseBidimensionalMap the inverse BidiMap
	 * @return new bidi map
	 */
	protected BidimensionalMap createBidiMap(Map normalMap, Map reverseMap, BidimensionalMap inverseBidimensionalMap)
	{
		return new DualHashBidimensionalMap(normalMap, reverseMap, inverseBidimensionalMap);
	}

	// Serialization
	private void writeObject(ObjectOutputStream out) throws IOException
	{
		out.defaultWriteObject();
		out.writeObject(maps[0]);
	}

	private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException
	{
		in.defaultReadObject();
		maps[0] = new HashMap();
		maps[1] = new HashMap();
		Map map = (Map) in.readObject();
		putAll(map);
	}

}
