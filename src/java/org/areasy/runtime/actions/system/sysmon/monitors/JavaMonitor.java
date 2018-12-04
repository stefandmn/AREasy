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

import org.areasy.common.data.StringUtility;
import org.areasy.runtime.actions.system.Sysmon;
import org.areasy.runtime.actions.system.sysmon.Monitor;
import org.areasy.runtime.actions.system.sysmon.infos.*;

public class JavaMonitor implements Monitor
{
	private com.sun.management.OperatingSystemMXBean osBean = null;
	private java.lang.management.RuntimeMXBean rtBean = null;

	public JavaMonitor()
	{
		if(Sysmon.getMonitor() == null) Sysmon.setMonitor(this);

		this.osBean = (com.sun.management.OperatingSystemMXBean) java.lang.management.ManagementFactory.getOperatingSystemMXBean();
		this.rtBean = java.lang.management.ManagementFactory.getRuntimeMXBean();
	}

	/**
	 * Get the operating system details (name, version).
	 *
	 * @return The operating system structure.
	 */
	public SystemInfo getSystemInfo()
	{
		String name = null;
		String version = null;

		if (StringUtility.isNotEmpty(System.getProperty("os.name"))) name = System.getProperty("os.name");
			else name = this.osBean.getName();

		if (StringUtility.isNotEmpty(System.getProperty("os.version"))) version = System.getProperty("os.version");
			else version = this.osBean.getVersion();

		return new SystemInfo(name, version);
	}

	public MemoryInfo getPhysicalMemoryInfo()
	{
		return new MemoryInfo(this.osBean.getFreePhysicalMemorySize(), this.osBean.getTotalPhysicalMemorySize());
	}

	public MemoryInfo getSwapMemoryInfo()
	{
		return new MemoryInfo(this.osBean.getFreeSwapSpaceSize(), this.osBean.getTotalSwapSpaceSize());
	}

	public JavaInfo getJavaInfo()
	{
		int pid = 0;

		try
		{
			pid = Integer.parseInt(this.rtBean.getName().split("@")[0]);
		}
		catch(Exception e) { /* nothing to be considered here */ }

		return new JavaInfo(pid, this.rtBean.getUptime());
	}

	public ProcessorInfo getProcessorInfo()
	{
		return new ProcessorInfo(this.osBean.getAvailableProcessors(), 0);
	}

	public UsageInfo getUsageInfo()
	{
		return new UsageInfo(this.osBean.getProcessCpuLoad(), this.osBean.getSystemCpuLoad());
	}
}
