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

import org.areasy.common.data.type.Transformer;

import java.io.Serializable;

/**
 * Transformer implementation that returns a clone of the input object.
 * <p/>
 * Clone is performed using <code>PrototypeFactory.getInstance(input).create()</code>.
 *
 * @version $Id: CloneTransformer.java,v 1.2 2008/05/14 09:32:32 swd\stefan.damian Exp $
 */
public class CloneTransformer implements Transformer, Serializable
{
	/**
	 * Singleton predicate instance
	 */
	public static final Transformer INSTANCE = new CloneTransformer();

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
	 * Constructor
	 */
	private CloneTransformer()
	{
		super();
	}

	/**
	 * Transforms the input to result by cloning it.
	 *
	 * @param input the input object to transform
	 * @return the transformed result
	 */
	public Object transform(Object input)
	{
		if (input == null)
		{
			return null;
		}
		return PrototypeFactory.getInstance(input).create();
	}

}
