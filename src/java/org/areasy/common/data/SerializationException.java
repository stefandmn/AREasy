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

import org.areasy.common.errors.NestableRuntimeException;

/**
 * <p>Exception thrown when the Serialization process fails.</p>
 * <p/>
 * <p>The original error is wrapped within this one.</p>
 *
 * @version $Id: SerializationException.java,v 1.2 2008/05/14 09:32:29 swd\stefan.damian Exp $
 */
public class SerializationException extends NestableRuntimeException
{

	/**
	 * <p>Constructs a new <code>SerializationException</code> without specified
	 * detail message.</p>
	 */
	public SerializationException()
	{
		super();
	}

	/**
	 * <p>Constructs a new <code>SerializationException</code> with specified
	 * detail message.</p>
	 *
	 * @param msg The error message.
	 */
	public SerializationException(String msg)
	{
		super(msg);
	}

	/**
	 * <p>Constructs a new <code>SerializationException</code> with specified
	 * nested <code>Throwable</code>.</p>
	 *
	 * @param cause The <code>Exception</code> or <code>Error</code>
	 *              that caused this exception to be thrown.
	 */
	public SerializationException(Throwable cause)
	{
		super(cause);
	}

	/**
	 * <p>Constructs a new <code>SerializationException</code> with specified
	 * detail message and nested <code>Throwable</code>.</p>
	 *
	 * @param msg   The error message.
	 * @param cause The <code>Exception</code> or <code>Error</code>
	 *              that caused this exception to be thrown.
	 */
	public SerializationException(String msg, Throwable cause)
	{
		super(msg, cause);
	}

}
