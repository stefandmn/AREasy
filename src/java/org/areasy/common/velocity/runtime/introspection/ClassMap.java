package org.areasy.common.velocity.runtime.introspection;

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

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Hashtable;
import java.util.Map;

/**
 * A cache of introspection information for a specific class instance.
 * Keys {@link java.lang.reflect.Method} objects by a concatenation of the
 * method name and the names of classes that make up the parameters.
 *
 * @version $Id: ClassMap.java,v 1.1 2008/05/25 22:33:14 swd\stefan.damian Exp $
 */
public class ClassMap
{
	private static final class CacheMissing
	{
	}

	private static final CacheMissing CACHE_MISSING = new CacheMissing();
	private static final Object OBJECT = new Object();

	/**
	 * Class passed into the constructor used to as
	 * the basis for the Method map.
	 */

	private Class clazz;

	/**
	 * Cache of Methods, or CACHE_MISS, keyed by method
	 * name and actual arguments used to find it.
	 */
	private Map methodCache = new Hashtable();

	private MethodMap methodMap = new MethodMap();

	/**
	 * Standard constructor
	 */
	public ClassMap(Class clazz)
	{
		this.clazz = clazz;
		populateMethodCache();
	}

	/**
	 * @return the class object whose methods are cached by this map.
	 */
	Class getCachedClass()
	{
		return clazz;
	}

	/**
	 * Find a Method using the methodKey
	 * provided.
	 * <p/>
	 * Look in the methodMap for an entry.  If found,
	 * it'll either be a CACHE_MISS, in which case we
	 * simply give up, or it'll be a Method, in which
	 * case, we return it.
	 * <p/>
	 * If nothing is found, then we must actually go
	 * and introspect the method from the MethodMap.
	 */
	public Method findMethod(String name, Object[] params)
			throws MethodMap.AmbiguousException
	{
		String methodKey = makeMethodKey(name, params);
		Object cacheEntry = methodCache.get(methodKey);

		if (cacheEntry == CACHE_MISSING)
		{
			return null;
		}

		if (cacheEntry == null)
		{
			try
			{
				cacheEntry = methodMap.find(name,
						params);
			}
			catch (MethodMap.AmbiguousException ae)
			{
				/*
				 *  that's a miss :)
				 */

				methodCache.put(methodKey,
						CACHE_MISSING);

				throw ae;
			}

			if (cacheEntry == null)
			{
				methodCache.put(methodKey,
						CACHE_MISSING);
			}
			else
			{
				methodCache.put(methodKey,
						cacheEntry);
			}
		}

		// Yes, this might just be null.

		return (Method) cacheEntry;
	}

	/**
	 * Populate the Map of direct hits. These
	 * are taken from all the public methods
	 * that our class provides.
	 */
	private void populateMethodCache()
	{
		Method[] methods = getAccessibleMethods(clazz);

		for (int i = 0; i < methods.length; i++)
		{
			Method method = methods[i];

			/*
			 *  now get the 'public method', the method declared by a
			 *  public interface or class. (because the actual implementing
			 *  class may be a facade...
			 */

			Method publicMethod = getPublicMethod(method);

			/*
			 *  it is entirely possible that there is no public method for
			 *  the methods of this class (i.e. in the facade, a method
			 *  that isn't on any of the interfaces or superclass
			 *  in which case, ignore it.  Otherwise, map and cache
			 */

			if (publicMethod != null)
			{
				methodMap.add(publicMethod);
				methodCache.put(makeMethodKey(publicMethod), publicMethod);
			}
		}
	}

	/**
	 * Make a methodKey for the given method using
	 * the concatenation of the name and the
	 * types of the method parameters.
	 */
	private String makeMethodKey(Method method)
	{
		Class[] parameterTypes = method.getParameterTypes();

		StringBuffer methodKey = new StringBuffer(method.getName());

		for (int j = 0; j < parameterTypes.length; j++)
		{
			/*
			 * If the argument type is primitive then we want
			 * to convert our primitive type signature to the
			 * corresponding Object type so introspection for
			 * methods with primitive types will work correctly.
			 */
			if (parameterTypes[j].isPrimitive())
			{
				if (parameterTypes[j].equals(Boolean.TYPE))
				{
					methodKey.append("java.lang.Boolean");
				}
				else if (parameterTypes[j].equals(Byte.TYPE))
				{
					methodKey.append("java.lang.Byte");
				}
				else if (parameterTypes[j].equals(Character.TYPE))
				{
					methodKey.append("java.lang.Character");
				}
				else if (parameterTypes[j].equals(Double.TYPE))
				{
					methodKey.append("java.lang.Double");
				}
				else if (parameterTypes[j].equals(Float.TYPE))
				{
					methodKey.append("java.lang.Float");
				}
				else if (parameterTypes[j].equals(Integer.TYPE))
				{
					methodKey.append("java.lang.Integer");
				}
				else if (parameterTypes[j].equals(Long.TYPE))
				{
					methodKey.append("java.lang.Long");
				}
				else if (parameterTypes[j].equals(Short.TYPE))
				{
					methodKey.append("java.lang.Short");
				}
			}
			else
			{
				methodKey.append(parameterTypes[j].getName());
			}
		}

		return methodKey.toString();
	}

	private static String makeMethodKey(String method, Object[] params)
	{
		StringBuffer methodKey = new StringBuffer().append(method);

		for (int j = 0; j < params.length; j++)
		{
			Object arg = params[j];

			if (arg == null)
			{
				arg = OBJECT;
			}

			methodKey.append(arg.getClass().getName());
		}

		return methodKey.toString();
	}

