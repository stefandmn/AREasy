package org.areasy.runtime.actions.system.sysmon.monitors;

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

import org.areasy.runtime.actions.system.sysmon.*;
import org.areasy.runtime.engine.base.AREasyException;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Linux monitor.
 * Network stats will come from /proc/net/dev; disk stats will be from /proc/diskstats
 */
public class LinuxMonitor implements Monitor
{
	private static final Logger LOG = Logger.getLogger(LinuxMonitor.class.getName());

	private static final Pattern TOTAL_MEMORY_PATTERN = Pattern.compile("MemTotal:\\s+(\\d+) kB", Pattern.MULTILINE);
	private static final Pattern FREE_MEMORY_PATTERN = Pattern.compile("MemFree:\\s+(\\d+) kB", Pattern.MULTILINE);
	private static final Pattern TOTAL_SWAP_PATTERN = Pattern.compile("SwapTotal:\\s+(\\d+) kB", Pattern.MULTILINE);
	private static final Pattern FREE_SWAP_PATTERN = Pattern.compile("SwapFree:\\s+(\\d+) kB", Pattern.MULTILINE);
	private static final Pattern CPU_JIFFIES_PATTERN = Pattern.compile("cpu\\s+(.*)", Pattern.MULTILINE);
	private static final Pattern NUM_CPU_PATTERN = Pattern.compile("processor\\s+:\\s+(\\d+)", Pattern.MULTILINE);
	private static final Pattern CPU_FREQ_PATTERN = Pattern.compile("model name[^@]*@\\s+([0-9.A-Za-z]*)", Pattern.MULTILINE);
	private static final Pattern UPTIME_PATTERN = Pattern.compile("([\\d]*).*");
	private static final Pattern PID_PATTERN = Pattern.compile("([\\d]*).*");
	private static final Pattern DISTRIBUTION = Pattern.compile("DISTRIB_DESCRIPTION=\"(.*)\"", Pattern.MULTILINE);

	private int userHz = 100; // Shouldn't be hardcoded. See below.


	public LinuxMonitor()
	{
		if (System.getProperty("os.name").toLowerCase().startsWith("linux"))
		{
			SysmonAction.setMonitor(this);
		}
	}

	public String getOSName()
	{
		String distribution = ParserUtility.runRegexOnFile(DISTRIBUTION, "/etc/lsb-release");
		if (null == distribution)
		{
			return System.getProperty("os.name");
		}

		return distribution;
	}

	public MemoryInfo getPhysicalMemoryInfo()
	{
		String totalMemory = ParserUtility.runRegexOnFile(TOTAL_MEMORY_PATTERN, "/proc/meminfo");
		long total = Long.parseLong(totalMemory) * 1024;

		String freeMemory = ParserUtility.runRegexOnFile(FREE_MEMORY_PATTERN, "/proc/meminfo");
		long free = Long.parseLong(freeMemory) * 1024;

		return new MemoryInfo(free, total);
	}

	public MemoryInfo getSwapMemoryInfo()
	{
		String totalMemory = ParserUtility.runRegexOnFile(TOTAL_SWAP_PATTERN, "/proc/meminfo");
		long total = Long.parseLong(totalMemory) * 1024;

		String freeMemory = ParserUtility.runRegexOnFile(FREE_SWAP_PATTERN, "/proc/meminfo");
		long free = Long.parseLong(freeMemory) * 1024;

		return new MemoryInfo(free, total);
	}

	public int getNumberOfProcessors()
	{
		int numCpus = 0;
		try
		{
			String cpuInfo = ParserUtility.slurp("/proc/cpuinfo");
			Matcher matcher = NUM_CPU_PATTERN.matcher(cpuInfo);

			while (matcher.find())
			{
				numCpus++;
			}

			return numCpus;
		}
		catch (IOException ioe)
		{
			// return nothing
		}

		return 0;
	}

	public long getProcessorFrequency()
	{
		String cpuFrequencyAsString = ParserUtility.runRegexOnFile(CPU_FREQ_PATTERN, "/proc/cpuinfo");
		int strLen = cpuFrequencyAsString.length();

		BigDecimal cpuFrequency = new BigDecimal(cpuFrequencyAsString.substring(0, strLen - 3));
		long multiplier = getMultiplier(cpuFrequencyAsString.charAt(strLen - 3));

		return cpuFrequency.multiply(new BigDecimal(Long.toString(multiplier))).longValue();
	}

	public long getUptime()
	{
		String uptime = ParserUtility.runRegexOnFile(UPTIME_PATTERN, "/proc/uptime");

		return Long.parseLong(uptime);
	}

	public int getCurrentPid()
	{
		String pid = ParserUtility.runRegexOnFile(PID_PATTERN, "/proc/self/stat");

		return Integer.parseInt(pid);
	}

	public ProcessInfo[] getProcessesInfo()
	{
		ArrayList processTable = new ArrayList();
		final String[] pids = ParserUtility.pidsFromProcFilesystem();

		for (int i = 0; i < pids.length; i++)
		{
			try
			{
				String stat = ParserUtility.slurp("/proc/" + pids[i] + "/stat");
				String status = ParserUtility.slurp("/proc/" + pids[i] + "/status");
				String cmdline = ParserUtility.slurp("/proc/" + pids[i] + "/cmdline");
				LinuxPasswdParser passwdParser = new LinuxPasswdParser();

				final LinuxProcessParser parser = new LinuxProcessParser(stat, status, cmdline, passwdParser.parse(), userHz);
				processTable.add(parser.parse());
			}
			catch (AREasyException se)
			{
				// Skip this process, but log a warning for diagnosis.
				LOG.log(Level.WARNING, se.getMessage(), se);
			}
			catch (IOException ioe)
			{
				// process probably died since we got the process list
			}
		}

		return (ProcessInfo[]) processTable.toArray(new ProcessInfo[processTable.size()]);
	}

