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

import org.areasy.runtime.actions.system.sysmon.FormatUtility;

/**
 * This object represents a snapshot detailing the total time the CPUs have spent
 * idle, in user mode, and in kernel mode.
 */
public class UsageInfo
{
	private final double process;
	private final double system;

	public UsageInfo(double process, double system)
	{
		this.process = process;
		this.system = system;
	}

	/**
	 * Returns the "recent cpu usage" for the Java Virtual Machine process. This value is a double in the [0.0,1.0] interval.
	 * A value of 0.0 means that none of the CPUs were running threads from the JVM process during the recent period of time
	 * observed, while a value of 1.0 means that all CPUs were actively running threads from the JVM 100% of the time during
	 * the recent period being observed. Threads from the JVM include the application threads as well as the JVM internal
	 * threads. All values betweens 0.0 and 1.0 are possible depending of the activities going on in the JVM process and
	 * the whole system. If the Java Virtual Machine recent CPU usage is not available, the method returns a negative value.
	 *
	 * @return the "recent cpu usage" for the Java Virtual Machine process; a negative value if not available.
	 */
	public double getProcessLoad()
	{
		return process;
	}

	/**
	 * Returns the "recent cpu usage" for the whole system. This value is a double in the [0.0,1.0] interval.
	 * A value of 0.0 means that all CPUs were idle during the recent period of time observed, while a value
	 * of 1.0 means that all CPUs were actively running 100% of the time during the recent period being observed.
	 * All values betweens 0.0 and 1.0 are possible depending of the activities going on in the system.
	 * If the system recent cpu usage is not available, the method returns a negative value.
	 *
	 * @return the "recent cpu usage" for the whole system; a negative value if not available.
	 */
	public double getSystemLoad()
	{
		return system;
	}

	public String toString()
	{
		return "[jvm: " + FormatUtility.loadValueFormat(getProcessLoad()) + ", system: " + FormatUtility.loadValueFormat(getSystemLoad()) + "]";
	}
}
