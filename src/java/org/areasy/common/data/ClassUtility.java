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

import org.areasy.common.errors.NestableException;
import org.areasy.common.logger.LoggerFactory;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.*;

/**
 * <p>Operates on classes without using reflection.</p>
 * <p/>
 * <p>This class handles invalid <code>null</code> inputs as best it can.
 * Each method documents its behaviour in more detail.</p>
 *
 * @version $Id: ClassUtility.java,v 1.3 2008/05/29 13:55:39 swd\stefan.damian Exp $
 */
public class ClassUtility
{
	/**
	 * <p>The package separator character: <code>'&#x2e;' == {\@value}</code>.</p>
	 */
	public static final char PACKAGE_SEPARATOR_CHAR = '.';

	/**
	 * <p>The package separator String: <code>"&#x2e;"</code>.</p>
	 */
	public static final String PACKAGE_SEPARATOR = String.valueOf(PACKAGE_SEPARATOR_CHAR);

	/**
	 * <p>The inner class separator character: <code>'$' == {\@value}</code>.</p>
	 */
	public static final char INNER_CLASS_SEPARATOR_CHAR = '$';

	/**
	 * <p>The inner class separator String: <code>"$"</code>.</p>
	 */
	public static final String INNER_CLASS_SEPARATOR = String.valueOf(INNER_CLASS_SEPARATOR_CHAR);

	/**
	 * Maps primitive <code>Class</code>es to their corresponding wrapper <code>Class</code>.
	 */
	private static Map primitiveWrapperMap = new HashMap();

	static
	{
		primitiveWrapperMap.put(Boolean.TYPE, Boolean.class);
		primitiveWrapperMap.put(Byte.TYPE, Byte.class);
		primitiveWrapperMap.put(Character.TYPE, Character.class);
		primitiveWrapperMap.put(Short.TYPE, Short.class);
		primitiveWrapperMap.put(Integer.TYPE, Integer.class);
		primitiveWrapperMap.put(Long.TYPE, Long.class);
		primitiveWrapperMap.put(Double.TYPE, Double.class);
		primitiveWrapperMap.put(Float.TYPE, Float.class);
	}

	/**
	 * <p>ClassUtility instances should NOT be constructed in standard programming.
	 * Instead, the class should be used as
	 * <code>ClassUtility.getShortClassName(cls)</code>.</p>
	 * <p/>
	 * <p>This constructor is public to permit tools that require a JavaBean
	 * instance to operate.</p>
	 */
	public ClassUtility()
	{
	}

	// Short class name
	/**
	 * <p>Gets the class name minus the package name for an <code>Object</code>.</p>
	 *
	 * @param object      the class to get the short name for, may be null
	 * @param valueIfNull the value to return if null
	 * @return the class name of the object without the package name, or the null value
	 */
	public static String getShortClassName(Object object, String valueIfNull)
	{
		if (object == null) return valueIfNull;

		return getShortClassName(object.getClass().getName());
	}

	/**
	 * <p>Gets the class name minus the package name from a <code>Class</code>.</p>
	 *
	 * @param cls the class to get the short name for.
	 * @return the class name without the package name or an empty string
	 */
	public static String getShortClassName(Class cls)
	{
		if (cls == null) return StringUtility.EMPTY;

		return getShortClassName(cls.getName());
	}

	/**
	 * <p>Gets the class name minus the package name from a String.</p>
	 * <p/>
	 * <p>The string passed in is assumed to be a class name - it is not checked.</p>
	 *
	 * @param className the className to get the short name for
	 * @return the class name of the class without the package name or an empty string
	 */
	public static String getShortClassName(String className)
	{
		if (className == null) return StringUtility.EMPTY;

		if (className.length() == 0) return StringUtility.EMPTY;

		char[] chars = className.toCharArray();
		int lastDot = 0;

		for (int i = 0; i < chars.length; i++)
		{
			if (chars[i] == PACKAGE_SEPARATOR_CHAR) lastDot = i + 1;
				else if (chars[i] == INNER_CLASS_SEPARATOR_CHAR) chars[i] = PACKAGE_SEPARATOR_CHAR;
		}

		return new String(chars, lastDot, chars.length - lastDot);
	}

