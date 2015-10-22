package org.areasy.common.support.configuration.base.predicates;

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

import org.areasy.common.data.type.Predicate;
import org.areasy.common.support.configuration.ConfigurationEntry;
import org.areasy.common.support.configuration.ConfigurationLocator;

/**
 * Defines a functor interface to perform a predicate test on a configuration entry object.
 * The validation consists in checking for instance type and for entry deinition.
 * Evaluation is performed with success only if entry is data.
 *
 * @version $Id: DataConfigurationEntryPredicate.java,v 1.2 2008/05/14 09:32:41 swd\stefan.damian Exp $
 */
public class DataConfigurationEntryPredicate implements Predicate
{
	/** Configuration locator structure */
	private ConfigurationLocator locator = null;

	/**
	 * Default constructor specifing owner structure for the evaluated configuration entry.
 	 * @param locator configuration locator structure.
	 */
	public DataConfigurationEntryPredicate(ConfigurationLocator locator)
	{
		this.locator = locator;
	}

	/**
	 * Use the specified parameter to perform a test that returns true or false.
	 *
	 * @param object the object to evaluate, should not be changed
	 * @return true or false
	 * @throws ClassCastException       (runtime) if the input is the wrong class
	 * @throws IllegalArgumentException (runtime) if the input is invalid
	 * @throws org.areasy.common.data.workers.functors.FunctorException
	 *                                  (runtime) if the predicate encounters a problem
	 */
	public boolean evaluate(Object object)
	{
		ConfigurationEntry entry = (ConfigurationEntry)object;

		if(locator != null) return (entry.getLocator().equals(locator) && entry.isData());
			else return entry.isData(); 
	}
}
