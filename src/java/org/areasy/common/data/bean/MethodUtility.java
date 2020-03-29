package org.areasy.common.data.bean;


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

import org.areasy.common.logger.Logger;
import org.areasy.common.logger.LoggerFactory;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.WeakHashMap;


/**
 * <p> Utility reflection methods focussed on methods in general rather than properties in particular. </p>
 * <p/>
 * <h3>Known Limitations</h3>
 * <h4>Accessing Public Methods In A Default Access Superclass</h4>
 * <p>There is an issue when invoking public methods contained in a default access superclass.
 * Reflection locates these methods fine and correctly assigns them as public.
 * However, an <code>IllegalAccessException</code> is thrown if the method is invoked.</p>
 * <p/>
 * <p><code>MethodUtils</code> contains a workaround for this situation.
 * It will attempt to call <code>setAccessible</code> on this method.
 * If this call succeeds, then the method can be invoked as normal.
 * This call will only succeed when the application has sufficient security privilages.
 * If this call fails then a warning will be logged and the method may fail.</p>
 *
 * @version $Id: MethodUtility.java,v 1.2 2008/05/14 09:32:37 swd\stefan.damian Exp $
 */

public class MethodUtility
{
	/**
	 * All logging goes through this logger
	 */
	private static Logger log = LoggerFactory.getLog(MethodUtility.class);
	/**
	 * Only log warning about accessibility work around once
	 */
	private static boolean loggedAccessibleWarning = false;

	/**
	 * An empty class array
	 */
	private static final Class[] emptyClassArray = new Class[0];
	/**
	 * An empty object array
	 */
	private static final Object[] emptyObjectArray = new Object[0];

	/**
	 * Stores a cache of Methods against MethodDescriptors, in a WeakHashMap.
	 */
	private static WeakHashMap cache = new WeakHashMap();

	/**
	 * <p>Invoke a named method whose parameter type matches the object type.</p>
	 * <p/>
	 * <p>The behaviour of this method is less deterministic
	 * than {@link #invokeExactMethod}.
	 * It loops through all methods with names that match
	 * and then executes the first it finds with compatable parameters.</p>
	 * <p/>
	 * <p>This method supports calls to methods taking primitive parameters
	 * via passing in wrapping classes. So, for example, a <code>Boolean</code> class
	 * would match a <code>boolean</code> primitive.</p>
	 * <p/>
	 * <p> This is a convenient wrapper for
	 * {@link #invokeMethod(Object object,String methodName,Object [] args)}.
	 * </p>
	 *
	 * @param object     invoke method on this object
	 * @param methodName get method with this name
	 * @param arg        use this argument
	 * @throws NoSuchMethodException     if there is no such accessible method
	 * @throws InvocationTargetException wraps an exception thrown by the
	 *                                   method invoked
	 * @throws IllegalAccessException    if the requested method is not accessible
	 *                                   via reflection
	 */
	public static Object invokeMethod(Object object,  String methodName, Object arg) throws NoSuchMethodException, IllegalAccessException, InvocationTargetException
	{
		Object[] args = {arg};
		return invokeMethod(object, methodName, args);
	}


	/**
	 * <p>Invoke a named method whose parameter type matches the object type.</p>
	 * <p/>
	 * <p>The behaviour of this method is less deterministic
	 * than {@link #invokeExactMethod(Object object,String methodName,Object [] args)}.
	 * It loops through all methods with names that match
	 * and then executes the first it finds with compatable parameters.</p>
	 * <p/>
	 * <p>This method supports calls to methods taking primitive parameters
	 * via passing in wrapping classes. So, for example, a <code>Boolean</code> class
	 * would match a <code>boolean</code> primitive.</p>
	 * <p/>
	 *
	 * @param object     invoke method on this object
	 * @param methodName get method with this name
	 * @param args       use these arguments - treat null as empty array
	 * @throws NoSuchMethodException     if there is no such accessible method
	 * @throws InvocationTargetException wraps an exception thrown by the
	 *                                   method invoked
	 * @throws IllegalAccessException    if the requested method is not accessible
	 *                                   via reflection
	 */
	public static Object invokeMethod(Object object, String methodName, Object[] args) throws NoSuchMethodException, IllegalAccessException, InvocationTargetException
	{
		if (args == null)
		{
			args = emptyObjectArray;
		}

		int arguments = args.length;
		Class parameterTypes [] = new Class[arguments];

		for (int i = 0; i < arguments; i++)
		{
			parameterTypes[i] = args[i].getClass();
		}

		return invokeMethod(object, methodName, args, parameterTypes);

	}


