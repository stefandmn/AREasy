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

import org.areasy.common.data.type.Closure;
import org.areasy.common.data.type.Predicate;

import java.io.Serializable;

/**
 * Closure implementation that executes a closure repeatedly until a condition is met,
 * like a do-while or while loop.
 *
 * @version $Id: WhileClosure.java,v 1.2 2008/05/14 09:32:32 swd\stefan.damian Exp $
 */
public class WhileClosure implements Closure, Serializable
{
	/**
	 * The test condition
	 */
	private final Predicate iPredicate;
	/**
	 * The closure to call
	 */
	private final Closure iClosure;
	/**
	 * The flag, true is a do loop, false is a while
	 */
	private final boolean iDoLoop;

	/**
	 * Factory method that performs validation.
	 *
	 * @param predicate the predicate used to evaluate when the loop terminates, not null
	 * @param closure   the closure the execute, not null
	 * @param doLoop    true to act as a do-while loop, always executing the closure once
	 * @return the <code>while</code> closure
	 * @throws IllegalArgumentException if the predicate or closure is null
	 */
	public static Closure getInstance(Predicate predicate, Closure closure, boolean doLoop)
	{
		if (predicate == null)
		{
			throw new IllegalArgumentException("Predicate must not be null");
		}
		if (closure == null)
		{
			throw new IllegalArgumentException("Closure must not be null");
		}
		return new WhileClosure(predicate, closure, doLoop);
	}

	/**
	 * Constructor that performs no validation.
	 * Use <code>getInstance</code> if you want that.
	 *
	 * @param predicate the predicate used to evaluate when the loop terminates, not null
	 * @param closure   the closure the execute, not null
	 * @param doLoop    true to act as a do-while loop, always executing the closure once
	 */
	public WhileClosure(Predicate predicate, Closure closure, boolean doLoop)
	{
		super();
		iPredicate = predicate;
		iClosure = closure;
		iDoLoop = doLoop;
	}

	/**
	 * Executes the closure until the predicate is false.
	 *
	 * @param input the input object
	 */
	public void execute(Object input)
	{
		if (iDoLoop)
		{
			iClosure.execute(input);
		}
		while (iPredicate.evaluate(input))
		{
			iClosure.execute(input);
		}
	}

	/**
	 * Gets the predicate in use.
	 *
	 * @return the predicate
	 */
	public Predicate getPredicate()
	{
		return iPredicate;
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

	/**
	 * Is the loop a do-while loop.
	 *
	 * @return true is do-while, false if while
	 */
	public boolean isDoLoop()
	{
		return iDoLoop;
	}

}
