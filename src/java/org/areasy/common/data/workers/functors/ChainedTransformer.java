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

import org.areasy.common.data.FunctorUtility;
import org.areasy.common.data.type.Transformer;

import java.io.Serializable;
import java.util.Collection;
import java.util.Iterator;

/**
 * Transformer implementation that chains the specified transformers together.
 * <p/>
 * The input object is passed to the first transformer. The transformed result
 * is passed to the second transformer and so on.
 *
 * @version $Id: ChainedTransformer.java,v 1.2 2008/05/14 09:32:32 swd\stefan.damian Exp $
 */
public class ChainedTransformer implements Transformer, Serializable
{
	/**
	 * The transformers to call in turn
	 */
	private final Transformer[] iTransformers;

	/**
	 * Factory method that performs validation and copies the parameter array.
	 *
	 * @param transformers the transformers to chain, copied, no nulls
	 * @return the <code>chained</code> transformer
	 * @throws IllegalArgumentException if the transformers array is null
	 * @throws IllegalArgumentException if any transformer in the array is null
	 */
	public static Transformer getInstance(Transformer[] transformers)
	{
		FunctorUtility.validate(transformers);
		if (transformers.length == 0)
		{
			return NOPTransformer.INSTANCE;
		}
		transformers = FunctorUtility.copy(transformers);
		return new ChainedTransformer(transformers);
	}

	/**
	 * Create a new Transformer that calls each transformer in turn, passing the
	 * result into the next transformer. The ordering is that of the iterator()
	 * method on the collection.
	 *
	 * @param transformers a collection of transformers to chain
	 * @return the <code>chained</code> transformer
	 * @throws IllegalArgumentException if the transformers collection is null
	 * @throws IllegalArgumentException if any transformer in the collection is null
	 */
	public static Transformer getInstance(Collection transformers)
	{
		if (transformers == null)
		{
			throw new IllegalArgumentException("Transformer collection must not be null");
		}
		if (transformers.size() == 0)
		{
			return NOPTransformer.INSTANCE;
		}
		// convert to array like this to guarantee iterator() ordering
		Transformer[] cmds = new Transformer[transformers.size()];
		int i = 0;
		for (Iterator it = transformers.iterator(); it.hasNext();)
		{
			cmds[i++] = (Transformer) it.next();
		}
		FunctorUtility.validate(cmds);
		return new ChainedTransformer(cmds);
	}

	/**
	 * Factory method that performs validation.
	 *
	 * @param transformer1 the first transformer, not null
	 * @param transformer2 the second transformer, not null
	 * @return the <code>chained</code> transformer
	 * @throws IllegalArgumentException if either transformer is null
	 */
	public static Transformer getInstance(Transformer transformer1, Transformer transformer2)
	{
		if (transformer1 == null || transformer2 == null)
		{
			throw new IllegalArgumentException("Transformers must not be null");
		}
		Transformer[] transformers = new Transformer[]{transformer1, transformer2};
		return new ChainedTransformer(transformers);
	}

	/**
	 * Constructor that performs no validation.
	 * Use <code>getInstance</code> if you want that.
	 *
	 * @param transformers the transformers to chain, not copied, no nulls
	 */
	public ChainedTransformer(Transformer[] transformers)
	{
		super();
		iTransformers = transformers;
	}

	/**
	 * Transforms the input to result via each decorated transformer
	 *
	 * @param object the input object passed to the first transformer
	 * @return the transformed result
	 */
	public Object transform(Object object)
	{
		for (int i = 0; i < iTransformers.length; i++)
		{
			object = iTransformers[i].transform(object);
		}
		return object;
	}

	/**
	 * Gets the transformers, do not modify the array.
	 *
	 * @return the transformers
	 */
	public Transformer[] getTransformers()
	{
		return iTransformers;
	}

}
