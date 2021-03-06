package org.areasy.common.data.type.list;

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

import org.areasy.common.data.type.iterator.AbstractIteratorDecorator;
import org.areasy.common.data.type.iterator.AbstractListIteratorDecorator;
import org.areasy.common.data.type.set.UnmodifiableSet;

import java.util.*;

/**
 * Decorates a <code>List</code> to ensure that no duplicates are present
 * much like a <code>Set</code>.
 * <p/>
 * The <code>List</code> interface makes certain assumptions/requirements.
 * This implementation breaks these in certain ways, but this is merely the
 * result of rejecting duplicates.
 * Each violation is explained in the method, but it should not affect you.
 * <p/>
 * The {@link org.areasy.common.data.type.set.ListOrderedSet ListOrderedSet}
 * class provides an alternative approach, by wrapping an existing Set and
 * retaining insertion order in the iterator.
 *
 * @version $Id: SetUniqueList.java,v 1.2 2008/05/14 09:32:35 swd\stefan.damian Exp $
 */
public class SetUniqueList extends AbstractSerializableListDecorator
{
	/**
	 * Internal Set to maintain uniqueness.
	 */
	protected final Set set;

	/**
	 * Factory method to create a SetList using the supplied list to retain order.
	 * <p/>
	 * If the list contains duplicates, these are removed (first indexed one kept).
	 * A <code>HashSet</code> is used for the set behaviour.
	 *
	 * @param list the list to decorate, must not be null
	 * @throws IllegalArgumentException if list is null
	 */
	public static SetUniqueList decorate(List list)
	{
		if (list == null) throw new IllegalArgumentException("List must not be null");

		if (list.isEmpty()) return new SetUniqueList(list, new HashSet());

		else
		{
			List temp = new ArrayList(list);
			list.clear();
			SetUniqueList sl = new SetUniqueList(list, new HashSet());
			sl.addAll(temp);

			return sl;
		}
	}

	/**
	 * Constructor that wraps (not copies) the List and specifies the set to use.
	 * <p/>
	 * The set and list must both be correctly initialised to the same elements.
	 *
	 * @param set  the set to decorate, must not be null
	 * @param list the list to decorate, must not be null
	 * @throws IllegalArgumentException if set or list is null
	 */
	protected SetUniqueList(List list, Set set)
	{
		super(list);
		if (set == null) throw new IllegalArgumentException("Set must not be null");

		this.set = set;
	}

	/**
	 * Gets an unmodifiable view as a Set.
	 *
	 * @return an unmodifiable set view
	 */
	public Set asSet()
	{
		return UnmodifiableSet.decorate(set);
	}

	/**
	 * Adds an element to the list if it is not already present.
	 * <p/>
	 * <i>(Violation)</i>
	 * The <code>List</code> interface requires that this method returns
	 * <code>true</code> always. However this class may return <code>false</code>
	 * because of the <code>Set</code> behaviour.
	 *
	 * @param object the object to add
	 * @return true if object was added
	 */
	public boolean add(Object object)
	{
		// gets initial size
		final int sizeBefore = size();

		// adds element if unique
		add(size(), object);

		// compares sizes to detect if collection changed
		return (sizeBefore != size());
	}

	/**
	 * Adds an element to a specific index in the list if it is not already present.
	 * <p/>
	 * <i>(Violation)</i>
	 * The <code>List</code> interface makes the assumption that the element is
	 * always inserted. This may not happen with this implementation.
	 *
	 * @param index  the index to insert at
	 * @param object the object to add
	 */
	public void add(int index, Object object)
	{
		// adds element if it is not contained already
		if (!set.contains(object))
		{
			super.add(index, object);
			set.add(object);
		}
	}

	/**
	 * Adds an element to the end of the list if it is not already present.
	 * <p/>
	 * <i>(Violation)</i>
	 * The <code>List</code> interface makes the assumption that the element is
	 * always inserted. This may not happen with this implementation.
	 *
	 * @param coll the collection to add
	 */
	public boolean addAll(Collection coll)
	{
		return addAll(size(), coll);
	}

