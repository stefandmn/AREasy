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

import org.areasy.common.data.type.Transformer;

import java.io.Serializable;

/**
 * Transformer implementation that always throws an exception.
 *
 * @version $Id: ExceptionTransformer.java,v 1.2 2008/05/14 09:32:32 swd\stefan.damian Exp $
 */
public final class ExceptionTransformer implements Transformer, Serializable
{
	/**
	 * Singleton predicate instance
	 */
	public static final Transformer INSTANCE = new ExceptionTransformer();

	/**
	 * Factory returning the singleton instance.
	 *
	 * @return the singleton instance
	 */
	public static Transformer getInstance()
	{
		return INSTANCE;
	}

	/**
	 * Restricted constructor.
	 */
	private ExceptionTransformer()
	{
		super();
	}

	/**
	 * Transforms the input to result by cloning it.
	 *
	 * @param input the input object to transform
	 * @return never
	 * @throws FunctorException always
	 */
	public Object transform(Object input)
	{
		throw new FunctorException("ExceptionTransformer invoked");
	}

}
