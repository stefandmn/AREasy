package org.areasy.common.data.type.set;

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

import org.areasy.common.data.type.Transformer;
import org.areasy.common.data.type.collection.TransformedCollection;

import java.util.Set;

/**
 * Decorates another <code>Set</code> to transform objects that are added.
 * <p/>
 * The add methods are affected by this class.
 * Thus objects must be removed or searched for using their transformed form.
 * For example, if the transformation converts Strings to Integers, you must
 * use the Integer form to remove objects.
 *
 * @version $Id: TransformedSet.java,v 1.2 2008/05/14 09:32:37 swd\stefan.damian Exp $
 */
public class TransformedSet extends TransformedCollection implements Set
{
	/**
	 * Factory method to create a transforming set.
	 * <p/>
	 * If there are any elements already in the set being decorated, they
	 * are NOT transformed.
	 *
	 * @param set         the set to decorate, must not be null
	 * @param transformer the transformer to use for conversion, must not be null
	 * @throws IllegalArgumentException if set or transformer is null
	 */
	public static Set decorate(Set set, Transformer transformer)
	{
		return new TransformedSet(set, transformer);
	}

	/**
	 * Constructor that wraps (not copies).
	 * <p/>
	 * If there are any elements already in the set being decorated, they
	 * are NOT transformed.
	 *
	 * @param set         the set to decorate, must not be null
	 * @param transformer the transformer to use for conversion, must not be null
	 * @throws IllegalArgumentException if set or transformer is null
	 */
	protected TransformedSet(Set set, Transformer transformer)
	{
		super(set, transformer);
	}

}
