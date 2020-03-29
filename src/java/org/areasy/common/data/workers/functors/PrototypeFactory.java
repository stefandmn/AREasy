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

import org.areasy.common.data.type.Factory;

import java.io.*;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * Factory implementation that creates a new instance each time based on a prototype.
 *
 * @version $Id: PrototypeFactory.java,v 1.2 2008/05/14 09:32:31 swd\stefan.damian Exp $
 */
public class PrototypeFactory
{

	/**
	 * Factory method that performs validation.
	 * <p/>
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
	 */
	public static Factory getInstance(Object prototype)
	{
		if (prototype == null)
		{
			return ConstantFactory.NULL_INSTANCE;
		}
		try
		{
			Method method = prototype.getClass().getMethod("clone", null);
			return new PrototypeCloneFactory(prototype, method);

		}
		catch (NoSuchMethodException ex)
		{
			try
			{
				prototype.getClass().getConstructor(new Class[]{prototype.getClass()});
				return new InstantiateFactory(prototype.getClass(),
						new Class[]{prototype.getClass()},
						new Object[]{prototype});

			}
			catch (NoSuchMethodException ex2)
			{
				if (prototype instanceof Serializable)
				{
					return new PrototypeSerializationFactory((Serializable) prototype);
				}
			}
		}
		throw new IllegalArgumentException("The prototype must be cloneable via a public clone method");
	}

	/**
	 * Constructor that performs no validation.
	 * Use <code>getInstance</code> if you want that.
	 */
	private PrototypeFactory()
	{
		super();
	}

	/**
	 * PrototypeCloneFactory creates objects by copying a prototype using the clone method.
	 */
	static class PrototypeCloneFactory implements Factory, Serializable
	{
		/**
		 * The object to clone each time
		 */
		private final Object iPrototype;
		/**
		 * The method used to clone
		 */
		private transient Method iCloneMethod;

		/**
		 * Constructor to store prototype.
		 */
		private PrototypeCloneFactory(Object prototype, Method method)
		{
			super();
			iPrototype = prototype;
			iCloneMethod = method;
		}

		/**
		 * Find the Clone method for the class specified.
		 */
		private void findCloneMethod()
		{
			try
			{
				iCloneMethod = iPrototype.getClass().getMethod("clone", null);

			}
			catch (NoSuchMethodException ex)
			{
				throw new IllegalArgumentException("PrototypeCloneFactory: The clone method must exist and be public ");
			}
		}

		/**
		 * Creates an object by calling the clone method.
		 *
		 * @return the new object
		 */
		public Object create()
		{
			// needed for post-serialization
			if (iCloneMethod == null)
			{
				findCloneMethod();
			}

			try
			{
				return iCloneMethod.invoke(iPrototype, null);

			}
			catch (IllegalAccessException ex)
			{
				throw new FunctorException("PrototypeCloneFactory: Clone method must be public", ex);
			}
			catch (InvocationTargetException ex)
			{
				throw new FunctorException("PrototypeCloneFactory: Clone method threw an exception", ex);
			}
		}
	}

	/**
	 * PrototypeSerializationFactory creates objects by cloning a prototype using serialization.
	 */
	static class PrototypeSerializationFactory implements Factory, Serializable
	{
		/**
		 * The object to clone via serialization each time
		 */
		private final Serializable iPrototype;

		/**
		 * Constructor to store prototype
		 */
		private PrototypeSerializationFactory(Serializable prototype)
		{
			super();
			iPrototype = prototype;
		}

		/**
		 * Creates an object using serialization.
		 *
		 * @return the new object
		 */
		public Object create()
		{
			ByteArrayOutputStream baos = new ByteArrayOutputStream(512);
			ByteArrayInputStream bais = null;
			try
			{
				ObjectOutputStream out = new ObjectOutputStream(baos);
				out.writeObject(iPrototype);

				bais = new ByteArrayInputStream(baos.toByteArray());
				ObjectInputStream in = new ObjectInputStream(bais);
				return in.readObject();

			}
			catch (ClassNotFoundException ex)
			{
				throw new FunctorException(ex);
			}
			catch (IOException ex)
			{
				throw new FunctorException(ex);
			}
			finally
			{
				try
				{
					if (bais != null)
					{
						bais.close();
					}
				}
				catch (IOException ex)
				{
					// ignore
				}
				try
				{
					if (baos != null)
					{
						baos.close();
					}
				}
				catch (IOException ex)
				{
					// ignore
				}
			}
		}
	}

}
