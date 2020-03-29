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
import org.areasy.common.data.type.MapIterator;
import org.areasy.common.data.type.ResettableIterator;
import org.areasy.common.data.type.collection.AbstractCollectionDecorator;
import org.areasy.common.data.type.iterator.AbstractIteratorDecorator;
import org.areasy.common.data.type.map.keyvalue.AbstractMapEntryDecorator;

import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

/**
 * Abstract <code>BidiMap</code> implemented using two maps.
 * <p/>
 * An implementation can be written simply by implementing the
 * <code>createMap</code> method.
 *
 * @version $Id: AbstractDualBidimensionalMap.java,v 1.2 2008/05/14 09:32:31 swd\stefan.damian Exp $
 * @see DualHashBidimensionalMap
 * @see DualTreeBidimensionalMap
 */
public abstract class AbstractDualBidimensionalMap implements BidimensionalMap
{
	/**
	 * Delegate map array.  The first map contains standard entries, and the
	 * second contains inverses.
	 */
	protected transient final Map[] maps = new Map[2];
	/**
	 * Inverse view of this map.
	 */
	protected transient BidimensionalMap inverseBidimensionalMap = null;
	/**
	 * View of the keys.
	 */
	protected transient Set keySet = null;
	/**
	 * View of the values.
	 */
	protected transient Collection values = null;
	/**
	 * View of the entries.
	 */
	protected transient Set entrySet = null;

	/**
	 * Creates an empty map, initialised by <code>createMap</code>.
	 * <p/>
	 * This constructor remains in place for deserialization.
	 * All other usage is deprecated in favour of
	 * {@link #AbstractDualBidimensionalMap(Map, Map)}.
	 */
	protected AbstractDualBidimensionalMap()
	{
		super();
		maps[0] = new HashedMap();
		maps[1] = new HashedMap();
	}

	/**
	 * Creates an empty map using the two maps specified as storage.
	 * <p/>
	 * The two maps must be a matching pair, normal and reverse.
	 * They will typically both be empty.
	 * <p/>
	 * Neither map is validated, so nulls may be passed in.
	 * If you choose to do this then the subclass constructor must populate
	 * the <code>maps[]</code> instance variable itself.
	 *
	 * @param normalMap  the normal direction map
	 * @param reverseMap the reverse direction map
	 */
	protected AbstractDualBidimensionalMap(Map normalMap, Map reverseMap)
	{
		super();
		maps[0] = normalMap;
		maps[1] = reverseMap;
	}

	/**
	 * Constructs a map that decorates the specified maps,
	 * used by the subclass <code>createBidiMap</code> implementation.
	 *
	 * @param normalMap               the normal direction map
	 * @param reverseMap              the reverse direction map
	 * @param inverseBidimensionalMap the inverse BidiMap
	 */
	protected AbstractDualBidimensionalMap(Map normalMap, Map reverseMap, BidimensionalMap inverseBidimensionalMap)
	{
		super();
		maps[0] = normalMap;
		maps[1] = reverseMap;
		this.inverseBidimensionalMap = inverseBidimensionalMap;
	}

	/**
	 * Creates a new instance of the subclass.
	 *
	 * @param normalMap  the normal direction map
	 * @param reverseMap the reverse direction map
	 * @param inverseMap this map, which is the inverse in the new map
	 * @return the inverse map
	 */
	protected abstract BidimensionalMap createBidiMap(Map normalMap, Map reverseMap, BidimensionalMap inverseMap);

	// Map delegation
	public Object get(Object key)
	{
		return maps[0].get(key);
	}

	public int size()
	{
		return maps[0].size();
	}

	public boolean isEmpty()
	{
		return maps[0].isEmpty();
	}

	public boolean containsKey(Object key)
	{
		return maps[0].containsKey(key);
	}

	public boolean equals(Object obj)
	{
		return maps[0].equals(obj);
	}

	public int hashCode()
	{
		return maps[0].hashCode();
	}

	public String toString()
	{
		return maps[0].toString();
	}

	// BidiMap changes
	public Object put(Object key, Object value)
	{
		if (maps[0].containsKey(key)) maps[1].remove(maps[0].get(key));

		if (maps[1].containsKey(value)) maps[0].remove(maps[1].get(value));

		final Object obj = maps[0].put(key, value);
		maps[1].put(value, key);

		return obj;
	}

