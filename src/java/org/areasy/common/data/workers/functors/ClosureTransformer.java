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
import org.areasy.common.data.type.Transformer;

import java.io.Serializable;

/**
 * Transformer implementation that calls a Closure using the input object
 * and then returns the input.
 *
 * @version $Id: ClosureTransformer.java,v 1.2 2008/05/14 09:32:32 swd\stefan.damian Exp $
 */
public class ClosureTransformer implements Transformer, Serializable
{
	/**
	 * The closure to wrap
	 */
	private final Closure iClosure;

	/**
	 * Factory method that performs validation.
	 *
	 * @param closure the closure to call, not null
	 * @return the <code>closure</code> transformer
	 * @throws IllegalArgumentException if the closure is null
	 */
	public static Transformer getInstance(Closure closure)
	{
		if (closure == null)
		{
			throw new IllegalArgumentException("Closure must not be null");
		}
		return new ClosureTransformer(closure);
	}

	/**
	 * Constructor that performs no validation.
	 * Use <code>getInstance</code> if you want that.
	 *
	 * @param closure the closure to call, not null
	 */
	public ClosureTransformer(Closure closure)
	{
		super();
		iClosure = closure;
	}

	/**
	 * Transforms the input to result by executing a closure.
	 *
	 * @param input the input object to transform
	 * @return the transformed result
	 */
	public Object transform(Object input)
	{
		iClosure.execute(input);
		return input;
	}

	/**
	 * Gets the closure.
	 *
	 * @return the closure
	 */
	public Closure getClosure()
	{
		return iClosure;
	}

}
