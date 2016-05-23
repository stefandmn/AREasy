package org.areasy.runtime.actions.system.sysmon;

/*
 * Copyright (c) 2007-2016 AREasy Runtime
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
 * This object represents a snapshot detailing the total time the CPUs have spent
 * idle, in user mode, and in kernel mode.
 */
public class ProcessorInfo
{
	private final long userMillis;
	private final long systemMillis;
	private final long idleMillis;

	public ProcessorInfo(long userMillis, long systemMillis, long idleMillis)
	{
		this.userMillis = userMillis;
		this.systemMillis = systemMillis;
		this.idleMillis = idleMillis;
	}

	/**
	 * The total time in milliseconds that the CPUs have spent in user mode.
	 *
	 * @return The total time in milliseconds that the CPUs have spent in user mode.
	 */
	public long getUserMillis()
	{
		return userMillis;
	}

	/**
	 * The total time in milliseconds that the CPUs have spent in kernel mode.
	 *
	 * @return The total time in milliseconds that the CPUs have spent in kernel mode.
	 */
	public long getSystemMillis()
	{
		return systemMillis;
	}

	/**
	 * The total time in milliseconds that the CPUs have spent idle.
	 *
	 * @return The total time in milliseconds that the CPUs have spent idle.
	 */
	public long getIdleMillis()
	{
		return idleMillis;
	}

	/**
	 * The total time in milliseconds that the CPUs have been alive since the system
	 * was last booted. Should equal the sum of the other three numbers.
	 *
	 * @return The total time in milliseconds that the CPUs have been alive.
	 */
	public long getTotalMillis()
	{
		return userMillis + systemMillis + idleMillis;
	}

	/**
	 * Gets the CPU usage given a previous snapshot of CPU times.
	 * The number returned represents the proportion of time between
	 * the two snapshots that the CPUs spent not idle.
	 *
	 * @param previous a CpuTimes snapshot taken previously.
	 * @return the proportion of time between the previous snapshot and this snapshot
	 *         that the CPUs have spent working. 1 represents 100% usage, 0 represents 0% usage.
	 */
	public float getCpuUsage(ProcessorInfo previous)
	{
		if (getIdleMillis() == previous.getIdleMillis())
		{
			return 1f;
		}

		return 1 - ((float) (getIdleMillis() - previous.getIdleMillis())) /
				(float) (getTotalMillis() - previous.getTotalMillis());
	}
}
