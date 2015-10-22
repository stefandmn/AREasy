package org.areasy.common.velocity.runtime.introspection;

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
 * Method used for regular method invocation
 * <p/>
 * $foo.bar()
 *
 * @version $Id: VelocityMethod.java,v 1.1 2008/05/25 22:33:15 swd\stefan.damian Exp $
 */
public interface VelocityMethod
{
	/**
	 * invocation method - called when the method invocationshould be
	 * preformed and a value returned
	 */
	public Object invoke(Object o, Object[] params) throws Exception;

	/**
	 * specifies if this VelMethod is cacheable and able to be
	 * reused for this class of object it was returned for
	 *
	 * @return true if can be reused for this class, false if not
	 */
	public boolean isCacheable();

	/**
	 * returns the method name used
	 */
	public String getMethodName();

	/**
	 * returns the return type of the method invoked
	 */
	public Class getReturnType();
}