	/**
	 * <p>Gets the package name of an <code>Object</code>.</p>
	 *
	 * @param object      the class to get the package name for, may be null
	 * @param valueIfNull the value to return if null
	 * @return the package name of the object, or the null value
	 */
	public static String getPackageName(Object object, String valueIfNull)
	{
		if (object == null) return valueIfNull;

		return getPackageName(object.getClass().getName());
	}

	/**
	 * <p>Gets the package name from a <code>String</code>.</p>
	 * <p/>
	 * <p>The string passed in is assumed to be a class name - it is not checked.</p>
	 * <p>If the class is unpackaged, return an empty string.</p>
	 *
	 * @param className the className to get the package name for, may be <code>null</code>
	 * @return the package name or an empty string
	 */
	public static String getPackageName(String className)
	{
		if (className == null) return StringUtility.EMPTY;

		int i = className.lastIndexOf(PACKAGE_SEPARATOR_CHAR);
		if (i == -1) return StringUtility.EMPTY;

		return className.substring(0, i);
	}

	/**
	 * <p>Gets a <code>List</code> of superclasses for the given class.</p>
	 *
	 * @param cls the class to look up, may be <code>null</code>
	 * @return the <code>List</code> of superclasses in order going up from this one
	 *         <code>null</code> if null input
	 */
	public static List getAllSuperclasses(Class cls)
	{
		if (cls == null) return null;

		List classes = new ArrayList();
		Class superclass = cls.getSuperclass();

		while (superclass != null)
		{
			classes.add(superclass);
			superclass = superclass.getSuperclass();
		}

		return classes;
	}

	/**
	 * <p>Gets a <code>List</code> of all interfaces implemented by the given
	 * class and its superclasses.</p>
	 * <p/>
	 * <p>The order is determined by looking through each interface in turn as
	 * declared in the source file and following its hierarchy up. Then each
	 * superclass is considered in the same way. Later duplicates are ignored,
	 * so the order is maintained.</p>
	 *
	 * @param cls the class to look up, may be <code>null</code>
	 * @return the <code>List</code> of interfaces in order,
	 *         <code>null</code> if null input
	 */
	public static List getAllInterfaces(Class cls)
	{
		if (cls == null) return null;

		List list = new ArrayList();
		while (cls != null)
		{
			Class[] interfaces = cls.getInterfaces();
			for (int i = 0; i < interfaces.length; i++)
			{
				if (list.contains(interfaces[i]) == false)
				{
					list.add(interfaces[i]);
				}
				List superInterfaces = getAllInterfaces(interfaces[i]);
				for (Iterator it = superInterfaces.iterator(); it.hasNext();)
				{
					Class intface = (Class) it.next();
					if (list.contains(intface) == false)
					{
						list.add(intface);
					}
				}
			}

			cls = cls.getSuperclass();
		}

		return list;
	}

	/**
	 * <p>Given a <code>List</code> of class names, this method converts them into classes.</p>
	 * <p/>
	 * <p>A new <code>List</code> is returned. If the class name cannot be found, <code>null</code>
	 * is stored in the <code>List</code>. If the class name in the <code>List</code> is
	 * <code>null</code>, <code>null</code> is stored in the output <code>List</code>.</p>
	 *
	 * @param classNames the classNames to change
	 * @return a <code>List</code> of Class objects corresponding to the class names,
	 *         <code>null</code> if null input
	 * @throws ClassCastException if classNames contains a non String entry
	 */
	public static List convertClassNamesToClasses(List classNames)
	{
		if (classNames == null) return null;

		List classes = new ArrayList(classNames.size());
		for (Iterator it = classNames.iterator(); it.hasNext();)
		{
			String className = (String) it.next();
			try
			{
				classes.add(Class.forName(className));
			}
			catch (Exception ex)
			{
				classes.add(null);
			}
		}
		return classes;
	}