	/**
	 * Retrieves public methods for a class. In case the class is not
	 * public, retrieves methods with same signature as its public methods
	 * from public superclasses and interfaces (if they exist). Basically
	 * upcasts every method to the nearest acccessible method.
	 */
	private static Method[] getAccessibleMethods(Class clazz)
	{
		Method[] methods = clazz.getMethods();

		/*
		 *  Short circuit for the (hopefully) majority of cases where the
		 *  clazz is public
		 */

		if (Modifier.isPublic(clazz.getModifiers()))
		{
			return methods;
		}

		/*
		 *  No luck - the class is not public, so we're going the longer way.
		 */

		MethodInformation[] methodInfos = new MethodInformation[methods.length];

		for (int i = methods.length; i-- > 0;)
		{
			methodInfos[i] = new MethodInformation(methods[i]);
		}

		int upcastCount = getAccessibleMethods(clazz, methodInfos, 0);

		/*
		 *  Reallocate array in case some method had no accessible counterpart.
		 */

		if (upcastCount < methods.length)
		{
			methods = new Method[upcastCount];
		}

		int j = 0;
		for (int i = 0; i < methodInfos.length; ++i)
		{
			MethodInformation methodInformation = methodInfos[i];
			if (methodInformation.upcast)
			{
				methods[j++] = methodInformation.method;
			}
		}
		return methods;
	}

	/**
	 * Recursively finds a match for each method, starting with the class, and then
	 * searching the superclass and interfaces.
	 *
	 * @param clazz       Class to check
	 * @param methodInfos array of methods we are searching to match
	 * @param upcastCount current number of methods we have matched
	 * @return count of matched methods
	 */
	private static int getAccessibleMethods(Class clazz, MethodInformation[] methodInfos, int upcastCount)
	{
		int l = methodInfos.length;

		/*
		 *  if this class is public, then check each of the currently
		 *  'non-upcasted' methods to see if we have a match
		 */

		if (Modifier.isPublic(clazz.getModifiers()))
		{
			for (int i = 0; i < l && upcastCount < l; ++i)
			{
				try
				{
					MethodInformation methodInformation = methodInfos[i];

					if (!methodInformation.upcast)
					{
						methodInformation.tryUpcasting(clazz);
						upcastCount++;
					}
				}
				catch (NoSuchMethodException e)
				{
					/*
					 *  Intentionally ignored - it means
					 *  it wasn't found in the current class
					 */
				}
			}

			/*
			 *  Short circuit if all methods were upcast
			 */

			if (upcastCount == l)
			{
				return upcastCount;
			}
		}

		/*
		 *   Examine superclass
		 */

		Class superclazz = clazz.getSuperclass();

		if (superclazz != null)
		{
			upcastCount = getAccessibleMethods(superclazz, methodInfos, upcastCount);

			/*
			 *  Short circuit if all methods were upcast
			 */

			if (upcastCount == l)
			{
				return upcastCount;
			}
		}

		/*
		 *  Examine interfaces. Note we do it even if superclazz == null.
		 *  This is redundant as currently java.lang.Object does not implement
		 *  any interfaces, however nothing guarantees it will not in future.
		 */

		Class[] interfaces = clazz.getInterfaces();

		for (int i = interfaces.length; i-- > 0;)
		{
			upcastCount = getAccessibleMethods(interfaces[i], methodInfos, upcastCount);

			/*
			 *  Short circuit if all methods were upcast
			 */

			if (upcastCount == l)
			{
				return upcastCount;
			}
		}

		return upcastCount;
	}

	/**
	 * For a given method, retrieves its publicly accessible counterpart.
	 * This method will look for a method with same name
	 * and signature declared in a public superclass or implemented interface of this
	 * method's declaring class. This counterpart method is publicly callable.
	 *
	 * @param method a method whose publicly callable counterpart is requested.
	 * @return the publicly callable counterpart method. Note that if the parameter
	 *         method is itself declared by a public class, this method is an identity
	 *         function.
	 */
	public static Method getPublicMethod(Method method)
	{
		Class clazz = method.getDeclaringClass();

		if ((clazz.getModifiers() & Modifier.PUBLIC) != 0) return method;

		return getPublicMethod(clazz, method.getName(), method.getParameterTypes());
	}

	/**
	 * Looks up the method with specified name and signature in the first public
	 * superclass or implemented interface of the class.
	 *
	 * @param name       the name of the method
	 * @param paramTypes the classes of method parameters
	 */
	private static Method getPublicMethod(Class clazz, String name, Class[] paramTypes)
	{

		if ((clazz.getModifiers() & Modifier.PUBLIC) != 0)
		{
			try
			{
				return clazz.getMethod(name, paramTypes);
			}
			catch (NoSuchMethodException e)
			{
				return null;
			}
		}

		Class superclazz = clazz.getSuperclass();

		if (superclazz != null)
		{
			Method superclazzMethod = getPublicMethod(superclazz, name, paramTypes);

			if (superclazzMethod != null)
			{
				return superclazzMethod;
			}
		}

		Class[] interfaces = clazz.getInterfaces();

		for (int i = 0; i < interfaces.length; ++i)
		{
			Method interfaceMethod = getPublicMethod(interfaces[i], name, paramTypes);

			if (interfaceMethod != null)
			{
				return interfaceMethod;
			}
		}

		return null;
	}

	/**
	 * Used for the iterative discovery process for public methods.
	 */
	private static final class MethodInformation
	{
		Method method;
		String name;
		Class[] parameterTypes;
		boolean upcast;

		MethodInformation(Method method)
		{
			this.method = null;
			name = method.getName();
			parameterTypes = method.getParameterTypes();
			upcast = false;
		}

		void tryUpcasting(Class clazz) throws NoSuchMethodException
		{
			method = clazz.getMethod(name, parameterTypes);
			name = null;
			parameterTypes = null;
			upcast = true;
		}
	}
}
