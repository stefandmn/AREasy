package org.areasy.runtime.engine.structures.hybrid;

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

import org.areasy.common.support.configuration.Configuration;
import org.areasy.runtime.engine.base.AREasyException;
import org.areasy.runtime.engine.base.ServerConnection;
import org.areasy.runtime.engine.structures.CoreItem;

import java.util.Map;

/**
 * This interface was designed to read all related substructure for a specific structure.
 */
public interface AbstractHybrid
{
	/**
	 * Method to generate target data structure.
	 *
	 * @param target structure to be populated
	 * @param map data mapping between source and target
	 * @param connection source connection instance
	 * @param config runtime configuration or more specifically, multipart configuration
	 * @throws AREasyException if any error will occur
	 */
	void transfer(CoreItem target, Map<Integer,Object> map, ServerConnection connection, Configuration config) throws AREasyException;

	/**
	 * Method to generate related data structures.
	 *
	 * @param connection source connection instance
	 * @param config runtime configuration or more specifically, multipart configuration
	 * @throws AREasyException if any error will occur
	 */
	void relate(ServerConnection connection, Configuration config) throws AREasyException;
}