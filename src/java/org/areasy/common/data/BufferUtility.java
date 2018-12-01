package org.areasy.common.data;

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
import org.areasy.common.data.type.Buffer;
import org.areasy.common.data.type.Predicate;
import org.areasy.common.data.type.Transformer;
import org.areasy.common.data.type.buffer.*;

/**
 * Provides utility methods and decorators for {@link Buffer} instances.
 *
 * @version $Id: BufferUtility.java,v 1.2 2008/05/14 09:32:29 swd\stefan.damian Exp $
 */
public class BufferUtility
{

	/**
	 * An empty unmodifiable buffer.
	 */
	public static final Buffer EMPTY_BUFFER = UnmodifiableBuffer.decorate(new ArrayStack(1));

	/**
	 * <code>BufferUtility</code> should not normally be instantiated.
	 */
	public BufferUtility()
	{
	}

	/**
	 * Returns a synchronized buffer backed by the given buffer.
	 * Much like the synchronized collections returned by
	 * {@link java.util.Collections}, you must manually synchronize on
	 * the returned buffer's iterator to avoid non-deterministic behavior:
	 * <p/>
	 * <pre>
	 * Buffer b = BufferUtility.synchronizedBuffer(myBuffer);
	 * synchronized (b) {
	 *     Iterator i = b.iterator();
	 *     while (i.hasNext()) {
	 *         process (i.next());
	 *     }
	 * }
	 * </pre>
	 *
	 * @param buffer the buffer to synchronize, must not be null
	 * @return a synchronized buffer backed by that buffer
	 * @throws IllegalArgumentException if the Buffer is null
	 */
	public static Buffer synchronizedBuffer(Buffer buffer)
	{
		return SynchronizedBuffer.decorate(buffer);
	}

	/**
	 * Returns a synchronized buffer backed by the given buffer that will
	 * block on {@link Buffer#get()} and {@link Buffer#remove()} operations.
	 * If the buffer is empty, then the {@link Buffer#get()} and
	 * {@link Buffer#remove()} operations will block until new elements
	 * are added to the buffer, rather than immediately throwing a
	 * <code>BufferUnderflowException</code>.
	 *
	 * @param buffer the buffer to synchronize, must not be null
	 * @return a blocking buffer backed by that buffer
	 * @throws IllegalArgumentException if the Buffer is null
	 */
	public static Buffer blockingBuffer(Buffer buffer)
	{
		return BlockingBuffer.decorate(buffer);
	}

	/**
	 * Returns an unmodifiable buffer backed by the given buffer.
	 *
	 * @param buffer the buffer to make unmodifiable, must not be null
	 * @return an unmodifiable buffer backed by that buffer
	 * @throws IllegalArgumentException if the Buffer is null
	 */
	public static Buffer unmodifiableBuffer(Buffer buffer)
	{
		return UnmodifiableBuffer.decorate(buffer);
	}

	/**
	 * Returns a predicated (validating) buffer backed by the given buffer.
	 * <p/>
	 * Only objects that pass the test in the given predicate can be added to the buffer.
	 * Trying to add an invalid object results in an IllegalArgumentException.
	 * It is important not to use the original buffer after invoking this method,
	 * as it is a backdoor for adding invalid objects.
	 *
	 * @param buffer    the buffer to predicate, must not be null
	 * @param predicate the predicate used to evaluate new elements, must not be null
	 * @return a predicated buffer
	 * @throws IllegalArgumentException if the Buffer or Predicate is null
	 */
	public static Buffer predicatedBuffer(Buffer buffer, Predicate predicate)
	{
		return PredicatedBuffer.decorate(buffer, predicate);
	}

	/**
	 * Returns a typed buffer backed by the given buffer.
	 * <p/>
	 * Only elements of the specified type can be added to the buffer.
	 *
	 * @param buffer the buffer to predicate, must not be null
	 * @param type   the type to allow into the buffer, must not be null
	 * @return a typed buffer
	 * @throws IllegalArgumentException if the buffer or type is null
	 */
	public static Buffer typedBuffer(Buffer buffer, Class type)
	{
		return TypedBuffer.decorate(buffer, type);
	}

	/**
	 * Returns a transformed buffer backed by the given buffer.
	 * <p/>
	 * Each object is passed through the transformer as it is added to the
	 * Buffer. It is important not to use the original buffer after invoking this
	 * method, as it is a backdoor for adding untransformed objects.
	 *
	 * @param buffer      the buffer to predicate, must not be null
	 * @param transformer the transformer for the buffer, must not be null
	 * @return a transformed buffer backed by the given buffer
	 * @throws IllegalArgumentException if the Buffer or Transformer is null
	 */
	public static Buffer transformedBuffer(Buffer buffer, Transformer transformer)
	{
		return TransformedBuffer.decorate(buffer, transformer);
	}

}
