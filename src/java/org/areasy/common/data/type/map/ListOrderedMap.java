package org.areasy.common.data.type.map;

/*
 * Copyright (c) 2007-2016 AREasy Runtime
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

import org.areasy.common.data.type.MapIterator;
import org.areasy.common.data.type.OrderedMap;
import org.areasy.common.data.type.OrderedMapIterator;
import org.areasy.common.data.type.ResettableIterator;
import org.areasy.common.data.type.iterator.AbstractIteratorDecorator;
import org.areasy.common.data.type.list.UnmodifiableList;
import org.areasy.common.data.type.map.keyvalue.AbstractMapEntry;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.*;

/**
 * Decorates a <code>Map</code> to ensure that the order of addition is retained
 * using a <code>List</code> to maintain order.
 * <p/>
 * The order will be used via the iterators and toArray methods on the views.
 * The order is also returned by the <code>MapIterator</code>.
 * The <code>orderedMapIterator()</code> method accesses an iterator that can
 * iterate both forwards and backwards through the map.
 * In addition, non-interface methods are provided to access the map by index.
 * <p/>
 * If an object is added to the Map for a second time, it will remain in the
 * original position in the iteration.
 *
 * @version $Id: ListOrderedMap.java,v 1.4 2008/05/21 07:05:08 swd\stefan.damian Exp $
 */
public class ListOrderedMap extends AbstractMapDecorator implements OrderedMap, Serializable
{
	/**
	 * Internal list to hold the sequence of objects
	 */
	protected final List insertOrder = new ArrayList();

	/**
	 * Factory method to create an ordered map.
	 * <p/>
	 * An <code>ArrayList</code> is used to retain order.
	 *
	 * @param map the map to decorate, must not be null
	 * @throws IllegalArgumentException if map is null
	 */
	public static OrderedMap decorate(Map map)
	{
		return new ListOrderedMap(map);
	}

	/**
	 * Constructs a new empty <code>ListOrderedMap</code> that decorates
	 * a <code>HashMap</code>.
	 *
	 */
	public ListOrderedMap()
	{
		this(new HashMap());
	}

	/**
	 * Constructor that wraps (not copies).
	 *
	 * @param map the map to decorate, must not be null
	 * @throws IllegalArgumentException if map is null
	 */
	protected ListOrderedMap(Map map)
	{
		super(map);
		insertOrder.addAll(getMap().keySet());
	}

	/**
	 * Write the map out using a custom routine.
	 *
	 * @param out the output stream
	 * @throws IOException
	 */
	private void writeObject(ObjectOutputStream out) throws IOException
	{
		out.defaultWriteObject();
		out.writeObject(map);
	}

	/**
	 * Read the map in using a custom routine.
	 *
	 * @param in the input stream
	 * @throws IOException
	 * @throws ClassNotFoundException
	 */
	private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException
	{
		in.defaultReadObject();
		map = (Map) in.readObject();
	}

	// Implement OrderedMap
	public MapIterator mapIterator()
	{
		return orderedMapIterator();
	}

	public OrderedMapIterator orderedMapIterator()
	{
		return new ListOrderedMapIterator(this);
	}

	/**
	 * Gets the first key in this map by insert order.
	 *
	 * @return the first key currently in this map
	 * @throws NoSuchElementException if this map is empty
	 */
	public Object firstKey()
	{
		if (size() == 0) throw new NoSuchElementException("Map is empty");

		return insertOrder.get(0);
	}

	/**
	 * Gets the last key in this map by insert order.
	 *
	 * @return the last key currently in this map
	 * @throws NoSuchElementException if this map is empty
	 */
	public Object lastKey()
	{
		if (size() == 0) throw new NoSuchElementException("Map is empty");

		return insertOrder.get(size() - 1);
	}

	/**
	 * Gets the next key to the one specified using insert order.
	 * This method performs a list search to find the key and is O(n).
	 *
	 * @param key the key to find previous for
	 * @return the next key, null if no match or at start
	 */
	public Object nextKey(Object key)
	{
		int index = insertOrder.indexOf(key);
		if (index >= 0 && index < size() - 1) return insertOrder.get(index + 1);

		return null;
	}

	/**
	 * Gets the previous key to the one specified using insert order.
	 * This method performs a list search to find the key and is O(n).
	 *
	 * @param key the key to find previous for
	 * @return the previous key, null if no match or at start
	 */
	public Object previousKey(Object key)
	{
		int index = insertOrder.indexOf(key);
		if (index > 0) return insertOrder.get(index - 1);

		return null;
	}