	/**
	 * <p>Given a <code>List</code> of <code>Class</code> objects, this method converts
	 * them into class names.</p>
	 * <p/>
	 * <p>A new <code>List</code> is returned. <code>null</code> objects will be copied into
	 * the returned list as <code>null</code>.</p>
	 *
	 * @param classes the classes to change
	 * @return a <code>List</code> of class names corresponding to the Class objects,
	 *         <code>null</code> if null input
	 * @throws ClassCastException if <code>classes</code> contains a non-<code>Class</code> entry
	 */
	public static List convertClassesToClassNames(List classes)
	{
		if (classes == null) return null;

		List classNames = new ArrayList(classes.size());
		for (Iterator it = classes.iterator(); it.hasNext();)
		{
			Class cls = (Class) it.next();
			if (cls == null) classNames.add(null);
				else classNames.add(cls.getName());
		}

		return classNames;
	}

	/**
	 * <p>Checks if an array of Classes can be assigned to another array of Classes.</p>
	 * <p/>
	 * <p>This method calls {@link #isAssignable(Class, Class) isAssignable} for each
	 * Class pair in the input arrays. It can be used to check if a set of arguments
	 * (the first parameter) are suitably compatible with a set of method parameter types
	 * (the second parameter).</p>
	 * <p/>
	 * <p>Unlike the {@link Class#isAssignableFrom(java.lang.Class)} method, this
	 * method takes into account widenings of primitive classes and
	 * <code>null</code>s.</p>
	 * <p/>
	 * <p>Primitive widenings allow an int to be assigned to a <code>long</code>,
	 * <code>float</code> or <code>double</code>. This method returns the correct
	 * result for these cases.</p>
	 * <p/>
	 * <p><code>Null</code> may be assigned to any reference type. This method will
	 * return <code>true</code> if <code>null</code> is passed in and the toClass is
	 * non-primitive.</p>
	 * <p/>
	 * <p>Specifically, this method tests whether the type represented by the
	 * specified <code>Class</code> parameter can be converted to the type
	 * represented by this <code>Class</code> object via an identity conversion
	 * widening primitive or widening reference conversion. See
	 * <em><a href="http://java.sun.com/docs/books/jls/">The Java Language Specification</a></em>,
	 * sections 5.1.1, 5.1.2 and 5.1.4 for details.</p>
	 *
	 * @param classArray   the array of Classes to check, may be <code>null</code>
	 * @param toClassArray the array of Classes to try to assign into, may be <code>null</code>
	 * @return <code>true</code> if assignment possible
	 */
	public static boolean isAssignable(Class[] classArray, Class[] toClassArray)
	{
		if (ArrayUtility.isSameLength(classArray, toClassArray) == false) return false;

		if (classArray == null) classArray = ArrayUtility.EMPTY_CLASS_ARRAY;

		if (toClassArray == null) toClassArray = ArrayUtility.EMPTY_CLASS_ARRAY;

		for (int i = 0; i < classArray.length; i++)
		{
			if (isAssignable(classArray[i], toClassArray[i]) == false) return false;
		}

		return true;
	}

