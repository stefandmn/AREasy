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
import org.areasy.common.data.type.Predicate;
import org.areasy.common.data.type.collection.PredicatedCollection;

/**
 * Decorates another <code>Buffer</code> to validate that additions
 * match a specified predicate.
 * <p/>
 * This buffer exists to provide validation for the decorated buffer.
 * It is normally created to decorate an empty buffer.
 * If an object cannot be added to the buffer, an IllegalArgumentException is thrown.
 *
 * @version $Id: PredicatedBuffer.java,v 1.2 2008/05/14 09:32:36 swd\stefan.damian Exp $
 */
public class PredicatedBuffer extends PredicatedCollection implements Buffer
{
	/**
	 * Factory method to create a predicated (validating) buffer.
	 * <p/>
	 * If there are any elements already in the buffer being decorated, they
	 * are validated.
	 *
	 * @param buffer    the buffer to decorate, must not be null
	 * @param predicate the predicate to use for validation, must not be null
	 * @return a new predicated Buffer
	 * @throws IllegalArgumentException if buffer or predicate is null
	 * @throws IllegalArgumentException if the buffer contains invalid elements
	 */
	public static Buffer decorate(Buffer buffer, Predicate predicate)
	{
		return new PredicatedBuffer(buffer, predicate);
	}

	/**
	 * Constructor that wraps (not copies).
	 * <p/>
	 * If there are any elements already in the collection being decorated, they
	 * are validated.
	 *
	 * @param buffer    the buffer to decorate, must not be null
	 * @param predicate the predicate to use for validation, must not be null
	 * @throws IllegalArgumentException if buffer or predicate is null
	 * @throws IllegalArgumentException if the buffer contains invalid elements
	 */
	protected PredicatedBuffer(Buffer buffer, Predicate predicate)
	{
		super(buffer, predicate);
	}

	/**
	 * Gets the buffer being decorated.
	 *
	 * @return the decorated buffer
	 */
	protected Buffer getBuffer()
	{
		return (Buffer) getCollection();
	}

	public Object get()
	{
		return getBuffer().get();
	}

	public Object remove()
	{
		return getBuffer().remove();
	}

}
