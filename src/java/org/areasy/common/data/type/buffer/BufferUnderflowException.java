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

import java.util.NoSuchElementException;

/**
 * The BufferUnderflowException is used when the buffer is already empty.
 * <p/>
 * NOTE: From version 3.0, this exception extends NoSuchElementException.
 *
 * @version $Id: BufferUnderflowException.java,v 1.3 2008/05/20 06:52:55 swd\stefan.damian Exp $
 */
public class BufferUnderflowException extends NoSuchElementException
{
	/**
	 * The root cause throwable
	 */
	private final Throwable throwable;

	/**
	 * Constructs a new <code>BufferUnderflowException</code>.
	 */
	public BufferUnderflowException()
	{
		super();
		throwable = null;
	}

	/**
	 * Construct a new <code>BufferUnderflowException</code>.
	 *
	 * @param message the detail message for this exception
	 */
	public BufferUnderflowException(String message)
	{
		this(message, null);
	}

	/**
	 * Construct a new <code>BufferUnderflowException</code>.
	 *
	 * @param message   the detail message for this exception
	 * @param exception the root cause of the exception
	 */
	public BufferUnderflowException(String message, Throwable exception)
	{
		super(message);
		throwable = exception;
	}

	/**
	 * Gets the root cause of the exception.
	 *
	 * @return the root cause
	 */
	public final Throwable getCause()
	{
		return throwable;
	}

}
