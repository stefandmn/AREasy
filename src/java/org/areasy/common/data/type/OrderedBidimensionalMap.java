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

/**
 * Defines a map that allows bidirectional lookup between key and values
 * and retains and provides access to an ordering.
 * <p/>
 * Implementations should allow a value to be looked up from a key and
 * a key to be looked up from a value with equal performance.
 *
 * @version $Id: OrderedBidimensionalMap.java,v 1.2 2008/05/14 09:32:34 swd\stefan.damian Exp $
 */
public interface OrderedBidimensionalMap extends BidimensionalMap, OrderedMap
{

	/**
	 * Gets a view of this map where the keys and values are reversed.
	 * <p/>
	 * Changes to one map will be visible in the other and vice versa.
	 * This enables both directions of the map to be accessed equally.
	 * <p/>
	 * Implementations should seek to avoid creating a new object every time this
	 * method is called. See <code>AbstractMap.values()</code> etc. Calling this
	 * method on the inverse map should return the original.
	 * <p/>
	 * Implementations must return an <code>OrderedBidiMap</code> instance,
	 * usually by forwarding to <code>inverseOrderedBidiMap()</code>.
	 *
	 * @return an inverted bidirectional map
	 */
	public BidimensionalMap inverseBidiMap();

	/**
	 * Gets a view of this map where the keys and values are reversed.
	 * <p/>
	 * Changes to one map will be visible in the other and vice versa.
	 * This enables both directions of the map to be accessed equally.
	 * <p/>
	 * Implementations should seek to avoid creating a new object every time this
	 * method is called. See <code>AbstractMap.values()</code> etc. Calling this
	 * method on the inverse map should return the original.
	 *
	 * @return an inverted bidirectional map
	 */
	public OrderedBidimensionalMap inverseOrderedBidiMap();

}
