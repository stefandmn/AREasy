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
 * Transformer implementation that returns the same constant each time.
 * <p/>
 * No check is made that the object is immutable. In general, only immutable
 * objects should use the constant factory. Mutable objects should
 * use the prototype factory.
 *
 * @version $Id: ConstantTransformer.java,v 1.2 2008/05/14 09:32:32 swd\stefan.damian Exp $
 */
public class ConstantTransformer implements Transformer, Serializable
{
	/**
	 * Returns null each time
	 */
	public static final Transformer NULL_INSTANCE = new ConstantTransformer(null);

	/**
	 * The closures to call in turn
	 */
	private final Object iConstant;

	/**
	 * Transformer method that performs validation.
	 *
	 * @param constantToReturn the constant object to return each time in the factory
	 * @return the <code>constant</code> factory.
	 */
	public static Transformer getInstance(Object constantToReturn)
	{
		if (constantToReturn == null)
		{
			return NULL_INSTANCE;
		}
		return new ConstantTransformer(constantToReturn);
	}

	/**
	 * Constructor that performs no validation.
	 * Use <code>getInstance</code> if you want that.
	 *
	 * @param constantToReturn the constant to return each time
	 */
	public ConstantTransformer(Object constantToReturn)
	{
		super();
		iConstant = constantToReturn;
	}

	/**
	 * Transforms the input by ignoring it and returning the stored constant instead.
	 *
	 * @param input the input object which is ignored
	 * @return the stored constant
	 */
	public Object transform(Object input)
	{
		return iConstant;
	}

	/**
	 * Gets the constant.
	 *
	 * @return the constant
	 */
	public Object getConstant()
	{
		return iConstant;
	}

}
