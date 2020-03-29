package org.areasy.common.velocity.runtime.parser.node;

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


import org.areasy.common.logger.Logger;
import org.areasy.common.logger.LoggerFactory;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * Abstract class that is used to execute an arbitrary
 * method that is in introspected. This is the superclass
 * for the GetExecutor and PropertyExecutor.
 *
 * @version $Id: AbstractExecutor.java,v 1.1 2008/05/25 22:33:08 swd\stefan.damian Exp $
 */
public abstract class AbstractExecutor
{
	/** the logger */
	protected static Logger logger = LoggerFactory.getLog(PropertyExecutor.class.getName());

	/**
	 * Method to be executed.
	 */
	protected Method method = null;

	/**
	 * Execute method against context.
	 */
	public abstract Object execute(Object o) throws IllegalAccessException, InvocationTargetException;

	/**
	 * Tell whether the executor is alive by looking
	 * at the value of the method.
	 */
	public boolean isAlive()
	{
		return (method != null);
	}

	public Method getMethod()
	{
		return method;
	}
}
