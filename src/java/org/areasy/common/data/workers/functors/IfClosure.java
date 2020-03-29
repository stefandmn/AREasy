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
import org.areasy.common.data.type.Predicate;

import java.io.Serializable;

/**
 * Closure implementation acts as an if statement calling one or other closure
 * based on a predicate.
 *
 * @version $Id: IfClosure.java,v 1.2 2008/05/14 09:32:32 swd\stefan.damian Exp $
 */
public class IfClosure implements Closure, Serializable
{
	/**
	 * The test
	 */
	private final Predicate iPredicate;
	/**
	 * The closure to use if true
	 */
	private final Closure iTrueClosure;
	/**
	 * The closure to use if false
	 */
	private final Closure iFalseClosure;

	/**
	 * Factory method that performs validation.
	 *
	 * @param predicate    predicate to switch on
	 * @param trueClosure  closure used if true
	 * @param falseClosure closure used if false
	 * @return the <code>if</code> closure
	 * @throws IllegalArgumentException if any argument is null
	 */
	public static Closure getInstance(Predicate predicate, Closure trueClosure, Closure falseClosure)
	{
		if (predicate == null)
		{
			throw new IllegalArgumentException("Predicate must not be null");
		}
		if (trueClosure == null || falseClosure == null)
		{
			throw new IllegalArgumentException("Closures must not be null");
		}
		return new IfClosure(predicate, trueClosure, falseClosure);
	}

	/**
	 * Constructor that performs no validation.
	 * Use <code>getInstance</code> if you want that.
	 *
	 * @param predicate    predicate to switch on, not null
	 * @param trueClosure  closure used if true, not null
	 * @param falseClosure closure used if false, not null
	 */
	public IfClosure(Predicate predicate, Closure trueClosure, Closure falseClosure)
	{
		super();
		iPredicate = predicate;
		iTrueClosure = trueClosure;
		iFalseClosure = falseClosure;
	}

	/**
	 * Executes the true or false closure accoring to the result of the predicate.
	 *
	 * @param input the input object
	 */
	public void execute(Object input)
	{
		if (iPredicate.evaluate(input) == true)
		{
			iTrueClosure.execute(input);
		}
		else
		{
			iFalseClosure.execute(input);
		}
	}

	/**
	 * Gets the predicate.
	 *
	 * @return the predicate
	 */
	public Predicate getPredicate()
	{
		return iPredicate;
	}

	/**
	 * Gets the closure called when true.
	 *
	 * @return the closure
	 */
	public Closure getTrueClosure()
	{
		return iTrueClosure;
	}

	/**
	 * Gets the closure called when false.
	 *
	 * @return the closure
	 */
	public Closure getFalseClosure()
	{
		return iFalseClosure;
	}

}
