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

import org.areasy.common.data.type.Buffer;
import org.areasy.common.data.type.collection.SynchronizedCollection;

/**
 * Decorates another <code>Buffer</code> to synchronize its behaviour
 * for a multi-threaded environment.
 * <p/>
 * Methods are synchronized, then forwarded to the decorated buffer.
 *
 * @version $Id: SynchronizedBuffer.java,v 1.2 2008/05/14 09:32:36 swd\stefan.damian Exp $
 */
public class SynchronizedBuffer extends SynchronizedCollection implements Buffer
{
	/**
	 * Factory method to create a synchronized buffer.
	 *
	 * @param buffer the buffer to decorate, must not be null
	 * @return a new synchronized Buffer
	 * @throws IllegalArgumentException if buffer is null
	 */
	public static Buffer decorate(Buffer buffer)
	{
		return new SynchronizedBuffer(buffer);
	}

	/**
	 * Constructor that wraps (not copies).
	 *
	 * @param buffer the buffer to decorate, must not be null
	 * @throws IllegalArgumentException if the buffer is null
	 */
	protected SynchronizedBuffer(Buffer buffer)
	{
		super(buffer);
	}

	/**
	 * Constructor that wraps (not copies).
	 *
	 * @param buffer the buffer to decorate, must not be null
	 * @param lock   the lock object to use, must not be null
	 * @throws IllegalArgumentException if the buffer is null
	 */
	protected SynchronizedBuffer(Buffer buffer, Object lock)
	{
		super(buffer, lock);
	}

	/**
	 * Gets the buffer being decorated.
	 *
	 * @return the decorated buffer
	 */
	protected Buffer getBuffer()
	{
		return (Buffer) collection;
	}

	public Object get()
	{
		synchronized (lock)
		{
			return getBuffer().get();
		}
	}

	public Object remove()
	{
		synchronized (lock)
		{
			return getBuffer().remove();
		}
	}

}
