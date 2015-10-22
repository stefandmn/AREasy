package org.areasy.common.data;

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

import org.areasy.common.data.type.Factory;
import org.areasy.common.data.workers.functors.ConstantFactory;
import org.areasy.common.data.workers.functors.ExceptionFactory;
import org.areasy.common.data.workers.functors.InstantiateFactory;
import org.areasy.common.data.workers.functors.PrototypeFactory;

/**
 * <code>FactoryUtility</code> provides reference implementations and utilities
 * for the Factory functor interface. The supplied factories are:
 * <ul>
 * <li>Prototype - clones a specified object
 * <li>Reflection - creates objects using reflection
 * <li>Constant - always returns the same object
 * <li>Null - always returns null
 * <li>Exception - always throws an exception
 * </ul>
 * All the supplied factories are Serializable.
 *
 * @version $Id: FactoryUtility.java,v 1.2 2008/05/14 09:32:29 swd\stefan.damian Exp $
 */
public class FactoryUtility
{

	/**
	 * This class is not normally instantiated.
	 */
	public FactoryUtility()
	{
		super();
	}

	/**
	 * Gets a Factory that always throws an exception.
	 * This could be useful during testing as a placeholder.
	 *
	 * @return the factory
	 * @see org.areasy.common.data.workers.functors.ExceptionFactory
	 */
	public static Factory exceptionFactory()
	{
		return ExceptionFactory.INSTANCE;
	}

	/**
	 * Gets a Factory that will return null each time the factory is used.
	 * This could be useful during testing as a placeholder.
	 *
	 * @return the factory
	 * @see org.areasy.common.data.workers.functors.ConstantFactory
	 */
	public static Factory nullFactory()
	{
		return ConstantFactory.NULL_INSTANCE;
	}

	/**
	 * Creates a Factory that will return the same object each time the factory
	 * is used. No check is made that the object is immutable. In general, only
	 * immutable objects should use the constant factory. Mutable objects should
	 * use the prototype factory.
	 *
	 * @param constantToReturn the constant object to return each time in the factory
	 * @return the <code>constant</code> factory.
	 * @see org.areasy.common.data.workers.functors.ConstantFactory
	 */
	public static Factory constantFactory(Object constantToReturn)
	{
		return ConstantFactory.getInstance(constantToReturn);
	}

	/**
	 * Creates a Factory that will return a clone of the same prototype object
	 * each time the factory is used. The prototype will be cloned using one of these
	 * techniques (in order):
	 * <ul>
	 * <li>public clone method
	 * <li>public copy constructor
	 * <li>serialization clone
	 * <ul>
	 *
	 * @param prototype the object to clone each time in the factory
	 * @return the <code>prototype</code> factory
	 * @throws IllegalArgumentException if the prototype is null
	 * @throws IllegalArgumentException if the prototype cannot be cloned
	 * @see org.areasy.common.data.workers.functors.PrototypeFactory
	 */
	public static Factory prototypeFactory(Object prototype)
	{
		return PrototypeFactory.getInstance(prototype);
	}

	/**
	 * Creates a Factory that can create objects of a specific type using
	 * a no-args constructor.
	 *
	 * @param classToInstantiate the Class to instantiate each time in the factory
	 * @return the <code>reflection</code> factory
	 * @throws IllegalArgumentException if the classToInstantiate is null
	 * @see org.areasy.common.data.workers.functors.InstantiateFactory
	 */
	public static Factory instantiateFactory(Class classToInstantiate)
	{
		return InstantiateFactory.getInstance(classToInstantiate, null, null);
	}

	/**
	 * Creates a Factory that can create objects of a specific type using
	 * the arguments specified to this method.
	 *
	 * @param classToInstantiate the Class to instantiate each time in the factory
	 * @param paramTypes         parameter types for the constructor, can be null
	 * @param args               the arguments to pass to the constructor, can be null
	 * @return the <code>reflection</code> factory
	 * @throws IllegalArgumentException if the classToInstantiate is null
	 * @throws IllegalArgumentException if the paramTypes and args don't match
	 * @throws IllegalArgumentException if the constructor doesn't exist
	 * @see org.areasy.common.data.workers.functors.InstantiateFactory
	 */
	public static Factory instantiateFactory(Class classToInstantiate, Class[] paramTypes, Object[] args)
	{
		return InstantiateFactory.getInstance(classToInstantiate, paramTypes, args);
	}

}
