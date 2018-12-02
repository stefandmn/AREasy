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
 * This object represents a snapshot detailing the total memory of
 * some type (physical or swap) available to the operating system,
 * and the amount that is currently free.
 */
public class ProcessorInfo
{
	private final int count;
	private final long frequency;

	public ProcessorInfo(int count, long frequency)
	{
		this.count = count;
		this.frequency = frequency;
	}

	/**
	 * Get the number of CPU cores.
	 *
	 * @return The number of CPU cores.
	 */
	public int getNumberOfProcessors()
	{
		return count;
	}

	/**
	 * Get the CPU frequency in Hz
	 *
	 * @return the CPU frequency in Hz
	 */
	public long getProcessorFrequency()
	{
		return frequency;
	}

	public String toString()
	{
		return "[count: " + getNumberOfProcessors() + ", frequency: " + ParserUtility.frequencyValueFormat(getProcessorFrequency()) + "]";
	}
}
