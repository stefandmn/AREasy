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

import org.areasy.runtime.RuntimeManager;
import org.areasy.runtime.actions.system.Sysmon;
import org.areasy.runtime.actions.system.sysmon.*;
import org.areasy.runtime.actions.system.sysmon.infos.MemoryInfo;
import org.areasy.runtime.engine.RuntimeLogger;
import org.areasy.runtime.utilities.StreamUtility;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;

public class WindowsMonitor extends JavaMonitor implements Monitor
{
	private static final String CRLF = "\r\n";

	public WindowsMonitor()
	{
		if (System.getProperty("os.name").toLowerCase().startsWith("windows"))
		{
			Sysmon.setMonitor(this);
		}
	}

	public MemoryInfo getPhysicalMemoryInfo()
	{
		try
		{
			String mfree = getWMIValue("Select * from Win32_OperatingSystem", "FreePhysicalMemory");
			String mtotal = getWMIValue("Select * from Win32_ComputerSystem", "TotalPhysicalMemory");

			return new MemoryInfo(Long.parseLong(mfree)*1024, Long.parseLong(mtotal));
		}
		catch(Exception e)
		{
			RuntimeLogger.error(e.getMessage());

			return super.getPhysicalMemoryInfo();
		}
	}

	public MemoryInfo getSwapMemoryInfo()
	{
		try
		{
			String data = getWMIValue("Select * from Win32_OperatingSystem", "FreeVirtualMemory,TotalVirtualMemorySize");
			String mfree = data.split(CRLF)[0].trim();
			String mtotal = data.split(CRLF)[1].trim();

			return new MemoryInfo(Long.parseLong(mfree)*1024, Long.parseLong(mtotal)*1024);
		}
		catch(Exception e)
		{
			RuntimeLogger.error(e.getMessage());

			return super.getSwapMemoryInfo();
		}
	}

	/**
	 * Generate a VBScript string capable of querying the desired WMI information.
	 *
	 * @param wmiQueryStr the query string to be passed to the WMI sub-system (i.e. "Select * from Win32_ComputerSystem")
	 * @param wmiCommaSeparatedFieldName a comma separated list of the WMI fields to be collected from the query results (i.e. "Model")
	 * @return the vbscript string.
	 * */
	private static String getVBScript(String wmiQueryStr, String wmiCommaSeparatedFieldName)
	{
		String vbs = "Dim oWMI : Set oWMI = GetObject(\"winmgmts:\")" + CRLF;
		vbs += "Dim classComponent : Set classComponent = oWMI.ExecQuery(\"" + wmiQueryStr + "\")" + CRLF;
		vbs += "Dim obj, strData" + CRLF;
		vbs += "For Each obj in classComponent" + CRLF;

		String[] wmiFieldNameArray = wmiCommaSeparatedFieldName.split(",");
		for(int i = 0; i < wmiFieldNameArray.length; i++)
		{
			vbs += "  strData = strData & obj." + wmiFieldNameArray[i].trim() + " & VBCrLf" + CRLF;
		}

		vbs += "Next" + CRLF;
		vbs += "wscript.echo strData" + CRLF;

		return vbs;
	}

	/**
	 * Get the given WMI value from the WMI subsystem on the local computer
	 * @param wmiQueryStr the query string as syntactically defined by the WMI reference
	 * @param wmiCommaSeparatedFieldName the field object that you want to get out of the query results
	 * @return the value
	 * @throws Exception if there is a problem obtaining the value
	 * */
	public static String getWMIValue(String wmiQueryStr, String wmiCommaSeparatedFieldName) throws Exception
	{
		String vbScript = getVBScript(wmiQueryStr, wmiCommaSeparatedFieldName);
		File fScript = new File(RuntimeManager.getWorkingDirectory(), RuntimeLogger.getChannelName()+ "-wmi.vbs");

		StreamUtility.writeTextFile("UTF-8", fScript, vbScript);
		String output = execute(new String[] {"cmd.exe", "/C", "cscript.exe", fScript.getPath()});
		StreamUtility.deleteFile(fScript);

		return output.trim();
	}

	/**
	 * Execute the application with the given command line parameters.
	 *
	 * @param cmdArray an array of the command line params
	 * @return the output as gathered from stdout of the process
	 * @throws Exception upon encountering a problem
	 * */
	private static String execute(String[] cmdArray) throws Exception
	{
		Process process = Runtime.getRuntime().exec(cmdArray);
		BufferedReader input = new BufferedReader(new InputStreamReader(process.getInputStream()));
		String output = "";
		String line = "";

		while((line = input.readLine()) != null)
		{
			//need to filter out lines that don't contain our desired output
			if(!line.contains("Microsoft") && !line.equals(""))
			{
				output += line +CRLF;
			}
		}

		process.destroy();
		process = null;

		return output.trim();
	}
}

