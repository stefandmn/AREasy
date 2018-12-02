package org.areasy.runtime.actions.system.sysmon.infos;

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

import org.areasy.runtime.actions.system.sysmon.ParserUtility;

/**
 * This object represents system coordinates.
 */
public class SystemInfo
{
	private final String name;
	private final String version;

	public SystemInfo(String name, String version)
	{
		this.name = name;
		this.version = version;
	}

	/**
	 * Get the operating system name.
	 *
	 * @return The operating system name.
	 */
	public String getName()
	{
		return name;
	}

	/**
	 * Get the operating system version.
	 *
	 * @return The operating system version.
	 */
	public String getVersion()
	{
		return version;
	}

	public String toString()
	{
		return "[name: " + getName() + ", version: " + getVersion() + "]";
	}
}
