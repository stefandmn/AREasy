package org.areasy.runtime.actions.system.sysmon.monitors;

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

import org.areasy.runtime.actions.system.Sysmon;
import org.areasy.runtime.actions.system.sysmon.*;

public class WindowsMonitor implements Monitor
{
	private static Monitor monitor = null;

	static
	{
		if (System.getProperty("os.name").toLowerCase().startsWith("windows"))
		{
			monitor = new WindowsMonitor();
		}
	}

	public WindowsMonitor()
	{
		if (monitor != null)
		{
			Sysmon.setMonitor(monitor);
		}
	}

	public String getOSName()
	{
		return System.getProperty("os.name");
	}

	public int getNumberOfProcessors()
	{
		return Runtime.getRuntime().availableProcessors();
	}

	public long getProcessorFrequency()
	{
		return 0;
	}

	public ProcessorInfo getProcessorInfo()
	{
		return null;
	}

	public MemoryInfo getPhysicalMemoryInfo()
	{
		return null;
	}

	public MemoryInfo getSwapMemoryInfo()
	{
		return null;
	}

	public long getUptime()
	{
		return 0;
	}

	public int getCurrentPid()
	{
		return 0;
	}

	public ProcessInfo[] getProcessesInfo()
	{
		return new ProcessInfo[0];
	}
}
