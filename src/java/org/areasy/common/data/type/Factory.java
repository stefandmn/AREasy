package org.areasy.common.data.type;

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

/**
 * Defines a functor interface implemented by classes that create objects.
 * <p/>
 * A <code>Factory</code> creates an object without using an input parameter.
 * If an input parameter is required, then {@link Transformer} is more appropriate.
 * <p/>
 * Standard implementations of common factories are provided by
 * {@link org.areasy.common.data.FactoryUtility}. These include factories that return a constant,
 * a copy of a prototype or a new instance.
 *
 * @version $Id: Factory.java,v 1.2 2008/05/14 09:32:34 swd\stefan.damian Exp $
 */
public interface Factory
{

	/**
	 * Create a new object.
	 *
	 * @return a new object
	 * @throws org.areasy.common.data.workers.functors.FunctorException
	 *          (runtime) if the factory cannot create an object
	 */
	public Object create();

}
