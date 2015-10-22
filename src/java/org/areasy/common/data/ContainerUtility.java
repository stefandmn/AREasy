package org.areasy.common.data;

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

import org.areasy.common.data.type.Container;
import org.areasy.common.data.type.Predicate;
import org.areasy.common.data.type.SortedContainer;
import org.areasy.common.data.type.Transformer;
import org.areasy.common.data.type.container.*;

/**
 * Provides utility methods and decorators for
 * {@link Container} and {@link SortedContainer} instances.
 *
 * @version $Id: ContainerUtility.java,v 1.2 2008/05/14 09:32:29 swd\stefan.damian Exp $
 */
public class ContainerUtility
{

	/**
	 * An empty unmodifiable bag.
	 */
	public static final Container EMPTY_CONTAINER = UnmodifiableContainer.decorate(new HashContainer());

	/**
	 * An empty unmodifiable sorted bag.
	 */
	public static final Container EMPTY_SORTED_CONTAINER = UnmodifiableSortedContainer.decorate(new TreeContainer());

	/**
	 * Instantiation of BagUtility is not intended or required.
	 * However, some tools require an instance to operate.
	 */
	public ContainerUtility()
	{
		//nothing to do here
	}

	/**
	 * Returns a synchronized (thread-safe) bag backed by the given bag.
	 * In order to guarantee serial access, it is critical that all
	 * access to the backing bag is accomplished through the returned bag.
	 * <p/>
	 * It is imperative that the user manually synchronize on the returned
	 * bag when iterating over it:
	 * <p/>
	 * <pre>
	 * Bag bag = BagUtility.synchronizedBag(new HashBag());
	 * ...
	 * synchronized(bag) {
	 *     Iterator i = bag.iterator(); // Must be in synchronized block
	 *     while (i.hasNext())
	 *         foo(i.next());
	 *     }
	 * }
	 * </pre>
	 * <p/>
	 * Failure to follow this advice may result in non-deterministic
	 * behavior.
	 *
	 * @param container the bag to synchronize, must not be null
	 * @return a synchronized bag backed by that bag
	 * @throws IllegalArgumentException if the Bag is null
	 */
	public static Container synchronizedBag(Container container)
	{
		return SynchronizedContainer.decorate(container);
	}

	/**
	 * Returns an unmodifiable view of the given bag.  Any modification
	 * attempts to the returned bag will raise an
	 * {@link UnsupportedOperationException}.
	 *
	 * @param container the bag whose unmodifiable view is to be returned, must not be null
	 * @return an unmodifiable view of that bag
	 * @throws IllegalArgumentException if the Bag is null
	 */
	public static Container unmodifiableBag(Container container)
	{
		return UnmodifiableContainer.decorate(container);
	}

	/**
	 * Returns a predicated (validating) bag backed by the given bag.
	 * <p/>
	 * Only objects that pass the test in the given predicate can be added to the bag.
	 * Trying to add an invalid object results in an IllegalArgumentException.
	 * It is important not to use the original bag after invoking this method,
	 * as it is a backdoor for adding invalid objects.
	 *
	 * @param container the bag to predicate, must not be null
	 * @param predicate the predicate for the bag, must not be null
	 * @return a predicated bag backed by the given bag
	 * @throws IllegalArgumentException if the Bag or Predicate is null
	 */
	public static Container predicatedBag(Container container, Predicate predicate)
	{
		return PredicatedContainer.decorate(container, predicate);
	}

	/**
	 * Returns a typed bag backed by the given bag.
	 * <p/>
	 * Only objects of the specified type can be added to the bag.
	 *
	 * @param container the bag to limit to a specific type, must not be null
	 * @param type      the type of objects which may be added to the bag
	 * @return a typed bag backed by the specified bag
	 */
	public static Container typedBag(Container container, Class type)
	{
		return TypedContainer.decorate(container, type);
	}

