package org.areasy.common.data.type;

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

/**
 * Defines a functor interface implemented by classes that perform a predicate
 * test on an object.
 * <p/>
 * A <code>Predicate</code> is the object equivalent of an <code>if</code> statement.
 * It uses the input object to return a true or false value, and is often used in
 * validation or filtering.
 * <p/>
 * Standard implementations of common predicates are provided by
 * {@link org.areasy.common.data.PredicateUtility}. These include true, false, instanceof, equals, and,
 * or, not, method invokation and null testing.
 *
 * @version $Id: Predicate.java,v 1.2 2008/05/14 09:32:34 swd\stefan.damian Exp $
 */
public interface Predicate
{

	/**
	 * Use the specified parameter to perform a test that returns true or false.
	 *
	 * @param object the object to evaluate, should not be changed
	 * @return true or false
	 * @throws ClassCastException       (runtime) if the input is the wrong class
	 * @throws IllegalArgumentException (runtime) if the input is invalid
	 * @throws org.areasy.common.data.workers.functors.FunctorException
	 *                                  (runtime) if the predicate encounters a problem
	 */
	public boolean evaluate(Object object);

}
