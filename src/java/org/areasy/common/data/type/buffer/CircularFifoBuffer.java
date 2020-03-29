package org.areasy.common.data.type.buffer;

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

import java.util.Collection;

/**
 * CircularFifoBuffer is a first in first out buffer with a fixed size that
 * replaces its oldest element if full.
 * <p/>
 * The removal order of a <code>CircularFifoBuffer</code> is based on the
 * insertion order; elements are removed in the same order in which they
 * were added.  The iteration order is the same as the removal order.
 * <p/>
 * The {@link #add(Object)}, {@link #remove()} and {@link #get()} operations
 * all perform in constant time.  All other operations perform in linear
 * time or worse.
 * <p/>
 * Note that this implementation is not synchronized.  The following can be
 * used to provide synchronized access to your <code>CircularFifoBuffer</code>:
 * <pre>
 *   Buffer fifo = BufferUtility.synchronizedBuffer(new CircularFifoBuffer());
 * </pre>
 * <p/>
 * This buffer prevents null objects from being added.
 *
 * @version $Id: CircularFifoBuffer.java,v 1.2 2008/05/14 09:32:36 swd\stefan.damian Exp $
 */
public class CircularFifoBuffer extends BoundedFifoBuffer
{
	/**
	 * Constructor that creates a buffer with the default size of 32.
	 */
	public CircularFifoBuffer()
	{
		super(32);
	}

	/**
	 * Constructor that creates a buffer with the specified size.
	 *
	 * @param size the size of the buffer (cannot be changed)
	 * @throws IllegalArgumentException if the size is less than 1
	 */
	public CircularFifoBuffer(int size)
	{
		super(size);
	}

	/**
	 * Constructor that creates a buffer from the specified collection.
	 * The collection size also sets the buffer size
	 *
	 * @param coll the collection to copy into the buffer, may not be null
	 * @throws NullPointerException if the collection is null
	 */
	public CircularFifoBuffer(Collection coll)
	{
		super(coll);
	}

	/**
	 * If the buffer is full, the least recently added element is discarded so
	 * that a new element can be inserted.
	 *
	 * @param element the element to add
	 * @return true, always
	 */
	public boolean add(Object element)
	{
		if (isFull()) remove();

		return super.add(element);
	}

}
