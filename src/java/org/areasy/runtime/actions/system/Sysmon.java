package org.areasy.runtime.actions.system;

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
import org.areasy.runtime.RuntimeAction;
import org.areasy.runtime.actions.SystemAction;
import org.areasy.runtime.actions.system.sysmon.*;
import org.areasy.runtime.actions.system.sysmon.infos.*;
import org.areasy.runtime.actions.system.sysmon.monitors.LinuxMonitor;
import org.areasy.runtime.actions.system.sysmon.monitors.JavaMonitor;
import org.areasy.runtime.actions.system.sysmon.monitors.WindowsMonitor;
import org.areasy.runtime.engine.RuntimeLogger;
import org.areasy.runtime.engine.base.AREasyException;

/**
 * This class provides the main API for JavaSysMon.
 * You must instantiate this class in order to use it, but it stores no state, so there is zero overhead to
 * instantiating it as many times as you like, and hence no need to cache it.
 * <p/>
 * When instantiated for the first time, JavaSysMon will discover which operating system it is running on
 * and attempt to load the appropriate OS-specific extensions. If JavaSysMon doesn't support the OS
 * you're running on, all calls to the API will return null or zero values. Probably the best one to test is osName.
 * <p/>
 * You can run JavaSysMon directly as a jar file, using the command "java -jar javasysmon.jar", in which case
 * it will display output similar to the UNIX "top" command. You can optionally specify a process id as an
 * argument, in which case JavaSysMon will attempt to kill the process.
 */
public class Sysmon extends SystemAction implements Monitor, RuntimeAction
{
	/** Declare system monitor taht should describe a specific OS */
	private static Monitor monitor = null;

	static
	{
		new LinuxMonitor();
		new WindowsMonitor();
		new JavaMonitor(); // default monitor in case no other specific system monitor is found
	}

	/**
	 * Allows you to register your own implementation of {@link Monitor}.
	 *
	 * @param mon An implementation of the Monitor interface that all API calls will be delegated to
	 */
	public static void setMonitor(Monitor mon)
	{
		if (mon != null) monitor = mon;
	}

	public static Monitor getMonitor()
	{
		return monitor;
	}

	/**
	 * Execute 'sysmon' action.
	 * Processing system monitoring internal command (for client/runtime of server)
	 *
	 * @throws org.areasy.runtime.engine.base.AREasyException if any error will occur.
	 */
	public void run() throws AREasyException
	{
		if(getConfiguration().getBoolean("info", false))
		{
			RuntimeLogger.add("OS:    " + getSystemInfo());
			RuntimeLogger.add("CPU:   " + getProcessorInfo());
			RuntimeLogger.add("RAM:   " + getPhysicalMemoryInfo());
			RuntimeLogger.add("Swap:  " + getSwapMemoryInfo());
			RuntimeLogger.add("JVM:   " + getJavaInfo());
			RuntimeLogger.add("Usage: " + getUsageInfo());
		}
		else if(getConfiguration().getBoolean("os", false))
		{
			RuntimeLogger.add(getSystemInfo().toString());
		}
		else if(getConfiguration().getBoolean("cpu", false))
		{
			RuntimeLogger.add(getProcessorInfo().toString());
		}
		else if(getConfiguration().getBoolean("jvm", false))
		{
			RuntimeLogger.add(getJavaInfo().toString());
		}
		else if(getConfiguration().containsKey("ram"))
		{
			if(StringUtility.equalsIgnoreCase(getConfiguration().getString("ram"), "total")) RuntimeLogger.add(FormatUtility.memoryValueFormat(getPhysicalMemoryInfo().getTotalBytes()));
				else if(StringUtility.equalsIgnoreCase(getConfiguration().getString("ram"), "free")) RuntimeLogger.add(FormatUtility.memoryValueFormat(getPhysicalMemoryInfo().getFreeBytes()));
					else if(getConfiguration().getBoolean("ram", false)) RuntimeLogger.add(getPhysicalMemoryInfo().toString());
		}
		else if(getConfiguration().containsKey("swap"))
		{
			if(StringUtility.equalsIgnoreCase(getConfiguration().getString("swap"), "total")) RuntimeLogger.add(FormatUtility.memoryValueFormat(getSwapMemoryInfo().getTotalBytes()));
				else if(StringUtility.equalsIgnoreCase(getConfiguration().getString("swap"), "free")) RuntimeLogger.add(FormatUtility.memoryValueFormat(getSwapMemoryInfo().getFreeBytes()));
					else if(getConfiguration().getBoolean("swap", false)) RuntimeLogger.add(getSwapMemoryInfo().toString());
		}
		else if(getConfiguration().containsKey("usage"))
		{
			if(StringUtility.equalsIgnoreCase(getConfiguration().getString("usage"), "jvm")) RuntimeLogger.add(FormatUtility.loadValueFormat(getUsageInfo().getProcessLoad()));
				else if(StringUtility.equalsIgnoreCase(getConfiguration().getString("usage"), "system")) RuntimeLogger.add(FormatUtility.loadValueFormat(getUsageInfo().getSystemLoad()));
					else if(getConfiguration().getBoolean("usage", false)) RuntimeLogger.add(getUsageInfo().toString());
		}
	}