	/**
	 * <p>Invoke a named method whose parameter type matches the object type.</p>
	 * <p/>
	 * <p>The behaviour of this method is less deterministic
	 * than {@link
	 * #invokeExactMethod(Object object,String methodName,Object [] args,Class[] parameterTypes)}.
	 * It loops through all methods with names that match
	 * and then executes the first it finds with compatable parameters.</p>
	 * <p/>
	 * <p>This method supports calls to methods taking primitive parameters
	 * via passing in wrapping classes. So, for example, a <code>Boolean</code> class
	 * would match a <code>boolean</code> primitive.</p>
	 *
	 * @param object         invoke method on this object
	 * @param methodName     get method with this name
	 * @param args           use these arguments - treat null as empty array
	 * @param parameterTypes match these parameters - treat null as empty array
	 * @throws NoSuchMethodException     if there is no such accessible method
	 * @throws InvocationTargetException wraps an exception thrown by the
	 *                                   method invoked
	 * @throws IllegalAccessException    if the requested method is not accessible
	 *                                   via reflection
	 */
	public static Object invokeMethod(Object object, String methodName,  Object[] args, Class[] parameterTypes) throws NoSuchMethodException, IllegalAccessException, InvocationTargetException
	{
		if (parameterTypes == null)
		{
			parameterTypes = emptyClassArray;
		}

		if (args == null)
		{
			args = emptyObjectArray;
		}

		Method method = getMatchingAccessibleMethod(object.getClass(), methodName, parameterTypes);

		if (method == null)
		{
			throw new NoSuchMethodException("No such accessible method: " + methodName + "() on object: " + object.getClass().getName());
		}

		return method.invoke(object, args);
	}


	/**
	 * <p>Invoke a method whose parameter type matches exactly the object
	 * type.</p>
	 * <p/>
	 * <p> This is a convenient wrapper for
	 * {@link #invokeExactMethod(Object object,String methodName,Object [] args)}.
	 * </p>
	 *
	 * @param object     invoke method on this object
	 * @param methodName get method with this name
	 * @param arg        use this argument
	 * @throws NoSuchMethodException     if there is no such accessible method
	 * @throws InvocationTargetException wraps an exception thrown by the
	 *                                   method invoked
	 * @throws IllegalAccessException    if the requested method is not accessible
	 *                                   via reflection
	 */
	public static Object invokeExactMethod(Object object, String methodName,  Object arg) throws NoSuchMethodException, IllegalAccessException, InvocationTargetException
	{
		Object[] args = {arg};

		return invokeExactMethod(object, methodName, args);
	}


	/**
	 * <p>Invoke a method whose parameter types match exactly the object
	 * types.</p>
	 * <p/>
	 * <p> This uses reflection to invoke the method obtained from a call to
	 * {@link #getAccessibleMethod}.</p>
	 *
	 * @param object     invoke method on this object
	 * @param methodName get method with this name
	 * @param args       use these arguments - treat null as empty array
	 * @throws NoSuchMethodException     if there is no such accessible method
	 * @throws InvocationTargetException wraps an exception thrown by the
	 *                                   method invoked
	 * @throws IllegalAccessException    if the requested method is not accessible
	 *                                   via reflection
	 */
	public static Object invokeExactMethod(Object object, String methodName, Object[] args) throws NoSuchMethodException, IllegalAccessException, InvocationTargetException
	{
		if (args == null)
		{
			args = emptyObjectArray;
		}

		int arguments = args.length;
		Class parameterTypes [] = new Class[arguments];

		for (int i = 0; i < arguments; i++)
		{
			parameterTypes[i] = args[i].getClass();
		}

		return invokeExactMethod(object, methodName, args, parameterTypes);
	}