	public void putAll(Map map)
	{
		for (Iterator it = map.entrySet().iterator(); it.hasNext();)
		{
			Map.Entry entry = (Map.Entry) it.next();
			put(entry.getKey(), entry.getValue());
		}
	}

	public Object remove(Object key)
	{
		Object value = null;
		if (maps[0].containsKey(key))
		{
			value = maps[0].remove(key);
			maps[1].remove(value);
		}

		return value;
	}

	public void clear()
	{
		maps[0].clear();
		maps[1].clear();
	}

	public boolean containsValue(Object value)
	{
		return maps[1].containsKey(value);
	}

	/**
	 * Obtains a <code>MapIterator</code> over the map.
	 * The iterator implements <code>ResetableMapIterator</code>.
	 * This implementation relies on the entrySet iterator.
	 * <p/>
	 * The setValue() methods only allow a new value to be set.
	 * If the value being set is already in the map, an IllegalArgumentException
	 * is thrown (as setValue cannot change the size of the map).
	 *
	 * @return a map iterator
	 */
	public MapIterator mapIterator()
	{
		return new BidiMapIterator(this);
	}

	public Object getKey(Object value)
	{
		return maps[1].get(value);
	}

	public Object removeValue(Object value)
	{
		Object key = null;
		if (maps[1].containsKey(value))
		{
			key = maps[1].remove(value);
			maps[0].remove(key);
		}

		return key;
	}

	public BidimensionalMap inverseBidiMap()
	{
		if (inverseBidimensionalMap == null) inverseBidimensionalMap = createBidiMap(maps[1], maps[0], this);

		return inverseBidimensionalMap;
	}

	/**
	 * Gets a keySet view of the map.
	 * Changes made on the view are reflected in the map.
	 * The set supports remove and clear but not add.
	 *
	 * @return the keySet view
	 */
	public Set keySet()
	{
		if (keySet == null) keySet = new KeySet(this);

		return keySet;
	}

	/**
	 * Creates a key set iterator.
	 * Subclasses can override this to return iterators with different properties.
	 *
	 * @param iterator the iterator to decorate
	 * @return the keySet iterator
	 */
	protected Iterator createKeySetIterator(Iterator iterator)
	{
		return new KeySetIterator(iterator, this);
	}

	/**
	 * Gets a values view of the map.
	 * Changes made on the view are reflected in the map.
	 * The set supports remove and clear but not add.
	 *
	 * @return the values view
	 */
	public Collection values()
	{
		if (values == null) values = new Values(this);

		return values;
	}

	/**
	 * Creates a values iterator.
	 * Subclasses can override this to return iterators with different properties.
	 *
	 * @param iterator the iterator to decorate
	 * @return the values iterator
	 */
	protected Iterator createValuesIterator(Iterator iterator)
	{
		return new ValuesIterator(iterator, this);
	}

	/**
	 * Gets an entrySet view of the map.
	 * Changes made on the set are reflected in the map.
	 * The set supports remove and clear but not add.
	 * <p/>
	 * The Map Entry setValue() method only allow a new value to be set.
	 * If the value being set is already in the map, an IllegalArgumentException
	 * is thrown (as setValue cannot change the size of the map).
	 *
	 * @return the entrySet view
	 */
	public Set entrySet()
	{
		if (entrySet == null) entrySet = new EntrySet(this);

		return entrySet;
	}

	/**
	 * Creates an entry set iterator.
	 * Subclasses can override this to return iterators with different properties.
	 *
	 * @param iterator the iterator to decorate
	 * @return the entrySet iterator
	 */
	protected Iterator createEntrySetIterator(Iterator iterator)
	{
		return new EntrySetIterator(iterator, this);
	}

	/**
	 * Inner class View.
	 */
	protected static abstract class View extends AbstractCollectionDecorator
	{

		/**
		 * The parent map
		 */
		protected final AbstractDualBidimensionalMap parent;

		/**
		 * Constructs a new view of the BidiMap.
		 *
		 * @param coll   the collection view being decorated
		 * @param parent the parent BidiMap
		 */
		protected View(Collection coll, AbstractDualBidimensionalMap parent)
		{
			super(coll);
			this.parent = parent;
		}

		public boolean removeAll(Collection coll)
		{
			if (parent.isEmpty() || coll.isEmpty())
			{
				return false;
			}
			boolean modified = false;
			Iterator it = iterator();
			while (it.hasNext())
			{
				if (coll.contains(it.next()))
				{
					it.remove();
					modified = true;
				}
			}
			return modified;
		}

