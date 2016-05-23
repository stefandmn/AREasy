package org.areasy.runtime.actions.system;

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

import org.areasy.runtime.RuntimeAction;
import org.areasy.runtime.RuntimeManager;
import org.areasy.runtime.actions.SystemAction;
import org.areasy.runtime.engine.RuntimeClient;
import org.areasy.runtime.engine.RuntimeLogger;
import org.areasy.runtime.engine.base.AREasyException;
import org.areasy.common.data.NumberUtility;
import org.areasy.common.data.StringUtility;
import org.areasy.common.support.configuration.Configuration;
import org.areasy.common.support.configuration.base.BaseConfiguration;

import java.io.File;
import java.util.List;

/**
 * Server startup action frokm runtime client environment. With this action you can start
 * locally runtime server instance.
 *
 */
public class StartAction extends SystemAction implements RuntimeAction
{
	/**
	 * Execute 'startup' action which in fact is server start up
	 *
	 * @throws org.areasy.runtime.engine.base.AREasyException if any error will occur.
	 */
	public void run() throws AREasyException
	{
		try
		{
			Configuration config = new BaseConfiguration();
			config.setKey("action", "echo");
			config.setKey("signal", "true");
			config.setKey("loglevel", "off");

			getLogger().info("Checking in the AREasy Runtime server is already started");
			RuntimeLogger.clearData();
			RuntimeLogger.Off();

			RuntimeClient client = new RuntimeClient(getManager());
			client.run(config);

			List data = RuntimeLogger.getDataList();

			if(data != null && !data.isEmpty() && NumberUtility.toInt((String) data.get(0), -1) == 0)
			{
				//reset runtime output channel.
				RuntimeLogger.clearData();
				RuntimeLogger.On();

				RuntimeLogger.warn("AREasy Runtime Server is already running");
				return;
			}

			//reset runtime output channel.
			RuntimeLogger.reset();
			RuntimeLogger.On();
		}
		catch(Throwable th)
		{
			getLogger().debug("Error calling 'echo' action: " + th.getMessage());
		}

		//start server instance in a separate process
		try
		{
			String javaCmd = System.getProperty("java_home", System.getProperty("java.home", System.getenv("java_home")));

			if(javaCmd != null)
			{
				if(File.separatorChar == '\\') javaCmd = "\"" + javaCmd + File.separator + "bin" + File.separator + "javaw.exe" + "\"";
					else javaCmd = javaCmd + File.separator + "bin" + File.separator + "java";
			}
			else
			{
				if(File.separatorChar == '\\') javaCmd += "javaw.exe";
					else javaCmd += "java";
			}

			List javad = getConfiguration().getList("javad", null);

			String xms = getConfiguration().getString("javaxms", System.getProperty("RUNTIME_MIN_MM", System.getProperty("java.runtime.min.mm", System.getenv("RUNTIME_MIN_MM"))));
			if(xms == null) xms = "128M";
				else if(!xms.endsWith("M") && !xms.endsWith("m")) xms += "M";

			String xmx = getConfiguration().getString("javaxmx", System.getProperty("RUNTIME_MAX_MM", System.getProperty("java.runtime.max.mm", System.getenv("RUNTIME_MAX_MM"))));
			if(xmx == null) xmx = "1024M";
				else if(!xmx.endsWith("M") && !xmx.endsWith("m")) xmx += "M";

			//get boot directory
			File bootDirFile = new File(RuntimeManager.getHomeDirectory() + File.separator + "bin");
			File bootFile = null;

			//find boot file
			if(bootDirFile.exists())
			{
				File files[] = bootDirFile.listFiles();

				for (int i = 0; i < files.length; i++)
				{
					if(files[i].getName().contains("boot") && files[i].getName().indexOf(".jar") > 0) bootFile = files[i];
				}
			}

			//validate found boot file
			if(bootFile == null) throw new AREasyException("Boot library wasn't found. Action stopped!");

			for(int i = 0; javad != null && i < javad.size(); i++) javaCmd += " -D" + javad.get(i);

			javaCmd += " -Xms" + xms;
			javaCmd += " -Xmx" + xmx;
			javaCmd += " -jar \"" + bootFile.getPath() + "\"";
			javaCmd += " -config \"" + RuntimeManager.getCfgDirectory() + File.separator + "default.properties\"";
			javaCmd += " -mode server";

			String level = getConfiguration().getString("loglevel", null);
			if(StringUtility.isNotEmpty(level)) javaCmd += " -loglevel " + level;

			String formatter = getConfiguration().getString("logformatter", null);
			if(StringUtility.isNotEmpty(formatter)) javaCmd += " -logformatter " + formatter;

			String host = getConfiguration().getString("host", null);
			if(StringUtility.isNotEmpty(host)) javaCmd += " -host " + host;

			String port = getConfiguration().getString("port", null);
			if(StringUtility.isNotEmpty(port)) javaCmd += " -port " + port;

			String threads = getConfiguration().getString("threads", null);
			if(StringUtility.isNotEmpty(threads)) javaCmd += " -threads " + threads;

			String arserver = getConfiguration().getString("arserver", null);
			if(StringUtility.isNotEmpty(arserver)) javaCmd += " -arserver " + arserver;

			String arport = getConfiguration().getString("arport", null);
			if(StringUtility.isNotEmpty(arport)) javaCmd += " -arport " + arport;

			String aruser = getConfiguration().getString("aruser", null);
			if(StringUtility.isNotEmpty(aruser)) javaCmd += " -aruser " + aruser;

			String arpassword = getConfiguration().getString("arpassword", null);
			if(StringUtility.isNotEmpty(arpassword)) javaCmd += " -arpassword " + arpassword;

			if(File.separatorChar == '/') javaCmd +=" &";
			if(File.separatorChar == '/') Runtime.getRuntime().exec(new String[] {"sh" , "-c", javaCmd});
				else Runtime.getRuntime().exec(javaCmd, null, RuntimeManager.getHomeDirectory());
			
			RuntimeLogger.info("AREasy Runtime Server has been started");
		}
		catch(Exception e)
		{
			RuntimeLogger.error("Error starting up AREasy Runtime server: " + e.getMessage());
			getLogger().debug("Exception", e);
		}
	}
}