	public Object put(Object key, Object value)
	{
		if (getMap().containsKey(key))
		{
			// re-adding doesn't change order
			return getMap().put(key, value);
		}
		else
		{
			// first add, so add to both map and list
			Object result = getMap().put(key, value);
			insertOrder.add(key);
			
			return result;
		}
	}

	public Object put(int index, Object key, Object value)
	{
		if (getMap().containsKey(key))
		{
			// re-adding doesn't change order
			return getMap().put(key, value);
		}
		else
		{
			// first add, so add to both map and list
			Object result = getMap().put(key, value);
			if(index < insertOrder.size()) insertOrder.add(index, key);

			return result;
		}
	}

	public Object putFirst(Object key, Object value)
	{
		if (getMap().containsKey(key))
		{
			// re-adding doesn't change order
			insertOrder.remove(key);
			insertOrder.add(0, key);
			
			return getMap().put(key, value);
		}
		else
		{
			// first add, so add to both map and list
			Object result = getMap().put(key, value);
			insertOrder.add(0, key);

			return result;
		}
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
		Object result = getMap().remove(key);
		insertOrder.remove(key);

		return result;
	}

	public void clear()
	{
		getMap().clear();
		insertOrder.clear();
	}

	public Set keySet()
	{
		return new KeySetView(this);
	}

	public Collection values()
	{
		return new ValuesView(this);
	}

	public Set entrySet()
	{
		return new EntrySetView(this, this.insertOrder);
	}

	/**
	 * Returns the Map as a string.
	 *
	 * @return the Map as a String
	 */
	public String toString()
	{
		if (isEmpty()) return "{}";

		StringBuffer buf = new StringBuffer();
		buf.append('{');

		boolean first = true;
		Iterator it = entrySet().iterator();

		while (it.hasNext())
		{
			Map.Entry entry = (Map.Entry) it.next();
			Object key = entry.getKey();
			Object value = entry.getValue();

			if (first) first = false;
				else buf.append(", ");

			buf.append(key == this ? "(this Map)" : key);
			buf.append('=');
			buf.append(value == this ? "(this Map)" : value);
		}

		buf.append('}');

		return buf.toString();
	}

	/**
	 * Gets the key at the specified index.
	 *
	 * @param index the index to retrieve
	 * @return the key at the specified index
	 * @throws IndexOutOfBoundsException if the index is invalid
	 */
	public Object get(int index)
	{
		return insertOrder.get(index);
	}

	/**
	 * Gets the value at the specified index.
	 *
	 * @param index the index to retrieve
	 * @return the key at the specified index
	 * @throws IndexOutOfBoundsException if the index is invalid
	 */
	public Object getValue(int index)
	{
		return get(insertOrder.get(index));
	}

	/**
	 * Gets the index of the specified key.
	 *
	 * @param key the key to find the index of
	 * @return the index, or -1 if not found
	 */
	public int indexOf(Object key)
	{
		return insertOrder.indexOf(key);
	}

	/**
	 * Removes the element at the specified index.
	 *
	 * @param index the index of the object to remove
	 * @return the previous value corresponding the <code>key</code>,
	 *         or <code>null</code> if none existed
	 * @throws IndexOutOfBoundsException if the index is invalid
	 */
	public Object remove(int index)
	{
		return remove(get(index));
	}

	/**
	 * Gets an unmodifiable List view of the keys which changes as the map changes.
	 * <p/>
	 * The returned list is unmodifiable because changes to the values of
	 * the list (using {@link java.util.ListIterator#set(Object)}) will
	 * effectively remove the value from the list and reinsert that value at
	 * the end of the list, which is an unexpected side effect of changing the
	 * value of a list.  This occurs because changing the key, changes when the
	 * mapping is added to the map and thus where it appears in the list.
	 * <p/>
	 * An alternative to this method is to use {@link #keySet()}.
	 *
	 * @return The ordered list of keys.
	 * @see #keySet()
	 */
	public List asList()
	{
		return UnmodifiableList.decorate(insertOrder);
	}

	static class ValuesView extends AbstractCollection
	{
		private final ListOrderedMap parent;

		ValuesView(ListOrderedMap parent)
		{
			super();
			this.parent = parent;
		}

		public int size()
		{
			return this.parent.size();
		}

		public boolean contains(Object value)
		{
			return this.parent.containsValue(value);
		}

		public void clear()
		{
			this.parent.clear();
		}

		public Iterator iterator()
		{
			return new AbstractIteratorDecorator(parent.entrySet().iterator())
			{
				public Object next()
				{
					return ((Map.Entry) iterator.next()).getValue();
				}
			};
		}
	}

	static class KeySetView extends AbstractSet
	{
		private final ListOrderedMap parent;

