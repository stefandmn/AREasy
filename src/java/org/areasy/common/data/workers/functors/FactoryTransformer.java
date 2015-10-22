package org.areasy.common.data.workers.functors;

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

import org.areasy.common.data.type.Factory;
import org.areasy.common.data.type.Transformer;

import java.io.Serializable;

/**
 * Transformer implementation that calls a Factory and returns the result.
 *
 * @version $Id: FactoryTransformer.java,v 1.2 2008/05/14 09:32:32 swd\stefan.damian Exp $
 */
public class FactoryTransformer implements Transformer, Serializable
{
	/**
	 * The factory to wrap
	 */
	private final Factory iFactory;

	/**
	 * Factory method that performs validation.
	 *
	 * @param factory the factory to call, not null
	 * @return the <code>factory</code> transformer
	 * @throws IllegalArgumentException if the factory is null
	 */
	public static Transformer getInstance(Factory factory)
	{
		if (factory == null)
		{
			throw new IllegalArgumentException("Factory must not be null");
		}
		return new FactoryTransformer(factory);
	}

	/**
	 * Constructor that performs no validation.
	 * Use <code>getInstance</code> if you want that.
	 *
	 * @param factory the factory to call, not null
	 */
	public FactoryTransformer(Factory factory)
	{
		super();
		iFactory = factory;
	}

	/**
	 * Transforms the input by ignoring the input and returning the result of
	 * calling the decorated factory.
	 *
	 * @param input the input object to transform
	 * @return the transformed result
	 */
	public Object transform(Object input)
	{
		return iFactory.create();
	}

	/**
	 * Gets the factory.
	 *
	 * @return the factory
	 */
	public Factory getFactory()
	{
		return iFactory;
	}

}
