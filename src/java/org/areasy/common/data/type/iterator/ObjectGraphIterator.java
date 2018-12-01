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

import org.areasy.common.data.type.ArrayStack;
import org.areasy.common.data.type.Transformer;

import java.util.Iterator;
import java.util.NoSuchElementException;

/**
 * An Iterator that can traverse multiple iterators down an object graph.
 * <p/>
 * This iterator can extract multiple objects from a complex tree-like object graph.
 * The iteration starts from a single root object.
 * It uses a <code>Transformer</code> to extract the iterators and elements.
 * Its main benefit is that no intermediate <code>List</code> is created.
 * <p/>
 * For example, consider an object graph:
 * <pre>
 *                 |- Branch -- Leaf
 *                 |         \- Leaf
 *         |- Tree |         /- Leaf
 *         |       |- Branch -- Leaf
 *  Forest |                 \- Leaf
 *         |       |- Branch -- Leaf
 *         |       |         \- Leaf
 *         |- Tree |         /- Leaf
 *                 |- Branch -- Leaf
 *                 |- Branch -- Leaf</pre>
 * The following <code>Transformer</code>, used in this class, will extract all
 * the Leaf objects without creating a combined intermediate list:
 * <pre>
 * public Object transform(Object input) {
 *   if (input instanceof Forest) {
 *     return ((Forest) input).treeIterator();
 *   }
 *   if (input instanceof Tree) {
 *     return ((Tree) input).branchIterator();
 *   }
 *   if (input instanceof Branch) {
 *     return ((Branch) input).leafIterator();
 *   }
 *   if (input instanceof Leaf) {
 *     return input;
 *   }
 *   throw new ClassCastException();
 * }</pre>
 * <p/>
 * Internally, iteration starts from the root object. When next is called,
 * the transformer is called to examine the object. The transformer will return
 * either an iterator or an object. If the object is an Iterator, the next element
 * from that iterator is obtained and the process repeats. If the element is an object
 * it is returned.
 * <p/>
 * Under many circumstances, linking Iterators together in this manner is
 * more efficient (and convenient) than using nested for loops to extract a list.
 *
 * @version $Id: ObjectGraphIterator.java,v 1.2 2008/05/14 09:32:40 swd\stefan.damian Exp $
 */
public class ObjectGraphIterator implements Iterator
{
	/**
	 * The stack of iterators
	 */
	protected final ArrayStack stack = new ArrayStack(8);
	/**
	 * The root object in the tree
	 */
	protected Object root;
	/**
	 * The transformer to use
	 */
	protected Transformer transformer;

	/**
	 * Whether there is another element in the iteration
	 */
	protected boolean hasNext = false;
	/**
	 * The current iterator
	 */
	protected Iterator currentIterator;
	/**
	 * The current value
	 */
	protected Object currentValue;
	/**
	 * The last used iterator, needed for remove()
	 */
	protected Iterator lastUsedIterator;

	/**
	 * Constructs an ObjectGraphIterator using a root object and transformer.
	 * <p/>
	 * The root object can be an iterator, in which case it will be immediately
	 * looped around.
	 *
	 * @param root        the root object, null will result in an empty iterator
	 * @param transformer the transformer to use, null will use a no effect transformer
	 */
	public ObjectGraphIterator(Object root, Transformer transformer)
	{
		super();

		if (root instanceof Iterator) this.currentIterator = (Iterator) root;
			else this.root = root;

		this.transformer = transformer;
	}

	/**
	 * Constructs a ObjectGraphIterator that will handle an iterator of iterators.
	 * <p/>
	 * This constructor exists for convenience to emphasise that this class can
	 * be used to iterate over nested iterators. That is to say that the iterator
	 * passed in here contains other iterators, which may in turn contain further
	 * iterators.
	 *
	 * @param rootIterator the root iterator, null will result in an empty iterator
	 */
	public ObjectGraphIterator(Iterator rootIterator)
	{
		super();

		this.currentIterator = rootIterator;
		this.transformer = null;
	}

	/**
	 * Loops around the iterators to find the next value to return.
	 */
	protected void updateCurrentIterator()
	{
		if (hasNext) return;

		if (currentIterator == null)
		{
			if (root == null)
			{
				// do nothing, hasNext will be false
			}
			else
			{
				if (transformer == null) findNext(root);
						else findNext(transformer.transform(root));

				root = null;
			}
		}
		else findNextByIterator(currentIterator);
	}

	/**
	 * Finds the next object in the iteration given any start object.
	 *
	 * @param value the value to start from
	 */
	protected void findNext(Object value)
	{
		if (value instanceof Iterator)
		{
			// need to examine this iterator
			findNextByIterator((Iterator) value);
		}
		else
		{
			// next value found
			currentValue = value;
			hasNext = true;
		}
	}

	/**
	 * Finds the next object in the iteration given an iterator.
	 *
	 * @param iterator the iterator to start from
	 */
	protected void findNextByIterator(Iterator iterator)
	{
		if (iterator != currentIterator)
		{
			// recurse a level
			if (currentIterator != null) stack.push(currentIterator);

			currentIterator = iterator;
		}

		while (currentIterator.hasNext() && hasNext == false)
		{
			Object next = currentIterator.next();
			if (transformer != null) next = transformer.transform(next);

			findNext(next);
		}

		if (hasNext)
		{
			// next value found
		}
		else if (stack.isEmpty())
		{
			// all iterators exhausted
		}
		else
		{
			// current iterator exhausted, go up a level
			currentIterator = (Iterator) stack.pop();
			findNextByIterator(currentIterator);
		}
	}

	/**
	 * Checks whether there are any more elements in the iteration to obtain.
	 *
	 * @return true if elements remain in the iteration
	 */
	public boolean hasNext()
	{
		updateCurrentIterator();
		return hasNext;
	}

	/**
	 * Gets the next element of the iteration.
	 *
	 * @return the next element from the iteration
	 * @throws NoSuchElementException if all the Iterators are exhausted
	 */
	public Object next()
	{
		updateCurrentIterator();
		if (hasNext == false) throw new NoSuchElementException("No more elements in the iteration");

		lastUsedIterator = currentIterator;
		Object result = currentValue;
		currentValue = null;
		hasNext = false;

		return result;
	}

	/**
	 * Removes from the underlying collection the last element returned.
	 * <p/>
	 * This method calls remove() on the underlying Iterator and it may
	 * throw an UnsupportedOperationException if the underlying Iterator
	 * does not support this method.
	 *
	 * @throws UnsupportedOperationException if the remove operator is not supported by the underlying Iterator
	 * @throws IllegalStateException         if the next method has not yet been called, or the remove method has
	 *                                       already been called after the last call to the next method.
	 */
	public void remove()
	{
		if (lastUsedIterator == null) throw new IllegalStateException("Iterator remove() cannot be called at this time");

		lastUsedIterator.remove();
		lastUsedIterator = null;
	}

}