	/**
	 * <p>Invoke a method whose parameter types match exactly the parameter
	 * types given.</p>
	 * <p/>
	 * <p>This uses reflection to invoke the method obtained from a call to
	 * {@link #getAccessibleMethod}.</p>
	 *
	 * @param object         invoke method on this object
	 * @param methodName     get method with this name
	 * @param args           use these arguments - treat null as empty array
	 * @param parameterTypes match these parameters - treat null as empty array
	 * @throws NoSuchMethodException     if there is no such accessible method
	 * @throws InvocationTargetException wraps an exception thrown by the
	 *                                   method invoked
	 * @throws IllegalAccessException    if the requested method is not accessible
	 *                                   via reflection
	 */
	public static Object invokeExactMethod(Object object, String methodName, Object[] args, Class[] parameterTypes) throws NoSuchMethodException, IllegalAccessException, InvocationTargetException
	{
		if (args == null)
		{
			args = emptyObjectArray;
		}

		if (parameterTypes == null)
		{
			parameterTypes = emptyClassArray;
		}

		Method method = getAccessibleMethod(object.getClass(), methodName, parameterTypes);

		if (method == null)
		{
			throw new NoSuchMethodException("No such accessible method: " + methodName + "() on object: " + object.getClass().getName());
		}

		return method.invoke(object, args);
	}


	/**
	 * <p>Return an accessible method (that is, one that can be invoked via
	 * reflection) with given name and a single parameter.  If no such method
	 * can be found, return <code>null</code>.
	 * Basically, a convenience wrapper that constructs a <code>Class</code>
	 * array for you.</p>
	 *
	 * @param clazz         get method from this class
	 * @param methodName    get method with this name
	 * @param parameterType taking this type of parameter
	 */
	public static Method getAccessibleMethod(Class clazz, String methodName, Class parameterType)
	{
		Class[] parameterTypes = {parameterType};
		return getAccessibleMethod(clazz, methodName, parameterTypes);
	}


	/**
	 * <p>Return an accessible method (that is, one that can be invoked via
	 * reflection) with given name and parameters.  If no such method
	 * can be found, return <code>null</code>.
	 * This is just a convenient wrapper for
	 * {@link #getAccessibleMethod(Method method)}.</p>
	 *
	 * @param clazz          get method from this class
	 * @param methodName     get method with this name
	 * @param parameterTypes with these parameters types
	 */
	public static Method getAccessibleMethod(Class clazz, String methodName, Class[] parameterTypes)
	{
		try
		{
			MethodDescriptor md = new MethodDescriptor(clazz, methodName, parameterTypes, true);
			// Check the cache first
			Method method = (Method) cache.get(md);
			if (method != null) return method;

			method = getAccessibleMethod(clazz.getMethod(methodName, parameterTypes));
			cache.put(md, method);
			return method;
		}
		catch (NoSuchMethodException e)
		{
			return (null);
		}

	}


	/**
	 * <p>Return an accessible method (that is, one that can be invoked via
	 * reflection) that implements the specified Method.  If no such method
	 * can be found, return <code>null</code>.</p>
	 *
	 * @param method The method that we wish to call
	 */
	public static Method getAccessibleMethod(Method method)
	{

		// Make sure we have a method to check
		if (method == null) return (null);

		// If the requested method is not public we cannot call it
		if (!Modifier.isPublic(method.getModifiers())) return (null);

		// If the declaring class is public, we are done
		Class clazz = method.getDeclaringClass();
		if (Modifier.isPublic(clazz.getModifiers())) return (method);

		// Check the implemented interfaces and subinterfaces
		method = getAccessibleMethodFromInterfaceNest(clazz, method.getName(), method.getParameterTypes());

		return (method);

	}

