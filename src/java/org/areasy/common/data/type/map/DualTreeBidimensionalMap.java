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

import org.areasy.common.data.type.*;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.*;

/**
 * Implementation of <code>BidiMap</code> that uses two <code>TreeMap</code> instances.
 * <p/>
 * The setValue() method on iterators will succeed only if the new value being set is
 * not already in the bidimap.
 * <p/>
 * When considering whether to use this class, the {@link TreeBidimensionalMap} class should
 * also be considered. It implements the interface using a dedicated design, and does
 * not store each object twice, which can save on memory use.
 *
 * @version $Id: DualTreeBidimensionalMap.java,v 1.2 2008/05/14 09:32:30 swd\stefan.damian Exp $
 */
public class DualTreeBidimensionalMap extends AbstractDualBidimensionalMap implements SortedBidimensionalMap, Serializable
{
	/**
	 * The comparator to use
	 */
	protected final Comparator comparator;

	/**
	 * Creates an empty <code>DualTreeBidiMap</code>
	 */
	public DualTreeBidimensionalMap()
	{
		super(new TreeMap(), new TreeMap());
		this.comparator = null;
	}

	/**
	 * Constructs a <code>DualTreeBidiMap</code> and copies the mappings from
	 * specified <code>Map</code>.
	 *
	 * @param map the map whose mappings are to be placed in this map
	 */
	public DualTreeBidimensionalMap(Map map)
	{
		super(new TreeMap(), new TreeMap());
		putAll(map);
		this.comparator = null;
	}

	/**
	 * Constructs a <code>DualTreeBidiMap</code> using the specified Comparator.
	 *
	 * @param comparator the Comparator
	 */
	public DualTreeBidimensionalMap(Comparator comparator)
	{
		super(new TreeMap(comparator), new TreeMap(comparator));
		this.comparator = comparator;
	}

	/**
	 * Constructs a <code>DualTreeBidiMap</code> that decorates the specified maps.
	 *
	 * @param normalMap               the normal direction map
	 * @param reverseMap              the reverse direction map
	 * @param inverseBidimensionalMap the inverse BidiMap
	 */
	protected DualTreeBidimensionalMap(Map normalMap, Map reverseMap, BidimensionalMap inverseBidimensionalMap)
	{
		super(normalMap, reverseMap, inverseBidimensionalMap);
		this.comparator = ((SortedMap) normalMap).comparator();
	}

	/**
	 * Creates a new instance of this object.
	 *
	 * @param normalMap  the normal direction map
	 * @param reverseMap the reverse direction map
	 * @param inverseMap the inverse BidiMap
	 * @return new bidi map
	 */
	protected BidimensionalMap createBidiMap(Map normalMap, Map reverseMap, BidimensionalMap inverseMap)
	{
		return new DualTreeBidimensionalMap(normalMap, reverseMap, inverseMap);
	}

	public Comparator comparator()
	{
		return ((SortedMap) maps[0]).comparator();
	}

	public Object firstKey()
	{
		return ((SortedMap) maps[0]).firstKey();
	}

	public Object lastKey()
	{
		return ((SortedMap) maps[0]).lastKey();
	}

	public Object nextKey(Object key)
	{
		if (isEmpty())
		{
			return null;
		}
		if (maps[0] instanceof OrderedMap)
		{
			return ((OrderedMap) maps[0]).nextKey(key);
		}
		SortedMap sm = (SortedMap) maps[0];
		Iterator it = sm.tailMap(key).keySet().iterator();
		it.next();
		if (it.hasNext())
		{
			return it.next();
		}
		return null;
	}

	public Object previousKey(Object key)
	{
		if (isEmpty())
		{
			return null;
		}
		if (maps[0] instanceof OrderedMap)
		{
			return ((OrderedMap) maps[0]).previousKey(key);
		}
		SortedMap sm = (SortedMap) maps[0];
		SortedMap hm = sm.headMap(key);
		if (hm.isEmpty())
		{
			return null;
		}
		return hm.lastKey();
	}

	/**
	 * Obtains an ordered map iterator.
	 * <p/>
	 * This implementation copies the elements to an ArrayList in order to
	 * provide the forward/backward behaviour.
	 *
	 * @return a new ordered map iterator
	 */
	public OrderedMapIterator orderedMapIterator()
	{
		return new BidiOrderedMapIterator(this);
	}

	public SortedBidimensionalMap inverseSortedBidiMap()
	{
		return (SortedBidimensionalMap) inverseBidiMap();
	}

	public OrderedBidimensionalMap inverseOrderedBidiMap()
	{
		return (OrderedBidimensionalMap) inverseBidiMap();
	}

