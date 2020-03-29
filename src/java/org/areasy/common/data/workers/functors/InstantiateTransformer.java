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

import org.areasy.common.data.type.Transformer;

import java.io.Serializable;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

/**
 * Transformer implementation that creates a new object instance by reflection.
 *
 * @version $Id: InstantiateTransformer.java,v 1.2 2008/05/14 09:32:32 swd\stefan.damian Exp $
 */
public class InstantiateTransformer implements Transformer, Serializable
{
	/**
	 * Singleton instance that uses the no arg constructor
	 */
	public static final Transformer NO_ARG_INSTANCE = new InstantiateTransformer();

	/**
	 * The constructor parameter types
	 */
	private final Class[] iParamTypes;
	/**
	 * The constructor arguments
	 */
	private final Object[] iArgs;

	/**
	 * Transformer method that performs validation.
	 *
	 * @param paramTypes the constructor parameter types
	 * @param args       the constructor arguments
	 * @return an instantiate transformer
	 */
	public static Transformer getInstance(Class[] paramTypes, Object[] args)
	{
		if (((paramTypes == null) && (args != null))
				|| ((paramTypes != null) && (args == null))
				|| ((paramTypes != null) && (args != null) && (paramTypes.length != args.length)))
		{
			throw new IllegalArgumentException("Parameter types must match the arguments");
		}

		if (paramTypes == null || paramTypes.length == 0)
		{
			return NO_ARG_INSTANCE;
		}
		else
		{
			paramTypes = (Class[]) paramTypes.clone();
			args = (Object[]) args.clone();
		}
		return new InstantiateTransformer(paramTypes, args);
	}

	/**
	 * Constructor for no arg instance.
	 */
	private InstantiateTransformer()
	{
		super();
		iParamTypes = null;
		iArgs = null;
	}

	/**
	 * Constructor that performs no validation.
	 * Use <code>getInstance</code> if you want that.
	 *
	 * @param paramTypes the constructor parameter types, not cloned
	 * @param args       the constructor arguments, not cloned
	 */
	public InstantiateTransformer(Class[] paramTypes, Object[] args)
	{
		super();
		iParamTypes = paramTypes;
		iArgs = args;
	}

	/**
	 * Transforms the input Class object to a result by instantiation.
	 *
	 * @param input the input object to transform
	 * @return the transformed result
	 */
	public Object transform(Object input)
	{
		try
		{
			if (input instanceof Class == false)
			{
				throw new FunctorException("InstantiateTransformer: Input object was not an instanceof Class, it was a "
						+ (input == null ? "null object" : input.getClass().getName()));
			}
			Constructor con = ((Class) input).getConstructor(iParamTypes);
			return con.newInstance(iArgs);

		}
		catch (NoSuchMethodException ex)
		{
			throw new FunctorException("InstantiateTransformer: The constructor must exist and be public ");
		}
		catch (InstantiationException ex)
		{
			throw new FunctorException("InstantiateTransformer: InstantiationException", ex);
		}
		catch (IllegalAccessException ex)
		{
			throw new FunctorException("InstantiateTransformer: Constructor must be public", ex);
		}
		catch (InvocationTargetException ex)
		{
			throw new FunctorException("InstantiateTransformer: Constructor threw an exception", ex);
		}
	}

}
