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

import org.areasy.common.data.type.Buffer;

import java.util.Collection;

/**
 * Decorates another <code>Buffer</code> to make {@link #get()} and
 * {@link #remove()} block when the <code>Buffer</code> is empty.
 * <p/>
 * If either <code>get</code> or <code>remove</code> is called on an empty
 * <code>Buffer</code>, the calling thread waits for notification that
 * an <code>add</code> or <code>addAll</code> operation has completed.
 * <p/>
 * When one or more entries are added to an empty <code>Buffer</code>,
 * all threads blocked in <code>get</code> or <code>remove</code> are notified.
 * There is no guarantee that concurrent blocked <code>get</code> or
 * <code>remove</code> requests will be "unblocked" and receive data in the
 * order that they arrive.
 * <p/>
 *
 * @version $Id: BlockingBuffer.java,v 1.2 2008/05/14 09:32:36 swd\stefan.damian Exp $
 */
public class BlockingBuffer extends SynchronizedBuffer
{
	/**
	 * Factory method to create a blocking buffer.
	 *
	 * @param buffer the buffer to decorate, must not be null
	 * @return a new blocking Buffer
	 * @throws IllegalArgumentException if buffer is null
	 */
	public static Buffer decorate(Buffer buffer)
	{
		return new BlockingBuffer(buffer);
	}

	/**
	 * Constructor that wraps (not copies).
	 *
	 * @param buffer the buffer to decorate, must not be null
	 * @throws IllegalArgumentException if the buffer is null
	 */
	protected BlockingBuffer(Buffer buffer)
	{
		super(buffer);
	}

	public boolean add(Object o)
	{
		synchronized (lock)
		{
			boolean result = collection.add(o);
			notifyAll();
			return result;
		}
	}

	public boolean addAll(Collection c)
	{
		synchronized (lock)
		{
			boolean result = collection.addAll(c);
			notifyAll();
			return result;
		}
	}

	public Object get()
	{
		synchronized (lock)
		{
			while (collection.isEmpty())
			{
				try
				{
					wait();
				}
				catch (InterruptedException e)
				{
					throw new BufferUnderflowException();
				}
			}
			return getBuffer().get();
		}
	}

	public Object remove()
	{
		synchronized (lock)
		{
			while (collection.isEmpty())
			{
				try
				{
					wait();
				}
				catch (InterruptedException e)
				{
					throw new BufferUnderflowException();
				}
			}
			return getBuffer().remove();
		}
	}

}
