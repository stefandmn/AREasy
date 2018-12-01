package org.areasy.common.data.type.buffer;

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

import org.areasy.common.data.type.Buffer;
import org.areasy.common.data.type.Transformer;
import org.areasy.common.data.type.collection.TransformedCollection;

/**
 * Decorates another <code>Buffer</code> to transform objects that are added.
 * <p/>
 * The add methods are affected by this class.
 * Thus objects must be removed or searched for using their transformed form.
 * For example, if the transformation converts Strings to Integers, you must
 * use the Integer form to remove objects.
 *
 * @version $Id: TransformedBuffer.java,v 1.2 2008/05/14 09:32:36 swd\stefan.damian Exp $
 */
public class TransformedBuffer extends TransformedCollection implements Buffer
{
	/**
	 * Factory method to create a transforming buffer.
	 * <p/>
	 * If there are any elements already in the buffer being decorated, they
	 * are NOT transformed.
	 *
	 * @param buffer      the buffer to decorate, must not be null
	 * @param transformer the transformer to use for conversion, must not be null
	 * @return a new transformed Buffer
	 * @throws IllegalArgumentException if buffer or transformer is null
	 */
	public static Buffer decorate(Buffer buffer, Transformer transformer)
	{
		return new TransformedBuffer(buffer, transformer);
	}

	/**
	 * Constructor that wraps (not copies).
	 * <p/>
	 * If there are any elements already in the buffer being decorated, they
	 * are NOT transformed.
	 *
	 * @param buffer      the buffer to decorate, must not be null
	 * @param transformer the transformer to use for conversion, must not be null
	 * @throws IllegalArgumentException if buffer or transformer is null
	 */
	protected TransformedBuffer(Buffer buffer, Transformer transformer)
	{
		super(buffer, transformer);
	}

	/**
	 * Gets the decorated buffer.
	 *
	 * @return the decorated buffer
	 */
	protected Buffer getBuffer()
	{
		return (Buffer) collection;
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
