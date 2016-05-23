package org.areasy.common.data.workers.functors;

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

import org.areasy.common.data.type.Predicate;

/**
 * Defines a predicate that decorates one or more other predicates.
 * <p/>
 * This interface enables tools to access the decorated predicates.
 *
 * @version $Id: PredicateDecorator.java,v 1.2 2008/05/14 09:32:32 swd\stefan.damian Exp $
 */
public interface PredicateDecorator extends Predicate
{

	/**
	 * Gets the predicates being decorated as an array.
	 * <p/>
	 * The array may be the internal data structure of the predicate and thus
	 * should not be altered.
	 *
	 * @return the predicates being decorated
	 */
	Predicate[] getPredicates();

}
