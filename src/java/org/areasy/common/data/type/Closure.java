package org.areasy.common.data.type;

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

/**
 * Defines a functor interface implemented by classes that do something.
 * <p/>
 * A <code>Closure</code>  represents a block of code which is executed from
 * inside some block, function or iteration. It operates an input object.
 * <p/>
 * Standard implementations of common closures are provided by
 * {@link org.areasy.common.data.ClosureUtility}. These include method invokation and for/while loops.
 *
 * @version $Id: Closure.java,v 1.2 2008/05/14 09:32:34 swd\stefan.damian Exp $
 */
public interface Closure
{

	/**
	 * Performs an action on the specified input object.
	 *
	 * @param input the input to execute on
	 * @throws ClassCastException       (runtime) if the input is the wrong class
	 * @throws IllegalArgumentException (runtime) if the input is invalid
	 * @throws org.areasy.common.data.workers.functors.FunctorException
	 *                                  (runtime) if any other error occurs
	 */
	public void execute(Object input);

}
