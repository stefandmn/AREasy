package org.areasy.common.data.workers.functors;

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

import java.io.PrintStream;
import java.io.PrintWriter;

/**
 * Runtime exception thrown from functors.
 * If required, a root cause error can be wrapped within this one.
 *
 * @version $Id: FunctorException.java,v 1.2 2008/05/14 09:32:32 swd\stefan.damian Exp $
 */
public class FunctorException extends RuntimeException
{

	/**
	 * Does JDK support nested exceptions
	 */
	private static final boolean JDK_SUPPORTS_NESTED;

	static
	{
		boolean flag = false;
		try
		{
			Throwable.class.getDeclaredMethod("getCause", new Class[0]);
			flag = true;
		}
		catch (NoSuchMethodException ex)
		{
			flag = false;
		}
		JDK_SUPPORTS_NESTED = flag;
	}

	/**
	 * Root cause of the exception
	 */
	private final Throwable rootCause;

	/**
	 * Constructs a new <code>FunctorException</code> without specified
	 * detail message.
	 */
	public FunctorException()
	{
		super();
		this.rootCause = null;
	}

	/**
	 * Constructs a new <code>FunctorException</code> with specified
	 * detail message.
	 *
	 * @param msg the error message.
	 */
	public FunctorException(String msg)
	{
		super(msg);
		this.rootCause = null;
	}

	/**
	 * Constructs a new <code>FunctorException</code> with specified
	 * nested <code>Throwable</code> root cause.
	 *
	 * @param rootCause the exception or error that caused this exception
	 *                  to be thrown.
	 */
	public FunctorException(Throwable rootCause)
	{
		super((rootCause == null ? null : rootCause.getMessage()));
		this.rootCause = rootCause;
	}

	/**
	 * Constructs a new <code>FunctorException</code> with specified
	 * detail message and nested <code>Throwable</code> root cause.
	 *
	 * @param msg       the error message.
	 * @param rootCause the exception or error that caused this exception
	 *                  to be thrown.
	 */
	public FunctorException(String msg, Throwable rootCause)
	{
		super(msg);
		this.rootCause = rootCause;
	}

	/**
	 * Gets the cause of this throwable.
	 *
	 * @return the cause of this throwable, or <code>null</code>
	 */
	public Throwable getCause()
	{
		return rootCause;
	}

	/**
	 * Prints the stack trace of this exception to the standard error stream.
	 */
	public void printStackTrace()
	{
		printStackTrace(System.err);
	}

	/**
	 * Prints the stack trace of this exception to the specified stream.
	 *
	 * @param out the <code>PrintStream</code> to use for output
	 */
	public void printStackTrace(PrintStream out)
	{
		synchronized (out)
		{
			PrintWriter pw = new PrintWriter(out, false);
			printStackTrace(pw);
			// Flush the PrintWriter before it's GC'ed.
			pw.flush();
		}
	}

	/**
	 * Prints the stack trace of this exception to the specified writer.
	 *
	 * @param out the <code>PrintWriter</code> to use for output
	 */
	public void printStackTrace(PrintWriter out)
	{
		synchronized (out)
		{
			super.printStackTrace(out);
			if (rootCause != null && JDK_SUPPORTS_NESTED == false)
			{
				out.print("Caused by: ");
				rootCause.printStackTrace(out);
			}
		}
	}

}