	/**
	 * Get the operating system details (name, version).
	 *
	 * @return The operating system structure.
	 */
	public SystemInfo getSystemInfo()
	{
		return monitor.getSystemInfo();
	}

	/**
	 * Get the operating system name.
	 *
	 * @return The operating system name.
	 */
	public String getOSName()
	{
		return monitor.getSystemInfo().getName();
	}

	/**
	 * Get the operating system version.
	 *
	 * @return The operating system version.
	 */
	public String getOSVersion()
	{
		return monitor.getSystemInfo().getVersion();
	}

	/**
	 * Gets CPU details
	 *
	 * @return An object containing the number of CPUs and related frequency.
	 */
	public ProcessorInfo getProcessorInfo()
	{
		return monitor.getProcessorInfo();
	}

	/**
	 * Get the number of CPU cores.
	 *
	 * @return The number of CPU cores.
	 */
	public int getNumberOfProcessors()
	{
		return monitor.getProcessorInfo().getNumberOfProcessors();
	}

	/**
	 * Get the CPU frequency in Hz
	 *
	 * @return the CPU frequency in Hz
	 */
	public long getProcessorFrequency()
	{
		return monitor.getProcessorInfo().getProcessorFrequency();
	}

	/**
	 * Gets JVM details
	 *
	 * @return An object containing pid, uptime and Head details.
	 */
	public JavaInfo getJavaInfo()
	{
		return monitor.getJavaInfo();
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
		return monitor.getJavaInfo().getUptime();
	}

	/**
	 * Gets the pid of the process that is calling this method
	 * (assuming it is running in the same process).
	 *
	 * @return The pid of the process calling this method.
	 */
	public int getPid()
	{
		return monitor.getJavaInfo().getPid();
	}

	/**
	 * Gets the physical memory installed, and the amount free.
	 *
	 * @return An object containing the amount of physical
	 *         memory installed, and the amount free.
	 */
	public MemoryInfo getPhysicalMemoryInfo()
	{
		return monitor.getPhysicalMemoryInfo();
	}

	/**
	 * Gets the amount of swap available to the operating system,
	 * and the amount that is free.
	 *
	 * @return An object containing the amount of swap available
	 *         to the system, and the amount free.
	 */
	public MemoryInfo getSwapMemoryInfo()
	{
		return monitor.getSwapMemoryInfo();
	}

	/**
	 * Gets a snapshot which contains the total load
	 * of the CPU for the current process (JVM) and for the whole system.
	 *
	 * @return An object containing the load of the CPU for the current process and also for the whole system.
	 */
	public UsageInfo getUsageInfo()
	{
		return monitor.getUsageInfo();
	}
}