	/**
	 * Adds a collection of objects to the end of the list avoiding duplicates.
	 * <p/>
	 * Only elements that are not already in this list will be added, and
	 * duplicates from the specified collection will be ignored.
	 * <p/>
	 * <i>(Violation)</i>
	 * The <code>List</code> interface makes the assumption that the elements
	 * are always inserted. This may not happen with this implementation.
	 *
	 * @param index the index to insert at
	 * @param coll  the collection to add in iterator order
	 * @return true if this collection changed
	 */
	public boolean addAll(int index, Collection coll)
	{
		// gets initial size
		final int sizeBefore = size();

		// adds all elements
		for (final Iterator it = coll.iterator(); it.hasNext();)
		{
			add(it.next());
		}

		// compares sizes to detect if collection changed
		return sizeBefore != size();
	}

	/**
	 * Sets the value at the specified index avoiding duplicates.
	 * <p/>
	 * The object is set into the specified index.
	 * Afterwards, any previous duplicate is removed
	 * If the object is not already in the list then a normal set occurs.
	 * If it is present, then the old version is removed and re-added at this index
	 *
	 * @param index  the index to insert at
	 * @param object the object to set
	 * @return the previous object
	 */
	public Object set(int index, Object object)
	{
		int pos = indexOf(object);
		Object result = super.set(index, object);
		if (pos == -1 || pos == index) return result;

		return remove(pos);
	}

	public boolean remove(Object object)
	{
		boolean result = super.remove(object);
		set.remove(object);
		return result;
	}

	public Object remove(int index)
	{
		Object result = super.remove(index);
		set.remove(result);
		return result;
	}

	public boolean removeAll(Collection coll)
	{
		boolean result = super.removeAll(coll);
		set.removeAll(coll);
		return result;
	}

	public boolean retainAll(Collection coll)
	{
		boolean result = super.retainAll(coll);
		set.retainAll(coll);
		return result;
	}

	public void clear()
	{
		super.clear();
		set.clear();
	}

	public boolean contains(Object object)
	{
		return set.contains(object);
	}

	public boolean containsAll(Collection coll)
	{
		return set.containsAll(coll);
	}

	public Iterator iterator()
	{
		return new SetListIterator(super.iterator(), set);
	}

	public ListIterator listIterator()
	{
		return new SetListListIterator(super.listIterator(), set);
	}

	public ListIterator listIterator(int index)
	{
		return new SetListListIterator(super.listIterator(index), set);
	}

	public List subList(int fromIndex, int toIndex)
	{
		return new SetUniqueList(super.subList(fromIndex, toIndex), set);
	}

	/**
	 * Inner class iterator.
	 */
	static class SetListIterator extends AbstractIteratorDecorator
	{

		protected final Set set;
		protected Object last = null;

		protected SetListIterator(Iterator it, Set set)
		{
			super(it);
			this.set = set;
		}

		public Object next()
		{
			last = super.next();
			return last;
		}

		public void remove()
		{
			super.remove();
			set.remove(last);
			last = null;
		}
	}

	/**
	 * Inner class iterator.
	 */
	static class SetListListIterator extends AbstractListIteratorDecorator
	{

		protected final Set set;
		protected Object last = null;

		protected SetListListIterator(ListIterator it, Set set)
		{
			super(it);
			this.set = set;
		}

		public Object next()
		{
			last = super.next();
			return last;
		}

		public Object previous()
		{
			last = super.previous();
			return last;
		}

		public void remove()
		{
			super.remove();
			set.remove(last);
			last = null;
		}

		public void add(Object object)
		{
			if (!set.contains(object))
			{
				super.add(object);
				set.add(object);
			}
		}

		public void set(Object object)
		{
			throw new UnsupportedOperationException("ListIterator does not support set");
		}
	}

}
