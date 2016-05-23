package org.areasy.common.logger;

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

/**
 * <p>An exception that is thrown only if a suitable <code>LoggerFactory</code>
 * or <code>Logger</code> instance cannot be created by the corresponding
 * factory methods.</p>
 */

public class LoggerException extends RuntimeException
{

	/**
	 * The underlying cause of this exception.
	 */
	protected Throwable cause = null;

	/**
	 * Construct a new exception with <code>null</code> as its detail message.
	 */
	public LoggerException()
	{
		super();
	}

	/**
	 * Construct a new exception with the specified detail message.
	 *
	 * @param message The detail message
	 */
	public LoggerException(String message)
	{
		super(message);
	}

	/**
	 * Construct a new exception with the specified cause and a derived
	 * detail message.
	 *
	 * @param cause The underlying cause
	 */
	public LoggerException(Throwable cause)
	{
		this((cause == null) ? null : cause.toString(), cause);
	}


	/**
	 * Construct a new exception with the specified detail message and cause.
	 *
	 * @param message The detail message
	 * @param cause   The underlying cause
	 */
	public LoggerException(String message, Throwable cause)
	{
		super(message + " (Caused by " + cause + ")");
		this.cause = cause; // Two-argument version requires JDK 1.4 or later
	}

	/**
	 * Return the underlying cause of this exception (if any).
	 */
	public Throwable getCause()
	{
		return (this.cause);
	}
}
