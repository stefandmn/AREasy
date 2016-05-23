package org.areasy.common.data.type.iterator;

/*
 * Copyright (c) 2007-2016 AREasy Runtime
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

import java.util.Iterator;

/**
 * Decorates an iterator such that each element returned is transformed.
 *
 * @version $Id: TransformIterator.java,v 1.3 2008/05/14 09:32:39 swd\stefan.damian Exp $
 */
public class TransformIterator implements Iterator
{
	/**
	 * The iterator being used
	 */
	private Iterator iterator;
	/**
	 * The transformer being used
	 */
	private Transformer transformer;

	/**
	 * Constructs a new <code>TransformIterator</code> that will not function
	 * until the {@link #setIterator(Iterator) setIterator} method is
	 * invoked.
	 */
	public TransformIterator()
	{
		super();
	}

	/**
	 * Constructs a new <code>TransformIterator</code> that won't transform
	 * elements from the given iterator.
	 *
	 * @param iterator the iterator to use
	 */
	public TransformIterator(Iterator iterator)
	{
		super();
		this.iterator = iterator;
	}

	/**
	 * Constructs a new <code>TransformIterator</code> that will use the
	 * given iterator and transformer.  If the given transformer is null,
	 * then objects will not be transformed.
	 *
	 * @param iterator    the iterator to use
	 * @param transformer the transformer to use
	 */
	public TransformIterator(Iterator iterator, Transformer transformer)
	{
		super();

		this.iterator = iterator;
		this.transformer = transformer;
	}

	public boolean hasNext()
	{
		return iterator.hasNext();
	}

	/**
	 * Gets the next object from the iteration, transforming it using the
	 * current transformer. If the transformer is null, no transformation
	 * occurs and the object from the iterator is returned directly.
	 *
	 * @return the next object
	 * @throws java.util.NoSuchElementException
	 *          if there are no more elements
	 */
	public Object next()
	{
		Object object = transform(iterator.next());

		if(object == null && hasNext()) return next();
			else return object;
	}

	public void remove()
	{
		iterator.remove();
	}

	/**
	 * Gets the iterator this iterator is using.
	 *
	 * @return the iterator.
	 */
	public Iterator getIterator()
	{
		return iterator;
	}

	/**
	 * Sets the iterator for this iterator to use.
	 * If iteration has started, this effectively resets the iterator.
	 *
	 * @param iterator the iterator to use
	 */
	public void setIterator(Iterator iterator)
	{
		this.iterator = iterator;
	}

	/**
	 * Gets the transformer this iterator is using.
	 *
	 * @return the transformer.
	 */
	public Transformer getTransformer()
	{
		return transformer;
	}

	/**
	 * Sets the transformer this the iterator to use.
	 * A null transformer is a no-op transformer.
	 *
	 * @param transformer the transformer to use
	 */
	public void setTransformer(Transformer transformer)
	{
		this.transformer = transformer;
	}

	/**
	 * Transforms the given object using the transformer.
	 * If the transformer is null, the original object is returned as-is.
	 *
	 * @param source the object to transform
	 * @return the transformed object
	 */
	protected Object transform(Object source)
	{
		if (transformer != null) return transformer.transform(source);

		return source;
	}
}
