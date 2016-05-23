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
 * This object represents a snapshot detailing the total memory of
 * some type (physical or swap) available to the operating system,
 * and the amount that is currently free.
 */
public class MemoryInfo
{
	private final long free;
	private final long total;

	public MemoryInfo(long free, long total)
	{
		this.free = free;
		this.total = total;
	}

	/**
	 * The amount of memory that is currently free, in bytes.
	 *
	 * @return The amount of memory that is currently free.
	 */
	public long getFreeBytes()
	{
		return free;
	}

	/**
	 * The amount of memory that is available to the operating system,
	 * in bytes.
	 *
	 * @return The total amount of memory that is available.
	 */
	public long getTotalBytes()
	{
		return total;
	}

	public String toString()
	{
		return "Total: " + ParserUtility.diskSizeFormat(getTotalBytes()) + ", Free: " + ParserUtility.diskSizeFormat(getFreeBytes());
	}
}
