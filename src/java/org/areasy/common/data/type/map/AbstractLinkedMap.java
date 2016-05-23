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

import org.areasy.common.data.type.*;
import org.areasy.common.data.type.iterator.EmptyOrderedIterator;
import org.areasy.common.data.type.iterator.EmptyOrderedMapIterator;

import java.util.ConcurrentModificationException;
import java.util.Iterator;
import java.util.Map;
import java.util.NoSuchElementException;

/**
 * An abstract implementation of a hash-based map that links entries to create an
 * ordered map and which provides numerous points for subclasses to override.
 * <p/>
 * This class implements all the features necessary for a subclass linked
 * hash-based map. Key-value entries are stored in instances of the
 * <code>LinkEntry</code> class which can be overridden and replaced.
 * The iterators can similarly be replaced, without the need to replace the KeySet,
 * EntrySet and Values view classes.
 * <p/>
 * Overridable methods are provided to change the default hashing behaviour, and
 * to change how entries are added to and removed from the map. Hopefully, all you
 * need for unusual subclasses is here.
 * <p/>
 * This implementation maintains order by original insertion, but subclasses
 * may work differently. The <code>OrderedMap</code> interface is implemented
 * to provide access to bidirectional iteration and extra convenience methods.
 * <p/>
 * The <code>orderedMapIterator()</code> method provides direct access to a
 * bidirectional iterator. The iterators from the other views can also be cast
 * to <code>OrderedIterator</code> if required.
 * <p/>
 * All the available iterators can be reset back to the start by casting to
 * <code>ResettableIterator</code> and calling <code>reset()</code>.
 * <p/>
 * The implementation is also designed to be subclassed, with lots of useful
 * methods exposed.
 *
 * @version $Id: AbstractLinkedMap.java,v 1.2 2008/05/14 09:32:31 swd\stefan.damian Exp $
 */
public class AbstractLinkedMap extends AbstractHashedMap implements OrderedMap
{
	/**
	 * Header in the linked list
	 */
	protected transient LinkEntry header;

	/**
	 * Constructor only used in deserialization, do not use otherwise.
	 */
	protected AbstractLinkedMap()
	{
		super();
	}

	/**
	 * Constructor which performs no validation on the passed in parameters.
	 *
	 * @param initialCapacity the initial capacity, must be a power of two
	 * @param loadFactor      the load factor, must be > 0.0f and generally < 1.0f
	 * @param threshold       the threshold, must be sensible
	 */
	protected AbstractLinkedMap(int initialCapacity, float loadFactor, int threshold)
	{
		super(initialCapacity, loadFactor, threshold);
	}

	/**
	 * Constructs a new, empty map with the specified initial capacity.
	 *
	 * @param initialCapacity the initial capacity
	 * @throws IllegalArgumentException if the initial capacity is less than one
	 */
	protected AbstractLinkedMap(int initialCapacity)
	{
		super(initialCapacity);
	}

	/**
	 * Constructs a new, empty map with the specified initial capacity and
	 * load factor.
	 *
	 * @param initialCapacity the initial capacity
	 * @param loadFactor      the load factor
	 * @throws IllegalArgumentException if the initial capacity is less than one
	 * @throws IllegalArgumentException if the load factor is less than zero
	 */
	protected AbstractLinkedMap(int initialCapacity, float loadFactor)
	{
		super(initialCapacity, loadFactor);
	}

	/**
	 * Constructor copying elements from another map.
	 *
	 * @param map the map to copy
	 * @throws NullPointerException if the map is null
	 */
	protected AbstractLinkedMap(Map map)
	{
		super(map);
	}

	/**
	 * Initialise this subclass during construction.
	 */
	protected void init()
	{
		header = new LinkEntry(null, -1, null, null);
		header.before = header.after = header;
	}

