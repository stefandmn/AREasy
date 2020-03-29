package org.areasy.common.data.type.container;

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

import org.areasy.common.data.type.SortedContainer;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Collection;
import java.util.Comparator;
import java.util.SortedMap;
import java.util.TreeMap;

/**
 * Implements <code>SortedBag</code>, using a <code>TreeMap</code> to provide
 * the data storage. This is the standard implementation of a sorted bag.
 * <p/>
 * Order will be maintained among the bag members and can be viewed through the
 * iterator.
 * <p/>
 * A <code>Bag</code> stores each object in the collection together with a
 * count of occurrences. Extra methods on the interface allow multiple copies
 * of an object to be added or removed at once. It is important to read the
 * interface javadoc carefully as several methods violate the
 * <code>Collection</code> interface specification.
 *
 * @version $Id: TreeContainer.java,v 1.2 2008/05/14 09:32:35 swd\stefan.damian Exp $
 */
public class TreeContainer extends AbstractMapContainer implements SortedContainer, Serializable
{
	/**
	 * Constructs an empty <code>TreeBag</code>.
	 */
	public TreeContainer()
	{
		super(new TreeMap());
	}

	/**
	 * Constructs an empty bag that maintains order on its unique
	 * representative members according to the given {@link Comparator}.
	 *
	 * @param comparator the comparator to use
	 */
	public TreeContainer(Comparator comparator)
	{
		super(new TreeMap(comparator));
	}

	/**
	 * Constructs a <code>TreeBag</code> containing all the members of the
	 * specified collection.
	 *
	 * @param coll the collection to copy into the bag
	 */
	public TreeContainer(Collection coll)
	{
		this();
		addAll(coll);
	}

	public Object first()
	{
		return ((SortedMap) getMap()).firstKey();
	}

	public Object last()
	{
		return ((SortedMap) getMap()).lastKey();
	}

	public Comparator comparator()
	{
		return ((SortedMap) getMap()).comparator();
	}

	/**
	 * Write the bag out using a custom routine.
	 */
	private void writeObject(ObjectOutputStream out) throws IOException
	{
		out.defaultWriteObject();
		out.writeObject(comparator());
		super.doWriteObject(out);
	}

	/**
	 * Read the bag in using a custom routine.
	 */
	private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException
	{
		in.defaultReadObject();
		Comparator comp = (Comparator) in.readObject();
		super.doReadObject(new TreeMap(comp), in);
	}

}
