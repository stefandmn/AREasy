package org.areasy.common.errors;

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

import java.io.PrintStream;
import java.io.PrintWriter;

/**
 * The base class of all errors which can contain other exceptions.
 *
 * @version $Id: NestableError.java,v 1.2 2008/05/14 09:32:32 swd\stefan.damian Exp $
 * @see org.areasy.common.errors.NestableException
 */
public class NestableError extends Error implements Nestable
{

	/**
	 * The helper instance which contains much of the code which we
	 * delegate to.
	 */
	protected NestableDelegate delegate = new NestableDelegate(this);

	/**
	 * Holds the reference to the exception or error that caused
	 * this exception to be thrown.
	 */
	private Throwable cause = null;

	/**
	 * Constructs a new <code>NestableError</code> without specified
	 * detail message.
	 */
	public NestableError()
	{
		super();
	}

	/**
	 * Constructs a new <code>NestableError</code> with specified
	 * detail message.
	 *
	 * @param msg The error message.
	 */
	public NestableError(String msg)
	{
		super(msg);
	}

	/**
	 * Constructs a new <code>NestableError</code> with specified
	 * nested <code>Throwable</code>.
	 *
	 * @param cause the exception or error that caused this exception to be
	 *              thrown
	 */
	public NestableError(Throwable cause)
	{
		super();
		this.cause = cause;
	}

	/**
	 * Constructs a new <code>NestableError</code> with specified
	 * detail message and nested <code>Throwable</code>.
	 *
	 * @param msg   the error message
	 * @param cause the exception or error that caused this exception to be
	 *              thrown
	 */
	public NestableError(String msg, Throwable cause)
	{
		super(msg);
		this.cause = cause;
	}

	public Throwable getCause()
	{
		return cause;
	}

	/**
	 * Returns the detail message string of this throwable. If it was
	 * created with a null message, returns the following:
	 * (cause==null ? null : cause.toString()).
	 *
	 * @return String message string of the throwable
	 */
	public String getMessage()
	{
		if (super.getMessage() != null) return super.getMessage();
			else if (cause != null) return cause.toString();
			else return null;
	}

	public String getMessage(int index)
	{
		if (index == 0) return super.getMessage();
			else return delegate.getMessage(index);
	}

	public String[] getMessages()
	{
		return delegate.getMessages();
	}

	public Throwable getThrowable(int index)
	{
		return delegate.getThrowable(index);
	}

	public int getThrowableCount()
	{
		return delegate.getThrowableCount();
	}

	public Throwable[] getThrowables()
	{
		return delegate.getThrowables();
	}

	public int indexOfThrowable(Class type)
	{
		return delegate.indexOfThrowable(type, 0);
	}

	public int indexOfThrowable(Class type, int fromIndex)
	{
		return delegate.indexOfThrowable(type, fromIndex);
	}

	public void printStackTrace()
	{
		delegate.printStackTrace();
	}

	public void printStackTrace(PrintStream out)
	{
		delegate.printStackTrace(out);
	}

	public void printStackTrace(PrintWriter out)
	{
		delegate.printStackTrace(out);
	}

	public final void printPartialStackTrace(PrintWriter out)
	{
		super.printStackTrace(out);
	}
}