	/**
	 * Checks whether the map contains the specified value.
	 *
	 * @param value the value to search for
	 * @return true if the map contains the value
	 */
	public boolean containsValue(Object value)
	{
		// override uses faster iterator
		if (value == null)
		{
			for (LinkEntry entry = header.after; entry != header; entry = entry.after)
			{
				if (entry.getValue() == null) return true;
			}
		}
		else
		{
			for (LinkEntry entry = header.after; entry != header; entry = entry.after)
			{
				if (isEqualValue(value, entry.getValue())) return true;
			}
		}
		return false;
	}

	/**
	 * Clears the map, resetting the size to zero and nullifying references
	 * to avoid garbage collection issues.
	 */
	public void clear()
	{
		// override to reset the linked list
		super.clear();
		header.before = header.after = header;
	}

	/**
	 * Gets the first key in the map, which is the most recently inserted.
	 *
	 * @return the most recently inserted key
	 */
	public Object firstKey()
	{
		if (size == 0)
		{
			throw new NoSuchElementException("Map is empty");
		}
		return header.after.getKey();
	}

	/**
	 * Gets the last key in the map, which is the first inserted.
	 *
	 * @return the eldest key
	 */
	public Object lastKey()
	{
		if (size == 0) throw new NoSuchElementException("Map is empty");

		return header.before.getKey();
	}

	/**
	 * Gets the next key in sequence.
	 *
	 * @param key the key to get after
	 * @return the next key
	 */
	public Object nextKey(Object key)
	{
		LinkEntry entry = (LinkEntry) getEntry(key);
		return (entry == null || entry.after == header ? null : entry.after.getKey());
	}

	/**
	 * Gets the previous key in sequence.
	 *
	 * @param key the key to get before
	 * @return the previous key
	 */
	public Object previousKey(Object key)
	{
		LinkEntry entry = (LinkEntry) getEntry(key);
		return (entry == null || entry.before == header ? null : entry.before.getKey());
	}

	/**
	 * Gets the key at the specified index.
	 *
	 * @param index the index to retrieve
	 * @return the key at the specified index
	 * @throws IndexOutOfBoundsException if the index is invalid
	 */
	protected LinkEntry getEntry(int index)
	{
		if (index < 0)
		{
			throw new IndexOutOfBoundsException("Index " + index + " is less than zero");
		}
		if (index >= size)
		{
			throw new IndexOutOfBoundsException("Index " + index + " is invalid for size " + size);
		}
		LinkEntry entry;
		if (index < (size / 2))
		{
			// Search forwards
			entry = header.after;
			for (int currentIndex = 0; currentIndex < index; currentIndex++)
			{
				entry = entry.after;
			}
		}
		else
		{
			// Search backwards
			entry = header;
			for (int currentIndex = size; currentIndex > index; currentIndex--)
			{
				entry = entry.before;
			}
		}
		return entry;
	}

	/**
	 * Adds an entry into this map, maintaining insertion order.
	 * <p/>
	 * This implementation adds the entry to the data storage table and
	 * to the end of the linked list.
	 *
	 * @param entry     the entry to add
	 * @param hashIndex the index into the data array to store at
	 */
	protected void addEntry(HashEntry entry, int hashIndex)
	{
		LinkEntry link = (LinkEntry) entry;
		link.after = header;
		link.before = header.before;
		header.before.after = link;
		header.before = link;
		data[hashIndex] = entry;
	}

	/**
	 * Creates an entry to store the data.
	 * <p/>
	 * This implementation creates a new LinkEntry instance.
	 *
	 * @param next     the next entry in sequence
	 * @param hashCode the hash code to use
	 * @param key      the key to store
	 * @param value    the value to store
	 * @return the newly created entry
	 */
	protected HashEntry createEntry(HashEntry next, int hashCode, Object key, Object value)
	{
		return new LinkEntry(next, hashCode, key, value);
	}

	/**
	 * Removes an entry from the map and the linked list.
	 * <p/>
	 * This implementation removes the entry from the linked list chain, then
	 * calls the superclass implementation.
	 *
	 * @param entry     the entry to remove
	 * @param hashIndex the index into the data structure
	 * @param previous  the previous entry in the chain
	 */
	protected void removeEntry(HashEntry entry, int hashIndex, HashEntry previous)
	{
		LinkEntry link = (LinkEntry) entry;
		link.before.after = link.after;
		link.after.before = link.before;
		link.after = null;
		link.before = null;
		super.removeEntry(entry, hashIndex, previous);
	}

