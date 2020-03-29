package org.areasy.runtime.actions.system.sysmon.monitors;

/*
 * Copyright (c) 2007-2020 AREasy Runtime
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
import org.areasy.runtime.actions.system.sysmon.FormatUtility;
import org.areasy.runtime.actions.system.sysmon.Monitor;
import org.areasy.runtime.actions.system.sysmon.infos.MemoryInfo;
import org.areasy.runtime.actions.system.sysmon.infos.ProcessorInfo;
import org.areasy.runtime.actions.system.sysmon.infos.SystemInfo;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Linux monitor.
 * Network stats will come from /proc/net/dev; disk stats will be from /proc/diskstats
 */
public class LinuxMonitor extends JavaMonitor implements Monitor
{
	private static final Logger LOG = Logger.getLogger(LinuxMonitor.class.getName());

	private static final Pattern TOTAL_MEMORY_PATTERN = Pattern.compile("MemTotal:\\s+(\\d+) kB", Pattern.MULTILINE);
	private static final Pattern FREE_MEMORY_PATTERN = Pattern.compile("MemFree:\\s+(\\d+) kB", Pattern.MULTILINE);
	private static final Pattern TOTAL_SWAP_PATTERN = Pattern.compile("SwapTotal:\\s+(\\d+) kB", Pattern.MULTILINE);
	private static final Pattern FREE_SWAP_PATTERN = Pattern.compile("SwapFree:\\s+(\\d+) kB", Pattern.MULTILINE);
	private static final Pattern NUM_CPU_PATTERN = Pattern.compile("processor\\s+:\\s+(\\d+)", Pattern.MULTILINE);
	private static final Pattern CPU_FREQ_PATTERN = Pattern.compile("model name[^@]*@\\s+([0-9.A-Za-z]*)", Pattern.MULTILINE);
	private static final Pattern DISTRO_NAME = Pattern.compile("DISTRIB_ID=\"(.*)\"", Pattern.MULTILINE);
	private static final Pattern DISTRO_VER = Pattern.compile("DISTRIB_RELEASE=(.*)", Pattern.MULTILINE);

	public LinuxMonitor()
	{
		if (System.getProperty("os.name").toLowerCase().startsWith("linux"))
		{
			Sysmon.setMonitor(this);
		}
	}

	/**
	 * Get the operating system details (name, version).
	 *
	 * @return The operating system structure.
	 */
	public SystemInfo getSystemInfo()
	{
		String name = FormatUtility.runRegexOnFile(DISTRO_NAME, "/etc/lsb-release");
		String version = FormatUtility.runRegexOnFile(DISTRO_VER, "/etc/lsb-release");

		if(name == null) name = System.getProperty("os.name");
		if(version == null) version = System.getProperty("os.version");

		return new SystemInfo(name, version);
	}


	public MemoryInfo getPhysicalMemoryInfo()
	{
		String totalMemory = FormatUtility.runRegexOnFile(TOTAL_MEMORY_PATTERN, "/proc/meminfo");
		long total = Long.parseLong(totalMemory) * 1024;

		String freeMemory = FormatUtility.runRegexOnFile(FREE_MEMORY_PATTERN, "/proc/meminfo");
		long free = Long.parseLong(freeMemory) * 1024;

		return new MemoryInfo(free, total);
	}

	public MemoryInfo getSwapMemoryInfo()
	{
		String totalMemory = FormatUtility.runRegexOnFile(TOTAL_SWAP_PATTERN, "/proc/meminfo");
		long total = Long.parseLong(totalMemory) * 1024;

		String freeMemory = FormatUtility.runRegexOnFile(FREE_SWAP_PATTERN, "/proc/meminfo");
		long free = Long.parseLong(freeMemory) * 1024;

		return new MemoryInfo(free, total);
	}

	public ProcessorInfo getProcessorInfo()
	{
		int count = 0;
		try
		{
			String cpuInfo = FormatUtility.slurp("/proc/cpuinfo");
			Matcher matcher = NUM_CPU_PATTERN.matcher(cpuInfo);

			while (matcher.find())
			{
				count++;
			}
		}
		catch (IOException ioe) { /* do nothing here */ }

		String cpuFrequencyAsString = FormatUtility.runRegexOnFile(CPU_FREQ_PATTERN, "/proc/cpuinfo");
		int strLen = cpuFrequencyAsString.length();

		BigDecimal cpuFrequency = new BigDecimal(cpuFrequencyAsString.substring(0, strLen - 3));
		long multiplier = getMultiplier(cpuFrequencyAsString.charAt(strLen - 3));

		return new ProcessorInfo(count, (long)cpuFrequency.floatValue()*multiplier);
	}

	private long getMultiplier(char multiplier)
	{
		switch (multiplier)
		{
			case 'G':
				return 1000000000;
			case 'M':
				return 1000000;
			case 'k':
				return 1000;
		}

		return 1;
	}
}



