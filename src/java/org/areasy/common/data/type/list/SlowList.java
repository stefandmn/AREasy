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

import org.areasy.common.data.type.Factory;

import java.util.List;

/**
 * Decorates another <code>List</code> to create objects in the list on demand.
 * <p/>
 * When the {@link #get(int)} method is called with an index greater than
 * the size of the list, the list will automatically grow in size and return
 * a new object from the specified factory. The gaps will be filled by null.
 * If a get method call encounters a null, it will be replaced with a new
 * object from the factory. Thus this list is unsuitable for storing null
 * objects.
 * <p/>
 * For instance:
 * <p/>
 * <pre>
 * Factory factory = new Factory() {
 *     public Object create() {
 *         return new Date();
 *     }
 * }
 * List lazy = SlowList.decorate(new ArrayList(), factory);
 * Object obj = lazy.get(3);
 * </pre>
 * <p/>
 * After the above code is executed, <code>obj</code> will contain
 * a new <code>Date</code> instance.  Furthermore, that <code>Date</code>
 * instance is the fourth element in the list.  The first, second,
 * and third element are all set to <code>null</code>.
 * <p/>
 *
 * @version $Id: SlowList.java,v 1.2 2008/05/14 09:32:35 swd\stefan.damian Exp $
 */
public class SlowList extends AbstractSerializableListDecorator
{
	/**
	 * The factory to use to lazily instantiate the objects
	 */
	protected final Factory factory;

	/**
	 * Factory method to create a lazily instantiating list.
	 *
	 * @param list    the list to decorate, must not be null
	 * @param factory the factory to use for creation, must not be null
	 * @throws IllegalArgumentException if list or factory is null
	 */
	public static List decorate(List list, Factory factory)
	{
		return new SlowList(list, factory);
	}

	/**
	 * Constructor that wraps (not copies).
	 *
	 * @param list    the list to decorate, must not be null
	 * @param factory the factory to use for creation, must not be null
	 * @throws IllegalArgumentException if list or factory is null
	 */
	protected SlowList(List list, Factory factory)
	{
		super(list);
		if (factory == null) throw new IllegalArgumentException("Factory must not be null");

		this.factory = factory;
	}

	/**
	 * Decorate the get method to perform the lazy behaviour.
	 * <p/>
	 * If the requested index is greater than the current size, the list will
	 * grow to the new size and a new object will be returned from the factory.
	 * Indexes in-between the old size and the requested size are left with a
	 * placeholder that is replaced with a factory object when requested.
	 *
	 * @param index the index to retrieve
	 */
	public Object get(int index)
	{
		int size = getList().size();
		if (index < size)
		{
			// within bounds, get the object
			Object object = getList().get(index);
			if (object == null)
			{
				// item is a place holder, create new one, set and return
				object = factory.create();
				getList().set(index, object);
				return object;
			}
			else return object;
		}
		else
		{
			// we have to grow the list
			for (int i = size; i < index; i++)
			{
				getList().add(null);
			}
			// create our last object, set and return
			Object object = factory.create();
			getList().add(object);
			return object;
		}
	}


	public List subList(int fromIndex, int toIndex)
	{
		List sub = getList().subList(fromIndex, toIndex);
		return new SlowList(sub, factory);
	}

}
