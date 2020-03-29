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

import org.areasy.common.data.type.Transformer;
import org.areasy.common.data.type.collection.TransformedCollection;
import org.areasy.common.data.type.iterator.AbstractListIteratorDecorator;

import java.util.Collection;
import java.util.List;
import java.util.ListIterator;

/**
 * Decorates another <code>List</code> to transform objects that are added.
 * <p/>
 * The add and set methods are affected by this class.
 * Thus objects must be removed or searched for using their transformed form.
 * For example, if the transformation converts Strings to Integers, you must
 * use the Integer form to remove objects.
 *
 * @version $Id: TransformedList.java,v 1.2 2008/05/14 09:32:35 swd\stefan.damian Exp $
 */
public class TransformedList extends TransformedCollection implements List
{
	/**
	 * Factory method to create a transforming list.
	 * <p/>
	 * If there are any elements already in the list being decorated, they
	 * are NOT transformed.
	 *
	 * @param list        the list to decorate, must not be null
	 * @param transformer the transformer to use for conversion, must not be null
	 * @throws IllegalArgumentException if list or transformer is null
	 */
	public static List decorate(List list, Transformer transformer)
	{
		return new TransformedList(list, transformer);
	}

	/**
	 * Constructor that wraps (not copies).
	 * <p/>
	 * If there are any elements already in the list being decorated, they
	 * are NOT transformed.
	 *
	 * @param list        the list to decorate, must not be null
	 * @param transformer the transformer to use for conversion, must not be null
	 * @throws IllegalArgumentException if list or transformer is null
	 */
	protected TransformedList(List list, Transformer transformer)
	{
		super(list, transformer);
	}

	/**
	 * Gets the decorated list.
	 *
	 * @return the decorated list
	 */
	protected List getList()
	{
		return (List) collection;
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
		object = transform(object);
		getList().add(index, object);
	}

	public boolean addAll(int index, Collection coll)
	{
		coll = transform(coll);
		return getList().addAll(index, coll);
	}

	public ListIterator listIterator()
	{
		return listIterator(0);
	}

	public ListIterator listIterator(int i)
	{
		return new TransformedListIterator(getList().listIterator(i));
	}

	public Object set(int index, Object object)
	{
		object = transform(object);
		return getList().set(index, object);
	}

	public List subList(int fromIndex, int toIndex)
	{
		List sub = getList().subList(fromIndex, toIndex);
		return new TransformedList(sub, transformer);
	}

	/**
	 * Inner class Iterator for the TransformedList
	 */
	protected class TransformedListIterator extends AbstractListIteratorDecorator
	{

		protected TransformedListIterator(ListIterator iterator)
		{
			super(iterator);
		}

		public void add(Object object)
		{
			object = transform(object);
			iterator.add(object);
		}

		public void set(Object object)
		{
			object = transform(object);
			iterator.set(object);
		}
	}

}
