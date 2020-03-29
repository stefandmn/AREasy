package org.areasy.runtime.engine.base;

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

import org.areasy.common.errors.NestableException;

/**
 * The base class of all exceptions thrown by this library.
 * <p/>
 * The main engine for error traces is <code>org.areasy.common.nestable.NestableException</code> library.
 *
 */
public class AREasyException extends NestableException
{
	/**
	 * Constructs a new <code>AREasyException</code> without specified
	 * detail message.
	 */
	public AREasyException()
	{
		super();
	}

	/**
	 * Constructs a new <code>AREasyException</code> with specified
	 * detail message.
	 *
	 * @param msg The error message.
	 */
	public AREasyException(String msg)
	{
		super(msg);
	}

	/**
	 * Constructs a new <code>AREasyException</code> with specified
	 * nested <code>Throwable</code>.
	 *
	 * @param nested The exception or error that caused this exception
	 *               to be thrown.
	 */
	public AREasyException(Throwable nested)
	{
		super(nested);
	}

	/**
	 * Constructs a new <code>AREasyException</code> with specified
	 * detail message and nested <code>Throwable</code>.
	 *
	 * @param msg	The error message.
	 * @param nested The exception or error that caused this exception
	 *               to be thrown.
	 */
	public AREasyException(String msg, Throwable nested)
	{
		super(msg, nested);
	}
}
