package org.areasy.common.data.workers.functors;

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

import org.areasy.common.data.type.Closure;

import java.io.Serializable;

/**
 * Closure implementation that does nothing.
 *
 * @version $Id: NOPClosure.java,v 1.2 2008/05/14 09:32:32 swd\stefan.damian Exp $
 */
public class NOPClosure implements Closure, Serializable
{
	/**
	 * Singleton predicate instance
	 */
	public static final Closure INSTANCE = new NOPClosure();

	/**
	 * Factory returning the singleton instance.
	 *
	 * @return the singleton instance
	 */
	public static Closure getInstance()
	{
		return INSTANCE;
	}

	/**
	 * Constructor
	 */
	private NOPClosure()
	{
		super();
	}

	/**
	 * Do nothing.
	 *
	 * @param input the input object
	 */
	public void execute(Object input)
	{
		// do nothing
	}

}
