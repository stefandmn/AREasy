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
import org.areasy.common.data.workers.functors.InstanceofPredicate;

/**
 * Decorates another <code>Buffer</code> to validate that elements added
 * are of a specific type.
 * <p/>
 * The validation of additions is performed via an instanceof test against
 * a specified <code>Class</code>. If an object cannot be added to the
 * collection, an IllegalArgumentException is thrown.
 *
 * @version $Id: TypedBuffer.java,v 1.2 2008/05/14 09:32:36 swd\stefan.damian Exp $
 */
public class TypedBuffer
{

	/**
	 * Factory method to create a typed list.
	 * <p/>
	 * If there are any elements already in the buffer being decorated, they
	 * are validated.
	 *
	 * @param buffer the buffer to decorate, must not be null
	 * @param type   the type to allow into the buffer, must not be null
	 * @return a new typed Buffer
	 * @throws IllegalArgumentException if buffer or type is null
	 * @throws IllegalArgumentException if the buffer contains invalid elements
	 */
	public static Buffer decorate(Buffer buffer, Class type)
	{
		return new PredicatedBuffer(buffer, InstanceofPredicate.getInstance(type));
	}

	/**
	 * Restrictive constructor.
	 */
	protected TypedBuffer()
	{
		super();
	}

}