	/**
	 * Returns a transformed bag backed by the given bag.
	 * <p/>
	 * Each object is passed through the transformer as it is added to the
	 * Bag. It is important not to use the original bag after invoking this
	 * method, as it is a backdoor for adding untransformed objects.
	 *
	 * @param container   the bag to predicate, must not be null
	 * @param transformer the transformer for the bag, must not be null
	 * @return a transformed bag backed by the given bag
	 * @throws IllegalArgumentException if the Bag or Transformer is null
	 */
	public static Container transformedBag(Container container, Transformer transformer)
	{
		return TransformedContainer.decorate(container, transformer);
	}

	/**
	 * Returns a synchronized (thread-safe) sorted bag backed by the given
	 * sorted bag.
	 * In order to guarantee serial access, it is critical that all
	 * access to the backing bag is accomplished through the returned bag.
	 * <p/>
	 * It is imperative that the user manually synchronize on the returned
	 * bag when iterating over it:
	 * <p/>
	 * <pre>
	 * SortedBag bag = BagUtility.synchronizedSortedBag(new TreeBag());
	 * ...
	 * synchronized(bag) {
	 *     Iterator i = bag.iterator(); // Must be in synchronized block
	 *     while (i.hasNext())
	 *         foo(i.next());
	 *     }
	 * }
	 * </pre>
	 * <p/>
	 * Failure to follow this advice may result in non-deterministic
	 * behavior.
	 *
	 * @param bag the bag to synchronize, must not be null
	 * @return a synchronized bag backed by that bag
	 * @throws IllegalArgumentException if the SortedBag is null
	 */
	public static SortedContainer synchronizedSortedBag(SortedContainer bag)
	{
		return SynchronizedSortedContainer.decorate(bag);
	}

	/**
	 * Returns an unmodifiable view of the given sorted bag.  Any modification
	 * attempts to the returned bag will raise an
	 * {@link UnsupportedOperationException}.
	 *
	 * @param bag the bag whose unmodifiable view is to be returned, must not be null
	 * @return an unmodifiable view of that bag
	 * @throws IllegalArgumentException if the SortedBag is null
	 */
	public static SortedContainer unmodifiableSortedBag(SortedContainer bag)
	{
		return UnmodifiableSortedContainer.decorate(bag);
	}

	/**
	 * Returns a predicated (validating) sorted bag backed by the given sorted bag.
	 * <p/>
	 * Only objects that pass the test in the given predicate can be added to the bag.
	 * Trying to add an invalid object results in an IllegalArgumentException.
	 * It is important not to use the original bag after invoking this method,
	 * as it is a backdoor for adding invalid objects.
	 *
	 * @param bag       the sorted bag to predicate, must not be null
	 * @param predicate the predicate for the bag, must not be null
	 * @return a predicated bag backed by the given bag
	 * @throws IllegalArgumentException if the SortedBag or Predicate is null
	 */
	public static SortedContainer predicatedSortedBag(SortedContainer bag, Predicate predicate)
	{
		return PredicatedSortedContainer.decorate(bag, predicate);
	}

	/**
	 * Returns a typed sorted bag backed by the given bag.
	 * <p/>
	 * Only objects of the specified type can be added to the bag.
	 *
	 * @param bag  the bag to limit to a specific type, must not be null
	 * @param type the type of objects which may be added to the bag
	 * @return a typed bag backed by the specified bag
	 */
	public static SortedContainer typedSortedBag(SortedContainer bag, Class type)
	{
		return TypedSortedContainer.decorate(bag, type);
	}

	/**
	 * Returns a transformed sorted bag backed by the given bag.
	 * <p/>
	 * Each object is passed through the transformer as it is added to the
	 * Bag. It is important not to use the original bag after invoking this
	 * method, as it is a backdoor for adding untransformed objects.
	 *
	 * @param bag         the bag to predicate, must not be null
	 * @param transformer the transformer for the bag, must not be null
	 * @return a transformed bag backed by the given bag
	 * @throws IllegalArgumentException if the Bag or Transformer is null
	 */
	public static SortedContainer transformedSortedBag(SortedContainer bag, Transformer transformer)
	{
		return TransformedSortedContainer.decorate(bag, transformer);
	}

}
