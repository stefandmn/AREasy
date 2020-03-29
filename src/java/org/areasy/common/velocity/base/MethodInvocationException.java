package org.areasy.common.velocity.base;

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

import org.areasy.common.velocity.VelocityException;

/**
 * Application-level exception thrown when a reference method is
 * invoked and an exception is thrown.
 * <br>
 * When this exception is thrown, a best effort will be made to have
 * useful information in the exception's message.  For complete
 * information, consult the runtime log.
 *
 * @version $Id: MethodInvocationException.java,v 1.1 2008/05/25 22:33:17 swd\stefan.damian Exp $
 */
public class MethodInvocationException extends VelocityException
{
	private String methodName = "";
	private String referenceName = "";
	private Throwable wrapped = null;

	/**
	 * CTOR - wraps the passed in exception for
	 * examination later
	 *
	 * @param message message to printout
	 * @param e          Throwable that we are wrapping
	 * @param methodName name of method that threw the exception
	 */
	public MethodInvocationException(String message, Throwable e, String methodName)
	{
		super(message);
		this.wrapped = e;
		this.methodName = methodName;
	}

	/**
	 * Returns the name of the method that threw the
	 * exception
	 *
	 * @return String name of method
	 */
	public String getMethodName()
	{
		return methodName;
	}

	/**
	 * returns the wrapped Throwable that caused this
	 * MethodInvocationException to be thrown
	 *
	 * @return Throwable thrown by method invocation
	 */
	public Throwable getWrappedThrowable()
	{
		return wrapped;
	}

	/**
	 * Sets the reference name that threw this exception
	 *
	 * @param ref name of reference
	 */
	public void setReferenceName(String ref)
	{
		referenceName = ref;
	}

	/**
	 * Retrieves the name of the reference that caused the
	 * exception
	 *
	 * @return name of reference
	 */
	public String getReferenceName()
	{
		return referenceName;
	}
}
