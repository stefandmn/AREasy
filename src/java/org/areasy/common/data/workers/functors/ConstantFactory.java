package org.areasy.common.data.workers.functors;

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

import org.areasy.common.data.type.Factory;

import java.io.Serializable;

/**
 * Factory implementation that returns the same constant each time.
 * <p/>
 * No check is made that the object is immutable. In general, only immutable
 * objects should use the constant factory. Mutable objects should
 * use the prototype factory.
 *
 * @version $Id: ConstantFactory.java,v 1.2 2008/05/14 09:32:32 swd\stefan.damian Exp $
 */
public class ConstantFactory implements Factory, Serializable
{
	/**
	 * Returns null each time
	 */
	public static final Factory NULL_INSTANCE = new ConstantFactory(null);

	/**
	 * The closures to call in turn
	 */
	private final Object iConstant;

	/**
	 * Factory method that performs validation.
	 *
	 * @param constantToReturn the constant object to return each time in the factory
	 * @return the <code>constant</code> factory.
	 */
	public static Factory getInstance(Object constantToReturn)
	{
		if (constantToReturn == null)
		{
			return NULL_INSTANCE;
		}
		return new ConstantFactory(constantToReturn);
	}

	/**
	 * Constructor that performs no validation.
	 * Use <code>getInstance</code> if you want that.
	 *
	 * @param constantToReturn the constant to return each time
	 */
	public ConstantFactory(Object constantToReturn)
	{
		super();
		iConstant = constantToReturn;
	}

	/**
	 * Always return constant.
	 *
	 * @return the stored constant value
	 */
	public Object create()
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
