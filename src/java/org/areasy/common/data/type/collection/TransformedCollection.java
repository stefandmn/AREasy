package org.areasy.common.data.type.collection;

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

import org.areasy.common.data.type.Transformer;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

/**
 * Decorates another <code>Collection</code> to transform objects that are added.
 * <p/>
 * The add methods are affected by this class.
 * Thus objects must be removed or searched for using their transformed form.
 * For example, if the transformation converts Strings to Integers, you must
 * use the Integer form to remove objects.
 *
 * @version $Id: TransformedCollection.java,v 1.2 2008/05/14 09:32:34 swd\stefan.damian Exp $
 */
public class TransformedCollection extends AbstractSerializableCollectionDecorator
{
	/**
	 * The transformer to use
	 */
	protected final Transformer transformer;

	/**
	 * Factory method to create a transforming collection.
	 * <p/>
	 * If there are any elements already in the collection being decorated, they
	 * are NOT transformed.
	 *
	 * @param coll        the collection to decorate, must not be null
	 * @param transformer the transformer to use for conversion, must not be null
	 * @return a new transformed collection
	 * @throws IllegalArgumentException if collection or transformer is null
	 */
	public static Collection decorate(Collection coll, Transformer transformer)
	{
		return new TransformedCollection(coll, transformer);
	}

	/**
	 * Constructor that wraps (not copies).
	 * <p/>
	 * If there are any elements already in the collection being decorated, they
	 * are NOT transformed.
	 *
	 * @param coll        the collection to decorate, must not be null
	 * @param transformer the transformer to use for conversion, must not be null
	 * @throws IllegalArgumentException if collection or transformer is null
	 */
	protected TransformedCollection(Collection coll, Transformer transformer)
	{
		super(coll);
		if (transformer == null)
		{
			throw new IllegalArgumentException("Transformer must not be null");
		}
		this.transformer = transformer;
	}

	/**
	 * Transforms an object.
	 * <p/>
	 * The transformer itself may throw an exception if necessary.
	 *
	 * @param object the object to transform
	 * @return a transformed object
	 */
	protected Object transform(Object object)
	{
		return transformer.transform(object);
	}

	/**
	 * Transforms a collection.
	 * <p/>
	 * The transformer itself may throw an exception if necessary.
	 *
	 * @param coll the collection to transform
	 * @return a transformed object
	 */
	protected Collection transform(Collection coll)
	{
		List list = new ArrayList(coll.size());
		for (Iterator it = coll.iterator(); it.hasNext();)
		{
			list.add(transform(it.next()));
		}
		return list;
	}

	public boolean add(Object object)
	{
		object = transform(object);
		return getCollection().add(object);
	}

	public boolean addAll(Collection coll)
	{
		coll = transform(coll);
		return getCollection().addAll(coll);
	}

}
