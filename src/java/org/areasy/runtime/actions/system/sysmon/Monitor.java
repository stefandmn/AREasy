package org.areasy.runtime.actions.system.sysmon;

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
 * This is the interface that needs to be implemented for any platform that SysMon
 * supports.
 */
public interface Monitor
{
	/**
	 * Get the operating system name.
	 *
	 * @return The operating system name.
	 */
	String getOSName();

	/**
	 * Get the number of CPU cores.
	 *
	 * @return The number of CPU cores.
	 */
	int getNumberOfProcessors();

	/**
	 * Get the CPU frequency in Hz
	 *
	 * @return the CPU frequency in Hz
	 */
	long getProcessorFrequency();

	/**
	 * How long the system has been up in seconds.
	 * Doesn't generally include time that the system
	 * has been hibernating or asleep.
	 *
	 * @return The time the system has been up in seconds.
	 */
	long getUptime();

	/**
	 * Gets the pid of the process that is calling this method
	 * (assuming it is running in the same process).
	 *
	 * @return The pid of the process calling this method.
	 */
	int getCurrentPid();

	/**
	 * Gets a snapshot which contains the total amount
	 * of time the CPU has spent in user mode, kernel mode,
	 * and idle. Given two snapshots, you can calculate
	 * the CPU usage during that time. There is a convenience
	 * method to perform this calculation in
	 * {@link ProcessorInfo#getCpuUsage}
	 *
	 * @return An object containing the amount of time the
	 *         CPU has spent idle, in user mode and in kernel mode,
	 *         in milliseconds.
	 */
	ProcessorInfo getProcessorInfo();

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
	 * Get the current process table. This call returns an array of
	 * objects, each of which represents a single process.
	 *
	 * @return An array of objects, each of which represents a process.
	 */
	ProcessInfo[] getProcessesInfo();
}
