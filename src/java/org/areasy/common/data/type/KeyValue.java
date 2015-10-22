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
 * Defines a simple key value pair.
 * <p/>
 * A Map Entry has considerable additional semantics over and above a simple
 * key-value pair. This interface defines the minimum key value, with just the
 * two get methods.
 *
 * @version $Id: KeyValue.java,v 1.2 2008/05/14 09:32:35 swd\stefan.damian Exp $
 */
public interface KeyValue
{

	/**
	 * Gets the key from the pair.
	 *
	 * @return the key
	 */
	Object getKey();

	/**
	 * Gets the value from the pair.
	 *
	 * @return the value
	 */
	Object getValue();

}
