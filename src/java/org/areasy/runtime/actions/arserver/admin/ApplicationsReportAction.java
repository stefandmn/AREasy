package org.areasy.runtime.actions.arserver.admin;

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
import org.areasy.runtime.actions.AbstractAction;
import org.areasy.runtime.engine.RuntimeLogger;
import org.areasy.runtime.engine.base.AREasyException;
import org.areasy.runtime.engine.base.ServerConnection;
import org.areasy.runtime.engine.structures.CoreItem;

import java.util.*;

/**
 * Remedy system information
 */
public class ApplicationsReportAction extends AbstractAction implements RuntimeAction
{
	private static long FIELDID_APPLICATION_GUID = 400081600;
	private static long FIELDID_PROPERTY_NAME = 400081700;
	private static long FIELDID_PROPERTY_VALUE = 400081800;

	/**
	 * Find property value by Application GUID
	 *
	 * @param appPropCol java.util.Collection of CoreItem objects to search within
	 * @param appGUID application GUID
	 * @return the String value of the property
	 */
	private String findOtherProperty(Collection appPropCol, Object appGUID)
	{
		for (Iterator i = appPropCol.iterator(); i.hasNext();)
		{
			CoreItem ci = (CoreItem) i.next();
			if (ci.getAttributeValue(FIELDID_APPLICATION_GUID).equals(appGUID))
			{
				return ci.getStringAttributeValue(FIELDID_PROPERTY_VALUE);
			}
		}

		return null;
	}

	/**
	 * Fetch application version information from the system
	 *
	 * @return a map with Application Name as key and Version/Patch Level as value.
	 * @throws AREasyException if any error will accure
	 */
	private Map getApplicationVersionInfo() throws AREasyException
	{
		CoreItem ci = new CoreItem();
		Map result = new TreeMap();

		ci.setFormName("SHARE:Application_Properties");
		ci.setAttribute(FIELDID_PROPERTY_NAME, "Name");
		List appList = ci.search(getServerConnection());

		ci.setAttribute(FIELDID_PROPERTY_NAME, "Version");
		List versionList = ci.search(getServerConnection());

		ci.setAttribute(FIELDID_PROPERTY_NAME, "Patch");
		List patchList = ci.search(getServerConnection());

		for (Iterator i = appList.iterator(); i.hasNext();)
		{
			CoreItem x = (CoreItem) i.next();

			String appName = x.getStringAttributeValue(FIELDID_PROPERTY_VALUE);
			Object appGUID = x.getAttributeValue(FIELDID_APPLICATION_GUID);
			String appVersion = findOtherProperty(versionList, appGUID);
			String patchLevel = findOtherProperty(patchList, appGUID);

			result.put(appName, appVersion + (patchLevel != null ? " Patch " + patchLevel : ""));
		}

		return result;
	}

	/**
	 * Execute action's workflow.
	 *
	 * @throws org.areasy.runtime.engine.base.AREasyException
	 *          if any global error occurs. All errors cumming from action's execution will become output items
	 */
	public void run() throws AREasyException
	{
		ServerConnection connection = getServerConnection();

		Map appVersionInfo = getApplicationVersionInfo();

		RuntimeLogger.add("Version information for " + connection.getServerName() + " (" + new Date() + ")");

		for (Iterator i = appVersionInfo.keySet().iterator(); i.hasNext();)
		{
			String appName = (String) i.next();
			String appVersion = (String) appVersionInfo.get(appName);

			RuntimeLogger.add("\t" + appName + ": " + appVersion);
		}
	}
}