	/**
	 * <p>Checks if one <code>Class</code> can be assigned to a variable of
	 * another <code>Class</code>.</p>
	 * <p/>
	 * <p>Unlike the {@link Class#isAssignableFrom(java.lang.Class)} method,
	 * this method takes into account widenings of primitive classes and
	 * <code>null</code>s.</p>
	 * <p/>
	 * <p>Primitive widenings allow an int to be assigned to a long, float or
	 * double. This method returns the correct result for these cases.</p>
	 * <p/>
	 * <p><code>Null</code> may be assigned to any reference type. This method
	 * will return <code>true</code> if <code>null</code> is passed in and the
	 * toClass is non-primitive.</p>
	 * <p/>
	 * <p>Specifically, this method tests whether the type represented by the
	 * specified <code>Class</code> parameter can be converted to the type
	 * represented by this <code>Class</code> object via an identity conversion
	 * widening primitive or widening reference conversion. See
	 * <em><a href="http://java.sun.com/docs/books/jls/">The Java Language Specification</a></em>,
	 * sections 5.1.1, 5.1.2 and 5.1.4 for details.</p>
	 *
	 * @param cls     the Class to check, may be null
	 * @param toClass the Class to try to assign into, returns false if null
	 * @return <code>true</code> if assignment possible
	 */
	public static boolean isAssignable(Class cls, Class toClass)
	{
		if (toClass == null) return false;

		// have to check for null, as isAssignableFrom doesn't
		if (cls == null) return !(toClass.isPrimitive());

		if (cls.equals(toClass)) return true;

		if (cls.isPrimitive())
		{
			if (toClass.isPrimitive() == false) return false;

			if (Integer.TYPE.equals(cls)) return Long.TYPE.equals(toClass) || Float.TYPE.equals(toClass) || Double.TYPE.equals(toClass);

			if (Long.TYPE.equals(cls)) return Float.TYPE.equals(toClass) || Double.TYPE.equals(toClass);

			if (Boolean.TYPE.equals(cls)) return false;

			if (Double.TYPE.equals(cls)) return false;

			if (Float.TYPE.equals(cls)) return Double.TYPE.equals(toClass);

			if (Character.TYPE.equals(cls)) return Integer.TYPE.equals(toClass) || Long.TYPE.equals(toClass) || Float.TYPE.equals(toClass) || Double.TYPE.equals(toClass);

			if (Short.TYPE.equals(cls)) return Integer.TYPE.equals(toClass) || Long.TYPE.equals(toClass) || Float.TYPE.equals(toClass) || Double.TYPE.equals(toClass);

			if (Byte.TYPE.equals(cls)) return Short.TYPE.equals(toClass) || Integer.TYPE.equals(toClass) || Long.TYPE.equals(toClass) || Float.TYPE.equals(toClass) || Double.TYPE.equals(toClass);

			// should never get here
			return false;
		}

		return toClass.isAssignableFrom(cls);
	}

	/**
	 * <p>Converts the specified primitive Class object to its corresponding
	 * wrapper Class object.</p>
	 *
	 * @param cls the class to convert, may be null
	 * @return the wrapper class for <code>cls</code> or <code>cls</code> if
	 *         <code>cls</code> is not a primitive. <code>null</code> if null input.
	 */
	public static Class primitiveToWrapper(Class cls)
	{
		Class convertedClass = cls;
		if (cls != null && cls.isPrimitive()) convertedClass = (Class) primitiveWrapperMap.get(cls);

		return convertedClass;
	}

	/**
	 * <p>Converts the specified array of primitive Class objects to an array of
	 * its corresponding wrapper Class objects.</p>
	 *
	 * @param classes the class array to convert, may be null or empty
	 * @return an array which contains for each given class, the wrapper class or
	 *         the original class if class is not a primitive. <code>null</code> if null input.
	 *         Empty array if an empty array passed in.
	 */
	public static Class[] primitivesToWrappers(Class[] classes)
	{
		if (classes == null) return null;

		if (classes.length == 0) return classes;

		Class[] convertedClasses = new Class[classes.length];
		for (int i = 0; i < classes.length; i++)
		{
			convertedClasses[i] = primitiveToWrapper(classes[i]);
		}

		return convertedClasses;
	}

    /**
     * Creates objects given the class structure.
     * Throws exceptions if the class is not found in the default class path,
     * or the class is not an instance of Object.
     *
     * @param classObj the class of object
     * @return the newly created object
     */
    public static Object getInstance(Class classObj) throws NestableException
    {
        Object object = null;

        try
        {
            object = classObj.newInstance();
        }
        catch(Exception e)
        {
            throw new NestableException(e);
        }

        return object;
    }

