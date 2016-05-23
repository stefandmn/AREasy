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
import org.areasy.common.data.type.collection.AbstractCollectionDecorator;

/**
 * Decorates another <code>Buffer</code> to provide additional behaviour.
 * <p/>
 * Methods are forwarded directly to the decorated buffer.
 *
 * @version $Id: AbstractBufferDecorator.java,v 1.2 2008/05/14 09:32:36 swd\stefan.damian Exp $
 */
public abstract class AbstractBufferDecorator extends AbstractCollectionDecorator implements Buffer
{

	/**
	 * Constructor only used in deserialization, do not use otherwise.
	 *
	 */
	protected AbstractBufferDecorator()
	{
		super();
	}

	/**
	 * Constructor that wraps (not copies).
	 *
	 * @param buffer the buffer to decorate, must not be null
	 * @throws IllegalArgumentException if list is null
	 */
	protected AbstractBufferDecorator(Buffer buffer)
	{
		super(buffer);
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