	public ProcessorInfo getProcessorInfo()
	{
		String[] parsedJiffies = ParserUtility.runRegexOnFile(CPU_JIFFIES_PATTERN, "/proc/stat").split("\\s+");
		long userJiffies = Long.parseLong(parsedJiffies[0]) + Long.parseLong(parsedJiffies[1]);
		long idleJiffies = Long.parseLong(parsedJiffies[3]);
		long systemJiffies = Long.parseLong(parsedJiffies[2]);

		// this is for Linux >= 2.6
		if (parsedJiffies.length > 4)
		{
			for (int i = 4; i < parsedJiffies.length; i++)
			{
				systemJiffies += Long.parseLong(parsedJiffies[i]);
			}
		}

		return new ProcessorInfo(toMillis(userJiffies), toMillis(systemJiffies), toMillis(idleJiffies));
	}

	private long getMultiplier(char multiplier)
	{
		switch (multiplier)
		{
			case 'G':
				return 1000000000;
			case 'M':
				return 1048576;
			case 'k':
				return 1000;
		}
		return 0;
	}

	private long toMillis(long jiffies)
	{
		int multiplier = 1000 / userHz;
		return jiffies * multiplier;
	}
}


class LinuxProcessParser
{
	private final String stat;
	private final String status;
	private final String cmdline;
	private final HashMap uids;
	private final int userHz;

	private static final Pattern STATUS_NAME_MATCHER = Pattern.compile("Name:\\s+(\\w+)", Pattern.MULTILINE);
	private static final Pattern STATUS_UID_MATCHER = Pattern.compile("Uid:\\s+(\\d+)\\s.*", Pattern.MULTILINE);
	private static final Pattern STATUS_VM_SIZE_MATCHER = Pattern.compile("VmSize:\\s+(\\d+) kB", Pattern.MULTILINE);
	private static final Pattern STATUS_VM_RSS_MATCHER = Pattern.compile("VmRSS:\\s+(\\d+) kB", Pattern.MULTILINE);

	public LinuxProcessParser(String stat, String status, String cmdline, HashMap uids, int userHz)
	{
		this.stat = stat;
		this.status = status;
		this.cmdline = cmdline;
		this.uids = uids;
		this.userHz = userHz;
	}

	public ProcessInfo parse() throws AREasyException
	{
		int openParen = stat.indexOf("(");
		int closeParen = stat.lastIndexOf(")");
		if (openParen <= 1 || closeParen < 0 || closeParen > stat.length() - 2)
		{
			throw new AREasyException("Stat '" + stat + "' does not include expected parens around process name");
		}

		// Start splitting after close of proc name
		String[] statElements = stat.substring(closeParen + 2).split(" ");
		if (statElements.length < 13)
		{
			throw new AREasyException("Stat '" + stat + "' contains fewer elements than expected");
		}

		String pidStr = stat.substring(0, openParen - 1);

		int pid;
		int parentPid;
		long userMillis;
		long systemMillis;
		try
		{
			pid = Integer.parseInt(pidStr);
			parentPid = Integer.parseInt(statElements[1]);
			userMillis = Long.parseLong(statElements[11]) * (1000 / userHz);
			systemMillis = Long.parseLong(statElements[12]) * (1000 / userHz);
		}
		catch (NumberFormatException e)
		{
			throw new AREasyException("Unable to parse stat '" + stat + "'");
		}

		long residentBytes;
		long totalBytes;
		try
		{
			residentBytes = Long.parseLong(getFirstMatch(STATUS_VM_RSS_MATCHER, status)) * 1024;
			totalBytes = Long.parseLong(getFirstMatch(STATUS_VM_SIZE_MATCHER, status)) * 1024;
		}
		catch (NumberFormatException e)
		{
			throw new AREasyException("Unable to extract memory usage information from status '" + status + "'");
		}

		return new ProcessInfo(pid,
				parentPid,
				trim(cmdline),
				getFirstMatch(STATUS_NAME_MATCHER, status),
				(String) uids.get(getFirstMatch(STATUS_UID_MATCHER, status)),
				userMillis,
				systemMillis,
				residentBytes,
				totalBytes);
	}

	private String trim(String cmdline)
	{
		return cmdline.replace('\000', ' ').replace('\n', ' ');
	}

	public String getFirstMatch(Pattern pattern, String string)
	{
		try
		{
			Matcher matcher = pattern.matcher(string);
			matcher.find();
			return matcher.group(1);
		}
		catch (Exception e)
		{
			return "0";
		}
	}
}

class LinuxPasswdParser
{
	public HashMap parse(BufferedReader reader)
	{
		if (reader == null)
		{
			System.err.println("Error parsing password file: reader is null");
			return new HashMap();
		}

		HashMap users = new HashMap();
		try
		{
			String line;
			while ((line = reader.readLine()) != null)
			{
				String[] fields = line.split(":");
				if (fields.length >= 2)
				{
					users.put(fields[2], fields[0]);
				}
			}
			return users;
		}
		catch (IOException e)
		{
			System.err.println("Error parsing password file: " + e.getMessage());
			return new HashMap();
		}
		finally
		{
			try
			{
				reader.close();
			}
			catch (IOException e)
			{
				System.err.println("Error closing reader: " + e.getMessage());
			}
		}
	}

	public HashMap parse()
	{
		try
		{
			final FileInputStream passwdFile = new FileInputStream("/etc/passwd");
			BufferedReader reader = new BufferedReader(new InputStreamReader(passwdFile, "UTF-8"));
			return parse(reader);
		}
		catch (IOException e)
		{
			System.err.println("Error reading password file: " + e.getMessage());
			return new HashMap();
		}
	}
}


