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

import org.areasy.common.data.type.Predicate;
import org.areasy.common.data.type.collection.PredicatedCollection;
import org.areasy.common.data.type.iterator.AbstractListIteratorDecorator;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

/**
 * Decorates another <code>List</code> to validate that all additions
 * match a specified predicate.
 * <p/>
 * This list exists to provide validation for the decorated list.
 * It is normally created to decorate an empty list.
 * If an object cannot be added to the list, an IllegalArgumentException is thrown.
 * <p/>
 * One usage would be to ensure that no null entries are added to the list.
 * <pre>List list = PredicatedList.decorate(new ArrayList(), NotNullPredicate.INSTANCE);</pre>
 *
 * @version $Id: PredicatedList.java,v 1.2 2008/05/14 09:32:35 swd\stefan.damian Exp $
 */
public class PredicatedList extends PredicatedCollection implements List
{
	/**
	 * Factory method to create a predicated (validating) list.
	 * <p/>
	 * If there are any elements already in the list being decorated, they
	 * are validated.
	 *
	 * @param list      the list to decorate, must not be null
	 * @param predicate the predicate to use for validation, must not be null
	 * @throws IllegalArgumentException if list or predicate is null
	 * @throws IllegalArgumentException if the list contains invalid elements
	 */
	public static List decorate(List list, Predicate predicate)
	{
		return new PredicatedList(list, predicate);
	}

	/**
	 * Constructor that wraps (not copies).
	 * <p/>
	 * If there are any elements already in the list being decorated, they
	 * are validated.
	 *
	 * @param list      the list to decorate, must not be null
	 * @param predicate the predicate to use for validation, must not be null
	 * @throws IllegalArgumentException if list or predicate is null
	 * @throws IllegalArgumentException if the list contains invalid elements
	 */
	protected PredicatedList(List list, Predicate predicate)
	{
		super(list, predicate);
	}

	/**
	 * Gets the list being decorated.
	 *
	 * @return the decorated list
	 */
	protected List getList()
	{
		return (List) getCollection();
	}

	public Object get(int index)
	{
		return getList().get(index);
	}

	public int indexOf(Object object)
	{
		return getList().indexOf(object);
	}

	public int lastIndexOf(Object object)
	{
		return getList().lastIndexOf(object);
	}

	public Object remove(int index)
	{
		return getList().remove(index);
	}

	public void add(int index, Object object)
	{
		validate(object);
		getList().add(index, object);
	}

	public boolean addAll(int index, Collection coll)
	{
		for (Iterator it = coll.iterator(); it.hasNext();)
		{
			validate(it.next());
		}

		return getList().addAll(index, coll);
	}

	public ListIterator listIterator()
	{
		return listIterator(0);
	}

	public ListIterator listIterator(int i)
	{
		return new PredicatedListIterator(getList().listIterator(i));
	}

	public Object set(int index, Object object)
	{
		validate(object);
		return getList().set(index, object);
	}

	public List subList(int fromIndex, int toIndex)
	{
		List sub = getList().subList(fromIndex, toIndex);

		return new PredicatedList(sub, predicate);
	}

	/**
	 * Inner class Iterator for the PredicatedList
	 */
	protected class PredicatedListIterator extends AbstractListIteratorDecorator
	{
		protected PredicatedListIterator(ListIterator iterator)
		{
			super(iterator);
		}

		public void add(Object object)
		{
			validate(object);
			iterator.add(object);
		}

		public void set(Object object)
		{
			validate(object);
			iterator.set(object);
		}
	}

}