	/**
	 * Gets the <code>before</code> field from a <code>LinkEntry</code>.
	 * Used in subclasses that have no visibility of the field.
	 *
	 * @param entry the entry to query, must not be null
	 * @return the <code>before</code> field of the entry
	 * @throws NullPointerException if the entry is null
	 */
	protected LinkEntry entryBefore(LinkEntry entry)
	{
		return entry.before;
	}

	/**
	 * Gets the <code>after</code> field from a <code>LinkEntry</code>.
	 * Used in subclasses that have no visibility of the field.
	 *
	 * @param entry the entry to query, must not be null
	 * @return the <code>after</code> field of the entry
	 * @throws NullPointerException if the entry is null
	 */
	protected LinkEntry entryAfter(LinkEntry entry)
	{
		return entry.after;
	}

	/**
	 * Gets an iterator over the map.
	 * Changes made to the iterator affect this map.
	 * <p/>
	 * A MapIterator returns the keys in the map. It also provides convenient
	 * methods to get the key and value, and set the value.
	 * It avoids the need to create an entrySet/keySet/values object.
	 *
	 * @return the map iterator
	 */
	public MapIterator mapIterator()
	{
		if (size == 0) return EmptyOrderedMapIterator.INSTANCE;

		return new LinkMapIterator(this);
	}

	/**
	 * Gets a bidirectional iterator over the map.
	 * Changes made to the iterator affect this map.
	 * <p/>
	 * A MapIterator returns the keys in the map. It also provides convenient
	 * methods to get the key and value, and set the value.
	 * It avoids the need to create an entrySet/keySet/values object.
	 *
	 * @return the map iterator
	 */
	public OrderedMapIterator orderedMapIterator()
	{
		if (size == 0) return EmptyOrderedMapIterator.INSTANCE;

		return new LinkMapIterator(this);
	}

	/**
	 * MapIterator implementation.
	 */
	protected static class LinkMapIterator extends LinkIterator implements OrderedMapIterator
	{

		protected LinkMapIterator(AbstractLinkedMap parent)
		{
			super(parent);
		}

		public Object next()
		{
			return super.nextEntry().getKey();
		}

		public Object previous()
		{
			return super.previousEntry().getKey();
		}

		public Object getKey()
		{
			HashEntry current = currentEntry();
			if (current == null) throw new IllegalStateException(AbstractHashedMap.GETKEY_INVALID);

			return current.getKey();
		}

		public Object getValue()
		{
			HashEntry current = currentEntry();
			if (current == null) throw new IllegalStateException(AbstractHashedMap.GETVALUE_INVALID);

			return current.getValue();
		}

		public Object setValue(Object value)
		{
			HashEntry current = currentEntry();
			if (current == null) throw new IllegalStateException(AbstractHashedMap.SETVALUE_INVALID);

			return current.setValue(value);
		}
	}

	/**
	 * Creates an entry set iterator.
	 * Subclasses can override this to return iterators with different properties.
	 *
	 * @return the entrySet iterator
	 */
	protected Iterator createEntrySetIterator()
	{
		if (size() == 0)
		{
			return EmptyOrderedIterator.INSTANCE;
		}
		return new EntrySetIterator(this);
	}

	/**
	 * EntrySet iterator.
	 */
	protected static class EntrySetIterator extends LinkIterator
	{

		protected EntrySetIterator(AbstractLinkedMap parent)
		{
			super(parent);
		}

		public Object next()
		{
			return super.nextEntry();
		}

		public Object previous()
		{
			return super.previousEntry();
		}
	}

	/**
	 * Creates a key set iterator.
	 * Subclasses can override this to return iterators with different properties.
	 *
	 * @return the keySet iterator
	 */
	protected Iterator createKeySetIterator()
	{
		if (size() == 0)
		{
			return EmptyOrderedIterator.INSTANCE;
		}
		return new KeySetIterator(this);
	}

