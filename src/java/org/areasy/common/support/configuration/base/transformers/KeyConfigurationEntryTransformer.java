package org.areasy.common.support.configuration.base.transformers;

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
import org.areasy.common.support.configuration.ConfigurationEntry;

/**
 * Defines a functor interface to transform one object into another.
 * This implementation will return from a configuration entry only data key.
 *
 * @version $Id: KeyConfigurationEntryTransformer.java,v 1.2 2008/05/14 09:32:41 swd\stefan.damian Exp $
 */
public class KeyConfigurationEntryTransformer implements Transformer
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
	public Object transform(Object input)
	{
		ConfigurationEntry entry = (ConfigurationEntry)input;

		if(entry.isData()) return entry.getKey();
			else throw new NullPointerException("Input configuration entry is not a data structure.");
	}
}
