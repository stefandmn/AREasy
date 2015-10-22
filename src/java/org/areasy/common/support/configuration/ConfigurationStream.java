package org.areasy.common.support.configuration;

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
 * This class is used to read and write configuration entries. This interface must be implemented
 * by locators that wants to read and write from sources.
 *
 * @version $Id: ConfigurationStream.java,v 1.2 2008/05/14 09:32:41 swd\stefan.damian Exp $
 */
public interface ConfigurationStream
{

	/**
	 * Read configuration entries for a specific locator.
	 * @param all specify if will be read all included locators.
	 *
	 * @throws ConfigurationException
	 */
	void read(boolean all) throws ConfigurationException;

	/**
	 * Write configuration entries from a specific locator.
	 * @param all specify if will be write all included locators or only the current locator.
	 *
	 * @throws ConfigurationException
	 */
	void write(boolean all) throws ConfigurationException;
}
