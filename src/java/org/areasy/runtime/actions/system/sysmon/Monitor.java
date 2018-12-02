package org.areasy.runtime.actions.system.sysmon;

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

import org.areasy.runtime.actions.system.sysmon.infos.*;

/**
 * This is the interface that needs to be implemented for any platform that SysMon
 * supports.
 */
public interface Monitor
{
	/**
	 * Get the operating system details (name, version).
	 *
	 * @return The operating system structure.
	 */
	SystemInfo getSystemInfo();

	/**
	 * Gets CPU details
	 *
	 * @return An object containing the number of CPUs and related frequency.
	 */
	ProcessorInfo getProcessorInfo();

	/**
	 * Gets JVM details
	 *
	 * @return An object containing pid, uptime and Head details.
	 */
	JavaInfo getJavaInfo();

	/**
	 * Gets the physical memory installed, and the amount free.
	 *
	 * @return An object containing the amount of physical
	 *         memory installed, and the amount free.
	 */
	MemoryInfo getPhysicalMemoryInfo();

	/**
	 * Gets the amount of swap available to the operating system,
	 * and the amount that is free.
	 *
	 * @return An object containing the amount of swap available
	 *         to the system, and the amount free.
	 */
	MemoryInfo getSwapMemoryInfo();

	/**
	 * Gets a snapshot which contains the total load
	 * of the CPU for the current process (JVM) and for the whole system.
	 *
	 * @return An object containing the load of the CPU for the current process and also for the whole system.
	 */
	UsageInfo getUsageInfo();
}