    /**
     * Creates objects instance given the class signature.
     * Throws exceptions if the class is not found in the default class path,
     * or the class is not an instance of Object.
     *
     * @param classSig the signature of object
     * @return the newly created object
     */
    public static Object getInstance(String classSig) throws NestableException
    {
        Object object = null;
		Class classObj = null;

        try
        {
            classObj =  Class.forName(classSig);
        }
        catch(ClassNotFoundException e)
        {
            throw new NestableException("Could not preload " + classSig + " implementation class.", e);
        }

		if(classObj == null) throw new  NestableException("Null class structure from signature: " + classSig);

        try
        {
			object = classObj.newInstance();
        }
        catch(Exception e)
        {
            throw new NestableException(e);
        }

        return object;
    }

	/**
	 * Invokes the underlying method represented by this <code>Method</code> object, on the specified object with the specified parameters.
	 * Individual parameters are automatically unwrapped to match primitive formal parameters, and both primitive and reference
	 * parameters are subject to method invocation conversions as necessary.
	 * <p>
	 * This method will execute bean method format (set or get - depending <code>methodParameter</code> parameter: if is null,
	 * considered method will be "get" + the value of <code>abstractMethod</code> parameter. Otherwise considered method will be
	 * "get" + the value of <code>abstractMethod</code> parameter.
	 * <p>
	 * <b>Note:</b> <br/>
	 * In case of <code>methodParameter</code> is not null and <code>arg</code> is null the argument will be considered the first instance of
	 * <code>methodParameter</code> parameter value (specifing a library)
	 *
	 * @param baseClass the owner of method.
	 * @param methodObject the object the underlying method is invoked from
	 * @param abstractMethod bean method format (must includes capitalization)
	 * @param methodParameter must be null or a string specifing a classname to identify method signature. If method is not recognized with
	 * this value will be discovered all implemented interfaces and will retry.
	 * @param arg the argument used for the method call
	 * @return the result of dispatching the method represented by this object on <code>methodObject</code> with parameter <code>arg</code>
	 * @throws org.areasy.common.errors.NestableException if class is not found or if is tried to create a wrong instance or an exception
	 * thrown by an invoked method or constructor.
	 */
	public static Object invokeBeanMethod(Class baseClass, Object methodObject,  String abstractMethod, String methodParameter, Object arg) throws NestableException
	{
		//define method
		String methodName = null;

		if(!StringUtility.isEmpty(methodParameter)) methodName = "set" + abstractMethod;
			else methodName = "get" + abstractMethod;

		try
		{
			Class params[] = null;

			//define parameters for method signature.
			if(!StringUtility.isEmpty(methodParameter))
			{
				params = new Class[1];
				Class paramClass = Class.forName(methodParameter);
				params[0] = paramClass;
			}

			//get method.
			Method method = baseClass.getMethod(methodName, params);

			//define arguments
			Object objects[] = null;

			if(arg != null && !StringUtility.isEmpty(methodParameter))
			{
				objects = new Object[1];
				objects[0] = arg;
			}

			//if argument is null and methodParameter is not null will be send (like parameter a simple instance of  methodParameter class
			if(arg == null && !StringUtility.isEmpty(methodParameter))
			{
				Class objParamClass = Class.forName(methodParameter);

				objects = new Object[1];
				objects[0] = objParamClass.newInstance();
			}

			//invoke method.
			return method.invoke(methodObject, objects);
		}
		catch(ClassNotFoundException e)
		{
			throw new NestableException(e);
		}
		catch(InstantiationException e)
		{
			throw new NestableException(e);
		}
		catch(IllegalAccessException e)
		{
			throw new NestableException(e);
		}
		catch(InvocationTargetException e)
		{
			throw new NestableException(e);
		}
		catch(NoSuchMethodException e)
		{
			if(StringUtility.isEmpty(methodParameter)) throw new NestableException(e);
			else
			{
				try
				{
					//define the output.
					Object result = null;

					//get class from methodParameter (to discover all implemented interfaces)
					Class paramClass = Class.forName(methodParameter);
					Class[] interfaces =paramClass.getInterfaces();

					//set an execution flag.
					boolean executed = false;

					//set argument if is null and methodParameter is not null, because now will be send other parameterValue (implemented interface).
					if(arg == null && !StringUtility.isEmpty(methodParameter))
					{
						Class argParamClass = Class.forName(methodParameter);
						arg = argParamClass.newInstance();
					}

					//check each interface.
					if(interfaces != null && interfaces.length > 0)
					{
						int index = 0;
						while(!executed && index < interfaces.length)
						{
							try
							{
								result = invokeBeanMethod(baseClass, methodObject, abstractMethod, interfaces[index].getName(), arg);
								executed = true;
							}
							catch(NestableException pe)
							{
								executed = false;
							}

							//next interface.
							index = index + 1;
						}
					}

					//check method execution.
					if(!executed) throw new NestableException(e);
						else return result;
				}
				catch(ClassNotFoundException x)
				{
					throw new NestableException(x);
				}
				catch(InstantiationException x)
				{
					throw new NestableException(x);
				}
				catch(IllegalAccessException x)
				{
					throw new NestableException(x);
				}
			}
		}
	}