	/**
	 * <p>Return an accessible method (that is, one that can be invoked via
	 * reflection) that implements the specified method, by scanning through
	 * all implemented interfaces and subinterfaces.  If no such method
	 * can be found, return <code>null</code>.</p>
	 * <p/>
	 * <p> There isn't any good reason why this method must be private.
	 * It is because there doesn't seem any reason why other classes should
	 * call this rather than the higher level methods.</p>
	 *
	 * @param clazz          Parent class for the interfaces to be checked
	 * @param methodName     Method name of the method we wish to call
	 * @param parameterTypes The parameter type signatures
	 */
	private static Method getAccessibleMethodFromInterfaceNest(Class clazz, String methodName, Class parameterTypes[])
	{

		Method method = null;

		// Search up the superclass chain
		for (; clazz != null; clazz = clazz.getSuperclass())
		{

			// Check the implemented interfaces of the parent class
			Class interfaces[] = clazz.getInterfaces();
			for (int i = 0; i < interfaces.length; i++)
			{

				// Is this interface public?
				if (!Modifier.isPublic(interfaces[i].getModifiers()))
				{
					continue;
				}

				// Does the method exist on this interface?
				try
				{
					method = interfaces[i].getDeclaredMethod(methodName,
							parameterTypes);
				}
				catch (NoSuchMethodException e)
				{
					;
				}
				if (method != null)
				{
					break;
				}

				// Recursively check our parent interfaces
				method =
						getAccessibleMethodFromInterfaceNest(interfaces[i],
								methodName,
								parameterTypes);
				if (method != null)
				{
					break;
				}

			}

		}

		// If we found a method return it
		if (method != null)
		{
			return (method);
		}

		// We did not find anything
		return (null);

	}

	/**
	 * <p>Find an accessible method that matches the given name and has compatible parameters.
	 * Compatible parameters mean that every method parameter is assignable from
	 * the given parameters.
	 * In other words, it finds a method with the given name
	 * that will take the parameters given.<p>
	 * <p/>
	 * <p>This method is slightly undeterminstic since it loops
	 * through methods names and return the first matching method.</p>
	 * <p/>
	 * <p>This method is used by
	 * {@link
	 * #invokeMethod(Object object,String methodName,Object [] args,Class[] parameterTypes)}.
	 * <p/>
	 * <p>This method can match primitive parameter by passing in wrapper classes.
	 * For example, a <code>Boolean</code> will match a primitive <code>boolean</code>
	 * parameter.
	 *
	 * @param clazz          find method in this class
	 * @param methodName     find method with this name
	 * @param parameterTypes find method with compatible parameters
	 */
	public static Method getMatchingAccessibleMethod(Class clazz, String methodName, Class[] parameterTypes)
	{
		// trace logging
		if (log.isTraceEnabled())
		{
			log.trace("Matching name=" + methodName + " on " + clazz);
		}
		MethodDescriptor md = new MethodDescriptor(clazz, methodName, parameterTypes, false);

		// see if we can find the method directly
		// most of the time this works and it's much faster
		try
		{
			// Check the cache first
			Method method = (Method) cache.get(md);
			if (method != null)
			{
				return method;
			}

			method = clazz.getMethod(methodName, parameterTypes);
			if (log.isTraceEnabled())
			{
				log.trace("Found straight match: " + method);
				log.trace("isPublic:" + Modifier.isPublic(method.getModifiers()));
			}

			try
			{
				method.setAccessible(true);
			}
			catch (SecurityException se)
			{
				// log but continue just in case the method.invoke works anyway
				if (!loggedAccessibleWarning)
				{
					boolean vunerableJVM = false;
					try
					{
						String specVersion = System.getProperty("java.specification.version");
						if (specVersion.charAt(0) == '1' &&
								(specVersion.charAt(0) == '0' ||
								specVersion.charAt(0) == '1' ||
								specVersion.charAt(0) == '2' ||
								specVersion.charAt(0) == '3'))
						{

							vunerableJVM = true;
						}
					}
					catch (SecurityException e)
					{
						// don't know - so display warning
						vunerableJVM = true;
					}
					if (vunerableJVM)
					{
						log.warn("Current Security Manager restricts use of workarounds for reflection bugs "
								+ " in pre-1.4 JVMs.");
					}
					loggedAccessibleWarning = true;
				}
				log.debug("Cannot setAccessible on method. Therefore cannot use jvm access bug workaround.",
						se);
			}
			cache.put(md, method);
			return method;

		}
		catch (NoSuchMethodException e)
		{ /* SWALLOW */
		}

		// search through all methods
		int paramSize = parameterTypes.length;
		Method[] methods = clazz.getMethods();
		for (int i = 0, size = methods.length; i < size; i++)
		{
			if (methods[i].getName().equals(methodName))
			{
				// log some trace information
				if (log.isTraceEnabled())
				{
					log.trace("Found matching name:");
					log.trace(methods[i]);
				}

				// compare parameters
				Class[] methodsParams = methods[i].getParameterTypes();
				int methodParamSize = methodsParams.length;
				if (methodParamSize == paramSize)
				{
					boolean match = true;
					for (int n = 0; n < methodParamSize; n++)
					{
						if (log.isTraceEnabled())
						{
							log.trace("Param=" + parameterTypes[n].getName());
							log.trace("Method=" + methodsParams[n].getName());
						}
						if (!isAssignmentCompatible(methodsParams[n], parameterTypes[n]))
						{
							if (log.isTraceEnabled())
							{
								log.trace(methodsParams[n] + " is not assignable from "
										+ parameterTypes[n]);
							}
							match = false;
							break;
						}
					}

					if (match)
					{
						// get accessible version of method
						Method method = getAccessibleMethod(methods[i]);
						if (method != null)
						{
							if (log.isTraceEnabled())
							{
								log.trace(method + " accessible version of "
										+ methods[i]);
							}
							try
							{
								//
								// XXX Default access superclass workaround
								// (See above for more details.)
								//
								method.setAccessible(true);

							}
							catch (SecurityException se)
							{
								// log but continue just in case the method.invoke works anyway
								if (!loggedAccessibleWarning)
								{
									log.warn("Cannot use JVM pre-1.4 access bug workaround due to restrictive security manager.");
									loggedAccessibleWarning = true;
								}
								log.debug("Cannot setAccessible on method. Therefore cannot use jvm access bug workaround.",
										se);
							}
							cache.put(md, method);
							return method;
						}

						log.trace("Couldn't find accessible method.");
					}
				}
			}
		}

		// didn't find a match
		log.trace("No match found.");
		return null;
	}

