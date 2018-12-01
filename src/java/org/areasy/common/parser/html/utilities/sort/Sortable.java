package org.areasy.common.parser.html.utilities.sort;

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

/**
 * Provides a mechanism to abstract the sort process.
 * Classes implementing this interface are collections of Ordered objects
 * that are to be sorted by the Sort class and are
 * not necessarily Vectors or Arrays of Ordered objects.
 *
 * @see Sort
 *
 * @version $Id: Sortable.java,v 1.1 2008/05/25 17:26:05 swd\stefan.damian Exp $
 */
public interface Sortable
{

    /**
     * Returns the first index of the Sortable.
     *
     * @return The index of the first element.
     */
    public int first();

    /**
     * Returns the last index of the Sortable.
     *
     * @return The index of the last element.
     *         If this were an array object this would be (object.length - 1).
     */
    public int last();

    /**
     * Fetch the object at the given index.
     *
     * @param index The item number to get.
     * @param reuse If this argument is not null, it is an object
     *              acquired from a previous fetch that is no longer needed and
     *              may be returned as the result if it makes mores sense to alter
     *              and return it than to fetch or create a new element. That is, the
     *              reuse object is garbage and may be used to avoid allocating a new
     *              object if that would normally be the strategy.
     * @return The Ordered object at that index.
     */
    public Ordered fetch(int index, Ordered reuse);

    /**
     * Swaps the elements at the given indicies.
     *
     * @param i One index.
     * @param j The other index.
     */
    public void swap(int i, int j);
}
