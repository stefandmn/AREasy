package org.areasy.common.data.type.buffer;

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

import org.areasy.common.data.type.BoundedCollection;
import org.areasy.common.data.type.Buffer;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.*;

/**
 * The BoundedFifoBuffer is a very efficient implementation of
 * Buffer that does not alter the size of the buffer at runtime.
 * <p/>
 * The removal order of a <code>BoundedFifoBuffer</code> is based on the
 * insertion order; elements are removed in the same order in which they
 * were added.  The iteration order is the same as the removal order.
 * <p/>
 * The {@link #add(Object)}, {@link #remove()} and {@link #get()} operations
 * all perform in constant time.  All other operations perform in linear
 * time or worse.
 * <p/>
 * Note that this implementation is not synchronized.  The following can be
 * used to provide synchronized access to your <code>BoundedFifoBuffer</code>:
 * <pre>
 *   Buffer fifo = BufferUtility.synchronizedBuffer(new BoundedFifoBuffer());
 * </pre>
 * <p/>
 * This buffer prevents null objects from being added.
 *
 * @version $Id: BoundedFifoBuffer.java,v 1.3 2008/05/20 05:30:13 swd\stefan.damian Exp $
 */
public class BoundedFifoBuffer extends AbstractCollection implements Buffer, BoundedCollection, Serializable
{
	protected transient Object[] elements;
	protected final int maxElements;

	protected transient int start = 0;
	protected transient int end = 0;
	protected transient boolean full = false;	

	/**
	 * Constructs a new <code>BoundedFifoBuffer</code> big enough to hold
	 * 32 elements.
	 */
	public BoundedFifoBuffer()
	{
		this(32);
	}

	/**
	 * Constructs a new <code>BoundedFifoBuffer</code> big enough to hold
	 * the specified number of elements.
	 *
	 * @param size the maximum number of elements for this fifo
	 * @throws IllegalArgumentException if the size is less than 1
	 */
	public BoundedFifoBuffer(int size)
	{
		if (size <= 0) throw new IllegalArgumentException("The size must be greater than 0");

		elements = new Object[size];
		maxElements = elements.length;
	}

	/**
	 * Constructs a new <code>BoundedFifoBuffer</code> big enough to hold all
	 * of the elements in the specified collection. That collection's
	 * elements will also be added to the buffer.
	 *
	 * @param coll the collection whose elements to add, may not be null
	 * @throws NullPointerException if the collection is null
	 */
	public BoundedFifoBuffer(Collection coll)
	{
		this(coll.size());
		addAll(coll);
	}

	/**
	 * Write the buffer out using a custom routine.
	 *
	 * @param out the output stream
	 * @throws IOException if any IO error will occur
	 */
	private void writeObject(ObjectOutputStream out) throws IOException
	{
		out.defaultWriteObject();
		out.writeInt(size());

		for (Iterator it = iterator(); it.hasNext();)
		{
			out.writeObject(it.next());
		}
	}

	/**
	 * Read the buffer in using a custom routine.
	 *
	 * @param in the input stream
	 * @throws IOException  if any IO error will occur
	 * @throws ClassNotFoundException if any Class error will occur
	 */
	private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException
	{
		in.defaultReadObject();
		elements = new Object[maxElements];

		int size = in.readInt();

		for (int i = 0; i < size; i++)
		{
			elements[i] = in.readObject();
		}

		start = 0;
		end = size;
		full = (size == maxElements);
	}

	/**
	 * Returns the number of elements stored in the buffer.
	 *
	 * @return this buffer's size
	 */
	public int size()
	{
		int size = 0;

		if (end < start) size = maxElements - start + end;
			else if (end == start) size = (full ? maxElements : 0);
				else size = end - start;

		return size;
	}

	/**
	 * Returns true if this buffer is empty; false otherwise.
	 *
	 * @return true if this buffer is empty
	 */
	public boolean isEmpty()
	{
		return size() == 0;
	}

	/**
	 * Returns true if this collection is full and no new elements can be added.
	 *
	 * @return <code>true</code> if the collection is full
	 */
	public boolean isFull()
	{
		return size() == maxElements;
	}

	/**
	 * Gets the maximum size of the collection (the bound).
	 *
	 * @return the maximum number of elements the collection can hold
	 */
	public int maxSize()
	{
		return maxElements;
	}

	/**
	 * Clears this buffer.
	 */
	public void clear()
	{
		full = false;
		start = 0;
		end = 0;

		Arrays.fill(elements, null);
	}

	/**
	 * Adds the given element to this buffer.
	 *
	 * @param element the element to add
	 * @return true, always
	 * @throws NullPointerException    if the given element is null
	 * @throws BufferOverflowException if this buffer is full
	 */
	public boolean add(Object element)
	{
		if (null == element) throw new NullPointerException("Attempted to add null object to buffer");

		if (full) throw new BufferOverflowException("The buffer cannot hold more than " + maxElements + " objects.");

		elements[end++] = element;

		if (end >= maxElements) end = 0;

		if (end == start) full = true;

		return true;
	}

	/**
	 * Returns the least recently inserted element in this buffer.
	 *
	 * @return the least recently inserted element
	 * @throws BufferUnderflowException if the buffer is empty
	 */
	public Object get()
	{
		if (isEmpty()) throw new BufferUnderflowException("The buffer is already empty");

		return elements[start];
	}

	/**
	 * Removes the least recently inserted element from this buffer.
	 *
	 * @return the least recently inserted element
	 * @throws BufferUnderflowException if the buffer is empty
	 */
	public Object remove()
	{
		if (isEmpty()) throw new BufferUnderflowException("The buffer is already empty");

		Object element = elements[start];

		if (null != element)
		{
			elements[start++] = null;

			if (start >= maxElements) start = 0;

			full = false;
		}

		return element;
	}

	/**
	 * Increments the internal index.
	 *
	 * @param index the index to increment
	 * @return the updated index
	 */
	private int increment(int index)
	{
		index++;
		if (index >= maxElements) index = 0;

		return index;
	}

	/**
	 * Decrements the internal index.
	 *
	 * @param index the index to decrement
	 * @return the updated index
	 */
	private int decrement(int index)
	{
		index--;
		if (index < 0) index = maxElements - 1;

		return index;
	}

	/**
	 * Returns an iterator over this buffer's elements.
	 *
	 * @return an iterator over this buffer's elements
	 */
	public Iterator iterator()
	{
		return new Iterator()
		{
			private int index = start;
			private int lastReturnedIndex = -1;
			private boolean isFirst = full;

			public boolean hasNext()
			{
				return isFirst || (index != end);
			}

			public Object next()
			{
				if (!hasNext()) throw new NoSuchElementException();

				isFirst = false;
				lastReturnedIndex = index;
				index = increment(index);

				return elements[lastReturnedIndex];
			}

			public void remove()
			{
				if (lastReturnedIndex == -1) throw new IllegalStateException();

				// First element can be removed quickly
				if (lastReturnedIndex == start)
				{
					BoundedFifoBuffer.this.remove();
					lastReturnedIndex = -1;

					return;
				}

				// Other elements require us to shift the subsequent elements
				int i = lastReturnedIndex + 1;
				while (i != end)
				{
					if (i >= maxElements)
					{
						elements[i - 1] = elements[0];
						i = 0;
					}
					else
					{
						elements[i - 1] = elements[i];
						i++;
					}
				}

				lastReturnedIndex = -1;
				end = decrement(end);
				elements[end] = null;

				full = false;
				index = decrement(index);
			}

		};
	}
}