	/**
	 * <p>Determine whether a type can be used as a parameter in a method invocation.
	 * This method handles primitive conversions correctly.</p>
	 * <p/>
	 * <p>In order words, it will match a <code>Boolean</code> to a <code>boolean</code>,
	 * a <code>Long</code> to a <code>long</code>,
	 * a <code>Float</code> to a <code>float</code>,
	 * a <code>Integer</code> to a <code>int</code>,
	 * and a <code>Double</code> to a <code>double</code>.
	 * Now logic widening matches are allowed.
	 * For example, a <code>Long</code> will not match a <code>int</code>.
	 *
	 * @param parameterType    the type of parameter accepted by the method
	 * @param parameterization the type of parameter being tested
	 * @return true if the assignement is compatible.
	 */
	public static final boolean isAssignmentCompatible(Class parameterType, Class parameterization)
	{
		// try plain assignment
		if (parameterType.isAssignableFrom(parameterization))
		{
			return true;
		}

		if (parameterType.isPrimitive())
		{
			// this method does *not* do widening - you must specify exactly is this the right behaviour?
			Class parameterWrapperClazz = getPrimitiveWrapper(parameterType);
			if (parameterWrapperClazz != null)
			{
				return parameterWrapperClazz.equals(parameterization);
			}
		}

		return false;
	}

	/**
	 * Gets the wrapper object class for the given primitive type class.
	 * For example, passing <code>boolean.class</code> returns <code>Boolean.class</code>
	 *
	 * @param primitiveType the primitive type class for which a match is to be found
	 * @return the wrapper type associated with the given primitive
	 *         or null if no match is found
	 */
	public static Class getPrimitiveWrapper(Class primitiveType)
	{
		// does anyone know a better strategy than comparing names?
		if (boolean.class.equals(primitiveType))
		{
			return Boolean.class;
		}
		else if (float.class.equals(primitiveType))
		{
			return Float.class;
		}
		else if (long.class.equals(primitiveType))
		{
			return Long.class;
		}
		else if (int.class.equals(primitiveType))
		{
			return Integer.class;
		}
		else if (short.class.equals(primitiveType))
		{
			return Short.class;
		}
		else if (byte.class.equals(primitiveType))
		{
			return Byte.class;
		}
		else if (double.class.equals(primitiveType))
		{
			return Double.class;
		}
		else if (char.class.equals(primitiveType))
		{
			return Character.class;
		}
		else
		{
			return null;
		}
	}

