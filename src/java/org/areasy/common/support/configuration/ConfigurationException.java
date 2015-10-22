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
 * Any exception that occurs while initializing a Configuration object.
 *
 * @version $Id: ConfigurationException.java,v 1.2 2008/05/14 09:32:41 swd\stefan.damian Exp $
 */

public class ConfigurationException extends Exception
{
	/**
	 * Throwable configuration exception.
	 * @param root
	 */
	public ConfigurationException(Throwable root)
	{
		super(root);
	}

	/**
	 * Throwable and message configuration exception.
	 * @param string
	 * @param root
	 */
	public ConfigurationException(String string, Throwable root)
	{
		super(string, root);
	}

	/**
	 * Message configuration exception.
	 * @param s
	 */
	public ConfigurationException(String s)
	{
		super(s);
	}
}






