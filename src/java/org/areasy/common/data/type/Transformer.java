package org.areasy.common.data.type;

/*
 * Copyright (c) 2007-2018 AREasy Runtime
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
 * Defines a functor interface implemented by classes that transform one
 * object into another.
 * <p/>
 * A <code>Transformer</code> converts the input object to the output object.
 * The input object should be left unchanged.
 * Transformers are typically used for type conversions, or extracting data
 * from an object.
 * <p/>
 * Standard implementations of common transformers are provided by
 * {@link org.areasy.common.data.TransformerUtility}. These include method invokation, returning a constant,
 * cloning and returning the string value.
 *
 * @version $Id: Transformer.java,v 1.2 2008/05/14 09:32:34 swd\stefan.damian Exp $
 */
public interface Transformer
{

	/**
	 * Transforms the input object (leaving it unchanged) into some output object.
	 *
	 * @param input the object to be transformed, should be left unchanged
	 * @return a transformed object
	 * @throws ClassCastException       (runtime) if the input is the wrong class
	 * @throws IllegalArgumentException (runtime) if the input is invalid
	 * @throws org.areasy.common.data.workers.functors.FunctorException
	 *                                  (runtime) if the transform cannot be completed
	 */
	public Object transform(Object input);

}
