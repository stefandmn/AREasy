package org.areasy.common.data.type.list;

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

import java.util.*;

/**
 * Static list creation.
 * A List helper class that attempts to avoid unneccessary List
 * creation.   If a method needs to create a List to return, but it is
 * expected that this will either be empty or frequently contain a
 * single item, then using SlowList will avoid additional object
 * creations by using Collections.EMPTY_LIST or
 * Collections.singletonList where possible.
 * <p/>
 * <p><h4>Usage</h4>
 * <pre>
 *   Object list = null;
 *   while(loopCondition)
 *   {
 *     Object item = getItem();
 *     if (item.isToBeAdded())
 *         list = StaticList.add(list, item);
 *   }
 *
 *   return StaticList.getList(list);
 * </pre>
 * <p/>
 * An ArrayList of default size is used as the initial SlowList.
 *
 * @version $Id: StaticList.java,v 1.2 2008/05/14 09:32:35 swd\stefan.damian Exp $
 * @see java.util.List
 */
public class StaticList
{
	private static final String[] __EMTPY_STRING_ARRAY = new String[0];

	/**
	 * Private and default constructor. Not be used externally.
	 */
	private StaticList()
	{
		//private constructor.
	}

	/**
	 * Add an item to a SlowList
	 *
	 * @param list The list to add to or null if none yet created.
	 * @param item The item to add.
	 * @return The lazylist created or added to.
	 */
	public static Object add(Object list, Object item)
	{
		if (list == null)
		{
			if (item instanceof List || item == null)
			{
				List l = new ArrayList();
				l.add(item);

				return l;
			}

			return item;
		}

		if (list instanceof List)
		{
			((List) list).add(item);

			return list;
		}

		List l = new ArrayList();
		l.add(list);
		l.add(item);

		return l;
	}

	/**
	 * Add an item to a SlowList
	 *
	 * @param list  The list to add to or null if none yet created.
	 * @param index The index to add the item at.
	 * @param item  The item to add.
	 * @return The lazylist created or added to.
	 */
	public static Object add(Object list, int index, Object item)
	{
		if (list == null)
		{
			if (index > 0 || item instanceof List || item == null)
			{
				List l = new ArrayList();
				l.add(index, item);
				return l;
			}

			return item;
		}

		if (list instanceof List)
		{
			((List) list).add(index, item);
			return list;
		}

		List l = new ArrayList();
		l.add(list);
		l.add(index, item);

		return l;
	}

	/**
	 * Add the contents of a Collection to a SlowList
	 *
	 * @param list       The list to add to or null if none yet created.
	 * @param collection The Collection whose contents should be added.
	 * @return The lazylist created or added to.
	 */
	public static Object addCollection(Object list, Collection collection)
	{
		Iterator i = collection.iterator();
		while (i.hasNext())
		{
			list = StaticList.add(list, i.next());
		}

		return list;
	}

	public static Object ensureSize(Object list, int initialSize)
	{
		if (list == null) return new ArrayList(initialSize);

		if (list instanceof ArrayList) return list;

		List l = new ArrayList(initialSize);
		l.add(list);

		return l;
	}

	public static Object remove(Object list, Object o)
	{
		if (list == null) return null;

		if (list instanceof List)
		{
			List l = (List) list;
			l.remove(o);
			if (l.size() == 0) return null;

			return list;
		}

		if (list.equals(o)) return null;

		return list;
	}

	public static Object remove(Object list, int i)
	{
		if (list == null) return null;

		if (list instanceof List)
		{
			List l = (List) list;
			l.remove(i);

			if (l.size() == 0) return null;

			return list;
		}

		if (i == 0) return null;

		return list;
	}


	/**
	 * Get the real List from a SlowList.
	 *
	 * @param list A SlowList returned from SlowList.add(Object)
	 * @return The List of added items, which may be an EMPTY_LIST
	 *         or a SingletonList.
	 */
	public static List getList(Object list)
	{
		return getList(list, false);
	}


	/**
	 * Get the real List from a SlowList.
	 *
	 * @param list         A SlowList returned from SlowList.add(Object) or null
	 * @param nullForEmpty If true, null is returned instead of an
	 *                     empty list.
	 * @return The List of added items, which may be null, an EMPTY_LIST
	 *         or a SingletonList.
	 */
	public static List getList(Object list, boolean nullForEmpty)
	{
		if (list == null) return nullForEmpty ? null : Collections.EMPTY_LIST;

		if (list instanceof List) return (List) list;

		List l = new ArrayList(1);
		l.add(list);
		return l;
	}


	public static String[] toStringArray(Object list)
	{
		if (list == null) return __EMTPY_STRING_ARRAY;

		if (list instanceof List)
		{
			List l = (List) list;

			String[] a = new String[l.size()];
			for (int i = l.size(); i-- > 0;)
			{
				Object o = l.get(i);
				if (o != null) a[i] = o.toString();
			}

			return a;
		}

		return new String[]{list.toString()};
	}


	/**
	 * The size of a lazy List
	 *
	 * @param list A SlowList returned from SlowList.add(Object) or null
	 * @return the size of the list.
	 */
	public static int size(Object list)
	{
		if (list == null) return 0;

		if (list instanceof List) return ((List) list).size();

		return 1;
	}

	/**
	 * Get item from the list
	 *
	 * @param list A SlowList returned from SlowList.add(Object) or null
	 * @param i    int index
	 * @return the item from the list.
	 */
	public static Object get(Object list, int i)
	{
		if (list == null) throw new IndexOutOfBoundsException();

		if (list instanceof List) return ((List) list).get(i);

		if (i == 0) return list;

		throw new IndexOutOfBoundsException();
	}

	public static boolean contains(Object list, Object item)
	{
		if (list == null) return false;

		if (list instanceof List) return ((List) list).contains(item);

		return list.equals(item);
	}


	public static Object clone(Object list)
	{
		if (list == null) return null;

		if (list instanceof List) return new ArrayList((List) list);

		return list;
	}

	public static String toString(Object list)
	{
		if (list == null) return "[]";

		if (list instanceof List) return ((List) list).toString();

		return "[" + list + "]";
	}

	public static Iterator iterator(Object list)
	{
		if (list == null) return Collections.EMPTY_LIST.iterator();

		if (list instanceof List) return ((List) list).iterator();

		return getList(list).iterator();
	}

	public static ListIterator listIterator(Object list)
	{
		if (list == null) return Collections.EMPTY_LIST.listIterator();

		if (list instanceof List) return ((List) list).listIterator();

		return getList(list).listIterator();
	}
}

