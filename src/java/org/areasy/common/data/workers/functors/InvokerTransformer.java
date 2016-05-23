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

import org.areasy.common.data.type.Transformer;

import java.io.Serializable;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * Transformer implementation that creates a new object instance by reflection.
 *
 * @version $Id: InvokerTransformer.java,v 1.2 2008/05/14 09:32:32 swd\stefan.damian Exp $
 */
public class InvokerTransformer implements Transformer, Serializable
{
	/**
	 * The method name to call
	 */
	private final String iMethodName;
	/**
	 * The array of reflection parameter types
	 */
	private final Class[] iParamTypes;
	/**
	 * The array of reflection arguments
	 */
	private final Object[] iArgs;

	/**
	 * Gets an instance of this transformer calling a specific method with no arguments.
	 *
	 * @param methodName the method name to call
	 * @return an invoker transformer
	 */
	public static Transformer getInstance(String methodName)
	{
		if (methodName == null)
		{
			throw new IllegalArgumentException("The method to invoke must not be null");
		}
		return new InvokerTransformer(methodName);
	}

	/**
	 * Gets an instance of this transformer calling a specific method with specific values.
	 *
	 * @param methodName the method name to call
	 * @param paramTypes the parameter types of the method
	 * @param args       the arguments to pass to the method
	 * @return an invoker transformer
	 */
	public static Transformer getInstance(String methodName, Class[] paramTypes, Object[] args)
	{
		if (methodName == null)
		{
			throw new IllegalArgumentException("The method to invoke must not be null");
		}
		if (((paramTypes == null) && (args != null))
				|| ((paramTypes != null) && (args == null))
				|| ((paramTypes != null) && (args != null) && (paramTypes.length != args.length)))
		{
			throw new IllegalArgumentException("The parameter types must match the arguments");
		}
		if (paramTypes == null || paramTypes.length == 0)
		{
			return new InvokerTransformer(methodName);
		}
		else
		{
			paramTypes = (Class[]) paramTypes.clone();
			args = (Object[]) args.clone();
			return new InvokerTransformer(methodName, paramTypes, args);
		}
	}

	/**
	 * Constructor for no arg instance.
	 *
	 * @param methodName the method to call
	 */
	private InvokerTransformer(String methodName)
	{
		super();
		iMethodName = methodName;
		iParamTypes = null;
		iArgs = null;
	}

	/**
	 * Constructor that performs no validation.
	 * Use <code>getInstance</code> if you want that.
	 *
	 * @param methodName the method to call
	 * @param paramTypes the constructor parameter types, not cloned
	 * @param args       the constructor arguments, not cloned
	 */
	public InvokerTransformer(String methodName, Class[] paramTypes, Object[] args)
	{
		super();
		iMethodName = methodName;
		iParamTypes = paramTypes;
		iArgs = args;
	}

	/**
	 * Transforms the input to result by invoking a method on the input.
	 *
	 * @param input the input object to transform
	 * @return the transformed result, null if null input
	 */
	public Object transform(Object input)
	{
		if (input == null)
		{
			return null;
		}
		try
		{
			Class cls = input.getClass();
			Method method = cls.getMethod(iMethodName, iParamTypes);
			return method.invoke(input, iArgs);

		}
		catch (NoSuchMethodException ex)
		{
			throw new FunctorException("InvokerTransformer: The method '" + iMethodName + "' on '" + input.getClass() + "' does not exist");
		}
		catch (IllegalAccessException ex)
		{
			throw new FunctorException("InvokerTransformer: The method '" + iMethodName + "' on '" + input.getClass() + "' cannot be accessed");
		}
		catch (InvocationTargetException ex)
		{
			throw new FunctorException("InvokerTransformer: The method '" + iMethodName + "' on '" + input.getClass() + "' threw an exception", ex);
		}
	}

}
