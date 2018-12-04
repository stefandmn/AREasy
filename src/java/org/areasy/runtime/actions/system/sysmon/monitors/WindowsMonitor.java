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

import org.areasy.common.data.NumberUtility;
import org.areasy.common.logger.Logger;
import org.areasy.common.logger.LoggerFactory;
import org.areasy.runtime.RuntimeManager;
import org.areasy.runtime.actions.system.Sysmon;
import org.areasy.runtime.actions.system.sysmon.*;
import org.areasy.runtime.actions.system.sysmon.infos.MemoryInfo;
import org.areasy.runtime.actions.system.sysmon.infos.ProcessorInfo;
import org.areasy.runtime.engine.RuntimeLogger;
import org.areasy.runtime.utilities.StreamUtility;

import java.io.File;
import java.io.BufferedReader;
import java.io.InputStreamReader;

public class WindowsMonitor extends JavaMonitor implements Monitor
{
	/** Library logger */
	protected static Logger logger = LoggerFactory.getLog(WindowsMonitor.class);

	public WindowsMonitor()
	{
		if (System.getProperty("os.name").toLowerCase().startsWith("windows"))
		{
			Sysmon.setMonitor(this);
		}
	}

	public MemoryInfo getPhysicalMemoryInfo()
	{
		String mfree = callWMI("Select * from Win32_OperatingSystem", "FreePhysicalMemory");
		String mtotal = callWMI("Select * from Win32_ComputerSystem", "TotalPhysicalMemory");

		return new MemoryInfo(NumberUtility.toLong(mfree, 0)*1024, NumberUtility.toLong(mtotal, 0));
	}

	public MemoryInfo getSwapMemoryInfo()
	{
		String data[] = callWMI("Select * from Win32_OperatingSystem", "FreeVirtualMemory","TotalVirtualMemorySize");

		return new MemoryInfo(NumberUtility.toLong(data[0], 0)*1024, NumberUtility.toLong(data[1], 0)*1024);
	}

	public ProcessorInfo getProcessorInfo()
	{
		int count = 0;

		String data[] = callWMI("Select * from Win32_ComputerSystem", "NumberOfProcessors","NumberOfLogicalProcessors");
		count = Integer.parseInt(data[0].trim()) * Integer.parseInt(data[1].trim());

		String cpuFreq = callWMI("Select * from Win32_Processor", "CurrentClockSpeed");

		return new ProcessorInfo(count, NumberUtility.toLong(cpuFreq, 0) * 1000 * 1000);
	}

	/**
	 * Generate a VBScript string capable of querying the desired WMI information.
	 * Get the given WMI value from the WMI subsystem on the local computer
	 *
	 * @param wmiQuery the query string as syntactically defined by the WMI reference
	 * @param fieldNames the array of fields object that you want to get out of the query results
	 * @return the interrogated value trhough WMI
	 * */
	public static String[] callWMI(String wmiQuery, String[] fieldNames)
	{
		StringBuilder vbScript = new StringBuilder();

		vbScript.append("Dim oWMI : Set oWMI = GetObject(\"winmgmts:\")").append(System.getProperty("line.separator"));
		vbScript.append("Dim classComponent : Set classComponent = oWMI.ExecQuery(\"").append(wmiQuery).append("\")").append(System.getProperty("line.separator"));
		vbScript.append("Dim obj, strData").append(System.getProperty("line.separator"));
		vbScript.append("For Each obj in classComponent").append(System.getProperty("line.separator"));

		String output[] = new String[fieldNames.length];

		for (String aWmiFieldNameArray : fieldNames)
		{
			vbScript.append("  strData = strData & obj.").append(aWmiFieldNameArray.trim()).append(" & VBCrLf").append(System.getProperty("line.separator"));
		}

		vbScript.append("Next").append(System.getProperty("line.separator"));
		vbScript.append("wscript.echo strData").append(System.getProperty("line.separator"));

		//String vbScript = getVBScript(wmiQuery, fieldNames);
		File vbFile = new File(RuntimeManager.getWorkingDirectory(), RuntimeLogger.getChannelName() + "-wmi.vbs");
		StreamUtility.writeTextFile("UTF-8", vbFile, vbScript.toString());

		try
		{
			int index = 0;
			String line = null;
			Process process = Runtime.getRuntime().exec(new String[] {"cmd.exe", "/C", "cscript.exe", vbFile.getPath()});
			BufferedReader input = new BufferedReader(new InputStreamReader(process.getInputStream()));

			while((line = input.readLine()) != null)
			{
				//need to filter out lines that don't contain our desired output
				if(!line.contains("Microsoft") && !line.equals(""))
				{
					output[index] = line.trim();
					index++;
				}
			}

			StreamUtility.deleteFile(vbFile);
			process.destroy();
			process = null;
		}
		catch(Exception ioe)
		{
			RuntimeLogger.error("Error calling WMI utility: " + ioe.getMessage());
			logger.debug("Exception", ioe);
		}

		return output;
	}

	/**
	 * Generate a VBScript string capable of querying the desired WMI information.
	 * Get the given WMI value from the WMI subsystem on the local computer
	 *
	 * @param wmiQuery the query string as syntactically defined by the WMI reference
	 * @param FieldName the field object that you want to get out of the wmiQuery results
	 * @return the interrogated value trhough WMI
	 * */
	public static String callWMI(String wmiQuery, String FieldName)
	{
		return callWMI(wmiQuery, new String[] {FieldName})[0];
	}

	/**
	 * Generate a VBScript string capable of querying the desired WMI information.
	 * Get the given WMI value from the WMI subsystem on the local computer
	 *
	 * @param wmiQuery the query string as syntactically defined by the WMI reference
	 * @param fieldName1 the first field object that you want to get out of the wmiQuery results
	 * @param fieldName2 the second field object that you want to get out of the wmiQuery results
	 * @return the interrogated value trhough WMI
	 * */
	public static String[] callWMI(String wmiQuery, String fieldName1, String fieldName2)
	{
		return callWMI(wmiQuery, new String[] {fieldName1, fieldName2});
	}

	/**
	 * Generate a VBScript string capable of querying the desired WMI information.
	 * Get the given WMI value from the WMI subsystem on the local computer
	 *
	 * @param wmiQuery the query string as syntactically defined by the WMI reference
	 * @param fieldName1 the first field object that you want to get out of the wmiQuery results
	 * @param fieldName2 the second field object that you want to get out of the wmiQuery results
	 * @param fieldName3 the second field object that you want to get out of the wmiQuery results
	 * @return the interrogated value trhough WMI
	 * */
	public static String[] callWMI(String wmiQuery, String fieldName1, String fieldName2, String fieldName3)
	{
		return callWMI(wmiQuery, new String[] {fieldName1, fieldName2, fieldName3});
	}
}