	/**
	 * <p>Is the specified class an inner class or static nested class.</p>
	 *
	 * @param cls the class to check, may be null
	 * @return <code>true</code> if the class is an inner or static nested class,
	 *         false if not or <code>null</code>
	 */
	public static boolean isInnerClass(Class cls)
	{
		if (cls == null) return false;

		return cls.getName().indexOf(INNER_CLASS_SEPARATOR_CHAR) >= 0;
	}

	/**
	 * Get package name.
	 * Not all class loaders 'keep' package information,
	 * in which case Class.getPackage() returns null.
	 * This means that calling Class.getPackage().getName()
	 * is unreliable at best.
	 */
	public static String getPackageName(Class clazz)
	{
		Package clazzPackage = clazz.getPackage();
		String packageName;

		if (clazzPackage != null)
		{
			packageName = clazzPackage.getName();
		}
		else
		{
			String clazzName = clazz.getName();
			packageName = clazzName.substring(0, clazzName.lastIndexOf('.'));
		}
		
		return packageName;
	}

	/**
	 * @return Method 'public static returnType methodName(paramTypes)',
	 *         if found to be <strong>directly</strong> implemented by clazz.
	 */
	public static Method findPublicStaticMethod(Class clazz, Class returnType, String methodName, Class[] paramTypes)
	{
		boolean problem = false;
		Method method = null;

		// verify '<methodName>(<paramTypes>)' is directly in class.
		try
		{
			method = clazz.getDeclaredMethod(methodName, paramTypes);
		}
		catch (NoSuchMethodException e)
		{
			problem = true;
			LoggerFactory.getLog(ClassUtility.class).debug("Class " + clazz.getName() + ": missing method '" + methodName + "(...)", e);
		}

		// verify 'public static <returnType>'
		if (!problem && !(Modifier.isPublic(method.getModifiers()) && Modifier.isStatic(method.getModifiers()) && method.getReturnType() == returnType))
		{
			if (!Modifier.isPublic(method.getModifiers()))
			{
				LoggerFactory.getLog(ClassUtility.class).debug(methodName + "() is not public");
			}

			if (!Modifier.isStatic(method.getModifiers()))
			{
				LoggerFactory.getLog(ClassUtility.class).debug(methodName + "() is not static");
			}

			if (method.getReturnType() != returnType)
			{
				LoggerFactory.getLog(ClassUtility.class).debug("Method returns: " + method.getReturnType().getName() + "@@" + method.getReturnType().getClassLoader());
				LoggerFactory.getLog(ClassUtility.class).debug("Should return:  " + returnType.getName() + "@@" + returnType.getClassLoader());
			}

			problem = true;
			method = null;
		}

		return method;
	}
}
