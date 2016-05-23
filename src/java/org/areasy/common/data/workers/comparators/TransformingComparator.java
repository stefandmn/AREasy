package org.areasy.common.data.workers.comparators;

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

import java.util.Comparator;

/**
 * Decorates another Comparator with transformation behavior. That is, the
 * return value from the transform operation will be passed to the decorated
 * {@link Comparator#compare(Object,Object) compare} method.
 *
 * @version $Id: TransformingComparator.java,v 1.2 2008/05/14 09:32:40 swd\stefan.damian Exp $
 * @see org.areasy.common.data.type.Transformer
 * @see org.areasy.common.data.workers.comparators.ComparableComparator
 */
public class TransformingComparator implements Comparator
{

	/**
	 * The decorated comparator.
	 */
	protected Comparator decorated;
	/**
	 * The transformer being used.
	 */
	protected Transformer transformer;

	/**
	 * Constructs an instance with the given Transformer and a
	 * {@link ComparableComparator ComparableComparator}.
	 *
	 * @param transformer what will transform the arguments to <code>compare</code>
	 */
	public TransformingComparator(Transformer transformer)
	{
		this(transformer, new ComparableComparator());
	}

	/**
	 * Constructs an instance with the given Transformer and Comparator.
	 *
	 * @param transformer what will transform the arguments to <code>compare</code>
	 * @param decorated   the decorated Comparator
	 */
	public TransformingComparator(Transformer transformer, Comparator decorated)
	{
		this.decorated = decorated;
		this.transformer = transformer;
	}

	/**
	 * Returns the result of comparing the values from the transform operation.
	 *
	 * @param obj1 the first object to transform then compare
	 * @param obj2 the second object to transform then compare
	 * @return negative if obj1 is less, positive if greater, zero if equal
	 */
	public int compare(Object obj1, Object obj2)
	{
		Object value1 = this.transformer.transform(obj1);
		Object value2 = this.transformer.transform(obj2);
		return this.decorated.compare(value1, value2);
	}

}

