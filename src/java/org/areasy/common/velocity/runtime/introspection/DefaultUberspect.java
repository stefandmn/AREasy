package org.areasy.common.velocity.runtime.introspection;

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

import org.areasy.common.logger.Logger;
import org.areasy.common.logger.LoggerFactory;
import org.areasy.common.data.type.iterator.ArrayIterator;
import org.areasy.common.data.type.iterator.EnumerationIterator;
import org.areasy.common.velocity.runtime.parser.node.AbstractExecutor;
import org.areasy.common.velocity.runtime.parser.node.BooleanPropertyExecutor;
import org.areasy.common.velocity.runtime.parser.node.GetExecutor;
import org.areasy.common.velocity.runtime.parser.node.PropertyExecutor;

import java.lang.reflect.Method;
import java.util.*;

/**
 * Implementation of Uberspect to provide the default introspective
 * functionality of Velocity
 *
 * @version $Id: DefaultUberspect.java,v 1.1 2008/05/25 22:33:14 swd\stefan.damian Exp $
 */
public class DefaultUberspect implements Uberspect
{
	/** the logger */
	private static Logger logger = LoggerFactory.getLog(DefaultUberspect.class.getName());

	/**
	 * the default Velocity introspector
	 */
	private static Introspector introspector;

	/**
	 * init - does nothing - we need to have setRuntimeLogger
	 * called before getting our introspector, as the default
	 * vel introspector depends upon it.
	 */
	public void init() throws Exception
	{
		introspector = new Introspector();
	}

	/**
	 * To support iteratives - #foreach()
	 */
	public Iterator getIterator(Object obj, Information i) throws Exception
	{
		if (obj.getClass().isArray()) return new ArrayIterator(obj);
		else if (obj instanceof Collection) return ((Collection) obj).iterator();
		else if (obj instanceof Map) return ((Map) obj).values().iterator();
		else if (obj instanceof Iterator)
		{
			logger.warn("The iterative "
					+ " is an Iterator in the #foreach() loop at ["
					+ i.getLine() + "," + i.getColumn() + "]"
					+ " in template " + i.getTemplateName()
					+ ". Because it's not resetable,"
					+ " if used in more than once, this may lead to"
					+ " unexpected results.");

			return ((Iterator) obj);
		}
		else if (obj instanceof Enumeration)
		{
			logger.warn("The iterative "
					+ " is an Enumeration in the #foreach() loop at ["
					+ i.getLine() + "," + i.getColumn() + "]"
					+ " in template " + i.getTemplateName()
					+ ". Because it's not resetable,"
					+ " if used in more than once, this may lead to"
					+ " unexpected results.");

			return new EnumerationIterator((Enumeration) obj);
		}

		/*  we have no clue what this is  */
		logger.warn("Could not determine type of iterator in "
				+ "#foreach loop "
				+ " at [" + i.getLine() + "," + i.getColumn() + "]"
				+ " in template " + i.getTemplateName());

		return null;
	}

	/**
	 * Method
	 */
	public VelocityMethod getMethod(Object obj, String methodName, Object[] args, Information i) throws Exception
	{
		if (obj == null) return null;

		Method m = introspector.getMethod(obj.getClass(), methodName, args);

		return (m != null) ? new DefaultVelocityMethod(m) : null;
	}

	/**
	 * Property  getter
	 */
	public VelocityPropertyGet getPropertyGet(Object obj, String identifier, Information i) throws Exception
	{
		AbstractExecutor executor;

		Class claz = obj.getClass();

		executor = new PropertyExecutor(introspector, claz, identifier);

		if (executor.isAlive() == false) executor = new GetExecutor(introspector, claz, identifier);

		if (executor.isAlive() == false) executor = new BooleanPropertyExecutor(introspector, claz, identifier);

		return (executor != null) ? new DefaultVelocityGetter(executor) : null;
	}

	/**
	 * Property setter
	 */
	public VelocityPropertySet getPropertySet(Object obj, String identifier, Object arg, Information i) throws Exception
	{
		Class claz = obj.getClass();

		//VelocityPropertySet vs = null;
		VelocityMethod vm = null;
		try
		{
			Object[] params = {arg};

			try
			{
				vm = getMethod(obj, "set" + identifier, params, i);

				if (vm == null) throw new NoSuchMethodException();
			}
			catch (NoSuchMethodException nsme2)
			{
				StringBuffer sb = new StringBuffer("set");
				sb.append(identifier);

				if (Character.isLowerCase(sb.charAt(3))) sb.setCharAt(3, Character.toUpperCase(sb.charAt(3)));
					else sb.setCharAt(3, Character.toLowerCase(sb.charAt(3)));

				vm = getMethod(obj, sb.toString(), params, i);

				if (vm == null) throw new NoSuchMethodException();
			}
		}
		catch (NoSuchMethodException nsme)
		{
			if (Map.class.isAssignableFrom(claz))
			{
				Object[] params = {new Object(), new Object()};

				vm = getMethod(obj, "put", params, i);

				if (vm != null) return new DefaultVelocitySetter(vm, identifier);
			}
		}

		return (vm != null) ? new DefaultVelocitySetter(vm) : null;
	}

	/**
	 * Implementation of VelMethod
	 */
	public class DefaultVelocityMethod implements VelocityMethod
	{
		Method method = null;

		public DefaultVelocityMethod(Method m)
		{
			method = m;
		}

		public Object invoke(Object o, Object[] params)
				throws Exception
		{
			return method.invoke(o, params);
		}

		public boolean isCacheable()
		{
			return true;
		}

		public String getMethodName()
		{
			return method.getName();
		}

		public Class getReturnType()
		{
			return method.getReturnType();
		}
	}

	public class DefaultVelocityGetter implements VelocityPropertyGet
	{
		AbstractExecutor ae = null;

		public DefaultVelocityGetter(AbstractExecutor exec)
		{
			ae = exec;
		}

		public Object invoke(Object o) throws Exception
		{
			return ae.execute(o);
		}

		public boolean isCacheable()
		{
			return true;
		}

		public String getMethodName()
		{
			return ae.getMethod().getName();
		}

	}

	public class DefaultVelocitySetter implements VelocityPropertySet
	{
		VelocityMethod vm = null;
		String putKey = null;

		public DefaultVelocitySetter(VelocityMethod velmethod)
		{
			this.vm = velmethod;
		}

		public DefaultVelocitySetter(VelocityMethod velmethod, String key)
		{
			this.vm = velmethod;
			putKey = key;
		}

		public Object invoke(Object o, Object value) throws Exception
		{
			ArrayList al = new ArrayList();

			if (putKey != null)
			{
				al.add(putKey);
				al.add(value);
			}
			else al.add(value);

			return vm.invoke(o, al.toArray());
		}

		public boolean isCacheable()
		{
			return true;
		}

		public String getMethodName()
		{
			return vm.getMethodName();
		}

	}
}
