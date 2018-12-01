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

import org.areasy.runtime.actions.system.sysmon.*;
import org.areasy.runtime.actions.system.sysmon.monitors.LinuxMonitor;
import org.areasy.runtime.actions.system.sysmon.monitors.NullMonitor;
import org.areasy.runtime.actions.system.sysmon.monitors.WindowsMonitor;

import java.text.DecimalFormat;
import java.util.Arrays;
import java.util.List;

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
public class Sysmon implements Monitor
{

	private static Monitor monitor = null;

	/**
	 * Allows you to register your own implementation of {@link Monitor}.
	 *
	 * @param myMonitor An implementation of the Monitor interface that all API calls will be delegated to
	 */
	public static void setMonitor(Monitor myMonitor)
	{
		if (monitor == null || monitor instanceof NullMonitor)
		{
			monitor = myMonitor;
		}
	}

	static
	{
		new LinuxMonitor();
		new WindowsMonitor();
		new NullMonitor(); // make sure the API never gives back a NPE
	}

	/**
	 * Creates a new JavaSysMon object through which to access
	 * the JavaSysMon API. All necessary state is kept statically
	 * so there is zero overhead to instantiating this class.
	 */
	public Sysmon()
	{
		//nothing to do here
	}

	/**
	 * This is the main entry point when running the jar directly.
	 * It prints out some system performance metrics and the process table
	 * in a format similar to the UNIX top command. Optionally you can
	 * specify a process id as an argument, in which case JavaSysMon
	 * will attempt to kill the process specified by that pid.
	 */
	public static void main(String[] params) throws Exception
	{
		if (monitor instanceof NullMonitor)
		{
			System.err.println("Couldn't find an implementation for OS: " + System.getProperty("os.name"));
			System.exit(1);
		}
		else
		{
			List lps = Arrays.asList(params);

			ProcessorInfo initialTimes = monitor.getProcessorInfo();
			System.out.println("\nOS Name: " + monitor.getOSName());
			System.out.println("Uptime: " + ParserUtility.secsInDaysAndHours(monitor.getUptime()));
			System.out.println("Current Pid: " + monitor.getCurrentPid());
			System.out.println("\nNumber of CPUs: " + monitor.getNumberOfProcessors());
			System.out.println("CPU frequency: " + monitor.getProcessorFrequency() / (1000 * 1000) + " MHz");
			System.out.println("RAM: " + monitor.getPhysicalMemoryInfo());
			System.out.println("Swap: " + monitor.getSwapMemoryInfo());
			Thread.sleep(500);
			System.out.println("CPU Usage: " + new DecimalFormat("#,##0.#").format(monitor.getProcessorInfo().getCpuUsage(initialTimes) * 100) + "%");
			System.out.println();

			if (lps.contains("-p"))
			{
				String filter = null;
				int index = lps.indexOf("-p");

				if (lps.size() >= index + 2) filter = (String) lps.get(index + 1);

				System.out.println("\n" + ProcessInfo.header());
				ProcessInfo[] processes = monitor.getProcessesInfo();

				for (int i = 0; i < processes.length; i++)
				{
					if (processes[i].getPid() != monitor.getCurrentPid())
					{
						if (filter != null)
						{
							if (processes[i].toString().indexOf(filter) >= 0) System.out.println(processes[i].toString());
						}
						else System.out.println(processes[i].toString());
					}
				}
			}
		}
	}

	// Following is the actual API

	/**
	 * Get the operating system name.
	 *
	 * @return The operating system name.
	 */
	public String getOSName()
	{
		return monitor.getOSName();
	}

	/**
	 * Get the number of CPU cores.
	 *
	 * @return The number of CPU cores.
	 */
	public int getNumberOfProcessors()
	{
		return monitor.getNumberOfProcessors();
	}

	/**
	 * Get the CPU frequency in Hz
	 *
	 * @return the CPU frequency in Hz
	 */
	public long getProcessorFrequency()
	{
		return monitor.getProcessorFrequency();
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
		return monitor.getUptime();
	}

	/**
	 * Gets the pid of the process that is calling this method
	 * (assuming it is running in the same process).
	 *
	 * @return The pid of the process calling this method.
	 */
	public int getCurrentPid()
	{
		return monitor.getCurrentPid();
	}

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
	public ProcessorInfo getProcessorInfo()
	{
		return monitor.getProcessorInfo();
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
	 * Get the current process table. This call returns an array of
	 * objects, each of which represents a single process. If you want
	 * the objects in a tree structure.
	 *
	 * @return An array of objects, each of which represents a process.
	 */
	public ProcessInfo[] getProcessesInfo()
	{
		return monitor.getProcessesInfo();
	}


	int parseInt(String value)
	{
		try
		{
			return Integer.parseInt(value);
		}
		catch (NumberFormatException nfe)
		{
			// Log exception.
			return 0;
		}
	}
}
