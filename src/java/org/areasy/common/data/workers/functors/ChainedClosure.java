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

import org.areasy.common.data.FunctorUtility;
import org.areasy.common.data.type.Closure;

import java.io.Serializable;
import java.util.Collection;
import java.util.Iterator;

/**
 * Closure implementation that chains the specified closures together.
 *
 * @version $Id: ChainedClosure.java,v 1.2 2008/05/14 09:32:31 swd\stefan.damian Exp $
 */
public class ChainedClosure implements Closure, Serializable
{
	/**
	 * The closures to call in turn
	 */
	private final Closure[] iClosures;

	/**
	 * Factory method that performs validation and copies the parameter array.
	 *
	 * @param closures the closures to chain, copied, no nulls
	 * @return the <code>chained</code> closure
	 * @throws IllegalArgumentException if the closures array is null
	 * @throws IllegalArgumentException if any closure in the array is null
	 */
	public static Closure getInstance(Closure[] closures)
	{
		FunctorUtility.validate(closures);
		if (closures.length == 0)
		{
			return NOPClosure.INSTANCE;
		}
		closures = FunctorUtility.copy(closures);
		return new ChainedClosure(closures);
	}

	/**
	 * Create a new Closure that calls each closure in turn, passing the
	 * result into the next closure. The ordering is that of the iterator()
	 * method on the collection.
	 *
	 * @param closures a collection of closures to chain
	 * @return the <code>chained</code> closure
	 * @throws IllegalArgumentException if the closures collection is null
	 * @throws IllegalArgumentException if any closure in the collection is null
	 */
	public static Closure getInstance(Collection closures)
	{
		if (closures == null)
		{
			throw new IllegalArgumentException("Closure collection must not be null");
		}
		if (closures.size() == 0)
		{
			return NOPClosure.INSTANCE;
		}
		// convert to array like this to guarantee iterator() ordering
		Closure[] cmds = new Closure[closures.size()];
		int i = 0;
		for (Iterator it = closures.iterator(); it.hasNext();)
		{
			cmds[i++] = (Closure) it.next();
		}
		FunctorUtility.validate(cmds);
		return new ChainedClosure(cmds);
	}

	/**
	 * Factory method that performs validation.
	 *
	 * @param closure1 the first closure, not null
	 * @param closure2 the second closure, not null
	 * @return the <code>chained</code> closure
	 * @throws IllegalArgumentException if either closure is null
	 */
	public static Closure getInstance(Closure closure1, Closure closure2)
	{
		if (closure1 == null || closure2 == null)
		{
			throw new IllegalArgumentException("Closures must not be null");
		}
		Closure[] closures = new Closure[]{closure1, closure2};
		return new ChainedClosure(closures);
	}

	/**
	 * Constructor that performs no validation.
	 * Use <code>getInstance</code> if you want that.
	 *
	 * @param closures the closures to chain, not copied, no nulls
	 */
	public ChainedClosure(Closure[] closures)
	{
		super();
		iClosures = closures;
	}

	/**
	 * Execute a list of closures.
	 *
	 * @param input the input object passed to each closure
	 */
	public void execute(Object input)
	{
		for (int i = 0; i < iClosures.length; i++)
		{
			iClosures[i].execute(input);
		}
	}

	/**
	 * Gets the closures, do not modify the array.
	 *
	 * @return the closures
	 */
	public Closure[] getClosures()
	{
		return iClosures;
	}

}