		public boolean retainAll(Collection coll)
		{
			if (parent.isEmpty())
			{
				return false;
			}
			if (coll.isEmpty())
			{
				parent.clear();
				return true;
			}
			boolean modified = false;
			Iterator it = iterator();
			while (it.hasNext())
			{
				if (coll.contains(it.next()) == false)
				{
					it.remove();
					modified = true;
				}
			}
			return modified;
		}

		public void clear()
		{
			parent.clear();
		}
	}

	/**
	 * Inner class KeySet.
	 */
	protected static class KeySet extends View implements Set
	{

		/**
		 * Constructs a new view of the BidiMap.
		 *
		 * @param parent the parent BidiMap
		 */
		protected KeySet(AbstractDualBidimensionalMap parent)
		{
			super(parent.maps[0].keySet(), parent);
		}

		public Iterator iterator()
		{
			return parent.createKeySetIterator(super.iterator());
		}

		public boolean contains(Object key)
		{
			return parent.maps[0].containsKey(key);
		}

		public boolean remove(Object key)
		{
			if (parent.maps[0].containsKey(key))
			{
				Object value = parent.maps[0].remove(key);
				parent.maps[1].remove(value);
				return true;
			}
			return false;
		}
	}

	/**
	 * Inner class KeySetIterator.
	 */
	protected static class KeySetIterator extends AbstractIteratorDecorator
	{

		/**
		 * The parent map
		 */
		protected final AbstractDualBidimensionalMap parent;
		/**
		 * The last returned key
		 */
		protected Object lastKey = null;
		/**
		 * Whether remove is allowed at present
		 */
		protected boolean canRemove = false;

		/**
		 * Constructor.
		 *
		 * @param iterator the iterator to decorate
		 * @param parent   the parent map
		 */
		protected KeySetIterator(Iterator iterator, AbstractDualBidimensionalMap parent)
		{
			super(iterator);
			this.parent = parent;
		}

		public Object next()
		{
			lastKey = super.next();
			canRemove = true;
			return lastKey;
		}

		public void remove()
		{
			if (canRemove == false)
			{
				throw new IllegalStateException("Iterator remove() can only be called once after next()");
			}
			Object value = parent.maps[0].get(lastKey);
			super.remove();
			parent.maps[1].remove(value);
			lastKey = null;
			canRemove = false;
		}
	}

	/**
	 * Inner class Values.
	 */
	protected static class Values extends View implements Set
	{

		/**
		 * Constructs a new view of the BidiMap.
		 *
		 * @param parent the parent BidiMap
		 */
		protected Values(AbstractDualBidimensionalMap parent)
		{
			super(parent.maps[0].values(), parent);
		}

		public Iterator iterator()
		{
			return parent.createValuesIterator(super.iterator());
		}

		public boolean contains(Object value)
		{
			return parent.maps[1].containsKey(value);
		}

		public boolean remove(Object value)
		{
			if (parent.maps[1].containsKey(value))
			{
				Object key = parent.maps[1].remove(value);
				parent.maps[0].remove(key);
				return true;
			}
			return false;
		}
	}

	/**
	 * Inner class ValuesIterator.
	 */
	protected static class ValuesIterator extends AbstractIteratorDecorator
	{

		/**
		 * The parent map
		 */
		protected final AbstractDualBidimensionalMap parent;
		/**
		 * The last returned value
		 */
		protected Object lastValue = null;
		/**
		 * Whether remove is allowed at present
		 */
		protected boolean canRemove = false;

		/**
		 * Constructor.
		 *
		 * @param iterator the iterator to decorate
		 * @param parent   the parent map
		 */
		protected ValuesIterator(Iterator iterator, AbstractDualBidimensionalMap parent)
		{
			super(iterator);
			this.parent = parent;
		}

		public Object next()
		{
			lastValue = super.next();
			canRemove = true;
			return lastValue;
		}

		public void remove()
		{
			if (canRemove == false)
			{
				throw new IllegalStateException("Iterator remove() can only be called once after next()");
			}
			super.remove(); // removes from maps[0]
			parent.maps[1].remove(lastValue);
			lastValue = null;
			canRemove = false;
		}
	}

	/**
	 * Inner class EntrySet.
	 */
	protected static class EntrySet extends View implements Set
	{