		KeySetView(ListOrderedMap parent)
		{
			super();
			this.parent = parent;
		}

		public int size()
		{
			return this.parent.size();
		}

		public boolean contains(Object value)
		{
			return this.parent.containsKey(value);
		}

		public void clear()
		{
			this.parent.clear();
		}

		public Iterator iterator()
		{
			return new AbstractIteratorDecorator(parent.entrySet().iterator())
			{
				public Object next()
				{
					return ((Map.Entry) super.next()).getKey();
				}
			};
		}
	}

	static class EntrySetView extends AbstractSet
	{
		private final ListOrderedMap parent;
		private final List insertOrder;
		private Set entrySet;

		public EntrySetView(ListOrderedMap parent, List insertOrder)
		{
			super();
			this.parent = parent;
			this.insertOrder = insertOrder;
		}

		private Set getEntrySet()
		{
			if (entrySet == null)
			{
				entrySet = parent.getMap().entrySet();
			}

			return entrySet;
		}

		public int size()
		{
			return this.parent.size();
		}

		public boolean isEmpty()
		{
			return this.parent.isEmpty();
		}

		public boolean contains(Object obj)
		{
			return getEntrySet().contains(obj);
		}

		public boolean containsAll(Collection coll)
		{
			return getEntrySet().containsAll(coll);
		}

		public boolean remove(Object obj)
		{
			if (!(obj instanceof Map.Entry)) return false;

			if (getEntrySet().contains(obj))
			{
				Object key = ((Map.Entry) obj).getKey();
				parent.remove(key);

				return true;
			}

			return false;
		}

		public void clear()
		{
			this.parent.clear();
		}

		public boolean equals(Object obj)
		{
			if (obj == this) return true;

			return getEntrySet().equals(obj);
		}

		public int hashCode()
		{
			return getEntrySet().hashCode();
		}

		public String toString()
		{
			return getEntrySet().toString();
		}

		public Iterator iterator()
		{
			return new ListOrderedIterator(parent, insertOrder);
		}
	}

	static class ListOrderedIterator extends AbstractIteratorDecorator
	{
		private final ListOrderedMap parent;
		private Object last = null;

		ListOrderedIterator(ListOrderedMap parent, List insertOrder)
		{
			super(insertOrder.iterator());
			this.parent = parent;
		}

		public Object next()
		{
			last = super.next();
			return new ListOrderedMapEntry(parent, last);
		}

		public void remove()
		{
			super.remove();
			parent.getMap().remove(last);
		}
	}

	static class ListOrderedMapEntry extends AbstractMapEntry
	{
		private final ListOrderedMap parent;

		ListOrderedMapEntry(ListOrderedMap parent, Object key)
		{
			super(key, null);
			this.parent = parent;
		}

		public Object getValue()
		{
			return parent.get(key);
		}

		public Object setValue(Object value)
		{
			return parent.getMap().put(key, value);
		}
	}

	static class ListOrderedMapIterator implements OrderedMapIterator, ResettableIterator
	{
		private final ListOrderedMap parent;
		private ListIterator iterator;
		private Object last = null;
		private boolean readable = false;

		ListOrderedMapIterator(ListOrderedMap parent)
		{
			super();
			this.parent = parent;
			this.iterator = parent.insertOrder.listIterator();
		}

		public boolean hasNext()
		{
			return iterator.hasNext();
		}

		public Object next()
		{
			last = iterator.next();
			readable = true;
			return last;
		}

		public boolean hasPrevious()
		{
			return iterator.hasPrevious();
		}

		public Object previous()
		{
			last = iterator.previous();
			readable = true;
			return last;
		}

		public void remove()
		{
			if (!readable) throw new IllegalStateException(AbstractHashedMap.REMOVE_INVALID);

			iterator.remove();
			parent.map.remove(last);
			readable = false;
		}

		public Object getKey()
		{
			if (!readable) throw new IllegalStateException(AbstractHashedMap.GETKEY_INVALID);

			return last;
		}

		public Object getValue()
		{
			if (!readable) throw new IllegalStateException(AbstractHashedMap.GETVALUE_INVALID);

			return parent.get(last);
		}

		public Object setValue(Object value)
		{
			if (!readable) throw new IllegalStateException(AbstractHashedMap.SETVALUE_INVALID);

			return parent.map.put(last, value);
		}

		public void reset()
		{
			iterator = parent.insertOrder.listIterator();
			last = null;
			readable = false;
		}

		public String toString()
		{
			if (readable) return "Iterator[" + getKey() + "=" + getValue() + "]";
				else return "Iterator[]";
		}
	}

}
