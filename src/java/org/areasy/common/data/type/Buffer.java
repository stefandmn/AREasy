package org.areasy.common.data.type;

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

import java.util.Collection;

/**
 * Defines a collection that allows objects to be removed in some well-defined order.
 * <p/>
 * The removal order can be based on insertion order (eg, a FIFO queue or a
 * LIFO stack), on access order (eg, an LRU cache), on some arbitrary comparator
 * (eg, a priority queue) or on any other well-defined ordering.
 * <p/>
 * Note that the removal order is not necessarily the same as the iteration
 * order.  A <code>Buffer</code> implementation may have equivalent removal
 * and iteration orders, but this is not required.
 * <p/>
 * This interface does not specify any behavior for
 * {@link Object#equals(Object)} and {@link Object#hashCode} methods.  It
 * is therefore possible for a <code>Buffer</code> implementation to also
 * also implement {@link java.util.List}, {@link java.util.Set} or
 * {@link org.areasy.common.data.type.Container}.
 *
 * @version $Id: Buffer.java,v 1.3 2008/05/20 06:52:55 swd\stefan.damian Exp $
 */
public interface Buffer extends Collection
{
	/**
	 * Gets and removes the next object from the buffer.
	 *
	 * @return the next object in the buffer, which is also removed
	 * @throws org.areasy.common.data.type.buffer.BufferUnderflowException
	 *          if the buffer is already empty
	 */
	Object remove();

	/**
	 * Gets the next object from the buffer without removing it.
	 *
	 * @return the next object in the buffer, which is not removed
	 * @throws org.areasy.common.data.type.buffer.BufferUnderflowException
	 *          if the buffer is empty
	 */
	Object get();
}