	public SortedMap headMap(Object toKey)
	{
		SortedMap sub = ((SortedMap) maps[0]).headMap(toKey);
		return new ViewMap(this, sub);
	}

	public SortedMap tailMap(Object fromKey)
	{
		SortedMap sub = ((SortedMap) maps[0]).tailMap(fromKey);
		return new ViewMap(this, sub);
	}

	public SortedMap subMap(Object fromKey, Object toKey)
	{
		SortedMap sub = ((SortedMap) maps[0]).subMap(fromKey, toKey);
		return new ViewMap(this, sub);
	}

	/**
	 * Internal sorted map view.
	 */
	protected static class ViewMap extends AbstractSortedMapDecorator
	{
		/**
		 * The parent bidi map.
		 */
		final DualTreeBidimensionalMap bidi;

		/**
		 * Constructor.
		 *
		 * @param bidi the parent bidi map
		 * @param sm   the subMap sorted map
		 */
		protected ViewMap(DualTreeBidimensionalMap bidi, SortedMap sm)
		{
			// the implementation is not great here...
			// use the maps[0] as the filtered map, but maps[1] as the full map
			// this forces containsValue and clear to be overridden
			super((SortedMap) bidi.createBidiMap(sm, bidi.maps[1], bidi.inverseBidimensionalMap));
			this.bidi = (DualTreeBidimensionalMap) map;
		}

		public boolean containsValue(Object value)
		{
			// override as default implementation jumps to [1]
			return bidi.maps[0].containsValue(value);
		}

		public void clear()
		{
			// override as default implementation jumps to [1]
			for (Iterator it = keySet().iterator(); it.hasNext();)
			{
				it.next();
				it.remove();
			}
		}

		public SortedMap headMap(Object toKey)
		{
			return new ViewMap(bidi, super.headMap(toKey));
		}

		public SortedMap tailMap(Object fromKey)
		{
			return new ViewMap(bidi, super.tailMap(fromKey));
		}

		public SortedMap subMap(Object fromKey, Object toKey)
		{
			return new ViewMap(bidi, super.subMap(fromKey, toKey));
		}
	}

	/**
	 * Inner class MapIterator.
	 */
	protected static class BidiOrderedMapIterator implements OrderedMapIterator, ResettableIterator
	{

		/**
		 * The parent map
		 */
		protected final AbstractDualBidimensionalMap parent;
		/**
		 * The iterator being decorated
		 */
		protected ListIterator iterator;
		/**
		 * The last returned entry
		 */
		private Map.Entry last = null;

		/**
		 * Constructor.
		 *
		 * @param parent the parent map
		 */
		protected BidiOrderedMapIterator(AbstractDualBidimensionalMap parent)
		{
			super();
			this.parent = parent;
			iterator = new ArrayList(parent.entrySet()).listIterator();
		}

		public boolean hasNext()
		{
			return iterator.hasNext();
		}

		public Object next()
		{
			last = (Map.Entry) iterator.next();
			return last.getKey();
		}

		public boolean hasPrevious()
		{
			return iterator.hasPrevious();
		}

		public Object previous()
		{
			last = (Map.Entry) iterator.previous();
			return last.getKey();
		}

		public void remove()
		{
			iterator.remove();
			parent.remove(last.getKey());
			last = null;
		}

		public Object getKey()
		{
			if (last == null)
			{
				throw new IllegalStateException("Iterator getKey() can only be called after next() and before remove()");
			}
			return last.getKey();
		}

		public Object getValue()
		{
			if (last == null)
			{
				throw new IllegalStateException("Iterator getValue() can only be called after next() and before remove()");
			}
			return last.getValue();
		}

		public Object setValue(Object value)
		{
			if (last == null)
			{
				throw new IllegalStateException("Iterator setValue() can only be called after next() and before remove()");
			}
			if (parent.maps[1].containsKey(value) &&
					parent.maps[1].get(value) != last.getKey())
			{
				throw new IllegalArgumentException("Cannot use setValue() when the object being set is already in the map");
			}
			return parent.put(last.getKey(), value);
		}

		public void reset()
		{
			iterator = new ArrayList(parent.entrySet()).listIterator();
			last = null;
		}

		public String toString()
		{
			if (last != null)
			{
				return "MapIterator[" + getKey() + "=" + getValue() + "]";
			}
			else
			{
				return "MapIterator[]";
			}
		}
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
		maps[0] = new TreeMap(comparator);
		maps[1] = new TreeMap(comparator);
		Map map = (Map) in.readObject();
		putAll(map);
	}

}
