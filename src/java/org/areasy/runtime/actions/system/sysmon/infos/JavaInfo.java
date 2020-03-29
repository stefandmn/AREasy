package org.areasy.runtime.actions.system.sysmon.infos;

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

import org.areasy.runtime.actions.system.sysmon.FormatUtility;

/**
 * This object represents the JVM details.
 */
public class JavaInfo
{
	private final int pid;
	private final long uptime;

	public JavaInfo(int pid, long uptime)
	{
		this.pid = pid;
		this.uptime = uptime;
	}

	/**
	 * Gets the pid of the process that is calling this method
	 * (assuming it is running in the same process).
	 *
	 * @return The pid of the process calling this method.
	 */
	public int getPid()
	{
		return pid;
	}

	/**
	 * How long the system has been up in seconds.
	 * Doesn't generally include time that the system
	 * has been hibernating or asleep.
	 *
	 * @return The time the system has been up in seconds.
	 */
	public long getUptime()
	{
		return uptime;
	}

	public String toString()
	{
		return "[pid: " + getPid() + ", uptime: " + FormatUtility.msecs2DHMS(getUptime()) + "]";
	}
}
