package org.areasy.runtime.actions.system;

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

import org.areasy.runtime.RuntimeAction;
import org.areasy.runtime.RuntimeManager;
import org.areasy.runtime.actions.SystemAction;
import org.areasy.runtime.engine.RuntimeLogger;
import org.areasy.runtime.engine.RuntimeServer;
import org.areasy.runtime.engine.base.AREasyException;

import java.io.File;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Vector;

/**
 * Define the runtime cleaner job executed by server component
 */
public class Clean extends SystemAction implements RuntimeAction
{
	/**
	 * Execute 'cleanup' action.
	 * Define the runtime cleaner job executed by server component
	 *
	 * @throws org.areasy.runtime.engine.base.AREasyException if any error will occur.
	 */
	public void run() throws AREasyException
	{
		getLogger().debug("Recycle internal objects: execute garbage collector, remove temporary files and old log files");

		//1.execute GC.
		RuntimeLogger.info("Runs the garbage collector");
		System.gc();

		//2.remove log files & temporary files older then 10 days.
		List files = new Vector();
		files.addAll( Arrays.asList(RuntimeManager.getWorkingDirectory().listFiles()) );
		files.addAll( Arrays.asList(RuntimeManager.getLogsDirectory().listFiles()) );

		//define the retention period
		Date current = new Date();
		long period = current.getTime() - getConfiguration().getInt("olderthan", 10) * 24 * 3600 * 1000;

		for(int i = 0; i < files.size(); i++)
		{
			File file  = (File) files.get(i);

			if(file.lastModified() < period)
			{
				RuntimeLogger.info("Remove file: " + file.getAbsolutePath());
				if(!file.delete()) file.deleteOnExit();
			}
		}

		//3. remove expired objects from the caching layer;
		if(getConfiguration().getBoolean("force", false))
		{
			RuntimeServer.getCache().clear();
			RuntimeLogger.warn("Force to remove all objects from the cache layer");
		}
		else
		{
			RuntimeServer.getCache().removeExpiredObjects();
			RuntimeLogger.info("Remove all expired objects from the cache layer");
		}
	}
}
