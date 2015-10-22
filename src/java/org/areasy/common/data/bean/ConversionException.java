package org.areasy.common.data.bean;

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

/**
 * <p>A <strong>ConversionException</strong> indicates that a call to
 * <code>Converter.convert()</code> has failed to complete successfully.
 *
 * @version $Id: ConversionException.java,v 1.2 2008/05/14 09:32:37 swd\stefan.damian Exp $
 */

public class ConversionException extends RuntimeException
{
	/**
	 * Construct a new exception with the specified message.
	 *
	 * @param message The message describing this exception
	 */
	public ConversionException(String message)
	{
		super(message);
	}


	/**
	 * Construct a new exception with the specified message and root cause.
	 *
	 * @param message The message describing this exception
	 * @param cause   The root cause of this exception
	 */
	public ConversionException(String message, Throwable cause)
	{
		super(message);
		this.cause = cause;
	}


	/**
	 * Construct a new exception with the specified root cause.
	 *
	 * @param cause The root cause of this exception
	 */
	public ConversionException(Throwable cause)
	{
		super(cause.getMessage());
		this.cause = cause;
	}

	/**
	 * The root cause of this <code>ConversionException</code>, compatible with
	 * JDK 1.4's extensions to <code>java.lang.Throwable</code>.
	 */
	protected Throwable cause = null;

	public Throwable getCause()
	{
		return (this.cause);
	}
}
