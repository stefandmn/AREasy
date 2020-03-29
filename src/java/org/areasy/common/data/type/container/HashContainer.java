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

import org.areasy.common.data.type.Container;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Collection;
import java.util.HashMap;

/**
 * Implements <code>Bag</code>, using a <code>HashMap</code> to provide the
 * data storage. This is the standard implementation of a bag.
 * <p/>
 * A <code>Bag</code> stores each object in the collection together with a
 * count of occurrences. Extra methods on the interface allow multiple copies
 * of an object to be added or removed at once. It is important to read the
 * interface javadoc carefully as several methods violate the
 * <code>Collection</code> interface specification.
 *
 * @version $Id: HashContainer.java,v 1.2 2008/05/14 09:32:35 swd\stefan.damian Exp $
 */
public class HashContainer extends AbstractMapContainer implements Container, Serializable
{
	/**
	 * Constructs an empty <code>HashBag</code>.
	 */
	public HashContainer()
	{
		super(new HashMap());
	}

	/**
	 * Constructs a bag containing all the members of the given collection.
	 *
	 * @param coll a collection to copy into this bag
	 */
	public HashContainer(Collection coll)
	{
		this();
		addAll(coll);
	}

	/**
	 * Write the bag out using a custom routine.
	 */
	private void writeObject(ObjectOutputStream out) throws IOException
	{
		out.defaultWriteObject();
		super.doWriteObject(out);
	}

	/**
	 * Read the bag in using a custom routine.
	 */
	private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException
	{
		in.defaultReadObject();
		super.doReadObject(new HashMap(), in);
	}

}
