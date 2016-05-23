package org.areasy.common.data.workers.functors;

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

import org.areasy.common.data.type.Factory;

import java.io.Serializable;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

/**
 * Factory implementation that creates a new object instance by reflection.
 *
 * @version $Id: InstantiateFactory.java,v 1.2 2008/05/14 09:32:32 swd\stefan.damian Exp $
 */
public class InstantiateFactory implements Factory, Serializable
{
	/**
	 * The class to create
	 */
	private final Class iClassToInstantiate;
	/**
	 * The constructor parameter types
	 */
	private final Class[] iParamTypes;
	/**
	 * The constructor arguments
	 */
	private final Object[] iArgs;
	/**
	 * The constructor
	 */
	private transient Constructor iConstructor = null;

	/**
	 * Factory method that performs validation.
	 *
	 * @param classToInstantiate the class to instantiate, not null
	 * @param paramTypes         the constructor parameter types
	 * @param args               the constructor arguments
	 * @return a new instantiate factory
	 */
	public static Factory getInstance(Class classToInstantiate, Class[] paramTypes, Object[] args)
	{
		if (classToInstantiate == null)
		{
			throw new IllegalArgumentException("Class to instantiate must not be null");
		}
		if (((paramTypes == null) && (args != null))
				|| ((paramTypes != null) && (args == null))
				|| ((paramTypes != null) && (args != null) && (paramTypes.length != args.length)))
		{
			throw new IllegalArgumentException("Parameter types must match the arguments");
		}

		if (paramTypes == null || paramTypes.length == 0)
		{
			return new InstantiateFactory(classToInstantiate);
		}
		else
		{
			paramTypes = (Class[]) paramTypes.clone();
			args = (Object[]) args.clone();
			return new InstantiateFactory(classToInstantiate, paramTypes, args);
		}
	}

	/**
	 * Constructor that performs no validation.
	 * Use <code>getInstance</code> if you want that.
	 *
	 * @param classToInstantiate the class to instantiate
	 */
	public InstantiateFactory(Class classToInstantiate)
	{
		super();
		iClassToInstantiate = classToInstantiate;
		iParamTypes = null;
		iArgs = null;
		findConstructor();
	}

	/**
	 * Constructor that performs no validation.
	 * Use <code>getInstance</code> if you want that.
	 *
	 * @param classToInstantiate the class to instantiate
	 * @param paramTypes         the constructor parameter types, not cloned
	 * @param args               the constructor arguments, not cloned
	 */
	public InstantiateFactory(Class classToInstantiate, Class[] paramTypes, Object[] args)
	{
		super();
		iClassToInstantiate = classToInstantiate;
		iParamTypes = paramTypes;
		iArgs = args;
		findConstructor();
	}

	/**
	 * Find the Constructor for the class specified.
	 */
	private void findConstructor()
	{
		try
		{
			iConstructor = iClassToInstantiate.getConstructor(iParamTypes);

		}
		catch (NoSuchMethodException ex)
		{
			throw new IllegalArgumentException("InstantiateFactory: The constructor must exist and be public ");
		}
	}

	/**
	 * Creates an object using the stored constructor.
	 *
	 * @return the new object
	 */
	public Object create()
	{
		// needed for post-serialization
		if (iConstructor == null)
		{
			findConstructor();
		}

		try
		{
			return iConstructor.newInstance(iArgs);

		}
		catch (InstantiationException ex)
		{
			throw new FunctorException("InstantiateFactory: InstantiationException", ex);
		}
		catch (IllegalAccessException ex)
		{
			throw new FunctorException("InstantiateFactory: Constructor must be public", ex);
		}
		catch (InvocationTargetException ex)
		{
			throw new FunctorException("InstantiateFactory: Constructor threw an exception", ex);
		}
	}

}
