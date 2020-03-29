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

import java.io.Serializable;

/**
 * Closure implementation that calls another closure n times, like a for loop.
 *
 * @version $Id: ForClosure.java,v 1.2 2008/05/14 09:32:32 swd\stefan.damian Exp $
 */
public class ForClosure implements Closure, Serializable
{

	/**
	 * The number of times to loop
	 */
	private final int iCount;
	/**
	 * The closure to call
	 */
	private final Closure iClosure;

	/**
	 * Factory method that performs validation.
	 * <p/>
	 * A null closure or zero count returns the <code>NOPClosure</code>.
	 * A count of one returns the specified closure.
	 *
	 * @param count   the number of times to execute the closure
	 * @param closure the closure to execute, not null
	 * @return the <code>for</code> closure
	 */
	public static Closure getInstance(int count, Closure closure)
	{
		if (count <= 0 || closure == null)
		{
			return NOPClosure.INSTANCE;
		}
		if (count == 1)
		{
			return closure;
		}
		return new ForClosure(count, closure);
	}

	/**
	 * Constructor that performs no validation.
	 * Use <code>getInstance</code> if you want that.
	 *
	 * @param count   the number of times to execute the closure
	 * @param closure the closure to execute, not null
	 */
	public ForClosure(int count, Closure closure)
	{
		super();
		iCount = count;
		iClosure = closure;
	}

	/**
	 * Executes the closure <code>count</code> times.
	 *
	 * @param input the input object
	 */
	public void execute(Object input)
	{
		for (int i = 0; i < iCount; i++)
		{
			iClosure.execute(input);
		}
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
	 * Gets the count.
	 *
	 * @return the count
	 */
	public int getCount()
	{
		return iCount;
	}

}
