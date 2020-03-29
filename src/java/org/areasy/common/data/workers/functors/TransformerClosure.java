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

import org.areasy.common.data.type.Closure;
import org.areasy.common.data.type.Transformer;

import java.io.Serializable;

/**
 * Closure implementation that calls a Transformer using the input object
 * and ignore the result.
 *
 * @version $Id: TransformerClosure.java,v 1.2 2008/05/14 09:32:32 swd\stefan.damian Exp $
 */
public class TransformerClosure implements Closure, Serializable
{
	/**
	 * The transformer to wrap
	 */
	private final Transformer iTransformer;

	/**
	 * Factory method that performs validation.
	 * <p/>
	 * A null transformer will return the <code>NOPClosure</code>.
	 *
	 * @param transformer the transformer to call, null means nop
	 * @return the <code>transformer</code> closure
	 */
	public static Closure getInstance(Transformer transformer)
	{
		if (transformer == null) return NOPClosure.INSTANCE;

		return new TransformerClosure(transformer);
	}

	/**
	 * Constructor that performs no validation.
	 * Use <code>getInstance</code> if you want that.
	 *
	 * @param transformer the transformer to call, not null
	 */
	public TransformerClosure(Transformer transformer)
	{
		super();
		iTransformer = transformer;
	}

	/**
	 * Executes the closure by calling the decorated transformer.
	 *
	 * @param input the input object
	 */
	public void execute(Object input)
	{
		iTransformer.transform(input);
	}

	/**
	 * Gets the transformer.
	 *
	 * @return the transformer
	 */
	public Transformer getTransformer()
	{
		return iTransformer;
	}

}
