package org.areasy.common.data.type.map;

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

import org.areasy.common.data.type.BidimensionalMap;
import org.areasy.common.data.type.MapIterator;
import org.areasy.common.data.type.Unmodifiable;
import org.areasy.common.data.type.collection.UnmodifiableCollection;
import org.areasy.common.data.type.iterator.UnmodifiableMapIterator;
import org.areasy.common.data.type.set.UnmodifiableSet;

import java.util.Collection;
import java.util.Map;
import java.util.Set;

/**
 * Decorates another <code>BidiMap</code> to ensure it can't be altered.
 *
 * @version $Id: UnmodifiableBidimensionalMap.java,v 1.2 2008/05/14 09:32:30 swd\stefan.damian Exp $
 */
public final class UnmodifiableBidimensionalMap
		extends AbstractBidimensionalMapDecorator implements Unmodifiable
{

	/**
	 * The inverse unmodifiable map
	 */
	private UnmodifiableBidimensionalMap inverse;

	/**
	 * Factory method to create an unmodifiable map.
	 * <p/>
	 * If the map passed in is already unmodifiable, it is returned.
	 *
	 * @param map the map to decorate, must not be null
	 * @return an unmodifiable BidiMap
	 * @throws IllegalArgumentException if map is null
	 */
	public static BidimensionalMap decorate(BidimensionalMap map)
	{
		if (map instanceof Unmodifiable)
		{
			return map;
		}
		return new UnmodifiableBidimensionalMap(map);
	}

	/**
	 * Constructor that wraps (not copies).
	 *
	 * @param map the map to decorate, must not be null
	 * @throws IllegalArgumentException if map is null
	 */
	private UnmodifiableBidimensionalMap(BidimensionalMap map)
	{
		super(map);
	}

	public void clear()
	{
		throw new UnsupportedOperationException();
	}

	public Object put(Object key, Object value)
	{
		throw new UnsupportedOperationException();
	}

	public void putAll(Map mapToCopy)
	{
		throw new UnsupportedOperationException();
	}

	public Object remove(Object key)
	{
		throw new UnsupportedOperationException();
	}

	public Set entrySet()
	{
		Set set = super.entrySet();
		return UnmodifiableEntrySet.decorate(set);
	}

	public Set keySet()
	{
		Set set = super.keySet();
		return UnmodifiableSet.decorate(set);
	}

	public Collection values()
	{
		Collection coll = super.values();
		return UnmodifiableCollection.decorate(coll);
	}

	public Object removeValue(Object value)
	{
		throw new UnsupportedOperationException();
	}

	public MapIterator mapIterator()
	{
		MapIterator it = getBidiMap().mapIterator();
		return UnmodifiableMapIterator.decorate(it);
	}

	public BidimensionalMap inverseBidiMap()
	{
		if (inverse == null)
		{
			inverse = new UnmodifiableBidimensionalMap(getBidiMap().inverseBidiMap());
			inverse.inverse = this;
		}
		return inverse;
	}

}
