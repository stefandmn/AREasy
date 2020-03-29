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

import org.areasy.common.data.type.Predicate;

import java.io.Serializable;

/**
 * Predicate implementation that always throws an exception.
 *
 * @version $Id: ExceptionPredicate.java,v 1.2 2008/05/14 09:32:32 swd\stefan.damian Exp $
 */
public final class ExceptionPredicate implements Predicate, Serializable
{
	/**
	 * Singleton predicate instance
	 */
	public static final Predicate INSTANCE = new ExceptionPredicate();

	/**
	 * Factory returning the singleton instance.
	 *
	 * @return the singleton instance
	 */
	public static Predicate getInstance()
	{
		return INSTANCE;
	}

	/**
	 * Restricted constructor.
	 */
	private ExceptionPredicate()
	{
		super();
	}

	/**
	 * Evaluates the predicate always throwing an exception.
	 *
	 * @param object the input object
	 * @return never
	 * @throws org.areasy.common.data.workers.functors.FunctorException
	 *          always
	 */
	public boolean evaluate(Object object)
	{
		throw new FunctorException("ExceptionPredicate invoked");
	}

}