	/**
	 * Gets the class for the primitive type corresponding to the primitive wrapper class given.
	 * For example, an instance of <code>Boolean.class</code> returns a <code>boolean.class</code>.
	 *
	 * @param wrapperType the
	 * @return the primitive type class corresponding to the given wrapper class,
	 *         null if no match is found
	 */
	public static Class getPrimitiveType(Class wrapperType)
	{
		// does anyone know a better strategy than comparing names?
		if (Boolean.class.equals(wrapperType))
		{
			return boolean.class;
		}
		else if (Float.class.equals(wrapperType))
		{
			return float.class;
		}
		else if (Long.class.equals(wrapperType))
		{
			return long.class;
		}
		else if (Integer.class.equals(wrapperType))
		{
			return int.class;
		}
		else if (Short.class.equals(wrapperType))
		{
			return short.class;
		}
		else if (Byte.class.equals(wrapperType))
		{
			return byte.class;
		}
		else if (Double.class.equals(wrapperType))
		{
			return double.class;
		}
		else if (Character.class.equals(wrapperType))
		{
			return char.class;
		}
		else
		{
			if (log.isDebugEnabled())
			{
				log.debug("Not a known primitive wrapper class: " + wrapperType);
			}

			return null;
		}
	}

	/**
	 * Find a non primitive representation for given primitive class.
	 *
	 * @param clazz the class to find a representation for, not null
	 * @return the original class if it not a primitive. Otherwise the wrapper class. Not null
	 */
	public static Class toNonPrimitiveClass(Class clazz)
	{
		if (clazz.isPrimitive())
		{
			Class primitiveClazz = MethodUtility.getPrimitiveWrapper(clazz);
			// the above method returns
			if (primitiveClazz != null) return primitiveClazz;
				else return clazz;
		}
		
		else return clazz;
	}


	/**
	 * Represents the key to looking up a Method by reflection.
	 */
	private static class MethodDescriptor
	{
		private Class cls;
		private String methodName;
		private Class[] paramTypes;
		private boolean exact;
		private int hashCode;

		/**
		 * The sole constructor.
		 *
		 * @param cls        the class to reflect, must not be null
		 * @param methodName the method name to obtain
		 * @param paramTypes the array of classes representing the paramater types
		 * @param exact      whether the match has to be exact.
		 */
		public MethodDescriptor(Class cls, String methodName, Class[] paramTypes, boolean exact)
		{
			if (cls == null) throw new IllegalArgumentException("Class cannot be null");

			if (methodName == null) throw new IllegalArgumentException("Method Name cannot be null");

			if (paramTypes == null) paramTypes = emptyClassArray;

			this.cls = cls;
			this.methodName = methodName;
			this.paramTypes = paramTypes;
			this.exact = exact;

			this.hashCode = methodName.length();
		}

		/**
		 * Checks for equality.
		 *
		 * @param obj object to be tested for equality
		 * @return true, if the object describes the same Method.
		 */
		public boolean equals(Object obj)
		{
			if (!(obj instanceof MethodDescriptor)) return false;

			MethodDescriptor md = (MethodDescriptor) obj;

			return (exact == md.exact && cls.equals(md.cls) && java.util.Arrays.equals(paramTypes, md.paramTypes));
		}

		/**
		 * Returns the string length of method name. I.e. if the
		 * hashcodes are different, the objects are different. If the
		 * hashcodes are the same, need to use the equals method to
		 * determine equality.
		 *
		 * @return the string length of method name.
		 */
		public int hashCode()
		{
			return hashCode;
		}

		public String getMethodName()
		{
			return this.methodName;
		}
	}
}
