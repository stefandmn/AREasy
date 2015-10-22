package org.areasy.common.data.type.list;

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

import org.areasy.common.data.type.collection.AbstractCollectionDecorator;

import java.util.Collection;
import java.util.List;
import java.util.ListIterator;

/**
 * Decorates another <code>List</code> to provide additional behaviour.
 * <p/>
 * Methods are forwarded directly to the decorated list.
 *
 * @version $Id: AbstractListDecorator.java,v 1.2 2008/05/14 09:32:35 swd\stefan.damian Exp $
 */
public abstract class AbstractListDecorator extends AbstractCollectionDecorator implements List
{

	/**
	 * Constructor only used in deserialization, do not use otherwise.
	 *
	 */
	protected AbstractListDecorator()
	{
		super();
	}

	/**
	 * Constructor that wraps (not copies).
	 *
	 * @param list the list to decorate, must not be null
	 * @throws IllegalArgumentException if list is null
	 */
	protected AbstractListDecorator(List list)
	{
		super(list);
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

	public void add(int index, Object object)
	{
		getList().add(index, object);
	}

	public boolean addAll(int index, Collection coll)
	{
		return getList().addAll(index, coll);
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

	public ListIterator listIterator()
	{
		return getList().listIterator();
	}

	public ListIterator listIterator(int index)
	{
		return getList().listIterator(index);
	}

	public Object remove(int index)
	{
		return getList().remove(index);
	}

	public Object set(int index, Object object)
	{
		return getList().set(index, object);
	}

	public List subList(int fromIndex, int toIndex)
	{
		return getList().subList(fromIndex, toIndex);
	}

}