		/**
		 * Constructs a new view of the BidiMap.
		 *
		 * @param parent the parent BidiMap
		 */
		protected EntrySet(AbstractDualBidimensionalMap parent)
		{
			super(parent.maps[0].entrySet(), parent);
		}

		public Iterator iterator()
		{
			return parent.createEntrySetIterator(super.iterator());
		}

		public boolean remove(Object obj)
		{
			if (obj instanceof Map.Entry == false)
			{
				return false;
			}
			Map.Entry entry = (Map.Entry) obj;
			Object key = entry.getKey();
			if (parent.containsKey(key))
			{
				Object value = parent.maps[0].get(key);
				if (value == null ? entry.getValue() == null : value.equals(entry.getValue()))
				{
					parent.maps[0].remove(key);
					parent.maps[1].remove(value);
					return true;
				}
			}
			return false;
		}
	}

	/**
	 * Inner class EntrySetIterator.
	 */
	protected static class EntrySetIterator extends AbstractIteratorDecorator
	{

		/**
		 * The parent map
		 */
		protected final AbstractDualBidimensionalMap parent;
		/**
		 * The last returned entry
		 */
		protected Map.Entry last = null;
		/**
		 * Whether remove is allowed at present
		 */
		protected boolean canRemove = false;

		/**
		 * Constructor.
		 *
		 * @param iterator the iterator to decorate
		 * @param parent   the parent map
		 */
		protected EntrySetIterator(Iterator iterator, AbstractDualBidimensionalMap parent)
		{
			super(iterator);
			this.parent = parent;
		}

		public Object next()
		{
			last = new MapEntry((Map.Entry) super.next(), parent);
			canRemove = true;
			return last;
		}

		public void remove()
		{
			if (canRemove == false)
			{
				throw new IllegalStateException("Iterator remove() can only be called once after next()");
			}
			// store value as remove may change the entry in the decorator (eg.TreeMap)
			Object value = last.getValue();
			super.remove();
			parent.maps[1].remove(value);
			last = null;
			canRemove = false;
		}
	}

	/**
	 * Inner class MapEntry.
	 */
	protected static class MapEntry extends AbstractMapEntryDecorator
	{

		/**
		 * The parent map
		 */
		protected final AbstractDualBidimensionalMap parent;

		/**
		 * Constructor.
		 *
		 * @param entry  the entry to decorate
		 * @param parent the parent map
		 */
		protected MapEntry(Map.Entry entry, AbstractDualBidimensionalMap parent)
		{
			super(entry);
			this.parent = parent;
		}

		public Object setValue(Object value)
		{
			Object key = MapEntry.this.getKey();
			if (parent.maps[1].containsKey(value) &&
					parent.maps[1].get(value) != key)
			{
				throw new IllegalArgumentException("Cannot use setValue() when the object being set is already in the map");
			}
			parent.put(key, value);
			final Object oldValue = super.setValue(value);
			return oldValue;
		}
	}

	/**
	 * Inner class MapIterator.
	 */
	protected static class BidiMapIterator implements MapIterator, ResettableIterator
	{

		/**
		 * The parent map
		 */
		protected final AbstractDualBidimensionalMap parent;
		/**
		 * The iterator being wrapped
		 */
		protected Iterator iterator;
		/**
		 * The last returned entry
		 */
		protected Map.Entry last = null;
		/**
		 * Whether remove is allowed at present
		 */
		protected boolean canRemove = false;

		/**
		 * Constructor.
		 *
		 * @param parent the parent map
		 */
		protected BidiMapIterator(AbstractDualBidimensionalMap parent)
		{
			super();
			this.parent = parent;
			this.iterator = parent.maps[0].entrySet().iterator();
		}

		public boolean hasNext()
		{
			return iterator.hasNext();
		}

		public Object next()
		{
			last = (Map.Entry) iterator.next();
			canRemove = true;
			return last.getKey();
		}

		public void remove()
		{
			if (canRemove == false)
			{
				throw new IllegalStateException("Iterator remove() can only be called once after next()");
			}
			// store value as remove may change the entry in the decorator (eg.TreeMap)
			Object value = last.getValue();
			iterator.remove();
			parent.maps[1].remove(value);
			last = null;
			canRemove = false;
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
			iterator = parent.maps[0].entrySet().iterator();
			last = null;
			canRemove = false;
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

}
