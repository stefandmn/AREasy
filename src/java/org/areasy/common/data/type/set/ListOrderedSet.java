package org.areasy.common.data.type.set;

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
import org.areasy.common.data.type.list.UnmodifiableList;

import java.util.*;

/**
 * Decorates another <code>Set</code> to ensure that the order of addition
 * is retained and used by the iterator.
 * <p/>
 * If an object is added to the set for a second time, it will remain in the
 * original position in the iteration.
 * The order can be observed from the set via the iterator or toArray methods.
 * <p/>
 * The ListOrderedSet also has various useful direct methods. These include many
 * from <code>List</code>, such as <code>get(int)</code>, <code>remove(int)</code>
 * and <code>indexOf(int)</code>. An unmodifiable <code>List</code> view of
 * the set can be obtained via <code>asList()</code>.
 * <p/>
 * This class cannot implement the <code>List</code> interface directly as
 * various interface methods (notably equals/hashCode) are incompatable with a set.
 *
 * @version $Id: ListOrderedSet.java,v 1.2 2008/05/14 09:32:37 swd\stefan.damian Exp $
 */
public class ListOrderedSet extends AbstractSerializableSetDecorator implements Set
{
	/**
	 * Internal list to hold the sequence of objects
	 */
	protected final List setOrder;

	/**
	 * Factory method to create an ordered set specifying the list and set to use.
	 *
	 * @param set  the set to decorate, must be empty and not null
	 * @param list the list to decorate, must be empty and not null
	 * @throws IllegalArgumentException if set or list is null
	 * @throws IllegalArgumentException if either the set or list is not empty
	 */
	public static ListOrderedSet decorate(Set set, List list)
	{
		if (set == null) throw new IllegalArgumentException("Set must not be null");
		if (list == null) throw new IllegalArgumentException("List must not be null");
		if (set.size() > 0 || list.size() > 0) throw new IllegalArgumentException("Set and List must be empty");

		return new ListOrderedSet(set, list);
	}

	/**
	 * Factory method to create an ordered set.
	 * <p/>
	 * An <code>ArrayList</code> is used to retain order.
	 *
	 * @param set the set to decorate, must not be null
	 * @throws IllegalArgumentException if set is null
	 */
	public static ListOrderedSet decorate(Set set)
	{
		return new ListOrderedSet(set);
	}

	/**
	 * Factory method to create an ordered set using the supplied list to retain order.
	 * <p/>
	 * A <code>HashSet</code> is used for the set behaviour.
	 *
	 * @param list the list to decorate, must not be null
	 * @throws IllegalArgumentException if list is null
	 */
	public static ListOrderedSet decorate(List list)
	{
		if (list == null) throw new IllegalArgumentException("List must not be null");

		Set set = new HashSet(list);
		list.retainAll(set);

		return new ListOrderedSet(set, list);
	}

	/**
	 * Constructs a new empty <code>ListOrderedSet</code> using
	 * a <code>HashSet</code> and an <code>ArrayList</code> internally.
	 *
	 */
	public ListOrderedSet()
	{
		super(new HashSet());
		setOrder = new ArrayList();
	}

	/**
	 * Constructor that wraps (not copies).
	 *
	 * @param set the set to decorate, must not be null
	 * @throws IllegalArgumentException if set is null
	 */
	protected ListOrderedSet(Set set)
	{
		super(set);
		setOrder = new ArrayList(set);
	}

	/**
	 * Constructor that wraps (not copies) the Set and specifies the list to use.
	 * <p/>
	 * The set and list must both be correctly initialised to the same elements.
	 *
	 * @param set  the set to decorate, must not be null
	 * @param list the list to decorate, must not be null
	 * @throws IllegalArgumentException if set or list is null
	 */
	protected ListOrderedSet(Set set, List list)
	{
		super(set);
		if (list == null) throw new IllegalArgumentException("List must not be null");

		setOrder = list;
	}

	/**
	 * Gets an unmodifiable view of the order of the Set.
	 *
	 * @return an unmodifiable list view
	 */
	public List asList()
	{
		return UnmodifiableList.decorate(setOrder);
	}

	public void clear()
	{
		collection.clear();
		setOrder.clear();
	}

	public Iterator iterator()
	{
		return new OrderedSetIterator(setOrder.iterator(), collection);
	}

	public boolean add(Object object)
	{
		if (collection.contains(object)) return collection.add(object);
		else
		{
			// first add, so add to both set and list
			boolean result = collection.add(object);
			setOrder.add(object);
			return result;
		}
	}

	public boolean addAll(Collection coll)
	{
		boolean result = false;
		for (Iterator it = coll.iterator(); it.hasNext();)
		{
			Object object = it.next();
			result = result | add(object);
		}

		return result;
	}

	public boolean remove(Object object)
	{
		boolean result = collection.remove(object);
		setOrder.remove(object);
		return result;
	}

	public boolean removeAll(Collection coll)
	{
		boolean result = false;
		for (Iterator it = coll.iterator(); it.hasNext();)
		{
			Object object = it.next();
			result = result | remove(object);
		}

		return result;
	}

	public boolean retainAll(Collection coll)
	{
		boolean result = collection.retainAll(coll);
		if (result == false) return false;
			else if (collection.size() == 0) setOrder.clear();
				else
				{
					for (Iterator it = setOrder.iterator(); it.hasNext();)
					{
						Object object = it.next();
						if (collection.contains(object) == false) it.remove();
					}
				}

		return result;
	}

	public Object[] toArray()
	{
		return setOrder.toArray();
	}

	public Object[] toArray(Object a[])
	{
		return setOrder.toArray(a);
	}

	public Object get(int index)
	{
		return setOrder.get(index);
	}

	public int indexOf(Object object)
	{
		return setOrder.indexOf(object);
	}

	public void add(int index, Object object)
	{
		if (contains(object) == false)
		{
			collection.add(object);
			setOrder.add(index, object);
		}
	}

	public boolean addAll(int index, Collection coll)
	{
		boolean changed = false;
		for (Iterator it = coll.iterator(); it.hasNext();)
		{
			Object object = it.next();
			if (contains(object) == false)
			{
				collection.add(object);
				setOrder.add(index, object);

				index++;
				changed = true;
			}
		}

		return changed;
	}

	public Object remove(int index)
	{
		Object obj = setOrder.remove(index);
		remove(obj);

		return obj;
	}

	/**
	 * Uses the underlying List's toString so that order is achieved.
	 * This means that the decorated Set's toString is not used, so
	 * any custom toStrings will be ignored.
	 */
	public String toString()
	{
		return setOrder.toString();
	}

	/**
	 * Internal iterator handle remove.
	 */
	static class OrderedSetIterator extends AbstractIteratorDecorator
	{

		/**
		 * Object we iterate on
		 */
		protected final Collection set;
		
		/**
		 * Last object retrieved
		 */
		protected Object last;

		private OrderedSetIterator(Iterator iterator, Collection set)
		{
			super(iterator);
			this.set = set;
		}

		public Object next()
		{
			last = iterator.next();
			return last;
		}

		public void remove()
		{
			set.remove(last);
			iterator.remove();
			last = null;
		}
	}

}
