package org.areasy.common.data.type.buffer;

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

import org.areasy.common.data.type.Buffer;
import org.areasy.common.data.type.Unmodifiable;
import org.areasy.common.data.type.iterator.UnmodifiableIterator;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Collection;
import java.util.Iterator;

/**
 * Decorates another <code>Buffer</code> to ensure it can't be altered.
 *
 * @version $Id: UnmodifiableBuffer.java,v 1.2 2008/05/14 09:32:36 swd\stefan.damian Exp $
 */
public final class UnmodifiableBuffer extends AbstractBufferDecorator implements Unmodifiable, Serializable
{

	/**
	 * Factory method to create an unmodifiable buffer.
	 * <p/>
	 * If the buffer passed in is already unmodifiable, it is returned.
	 *
	 * @param buffer the buffer to decorate, must not be null
	 * @return an unmodifiable Buffer
	 * @throws IllegalArgumentException if buffer is null
	 */
	public static Buffer decorate(Buffer buffer)
	{
		if (buffer instanceof Unmodifiable) return buffer;

		return new UnmodifiableBuffer(buffer);
	}

	/**
	 * Constructor that wraps (not copies).
	 *
	 * @param buffer the buffer to decorate, must not be null
	 * @throws IllegalArgumentException if buffer is null
	 */
	private UnmodifiableBuffer(Buffer buffer)
	{
		super(buffer);
	}

	/**
	 * Write the collection out using a custom routine.
	 *
	 * @param out the output stream
	 * @throws IOException
	 */
	private void writeObject(ObjectOutputStream out) throws IOException
	{
		out.defaultWriteObject();
		out.writeObject(collection);
	}

	/**
	 * Read the collection in using a custom routine.
	 *
	 * @param in the input stream
	 * @throws IOException
	 * @throws ClassNotFoundException
	 */
	private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException
	{
		in.defaultReadObject();
		collection = (Collection) in.readObject();
	}

	public Iterator iterator()
	{
		return UnmodifiableIterator.decorate(getCollection().iterator());
	}

	public boolean add(Object object)
	{
		throw new UnsupportedOperationException();
	}

	public boolean addAll(Collection coll)
	{
		throw new UnsupportedOperationException();
	}

	public void clear()
	{
		throw new UnsupportedOperationException();
	}

	public boolean remove(Object object)
	{
		throw new UnsupportedOperationException();
	}

	public boolean removeAll(Collection coll)
	{
		throw new UnsupportedOperationException();
	}

	public boolean retainAll(Collection coll)
	{
		throw new UnsupportedOperationException();
	}

	public Object remove()
	{
		throw new UnsupportedOperationException();
	}

}