	/**
	 * KeySet iterator.
	 */
	protected static class KeySetIterator extends EntrySetIterator
	{

		protected KeySetIterator(AbstractLinkedMap parent)
		{
			super(parent);
		}

		public Object next()
		{
			return super.nextEntry().getKey();
		}

		public Object previous()
		{
			return super.previousEntry().getKey();
		}
	}

	/**
	 * Creates a values iterator.
	 * Subclasses can override this to return iterators with different properties.
	 *
	 * @return the values iterator
	 */
	protected Iterator createValuesIterator()
	{
		if (size() == 0) return EmptyOrderedIterator.INSTANCE;

		return new ValuesIterator(this);
	}

	/**
	 * Values iterator.
	 */
	protected static class ValuesIterator extends LinkIterator
	{

		protected ValuesIterator(AbstractLinkedMap parent)
		{
			super(parent);
		}

		public Object next()
		{
			return super.nextEntry().getValue();
		}

		public Object previous()
		{
			return super.previousEntry().getValue();
		}
	}

	/**
	 * LinkEntry that stores the data.
	 * <p/>
	 * If you subclass <code>AbstractLinkedMap</code> but not <code>LinkEntry</code>
	 * then you will not be able to access the protected fields.
	 * The <code>entryXxx()</code> methods on <code>AbstractLinkedMap</code> exist
	 * to provide the necessary access.
	 */
	protected static class LinkEntry extends HashEntry
	{
		/**
		 * The entry before this one in the order
		 */
		protected LinkEntry before;
		/**
		 * The entry after this one in the order
		 */
		protected LinkEntry after;

		/**
		 * Constructs a new entry.
		 *
		 * @param next     the next entry in the hash bucket sequence
		 * @param hashCode the hash code
		 * @param key      the key
		 * @param value    the value
		 */
		protected LinkEntry(HashEntry next, int hashCode, Object key, Object value)
		{
			super(next, hashCode, key, value);
		}
	}

	/**
	 * Base Iterator that iterates in link order.
	 */
	protected static abstract class LinkIterator implements OrderedIterator, ResettableIterator
	{

		/**
		 * The parent map
		 */
		protected final AbstractLinkedMap parent;
		/**
		 * The current (last returned) entry
		 */
		protected LinkEntry last;
		/**
		 * The next entry
		 */
		protected LinkEntry next;
		/**
		 * The modification count expected
		 */
		protected int expectedModCount;

		protected LinkIterator(AbstractLinkedMap parent)
		{
			super();
			this.parent = parent;
			this.next = parent.header.after;
			this.expectedModCount = parent.modCount;
		}

		public boolean hasNext()
		{
			return (next != parent.header);
		}

		public boolean hasPrevious()
		{
			return (next.before != parent.header);
		}

		protected LinkEntry nextEntry()
		{
			if (parent.modCount != expectedModCount) throw new ConcurrentModificationException();

			if (next == parent.header) throw new NoSuchElementException(AbstractHashedMap.NO_NEXT_ENTRY);

			last = next;
			next = next.after;
			return last;
		}

		protected LinkEntry previousEntry()
		{
			if (parent.modCount != expectedModCount) throw new ConcurrentModificationException();

			LinkEntry previous = next.before;
			if (previous == parent.header) throw new NoSuchElementException(AbstractHashedMap.NO_PREVIOUS_ENTRY);

			next = previous;
			last = previous;
			return last;
		}

		protected LinkEntry currentEntry()
		{
			return last;
		}

		public void remove()
		{
			if (last == null) throw new IllegalStateException(AbstractHashedMap.REMOVE_INVALID);

			if (parent.modCount != expectedModCount) throw new ConcurrentModificationException();

			parent.remove(last.getKey());
			last = null;
			expectedModCount = parent.modCount;
		}

		public void reset()
		{
			last = null;
			next = parent.header.after;
		}

		public String toString()
		{
			if (last != null) return "Iterator[" + last.getKey() + "=" + last.getValue() + "]";
				else return "Iterator[]";
		}
	}

}
