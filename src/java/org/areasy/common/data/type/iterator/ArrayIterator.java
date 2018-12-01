package org.areasy.common.data.type.iterator;

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

import org.areasy.common.data.type.ResettableIterator;

import java.lang.reflect.Array;
import java.util.NoSuchElementException;

/**
 * Implements an {@link java.util.Iterator Iterator} over any array.
 * <p/>
 * The array can be either an array of object or of primitives. If you know
 * that you have an object array, the
 * {@link org.areasy.common.data.type.iterator.ObjectArrayIterator ObjectArrayIterator}
 * class is a better choice, as it will perform better.
 * <p/>
 * The iterator implements a {@link #reset} method, allowing the reset of
 * the iterator back to the start if required.
 *
 * @version $Id: ArrayIterator.java,v 1.2 2008/05/14 09:32:40 swd\stefan.damian Exp $
 */
public class ArrayIterator implements ResettableIterator
{

	/**
	 * The array to iterate over
	 */
	protected Object array;
	/**
	 * The start index to loop from
	 */
	protected int startIndex = 0;
	/**
	 * The end index to loop to
	 */
	protected int endIndex = 0;
	/**
	 * The current iterator index
	 */
	protected int index = 0;

	// Constructors
	/**
	 * Constructor for use with <code>setArray</code>.
	 * <p/>
	 * Using this constructor, the iterator is equivalent to an empty iterator
	 * until {@link #setArray(Object)} is  called to establish the array to iterate over.
	 */
	public ArrayIterator()
	{
		super();
	}

	/**
	 * Constructs an ArrayIterator that will iterate over the values in the
	 * specified array.
	 *
	 * @param array the array to iterate over.
	 * @throws IllegalArgumentException if <code>array</code> is not an array.
	 * @throws NullPointerException     if <code>array</code> is <code>null</code>
	 */
	public ArrayIterator(final Object array)
	{
		super();
		setArray(array);
	}

	/**
	 * Constructs an ArrayIterator that will iterate over the values in the
	 * specified array from a specific start index.
	 *
	 * @param array      the array to iterate over.
	 * @param startIndex the index to start iterating at.
	 * @throws IllegalArgumentException  if <code>array</code> is not an array.
	 * @throws NullPointerException      if <code>array</code> is <code>null</code>
	 * @throws IndexOutOfBoundsException if the index is invalid
	 */
	public ArrayIterator(final Object array, final int startIndex)
	{
		super();
		setArray(array);
		checkBound(startIndex, "start");
		this.startIndex = startIndex;
		this.index = startIndex;
	}

	/**
	 * Construct an ArrayIterator that will iterate over a range of values
	 * in the specified array.
	 *
	 * @param array      the array to iterate over.
	 * @param startIndex the index to start iterating at.
	 * @param endIndex   the index to finish iterating at.
	 * @throws IllegalArgumentException  if <code>array</code> is not an array.
	 * @throws NullPointerException      if <code>array</code> is <code>null</code>
	 * @throws IndexOutOfBoundsException if either index is invalid
	 */
	public ArrayIterator(final Object array, final int startIndex, final int endIndex)
	{
		super();
		setArray(array);
		checkBound(startIndex, "start");
		checkBound(endIndex, "end");
		if (endIndex < startIndex)
		{
			throw new IllegalArgumentException("End index must not be less than start index.");
		}
		this.startIndex = startIndex;
		this.endIndex = endIndex;
		this.index = startIndex;
	}

	/**
	 * Checks whether the index is valid or not.
	 *
	 * @param bound the index to check
	 * @param type  the index type (for error messages)
	 * @throws IndexOutOfBoundsException if the index is invalid
	 */
	protected void checkBound(final int bound, final String type)
	{
		if (bound > this.endIndex)
		{
			throw new ArrayIndexOutOfBoundsException("Attempt to make an ArrayIterator that " + type +
					"s beyond the end of the array. ");
		}
		if (bound < 0)
		{
			throw new ArrayIndexOutOfBoundsException("Attempt to make an ArrayIterator that " + type +
					"s before the start of the array. ");
		}
	}

	// Iterator interface
	/**
	 * Returns true if there are more elements to return from the array.
	 *
	 * @return true if there is a next element to return
	 */
	public boolean hasNext()
	{
		return (index < endIndex);
	}

	/**
	 * Returns the next element in the array.
	 *
	 * @return the next element in the array
	 * @throws NoSuchElementException if all the elements in the array
	 *                                have already been returned
	 */
	public Object next()
	{
		if (hasNext() == false)
		{
			throw new NoSuchElementException();
		}
		return Array.get(array, index++);
	}

	/**
	 * Throws {@link UnsupportedOperationException}.
	 *
	 * @throws UnsupportedOperationException always
	 */
	public void remove()
	{
		throw new UnsupportedOperationException("remove() method is not supported");
	}

	// Properties
	/**
	 * Gets the array that this iterator is iterating over.
	 *
	 * @return the array this iterator iterates over, or <code>null</code> if
	 *         the no-arg constructor was used and {@link #setArray(Object)} has never
	 *         been called with a valid array.
	 */
	public Object getArray()
	{
		return array;
	}

	/**
	 * Sets the array that the ArrayIterator should iterate over.
	 * <p/>
	 * If an array has previously been set (using the single-arg constructor
	 * or this method) then that array is discarded in favour of this one.
	 * Iteration is restarted at the start of the new array.
	 * Although this can be used to reset iteration, the {@link #reset()} method
	 * is a more effective choice.
	 *
	 * @param array the array that the iterator should iterate over.
	 * @throws IllegalArgumentException if <code>array</code> is not an array.
	 * @throws NullPointerException     if <code>array</code> is <code>null</code>
	 */
	public void setArray(final Object array)
	{
		// Array.getLength throws IllegalArgumentException if the object is not
		// an array or NullPointerException if the object is null.  This call
		// is made before saving the array and resetting the index so that the
		// array iterator remains in a consistent state if the argument is not
		// an array or is null.
		this.endIndex = Array.getLength(array);
		this.startIndex = 0;
		this.array = array;
		this.index = 0;
	}

	/**
	 * Resets the iterator back to the start index.
	 */
	public void reset()
	{
		this.index = this.startIndex;
	}

}
